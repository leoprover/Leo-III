package leo
package agents

import leo.datastructures.blackboard.{Event, FormulaStore, Blackboard}
import leo.datastructures.context.Context

import scala.collection.mutable

/**
 * <p>
 * Interface for all Agent Implementations.
 * </p>
 *
 * <p>
 * The Agent itself is not a Thread, but a function to be called, at any
 * time its guard is satisfied.
 * </p>
 *
 * <p>
 * To be considered to the change of an guard an implementing class
 * must register to at least on of the blackboard triggers. Copies
 * of pointers to objects in the blackboard will not be considered
 * for a change of the guard.
 *
 * This holds unless the guard is simply true, then the agent can be executed
 * at any time.
 * </p>
 * @author Max Wisniewski
 * @since 5/14/14
 */
abstract class Agent {

  /**
   *
   * @return the name of the agent
   */
  def name : String

  /**
   *
   * @return number of tasks, the agent can currently work on
   */
  def openTasks : Int

  private var _isActive : Boolean = false

  /**
   * Returns the agents active status.
   * If true, he is considered in the task auction.
   *
   * @return the activity status of the agent
   */
  def isActive : Boolean = _isActive

  /**
   * Sets the agent to a new status, thus (un-)considered by the scheduler
   * @param a - The new activity status
   */
  def setActive(a : Boolean) = {
    _isActive = a
  }

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t : Task) : Result

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  def register() {
    Blackboard().registerAgent(this)
    setActive(true)
  }


  /*
--------------------------------------------------------------------------------------------
                        COMBINATORICAL AUCTION
--------------------------------------------------------------------------------------------
   */


  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate tasks from it,
   * that will be stored in the Agent.
   *
   * @param event - Newly added or updated formula
   */
  def filter(event : Event) : Unit


  /**
   *
   * Returns a a list of Tasks, the Agent can afford with the given budget.
   *
   * @param budget - Budget that is granted to the agent.
   */
  def getTasks(budget : Double) : Iterable[Task]

  /**
   * Each task can define a maximum amount of money, they
   * want to posses.
   *
   * A process has to be careful with this barrier, for he
   * may never be doing anything if he has to low money.
   *
   * @return maxMoney
   */
  def maxMoney : Double

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  def getAllTasks : Iterable[Task]

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  def removeColliding(nExec : Iterable[Task]) : Unit

  /**
   * Removes all Tasks
   */
  def clearTasks() : Unit
}


abstract class FifoAgent extends Agent {

  protected def toFilter(event : Event) : Iterable[Task]

  override def setActive(a : Boolean) = {
    super.setActive(a)
    if(a && q.nonEmpty) Blackboard().signalTask()
   }

  override def openTasks : Int = q.size

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  override def register() {
    Blackboard().registerAgent(this)
    setActive(true)
  }

  protected val q : mutable.Queue[Task] = new mutable.Queue[Task]()

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filter(f: Event) : Unit = {
    var done = false
    for(t <- toFilter(f)) {
      if (!Blackboard().collision(t)) {
//        println(name+" : Got a task.")
        q.synchronized {
          q.enqueue(t)
        }
        done = true
      }
    }
    if(done) {
//      println(name+" : Has now "+q.size+" task queued.")
      Blackboard().signalTask()
    }
  }

  override val maxMoney : Double = 2000

  /**
   *
   * Returns a a list of Tasks, the Agent can afford with the given budget.
   *
   * @param budget - Budget that is granted to the agent.
   */
  override def getTasks(budget: Double): Iterable[Task] = {
    var erg = List[Task]()
    var costs : Double = 0
    q.synchronized {
      for (t <- q) {
        if (costs > budget) return erg
        else {
          costs += t.bid(budget)
          erg = t :: erg
        }
      }
    }
//    println(name+ " : Send "+erg.size+" tasks to Auction.")
    erg
  }

  /**
   * Removes all Tasks
   */
  override def clearTasks(): Unit = q.synchronized(q.clear())

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = q.synchronized(q.iterator.toIterable)

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = q.synchronized(q.dequeueAll{tbe => nExec.exists(_.collide(tbe))})
}

abstract class PriorityAgent extends Agent {

  private var _isActive : Boolean = true

  override def isActive : Boolean = _isActive

  override def setActive(a : Boolean) = {
    super.setActive(a)
    if(a && q.nonEmpty) Blackboard().signalTask()
  }

  // Sort by a fixed amount of money
  protected var q : mutable.PriorityQueue[Task] = new mutable.PriorityQueue[Task]()(Ordering.by{(x : Task) => x.bid(100)})


  override def openTasks : Int = synchronized(q.size)

  /**
   * Internal method called from the filter method. Specific to the agent.
   *
   * @param event - The event that triggered the filter
   * @return A sequence of new tasks, to be added to the internal priority queue.
   */
  protected def toFilter(event : Event) : Iterable[Task]

