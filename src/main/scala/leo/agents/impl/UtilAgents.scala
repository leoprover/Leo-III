package leo.agents
package impl

import leo.datastructures.{Role_NegConjecture, Role_Conjecture, LitFalse, Not}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import leo.modules.output.logger.Out


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
    case DataEvent( event : FormulaStore, FormulaType) => if (event.role == Role_Conjecture) List(new SingleFormTask(event)) else Nil
    case _ => Nil
  }

  /**
   * Negates the conjecture and renames the role
   */
  override def run(t: Task) : Result = {
    t match {
      case t1: SingleFormTask =>
        val fS = t1.getFormula()
        val form = fS.clause
        val status = fS.status
        val rS = fS.newClause(form.mapLit(l => l.flipPolarity)).newRole(Role_NegConjecture).newStatus(status & ~7) // TODO: This is not generally not valid, fix me


        return Result().update(FormulaType)(fS)(rS.newOrigin(List(fS), "negate conjecture"))
      case _ => throw new IllegalArgumentException("Executing wrong task.")
    }
  }
}


class SingleFormTask(f : FormulaStore) extends Task {
  def getFormula() : FormulaStore = f
  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)

  override def bid(budget : Double) : Double = 1

  override val toString : String = "SingleFormulaTask on "+f.toString
  override val pretty : String = "SingleFormulaTask on "+f.toString
  override val name : String = "Read-/Write Transformation"

  override def equals(other : Any) = other match {
    case o : SingleFormTask => o.getFormula() == f
    case _                  => false
  }
}