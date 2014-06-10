package leo.modules.normalization

import leo.datastructures.internal._

/**
 *
 * Simple object, that removes syntactic tatuologies
 * and idempotent operations.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
object Simplification extends AbstractNormalize{
  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = norm(formula.betaNormalize)

  private def norm(formula : Term) : Term = formula match {
    //case Bound(ty)   => formula // Sollte egal sein
    //case Symbol(key) => formula

    case s & t => formula

      // Pass through unimportant structures
    case s ::: t    => Term.mkTermApp(norm(s),norm(t))  // Should not happen after beta normalize, unless s is irreduceable
    case s :::: ty  => Term.mkTypeApp(norm(s), ty)
    case ty :::> s  => Term.mkTermAbs(ty, norm(s))
    case TypeLambda(t) => Term.mkTypeAbs(norm(t))
    case _  => formula
  }

  /**
   * Applies atm to anything
   */
  override def applicable(formula: Term): Boolean = true
}
