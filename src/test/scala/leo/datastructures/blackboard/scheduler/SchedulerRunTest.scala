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

    Blackboard().addData(AnyType)("ping")

    AgentA.register()
    AgentB.register()
    EndAgent.register()


    Scheduler().signal()
  }
}


case object AnyType extends DataType

object EndAgent extends AbstractAgent {
  import leo.datastructures.blackboard.DoneEvent
  override def name : String = "EndAgent"
  override def init(): Iterable[Task] = Seq()
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
  override def init(): Iterable[Task] = {
    if(Store.v.contains("ping")){
      return Seq(TaskA("ping", "pong"))
    } else
      Seq()
  }

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Result =>
      val up = r.updates(AnyType)
      if(up.nonEmpty){
        val s = up.head._2.toString
        if(s == "ping") {
          println("New Task")
          return Seq(TaskA("ping", "pong"))
        }
        else
          Seq()
      } else
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
  override def init(): Iterable[Task] = {
    if(Store.v.contains("pong")){
      return Seq(TaskA("pong", "ping"))
    } else
      Seq()
  }

  override def filter(event: Event): Iterable[Task] = event match {
    case r : Result =>
      val up = r.updates(AnyType)
      if(up.nonEmpty){
        val s = up.head._2.toString
        if(s == "pong") {
          println("New Task")
          return Seq(TaskA(s, "ping"))
        }
        else
          Seq()
      } else
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
  override def updateResult(r: Result): Boolean = {
    val ins = r.inserts(AnyType).toIterator
    val ups = r.updates(AnyType).toIterator
    while(ins.nonEmpty){
      val i = ins.next().toString
      v.add(i)
    }
    while(ups.nonEmpty){
      val (o,u) = ups.next().asInstanceOf[(String, String)]
      v.remove(o)
      v.add(u)
    }
    true
  }

  override def clear(): Unit = v.clear()
  override protected[blackboard] def all(t: DataType): Set[Any] = v.toSeq.toSet
}
