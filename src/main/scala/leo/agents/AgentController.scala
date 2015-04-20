package leo.agents

import leo.datastructures.blackboard.{Event, DataType, Blackboard, Result}

/**
 * <p>
 * Controlling Entity for an Agent.
 * </p>
 *
 * <p>
 * The AgentController stores all generated Tasks
 * and passes them to the auction scheduler.
 * The publishing order is determined by the implementation.
 * </p>
 * @param a is the Agent to be controlled.
 */
abstract class AgentController(a : Agent) {
  val name : String = a.name

  def openTasks : Int

  private var _isActive : Boolean = true

  def isActive : Boolean = _isActive

  def setActive(a : Boolean) = _isActive = a

  def run(t : Task) : Result = {
    //    Out.comment(s"[$name]: Executing task\n    ${t.pretty}.")
    a.run(t)
  }

  def register() = Blackboard().registerAgent(this)
  def unregister() = Blackboard().unregisterAgent(this)

  def kill() = a.kill()

  /**
   * Declares the agents interest in specific data.
   *
   * @return None -> The Agent does not register for any data changes. <br />
   *         Some(Nil) -> The agent registers for all data changes. <br />
   *         Some(xs) -> The agent registers only for data changes for any type in xs.
   */
  lazy val interest : Option[Seq[DataType]] = a.interest

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
  var maxMoney : Double = 100000

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
