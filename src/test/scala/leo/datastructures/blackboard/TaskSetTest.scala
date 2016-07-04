package leo.datastructures.blackboard

import leo.LeoTestSuite
import leo.agents.{Task, Agent}
import leo.datastructures.blackboard.impl.TaskSelectionSet

/**
  * Created by mwisnie on 12/2/15.
  */
class TaskSetTest extends LeoTestSuite {

  test("insertAgents_NoDep."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    assert(taskSet.containsAgent(AgentB), "AgentB was not added to the taskSet")
    assert(taskSet.containsAgent(AgentD), "AgentD was not added to the taskSet")
//    assert(!taskSet.dependOn(AgentB,AgentD), "There should be no dependencies")
//    assert(!taskSet.dependOn(AgentD,AgentB), "There should be no dependencies")
  }

  test("insertAgents_WithDep."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

//    assert(taskSet.dependOn(AgentA, AgentB), "AgentA should be executed before AgentB")
  }

  test("Task with no dependencies and no intersection."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    val t1 = new DummyTask("task1", Set(1), Set(2), AgentB)
    val t2 = new DummyTask("task2", Set(3),Set(4), AgentD)

    taskSet.submit(t1)
    taskSet.submit(t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with no dependencies but intersections."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    val t1 = new DummyTask("task1", Set(1), Set(2), AgentB)
    val t2 = new DummyTask("task2", Set(1),Set(3), AgentD)

    taskSet.submit(t1)
    taskSet.submit(t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with dependencies but no intersections."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 = new DummyTask("task1", Set(1), Set(2), AgentB)
    val t2 = new DummyTask("task2", Set(3),Set(4), AgentD)

    taskSet.submit(t1)
    taskSet.submit(t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with dependencies and intersections."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 = new DummyTask("task1", Set(1), Set(2), AgentA)
    val t2 = new DummyTask("task2", Set(1),Set(3), AgentB)

    taskSet.submit(t1)
    taskSet.submit(t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(!exec.contains(t2), "The task set should not contain task2")
  }

  test("Task with dependencies and intersection 2.") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    // A -> B -> C
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentC)

    val t1 = new DummyTask("task1", Set(1), Set(2), AgentA)
    val t2 = new DummyTask("task2", Set(1),Set(3), AgentB)
    val t3 = new DummyTask("task3", Set(4),Set(1), AgentC)

    taskSet.submit(t3)
    val exec1 = taskSet.executableTasks.toSet

    assert(exec1.contains(t3), "Only task should be able to be executed")

    taskSet.submit(t2)
    val exec2 = taskSet.executableTasks.toSet

    assert(exec2.contains(t2), "Task2 should be executed first")

    taskSet.submit(t1)
    val exec3 = taskSet.executableTasks.toSet

    assert(exec3.contains(t1), "Task1 should be executed first")
  }

  test("Task commit & finish.") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 =  new DummyTask("task1", Set(3), Set(1), AgentA)
    val t2 = new DummyTask("task2", Set(1), Set(2), AgentB)

    taskSet.submit(t1)
    taskSet.submit(t2)

    val exec1 = taskSet.executableTasks.toSet

    assert(exec1.contains(t1), "Only t1 should be executable")

    taskSet.commit(Set(t1))
    taskSet.finish(t1)

    val exec2 = taskSet.executableTasks.toSet

    assert(exec2.contains(t2), "Only t2 should be executable")
  }

  test("Task commit & finish interleave accepting.") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 =  new DummyTask("task1", Set(3), Set(1), AgentA)
    val t2 = new DummyTask("task2", Set(1), Set(2), AgentA)

    taskSet.submit(t1)

    taskSet.commit(Set(t1))

    taskSet.submit(t2)

    taskSet.finish(t1)

    val exec2 = taskSet.executableTasks.toSet
    assert(exec2.contains(t2), "Only t2 should be executable")
  }


  test("Task commit & finish interleave rejecting."){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 =  new DummyTask("task1", Set(1), Set(3), AgentA)
    val t2 = new DummyTask("task2", Set(1), Set(2), AgentA)

    taskSet.submit(t1)

    taskSet.commit(Set(t1))

    taskSet.submit(t2)

    taskSet.finish(t1)

    val exec2 = taskSet.executableTasks.toSet
    assert(exec2.isEmpty, "There should be no executable tasks.")
  }

  test("Task rejecting & interleave no dependency.") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    val t1 =  new DummyTask("task1", Set(1), Set(3), AgentB)
    val t2 = new DummyTask("task2", Set(1), Set(2), AgentD)

    taskSet.submit(t1)

    taskSet.commit(Set(t1))

    taskSet.submit(t2)

    taskSet.finish(t1)

    val exec2 = taskSet.executableTasks.toSet
    assert(exec2.isEmpty, "There should be no executable tasks.")
  }
}


// Dummy Agents for dependencies

object AgentA extends Agent {
  override val name: String = "DummyA"

  override def filter(event: Event): Iterable[Task] = Iterable.empty
  override def interest: Option[Seq[DataType]] = None
  override def kill(): Unit = ()
  override def maxMoney: Double = 0
  override def taskChoosen(t: Task): Unit = ()
  override def taskFinished(t: Task): Unit = ()


  override def before: Set[Agent] = Set(AgentB)
  override def after: Set[Agent] = Set.empty
}

object AgentB extends Agent {
  override val name: String = "DummyB"

  override def filter(event: Event): Iterable[Task] = Iterable.empty
  override def interest: Option[Seq[DataType]] = None
  override def kill(): Unit = ()
  override def maxMoney: Double = 0
  override def taskChoosen(t: Task): Unit = ()
  override def taskFinished(t: Task): Unit = ()

  override def before: Set[Agent] = Set.empty
  override def after: Set[Agent] = Set.empty
}

object AgentC extends Agent {
  override val name: String = "DummyC"

  override def filter(event: Event): Iterable[Task] = Iterable.empty
  override def interest: Option[Seq[DataType]] = None
  override def kill(): Unit = ()
  override def maxMoney: Double = 0
  override def taskChoosen(t: Task): Unit = ()
  override def taskFinished(t: Task): Unit = ()

  override def before: Set[Agent] = Set.empty
  override def after: Set[Agent] = Set(AgentB)
}

object AgentD extends Agent {
  override val name: String = "DummyD"

  override def filter(event: Event): Iterable[Task] = Iterable.empty
  override def interest: Option[Seq[DataType]] = None
  override def kill(): Unit = ()
  override def maxMoney: Double = 0
  override def taskChoosen(t: Task): Unit = ()
  override def taskFinished(t: Task): Unit = ()

  override def before: Set[Agent] = Set.empty
  override def after: Set[Agent] = Set.empty
}

class DummyTask(val name : String, writeData : Set[Any], readData : Set[Any], a : Agent) extends Task{
  override def writeSet(): Map[DataType, Set[Any]] = Map(AnyType -> writeData)
  override def readSet(): Map[DataType, Set[Any]] = Map(AnyType -> readData)
  override def run: Result = Result()
  override def bid: Double = 1

  override def pretty: String = s"name :\n  write: ${writeData.mkString(",")}\n  read : ${readData.mkString(",")}"
  override def toString : String = name
  override def getAgent: Agent = a
}

case object AnyType extends DataType {}