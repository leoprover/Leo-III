package leo

import leo.agents.impl.ContextControlAgent
import leo.datastructures.{Role_Conjecture, Role_NegConjecture}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.modules._
import leo.modules.output.SZS_Timeout

/**
 * Created by lex on 09.04.15.
 */
class ExtProverTest extends LeoTestSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

  //  Out.setLogLevel(FINEST)

  val b = Blackboard()
  val sig = Signature.get



  // Used Problems for the test
  val problems = Seq(
    "SET014^4" -> ("SET014^4 with ext. prover"),
    "SET014^5" -> ("SET014^5 with ext. prover"),
    "SET027^5" -> ("SET027^5 with ext. prover"),
    "SET067^1" -> ("SET067^1 with ext. prover")
  )

  printHeading(s"External prover tests")

  // DECIDE ON PROVER HERE
  val leopath = System.getenv("LEO2_PATH")
  val satallaxpath = System.getenv("SATALLAX_PATH")

  if (leopath != null) {
    Configuration.init(new CLParameterParser(Array("dummy", "--with-prover", "leo2", "-v", "2")))
  } else if (satallaxpath != null) {
    Configuration.init(new CLParameterParser(Array("dummy", "--with-prover", "satallax", "-v", "2")))
  } else {
    Configuration.init(new CLParameterParser(Array("dummy", "--with-prover", "remote-leo2", "-v", "2")))
  }
  ///

  for (p <- problems) {
    test(p._2) {
      printHeading(s"External prover tests")
      if (leopath != null) {
        Out.output("%%% Found LEO-II path in environment, using LEO-II for ext. prover tests.")
      } else if (satallaxpath != null) {
        Out.output("%%% Found Satallax path in environment, using Satallax for ext. prover tests.")
      } else {
        Out.output("%%% Found neither LEO-II nor Satallax path in environment, using remote call to LEO-II (via System-On-TPTP) for ext. prover tests.")
        Out.output("%%% This requires, of course, a running internet connection. ")
      }

      val deferredKill: DeferredKill = new DeferredKill(10, 15)
      deferredKill.start()

      // Used Phases for the test
      val extPhases = List(PreprocessPhase, ExternalProverPhase)


      println(s"## Running on ${p._1}$problem_suffix ...")

      Utility.load(source + "/" +  p._1 + ".p")
      //Negate the conjecture by ourselves, since the load phase uses Configurations (not present here)
      Blackboard().getAll(_.role == Role_Conjecture) foreach { f =>
        assert(f.clause.lits.size == 1, "Found a conjecture with more than one literal.")
        val nf = f.newClause(f.clause.mapLit(_.flipPolarity)).newRole(Role_NegConjecture).newOrigin(List(f),"Negate-Conjecture")
        b.removeFormula(f)
        b.addFormula(nf)
      }

      ContextControlAgent.register()
      var it: Iterator[Phase] = extPhases.iterator
      var r = true
      while (it.hasNext && r) {
        val phase = it.next()
        Out.info(s"\n [Phase]:\n  Starting ${phase.name}\n${phase.description}")
        val start = System.currentTimeMillis()
        r = phase.execute()
        val end = System.currentTimeMillis()
        Out.info(s"\n [Phase]:\n  Ended ${phase.name}\n  Time: ${end - start}ms")
      }
      deferredKill.kill()
      Out.output(s"%SZS Status ${Blackboard().getStatus(Context()).fold("Unkown")(_.output)} for ${p._1}")
      ContextControlAgent.unregister()

      Scheduler().clear()
      Utility.clear()
      Scheduler().clear()
      b.clear()
      Signature.resetWithHOL(sig)
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
