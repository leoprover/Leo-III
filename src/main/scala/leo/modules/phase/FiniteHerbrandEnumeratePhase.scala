package leo.modules.phase

import leo.agents.impl.FiniteHerbrandEnumerateAgent
import leo.agents.{FifoController, AgentController}
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.{Derived, Literal, Clause, Type}
import leo.datastructures.context.{BetaSplit, Context}
import leo.datastructures.impl.Signature

object FiniteHerbrandEnumeratePhase extends CompletePhase {
  override val name = "FiniteHerbrandEnumeratePhase"

  val size : Int = 3
  override lazy val description = "Agents used:\n    FiniteHerbrandEnumerationAgent"

  /**
   * A list of all agents to be started.
   * @return
   */
  protected var agents: Seq[AgentController] = List(new FifoController(new FiniteHerbrandEnumerateAgent(Context(), Map.empty))) // A bit of schmu, but I do not want to list the agents here

  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  override def execute(): Boolean = {
    if(!Context().split(BetaSplit, size)) {
      // Set context and reason???
      return false
    }

    agents = Nil

    val s1 : Set[Signature#Key] = Signature.get.baseTypes - 0 - 1 - 3 - 4 - 5// Without kind, numbers and boolean
    val s : Set[Type]= s1.map {k => Type.mkType(k)}

    var it : Int = 0
    var agents1 : Seq[FiniteHerbrandEnumerateAgent] = Nil

    val cs : Seq[Context] = Context().childContext.toList
    // Each context, assign the maximal number of elements per domain
    // Then generate teh new clauses and insert them into the blackboard.
    // If it is done build the agents from it.
    (1 to size).zip(cs).foreach { case (i,c1) =>
      // Generate and insert new constants
      val cons : Map[Type, Seq[Clause]] = s.map{ty => (ty, (1 to i).map{_ => newConstant(ty)}.toList)}.toMap

      //TODO Add some constraints?
      //cons.values.map(_.foreach{c => Blackboard().addFormula(s"domainConstrain_${c1.contextID}_${val s = it; it+=1; s}",c,Role_Axiom, c1)})

      // Generate an agent for this setting of domains
      val agent = new FiniteHerbrandEnumerateAgent(c1, cons.mapValues(_.map(_.lits.head.term)))
      agents1 = agent +: agents1
      agents = new FifoController(agent) +: agents
    }

    init()

    initWait()

    if(!waitTillEnd()) return false
    // Remove all formulas containing one of the domains. (Hacky. Move the Test Function to the module package.
    val  a : FiniteHerbrandEnumerateAgent = agents1.head

    FormulaDataStore.rmAll(Context()){f => f.clause.lits.exists{l => a.containsDomain(l.term)}}

    end()

    return true
  }

  private def newConstant(ty : Type) : Clause = {
    val s = Signature.get
    return Clause.mkClause(List(Literal(s.freshSkolemVar(ty), true)), Derived)
  }
}
