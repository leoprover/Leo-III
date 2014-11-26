package leo.datastructures.blackboard

import leo.datastructures.context.Context

/**
 * Marker Interface for any Event for the Agents
 */
trait Event {

}

/**
 * Marker Interface for a message from Agent to Agent
 *
 * @author Max Wisniewski
 * @since 11/19/14
 */
trait Message extends Event {

}

/**
 * Capsules a Formula that was recently added or modified in the blackboard.
 * @param f - Modified formula
 */
private class FormulaEvent(f : FormulaStore) extends Event {
  def getF : FormulaStore = f
}

/**
 * Creates and deconstructs an Event containing a single formula
 */
object FormulaEvent{

  def apply(f : FormulaStore) : Event = new FormulaEvent(f)

  def unapply(e : Event) : Option[FormulaStore] = e match {
    case f : FormulaEvent  => Some(f.getF)
    case _                => None
  }
}

private class ContextEvent(c : Context) extends Event {
  def getC : Context = c
}

object ContextEvent {
  def apply(c : Context) : Event = new ContextEvent(c)

  def unapply(e : Event) : Option[Context] = e match {
    case c : ContextEvent => Some(c.getC)
    case _    => None
  }
}