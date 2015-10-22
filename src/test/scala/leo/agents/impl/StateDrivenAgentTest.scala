package leo.agents.impl

import leo.Configuration
import leo.agents.{FifoController, Task, StateDrivenAgent, Agent}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.CLParameterParser


/**
 * Testing a StateDrivenAgent.
 */
object StateDrivenAgentTest {
  def main(args : Array[String]): Unit ={
    Blackboard().addDS(TaskQueue)
    TaskQueue.insert("schlafen")
    StateTestAgent.setMaxTasks(1)
    StateTestAgent.register()
    (new FifoController(ObserveAgent)).register()

    Scheduler().signal()

    synchronized{
      while(!end){wait()}
    }
    Scheduler().killAll()
    println(s"EndState:\n  ${TaskQueue.all(TaskType).mkString(",")}")
  }

  private var end = false
  def wake() : Unit = synchronized{
    end = true
    this.notifyAll()
  }
}






object StateTestAgent extends StateDrivenAgent {
  final val PARTASK : Int = 1
  override protected def searchTasks: Iterable[Task] = if(!TaskQueue.contains("losgehen")) {
      TaskQueue.get(PARTASK) map {s => StateTestTask(s)}
    } else { Nil }
  override def kill(): Unit = {}



  override def run(t: Task): Result = t match {
      case StateTestTask("schlafen") => Result().remove(TaskType)("schlafen").insert(TaskType)("aufstehen")
      case StateTestTask("aufstehen") => Result().remove(TaskType)("aufstehen").insert(TaskType)("waschen")
      case StateTestTask("waschen") => Result().remove(TaskType)("waschen").insert(TaskType)("frühstücken")
      case StateTestTask("frühstücken") => Result().remove(TaskType)("frühstücken").insert(TaskType)("anziehen")
      case StateTestTask("anziehen") => Result().remove(TaskType)("anziehen").insert(TaskType)("losgehen")
      case _ => println(s"${t.pretty} got no match"); Result()
    }




  override def openTasks: Int = 5 - TaskQueue.size
  override val name: String = "fib_agent"
}









case class StateTestTask(s : String) extends Task{
  override def name: String = s
  override def writeSet(): Map[DataType, Set[Any]] = Map(TaskType -> Set(s))
  override def readSet(): Map[DataType, Set[Any]] = Map.empty
  override def bid(budget: Double): Double = budget / 10
  override def pretty: String = s
  override def equals(o : Any) = o match {
    case StateTestTask(s1) => s.equals(s1)
    case _ => false
  }
}

object ObserveAgent extends Agent {
  override def name: String = "observe_agent"
  override def run(t: Task): Result = Result()
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(s : String, TaskType) =>
      println(s)
      Nil
    case _ : DoneEvent =>
      StateDrivenAgentTest.wake()
      Nil
    case _ => Nil
  }
}

/**
 * TaskQueue, delivering Strings in orderly fashion.
 */
object TaskQueue extends DataStore {

  private var q : List[String] = Nil

  def get(n : Int) = synchronized{
    q.take(n)
  }

  def size : Int = synchronized(q.size)

  def contains(s : String) = synchronized(q.toSet.contains(s))

  override val storedTypes: Seq[DataType] = Seq(TaskType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (os : String, ns : String) => synchronized{
      q = ((q.takeWhile(_ != os) ++ List(ns)) ++ {val p = q.dropWhile(_ != os); if(p.isEmpty) p else p.tail})
      true
    }
    case _ => false
  }
  override def delete(d: Any): Unit = d match {
    case ds : String => synchronized{
      q = q.takeWhile(_ != ds) ++ {val p = q.dropWhile(_ != ds); if(p.isEmpty) p else p.tail}
    }
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case ns : String => synchronized {
      q = q :+ ns
      true
    }
    case _ => false
  }
  override def clear(): Unit = synchronized{q = Nil}
  override def all(t: DataType): Set[Any] = synchronized(q.toSet)
}

case object TaskType extends DataType {}