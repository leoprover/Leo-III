package leo
package agents
package impl

import leo.datastructures.blackboard._
import leo.datastructures.term.Term
import leo.modules.proofCalculi._
import leo.datastructures.{Derived, Clause, Literal}


object ParamodulationAgent {
  def apply() : Unit = {
    (new ParamodulationAgent(PropParamodulation, IdComparison)).register()
    (new ParamodulationAgent(Paramodulation, IdComparison)).register()
  }
}

/**
 * Class to execute a calculus step from the paramodulation.
 *
 * @author Max Wisniewski
 * @since 12/11/14
 */
class ParamodulationAgent(para : ParamodStep, comp : TermComparison) extends PriorityAgent{

  /**
   * Considers only FormulaEvents. If there is a partner for paramodulation in the blackboard
   * return the tasks.
   *
   * @param event - The event that triggered the filter
   * @return A sequence of new tasks, to be added to the internal priority queue.
   */
  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case FormulaEvent(f) =>
      if(!f.normalized){
        Out.trace(s"[$name]:\n Got non normalized formula\n  ${f.pretty} (${f.status}))")
        return Nil
      }
      // If blocked => Nil
      var q : List[Task] = Nil
      Blackboard().getFormulas(f.context) foreach  {
        bf => para.find(f.clause,bf.clause, comp).fold(()) {
          t : (Term, Literal, TermComparison#Substitute) =>
            //Out.output(s"[$name]:\n Tested\n   $f\n Got\n  Partner: ${bf}\n  Literal: ${t._2.pretty}\n  Term: ${t._1.pretty}"
            val task = ParamodTask(f,bf, t._1, t._2, t._3)
            //Out.output(s"[$name]:\n New Task\n  $task")
            q = task :: q
        }
      }
      q
    case _ : Event => Nil
  }

  /**
   * Each task can define a maximum amount of money, they
   * want to posses.
   *
   * A process has to be careful with this barrier, for he
   * may never be doing anything if he has to low money.
   *
   * @return maxMoney
   */
  override def maxMoney: Double = 6800

  /**
   *
   * @return the name of the agent
   */
  override def name: String = s"Agent ${para.output}"

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(task: Task): Result = {
    task match {
      case ParamodTask(f1, f2, t, l, s) =>
        val removeLit = Clause.mkClause(f2.clause.lits.filter{l1 => ! l1.cong(l)}, f2.clause.implicitBindings, Derived)
        val nc = para.exec(f1.clause, removeLit, t, l, s) // The paramodulation result
        //Out.output(s"[$name]:\n Claculated\n   ${nc.pretty}\n from\n   $task")
          // Only add, if the it is not trivially given.TODO: Move te filter to not lock the clauses
          val nf = Store(nc, f1.status & f2.status, f1.context)
          Out.trace(s"[$name]:\n Paramdoulation step\n   (${f1.clause.pretty},\n   ${f2.clause.pretty}[,${l.pretty}]})\n =>\n   ${nc.pretty}")
          return new StdResult(Set(nf), Map.empty, Set.empty)
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.")
    }
    EmptyResult
  }
}



private class ParamodTask(val f1 : FormulaStore, val f2 : FormulaStore, val t : Term, val l : Literal, val s : TermComparison#Substitute) extends Task {
  override def readSet(): Set[FormulaStore] = Set(f1, f2)
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget / 10

  override val toString : String = s"Paramod: ${f1.pretty} with ${f2.pretty}[, ${l.pretty}] over ${t.pretty}=${l.pretty}}]"
  override val pretty : String = s"Paramod: ${f1.pretty} with ${f2.pretty}[, ${l.pretty}] over ${t.pretty}=${l.pretty}}]"
  override val name : String = "Paramodulation"
}

object ParamodTask {
  def apply(f1 : FormulaStore, f2 : FormulaStore, t : Term, l : Literal, s : TermComparison#Substitute) : Task = new ParamodTask(f1, f2, t, l ,s)
  def unapply (t : Task) : Option[(FormulaStore, FormulaStore, Term, Literal, TermComparison#Substitute)] = t match {
    case t1 : ParamodTask => Some((t1.f1, t1.f2, t1.t, t1.l, t1.s))
    case _ : Task => None
  }
}