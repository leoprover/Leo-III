package leo
package agents
package impl

import leo.datastructures._
import leo.datastructures.blackboard.impl
import leo.datastructures.blackboard.impl._
import leo.datastructures.blackboard._
import leo.modules.calculus._

/**
 * Created by lex on 18.05.15.
 */
class NewParamodAgent(rule: ParamodRule) extends Agent {

  override def name: String = s"Agent NewParamod"
  override val interest = Some(List(FormulaType, SelectionTimeType))


  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(TimeData(f : FormulaStore, t : TimeStamp), SelectionTimeType) =>
//    case DataEvent(f : FormulaStore, FormulaType) =>
      if(!f.normalized){
        Out.trace(s"[$name]:\n Got non normalized formula\n  ${f.pretty} (${f.status}))")
        return Nil
      }
      var tasks: Seq[Task] = Seq()
      val others = (SelectionTimeStore.noSelect(f.context) ++ SelectionTimeStore.after(t, f.context).filterNot(_ == f)).iterator
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
    case DataEvent(f: FormulaStore, FormulaType) => {
      // from unification
      if(!f.normalized){
        Out.trace(s"[$name]:\n Got non normalized formula\n  ${f.pretty} (${f.status}))")
        return Nil
      }
      if (SelectionTimeStore.get(f).isEmpty) {
        var tasks: Seq[Task] = Seq()
        val others = SelectionTimeStore.after(f.created, f.context).iterator
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
      } else {
        Seq()
      }
    }
    case _ : Event => Nil
  }

  override def run(task: Task): Result = {
    task match {
      case NewParamodTask(f1, f2, hint) =>
        val r = Result()
        val newHint: rule.HintType = hint.fold(in => (Set(in), Set()):rule.HintType, in => (Set(), Set(in)):rule.HintType)
        rule.apply(f1.clause, f2.clause, (newHint)).foreach( cl => {
          val erg = Store(cl, Role_Plain, f1.context, f1.status, ClauseAnnotation(rule, Set(f1, f2)))
          trace(rule.name + "paramod from\n" + f1.pretty +"\n" + f2.pretty + "\n" + "insert: "+erg.pretty)
          r.insert(UnificationTaskType)(erg)
        }
        )
        r
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.")
        Result()
    }
  }


  final private case class NewParamodTask(f1 : FormulaStore, f2: FormulaStore, hint: Either[(Literal, (Term, Term), Term),(Literal, (Term, Term), Term)]) extends Task {
    override def readSet(): Set[FormulaStore] = Set(f1, f2)
    override def writeSet(): Set[FormulaStore] = Set.empty
    override def bid(budget: Double): Double = budget / Math.max(f1.clause.weight, f2.clause.weight) /*hint.left.getOrElse(hint.right.get)._1.id*/
    override lazy val pretty : String = s"New Paramod: ${f1.pretty} with ${f2.pretty}"
    override val name : String = "New Paramodulation"
  }

}
