package leo

import leo.agents.impl.ContextControlAgent
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.datastructures.{Role_NegConjecture, Role_Conjecture}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.impl.Signature
import leo.modules._
import leo.modules.output.SZS_Timeout
import org.scalatest._
import java.util.logging.Level._

/**
 * Tests a typical Main of a LeoPARD application.
 *
 */
class PhaseTest extends FunSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

//  Out.setLogLevel(FINEST)

  val b = Blackboard()
  val sig = Signature.get

  // Used Phases for the test
  val tphases = List(SimplificationPhase, ParamodPhase)
  val hophases = List(PreprocessPhase, SimpleEnumerationPhase, ParamodPhase)

  // Used Problems for the test
  val problems = Seq(
    "ex1" -> ("Prop-Problem 1", tphases),
    //"ex2" -> ("Prop-Problem 2", tphases),
    "ex3" -> ("Prop-Problem 3", tphases),
    "SET014^4" -> ("HO-Problem 1", hophases),
    "SET014^5" -> ("HO-Problem 2", hophases),
    "SET027^5" -> ("HO-Problem 3", hophases),
    "SET067^1" -> ("HO-Problem 4", hophases),
    "SYN973+1" -> ("FOF-Problem 1", hophases),
    "SYN974+1" -> ("FOF-Probelm 2", hophases),
    "SYN978+1" -> ("FOF-Problem 3", hophases)
    //"COM001_1" -> ("TFF-Problem 1", hophases)
    //"COM003_1" -> ("TFF-Problem 2", hophases),
    //"KRS003_1" -> ("TFF-Problem 3", hophases)
  )

  test(s"Phase Test") {
    for(p <- problems){
      println("##################################")
      println("######### Execution Test #########")
      println(s"##### ${p._2._1}")
      println(s"## Parsing ${p._1}$problem_suffix ...")

      try{
        Utility.load(source + "/" +  p._1 + ".p")
      } catch {
        case e : SZSException =>
          Out.output(s"Execution Test ${p._2._1} failed\n   Status=${e.status}\n   Msg=${e.getMessage}\n   DbgMsg=${e.debugMessage}")
          fail()
      }

      //Negate the conjecture by ourselves, since the load phase uses Configurations (not present here)
      Blackboard().getAll(_.role == Role_Conjecture) foreach { f =>
        assert(f.clause.lits.size == 1, "Found a conjecture with more than one literal.")
        val nf = f.newClause(f.clause.mapLit(_.flipPolarity)).newRole(Role_NegConjecture).newOrigin(List(f),"Negate-Conjecture")
        b.removeFormula(f)
        b.addFormula(nf)
      }
      ContextControlAgent.register()
      val it = p._2._2.iterator
      var r = true
      while(it.hasNext && r) {
        val phase = it.next()
        Out.output(s"\n [Phase]:\n  Starting ${phase.name}\n${phase.description}")
        val start = System.currentTimeMillis()
        r = phase.execute()
        val end = System.currentTimeMillis()
        Out.output(s"\n [Phase]:\n  Ended ${phase.name}\n  Time: ${end-start}ms")
      }

      Out.output(s"%SZS Status ${Blackboard().getStatus(Context()).fold("Unkown")(_.output)} for ${p._1}")
      Blackboard().getAll{p => p.clause.isEmpty}.foreach(Utility.printDerivation(_))

      ContextControlAgent.unregister()


      Scheduler().clear()
      Utility.clear()
      Signature.resetWithHOL(sig)
    }
  }
}
