package leo
package agents
package impl

import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.modules.calculus._
import leo.datastructures._

/**
 * Class to execute a calculus step from the paramodulation.
 *
 * @author Max Wisniewski
 * @since 12/11/14
 */
class ParamodulationAgent(para : ParamodStep, comp : Unification) extends Agent {


  override def name: String = s"Agent ${para.output}"
  override val interest = Some(List(FormulaType))

  /**
   * Considers only FormulaEvents. If there is a partner for paramodulation in the blackboard
   * return the tasks.
   *
   * Since the order has to be decreasing and no unecessary formulas are added the filter will
   * compute the result. Execute will simply insert it into the Blackboard.
   *
   * @param event - The event that triggered the filter
   * @return A sequence of new tasks, to be added to the internal priority queue.
   */
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) =>
      if(!f.normalized){
        Out.trace(s"[$name]:\n Got non normalized formula\n  ${f.pretty} (${f.status}))")
        return Nil
      }
      // If blocked => Nil
      var q : List[Task] = Nil
      FormulaDataStore.getFormulas(f.context) foreach  {
        bf => para.find(f.clause,bf.clause, comp).fold(()) {
          t : (Term, Literal, Unification#Substitute) =>
            val removeLit = Clause.mkClause(bf.clause.lits.filter{l1 => ! l1.cong(t._2)}, bf.clause.implicitBindings, Derived)
            val nc = para.exec(f.clause, removeLit, t._1, t._2, t._3)
            val nf = Store(nc, Role_Plain, f.context, f.status & bf.status)
            if (!TrivRule.teqt(nc)){
              val task = ParamodTask(f,bf, nf, t._1, t._2, t._3)
              q = task :: q
            }
        }
      }
      q
    case _ : Event => Nil
  }

  private var ex : Int = 0
  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(task: Task): Result = {
    task match {
      case ParamodTask(f1, f2, r, t, l, s) =>
        //synchronized{ex=ex+1;println(s"[$name]: Executed a task $ex times.\n   ${task.pretty}")}
        return Result().insert(FormulaType)(r/*.newOrigin(List(f1,f2), para.output)*/)
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.")
    }
    Result()
  }


  final private case class ParamodTask(f1 : FormulaStore, f2 : FormulaStore, r : FormulaStore, t : Term, l : Literal, s : Unification#Substitute) extends Task {
    override def readSet(): Set[FormulaStore] = Set(f1, f2)
    override def writeSet(): Set[FormulaStore] = Set.empty
    override def bid(budget: Double): Double = budget / 10

    override val toString : String = s"Paramod: ${f1.pretty} with ${f2.pretty}[, ${l.pretty}] over ${t.pretty}=${l.pretty}}]"
    override val pretty : String = s"Paramod: ${f1.pretty} with ${f2.pretty}[, ${l.pretty}] over ${t.pretty}=${l.pretty}}]"
    override val name : String = "Paramodulation"
  }
}

