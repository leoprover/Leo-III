package leo
package agents.impl

import leo.agents.{EmptyResult, Result, Task, FifoAgent}
import leo.datastructures.blackboard._
import leo.datastructures.context.{BetaSplit, AlphaSplit, SplitKind, Context}
import leo.modules.output.{SZS_GaveUp, SZS_CounterSatisfiable, SZS_Theorem, StatusSZS}

/**
 *
 * Reacts to Context Change events, adding of empty clauses et cetera.
 *
 * Main purpos is to pass the information of closed contexts to the top.
 *
 * @author Max Wisniewski
 * @since 29/1/15
 */
object ContextControlAgent extends FifoAgent {
  override val maxMoney : Double = 50000
  override def name: String = "ContextControlAgent"

  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case FormulaEvent(f) if f.clause.isEmpty => List(SetContextTask(f.context, SZS_Theorem))
    case StatusEvent(c,s) if c.parentContext != null =>
      val p = c.parentContext
      if(p.splitKind == BetaSplit) return List(SetContextTask(p,Blackboard().getStatus(c).get))
      if(p.splitKind == AlphaSplit && c.childContext.forall(_.isClosed)) return List(SetContextTask(p,Blackboard().getStatus(c).get))     // TODO: Working for non theorem
      List()      // If neither is true, nothing can be done
    case ContextEvent(c) if c.isClosed && c.parentContext != null => // Only if the context is closed we might close the others.
      val p = c.parentContext
      if(p.splitKind == BetaSplit) return List(SetContextTask(p,Blackboard().getStatus(c).get))
      if(p.splitKind == AlphaSplit && c.childContext.forall(_.isClosed)) return List(SetContextTask(p,Blackboard().getStatus(c).get))     // TODO: Working for non theorem
      List()      // If neither is true, nothing can be done
    case _                => List()
  }

  override def run(t: Task): Result = t match {
    case SetContextTask(con,status) =>
      con.close()
      new ContextResult(con, status)

    case _  => Out.warn(s"[$name]:\n Got wrong task\n   ${t.pretty}"); EmptyResult
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

class ContextResult(c : Context, s : StatusSZS) extends Result {
  override def newFormula(): Set[FormulaStore] = Set()
  override def updateFormula(): Map[FormulaStore, FormulaStore] = Map()
  override def updateStatus(): List[(Context, StatusSZS)] = List((c,s))
  override def removeFormula(): Set[FormulaStore] = Set()
  override def updatedContext(): Set[Context] = Set(c)
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
object CounterContextControlAgent extends FifoAgent {
  override val maxMoney : Double = 50000
  override def name: String = "CounterContextControlAgent"

  override protected def toFilter(event: Event): Iterable[Task] = {
    event match {
      case StatusEvent(c,s) if c.parentContext != null => //&& c.splitKind == BetaSplit =>
        //Out.output(s"[$name]: StatusEvent($c,${s.output})")
        val p = c.parentContext
        if(s == SZS_CounterSatisfiable) return List(SetContextTask(p,SZS_CounterSatisfiable))
        List()      // If neither is true, nothing can be done
      case ContextEvent(c) if c.isClosed && c.parentContext != null => //&& c.splitKind == BetaSplit =>
        //Out.output(s"[$name]: ContextEvent($c)")
        val p = c.parentContext
        if(Blackboard().getStatus(c) == SZS_CounterSatisfiable) return List(SetContextTask(p, SZS_CounterSatisfiable))
        if(p.childContext.forall(_.isClosed)) return List(SetContextTask(p, SZS_GaveUp))
        List()      // If neither is true, nothing can be done
      case _                => List()
    }
  }

  override def run(t: Task): Result = t match {
    case SetContextTask(con,status) =>
      con.close()
      new ContextResult(con, status)

    case _  => Out.warn(s"[$name]:\n Got wrong task\n   ${t.pretty}"); EmptyResult
  }
}

