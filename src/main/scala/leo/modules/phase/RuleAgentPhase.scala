package leo.modules.phase
import leo.{Configuration, Out}
import leo.agents.Agent
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.agent.rules.control_rules.AnnotatedClauseGraph
import leo.modules.control.Control
import leo.modules.prover._

object RuleAgentPhase {
  def endOn[A](dt : DataType[A])(d : Delta) : Boolean = {
    val inserts = d.inserts(dt).nonEmpty
    inserts
  }
}

/**
  * Created by mwisnie on 4/24/17.
  */
class RuleAgentPhase
(ruleGraph : AnnotatedClauseGraph, parsedProblem: Seq[AnnotatedFormula])
(implicit val state : Control.LocalFVState,
 implicit val blackBoard: Blackboard, implicit val sched : Scheduler)
extends CompletePhase(blackBoard, sched, RuleAgentPhase.endOn(ruleGraph.outType), Seq(ruleGraph.outType))
{
  implicit val sig : Signature = state.signature
  override def name: String = "rule_agent_phase"
  override protected final val agents: Seq[Agent] = Seq()

  var parsingTime : Long = 0l

  override def execute(): Boolean = {
    if (Configuration.ATPS.nonEmpty) {
      import leo.modules.external.ExternalProver
      Configuration.ATPS.foreach { case(name, path) =>
        try {
          val p = ExternalProver.createProver(name,path)

          state.addExternalProver(p)

          leo.Out.info(s"$name registered as external prover.")
          leo.Out.info(s"$name timeout set to:${Configuration.ATP_TIMEOUT(name)}.")
        } catch {
          case e: NoSuchElementException => leo.Out.warn(e.getMessage)
        }
      }
    }

//    val start = System.currentTimeMillis()
//    val input2 = Input.parseProblemFile(Configuration.PROBLEMFILE)
    parsingTime = 0 // TODO: Since parsing is now done at Main
    val remainingInput = effectiveInput(parsedProblem, state)

    typeCheck(remainingInput, state)

    var initSet : Set[AnnotatedClause] = remainingInput.toSet
    var negConj : AnnotatedClause = null

    if (state.negConjecture != null) {
      // Expand conj, Initialize indexes
      // We expand here already since we are interested in all symbols (possibly contained within defined symbols)
      val simpNegConj = Control.expandDefinitions(state.negConjecture)
      negConj = simpNegConj
      Control.initIndexes(simpNegConj +: remainingInput)(state)
      initSet = initSet + simpNegConj
    } else {
      Control.initIndexes(remainingInput)
    }

    ruleGraph.initGraph(initSet)

    super.execute()

    true
  }
}
