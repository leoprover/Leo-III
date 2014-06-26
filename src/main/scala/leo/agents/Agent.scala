package leo.agents

import leo.datastructures.blackboard.{FormulaStore, Blackboard}

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
   *
   * @return true, if this Agent can execute at the moment
   */
  def isActive : Boolean

  /**
   * Sets isActive.
   *
   * @param bool
   */
  def setActive(bool : Boolean) : Unit

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
  def register()


  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param event - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  def filter(event : FormulaStore) : Set[Task]
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
trait Task {

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
}