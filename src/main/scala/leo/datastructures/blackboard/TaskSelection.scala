package leo.datastructures.blackboard

import java.util.concurrent.atomic.AtomicLong

import leo.Configuration
import leo.agents.{Agent, Task}
import leo.datastructures.blackboard.impl.SimpleTaskSet
import leo.datastructures.blackboard.scheduler.Scheduler

import scala.collection.immutable.HashMap
import scala.collection.mutable

object TaskSelection {
  def getFreshTaskSelection(blackboard: Blackboard, scheduler: Scheduler) : TaskSelection = {
    if(Configuration.isSetTo("scheduling", "fifo")) {
      new FifoTaskSelection(blackboard, scheduler)
    } else {
      new AuctionTaskSelection(blackboard, scheduler)
    }
  }
}

trait TaskSelection {

  /**
    * Stores the multiple amount of tasks to be given to the execution.
    */
  protected val taskTakenFactor: Int = 2

  protected def sendDoneEvents(): Unit = {
    val ra = agents.iterator
    while (ra.hasNext) {
      val a = ra.next()
      taskSet.submit(a.filter(DoneEvent))
    }
  }

  protected def taskSet : TaskSet

  /**
    * Adds an agent to the set of scheduled agents.
    *
    * @param a the new agent
    */
  def addAgent(a: Agent): Unit

  /**
    * Removes an agent from the set of scheduled agents.
    *
    * @param a the agent to be removed
    */
  def removeAgent(a: Agent): Unit

  /**
    * All currently registered agents
    *
    * @return currently registered agents
    */
  def agents: List[Agent]

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
  def getTask: Iterable[(Agent, Task)]

  /**
    * Clears tasks and agents from the set.
    * Returns the datastructure to a fresh state.
    */
  def clear(): Unit

  /**
    * Submits a set of tasks to the scheduling.
    *
    * @param ts the set of tasks to be executed
    */
  def submit(ts: Set[Task]): Unit

  /**
    * Marks a set of Tasks as finished. Enables other blocked tasks.
    */
  def finish(t: Task): Unit

  /**
    * Signal the selection to trigger a new round of selecting tasks.
    */
  def signalChange(): Unit = {
    this.synchronized {
      this.notifyAll()
    }
  }

  /**
    * Prints information on the workload in the TaskSelection
    */
  def info() : Unit
}

class FifoTaskSelection(blackboard: Blackboard, scheduler: Scheduler) extends TaskSelection {
  override protected val taskSet: SimpleTaskSet = new SimpleTaskSet(blackboard)
  private var regAgents : mutable.Set[Agent] = new mutable.HashSet[Agent]()

  override def addAgent(a: Agent): Unit = synchronized{
    regAgents.add(a)
    taskSet.addAgent(a)
  }

  override def removeAgent(a: Agent): Unit = synchronized{
    regAgents.remove(a)
    taskSet.removeAgent(a)
  }

  override def agents: List[Agent] = synchronized(regAgents.toList)

  override def clear(): Unit = synchronized{
    taskSet.clear()
    LockSet.clear()
    regAgents.clear()
  }

  private val schedulingTime : AtomicLong = new AtomicLong(0)
  private val schedulingAmount : AtomicLong = new AtomicLong(0)


  override def submit(ts: Set[Task]): Unit = {
    taskSet.submit(ts)
  }

  override def finish(t: Task): Unit = {
    taskSet.finish(t)
  }


  private val waitForAll: Boolean = true

  override def getTask: Iterable[(Agent, Task)] = {
    while (!scheduler.isTerminated) {
      try {
        this.synchronized {
          schedulingAmount.incrementAndGet()
          // Block until tasks can be choosen
          var r: Iterator[Task] = Iterator()
          while (waitForAll && scheduler.getActiveWork > 0) {
            wait()
          }
          while (!r.hasNext) {
            val start = System.currentTimeMillis()
            r = taskSet.executableTasks
            if (!r.hasNext) {
              if (ActiveTracker.get <= 0 && scheduler.getCurrentWork <= 0) {
                sendDoneEvents()
              }
              val time = System.currentTimeMillis() - start
              schedulingTime.addAndGet(time)
              wait()
            } else {
              val time = System.currentTimeMillis() - start
              schedulingTime.addAndGet(time)
            }
          }

          val start = System.currentTimeMillis()
          // Select the first open tasks to be executed
          val queue: Iterator[Task] = r

          var openSlots = scheduler.numberOfThreads * taskTakenFactor - scheduler.openTasks
          // 3. Take from beginning to front only the non colliding tasks
          // Check the currenlty executing tasks too.
          var newTask: Seq[(Agent, Task)] = Nil
//          println("Check tasks")
          while (queue.hasNext && openSlots > 0) {
            val t = queue.next()
//            println(s"\n  ${t.name}")
            val a = t.getAgent
            // Check if the agent can execute another task
            if (LockSet.isExecutable(t)) {
              // The task is not colliding with previous tasks and agent has enough money
              openSlots -= 1
              newTask = (a, t) +: newTask
              LockSet.lockTask(t)
              a.taskChoosen(t)
            }
          }
          val time = System.currentTimeMillis() - start
          schedulingTime.addAndGet(time)

          taskSet.commit(newTask.map(_._2).toSet)
          //                  println("Sending "+newTask.size+" tasks to scheduler.")
//          println(s"Select : \n${newTask.map(_._2.name).mkString("\n  ")} ")
          return newTask
        }
        //Lastly interrupt recovery
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt()
        case e: Exception => throw e
      }
    }
    Iterable()
  }

