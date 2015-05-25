package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Role_Plain, Clause}
import leo.datastructures.blackboard._
import leo.modules.calculus.{TrivRule, Clausification}

/**
 * Performs stepwise clausification on the Clauses of the Blackboard.
 *
 * @author Max Wisniewski
 * @since 12/1/15
 */
object ClausificationAgent extends Agent {
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
      if (Clausification.canApply(f.clause)) {
        Out.trace(s"[$name:]\n  Test ${f.clause.pretty}\nClausifier recommends one-step clausification")
        Seq(ClausificationTask(f))
      } else {
        Seq()
      }
    case _ => Seq()
  }

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case ClausificationTask(dc) =>
      val r : Result = Result()

      val newCls = Clausification.apply(dc.clause)
      val of = newCls map {c => TrivRule.triv(TrivRule.teqf(c))}      // Transform C | A | A => C | A and C | [T = F] => C
      val nf = of map {c => Store(c, Role_Plain, dc.context, dc.status, ClauseAnnotation(Clausification, dc))}
      Out.trace(s"$name: Clausify ${dc.name} `${dc.clause.pretty}`\n  Created new clauses:\n   ${newCls.map(_.pretty).mkString("\n   ")}\n  Optimized to\n   ${of.map(_.pretty).mkString("\n   ")}")
      nf.foreach{f => r.insert(FormulaType)(f)}
      return r
    case _ =>
      Out.warn(s"$name: Got a wrong task to execute")
      return Result()
  }

  /**
   * Creates a Clausification task with `dc` the old FormulaStore to be deleted and `nc` the list
   * of new clauses to be inserted into the blackboard.
   * @param dc - Old Formula Store
   * @return A Clausification Task
   */
  final private case class ClausificationTask(dc : FormulaStore) extends Task{
    override def readSet(): Set[FormulaStore] = Set(dc)
    override def writeSet(): Set[FormulaStore] = Set.empty
    override def bid(budget: Double): Double = budget / dc.clause.weight
    override lazy val pretty : String = s"Clausify: ${dc.pretty}"
    override val name : String = "Clausification"
  }

}

