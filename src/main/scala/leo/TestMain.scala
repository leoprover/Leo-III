package leo

import leo.datastructures.ClauseProxy
import leo.datastructures.blackboard.{Blackboard, DoneEvent, SignatureBlackboard}
import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSDataStore}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.{BetaSplit, Context}
import leo.modules.agent.relevance_filter.BlackboardPreFilterSet
import leo.modules.relevance_filter.{PreFilterSet, SeqFilter}
import leo.modules._
import leo.modules.external.ExternalCall
import leo.modules.output._
import leo.modules.phase._
import leo.modules.Utility._
import leo.modules.interleavingproc._
import leo.agents.InterferingLoopAgent
import leo.modules.parsers.CLParameterParser


/**
  * Created by mwisnie on 3/7/16.
  */
object TestMain {
  def main(args : Array[String]): Unit ={
    try {
      Configuration.init(new CLParameterParser(args))
    } catch {
      case e: IllegalArgumentException => {
        Configuration.help()
        return
      }
    }
    if ((args(0) == "-h") || Configuration.HELP){
      Configuration.help()
      return
    }

    val startTime : Long = System.currentTimeMillis()


    if (Configuration.isSet("seq")) {
      try {
        import leo.modules.seqpproc.SeqPProc
        SeqPProc(startTime)
      } catch {
        case e:SZSException => {
          if (e.getMessage != null) {
            Out.info(e.getMessage)
            Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE,e.getMessage))
          } else {
            Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE))
          }
          Out.debug(e.debugMessage)
          Out.trace(stackTraceAsString(e))
          if (e.getCause != null) {
            Out.trace("Caused by: " + e.getCause.getMessage)
            Out.trace("at: " + e.getCause.getStackTrace.toString)
          }
//          Out.trace(Utility.userDefinedSignatureAsString)
        }
        case e:Throwable => {
          if (e.getMessage != null) {
            Out.info(e.getMessage)
            Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE,e.getMessage))
          } else {
            Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE))
          }
          Out.trace(stackTraceAsString(e))
          if (e.getCause != null) {
            Out.trace("Caused by: " + e.getCause.getMessage)
            Out.trace("at: " + e.getCause.getStackTrace.toString)
          }
