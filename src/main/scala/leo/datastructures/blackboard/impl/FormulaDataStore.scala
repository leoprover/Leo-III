package leo.datastructures.blackboard.impl

import leo.datastructures.blackboard.FormulaStore
import leo.datastructures.context.Context
import leo.datastructures.{Role, Clause}

/**
 * Created by ryu on 4/15/15.
 */
trait FormulaDataStore {
  /**
   * For interactive use. Creates a formula store and adds it to the blackboard (or retuns the
   * existing one)
   */
  def addFormula(name : String, clause : Clause, role : Role, context : Context) : FormulaStore

  /**
   * <p>
   * Adds a formula to the blackboard, if it does not exist. If it exists
   * the old formula is returned.
   * </p>
   *
   * @param formula to be added.
   * @return The inserted Formula, or the already existing one.
   */
  def addFormula(formula : FormulaStore) : FormulaStore

  /**
   * Adds a formula to the Blackboard.
   * Returns true, if the adding was successful
   * and false, if the formula already existed.
   *
   * @param formula - New to add formula
   * @return true if the formula was not contained in the blackboard previously
   */
  def addNewFormula(formula : FormulaStore) : Boolean

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
   * Retrieves all formulas in a given context.
   * </p>
   *
   * @param c - A given Context
   * @return All formulas in the context `c`
   */
  def getFormulas(c : Context) : Iterable[FormulaStore]

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
   *   Filters the formulas of a given context.
   * </p>
   *
   * @param c - A given Context
   * @param p Predicate the formulas have to satisfy
   * @return All formulas in `c` satisfying `p`
   */
  def getAll(c : Context)(p : FormulaStore => Boolean) : Iterable[FormulaStore]

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p : FormulaStore => Boolean)

  /**
   * <p>
   *    Removes all formulas in the context `c` satisfiying `p`.
   * </p>
   * @param c - A given Context
   * @param p - Predicate the formulas have to satisfy
   * @return Removes all formulas in `c` satisfying `p`
   */
  def rmAll(c : Context)(p : FormulaStore => Boolean)

  /**
   * Clears the complete blackboard
   */
  def clear() : Unit
}
