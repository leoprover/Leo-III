package leo
package agents.impl

import leo.agents.{Task, Agent}
import leo.datastructures.blackboard
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{SZSDataStore, SZSStore}
import leo.datastructures.context.{BetaSplit, AlphaSplit, SplitKind, Context}
import leo.modules.output._

/**
 *
 * Reacts to Context Change events, adding of empty clauses et cetera.
 *
 * Main purpos is to pass the information of closed contexts to the top.
 *
 * @author Max Wisniewski
 * @since 29/1/15
 */
abstract class AbstractContextControlAgent extends Agent {

  override val interest : Option[Seq[DataType]] = Some(List(StatusType, FormulaType))

  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) if f.clause.isEmpty => List(SetContextTask(f.context, SZS_Theorem))
    case DataEvent(SZSStore(s,c),StatusType) if c.parentContext != null =>
      val p = c.parentContext
      bubbleStatus(p,s).map{ns => List(SetContextTask(p, ns))}.getOrElse(Nil)
    case _                => List()
  }

  override def run(t: Task): blackboard.Result = t match {
    case SetContextTask(con,status) =>
      Out.trace(s"[$name]: Set context ${con.contextID} to ${status.output}.")
      con.close()
      val r = Result()
      r.insert(StatusType)(SZSStore(status, con)).setPriority(1)
    case _  => Out.warn(s"[$name]:\n Got wrong task\n   ${t.pretty}"); Result()
  }

  /**
   * Checks for a context `p` if the status `ns` was set in one of the children,
   * if a status can be inferred for `p`.
   *
   * @param p the context to infere a status
   * @param ns a new status in the children
   * @return Some(s) if p can be set to s, else None
   */
  protected def bubbleStatus(p : Context, ns : StatusSZS) : Option[StatusSZS]
}

/**
 * Implements behaviour in a standard setting.
 * Proof by deriving the empty clause
 */
object ContextControlAgent extends AbstractContextControlAgent {
  override protected def bubbleStatus(p: Context, ns: StatusSZS): Option[StatusSZS] = {
    if(p.splitKind == AlphaSplit) {
      if (ns.isInstanceOf[NoSuccessSZS]) {
        return Some(ns)
      } else if (ns != SZS_Theorem) {
        return Some(ns)
      } else if (p.childContext.forall(_.isClosed)){
        // If all are closed and we reached this case, then all have to be SZS_Theorem
        return Some(SZS_Theorem)
      }
    } else if (p.splitKind == BetaSplit) {
      if(ns == SZS_Theorem){
        return Some(ns)
      } else if (p.childContext.forall(_.isClosed)){
        // TODO infere best option
        // At this point we now, that non of the disjunctiv split cases returned Theorem
        return Some(ns)
      }
    }
    None
  }

  override def name: String = "ContextControlAgent"

}

/**
 *
 * Reacts to Context Change events, adding of empty clauses et cetera.
 *
 * Main purpos is to pass the information of closed contexts to the top.
 *
 * Works on proofs of counter satisfiability and its Beta Split.
 *
 * @author Max Wisniewski
 * @since 23/2/15
 */
object CounterContextControlAgent extends AbstractContextControlAgent {
  override def name: String = "CounterContextControlAgent"

  override protected def bubbleStatus(p: Context, ns: StatusSZS): Option[StatusSZS] = {
    if(p.splitKind == AlphaSplit) {
      if (ns.isInstanceOf[NoSuccessSZS]) {
        return Some(ns)
      } else if (ns != SZS_CounterSatisfiable) {
        return Some(ns)
      } else if (p.childContext.forall(_.isClosed)){
        // If all are closed and we reached this case, then all have to be SZS_Theorem
        return Some(SZS_CounterSatisfiable)
      }
    } else if (p.splitKind == BetaSplit) {
      if(ns == SZS_CounterSatisfiable){
        return Some(ns)
      } else if (p.childContext.forall(_.isClosed)){
        // TODO infere best option
        // At this point we now, that non of the disjunctiv split cases returned counter sat
        return Some(ns)
      }
    }
    None
  }
}

private class SetContextTask(val c : Context, val s : StatusSZS) extends Task {
  override def name: String = "SetContextTask"
  override def pretty: String = "SetContextTask"
  override def writeSet(): Set[FormulaStore] = Set()
  override def readSet(): Set[FormulaStore] = Set()
  override def contextWriteSet() : Set[Context] = Set(c)
  override def bid(budget: Double): Double = budget / 2
}



object SetContextTask {
  def apply(c : Context, s : StatusSZS) : Task = new SetContextTask(c,s)
  def unapply(t : Task) : Option[(Context,StatusSZS)] = t match {
    case sc : SetContextTask => Some((sc.c,sc.s))
    case _                   => None
  }
}