//          Out.trace(Utility.userDefinedSignatureAsString)
        }
      } finally {
        Scheduler().killAll()
        System.exit(0)
      }
    } else if(Configuration.isSet("exttest")){
      testExternalProvers()
    } else {
      import leo.datastructures.Signature

      implicit val sig: Signature = Signature.freshWithHOL()
      SignatureBlackboard.set(sig)
      val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT

      val TimeOutProcess = new DeferredKill(timeout, timeout)
      TimeOutProcess.start()

      val interval = 10
      val config = {
        val sb = new StringBuilder()
        sb.append(s"problem(${Configuration.PROBLEMFILE}),")
        sb.append(s"time(${Configuration.TIMEOUT}),")
        sb.append(s"proofObject(${Configuration.PROOF_OBJECT}),")
        sb.append(s"sos(${Configuration.SOS}),")
        // TBA ...
        sb.init.toString()
      }
      Out.comment(s"Configuration: $config")


      val loadphase = new LoadPhase(Configuration.PROBLEMFILE)
      val filterphase = new SeqFilterPhase()


      Blackboard().addDS(FormulaDataStore)
      Blackboard().addDS(BlackboardPreFilterSet)
      Blackboard().addDS(SZSDataStore)

      printPhase(loadphase)
      if (!loadphase.execute()) {
        Scheduler().killAll()
        TimeOutProcess.kill()
        unexpectedEnd(System.currentTimeMillis() - startTime)
        return
      }

      val afterParsing = System.currentTimeMillis()
      val timeWOParsing: Long = afterParsing - startTime

      printPhase(filterphase)
      if (!filterphase.execute()) {
        Scheduler().killAll()
        TimeOutProcess.kill()
        unexpectedEnd(System.currentTimeMillis() - startTime)
        return
      }

      val timeForFilter: Long = System.currentTimeMillis() - afterParsing
      leo.Out.finest(s"Filter Time : ${timeForFilter}ms")

      leo.Out.debug("Used :")
      leo.Out.debug(FormulaDataStore.getFormulas.map(_.pretty(sig)).mkString("\n"))
      leo.Out.debug("Unused : ")
      leo.Out.debug(PreFilterSet.getFormulas.mkString("\n"))

      val state = BlackboardState.fresh[InterleavingLoop.A](sig)
      val uniStore = new UnificationStore[InterleavingLoop.A]()
      val iLoop : InterleavingLoop = new InterleavingLoop(state, uniStore, sig)
      val iLoopAgent = new InterferingLoopAgent[StateView[InterleavingLoop.A]](iLoop)
      val uniAgent = new DelayedUnificationAgent(uniStore, state, sig)

      val iPhase = new InterleavableLoopPhase(iLoopAgent, state, sig, uniAgent)


      Blackboard().addDS(state)
      Blackboard().addDS(uniStore)

      printPhase(iPhase)
      if(!iPhase.execute()){
        Scheduler().killAll()
          TimeOutProcess.kill()
          unexpectedEnd(System.currentTimeMillis() - startTime)
          return
      }

      TimeOutProcess.kill()
      val endTime = System.currentTimeMillis()
      val time = System.currentTimeMillis() - startTime
      Scheduler().killAll()

//      val szsStatus: StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Unknown: StatusSZS) { x => x }
      val szsStatus  = state.state.szsStatus
      Out.output("")
      Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms resp. ${endTime - afterParsing} ms w/o parsing"))

//      val proof = FormulaDataStore.getAll(_.cl.lits.isEmpty).headOption // Empty clause suchen
      val proof = state.state.derivationClause
      if (szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof.isDefined) {
        Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
        //      Out.output(makeDerivation(derivationClause).drop(1).toString)
        Out.output(Utility.userConstantsForProof(sig))
        Out.output(Utility.proofToTPTP(Utility.proofOf(proof.get)))
        Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
      }
    }
  }

  private def unexpectedEnd(time : Long) {
    val szsStatus : StatusSZS = SZSDataStore.getStatus(Context()).fold(SZS_Timeout : StatusSZS){x => x}
    Out.output("")
    Out.output(SZSOutput(szsStatus, Configuration.PROBLEMFILE, s"${time} ms"))
  }

  private def printPhase(p : Phase) = {
    Out.debug(" ########################")
    Out.debug(s" Starting Phase ${p.name}")
    Out.debug(p.description)
  }

  private def testExternalProvers(): Unit ={
    Configuration.ATPS foreach { case (name, cmd) =>
      val r = ExternalCall.exec(cmd+" "+Configuration.PROBLEMFILE)
        println(s"Output ($name) ${r.out.mkString("\n")}\n\n Error ($name)\n ${r.error.mkString("\n")}")
    }
  }

  /**
    * Thread to kill leo.
    *
    * @param interval
    * @param timeout
    */
  private class DeferredKill(interval : Double, timeout : Double) extends Thread {

    var remain : Double = timeout
    var exit : Boolean = false

    private var finished = false

    def isFinished = synchronized(finished)

    def kill() : Unit = {
      synchronized{
        exit = true
        this.interrupt()
        Out.finest("Scheduler killed before timeout.")
        Blackboard().filterAll(_.filter(DoneEvent()))
        Scheduler().killAll()
      }
    }

    override def run(): Unit = {
      //      println("Init delay kill.")
      synchronized{
        while(remain > 0 && !exit) {
          try {
            val w : Double = if (remain > interval) interval else remain
            wait((w * 1000).toInt)
          } catch {
            case e: InterruptedException => if(exit) return else Thread.interrupted()
            case _: Throwable => ()
          } finally {
            if(!exit) {
              Scheduler().signal()
              //agentStatus()
              remain -= interval
              Out.finest(s"Leo-III is still working. (Remain=$remain)")
            }
          }
        }
        SZSDataStore.setIfEmpty(SZS_Timeout)
        Out.finest(s"Timeout: Killing all Processes.")
        finished = true
        //TODO: Better mechanism
        Blackboard().filterAll(_.filter(DoneEvent()))
        Scheduler().killAll()
      }
    }
  }
}
