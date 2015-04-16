package leo.datastructures.blackboard

import leo.agents.{AgentController, Task, Agent}
import leo.datastructures.context.Context
import leo.modules.output.StatusSZS

// Singleton Blackboards
object Blackboard {
  private val single : Blackboard = new impl.AuctionBlackboard()

  def apply() : Blackboard = single
}

/**
 *
 * <p>
 * A blackboard is a central data collection object that supports
 * synchronized access between multiple processes.
 *  </p>
 *
 *  <p>
 *  The implementation decides over the fairness and order of exession of the
 *  processes.
 *  </p>
 *
 *  <p>
 *  IMPORTANT: CHANGE FROM TPTP to the internal used Representation as
 *  soon as they are ready.
 *  </p>
 *
 * @author Max Wisniewski
 * @since 29.04.2014
 */
trait Blackboard extends TaskOrganize with DataBlackboard with MessageBlackboard {
  /**
   * Resets the blackboard to an initial state.
   */
  def clear()
}



/**
 * Subtrait of the Blackboard, responsible for the
 * organization of tasks and agents. Not visible outside the
 * blackboard package except the agentRegistering.
 */
trait TaskOrganize {


  /**
   * Gives all agents the chance to react to an event
   * and adds the generated tasks.
   *
   * @param t - Function that generates for each agent a set of tasks.
   */
  def filterAll(t : AgentController => Unit) : Unit


  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  protected[blackboard] def freshAgent(a : AgentController) : Unit

  /**
   *
   * Starts a new auction for agents to buy computation time
   * for their tasks.
   *
   * The result is a set of tasks, that can be executed in parallel
   * and approximate the optimal combinatorical auction.
   *
   * @return Not yet executed noncolliding set of tasks
   */
  protected[blackboard] def getTask : Iterable[(AgentController,Task)]



  /**
   * Tells the tassk set, that one task has finished computing.
   *
   * @param t - The finished task.
   */
  protected[blackboard] def finishTask(t : Task) : Unit

  /**
   * Allows a force check for new Tasks. Necessary for the DoneEvent to be
   * thrown correctly.
   */
  protected[blackboard] def forceCheck() : Unit

  /**
   * Signal Task is called, when a new task is available.
   */
  def signalTask() : Unit

  /**
   * Checks through the current executing threads, if one is colliding
   *
   * @param t - Task that will be tested
   * @return true, iff no currently executing task collides
   */
  def collision(t : Task) : Boolean

  /**
   * Registers an agent to the blackboard, should only be called by the agent itself
   *
   * @param a - the new agent
   */
  def registerAgent(a : AgentController) : Unit

  /**
   * Removes an agent from the notification lists.
   *
   * Recomended if the agent will be used nevermore. Otherwise
   * a.setActive(false) should be used.
   *
   * This method should be called solely from the agent.
   *
   * @param a the agent to be unregistered.
   */
  def unregisterAgent(a : AgentController) : Unit

  /**
   *
   * Returns for debugging and interactive use the agent work
   *
   * @return all registered agents and their budget
   */
  def getAgents : Iterable[(AgentController, Double)]

  /**
   * Returns a collection of tasks that are currently executed
   * in the system. Debugging reasons only!!!
   *
   * @return Collection of tasks that are executed.
   */
  def getRunningTasks : Iterable[Task]
}

/**
 * The DataBlackboard handels publishing of data structures
 * through the blackboard and the execution interface.
 */
trait DataBlackboard {

  /**
   * Adds a data structure to the blackboard.
   * After this method the data structure will be
   * manipulated by the action of the agents.
   *
   * @param ds is the data structure to be added.
   */
  def addDS(ds : DataStore)

  /**
   * Adds a data structure to the blackboard.
   * After this method the data structure will
   * no longer be manipulated by the action of the agent.
   *
   * @param ds is the data structure to be added.
   */
  def rmDS(ds : DataStore)

  /**
   * For the update phase in the executor.
   * Returns a list of all data structures to
   * insert a given type.
   *
   * @param d is the type that we are interested in.
   * @return a list of all data structures, which store this type.
   */
  protected[blackboard] def getDS(d : DataType) : Seq[DataStore]

}

/**
 * This trait capsules the message handling for the blackboard
 */
trait MessageBlackboard {
  /**
   * Sends a message to an agent.
   *
   * @param m    - The message to send
   * @param to   - The recipient
   */
  def send(m : Message, to : AgentController)
}