package leo.modules.agent.multisearch

import leo.Configuration
import leo.agents.{Agent, CompletedState, DoItYourSelfAgent, OpenState}
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{Blackboard, Delta}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.GeneralState
import leo.modules.agent.rules.TypedSet
import leo.modules.output.SZS_Theorem
import leo.modules.phase.CompletePhase
import leo.modules.prover.{RunStrategy, effectiveInput, typeCheck}

object SchedulingPhase {
  def endBy(d : Delta) : Boolean = {
    val it = d.inserts(CompletedState).iterator
    while(it.hasNext){
      if(it.next().szsStatus == SZS_Theorem) return true  // Other problems???
    }
    false
  }
}

/**
  * Created by mwisnie on 6/7/17.
  */
class SchedulingPhase(tactics : Schedule,
                      parsedProblem: Seq[AnnotatedFormula],
                      implicit val state : GeneralState[AnnotatedClause])
                     (scheduler: Scheduler,
                      blackboard: Blackboard)
  extends CompletePhase(blackboard, scheduler, SchedulingPhase.endBy, Seq(CompletedState)) {
  override def name: String = "SchedulingPhase"

  var resultState = state

  override protected val agents: Seq[Agent] = Seq()
  implicit val sig : Signature = state.signature
  var negSet : Boolean = false

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

    // TODO Part of tactic, give potentially the parsed input to the procedure
//    val input2 = Input.parseProblemFile(Configuration.PROBLEMFILE)
    val remainingInput = effectiveInput(parsedProblem, state)
    // Typechecking: Throws and exception if not well-typed
    typeCheck(remainingInput, state)

    var negConj : AnnotatedClause = null

    state.addInitial(remainingInput.toSet)

    // Init Blackboard
    val completedStates = new TypedSet(CompletedState)
    val newStates = new TypedSet(OpenState)

    blackboard.addDS(completedStates)
    blackboard.addDS(newStates)

    val seqLoopAgent = new DoItYourSelfAgent(SeqLoopLift)
    blackboard.registerAgent(seqLoopAgent)
    val schedulingAgent = new SchedulingAgent(state, tactics)
    blackboard.registerAgent(schedulingAgent)

    super.execute()

    // Get result
    val it = completedStates.get(CompletedState).iterator
    while(it.nonEmpty){
      val s = it.next()
      if(s.szsStatus == SZS_Theorem) {
        resultState = s
      } else if(resultState.szsStatus != SZS_Theorem) {
        resultState = s
      }
    }
    true
  }

}
