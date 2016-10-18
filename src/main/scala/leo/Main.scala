package leo

//import leo.agents.impl.{FormulaSelectionAgent, CounterContextControlAgent, ContextControlAgent}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules._
import leo.modules.Utility._
import leo.modules.output._
//import leo.modules.phase.{PreprocessPhase, LoadPhase, Phase}
//import Phase._


/**
 * Entry Point for Leo-III as an executable to
 * proof a TPTP File
 *
 * @author Max Wisniewski
 * @since 7/8/14
 */
object Main {

  /**
   *
   * Tries to proof a Given TPTP file in
   * a given Time.
   *
   * @param args - See [[Configuration]] for argument treatment
   */
  def main(args : Array[String]){
    try {
      val beginTime = System.currentTimeMillis()
      try {
        Configuration.init(new CLParameterParser(args))
      } catch {
        case e: IllegalArgumentException => {
          Out.severe(e.getMessage)
          Configuration.help()
          return
        }
      }
      if (Configuration.HELP) {
        Configuration.help()
        return
      }

      val timeout = if (Configuration.TIMEOUT == 0) Double.PositiveInfinity else Configuration.TIMEOUT
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

      if (Configuration.isSet("seq")) {
        import leo.modules.seqpproc.SeqPProc
        SeqPProc(beginTime)
      } else {
        throw new SZSException(SZS_UsageError, "standard mode not included right now, use --seq")

//        val deferredKill: DeferredKill = new DeferredKill(interval, timeout)
//        deferredKill.start()
//
//        // Create Scheduler
//        Scheduler()
//
//        //==========================================
//        //
//        // Initialize Phases and data strucutres for the blackboard.
//        //
//        //==========================================
//        Blackboard().addDS(FormulaDataStore)
//        Blackboard().addDS(SZSDataStore)
//        Blackboard().addDS(UnificationStore)
//        Blackboard().addDS(SelectionTimeStore)
//        Blackboard().addDS(UnificationTaskStore)
//        //      Utility.printSignature()
//        var it: Iterator[Phase] = null
//        if (Configuration.COUNTER_SAT) {
//          new FifoController(CounterContextControlAgent).register()
//          it = getCounterSat.iterator
//        } else if (Configuration.isSet("with-prover")) {
//          new FifoController(ContextControlAgent).register()
//          it = getExternalPhases.iterator
//        } else {
//          new FifoController(ContextControlAgent).register()
//          it = getHOStdPhase.iterator
//        }
//        var r = true
//        while (it.hasNext && r && !deferredKill.isFinished) {
//          val phase = it.next()
//          Out.info(s"\n [Phase]:\n  Starting ${phase.name}\n${phase.description}")
//          val start = System.currentTimeMillis()
//          r = phase.execute()
//          val end = System.currentTimeMillis()
//          Out.info(s"\n [Phase]:\n  Ended ${phase.name}\n  Time: ${end - start}ms")
//        }
//        if (!deferredKill.isFinished) deferredKill.kill()
//
//
//        val endTime = System.currentTimeMillis()
//
//        //      println("=============\n   Passive\n================")
//        //      SelectionTimeStore.all(FormulaType).foreach{case f : FormulaStore => println(SelectionTimeStore.get(f).get.pretty+"@"+f.pretty)}
//        //      println("=============\n   Active\n================")
//        //      SelectionTimeStore.noSelect(Context()).foreach{case f => println(f.created.pretty+"@"+f.pretty)}
//
//        val szs_status = SZSDataStore.getStatus(Context()).getOrElse(SZS_Unknown)
//        Out.output(SZSOutput(szs_status, Configuration.PROBLEMFILE, s"${endTime - beginTime} ms"))
//
//        // TODO build switch for mulitple contexts
//        // if (Configuration.PROOF_OBJECT) FormulaDataStore.getAll { p => p.clause.isEmpty}.foreach(Utility.printDerivation(_))
//        //Utility.formulaContext()
//        if (Configuration.PROOF_OBJECT) {
//          Out.comment(s"SZS output start Proof for ${Configuration.PROBLEMFILE}")
//          import leo.datastructures.Clause.empty
//          FormulaDataStore.getAll { p => empty(p.clause) }.headOption.fold(Out.comment("No proof found."))(Utility.printProof(_))
//          Out.comment(s"SZS output end Proof for ${Configuration.PROBLEMFILE}")
//        }
      }
      
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
      }
      case e:Throwable => {
        Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE,e.toString))
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
      }
    } finally {
//      Scheduler().killAll()
      System.exit(0)
    }
  }



  /**
   * Thread to kill leo.
   *
   * TODO: Hook to let the kill Thread die.
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
        Out.info("Scheduler killed before timeout.")
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
              Out.info(s"Leo-III is still working. (Remain=$remain)")
            }
          }
        }
        SZSDataStore.forceStatus(Context())(SZS_Timeout)
        Out.info(s"Timeout: Killing all Processes.")
        finished = true
        //TODO: Better mechanism
        Blackboard().filterAll(_.filter(DoneEvent()))
        Scheduler().killAll()
      }
    }
  }
}

