package normalization

import tptp.Commons._

/**
 * This trait is shared by every Normalizing Object.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
trait Normalize extends Function1[AnnotatedFormula,AnnotatedFormula]{

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def normalize (formula : AnnotatedFormula) : AnnotatedFormula

  /**
   * Checks whether the given formula is normalizable.
   *
   * @param formula - Formula to be checked
   * @return True if a normaliziation is possible, false otherwise
   */
  def applicable (formula : AnnotatedFormula) : Boolean
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
   * @return The possibly normalized formula.
   */
  def apply(formula : AnnotatedFormula) : AnnotatedFormula = if (applicable(formula)) normalize(formula) else formula

}

/**
 * For testing a first Object, that turns every Input into $true
 */
object NoneSenseSimplify extends AbstractNormalize {
  def applicable (formula : AnnotatedFormula) : Boolean = true

  def normalize(formula : AnnotatedFormula) : AnnotatedFormula = formula match {
    case FOFAnnotated(name, role, _, anno) => FOFAnnotated(name, role, tptp.fof.Logical(tptp.fof.Atomic(tptp.Commons.DefinedPlain(tptp.Commons.DefinedFunc("$true", List())))), anno)
    case TPIAnnotated(name, role, _, anno) => FOFAnnotated(name, role, tptp.fof.Logical(tptp.fof.Atomic(tptp.Commons.DefinedPlain(tptp.Commons.DefinedFunc("$true", List())))), anno)
    case _  => println("error, not yet implemented"); null
  }
}
