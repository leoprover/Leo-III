package leo.datastructures.blackboard

import leo.datastructures.context.Context
import leo.modules.output.StatusSZS

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
//
///**
// * Capsules a Formula that was recently added or modified in the blackboard.
// * @param f - Modified formula
// */
//private class DataEvent(val f : Any, val dataType: DataType) extends Event {}

/**
 * Creates and deconstructs an Event containing a single formula
 */
//object DataEvent {
//
//  def apply(f : Any, d : DataType) : Event = new DataEvent(f,d)
//
//  def unapply(e : Event) : Option[(Any,DataType)] = e match {
//    case f : DataEvent  => Some((f.f, f.dataType))
//    case _                => None
//  }
//}
//
//private class ContextEvent(c : Context) extends Event {
//  def getC : Context = c
//}
//
//object ContextEvent {
//  def apply(c : Context) : Event = new ContextEvent(c)
//
//  def unapply(e : Event) : Option[Context] = e match {
//    case c : ContextEvent => Some(c.getC)
//    case _    => None
//  }
//}

//private class StatusEvent(val c : Context, val s : StatusSZS) extends Event {}
//
//object StatusEvent {
//  def apply(c : Context, s : StatusSZS) : Event = new StatusEvent(c,s)
//
//  def unapply(e : Event) : Option[(Context, StatusSZS)] = e match {
//    case c : StatusEvent => Some(c.c, c.s)
//    case _  => None
//  }
//}

class DoneEvent() extends Event {}

object DoneEvent {
  def apply() = new DoneEvent()
}