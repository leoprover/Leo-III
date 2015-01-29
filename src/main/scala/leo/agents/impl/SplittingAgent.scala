package leo
package agents
package impl

import leo.datastructures.blackboard.{FormulaStore, FormulaEvent, Event}
import leo.datastructures.context.SplitKind
import leo.modules.proofCalculi.splitting.Split
import leo.datastructures.context.Context
import leo.datastructures.{Role_Plain, Clause}

/**
 *
 * Agent for splitting the context, based on some splitting Rule
 *
 * @author Max Wisniewski
 * @since 29/1/15
 */
class SplittingAgent (s : Split) extends PriorityAgent{

  override def maxMoney: Double = 9000
  override def name: String = s"${s.name}-Agent"


  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case FormulaEvent(f)  => s.split(f.clause) match {
      case Some((cs, k))  => return List(SplitTask(f,cs,k))
      case None           => Nil
    }
    case _  => Nil
  }


  override def run(t: Task): Result = t match {
    case SplitTask(o,cs,k) =>
      val c = o.context
      if (!c.split(k,cs.size)){
        Out.warn(s"[$name]:\n Splitted already splitted Context.")
        return EmptyResult
      }
      // The split was successful
      val children = c.childContext.toList
      val res = cs.zip(children) map {case (clau, con) => o.randomName().newClause(clau).newContext(con).newRole(Role_Plain)}
      new StdResult(res.toSet, Map(), Set())
    case _                 => Out.warn(s"[$name]:\n Got wrong task\n   ${t.pretty}"); EmptyResult
  }
}

private class SplitTask(val o : FormulaStore, val cs : Seq[Clause], val k : SplitKind) extends Task {
  override def name: String = "Split"
  override def writeSet(): Set[FormulaStore] = Set()
  override def readSet(): Set[FormulaStore] = Set(o)
  override def bid(budget: Double): Double = budget / 2

  override def contextWriteSet() : Set[Context] = Set(o.context)    // TODO: Look for on filtering

  override def pretty: String = s"SplitTask:\n On\n  ${o.pretty}\n To\n   ${cs.map(_.pretty).mkString("\n   ")}."
}

/**
 * Compagnion Object for a SplitTask.
 * Generator and Descrutor for this kind of objects.
 */
object SplitTask {
  def apply(o : FormulaStore, cs : Seq[Clause], k : SplitKind) : Task = new SplitTask(o,cs,k)
  def unapply(t : Task) : Option[(FormulaStore,Seq[Clause], SplitKind)] = t match {
    case s : SplitTask  => Some((s.o,s.cs,s.k))
    case _              => None
  }
}