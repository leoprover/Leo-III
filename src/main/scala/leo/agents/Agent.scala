package leo.agents

import leo.datastructures.blackboard.Blackboard

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
trait Agent {

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def apply() : Unit

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   * @param blackboard - The Blackboard the Agent will work on
   */
  def register(blackboard : Blackboard)

  /**
   * Takes the current state of the Blackboard or variables set by
   * TriggerHandlers to check whether to execute the agent.
   * @return true if the agent can be executed, otherwise false.
   */
  def guard() : Boolean

  /**
   * Method that cancels an execution and possibly reverts its changes.
   */
  def cancel() : Unit

  /**
   * <p>
   * Wakes Up an Observer after a change.
   * </p>
   * <p>
   * What happened during the change can be
   * given to the observer in a specialization.
   * </p>
   */
  def wakeUp() : Unit

  /**
   * <p>
   * Testing method for an observer to sleep. (I.E. one run of its execution)
   * </p>
   * @deprecated
   */
  def goSleep() : Unit
}
