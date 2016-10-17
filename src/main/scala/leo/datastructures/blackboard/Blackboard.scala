package leo.datastructures.blackboard

import leo.agents.{Agent, Task}

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
  def filterAll(t : Agent => Unit) : Unit


  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  protected[blackboard] def freshAgent(a : Agent) : Unit

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
  protected[blackboard] def getTask : Iterable[(Agent,Task)]

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
   * Registers an agent to the blackboard, should only be called by the agent itself
   *
   * @param a - the new agent
   */
  def registerAgent(a : Agent) : Unit

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
  def unregisterAgent(a : Agent) : Unit

  /**
   *
   * Returns for debugging and interactive use the agent work
   *
   * @return all registered agents and their budget
   */
  def getAgents : Iterable[(Agent, Double)]

  /**
    * Submits a new Task to the list of executable tasks.
    *
    * @param ts Set of new Tasks
    */
  def submitTasks(a : Agent, ts : Set[Task]) : Unit

  /**
    * Declares, that a task has been completely executed.
    *
    * @param t The finished task.
    */
  def finishTask(t : Task) : Unit
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
  protected[blackboard] def getDS(d : Set[DataType]) : Iterable[DataStore]


  /**
    *
    * Adds new data to the blackboard.
    *
    * @param dataType The type of data to be added
    * @param d the data to be added
    * @return true if sucessfully added. false if already existing or could no be added
    */
  def addData(dataType : DataType)(d : Any) : Boolean = {
    val result = Result().insert(dataType)(d)
    val isNew = getDS(Set(dataType)) exists (ds => ds.updateResult(result)) // TODO forall or exist?
    if(isNew)
      Blackboard().filterAll{a =>
        Blackboard().submitTasks(a, a.filter(result).toSet)
      }
    isNew
  }

  /**
    *
    * Updates data in the blackboard
    *
    * @param dataType The type of data to be updated
    * @param d1 the old value
    * @param d2 the new value
    * @return true if sucessfully been updated. false if already existing or could no be added
    */
  def updateData(dataType: DataType)(d1 : Any)(d2 : Any) : Boolean = {
    val result = Result().update(dataType)(d1)(d2)
    val isNew = getDS(Set(dataType)) exists {ds => ds.updateResult(result)} // TODO forall or exist?
    if(isNew)
      Blackboard().filterAll{a =>
        Blackboard().submitTasks(a, a.filter(result).toSet)
      }
    isNew
  }

  /**
    *
    * Removes data from the blackboard.
    *
    * @param dataType The type of data to be deleted
    * @param d the value to be deleted
    */
  def removeData(dataType: DataType)(d : Any) : Unit = {
    val result = Result().remove(dataType)(d)
    val wasDel = getDS(Set(dataType)) exists {d => d.updateResult(result) }
    if(wasDel)
      Blackboard().filterAll{a =>
        Blackboard().submitTasks(a, a.filter(result).toSet)
      }
  }
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
  def send(m : Message, to : Agent)
}