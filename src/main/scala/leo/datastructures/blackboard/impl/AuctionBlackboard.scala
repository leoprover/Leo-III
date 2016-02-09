package leo.datastructures.blackboard.impl


import leo.agents.{TAgent, AgentController, Task}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import scala.collection.mutable

/**
 * This blackboard is a first reference implementation for the @see{Blackboard} interface.
 *
 * It utilizes no doubly added formulas and an auction implementation to access and organize tasks.
 *
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @since 19.08.2015
 */
protected[blackboard] class AuctionBlackboard extends Blackboard {

  /**
   * Register a new Handler for Formula adding Handlers.
   * @param a - The Handler that is to register
   */
  override def registerAgent(a : TAgent) : Unit = {
    TaskSet.addAgent(a)
    freshAgent(a)
  }

  override def unregisterAgent(a: TAgent): Unit = {
    TaskSet.removeAgent(a)
  }

  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask: Iterable[(TAgent,Task)] = TaskSet.getTask

  override def clear() : Unit = {
    dsset.foreach(_.clear())
    TaskSet.clear()
  }

  /**
   * Gives all agents the chance to react to an event
   * and adds the generated tasks.
   *
   * @param t - Function that generates for each agent a set of tasks.
   */
  override def filterAll(t: (TAgent) => Unit): Unit = {
    TaskSet.agents.foreach{ a =>
      t(a)
    }
  }

  override def submitTasks(a : TAgent, ts : Set[Task]) : Unit = {
    TaskSet.synchronized(TaskSet.taskSet.submit(a, ts))
  }

  override def finishTask(t : Task) : Unit = {
    TaskSet.synchronized(TaskSet.taskSet.finish(t))
  }

  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  override protected[blackboard] def freshAgent(a: TAgent): Unit = {
    a.interest match {
      case None => ()
      case Some(xs) =>
        val ts = if(xs.nonEmpty) xs else dsmap.keys.toList
        ts.foreach{t =>
          dsmap.getOrElse(t, Set.empty).foreach{ds =>
            ds.all(t).foreach{d =>
              val ts : Iterable[Task] = a.filter(DataEvent(d,t))
              //ts.foreach(t => ActiveTracker.incAndGet(s"Inserted Task\n  ${t.pretty}"))
              submitTasks(a, ts.toSet)
            }
        }}
        forceCheck()
    }
  }

  override def signalTask() : Unit = TaskSet.signalTask()

  /**
   *
   * @return all registered agents
   */
  override def getAgents(): Iterable[(TAgent,Double)] = TaskSet.regAgents.toSeq

  /**
   * Sends a message to an agent.
   *
   * TODO: Implement without loss of tasks through messages
   *
   * @param m    - The message to send
   * @param to   - The recipient
   */
  override def send(m: Message, to: TAgent): Unit = to.filter(m)

  /**
   * Allows a force check for new Tasks. Necessary for the DoneEvent to be
   * thrown correctly.
   */
  override protected[blackboard] def forceCheck(): Unit = TaskSet.synchronized(TaskSet.notifyAll())


  private val dsset : mutable.Set[DataStore] = new mutable.HashSet[DataStore]
  private val dsmap : mutable.Map[DataType, Set[DataStore]] = new mutable.HashMap[DataType, Set[DataStore]]

  /**
   * Adds a data structure to the blackboard.
   * After this method the data structure will be
   * manipulated by the action of the agents.
   *
   * @param ds is the data structure to be added.
   */
  override def addDS(ds: DataStore): Unit = if(dsset.add(ds)) ds.storedTypes.foreach{t => dsmap.put(t, dsmap.getOrElse(t, Set.empty) + ds)}

  /**
   * Adds a data structure to the blackboard.
   * After this method the data structure will
   * no longer be manipulated by the action of the agent.
   *
   * @param ds is the data structure to be added.
   */
  override def rmDS(ds: DataStore): Unit = if (dsset.remove(ds)) ds.storedTypes.foreach{t => dsmap.put(t, dsmap.getOrElse(t, Set.empty) - ds)}

  /**
   * For the update phase in the executor.
   * Returns a list of all data structures to
   * insert a given type.
   *
   * @param d is the type that we are interested in.
   * @return a list of all data structures, which store this type.
   */
  override protected[blackboard] def getDS(d: DataType): Seq[DataStore] = dsmap.getOrElse(d, Set.empty).toSeq
}





