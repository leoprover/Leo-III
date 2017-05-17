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
    taskSelectionSet.taskSet.submit(ts)  // TODO Synchronizing?
    signalTask()      // WHO HAS THE LOCK?=????
  }

  override def finishTask(t : Task) : Unit = {
    taskSelectionSet.taskSet.finish(t)     // TODO synchronizing?
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

  override def signalTask() : Unit = taskSelectionSet.signalTask()

  /**
   *
   * @return all registered agents
   */
  override def getAgents(): Iterable[(Agent,Double)] = taskSelectionSet.regAgents.toSeq

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
  private var taskSelectionSet : TaskSelectionSet = null

  private[blackboard] def setScheduler(scheduler : Scheduler) : Unit = {
    this.scheduler = scheduler
    this.taskSelectionSet = new TaskSelectionSet(this, scheduler)
  }
}





private class TaskSelectionSet(blackboard: Blackboard, scheduler: Scheduler) {

  // Number of tasks to queue in the scheduler = taskTakenFactor * ThreadCount
  val taskTakenFactor : Int = 2

  val regAgents = mutable.HashMap[Agent,Double]()
  //protected[impl] val execTasks = new mutable.HashSet[Task]

  /**
    * The set containing all dependencies on agents
    */
  val taskSet : SimpleTaskSet = new SimpleTaskSet(blackboard)
  private val AGENT_SALARY : Double = 5   // TODO changeable

  /**
   * Notifies process waiting in 'getTask', that there is a new task available.
   */
  protected[blackboard] def signalTask() : Unit = {
//    println("In signal. Before")
    this.synchronized{this.notifyAll()}
//    println("In signal. After")
  }

  def clear() : Unit = {
    this.synchronized {
      regAgents.clear()
      LockSet.clear()
      this.taskSet.clear()
    }
  }

  def addAgent(a: Agent) {
    this.synchronized(regAgents.put(a,AGENT_SALARY))
    this.synchronized(taskSet.addAgent(a))
  }

  def removeAgent(a: Agent): Unit = this.synchronized{
    this.synchronized(regAgents.remove(a))
    this.synchronized(taskSet.removeAgent(a))
  }

  def agents : List[Agent] = this.synchronized(regAgents.toList.map(_._1))

  private def sendDoneEvents() : Unit = {
    val agents = regAgents.toList.map(_._1).toIterator
    while(agents.hasNext){
      val a = agents.next()
      taskSet.submit(a.filter(DoneEvent))
    }
  }

  private val waitForAll : Boolean = true

  /**
   * Gets from any active agent the set of tasks, he wants to execute with his current budget.
   *
   * If the set of tasks is empty he waits until something is added
   * (filter should call signalTask).
   *
   * Of this set we play
   *
   * @return
   */
  def getTask : Iterable[(Agent,Task)] = {

    while(!scheduler.isTerminated) {
      try {
        this.synchronized {

          //
          // 1. Get all Tasks the Agents want to bid on during the auction with their current money
          //
          var r: List[(Double, Agent, Task)] = Nil
          while(waitForAll && scheduler.getActiveWork > 0) {
            wait()
          }
          while (r.isEmpty) {
            val ts = taskSet.executableTasks    // TODO Filter if no one can execute (simple done)
//            println(s"ts = ${ts.map(_.pretty).mkString(", ")}")
            ts.foreach { case t =>
              val a = t.getAgent
              val budget = regAgents.getOrElse(a, 0.0)
              r = (t.bid * budget, a, t) :: r  }
            if (r.isEmpty) {
              if (ActiveTracker.get <= 0 && scheduler.getCurrentWork <= 0) {
              //  if(!Scheduler.working() && LockSet.isEmpty && regAgents.forall{case (a,_) => if(!a.hasTasks) {leo.Out.comment(s"[Auction]: ${a.name} has no work");true} else {leo.Out.comment(s"[Auction]: ${a.name} has work");false}}) {
                sendDoneEvents()
              }
              wait()
              regAgents.foreach { case (a, budget) => regAgents.update(a, math.max(budget, budget + AGENT_SALARY)) }
            }
          }


          // println("Got tasks and ready to auction.")
          //
          // 2. Bring the Items in Order (sqrt (m) - Approximate Combinatorical Auction, with m - amount of colliding writes).
          //
          // Sort them by their value (Approximate best Solution by : (value) / (sqrt |WriteSet|)).
          // Value should be positive, s.t. we can square the values without changing order
          //
          val queue: Iterator[(Double, Agent, Task)] = r.sortBy { case (b, a, t) => b * b / (1+t.writeSet.size) }.reverse.toIterator    // TODO Order in reverse directly
          //println(s" Sorted Queue : \n   ${queue.map{case (b, _, t) => s"${t.pretty} -> ${b}"}.mkString("\n   ")}")
          var taken : Map[Agent, Int] = HashMap[Agent, Int]()

          var openSlots = scheduler.numberOfThreads * taskTakenFactor - scheduler.openTasks
          // 3. Take from beginning to front only the non colliding tasks
          // Check the currenlty executing tasks too.
          var newTask: Seq[(Agent, Task)] = Nil
          while(queue.hasNext && openSlots > 0){
            val (price, a, t) = queue.next()
            // Check if the agent can execute another task
            val open : Boolean = a.maxParTasks.fold(true)(n => n - taskSet.executingTasks(a) + taken.getOrElse(a, 0) > 0)
            if (open & LockSet.isExecutable(t)) {
              val budget = regAgents.getOrElse(a, 0.0)     //TODO Lock regAgents, got error during phase switch
              if (budget >= price) {
                // The task is not colliding with previous tasks and agent has enough money
                openSlots -= 1
                newTask = (a, t) +: newTask
                LockSet.lockTask(t)
                a.taskChoosen(t)
                regAgents.put(a, budget - price)
                taken = taken + (a -> (taken.getOrElse(a,0)+1))
              }
            }
          }

          taskSet.commit(newTask.map(_._2).toSet)

          //        println("Choose optimal.")

          //
          // 4. After work pay salary, tell colliding and return the tasks
          //
          for ((a, b) <- regAgents) {
            if (a.maxMoney - b > AGENT_SALARY) {
              regAgents.put(a, b + AGENT_SALARY)
            }
          }
//                  println("Sending "+newTask.size+" tasks to scheduler.")
          return newTask
        }
        //Lastly interrupt recovery
      } catch {
        case e : InterruptedException => Thread.currentThread().interrupt()
        case e : Exception => throw e
      }
    }
    return Nil
  }

}
