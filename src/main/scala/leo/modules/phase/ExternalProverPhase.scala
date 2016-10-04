
package leo.modules.phase
import leo.agents.Agent
import leo.agents.impl.{SZSScriptAgent, SZSScriptMessage}
import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures._
import leo.datastructures.blackboard.Blackboard
import leo.modules.output.ToTPTP


object ExternalProverPhase extends CompletePhase {
  /**
    * Returns the name of the phase.
    *
    * @return
    */
  override def name: String = "External Prover Phase"

  /**
    * A list of all agents to be started.
    *
    * @return
    */
  override protected def agents: Seq[Agent] = Seq(scriptAgent)
  private val scriptAgent : Agent = {
    // Read the external provers from file

    SZSScriptAgent("leo2", "leo", fs => ToTPTP(fs).map(_.apply), x => x)
  }

  override protected def init() : Unit = {
    super.init()

    //Send initial obligation (Proof true)
    val trueC = AnnotatedClause(Clause(Seq(Literal(LitTrue, true))), Role_Conjecture, NoAnnotation, ClauseAnnotation.PropNoProp)
    Blackboard().send(SZSScriptMessage(trueC), scriptAgent)
  }


}