  override def info(): Unit = {
    val sb = new mutable.StringBuilder()

    val sct = schedulingTime.get()
    val sca = schedulingAmount.get()
    val scm = if(sca == 0) 0.0 else (sct * 1.0) / sca


    sb.append("\n\n  Scheduler\n")
    sb.append(s"    Schedule : ${sca} times, ${sct}ms time, ${scm}ms mean\n")
    taskSet.info()

    leo.Out.comment(sb.toString())
  }
}


class AuctionTaskSelection(blackboard: Blackboard, scheduler: Scheduler) extends TaskSelection {
  private val regAgents = mutable.HashMap[Agent, Double]()
  /**
    * The set containing all dependencies on agents
    */
  override protected val taskSet: SimpleTaskSet = new SimpleTaskSet(blackboard)
  private val AGENT_SALARY: Double = 5 // TODO changeable


  def clear(): Unit = {
    this.synchronized {
      regAgents.clear()
      LockSet.clear()
      this.taskSet.clear()
    }
  }

  def addAgent(a: Agent): Unit = {
    this.synchronized(regAgents.put(a, AGENT_SALARY))
    this.synchronized(taskSet.addAgent(a))
  }

  def removeAgent(a: Agent): Unit = this.synchronized {
    this.synchronized(regAgents.remove(a))
    this.synchronized(taskSet.removeAgent(a))
  }


  override def submit(ts: Set[Task]): Unit = taskSet.submit(ts)

  override def finish(t: Task): Unit = taskSet.finish(t)

  def agents: List[Agent] = this.synchronized(regAgents.toList.map(_._1))

  private val waitForAll: Boolean = true

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
  def getTask: Iterable[(Agent, Task)] = {

    while (!scheduler.isTerminated) {
      try {
        this.synchronized {

          //
          // 1. Get all Tasks the Agents want to bid on during the auction with their current money
          //
          var r: List[(Double, Agent, Task)] = Nil
          while (waitForAll && scheduler.getActiveWork > 0) {
            wait()
          }
          while (r.isEmpty) {
            val ts = taskSet.executableTasks
            ts.foreach { t =>
              val a = t.getAgent
              val budget = regAgents.getOrElse(a, 0.0)
              r = (t.bid * budget, a, t) :: r
            }
            if (r.isEmpty) {
              if (ActiveTracker.get <= 0 && scheduler.getCurrentWork <= 0) {
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
          val queue: Iterator[(Double, Agent, Task)] = r.sortBy { case (b, a, t) => b * b / (1 + t.writeSet.size) }.reverse.iterator // TODO Order in reverse directly
          //println(s" Sorted Queue : \n   ${queue.map{case (b, _, t) => s"${t.pretty} -> ${b}"}.mkString("\n   ")}")

          var openSlots = scheduler.numberOfThreads * taskTakenFactor - scheduler.openTasks
          // 3. Take from beginning to front only the non colliding tasks
          // Check the currenlty executing tasks too.
          var newTask: Seq[(Agent, Task)] = Nil
          while (queue.hasNext && openSlots > 0) {
            val (price, a, t) = queue.next()
            // Check if the agent can execute another task
            if (LockSet.isExecutable(t)) {
              val budget = regAgents.getOrElse(a, 0.0) //TODO Lock regAgents, got error during phase switch
              if (budget >= price) {
                // The task is not colliding with previous tasks and agent has enough money
                openSlots -= 1
                newTask = (a, t) +: newTask
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
          //                  println("Sending "+newTask.size+" tasks to scheduler.")
          return newTask
        }
        //Lastly interrupt recovery
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt()
        case e: Exception => throw e
      }
    }
    Nil
  }

  override def info(): Unit = ???
}

