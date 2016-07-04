package leo.datastructures.blackboard.scheduler

import leo.LeoTestSuite
import leo.agents.{Task, AbstractAgent, Agent}
import leo.datastructures.blackboard._

/**
  * Created by mwisnie on 2/9/16.
  */
object SchedulerRunTest {


  def main(args : Array[String]) {
    Blackboard().addDS(Store)

    AgentA.register()
    AgentB.register()
    EndAgent.register()

    Blackboard().addData(AnyType)("ping")


    Scheduler().signal()
  }
}


case object AnyType extends DataType

object EndAgent extends AbstractAgent {
  import leo.datastructures.blackboard.DoneEvent
  override def name : String = "EndAgent"
  override def filter(event: Event): Iterable[Task] = event match {
    case d : DoneEvent =>
      println("Terminating"+" store="+Store.v.mkString(","))
      Scheduler().killAll()
      Iterable.empty
    case _ => Iterable.empty
  }
}

object AgentA extends AbstractAgent {
  override def name: String = "AgentA"
  override def filter(event: Event): Iterable[Task] = event match {
    case DataEvent(s : String, AnyType) =>
      if(s == "ping") {
        println("New Task")
        Seq(TaskA("ping", "pong"))
      }
      else
        Seq()
    case _ => Iterable.empty
  }
}

case class TaskA(in : String, out : String) extends Task {
  override val name: String = "TaskA"
  override def getAgent: Agent = AgentA
  override def writeSet(): Map[DataType, Set[Any]] = Map(AnyType -> Set(in))
  override def readSet(): Map[DataType, Set[Any]] = Map.empty
  override def run: Result = {
    println(in+" store="+Store.v.mkString(","))
    Result().update(AnyType)(in)(out)
  }
  override def bid: Double = 0.6

  override def pretty: String = s"TaskA($in -> $out)"
}

object AgentB extends AbstractAgent {
  override def name: String = "AgentB"
  override def filter(event: Event): Iterable[Task] = event match {
    case DataEvent(s : String, AnyType) =>
      if(s == "pong") {
        Seq(TaskB("pong", "ping"))
      }
      else
        Seq()
    case _ => Iterable.empty
  }
}

case class TaskB(in : String, out : String) extends Task {
  override val name: String = "TaskB"
  override def getAgent: Agent = AgentB
  override def writeSet(): Map[DataType, Set[Any]] = Map.empty
  override def readSet(): Map[DataType, Set[Any]] = Map(AnyType -> Set(in))
  override def run: Result = {
    println(in+" store="+Store.v.mkString(","))
    Result().insert(AnyType)(out)
  }
  override def bid: Double = 0.5

  override def pretty: String = s"TaskB($in -> $out)"
}

/**
  * Dump Store, that does not save anything
  */
object Store extends DataStore {
  import scala.collection.mutable

  val v : mutable.Set[String] = mutable.Set.empty

  override val storedTypes: Seq[DataType] = Seq(AnyType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (os : String, on : String) =>
      v.remove(os)
      if(v.contains(on)) false
      else {
        v.add(on)
        true
      }
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case on :String =>
      if(v.contains(on)) false
      else {
        v.add(on)
        true
      }
    case _ => false
  }
  override def clear(): Unit = v.clear()
  override protected[blackboard] def all(t: DataType): Set[Any] = v.toSeq.toSet
  override def delete(d: Any): Unit = d match {
    case s : String => v.remove(s)
    case _ => ()
  }
}
