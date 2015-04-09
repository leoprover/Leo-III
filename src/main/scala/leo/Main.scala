package leo


import java.io.File

import leo.agents.impl.{CounterContextControlAgent, ContextControlAgent}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules._
import leo.modules.Utility._
import leo.modules.output._
import leo.modules.Phase._


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


      val deferredKill: DeferredKill = new DeferredKill(interval, timeout)
      deferredKill.start()

      // Create Scheduler
      Scheduler(Configuration.THREADCOUNT)

      var it: Iterator[Phase] = null
      if (Configuration.COUNTER_SAT) {
        CounterContextControlAgent.register()
        it = getCounterSat.iterator
      } else if (Configuration.isSet("with-prover")) {
        ContextControlAgent.register()
        it = getExternalPhases.iterator
      } else {
        ContextControlAgent.register()
        it = getHOStdPhase.iterator
      }
      var r = true
      while (it.hasNext && r && !deferredKill.finished) {
        val phase = it.next()
        Out.info(s"\n [Phase]:\n  Starting ${phase.name}\n${phase.description}")
        val start = System.currentTimeMillis()
        r = phase.execute()
        val end = System.currentTimeMillis()
        Out.info(s"\n [Phase]:\n  Ended ${phase.name}\n  Time: ${end - start}ms")
      }
      deferredKill.kill()

      Out.output(s"% SZS status ${Blackboard().getStatus(Context()).fold(SZS_Unknown.output)(_.output)} for ${Configuration.PROBLEMFILE}")
      if (Configuration.PROOF_OBJECT) Blackboard().getAll { p => p.clause.isEmpty}.foreach(Utility.printDerivation(_))
      val endTime = System.currentTimeMillis()
      //    Out.output("Main context "+Context().contextID)
      //    formulaContext(Context())
      //    for(c <- Context().childContext){
      //      formulaContext(c)
      //    }
      Out.output("% Time: " + (endTime - beginTime) + "ms")

    } catch {
      case e:SZSException => {
        if (e.getMessage != null) {
          Out.comment(e.getMessage)
          Out.info(e.getMessage)
        }
        Out.output(SZSOutput(e.status, Configuration.PROBLEMFILE))
        Out.debug(e.debugMessage)
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
      }
      case e:Throwable => {
        if (e.getMessage != null) {
          Out.comment(e.getMessage)
          Out.info(e.getMessage)
        }
        Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE))
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
      }
    } finally {
      Scheduler().killAll()
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

    var finished = false

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
            }
          }
        }
        Blackboard().forceStatus(Context())(SZS_Timeout)
        //Out.output(SZSOutput(SZS_Timeout))    // TODO Interference with other SZS status
        finished = true
        Scheduler().killAll()
      }
    }
  }
}

