package blackboard

import datastructures.tptp.Commons.{AnnotatedFormula => Formula}

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
trait Blackboard {

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
   * Removex all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p : Formula => Boolean)

  // Observing Methods.

  /**
   * <p>
   * Informs an Observer over all Add Operations.
   * </p>
   * @param o - Observer to add.
   */
  def observeAllAdds(o : BlackboardObserver)

  /**
   * <p>
   * Informs an Observer over all Remove Operations.
   * </p>
   * @param o - Observer
   */
  def observeAllRem(o : BlackboardObserver)

  /**
   * <p>
   * Informs an Observer over all Add Actions satisfying a
   * Predicate p.
   * </p>
   *
   * @param p - Predicate to be satisfied.
   * @param o - Observer.
   */
  def observeAddPredicate(p : Formula => Boolean, o : BlackboardObserver)

  /**
   * Informs an Observer over all Remove Actions satisfying
   * a Predicate p.
   *
   * @param p - Predicate to be satisfied.
   * @param o - Observer.
   */
  def observeRemPredicate(p : Formula => Boolean, o : BlackboardObserver)
}

/**
 *
 * <p>
 * This Trait defines an observer to the blackboard. In the regestration
 * the Observer can choose one of the @see{Blackboard} Triggers from which he
 * would like to be called.
 * </p>
 *
 * @author Max Wisniewski
 * @since 29.04.2014
 */
trait BlackboardObserver{

  /**
   * <p>
   * Registers the Observer to a certain blackboard.
   * </p>
   *
   * @param b - The Blackboard we want to react to.
   */
  def register(b : Blackboard) : Unit

  /**
   * <p>
   * If the registered Triggers react, the changed
   * Formulas will send to the Observer.
   * </p>
   *
   * @param changes - New or Deleted Formulas
   */
  def apply(changes : BlackboardChanges) : Unit
}

/**
 * <p>
 * Container for Blackboard Changes.
 * Contains a List of newly Added Formulas and a List of deleted Formulas.
 * </p>
 *
 * @param newFormulas - A List of newly added Formulas
 * @param delFormulas - A List of deleted Formulas
 */
class BlackboardChanges (newFormulas : List[Formula], delFormulas : List[Formula])