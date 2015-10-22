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
    Blackboard().addDS(TaskDAG)
    StateTestAgent.setMaxTasks(1)
    StateTestAgent.register()
    (new FifoController(ObserveAgent)).register()

    Scheduler().signal()

    synchronized{
      while(!end){wait()}
    }
    Scheduler().killAll()
    println(s"EndState:\n  ${TaskDAG.all(TaskType).mkString(",")}")
  }

  private var end = false
  def wake() : Unit = synchronized{
    end = true
    this.notifyAll()
  }
}





object StateTestAgent extends StateDrivenAgent {
  final val PARTASK : Int = 3
  override protected def searchTasks: Iterable[Task] =TaskDAG.eligable.map{s => StateTestTask(s)}
  override def kill(): Unit = {}

  var round : Int = 0

  override def run(t: Task): Result = t match {
      case StateTestTask(s) =>
        round = round + 1
        //println(s"Done $s as $round")
        Result().insert(TaskType)(s)
      case _ => Result()
    }




  override def openTasks: Int = TaskDAG.openWork
  override val name: String = "dag_agent"
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
object TaskDAG extends DataStore {

  private var done : Set[String] = Set()

  /**
   * Stores the structure:
   *  a -> Set(b)
   * where a can be executed, if all elements `b` are
   * in [[done]].
   */
  private var dag : Map[String, Set[String]] = Map(
    "schlafen" -> Set(),
    "aufstehen" -> Set("schlafen"),
    "erleichtern" -> Set("aufstehen"),
    "essen" -> Set("aufstehen"),
    "zaehne_putzen" -> Set("essen", "erleichtern"),
    "tasche_öffnen" -> Set("aufstehen"),
    "tasche_packen" -> Set("tasche_öffnen"),
    "tasche_schließen" -> Set("tasche_packen"),
    "unterhose_anziehen" -> Set("zaehne_putzen"),
    "unterhemd_anziehen" -> Set("zaehne_putzen"),
    "hemd_anziehen" -> Set("unterhemd_anziehen"),
    "socken_anziehen" -> Set("zaehne_putzen"),
    "hose_anziehen" -> Set("hemd_anziehen", "unterhose_anziehen", "socken_anziehen"),
    "schuhe_anziehen" -> Set("socken_anziehen", "hose_anziehen"),
    "losgehen" -> Set("schuhe_anziehen", "hose_anziehen", "hemd_anziehen", "tasche_schließen")
  )


  def size : Int = synchronized(done.size)

  def eligable : Iterable[String] = synchronized{
    dag.filter{ case (w, req) => !(done contains w)&& (req subsetOf done)}.map(_._1)
  }

  def openWork : Int = {
    dag.size - done.size
  }


  override val storedTypes: Seq[DataType] = Seq(TaskType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (os : String, ns : String) => synchronized{
      done = done - os + ns
      true
    }
    case _ => false
  }
  override def delete(d: Any): Unit = d match {
    case ds : String => synchronized{
      done = done - ds
    }
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case ns : String => synchronized {
      done = done + ns
      true
    }
    case _ => false
  }
  override def clear(): Unit = synchronized{done = Set()}
  override def all(t: DataType): Set[Any] = synchronized(done.toSet)
}

case object TaskType extends DataType {}