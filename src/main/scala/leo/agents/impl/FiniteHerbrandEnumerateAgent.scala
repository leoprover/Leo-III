package leo.agents.impl

import leo.agents._
import leo.datastructures.blackboard.{FormulaStore, FormulaEvent, Event}
import leo.datastructures.context.Context
import leo.datastructures.term.Term
import leo.datastructures.Type
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
  private val size = domain.values.head.length

  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case FormulaEvent(f) if f.context.parentContext == null => List(FiniteHerbrandEnumerateTask(f))
    case _  => Nil
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
      return new StdResult(Set.empty, Map((f,f1)), Set.empty)
    case _ => EmptyResult
  }
}


private class FiniteHerbrandEnumerateTask(val f : FormulaStore) extends Task {
  override def name: String = "FiniteHerbrandEnumerateTask"
  override def writeSet(): Set[FormulaStore] = Set(f)
  override def readSet(): Set[FormulaStore] = Set.empty
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