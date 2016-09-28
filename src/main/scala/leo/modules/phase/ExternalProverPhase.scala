
package leo.modules.phase
import leo.agents.TAgent
import leo.agents.impl.{SZSScriptAgent, SZSScriptMessage}
import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures._
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.context._
import leo.modules.agent.split_search.Aggregate_SZS
import leo.modules.output.ToTPTP
import leo.modules.parsers.TPTP


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
  override protected def agents: Seq[TAgent] = Seq(Aggregate_SZS, scriptAgent)
  private val scriptAgent : TAgent = {
    // Read the external provers from file

    SZSScriptAgent("leo2", "leo", fs => ToTPTP(fs).map(_.apply), x => x)
  }

  override protected def init() : Unit = {
    super.init()
    val cs = Context.leaves(Context())

    //Send initial obligation (Proof true)
    val trueC = AnnotatedClause(Clause(Seq(Literal(LitTrue, true))), Role_Conjecture, NoAnnotation, ClauseAnnotation.PropNoProp)
    cs foreach {c =>
      Blackboard().send(SZSScriptMessage(trueC, c), scriptAgent)
    }
  }


}
