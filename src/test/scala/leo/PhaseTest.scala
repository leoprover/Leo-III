package leo

import leo.agents.impl.ContextControlAgent
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.datastructures.{Role_NegConjecture, Role_Conjecture}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.impl.Signature
import leo.modules.{SimpleEnumerationPhase, ParamodPhase, PreprocessPhase, Utility}
import org.scalatest._

/**
 * Tests a typical Main of a LeoPARD application.
 *
 */
class PhaseTest extends FunSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

  val b = Blackboard()
  val sig = Signature.get

  // Used Phases for the test
  val tphases = List(PreprocessPhase, ParamodPhase)
  val hophases = List(PreprocessPhase, SimpleEnumerationPhase, ParamodPhase)

  // Used Problems for the test
  val problems = Seq(
    "ex1" -> ("Problem 1", tphases),
    "ex2" -> ("Problem 2", tphases),
    "ex3" -> ("Problem 3", tphases),
    "SET014^4" -> ("HO-Problem 1", hophases)
  )

  for(p <- problems){
    test(s"Execution Tests ${p._2._1}") {
      println("##################################")
      println("######### Execution Test #########")
      println(s"##### ${p._2._1}")
      println(s"## Parsing ${p._1}$problem_suffix ...")


      Utility.load(source + "/" +  p._1 + ".p")
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

      Scheduler().clear()
      b.clear()
      Signature.resetWithHOL(sig)
    }
  }
}
