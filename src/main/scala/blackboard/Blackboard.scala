package blackboard

import datastructures.tptp.Commons.{AnnotatedFormula => Formula}
import agents.Agent
import scheduler.Scheduler

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
trait Blackboard extends FormulaAddTrigger with FormulaRemoveTrigger{

  /**
   * <p>
   * Adds a formula to the Set of formulas of the Blackboard.
   * </p>
   * @param formula to be added.
   */
  def addFormula(formula : Formula)

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
   * @return true if the formula was removed, false if the formula does not exist.
   */
  def removeFormula(formula : Formula) : Boolean

  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  def getFormulaByName(name : String) : Option[Formula]

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
  def getFormulas : List[Formula]

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  def getAll(p : Formula => Boolean) : List[Formula]

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p : Formula => Boolean)

  /**
   * Access to the Scheduler at a central level.
   *
   * @return the currently used scheduler
   */
  def scheduler : Scheduler
}

/**
 * <p>
 * Common Trait for all Blackboard Observer,
 * ATM only a Marker Interface. Maybe more in the future
 * </p>
 * @author Max Wisniewski
 * @since 5/14/14
 */
trait Observer extends Agent {


}

/**
 *<p>
 * BlackboardTrait for Registering AddHandler, that should be called
 * as soon as a Formula is added.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
trait FormulaAddTrigger {
  /**
   * Register a new Handler for Formula adding Handlers.
   * @param o - The Handler that is to register
   */
  def registerAddObserver(o : FormulaAddObserver)
}

/**
 *
 * <p>
 * The Handler for the event of adding a Formula to the Blackboard.
 * </p>
 *
 * <p>
 * Note that an Agent, that implements this handler, should not
 * compute immediately, but only save the change for later computation.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
trait FormulaAddObserver extends Observer {

  /**
   * Passes the added formula to the Handler.
   * @param f
   */
  def addFormula(f : Formula)

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  def filterAdd(f : Formula) : Boolean
}

/**
 * <p>
 * BlackboardTrait for registering Remove Handler.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
trait FormulaRemoveTrigger {

  /**
   * <p>
   * Method to add an Handler for the removing of a Formula of the Blackboard.
   * </p>
   *
   * @param o - The Handler that is registered.
   */
  def registerRemoveObserver(o : FormulaRemoveObserver)
}

/**
 * <p>
 * Handler for the event of removing a Formula from the Blackboard.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
trait FormulaRemoveObserver extends Observer{

  /**
   * <p>
   * Passes the removed Formula to the Handler.
   * </p>
   *
   * @param f - Removed Formula
   */
  def removeFormula(f : Formula)

  /**
   * <p>
   * Destinguishes usefull from unusefull Formulas for the Handler.
   * </p>
   * @param f - Recently removed Formula
   * @return true if the formula is relevant to the handler and false otherwise
   */
  def filterRemove(f : Formula) : Boolean

}