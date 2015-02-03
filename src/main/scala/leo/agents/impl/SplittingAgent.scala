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


  private var remainingSplits : Int = 1     // In the first test we limit the number of splits

  override protected def toFilter(event: Event): Iterable[Task] = {
    synchronized(if(remainingSplits == 0) return Nil)
    event match {
      case FormulaEvent(f)  => s.split(f.clause) match {
        case Some((cs, k))  => return List(SplitTask(f,cs,k))
        case None           => return Nil
      }
      case _  => Nil
    }
  }


  override def run(t: Task): Result = t match {
    case SplitTask(o,cs,k) if synchronized(remainingSplits > 0) =>
      val c = o.context
      if (!c.split(k,cs.size)){
        Out.warn(s"[$name]:\n Splitted already splitted Context.")
        return EmptyResult
      }
      // The split was successful
      val children = c.childContext.toList
      val res = (cs.zip(children) map {case (cs1, con) => cs1 map { clau => o.randomName().newClause(clau).newContext(con).newRole(Role_Plain)}}).flatten
      synchronized(remainingSplits = remainingSplits - 1)
      Out.info(s"[$name]:\n Splitted the context ${c.contextID} over formula\n   ${o.pretty}\n into\n    ${res.map(_.pretty).mkString("\n    ")}")
      return new StdResult(res.toSet, Map(), Set())
    case SplitTask(o,cs,k)  => EmptyResult
    case _                 => Out.warn(s"[$name]:\n Got wrong task\n   ${t.pretty}"); EmptyResult
  }
}

private class SplitTask(val o : FormulaStore, val cs : Seq[Seq[Clause]], val k : SplitKind) extends Task {
  override def name: String = "Split"
  override def writeSet(): Set[FormulaStore] = Set()
  override def readSet(): Set[FormulaStore] = Set(o)
  private val factor : Double = {val f = cs.map(_.foldLeft(0){(a,x) => a+x.weight}).max / o.clause.weight; if(f > 1 || f<0) 0 else 1-f}
  override def bid(budget: Double): Double = budget * factor                                                  // Der Split mit der kleinsten größten Klausel wird bevorhzugt

  override def contextWriteSet() : Set[Context] = Set(o.context)    // TODO: Look for on filtering

  override def pretty: String = s"SplitTask:\n On\n  ${o.pretty}\n To\n   ${cs.map(_.map(_.pretty).mkString(", ")).mkString("\n   ")}."
}

/**
 * Compagnion Object for a SplitTask.
 * Generator and Descrutor for this kind of objects.
 */
object SplitTask {
  def apply(o : FormulaStore, cs : Seq[Seq[Clause]], k : SplitKind) : Task = new SplitTask(o,cs,k)
  def unapply(t : Task) : Option[(FormulaStore,Seq[Seq[Clause]], SplitKind)] = t match {
    case s : SplitTask  => Some((s.o,s.cs,s.k))
    case _              => None
  }
}