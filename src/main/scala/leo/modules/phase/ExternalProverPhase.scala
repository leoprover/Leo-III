
package leo.modules.phase
import leo.agents.TAgent
import leo.agents.impl.{SZSScriptAgent, SZSScriptMessage}
import leo.datastructures.{Clause, LitTrue, Literal, Role_Conjecture}
import leo.datastructures.blackboard.{Blackboard, Store}
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
  override protected def agents: Seq[TAgent] = _agents

  private var _agents : Seq[TAgent] = Seq()

  override protected def init() : Unit = {
    val cs = Context.leaves(Context())
    Aggregate_SZS.register()
    _agents = Seq(Aggregate_SZS)
    // TODO Switch for more provers
    val ac = cs.map{c => (SZSScriptAgent("leo")(fs => ToTPTP.apply(fs).map(_.output))(x => x),c)}

    //Send initial obligation (Proof true)
    val trueC = Store(Clause(Seq(Literal(LitTrue, true))), Role_Conjecture, Context())
    ac foreach {case (a,c) =>
      a.register()
      _agents = a +: agents
      Blackboard().send(SZSScriptMessage(trueC)(c), a)
    }
  }
}
