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
trait Normalize extends Function2[FormulaStore,Boolean,FormulaStore] with Function1[FormulaStore,FormulaStore] with CalculusRule {

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
  override def apply(formula : FormulaStore, check : Boolean) : FormulaStore = {
    if (check) {
      if (applicable(formula.status)) markStatus(Store(normalize(formula.clause), Role_Plain, formula.context, formula.status, ClauseAnnotation(this, formula))) else formula
    } else
      markStatus(Store(normalize(formula.clause), Role_Plain, formula.context, formula.status, ClauseAnnotation(this, formula)))
  }

  /**
   * Like apply2, but assumes the normalization is applicable
   * @param formula
   * @return
   */
  override def apply(formula : FormulaStore) : FormulaStore =  markStatus(Store(normalize(formula.clause), Role_Plain, formula.context, formula.status, ClauseAnnotation(this, formula)))

  def markStatus(fs : FormulaStore) : FormulaStore
}
