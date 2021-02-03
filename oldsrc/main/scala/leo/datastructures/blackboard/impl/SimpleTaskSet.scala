package leo.datastructures.blackboard.impl

import java.util.concurrent.atomic.AtomicLong

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


  private val agents : mutable.Set[Agent] = mutable.Set[Agent]()
  private val tasks : mutable.Set[Task] = mutable.Set[Task]()
  private val deactivate : mutable.Set[Task] = mutable.Set[Task]()

  private val submitTime : AtomicLong = new AtomicLong(0)
  private val submitAmount : AtomicLong = new AtomicLong(0)
  private val finishTime : AtomicLong = new AtomicLong(0)
  private val finishAmount : AtomicLong = new AtomicLong(0)
  private val commitTime : AtomicLong = new AtomicLong(0)
  private val commitAmount : AtomicLong = new AtomicLong(0)


  /**
    * Adds a new [[leo.agents.Agent]] to the TaskGraph.
    *
    * @param a The agent to be added
    */
  override def addAgent(a: Agent): Unit = synchronized {
    agents += a
  }


  /**
    * Removes a [[leo.agents.Agent]] from the TaskGraph
    *
    * @param a The agent to be removed
    */
  override def removeAgent(a: Agent): Unit = synchronized {
    agents -= a
  }

  override def executableTasks: Iterator[Task] = synchronized {
    tasks.iterator
  }

  override def containsAgent(a: Agent): Boolean = synchronized(agents.contains(a))

//  override def executingTasks(a: Agent) : Int = synchronized(agentExec.getOrElse(a, 0))

  /**
    *
    * Finishes a task afer its execution.
    * It is henceforth removed from the TaskSet and no longer considered
    * for dependency consideration.
    *
    * @param t the newly finished task
    */
  override def finish(t: Task): Unit = synchronized {
    val start = System.currentTimeMillis()
    LockSet.releaseTask(t)
    if(ActiveTracker.decAndGet() <= 0)
      blackboard.forceCheck()
    val newEnabled = deactivate.filter(t => LockSet.isExecutable(t))
    tasks ++= newEnabled
    deactivate --= newEnabled
    val time = System.currentTimeMillis() - start
    finishTime.addAndGet(time)
    finishAmount.incrementAndGet()
  }


  override def clear(): Unit = synchronized{
    agents.clear()
    tasks.clear()
    deactivate.clear()
  }

  /**
    *
    * List of all tasks in the system
    *
    * @return
    */
  override def registeredTasks: Iterable[Task] = synchronized(tasks ++ deactivate)

  /**
    * Marks an agent as active and reanables its tasks for exectuion.
    *
    * @param a The agent to be turned active
    */
  override def active(a: Agent): Unit = {}

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
  override def existExecutable: Boolean = synchronized(tasks.nonEmpty)

  /**
    * Submits a new task created by an agent to the scheduler.
    *
    * @param t The task the agent `a` wants to execute.
    */
  override def submit(t: Task): Unit = synchronized {
    val start = System.currentTimeMillis()
    if(tasks.contains(t)) return  // If already existent, then nothing happens
    if(LockSet.isOutdated(t)) return
    if(LockSet.isExecutable(t)) {
      tasks += t
    } else {
      deactivate += t
    }
    ActiveTracker.incAndGet()
    val time = System.currentTimeMillis() - start
    submitAmount.incrementAndGet()
    submitTime.addAndGet(time)
  }

  /**
    * Marks a set of tasks as commited to the scheduler.
    * There are not removed from dependency considerations until
    * a `finish` is called on them.
    *
    * @param ts The set of tasks committed to the scheduler.
    */
  override def commit(ts: Set[Task]): Unit = synchronized{
    val start = System.currentTimeMillis()
    ts foreach (t => LockSet.lockTask(t))
    tasks --= ts
    val delT = tasks filter (t => LockSet.isOutdated(t))
    val delD = deactivate filter( t=> LockSet.isOutdated(t))

    tasks --= delT
    deactivate --= delD

    val newD = tasks filter (t => !LockSet.isExecutable(t))
    tasks --= newD
    deactivate ++= newD
    val time = System.currentTimeMillis() - start
    submitTime.addAndGet(time)
    submitAmount.addAndGet(ts.size)
  }

  def info(): Unit = {
    val sb = new mutable.StringBuilder()

    val ct = commitTime.get()
    val ca = commitAmount.get()
    val cm = if(ca == 0) 0.0 else (ct * 1.0) / ca
    val ft = finishTime.get()
    val fa = finishAmount.get()
    val fm = if(fa == 0) 0.0 else (ft * 1.0) / fa
    val st = submitTime.get()
    val sa = submitAmount.get()
    val sm = if(sa == 0) 0.0 else (st * 1.0) / sa
    val tt = ct + ft + st
    val ta = ca + fa + sa
    val tm = if(ta == 0) 0.0 else (tt * 1.0) / ta


    sb.append("\n\n  Scheduler\n")
    sb.append(s"    Submit : ${sa} times, ${st}ms time, ${sm}ms mean\n")
    sb.append(s"    Commit : ${ca} times, ${ct}ms time, ${cm}ms mean\n")
    sb.append(s"    Finish : ${fa} times, ${ft}ms time, ${fm}ms mean\n")
    sb.append("--------------------------------------------------------------\n")
    sb.append(s"    Total : ${ta} times, ${tt}ms time, ${tm}ms mean\n")

    leo.Out.comment(sb.toString())
  }
}
