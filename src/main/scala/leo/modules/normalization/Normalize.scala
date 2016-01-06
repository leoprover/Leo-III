package leo.modules.normalization

import leo.datastructures.{ClauseAnnotation, Role_Plain , Clause}
import leo.datastructures.blackboard.{Store, FormulaStore}
import leo.modules.output.SZS_Theorem
import leo.modules.calculus.CalculusRule

/**
 * This trait is shared by every Normalizing Object.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
trait Normalize extends Function3[Clause,Int,Boolean,Clause] with Function1[Clause,Clause] with CalculusRule {

  /**
   *
   * @return name of the normalization
   */
  def name : String

  // Should be the case for all normalization steps
  override val inferenceStatus = Some(SZS_Theorem)

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def normalize (formula : Clause) : Clause

  /**
   * Checks whether the given formula is normalizable.
   *
   * @param status - Bitarray stored in Int, Explaination see {@see leo.datastructures.blackboard.FormulaStore}
   * @return True if a normaliziation is possible, false otherwise
   */
  def applicable (status : Int) : Boolean
}

/**
 * Helper for Normalze. Already defines apply as
 * a test of the formula and an application of normalize
 * if possible
 */
abstract class AbstractNormalize extends Normalize {
  /**
   * If check is true, then
   *
   * @param formula - Formula that should be normalized.
   * @param check - Status of the forula
   * @return The possibly normalized formula.
   */
  override def apply(formula : Clause, status : Int, check : Boolean) : Clause = {
    if (check) {
      if (applicable(status)) normalize(formula) else formula
    } else
      normalize(formula)
  }

  /**
   * Like apply2, but assumes the normalization is applicable
   * @param formula
   * @return
   */
  override def apply(formula : Clause) : Clause =  normalize(formula)

  def markStatus(fs : FormulaStore) : FormulaStore
}
