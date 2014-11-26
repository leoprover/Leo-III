package leo.modules.normalization

import leo.datastructures.term.Term

/**
 * This trait is shared by every Normalizing Object.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
trait Normalize extends Function2[Term,Int,Term] with Function1[Term, Term] {

  /**
   *
   * @return name of the normalization
   */
  def name : String

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def normalize (formula : Term) : Term

  /**
   * Checks whether the given formula is normalizable.
   *
   * @param status - Bitarray stored in Int, Explaination see {@see leo.datastructures.blackboard.FormulaStore}
   * @return True if a normaliziation is possible, false otherwise
   */
  def applicable (status : Int) : Boolean

  /**
   * Marks a status for a formula as already normalized.
   *
   * @param status - Status of a formula
   * @return New Status with raised flag
   */
  def markStatus(status : Int) : Int
}

/**
 * Helper for Normalze. Already defines apply as
 * a test of the formula and an application of normalize
 * if possible
 */
abstract class AbstractNormalize extends Normalize {

  /**
   * Checks whether the normalization is possible. If it is,
   * the normalization is applied.
   *
   * @param formula - Formula that should be normalized.
   * @param status - Status of the forula
   * @return The possibly normalized formula.
   */
  override def apply(formula : Term, status : Int) : Term = if (applicable(status)) normalize(formula) else formula

  /**
   * Like apply2, but assumes the normalization is applicable
   * @param formula
   * @return
   */
  override def apply(formula : Term) : Term = normalize(formula)
}
