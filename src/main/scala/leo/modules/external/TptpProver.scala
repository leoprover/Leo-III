package leo.modules.external

import java.io.{File, PrintWriter}
import java.util.concurrent.TimeUnit

import leo.Configuration
import leo.datastructures.{Clause, ClauseProxy, Signature}
import leo.modules.output._

/**
  *
  * Capsules the call to an atp system with SZS conform return syntax.
  *
  * @since 1/17/17
  * @author Max Wisniewski
  *
  */
trait TptpProver[C <: ClauseProxy] extends HasCapabilities {
  /**
    *
    * The name of the prover
    *
    * @return prover name
    */
  def name : String

  /**
    * The path for the prover.
    *
    * @return prover path
    */
  def path : String

  /**
    *
    * Calls the external prover on a set of formulas assumed to be correct.
    *
    * @param problemOrigin the clauseproxys the `concreteProblem` originates from
    * @param concreteProblem the set of clauses to be sent to the prover
    * @param sig the signature under which `concreteProblem` is represented
    * @param callLanguage The TPTP language in which the call should be formatted
    * @param timeout the timeout for the prover in seconds
    *
    * @return A Future with the result of the prover.
    */
  def call(problemOrigin: Set[C], concreteProblem: Set[Clause],
           sig: Signature, callLanguage: Capabilities.Language,
           timeout : Int,
          extraArgs: Seq[String] = Seq.empty): Future[TptpResult[C]] = {
    val translatedProblem = translateProblem(concreteProblem, callLanguage)(sig)
    startProver(translatedProblem, problemOrigin, timeout, extraArgs)
  }


  /**
    * Constructs the resulting call for the prover.
    * Important are the nameing of the timeout and where to put the problem file.
    *
    * @param args Additional arguments for the prover
    * @param timeout The timeout in seconds
    * @param problemFileName The name of the problemfile in TPTP syntax
    * @return
    */
  protected def constructCall(args : Seq[String], timeout: Int, problemFileName : String) : Seq[String]

  /**
    * Translates the problem to a problem in TPTP syntax.
    *
    * @param problem Set of clauses to be checked.
    * @return
    */
  protected[external] def translateProblem(problem : Set[Clause], language: Capabilities.Language)(implicit sig : Signature) : String = {
    if (!capabilities.contains(language)) throw new IllegalArgumentException(s"Prover $name does not support the given language.")
    else {
      if (language == Capabilities.THF) createTHFProblem(problem)(sig)
      else if (language == Capabilities.TFF) createTFFProblem(problem)(sig)
      else if (language == Capabilities.FOF) throw new NotImplementedError("FOF export not yet implemented")
      else throw new IllegalArgumentException("unexpected TPTP output format")
    }
  }

  final private def startProver(parsedProblem : String, problem : Set[C], timeout : Int, args : Seq[String] = Seq()) : Future[TptpResult[C]] = {
    val process : KillableProcess = {
      val safeProverName = java.net.URLEncoder.encode(name, "UTF-8")
      val file = File.createTempFile(s"remoteInvoke_${safeProverName}_", ".p")
      if (!Configuration.isSet("overlord")) file.deleteOnExit()
      leo.Out.debug(s"Sending proof obligation ${file.toString}")
      val writer = new PrintWriter(file)
      try {
        writer.print(parsedProblem)
      } finally writer.close()
      // FIX ME : If a better solution for obtaining the processID is found
      val res = constructCall(args, timeout, file.getAbsolutePath)
      leo.Out.debug(s"Call constructed: ${res.mkString(" ")}")
      KillableProcess(res.mkString(" "))
    }
    new SZSKillFuture(process, problem, timeout)
  }

  protected[TptpProver] class TptpResultImpl(originalProblem : Set[C], passedSzsStatus : StatusSZS, passedExitValue : Int, passedOutput : Iterable[String], passedError : Iterable[String]) extends TptpResult[C] {
    /**
      * The name of the original prover called..
      * @return prover name
      */
    val proverName : String = name

    /**
      * The path of the original prover.
      * @return prover path
      */
    val proverPath : String = path

    /**
      * Returns the original problem, passed to the external prover
      * @return
      */
    val problem : Set[C] = originalProblem

    /**
      * The SZS status of the external prover if one was set
      * or [[leo.modules.output.SZS_Forced]] if the process was killed.
      *
      * @return SZSStatus of the problem
      */
    val szsStatus : StatusSZS = passedSzsStatus

    /**
      * The exit value of the prover
      * @return Passed through exitValue
      */
    val exitValue : Int = passedExitValue

    /**
      * The complete system output of the prover.
      *
      * @return system out
      */
    val output : Iterable[String] = passedOutput

    /**
      * The complete system error of the prover
      *
      * @return system err
      */
    val error : Iterable[String] = passedError
  }

