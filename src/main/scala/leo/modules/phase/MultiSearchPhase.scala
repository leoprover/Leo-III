package leo.modules.phase
import leo.agents.{DoItYourSelfAgent, DoItYourSelfMessage, ProofProcedure, TAgent}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.context.Context

/**
  * Created by mwisnie on 5/24/16.
  */
class MultiSearchPhase(proofProcedure: ProofProcedure*) extends CompletePhase {
  /**
    * Returns the name of the phase.
    *
    * @return
    */
  override def name: String = "multi-search"

  /**
    * A list of all agents to be started.
    *
    * @return
    */
  override protected val agents: Seq[TAgent] = {
    proofProcedure.map(proc => new DoItYourSelfAgent(proc))
  }

  override protected def init() = {
    super.init()
    agents foreach {a =>
      Blackboard().send(DoItYourSelfMessage(Context()), a)
    }
  }
}