  /**
   * Calls the internal toFilter method and inserts all generated tasks to the priority queue.
   *
   * @param f - Raised Event.
   */
  override def filter(f: Event) : Unit = {
    var done = false
    val it = toFilter(f).iterator
    while(it.hasNext) {
      val t = it.next()
      if (!Blackboard().collision(t)) {
        q.synchronized {
          q.enqueue (t)
        }
        done = true
      }
    }
    if(done) {
      Blackboard().signalTask()
    }
  }

    /**
     *
     * Returns a a list of Tasks, the Agent can afford with the given budget.
     *
     * @param budget - Budget that is granted to the agent.
     */
  override def getTasks(budget: Double): Iterable[Task] = {
    var erg = List[Task]()
    var costs : Double = 0
    q.synchronized {
      for (t <- q) {
        // TODO Change to iterator since for is inefficient
        if (costs > budget) return erg
        else {
          costs += t.bid(budget)
          erg = t :: erg
        }
      }
    }
    erg
  }

  /**
   * Removes all Tasks
   */
  override def clearTasks(): Unit = q.synchronized {q.clear()}

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = synchronized(q.iterator.toIterable)

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = {
    synchronized {
      q = q.filterNot { tbe =>
        if(nExec.exists(_.collide(tbe))){
          nExec.find(e => e.collide(tbe) && !(e.equals(tbe))).fold{()}{e => Out.trace("Collision detected:\n "+tbe.toString+"\n collided with "+e.toString)}
          true
        }else {
          false
        }
      }
    }
  }

}




/**
 * Common trait for all Agent Task's. Each agent specifies the
 * work it can do.
 *
 * The specific fields and accessors for the real task will be in
 * the implementation.
 *
 * @author Max Wisniewski
 * @since 6/26/14
 */
abstract class Task {

  /**
   *
   * Returns a set of all Formulas that are read for the task.
   *
   * @return Read set for the Task.
   */
  def readSet() : Set[FormulaStore]

  /**
   *
   * Returns a set of all Formulas, that will be written by the task.
   *
   * @return Write set for the task
   */
  def writeSet() : Set[FormulaStore]

  /**
   * Defines a set of Contexts on which the task will
   * write.
   *
   * @return set of all contexts the task will manipulate
   */
  def contextWriteSet() : Set[Context] = Set.empty

  /**
   * Checks for two tasks, if they are in conflict with each other.
   *
   * @param t2 - Second Task
   * @return true, iff they collide
   */
  def collide(t2 : Task) : Boolean = {
    val t1 = this
    if(t1 equals t2) true
    else {
      !t1.readSet().intersect(t2.writeSet()).isEmpty ||
        !t2.readSet().intersect(t1.writeSet()).isEmpty ||
        !t2.writeSet().intersect((t1.writeSet())).isEmpty ||
        !t2.contextWriteSet().intersect((t1.contextWriteSet())).isEmpty
    }
  }

  /**
   *
   * Defines the gain of a Task, defined for
   * a specific agent.
   *
   * @return - Possible profit, if the task is executed
   */
  def bid(budget : Double) : Double
}

/**
 * Common Trait, for the results of tasks.
 *
 * @author Max Wisniewski
 * @since 6/26/12
 */
trait Result {

  /**
   * A set of new formulas created by the task.
   *
   * @return New formulas to add
   */
  def newFormula() : Set[FormulaStore]

  /**
   * A mapping of formulas to be changed.
   *
   * @return Changed formulas
   */
  def updateFormula() : Map[FormulaStore, FormulaStore]

  /**
   * A set of formulas to be removed.
   *
   * @return Deleted formulas
   */
  def removeFormula() : Set[FormulaStore]

  /**
   * Set of all new or updated Contexts
   * @return
   */
  def updatedContext() : Set[Context]
}

/**
 * Simple container for the implementation of result.
 *
 * @param nf - New formulas
 * @param uf - Update formulas
 * @param rf - remove Formulas
 */
class StdResult(nf : Set[FormulaStore], uf : Map[FormulaStore,FormulaStore], rf : Set[FormulaStore]) extends Result{
  override def newFormula() : Set[FormulaStore] = nf
  override def updateFormula() : Map[FormulaStore,FormulaStore] = uf
  override def removeFormula() : Set[FormulaStore] = rf
  override def updatedContext() : Set[Context] = Set.empty
}

object EmptyResult extends Result{
  override def newFormula() : Set[FormulaStore] = Set.empty
  override def updateFormula() : Map[FormulaStore,FormulaStore] = Map.empty
  override def removeFormula() : Set[FormulaStore] = Set.empty
  override def updatedContext(): Set[Context] = Set.empty
}