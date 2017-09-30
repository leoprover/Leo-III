package leo.agents

import leo.datastructures.Pretty
import leo.datastructures.blackboard._

/**
 * Interface to any agent in the architecture.
 * Contains
 * <ul>
 *	<li> Activation Controls, to enable / disable the agent </li>
 *  <li> Offers an execution mechanism to the action of the agent </li>
 *  <li> Support the selection mechanism of tasks for the blackboard </li>
 * </ul>
 */
trait Agent {
  def name : String

  private var _isActive : Boolean = true

  /**
  * This flag shows, whether an agent should be considered for execution.
  * In this fashion, the agent can not prevent a [[leo.datastructures.blackboard.DoneEvent]] from being sent.
  */
  def isActive : Boolean = _isActive

	/**
	* Sets the active status.
	*/
  def setActive(a : Boolean) : Unit = _isActive = a

  /**
  * This method is called, whenever the program is forcefully stopped.
  * It has to be implemented to reset internal stati or the agent cannot simply be terminated.
  */
  def kill()

//  /**
//  * Registers this agent in the System for execution.
//  */
//  def register() : Unit = Blackboard().registerAgent(this)
//
//  /**
//  * Unregisteres this agent in the system.
//  */
//  def unregister() : Unit = Blackboard().unregisterAgent(this)

  /**
   * Declares the agents interest in specific data.
   *
   * @return None -> The Agent does not register for any data changes. <br />
   *         Some(Nil) -> The agent registers for all data changes. <br />
   *         Some(xs) -> The agent registers only for data changes for any type in xs.
   */
  def interest : Option[Seq[DataType[Any]]]    // TODO Swap None and Some(Nil)

  /**
    * Flags the maximal number of parallel executed tasks
    * per agent.
    *
    * @return Maximal number of executed tasks.
    *         None -> Upper bound is infinite
    *         Some(n) -> The upper bound is n
    */
  def maxParTasks : Option[Int] = None

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
  def filter(event : Event) : Iterable[Task]

  /**
    * Searches the Blackboard for possible tasks on initialization.
    *
    * @return All initial available tasks
    */
  def init() : Iterable[Task]

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
   * <p>
   * This method is called after a task is run and
   * all filter where applied sucessfully
   * </p>
   * <p>
   * The Method is standard implemented as the empty Instruction.
   * </p>
   *
   * @param t The comletely finished task
   */
  def taskFinished(t : Task) : Unit

  /**
    * Method called, when a task is choosen for execution.
    *
    * @param t
    */
  def taskChoosen(t : Task) : Unit

  /**
    * Method called, when a task cannot be executed
    * and is removed from the task set.
    * @param t
    */
  def taskCanceled(t : Task) : Unit // TODO link to the task set!!
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
abstract class Task extends Pretty  {

  def name : String

  def run : Delta

  def readSet : Map[DataType[Any], Set[Any]]

  def writeSet : Map[DataType[Any], Set[Any]]

  /**
    *
    * Set of [[DataType]] touched by this Task.
    *
    * @return all [[DataType]] contained in this task.
    */
  lazy val lockedTypes : Set[DataType[Any]] = readSet.keySet.union(writeSet.keySet)

  /**
    * Checks for two tasks, if they are in conflict with each other.
    *
    * @param t2 - Second Task
    * @return true, iff they collide
    */
  def collide(t2 : Task) : Boolean = {
    val t1 = this
    if(t1 eq t2) return true
    val sharedTypes = t1.lockedTypes.intersect(t2.lockedTypes)  // Restrict to the datatypes both tasks use.
    if(sharedTypes.isEmpty) return false  // Empty case
    sharedTypes.exists{d =>        // There exist datatype, where one of the sets intersects
      val r1 : Set[Any] = t1.readSet.getOrElse(d, Set.empty[Any])
      val w1 : Set[Any] = t1.writeSet.getOrElse(d, Set.empty[Any])
      val r2 : Set[Any] = t2.readSet.getOrElse(d, Set.empty[Any])
      val w2 : Set[Any] = t2.writeSet.getOrElse(d, Set.empty[Any])

      (r1 & w2).nonEmpty || (r2 & w1).nonEmpty || (w1 & w2).nonEmpty
    }
  }

  /**
    *
    * Tests if this blocks the execution of t2,
    * that is this.readSet & t2.writeSet is not empty
    *
    * @param t2 The task, that blocks t2
    * @return true iff this blocks t2
    */
  def blockes(t2 : Task) : Boolean = {
    val t1 = this
    val sharedTypes = t1.lockedTypes.intersect(t2.lockedTypes)
    if(sharedTypes.isEmpty) return false
    sharedTypes.exists { d =>
      val r1: Set[Any] = t1.readSet.getOrElse(d, Set.empty[Any])
      val w2: Set[Any] = t2.writeSet.getOrElse(d, Set.empty[Any])
      (r1 & w2).nonEmpty
    }
  }

  def bid : Double

  def getAgent : Agent
}

/**
  * Defines dependencies between data considered for execution.
  *
  * For any data contained in `before` should be exeuted before this object.
  * Any data in `after` should be executed after this object.
  *
  * @tparam A - arbitrary Types
  */
trait Dependency[A] {
  /**
    * A set of all data, that should be executed before this object.
    *
    * @return all data to be executed before
    */
  def before : Set[A]

  /**
    * A set of all data, that should be executed after this object.
    *
    * @return all data to be executed afterwards
    */
  def after : Set[A]
}