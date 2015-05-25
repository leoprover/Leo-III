package leo.modules.phase

import leo._
import leo.agents.impl.FiniteHerbrandEnumerateAgent
import leo.agents.{FifoController, AgentController}
import leo.datastructures.{Term, Type}
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.modules.calculus.enumeration.SimpleEnum

object SimpleEnumerationPhase extends CompletePhase {
  override val name = "SimpleEnumerationPhase"
  override lazy val description = "Agents used:\n    FiniteHerbrandEnumerationAgent"

  protected var agents: Seq[AgentController] = List(new FifoController(new FiniteHerbrandEnumerateAgent(Context(), Map.empty)))

  override def execute(): Boolean = {
    val s1 : Set[Type] = (Signature.get.baseTypes - 0 - 1 - 3 - 4 - 5).map(Type.mkType(_))
    val enumse : Map[Type, Seq[Term]] = s1.map{ty => (ty, SimpleEnum.enum(ty).toSeq)}.toMap
    val fhb = new FiniteHerbrandEnumerateAgent(Context(), enumse)
    Out.finest(enumse.toString())
    agents = List(new FifoController(fhb))

    init()
    initWait()

    if(!waitTillEnd()) return false

    FormulaDataStore.rmAll(Context()){f => f.clause.lits.exists{l => fhb.containsDomain(l.term)}}

    end()
    return true
  }
}
