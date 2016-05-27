package leo.modules.phase
import leo.agents.impl.SZSScriptAgent
import leo.agents.{DoItYourSelfAgent, DoItYourSelfMessage, ProofProcedure, TAgent}
import leo.datastructures._
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
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
    proofProcedure.map(proc => new DoItYourSelfAgent(proc)) ++: Seq[TAgent](
      //TODO As input or from Configurations
      SZSScriptAgent("leo")
    )
  }

  override protected def init() = {
    import leo.datastructures.{AnnotatedClause, Clause, Literal}
    super.init()
    agents foreach {a =>
      Blackboard().send(DoItYourSelfMessage(Context()), a)
    }
//    val fs = FormulaDataStore.getFormulas.toSet
//    SZSScriptAgent.execute(fs, Context())
  }
}