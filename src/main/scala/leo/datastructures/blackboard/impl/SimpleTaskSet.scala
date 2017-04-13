package leo.datastructures.blackboard.impl

import leo.agents.{Agent, Task}
import leo.datastructures.blackboard.{ActiveTracker, Blackboard, LockSet, TaskSet}

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
 *
  * @author Max WIsniewski
  * @since 5/23/16
  */
class SimpleTaskSet(blackboard: Blackboard) extends TaskSet{

  // TODO Save blocked tasks in extra queue (no unnecessary looping in scheduler)

  private val agentTasks : mutable.Map[Agent, mutable.Set[Task]] = mutable.HashMap[Agent, mutable.Set[Task]]()
  private val agentExec : mutable.Map[Agent, Int] = mutable.HashMap[Agent, Int]()

  /**
    * Adds a new [[leo.agents.Agent]] to the TaskGraph.
    *
    * @param a The agent to be added
    */
  override def addAgent(a: Agent): Unit = synchronized {
    agentTasks.put(a, mutable.HashSet[Task]())
    agentExec.put(a, 0)
  }


  /**
    * Removes a [[leo.agents.Agent]] from the TaskGraph
    *
    * @param a The agent to be removed
    */
  override def removeAgent(a: Agent): Unit = synchronized {
    agentTasks.remove(a)
    agentExec.remove(a)
  }

  /**
    * Dependecy Preselection for the scheduling algorithm.
    *
    * <p>
    * The returned list fullfills two properties:
    * <ol>
    * <li>For every [[leo.agents.Task]] `i` contained in this list, there exist no [[leo.agents.Task]] `j` in this TaskSet, with `j.before contains i` or
    * `i.after contains j`</li>
    * <li> For every [[leo.agents.Agent]] `a1` containing a task in this list, there exist no [[leo.agents.Agent]] `a2` that has a task  and (`a.before contains a2`
    * or `a2.after contains a`) holds.</li>
    * </ol>
    * </p>
    *
    * @return a set of non dependent [[leo.agents.Task]], ready for selection.
    */
  override def executableTasks: Iterable[Task] = synchronized {
    val rTasks = agentTasks.values
    val rTasks2 = rTasks.flatten
    rTasks2.filter{t => val a = t.getAgent; a.maxParTasks.fold(true)(n => n > agentExec.getOrElse(a, 0))}
  }
  // TODO not call registered tasks. compute it on its own.
  override def containsAgent(a: Agent): Boolean = synchronized(agentTasks.contains(a))

  override def executingTasks(a: Agent) : Int = synchronized(agentExec.getOrElse(a, 0))

  /**
    *
    * Finishes a task afer its execution.
    * It is henceforth removed from the TaskSet and no longer considered
    * for dependency consideration.
    *
    * @param t the newly finished task
    */
  override def finish(t: Task): Unit = synchronized {
    agentExec.put(t.getAgent, agentExec.getOrElse(t.getAgent, 1)-1)
    LockSet.releaseTask(t)
    if(ActiveTracker.decAndGet(s"Finished:\n  ${t.pretty}") <= 0)
      blackboard.forceCheck()
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
  override def active(a: Agent): Unit = {}

  override def dependOn(before: Agent, after: Agent): Unit = {}

  /**
    * Marks an agent as passive and considers its tasks no longer for execution.
    *
    * @param a The agent to be turned passive
    */
  override def passive(a: Agent): Unit = leo.Out.info(s"Called passive on:\n  ${a.name}\n  not supported feature.")


  /**
    * Checks through all [[leo.agents.Task]] and [[leo.agents.Agent]] for
    * executable tasks, after dependency check.
    *
    * @return true, iff there exist executable task
    */
  override def existExecutable: Boolean = synchronized(agentTasks.exists { case (a, set) => set.nonEmpty && a.maxParTasks.fold(true)(n => n > agentExec.getOrElse(a, 0))})

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param t The task the agent `a` wants to execute.
    */
  override def submit(t: Task): Unit = synchronized {
//    println(s"Try submitting Task:\n  ${t.pretty}")
    val s : Option[mutable.Set[Task]] = agentTasks.get(t.getAgent)
    val set = s.get
    if(set.contains(t)) return  // If already existent, then nothing happens
    if(LockSet.isOutdated(t)) return
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
      agentTasks.get(task.getAgent).foreach{set => set.remove(task)}  // Remove itself
      agentTasks.foreach{case (_, set) =>
        // Remove all colliding
        val rmSet = set.filter(isObsolete(task,_))
        rmSet.foreach{t =>
          ActiveTracker.decAndGet(s"Obsolete Task:\n  Remove ${t.pretty}\n  by ${task.pretty}")
          t.getAgent.taskCanceled(t)
          set.remove(t)
        }
      }
      task.getAgent.taskChoosen(task)
      agentExec.put(task.getAgent, agentExec.getOrElse(task.getAgent, 0)+1) // TODO group by agent and update
    }
  }

  private def isObsolete(takeTask : Task, obsoleteTask : Task) : Boolean = {
    val sharedTypes = takeTask.lockedTypes.intersect(obsoleteTask.lockedTypes)
    if(sharedTypes.isEmpty) return false
    sharedTypes.exists { d =>
      val w1: Set[Any] = takeTask.writeSet.getOrElse(d, Set.empty[Any])
      val r2 : Set[Any] = obsoleteTask.readSet.getOrElse(d, Set.empty[Any])
      val w2: Set[Any] = obsoleteTask.writeSet.getOrElse(d, Set.empty[Any])
      (w1 & w2).nonEmpty || (w1 & r2).nonEmpty
    }
  }
}
