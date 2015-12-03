package leo.datastructures.blackboard

import leo.LeoTestSuite
import leo.agents.{Task, TAgent}
import leo.datastructures.blackboard.impl.TaskSelectionSet

/**
  * Created by mwisnie on 12/2/15.
  */
class TaskSetTest extends LeoTestSuite {

  test("insertAgents_NoDep"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    assert(taskSet.containsAgent(AgentB), "AgentB was not added to the taskSet")
    assert(taskSet.containsAgent(AgentD), "AgentD was not added to the taskSet")
    assert(!taskSet.dependOn(AgentB,AgentD), "There should be no dependencies")
    assert(!taskSet.dependOn(AgentD,AgentB), "There should be no dependencies")
  }

  test("insertAgents_WithDep"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    assert(taskSet.dependOn(AgentA, AgentB), "AgentA should be executed before AgentB")
  }

  test("Task with no dependencies and no intersection"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    val t1 = new DummyTask("task1", Set(1), Set(2))
    val t2 = new DummyTask("task2", Set(3),Set(4))

    taskSet.submit(AgentB, t1)
    taskSet.submit(AgentD, t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with no dependencies but intersections"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentD)

    val t1 = new DummyTask("task1", Set(1), Set(2))
    val t2 = new DummyTask("task2", Set(1),Set(3))

    taskSet.submit(AgentB, t1)
    taskSet.submit(AgentD, t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with dependencies but no intersections"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 = new DummyTask("task1", Set(1), Set(2))
    val t2 = new DummyTask("task2", Set(3),Set(4))

    taskSet.submit(AgentB, t1)
    taskSet.submit(AgentD, t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(exec.contains(t2), "The task set should contain task2")
  }

  test("Task with dependencies and intersections"){
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 = new DummyTask("task1", Set(1), Set(2))
    val t2 = new DummyTask("task2", Set(1),Set(3))

    taskSet.submit(AgentA, t1)
    taskSet.submit(AgentB, t2)

    val exec = taskSet.executableTasks.toSet
    //println(exec.mkString(", "))
    assert(exec.contains(t1), "The task set should contain task1")
    assert(!exec.contains(t2), "The task set should not contain task2")
  }

  test("Task with dependencies and intersection 2") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()
    // A -> B -> C
    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)
    taskSet.addAgent(AgentC)

    val t1 = new DummyTask("task1", Set(1), Set(2))
    val t2 = new DummyTask("task2", Set(1),Set(3))
    val t3 = new DummyTask("task3", Set(4),Set(1))

    taskSet.submit(AgentC, t3)
    val exec1 = taskSet.executableTasks.toSet

    assert(exec1.contains(t3), "Only task should be able to be executed")

    taskSet.submit(AgentB, t2)
    val exec2 = taskSet.executableTasks.toSet

    assert(exec2.contains(t2), "Task2 should be executed first")

    taskSet.submit(AgentA, t1)
    val exec3 = taskSet.executableTasks.toSet

    assert(exec3.contains(t1), "Task1 should be executed first")
  }

  test("Task commit & finish") {
    val taskSet : TaskSelectionSet = new TaskSelectionSet()

    taskSet.addAgent(AgentA)
    taskSet.addAgent(AgentB)

    val t1 =  new DummyTask("task1", Set(3), Set(1))
    val t2 = new DummyTask("task2", Set(1), Set(2))

    taskSet.submit(AgentA, t1)
    taskSet.submit(AgentB, t2)

    val exec1 = taskSet.executableTasks.toSet

    assert(exec1.contains(t1), "Only t1 should be executable")

    taskSet.commit(Set(t1))
    taskSet.finish(t1)

    val exec2 = taskSet.executableTasks.toSet

    assert(exec2.contains(t2), "Only t2 should be executable")
  }

}


// Dummy Agents for dependencies

object AgentA extends TAgent {
  override val name: String = "DummyA"

  override def filter(event: Event): Unit = ???
  override def clearTasks(): Unit = ???
  override def interest: Option[Seq[DataType]] = ???
  override def kill(): Unit = ???
  override def maxMoney: Double = ???
  override def taskChoosen(t: Task): Unit = ???
  override def getAllTasks: Iterable[Task] = ???
  override def taskFinished(t: Task): Unit = ???
  override def hasTasks: Boolean = ???
  override def removeColliding(nExec: Iterable[Task]): Unit = ???
  override def openTasks: Int = ???
  override def getTasks: Iterable[Task] = ???


  override def before: Set[TAgent] = Set(AgentB)
  override def after: Set[TAgent] = Set.empty
}

object AgentB extends TAgent {
  override val name: String = "DummyB"

  override def filter(event: Event): Unit = ???
  override def clearTasks(): Unit = ???
  override def interest: Option[Seq[DataType]] = ???
  override def kill(): Unit = ???
  override def maxMoney: Double = ???
  override def taskChoosen(t: Task): Unit = ???
  override def getAllTasks: Iterable[Task] = ???
  override def taskFinished(t: Task): Unit = ???
  override def hasTasks: Boolean = ???
  override def removeColliding(nExec: Iterable[Task]): Unit = ???
  override def openTasks: Int = ???
  override def getTasks: Iterable[Task] = ???

  override def before: Set[TAgent] = Set.empty
  override def after: Set[TAgent] = Set.empty
}

object AgentC extends TAgent {
  override val name: String = "DummyC"

  override def filter(event: Event): Unit = ???
  override def clearTasks(): Unit = ???
  override def interest: Option[Seq[DataType]] = ???
  override def kill(): Unit = ???
  override def maxMoney: Double = ???
  override def taskChoosen(t: Task): Unit = ???
  override def getAllTasks: Iterable[Task] = ???
  override def taskFinished(t: Task): Unit = ???
  override def hasTasks: Boolean = ???
  override def removeColliding(nExec: Iterable[Task]): Unit = ???
  override def openTasks: Int = ???
  override def getTasks: Iterable[Task] = ???

  override def before: Set[TAgent] = Set.empty
  override def after: Set[TAgent] = Set(AgentB)
}

object AgentD extends TAgent {
  override val name: String = "DummyD"

  override def filter(event: Event): Unit = ???
  override def clearTasks(): Unit = ???
  override def interest: Option[Seq[DataType]] = ???
  override def kill(): Unit = ???
  override def maxMoney: Double = ???
  override def taskChoosen(t: Task): Unit = ???
  override def getAllTasks: Iterable[Task] = ???
  override def taskFinished(t: Task): Unit = ???
  override def hasTasks: Boolean = ???
  override def removeColliding(nExec: Iterable[Task]): Unit = ???
  override def openTasks: Int = ???
  override def getTasks: Iterable[Task] = ???

  override def before: Set[TAgent] = Set.empty
  override def after: Set[TAgent] = Set.empty
}

class DummyTask(val name : String, writeData : Set[Any], readData : Set[Any]) extends Task{
  override def writeSet(): Map[DataType, Set[Any]] = Map(AnyType -> writeData)
  override def readSet(): Map[DataType, Set[Any]] = Map(AnyType -> readData)
  override def run: Result = Result()
  override def bid: Double = 1

  override def pretty: String = s"name :\n  write: ${writeData.mkString(",")}\n  read : ${readData.mkString(",")}"
  override def toString : String = name
}

case object AnyType extends DataType {}