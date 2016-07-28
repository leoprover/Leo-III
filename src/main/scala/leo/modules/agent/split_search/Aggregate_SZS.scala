package leo.modules.agent.split_search

import leo.agents.{Agent, TAgent, Task}
import leo.datastructures.blackboard.impl.SZSStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import leo.modules.output.{SZS_GaveUp, SZS_Timeout, StatusSZS}
import leo.datastructures.context._

/**
  *
  * Observes a Splitted context (equal splits, with different normalizations)
  * and bubbles the Result up
  *
  * @since 4/13/16
  * @author Max Wisniewski
  */
object Aggregate_SZS extends Agent {
  override def name: String = "bubble_szs"
  override val interest : Option[Seq[DataType]] = Some(Seq(StatusType))

  /**
    * If a result is returned in a context. It will be bubbled upwards.
    */
  override def filter(event: Event): Iterable[Task] = {
    event match {
    case DataEvent(SZSStore(szs, c), StatusType) =>
      if(szs != SZS_Timeout && szs != SZS_GaveUp) {
        if(c.parentContext == null){
//          Scheduler().killAll()
        } else {
          val pc = c.parentContext
          return Seq(Aggregate_SZSTask(szs, pc, this))
        }
      }
      Seq()
    case _ => Seq()
  }
  }
}

case class Aggregate_SZSTask(szs : StatusSZS, c : Context, a : TAgent) extends Task {
  override def name: String = "bubble_szs"
  override def getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map()
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = Result().insert(StatusType)(SZSStore(szs, c))
  override def bid: Double = 0.5

  override val pretty: String = s"$name(${szs.apply} -> ${c.contextID})"
  override val toString: String = s"$name(${szs.apply} -> ${c.contextID})"
}
