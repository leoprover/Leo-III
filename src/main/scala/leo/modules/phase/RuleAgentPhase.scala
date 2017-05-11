package leo.modules.phase
import leo.{Configuration, Out}
import leo.agents.Agent
import leo.datastructures.{AnnotatedClause, Clause, Literal, Signature}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.agent.rules.RuleAgent
import leo.modules.agent.rules.control_rules.{Processed, Unify, Unprocessed}
import leo.modules.control.Control
import leo.modules.parsers.Input
import leo.modules.seqpproc.{SeqPProc, State}

object RuleAgentPhase {
  def endOn(d : Delta) : Boolean = {
    val clauses : Seq[AnnotatedClause] = d.inserts(Processed) ++ d.inserts(Unify) ++ d.inserts(Unprocessed)
    clauses.exists(c => Clause.empty(c.cl))
  }
}

/**
  * Created by mwisnie on 4/24/17.
  */
class RuleAgentPhase
(ruleAgents : Seq[RuleAgent]
, sig : Signature
, initType : DataType[AnnotatedClause])
(blackboard: Blackboard
, scheduler: Scheduler)
extends CompletePhase(blackboard, scheduler, RuleAgentPhase.endOn)
{
  override def name: String = "rule_agent_phase"
  override protected final val agents: Seq[Agent] = ruleAgents
  var negSet : Boolean = false

  override def execute(): Boolean = {
    implicit val s = sig
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

    val delta = Result()

    if (state.negConjecture != null) {
      // Expand conj, Initialize indexes
      // We expand here already since we are interested in all symbols (possibly contained within defined symbols)
      Out.debug("## Preprocess Neg.Conjecture BEGIN")
      Out.trace(s"Neg. conjecture: ${state.negConjecture.pretty(sig)}")
      val simpNegConj = Control.expandDefinitions(state.negConjecture)
      val result = SeqPProc.preprocess(state, simpNegConj).filterNot(cw => Clause.trivial(cw.cl))
      Out.debug(s"# Result:\n\t${
        result.map {
          _.pretty(sig)
        }.mkString("\n\t")
      }")
      Out.trace("## Preprocess Neg.Conjecture END")
      result foreach delta.insert(initType)
      negSet = true
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
      preprocessed foreach delta.insert(initType)

      if (preprocessIt.hasNext) Out.trace("--------------------")
    }
    Out.trace("## Preprocess END\n\n")

    blackboard.getDS(Set(initType)) foreach {d => d.updateResult(delta)}  // Blackboard insert?

    super.execute()

    true
  }
}
