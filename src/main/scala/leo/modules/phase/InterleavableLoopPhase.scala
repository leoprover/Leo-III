package leo.modules.phase
import leo.agents.{Agent, InterferingLoopAgent}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.{AnnotatedClause, Role_Conjecture, Role_NegConjecture, Signature}
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.modules.interleavingproc.{BlackboardState, StateView, UnprocessedClause}
import leo.modules.seqpproc.SeqPProc
import leo.modules.control.Control

/**
  * Created by mwisnie on 9/28/16.
  */
class InterleavableLoopPhase (interleavingLoop : InterferingLoopAgent[StateView[AnnotatedClause]], state : BlackboardState[AnnotatedClause], sig : Signature, interleavingAgents : Agent*) extends CompletePhase {
  /**
    * Returns the name of the phase.
    *
    * @return
    */
  override def name: String = "InterleavableLoopPhase"

  /**
    * A list of all agents to be started.
    *
    * @return
    */
  override protected val agents: Seq[Agent] = interleavingLoop +: interleavingAgents

  /**
    * Executes all defined agents and waits till no work is left.
    */
  override def execute(): Boolean = {

    // TODO Move the preprocessing and insertion into the blackboard state into another phase
    val forms = FormulaDataStore.getFormulas.iterator
    var startTheIndex : Seq[AnnotatedClause] = Seq()  // Learn features for fVIndex from initial problem
    while(forms.hasNext){

      val nForm = forms.next().asInstanceOf[AnnotatedClause]
      startTheIndex = nForm +: startTheIndex

      if(nForm.role == Role_Conjecture || nForm.role == Role_NegConjecture) state.conjecture = Some(nForm)  // Set there exists a conjecture
      val it = SeqPProc.preprocess(state.state, nForm).iterator
      while(it.nonEmpty){
        val processForm = it.next()
        Blackboard().addData(UnprocessedClause)(processForm)  // Write a method to bundle the writing
      }
    }
    Control.fvIndexInit(startTheIndex)(sig)

    super.execute()
  }
}
