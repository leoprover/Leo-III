package leo.modules.phase
import leo.{Configuration, Out}
import leo.agents.Agent
import leo.datastructures.{AnnotatedClause, Clause, Literal, Signature}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.agent.rules.RuleAgent
import leo.modules.agent.rules.control_rules.{AnnotatedClauseGraph, Processed, Unify, Unprocessed}
import leo.modules.control.Control
import leo.modules.parsers.Input
import leo.modules.seqpproc.{SeqPProc, State}

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
(ruleGraph : AnnotatedClauseGraph)
(implicit val sig : Signature, implicit val blackBoard: Blackboard, implicit val sched : Scheduler)
extends CompletePhase(blackBoard, sched, RuleAgentPhase.endOn(ruleGraph.outType), Seq(ruleGraph.outType))
{
  override def name: String = "rule_agent_phase"
  override protected final val agents: Seq[Agent] = Seq()
  var negSet : Boolean = false

  override def execute(): Boolean = {
    if (Configuration.ATPS.nonEmpty) {
      import leo.modules.external.ExternalProver
      Configuration.ATPS.foreach { case(name, path) =>
        try {
          val p = ExternalProver.createProver(name,path)

          // TODO External Agent implement

          leo.Out.info(s"$name registered as external prover.")
          leo.Out.info(s"$name timeout set to:${Configuration.ATP_TIMEOUT(name)}.")
        } catch {
          case e: NoSuchElementException => leo.Out.warn(e.getMessage)
        }
      }
    }

    // TODO Remove state from the processing
    val state : State[AnnotatedClause] = State.fresh(sig)
    val input2 = Input.parseProblem(Configuration.PROBLEMFILE)
    val remainingInput = SeqPProc.effectiveInput(input2, state)
    // Typechecking: Throws and exception if not well-typed
    SeqPProc.typeCheck(remainingInput, state)

    var initSet : Set[AnnotatedClause] = Set()
    var negConj : AnnotatedClause = null

    if (state.negConjecture != null) {
      // Expand conj, Initialize indexes
      // We expand here already since we are interested in all symbols (possibly contained within defined symbols)
      Out.debug("## Preprocess Neg.Conjecture BEGIN")
      Out.trace(s"Neg. conjecture: ${state.negConjecture.pretty(sig)}")
      val simpNegConj = Control.expandDefinitions(state.negConjecture)
      negConj = simpNegConj
      Control.initIndexes(simpNegConj +: remainingInput)
      val result = SeqPProc.preprocess(state, simpNegConj).filterNot(cw => Clause.trivial(cw.cl))
      Out.debug(s"# Result:\n\t${
        result.map {
          _.pretty(sig)
        }.mkString("\n\t")
      }")
      Out.trace("## Preprocess Neg.Conjecture END")
      initSet = result
      negSet = true
    } else {
      Control.initIndexes(remainingInput)
    }

    // Preprocessing
    Out.debug("## Preprocess BEGIN")
    val preprocessIt = remainingInput.iterator
    while (preprocessIt.hasNext) {
      val cur = preprocessIt.next()
      Out.trace(s"# Process: ${cur.pretty(sig)}")
      val processed = SeqPProc.preprocess(state, cur)
      Out.debug(s"# Result:\n\t${
        processed.map {
          _.pretty(sig)
        }.mkString("\n\t")
      }")
      val preprocessed = processed.filterNot(cw => Clause.trivial(cw.cl))
      initSet = initSet union preprocessed

      if (preprocessIt.hasNext) Out.trace("--------------------")
    }
    Out.trace("## Preprocess END\n\n")

    ruleGraph.initGraph(initSet)(Option(negConj))

    super.execute()

    true
  }
}
