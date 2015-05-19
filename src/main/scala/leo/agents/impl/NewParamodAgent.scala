package leo
package agents
package impl

import leo.datastructures.{Derived, Clause, Literal, Term}
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.blackboard._
import leo.modules.proofCalculi.{ParamodRule, TrivRule, NewParamod}

/**
 * Created by lex on 18.05.15.
 */
class NewParamodAgent(rule: ParamodRule) extends Agent {

  override def name: String = s"Agent NewParamod"
  override val interest = Some(List(FormulaType))


  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) =>
      if(!f.normalized){
        Out.trace(s"[$name]:\n Got non normalized formula\n  ${f.pretty} (${f.status}))")
        return Nil
      }
      var tasks: Seq[Task] = Seq()
      val others = FormulaDataStore.getFormulas.iterator
      while( others.hasNext) {
        val other = others.next()
        val (canApply, hint) = rule.canApply(f.clause, other.clause)
        if (canApply) {
          val (leftIt,rightIt) = (hint._1.iterator, hint._2.iterator)
          while(leftIt.hasNext) {
            val h = leftIt.next()
            tasks = tasks :+ NewParamodTask(f, other, Left(h))
          }
          while(rightIt.hasNext) {
            val h = rightIt.next()
            tasks = tasks :+ NewParamodTask(f, other, Right(h))
          }
        }
      }
      tasks
    case _ : Event => Nil
  }

  override def run(task: Task): Result = {
    task match {
      case NewParamodTask(f1, f2, hint) =>
        val r = Result()
        val newHint: rule.HintType = hint.fold(in => (Set(in), Set()):rule.HintType, in => (Set(), Set(in)):rule.HintType)
        rule.apply(f1.clause, f2.clause, (newHint)).foreach( cl => {
          Out.trace(rule.name + "new paramod agent insert clause: "+cl.pretty)
          r.insert(FormulaType)(Store(cl, f1.status, f1.context).newOrigin(List(f1, f2), rule.name))
        }
        )
        r
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.")
        Result()
    }
  }


  private case class NewParamodTask(f1 : FormulaStore, f2: FormulaStore, hint: Either[(Literal, (Term, Term), Term),(Literal, (Term, Term), Term)]) extends Task {
    override def readSet(): Set[FormulaStore] = Set(f1, f2)
    override def writeSet(): Set[FormulaStore] = Set.empty
    override def bid(budget: Double): Double = budget / Math.max(f1.clause.weight, f2.clause.weight) /*hint.left.getOrElse(hint.right.get)._1.id*/

    override val toString : String = s"New Paramod: ${f1.pretty} with ${f2.pretty}"
    override val pretty : String = s"New Paramod: ${f1.pretty} with ${f2.pretty}"
    override val name : String = "New Paramodulation"
  }

}
