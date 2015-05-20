package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Clause, Role_NegConjecture, Role_Conjecture}
import leo.datastructures.blackboard._
import leo.modules.output.SZS_CounterSatisfiable
import leo.modules.proofCalculi.{UnaryCalculusRule, CalculusRule}


/**
 * This agents runs on a Formula if it is a conjecture
 * and inserts the negated conjecture.
 *
 */
class ConjectureAgent extends Agent {

  override val name = "ConjectureAgent"

  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param e - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  override def toFilter(e: Event): Iterable[Task] = e match {
    case DataEvent( event : FormulaStore, FormulaType) => if (NegConjRule.canApply(event)) List(new SingleFormTask(event)) else Nil
    case _ => Nil
  }

  /**
   * Negates the conjecture and renames the role
   */
  override def run(t: Task) : Result = {
    t match {
      case SingleFormTask(fS) => Result().update(FormulaType)(fS)(NegConjRule(fS))
      case _ => Out.warn(s"[$name]: Got a wrong task to execute."); Result()
    }
  }

  private object NegConjRule extends CalculusRule {
    val name = "neg_conjecture"
    override val inferenceStatus = Some(SZS_CounterSatisfiable)
    def canApply(fs: FormulaStore) = fs.role == Role_Conjecture
    def apply(fs: FormulaStore) = Store(fs.name, fs.clause.mapLit(l => l.flipPolarity), Role_NegConjecture, fs.context, fs.status & ~7, ClauseAnnotation(this, fs)) // TODO: This is not generally not valid, fix me
  }
}


private case class SingleFormTask(f : FormulaStore) extends Task {
  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)

  override def bid(budget : Double) : Double = 1

  override val toString : String = "SingleFormulaTask on "+f.toString
  override val pretty : String = "SingleFormulaTask on "+f.toString
  override val name : String = "Read-/Write Transformation"

  override def equals(other : Any) = other match {
    case SingleFormTask(oF) => oF == f
    case _                  => false
  }
}