package leo.agents

import leo.datastructures.blackboard.{Event, DataType, Blackboard, Result}

/**
 * Interface to any agent in the architecture.
 * Contains
 * <ul>
 *	<li> Activation Controls, to enable / disable the agent </li>
 *  <li> Offers an execution mechanism to the action of the agent </li>
 *  <li> Support the selection mechanism of tasks for the blackboard </li>
 * </ul>
 */
trait TAgent {
  val name : String

	/**
	* Method to pinpoint the task, that can be currently executed.
	* Most importantly an agent with >0 openTasks will prevent
	* a [[DoneEvent]] from beeing sent.
	*/
  def openTasks : Int

  private var _isActive : Boolean = true

  /**
  * This flag shows, whether an agent should be considered for execution.
  * In this fashion, the agent can not prevent a [[DoneEvent]] from being sent.
  */
  def isActive : Boolean = _isActive
	
	/**
	* Sets the active status.
	*/
  def setActive(a : Boolean) = _isActive = a

  /**
  * This method will ultimately execute a task, if it is selected by the scheduler.
  */
  def run(t : Task) : Result

  /**
  * This method is called, whenever the program is forcefully stopped.
  * It has to be implemented to reset internal stati or the agent cannot simply be terminated.
  */
  def kill()

  /**
  * Registers this agent in the System for execution.
  */
  def register() = Blackboard().registerAgent(this)
  
  /**
  * Unregisteres this agent in the system.
  */
  def unregister() = Blackboard().unregisterAgent(this)

  /**
   * Declares the agents interest in specific data.
   *
   * @return None -> The Agent does not register for any data changes. <br />
   *         Some(Nil) -> The agent registers for all data changes. <br />
   *         Some(xs) -> The agent registers only for data changes for any type in xs.
   */
  def interest : Option[Seq[DataType]]

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
   * @return true if the agent has tasks, false otherwise
   */
  def hasTasks : Boolean

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

  def taskChoosen(t : Task) : Unit
}
