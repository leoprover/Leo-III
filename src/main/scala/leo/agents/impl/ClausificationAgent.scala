package leo
package agents
package impl

import leo.datastructures.{Role_Plain, Clause}
import leo.datastructures.blackboard._
import leo.modules.proofCalculi.{TrivRule, Clausification}

object ClausificationAgent {
  def apply() : Unit = new PriorityController(new ClausificationAgent()).register()
}

/**
 * Performs stepwise clausification on the Clauses of the Blackboard.
 *
 * @author Max Wisniewski
 * @since 12/1/15
 */
class ClausificationAgent extends Agent {
  override def name: String = "ClausificationAgent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  /**
   * Internal method called from the filter method. Specific to the agent.
   *
   * @param event - The event that triggered the filter
   * @return A sequence of new tasks, to be added to the internal priority queue.
   */
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) =>
      val nc : Seq[Clause] = Clausification.clausify(f.clause)
      val fc = nc.filter(!TrivRule.teqt(_))        // Optimized clauses (no [ T = T] or [ T = F]) containing clauses.
      if(fc.isEmpty) {
        return Nil
      }
      else {
        Out.trace(s"[$name:]\n  Test ${f.clause.pretty}\n  Clausifier recommends \n    ${nc.map(_.pretty).mkString("\n    ")}")
        return List(ClausificationTask(f, fc))
      }
    case _ => return Nil
  }

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case ClausificationTask(dc, nc) =>
      val r : Result = Result()
      val of = nc map {c => TrivRule.triv(TrivRule.teqf(c))}      // Transform C | A | A => C | A and C | [T = F] => C
      val nf = of map {c => dc.randomName().newClause(c).newRole(Role_Plain).newOrigin(List(dc), "clausification")}
      Out.trace(s"$name: Clausify ${dc.name} `${dc.clause.pretty}`\n  Created new clauses:\n   ${nc.map(_.pretty).mkString("\n   ")}\n  Optimized to\n   ${of.map(_.pretty).mkString("\n   ")}")
      nf.foreach{f => r.insert(FormulaType)(f)}
      return r
    case _ =>
      Out.warn(s"$name: Got a wrong task to execute")
      return Result()
  }
}

/**
 * Creates a Clausification task with `dc` the old FormulaStore to be deleted and `nc` the list
 * of new clauses to be inserted into the blackboard.
 * @param dc - Old Formula Store
 * @param nc - List of new clauses
 * @return A Clausification Task
 */
private case class ClausificationTask(dc : FormulaStore, nc : Seq[Clause]) extends Task{
  override def readSet(): Set[FormulaStore] = Set(dc)
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget / dc.clause.weight

  override val toString : String = s"Clausify: ${dc.pretty} => [${nc.map(_.pretty).mkString(", ")}}]"

  override val pretty : String = s"Clausify: ${dc.pretty} => [${nc.map(_.pretty).mkString(", ")}}]"

  override val name : String = "Clausification"
}
