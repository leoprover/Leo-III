package leo.modules.external

import java.io.{File, PrintWriter}

import leo.datastructures.{ClauseProxy, Signature}
import leo.modules.output.{SZS_Error, SZS_Forced, SZS_GaveUp, StatusSZS}

/**
  *
  * Capsules the call to an atp system with SZS conform return syntax.
  *
  * @since 1/17/17
  * @author Max Wisniewski
  *
  */
trait TptpProver[C <: ClauseProxy] {
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
    * @param problem the set of formulas to be checked.
    * @param timeout the timeout for the prover in seconds
    * @param args additional arguments for the prover
    * @param sig the current signature
    * @return A Future with the result of the prover.
    */
  def call(problem : Set[C], timeout : Int, args : Seq[String] = Seq())(implicit sig : Signature) : Future[TptpResult[C]] = {
    val parsedProblem = translateProblem(problem)
    startProver(parsedProblem, problem, timeout, args)
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
  protected def translateProblem(problem : Set[C])(implicit sig : Signature) : Seq[String]

  final private def startProver(parsedProblem : Seq[String], problem : Set[C], timeout : Int, args : Seq[String] = Seq()) : Future[TptpResult[C]] = {
    val process : KillableProcess = {
      val file = File.createTempFile("remoteInvoke", ".p")
      file.deleteOnExit()
      val writer = new PrintWriter(file)
      try {
        parsedProblem foreach { out =>
          writer.println(out)
        }
      } finally writer.close()
      // FIX ME : If a better solution for obtaining the processID is found
      val res = constructCall(args, timeout, file.getAbsolutePath)
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
    * Forcibly kills the underlying process calculating the future's result.
    */
  def kill(): Unit
}
