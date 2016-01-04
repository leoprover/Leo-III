package leo.agents

import leo.datastructures.blackboard.{Event, DataType, Blackboard, Result}

/**
 * <p>
 * Controlling Entity for Reactive Agents. This Controller is a sorting mechanism for
 * maintaining the tasks of a Reactive, Filterlike Agent.
 * </p>
 *
 * <p>
 * The AgentController stores all generated Tasks
 * and passes them to the auction scheduler.
 * The publishing order is determined by the implementation.
 * </p>
 * @param a is the Agent to be controlled.
 */
abstract class AgentController(a : Agent) extends TAgent {
  val name : String = a.name

  def openTasks : Int

  private var _isActive : Boolean = true


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
   * Each task can define a maximum amount of money, they
   * want to posses.
   *
   * A process has to be careful with this barrier, for he
   * may never be doing anything if he has to low money.
   *
   * @return maxMoney
   */
  var maxMoney : Double = 100000

  def taskFinished(t : Task) : Unit = {}

  def taskChoosen(t : Task) : Unit = {}

  override val after : Set[TAgent] = Set.empty
  override val before : Set[TAgent] = Set.empty
}
