package leo.modules.normalization

import leo.datastructures.internal.terms.Term

/**
 * Created by lex on 07.07.14.
 */
object DefExpansion extends AbstractNormalize {
  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = formula.expandAllDefinitions

  /**
   * Checks if the staus bit 1 is raised and the second is not
   *
   * @param formula - Formula to be checked
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(formula: Term, status : Int): Boolean = (status & 3) == 1

  override def markStatus(status : Int) : Int = (status | 2) & ~1
}
