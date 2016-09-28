package leo.datastructures.blackboard.impl

import leo.agents.{TAgent, Task}
import leo.datastructures.blackboard.{ActiveTracker, Blackboard, TaskSet}

import scala.collection.mutable

/**
  *<p>
  * This simple TaskSet implements the [[TaskSet]] interface.
  * The implementation is a simple set, that keeps no
  * track of any dependencies or checks for collision.
  * </p>
  * <p>
  * Can be employed in cases, when the developer is sure
  * there can be no interference between the tasks.
  *</p>
  * @author Max WIsniewski
  * @since 5/23/16
  */
class SimpleTaskSet extends TaskSet{

  private val agentTasks : mutable.Map[TAgent, mutable.Set[Task]] = mutable.HashMap[TAgent, mutable.Set[Task]]()

  /**
    * Adds a new [[leo.agents.TAgent]] to the TaskGraph.
    *
    * @param a The agent to be added
    */
  override def addAgent(a: TAgent): Unit = synchronized {
    agentTasks.put(a, mutable.HashSet[Task]())
  }


  /**
    * Removes a [[leo.agents.TAgent]] from the TaskGraph
    *
    * @param a The agent to be removed
    */
  override def removeAgent(a: TAgent): Unit = synchronized {
    agentTasks.remove(a)
  }

  /**
    * Dependecy Preselection for the scheduling algorithm.
    *
    * <p>
    * The returned list fullfills two properties:
    * <ol>
    * <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
    * `i.after contains j`</li>
    * <li> For every [[leo.agents.TAgent]] `a1` containing a task in this list, there exist no [[leo.agents.TAgent]] `a2` that has a task  and (`a.before contains a2`
    * or `a2.after contains a`) holds.</li>
    * </ol>
    * </p>
    *
    * @return a set of non dependent [[leo.agents.Task]], ready for selection.
    */
  override def executableTasks: Iterable[Task] = registeredTasks

  override def containsAgent(a: TAgent): Boolean = synchronized(agentTasks.contains(a))

  /**
    *
    * Finishes a task afer its execution.
    * It is henceforth removed from the TaskSet and no longer considered
    * for dependency consideration.
    *
    * @param t the newly finished task
    */
  override def finish(t: Task): Unit = synchronized {
    if(ActiveTracker.decAndGet(s"Finished:\n  ${t.pretty}") <= 0)
      Blackboard().forceCheck()
  }


  override def clear(): Unit = synchronized(agentTasks.clear())

  /**
    *
    * List of all tasks in the system
    *
    * @return
    */
  override def registeredTasks: Iterable[Task] = synchronized(agentTasks.values.flatten)

  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    *
    * @param a The agent to be turned active
    */
  override def active(a: TAgent): Unit = {}

  override def dependOn(before: TAgent, after: TAgent): Unit = {}

  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    *
    * @param a The agent to be turned passive
    */
  override def passive(a: TAgent): Unit = leo.Out.info(s"Called passive on:\n  ${a.name}\n  not supported feature.")


  /**
    * Checks through all [[leo.agents.Task]] and [[leo.agents.TAgent]] for
    * executable tasks, after dependency check.
    *
    * @return true, iff there exist executable task
    */
  override def existExecutable: Boolean = synchronized(agentTasks.exists { case (_, set) => set.nonEmpty})

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param t The task the agent `a` wants to execute.
    */
  override def submit(t: Task): Unit = synchronized {
    val s : Option[mutable.Set[Task]] = agentTasks.get(t.getAgent)
    val set = s.get
    if(set.contains(t)) return  // If already existent, then nothing happens
    set.add(t)
    ActiveTracker.incAndGet(s"Submitted Task:\n  ${t.pretty}")
  }

  /**
    * Marks a set of tasks as commited to the scheduler.
    * There are not removed from dependency considerations until
    * a `finish` is called on them.
    *
    * @param ts The set of tasks committed to the scheduler.
    */
  override def commit(ts: Set[Task]): Unit = synchronized{
    val it = ts.toIterator
    while (it.hasNext){
      val task = it.next
      agentTasks.get(task.getAgent).foreach{set => set.remove(task)}
    }
  }
}
