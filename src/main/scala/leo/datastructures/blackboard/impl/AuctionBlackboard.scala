package leo.datastructures.blackboard.impl


import leo.agents.{AgentController, Task}
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
  override def registerAgent(a : AgentController) : Unit = {
    TaskSet.addAgent(a)
    freshAgent(a)
  }

  override def unregisterAgent(a: AgentController): Unit = {
    TaskSet.removeAgent(a)
  }

  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask: Iterable[(AgentController,Task)] = TaskSet.getTask

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
  override def filterAll(t: (AgentController) => Unit): Unit = {
    TaskSet.agents.foreach{ a =>
      t(a)
    }
  }


  /**
   * Removes a task from the list of currently executed tasks.
   *
   * @param t - The finished task.
   */
  override def finishTask(t : Task) : Unit = TaskSet.finishTask(t)

  override def getRunningTasks() : Iterable[Task] = TaskSet.execTasks.toList

  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  override protected[blackboard] def freshAgent(a: AgentController): Unit = {
    a.interest match {
      case None => ()
      case Some(xs) =>
        val ts = if(xs.nonEmpty) xs else dsmap.keys.toList
        ts.foreach{t =>
          dsmap.getOrElse(t, Set.empty).foreach{ds =>
            ds.all(t).foreach{d =>
              a.filter(DataEvent(d,t))
            }
        }}
        forceCheck()
    }
  }

  override def signalTask() : Unit = TaskSet.signalTask()

  override def collision(t : Task) : Boolean = TaskSet.collision(t)

  /**
   *
   * @return all registered agents
   */
  override def getAgents(): Iterable[(AgentController,Double)] = TaskSet.regAgents.toSeq

  /**
   * Sends a message to an agent.
   *
   * TODO: Implement without loss of tasks through messages
   *
   * @param m    - The message to send
   * @param to   - The recipient
   */
  override def send(m: Message, to: AgentController): Unit = to.filter(m)

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

  val regAgents = mutable.HashMap[AgentController,Double]()
  protected[impl] val execTasks = new mutable.HashSet[Task]

  private val AGENT_SALARY : Double = 5

  /**
   * Notifies process waiting in 'getTask', that there is a new task available.
   */
  protected[blackboard] def signalTask() : Unit = this.synchronized(this.notifyAll())

  def clear() : Unit = {
    this.synchronized {
      regAgents.foreach(_._1.clearTasks())
      regAgents.clear()
      execTasks.clear()
    }
  }

  def addAgent(a : AgentController) {
    this.synchronized(regAgents.put(a,AGENT_SALARY))
  }

  def removeAgent(a : AgentController): Unit = this.synchronized{
    regAgents.remove(a)
  }

  private[impl] def finishTask(t : Task) = synchronized(execTasks.remove(t))

  def agents : List[AgentController] = this.synchronized(regAgents.toList.map(_._1))


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
  def getTask : Iterable[(AgentController,Task)] = {

    while(!Scheduler().isTerminated) {
      try {
        this.synchronized {
          //        println("Beginning to get items for the auction.")

          //
          // 1. Get all Tasks the Agents want to bid on during the auction with their current money
          //
          var r: List[(Double, AgentController, Task)] = Nil
          while (r.isEmpty) {
            //leo.Out.comment("Checking for new tasks.")
            regAgents.foreach { case (a, budget) => if (a.isActive) a.getTasks(budget).foreach { t => r = (t.bid(budget), a, t) :: r } }
            if (r.isEmpty) {
              if (!Scheduler.working() && execTasks.isEmpty && regAgents.forall { case (a, _) => !a.hasTasks }) {
              //  if(!Scheduler.working() && execTasks.isEmpty && regAgents.forall{case (a,_) => if(!a.hasTasks) {leo.Out.comment(s"[Auction]: ${a.name} has no work");true} else {leo.Out.comment(s"[Auction]: ${a.name} has work");false}}) {
                Blackboard().filterAll { a => a.filter(DoneEvent()) }
              }
              // TODO increase budget or we will run into a endless loop
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
          val queue: List[(Double, AgentController, Task)] = r.sortBy { case (b, a, t) => b * b / t.writeSet().size }

          //        println("Sorted tasks.")

          // 3. Take from beginning to front only the non colliding tasks
          // Check the currenlty executing tasks too.
          var newTask: List[(AgentController, Task)] = Nil
          for ((price, a, t) <- queue) {
            if (!newTask.exists { e => t.collide(e._2) } && !collision(t)) {
              val budget = regAgents.apply(a)
              if (budget >= price) {
                // The task is not colliding with previous tasks and agent has enough money
                newTask = (a, t) :: newTask
                execTasks.add(t)
                regAgents.put(a, budget - price)
              }
            }
          }

          //        println("Choose optimal.")

          //
          // 4. After work pay salary, tell colliding and return the tasks
          //
          for ((a, b) <- regAgents) {
            if (a.maxMoney - b > AGENT_SALARY) {
              regAgents.put(a, b + AGENT_SALARY)
            }
            a.removeColliding(newTask.map(_._2))
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




  /**
   * Checks if a Task collides with the current executing ones.
   *
   * @param t - Task that could be executed
   *
   * @return true, iff the task collides
   */
  def collision(t : Task) : Boolean = this.synchronized(execTasks.exists{e => t.collide(e)})


}