private object TaskSet {

  val regAgents = mutable.HashMap[TAgent,Double]()
  //protected[impl] val execTasks = new mutable.HashSet[Task]

  /**
    * The set containing all dependencies on agents
    */
  val taskSet : TaskSelectionSet = new TaskSelectionSet()

  private val AGENT_SALARY : Double = 5

  /**
   * Notifies process waiting in 'getTask', that there is a new task available.
   */
  protected[blackboard] def signalTask() : Unit = this.synchronized(this.notifyAll())

  def clear() : Unit = {
    this.synchronized {
      regAgents.clear()
      LockSet.clear()
      this.taskSet.clear()
    }
  }

  def addAgent(a : TAgent) {
    this.synchronized(regAgents.put(a,AGENT_SALARY))
    this.synchronized(taskSet.addAgent(a))
  }

  def removeAgent(a : TAgent): Unit = this.synchronized{
    this.synchronized(regAgents.remove(a))
    this.synchronized(taskSet.removeAgent(a))
  }

  def agents : List[TAgent] = this.synchronized(regAgents.toList.map(_._1))


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
  def getTask : Iterable[(TAgent,Task)] = {

    while(!Scheduler().isTerminated) {
      try {
        this.synchronized {
          //        println("Beginning to get items for the auction.")

          //
          // 1. Get all Tasks the Agents want to bid on during the auction with their current money
          //
          var r: List[(Double, TAgent, Task)] = Nil
          while (r.isEmpty) {
            //leo.Out.comment("Checking for new tasks.")
            val ts = taskSet.executableTasks
            ts.foreach { case t =>
              val a = t.getAgent
              val budget = regAgents.getOrElse(a, 0.0)
              r = (t.bid * budget, a, t) :: r  }
            if (r.isEmpty) {
              if (ActiveTracker.isNotActive) {
              //  if(!Scheduler.working() && LockSet.isEmpty && regAgents.forall{case (a,_) => if(!a.hasTasks) {leo.Out.comment(s"[Auction]: ${a.name} has no work");true} else {leo.Out.comment(s"[Auction]: ${a.name} has work");false}}) {
                Blackboard().filterAll { a => a.filter(DoneEvent())}
              }
              //leo.Out.comment("Going to wait for new Tasks.")
              TaskSet.wait()
              regAgents.foreach { case (a, budget) => regAgents.update(a, budget + AGENT_SALARY) }
            }
          }

          //        println("Got tasks and ready to auction.")
          //
          // 2. Bring the Items in Order (sqrt (m) - Approximate Combinatorical Auction, with m - amount of colliding writes).
          //
          // Sort them by their value (Approximate best Solution by : (value) / (sqrt |WriteSet|)).
          // Value should be positive, s.t. we can square the values without changing order
          //
          val queue: List[(Double, TAgent, Task)] = r.sortBy { case (b, a, t) => b * b / t.writeSet().size }

          //        println("Sorted tasks.")

          // 3. Take from beginning to front only the non colliding tasks
          // Check the currenlty executing tasks too.
          var newTask: List[(TAgent, Task)] = Nil
          for ((price, a, t) <- queue) {
            if (LockSet.isExecutable(t)) {
              val budget = regAgents.apply(a)
              if (budget >= price) {
                // The task is not colliding with previous tasks and agent has enough money
                newTask = (a, t) :: newTask
                LockSet.lockTask(t)
                a.taskChoosen(t)
                regAgents.put(a, budget - price)
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
          //        println("Sending "+newTask.size+" tasks to scheduler.")

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