  /**
    * Performs a translation of the result of the external process.
    *
    * The standard implementation reads the stdOut and searches for
    * TPTP conform SZS result
    *
    * @param originalProblem the original set of formulas, passed to the process
    * @param process the process itself
    * @return the result of the external prover, run on the originalProblem
    */
  protected def translateResult(originalProblem : Set[C], process : KillableProcess) : TptpResult[C] = {
    try{
      val exitValue = process.exitValue
      val output = scala.io.Source.fromInputStream(process.output).getLines().toSeq
      val error = scala.io.Source.fromInputStream(process.error).getLines().toSeq

      val it = output.iterator
      var szsStatus: StatusSZS = null
      while (it.hasNext && szsStatus == null) {
        val line = it.next()
        StatusSZS.answerLine(line) match {
          case Some(status) => szsStatus = status
          case _ => ()
        }
      }
      if (szsStatus == null) {
        szsStatus = SZS_GaveUp
      }
      new TptpResultImpl(originalProblem, szsStatus, exitValue, output, error)
    } catch {
      case e : Exception => new TptpResultImpl(originalProblem, SZS_Error, 51, Seq(), Seq(e.getMessage))
    }
  }

  class SZSKillFuture(process : KillableProcess, originalProblem : Set[C], timeout : Int) extends Future[TptpResult[C]] {

    private var result : TptpResult[C] = _
    private var isTerminated = false    // If this is true, result has been set
    private val startTime = System.currentTimeMillis()
    private lazy val timeoutMilli = (timeout + ExternalProver.WAITFORTERMINATION) * 1000

    /**
      * Checks for the processes termination.
      *
      * @return true, iff the processes has finished.
      */
    override def isCompleted: Boolean = synchronized{
      internalIsCompleted
    }

    private def internalIsCompleted : Boolean = {
      if(!isTerminated) {
        if(!process.isAlive){
          // The external process is finished. Put the result in an Resultobject.
          result = translateResult(originalProblem, process)
          isTerminated = true
        } else {
          // The process is still alive. Check for timeout and kill if it is over
          val cTime = System.currentTimeMillis()
          if(cTime - startTime > timeoutMilli) {
            result = new TptpResultImpl(originalProblem, SZS_Forced, 51, Seq(), Seq(s"$name has exceeded its timelimit of $timeout and was force fully killed."))
            process.kill
            isTerminated = true
          } else {
            isTerminated = false
          }
        }
      } else {
        // The process was previously finished. Return the previously returned result
        isTerminated = true
      }
      isTerminated
    }

    /**
      * Returns the result object after the process has finished.
      *
      * @return Some(result) if the process has finished, None otherwise.
      */
    override def value: Option[TptpResult[C]] = synchronized{if(isCompleted) Some(result) else None}

    override def blockValue : TptpResult[C] = synchronized{
      if(isTerminated) return result
      try {
        val time = timeoutMilli - (System.currentTimeMillis() - startTime)
        if(time > 0) {
          process.waitFor(time, TimeUnit.MILLISECONDS)
          result = translateResult(originalProblem, process)
        } else {
          result = new TptpResultImpl(originalProblem, SZS_Timeout, 51, Seq(), Seq(s"$name has exceeded its timelimit of $timeout and was force fully killed."))
        }
      } catch {
        case e : InterruptedException =>
          leo.Out.info(s"Call to prover $name was terminated by an interrupted exception.")
          result = new TptpResultImpl(originalProblem, SZS_Forced, 51, Seq(), Seq(s"$name has encountered an interrupted exception."))
        case _ : Exception =>
          leo.Out.info(s"Call to prover $name was terminated by an exception.")
          result = new TptpResultImpl(originalProblem, SZS_Forced, 51, Seq(), Seq(s"$name has encountered an exception."))
      }
      isTerminated = true
      result
    }

    /**
      * Forcibly kills the underlying process calculating the future's result.
      */
    override def kill(): Unit = process.kill
  }
}


trait TptpResult[C <: ClauseProxy] {
  /**
    * The name of the original prover called..
    * @return prover name
    */
  def proverName : String

  /**
    * The path of the original prover.
    * @return prover path
    */
  def proverPath : String

  /**
    * Returns the original problem, passed to the external prover
    * @return
    */
  def problem : Set[C]

  /**
    * The SZS status of the external prover if one was set
    * or [[leo.modules.output.SZS_Forced]] if the process was killed.
    *
    * @return SZSStatus of the problem
    */
  def szsStatus : StatusSZS

  /**
    * The exit value of the prover
    * @return Passed through exitValue
    */
  def exitValue : Int

  /**
    * The complete system output of the prover.
    *
    * @return system out
    */
  def output : Iterable[String]

  /**
    * The complete system error of the prover
    *
    * @return system err
    */
  def error : Iterable[String]
}

/**
  * A smaller interface for scala's [[scala.concurrent.Future]] class.
  * Implements the bare necessities for a Future in this setting.
  *
  * @tparam T - Type of the Result of the Future
  */
trait Future[+T] {
  /**
    * Checks for the processes termination.
    * @return true, iff the processes has finished.
    */
  def isCompleted : Boolean

  /**
    * Returns the result object after the process has finished.
    *
    * @return Some(result) if the process has finished, None otherwise.
    */
  def value : Option[T]

  /**
    * Blocks until the value is set.
    * After a call to blockValue isCompleted will return true and `value` will
    * always return the value of blockValue.
    * @return
    */
  def blockValue : T

  /**
    * Forcibly kills the underlying process calculating the future's result.
    */
  def kill(): Unit
}
