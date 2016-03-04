package leo.modules.normalization

import leo.datastructures.context.Context
import leo.datastructures.{ClauseProxy, ClauseAnnotation, Role_Plain, Clause}
import leo.datastructures.blackboard.{Store, AnnotatedClause}
import leo.modules.output.SZS_Theorem
import leo.modules.calculus.CalculusRule

/**
 * This trait is shared by every Normalizing Object.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
trait Normalize extends Function1[ClauseProxy,ClauseProxy] with CalculusRule {

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
   * @param formula - The formula
   * @return True if a normaliziation is possible, false otherwise
   */
  def applicable (formula : Clause) : Boolean
}

/**
 * Helper for Normalze. Already defines apply as
 * a test of the formula and an application of normalize
 * if possible
 */
abstract class AbstractNormalize extends Normalize {

  override def apply(formula: ClauseProxy): ClauseProxy = formula match {
    case f : AnnotatedClause => Store(normalize(formula.cl), Role_Plain, f.context, ClauseAnnotation.InferredFrom(this, formula))
    case _ => Store(normalize(formula.cl), Role_Plain, Context(), ClauseAnnotation.InferredFrom(this, formula))
  }

  override def applicable(clause : Clause) : Boolean = clause == normalize(clause)
}
