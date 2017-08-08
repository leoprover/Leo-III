package leo.datastructures.blackboard.impl


import leo.agents.{Agent, Task}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._

import scala.collection.immutable.HashMap
import scala.collection.mutable

/**
 * This blackboard is a first reference implementation for the @see{Blackboard} interface.
 *
 * It utilizes no doubly added formulas and an auction implementation to access and organize tasks.
 *
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @since 19.08.2015
 */
private[blackboard] class AuctionBlackboard extends Blackboard {

  /**
   * Register a new Handler for Formula adding Handlers.
 *
   * @param a - The Handler that is to register
   */
  override def registerAgent(a: Agent) : Unit = {
    taskSelectionSet.addAgent(a)
    freshAgent(a)
  }

  override def unregisterAgent(a: Agent): Unit = {
    taskSelectionSet.removeAgent(a)
  }

  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask: Iterable[(Agent,Task)] = taskSelectionSet.getTask

  override def clear() : Unit = {
    dsset.foreach(_.clear())
    taskSelectionSet.clear()
  }

  /**
   * Gives all agents the chance to react to an event
   * and adds the generated tasks.
   *
   * @param t - Function that generates for each agent a set of tasks.
   */
  override def filterAll(t: (Agent) => Unit): Unit = {
    taskSelectionSet.agents.foreach{ a =>
      t(a)
    }
  }

  override def submitTasks(a: Agent, ts : Set[Task]) : Unit = {
    taskSelectionSet.submit(ts)  // TODO Synchronizing?
    signalTask()      // WHO HAS THE LOCK?=????
  }

  override def finishTask(t : Task) : Unit = {
    taskSelectionSet.finish(t)     // TODO synchronizing?
    LockSet.releaseTask(t)        // TODO Still necessary?
  }

  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  override protected[blackboard] def freshAgent(a: Agent): Unit = {
    val initTasks = a.init()
    if(initTasks.nonEmpty) {
      submitTasks(a, initTasks.toSet) // Todo Synchronizing with completion of tasks in the blackboard
      forceCheck()  // TODO return value whether to check?
    }
  }

  override def signalTask() : Unit = taskSelectionSet.signalChange()

  /**
   *
   * @return all registered agents
   */
  override def getAgents(): Iterable[Agent] = taskSelectionSet.agents

  /**
   * Sends a message to an agent.
   *
   * TODO: Implement without loss of tasks through messages
   *
   * @param m    - The message to send
   * @param to   - The recipient
   */
  override def send(m: Message, to: Agent): Unit = {
//    println(s"Called send to ${to.name}: $m")
    val ts = to.filter(m)
//    println(s"Filtered message to ${to.name}: ${ts.map(_.pretty).mkString("\n")}")
    submitTasks(to, ts.toSet)
//    println(s"Done submitting")
  }

  /**
   * Allows a force check for new Tasks. Necessary for the DoneEvent to be
   * thrown correctly.
   */
  override protected[blackboard] def forceCheck(): Unit = taskSelectionSet.synchronized(taskSelectionSet.notifyAll())


  private val dsset : mutable.Set[DataStore] = new mutable.HashSet[DataStore]
  private val dsmap : mutable.Map[DataType[Any], Set[DataStore]] = new mutable.HashMap[DataType[Any], Set[DataStore]]


  override def getData[T](dataType: DataType[T]): Set[T] = {
    val ds : Set[DataStore] = dsmap.getOrElse(dataType, Set.empty[DataStore])
    val data : Set[T] = ds.flatMap(ds => ds.get(dataType))
    data
  }

  override def addDS(ds: DataStore): Unit = if(dsset.add(ds)) ds.storedTypes.foreach{t => dsmap.put(t, dsmap.getOrElse(t, Set.empty) + ds)}

  override def rmDS(ds: DataStore): Unit = if (dsset.remove(ds)) ds.storedTypes.foreach{t => dsmap.put(t, dsmap.getOrElse(t, Set.empty) - ds)}

  override def getDS(d: Set[DataType[Any]]): Iterable[DataStore] = {
    dsmap.filterKeys(k =>
      d.contains(k))
      .values.flatten.toSet
  }

  override def getDS: Iterable[DataStore] = dsmap.values.flatten.toSet

  private var scheduler : Scheduler = null
  private var taskSelectionSet : TaskSelection = null

  private[blackboard] def setScheduler(scheduler : Scheduler) : Unit = {
    this.scheduler = scheduler
    this.taskSelectionSet = TaskSelection.getFreshTaskSelection(this, scheduler)
  }
}

