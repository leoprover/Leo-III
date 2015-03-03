package leo.agents.impl

import leo.agents._
import leo.datastructures.blackboard.{FormulaStore, FormulaEvent, Event}
import leo.datastructures.context.Context
import leo.datastructures.term.Term
import leo.datastructures.term.Term._
import leo.datastructures._
import leo.modules.countersat.FiniteHerbrandEnumeration

/**
 *
 * Replaces each quantifier (over primitive Types) by
 * an explicitly inserting AND and OR over the existing Terms.
 *
 * The replacement works only on formulas in the root context
 * and reinsertion in the given context by the named replacement.
 *
 * @author Max Wisniewski
 * @since 3/2/15
 */
class FiniteHerbrandEnumerateAgent(c : Context, domain : Map[Type, Seq[Term]]) extends FifoAgent {

  private val replace : Map[Type, (Term, Term)] = FiniteHerbrandEnumeration.generateReplace(domain)
  private val size = if(domain.isEmpty) 0 else domain.values.head.length
  private val usedDomains : Set[Type] = domain.keySet

  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case FormulaEvent(f) if f.context.parentContext == null =>
      if(f.clause.lits.exists{l => containsDomain(l.term)})
        List(FiniteHerbrandEnumerateTask(f))
      else
        Nil
    case _  => Nil
  }

  def containsDomain(t : Term) : Boolean = {
    // TODO put some support in Term
    // TODO optimize for quant.
    t match {
      case Forall(ty :::> t1) => domain.contains(ty) || containsDomain(t1)
      case Exists(ty :::> t1) => domain.contains(ty) || containsDomain(t1)

      case s@Symbol(_)       => false
      case s@Bound(_,_)      => false
      case s @@@ t            => containsDomain(s) || containsDomain(t)
      case f ∙ args           => containsDomain(f) || args.exists(_.fold({t => containsDomain(t)},(t => false)))
      case s @@@@ ty          => containsDomain(s)
      case ty :::> s        => containsDomain(s)
      case TypeLambda(t)    => containsDomain(t)
    }
  }

  /**
   *
   * @return the name of the agent
   */
  override def name: String = "FiniteHerbrandEnumerationAgent"

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case FiniteHerbrandEnumerateTask(f) =>
      val nc = FiniteHerbrandEnumeration.replaceQuantOpt(f.clause, replace)
      val f1 = f.newClause(nc).newContext(c).newName(f.name + "_"+size)
      return new StdResult(Set(f1), Map.empty, Set.empty)
    case _ => EmptyResult
  }
}


private class FiniteHerbrandEnumerateTask(val f : FormulaStore) extends Task {
  override def name: String = "FiniteHerbrandEnumerateTask"
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def readSet(): Set[FormulaStore] = Set(f)
  override def bid(budget: Double): Double = budget/20
  override def pretty: String = s"FiniteHerbrandEnumerateTask(${f.pretty}"
}

object FiniteHerbrandEnumerateTask {
  def apply(f : FormulaStore) : Task = new FiniteHerbrandEnumerateTask(f)
  def unapply(e : Task) : Option[FormulaStore] = e match {
    case fe : FiniteHerbrandEnumerateTask => Some(fe.f)
    case _      => None
  }
}