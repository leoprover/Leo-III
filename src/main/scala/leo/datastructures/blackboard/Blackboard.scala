package leo.datastructures.blackboard

import leo.agents.{Task, Agent}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.internal.terms.Term
import scala.collection.mutable

// Singleton Blackboards
object Blackboard extends Function0[Blackboard] {
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
trait Blackboard extends TaskOrganize with FormulaBlackboard with MessageBlackboard{

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
   * Tells the tassk set, that one task has finished computing.
   *
   * @param t - The finished task.
   */
  protected[blackboard] def finishTask(t : Task) : Unit

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
   * Registers an agent to the blackboard
   *
   * @param a - the new agent
   */
  def registerAgent(a : Agent) : Unit

  /**
   *
   * Returns for debugging and interactive use the agent work
   *
   * @return all registered agents and their budget
   */
  def getAgents() : Iterable[(Agent,Double)]

  /**
   * Returns a collection of tasks that are currently executed
   * in the system. Debugging reasons only!!!
   *
   * @return Collection of tasks that are executed.
   */
  def getRunningTasks() : Iterable[Task]
}

/**
 * This trait capsules the formulas responsible for the formula manipulation of the
 * blackboard.
 */
trait FormulaBlackboard {

  /**
   * For interactive use. Creates a formula store and adds it to the blackboard (or retuns the
   * existing one)
   */
  def addFormula(name : String, formula : Term, role : String) : FormulaStore

  /**
   * <p>
   * Adds a formula to the blackboard, if it does not exist. If it exists
   * the old formula is returned.
   * </p>
   *
   * @param formula to be added.
   * @return Left the newly added formula or Right the already existing formula.
   */
  def addFormula(formula : FormulaStore) : Either[FormulaStore, FormulaStore]

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
   * @return true if the formula was removed, false if the formula does not exist.
   */
  def removeFormula(formula : FormulaStore) : Boolean

  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  def getFormulaByName(name : String) : Option[FormulaStore]

  /**
   * <p>
   * Removes a Formula by its name.
   * </p>
   *
   * @param name - Name of the Formula to be removed
   * @return true, iff the element existed.
   */
  def rmFormulaByName(name : String) : Boolean

  /**
   * <p>
   * Returns a List of all Formulas of the Blackboard.
   * </p>
   *
   * @return All formulas of the blackboard.
   */
  def getFormulas : Iterable[FormulaStore]

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  def getAll(p : FormulaStore => Boolean) : Iterable[FormulaStore]

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p : FormulaStore => Boolean)


  /**
   * Clears the complete blackboard
   */
  def clear() : Unit
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