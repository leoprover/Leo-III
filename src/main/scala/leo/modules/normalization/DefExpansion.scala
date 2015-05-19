package leo.modules.normalization

import leo.datastructures.{Role_Plain, Term, Clause}
import leo.datastructures.blackboard.{Store, FormulaStore}

/**
 * Created by lex on 07.07.14.
 */
object DefExpansion extends AbstractNormalize {

  override val name : String = "DefinitionExpansion"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Clause): Clause = {
    formula.mapLit(_.termMap(_.full_δ_expand).termMap(_.betaNormalize))
  }

  /**
   * Checks if the staus bit 1 is raised and the second is not
   *
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(status : Int): Boolean = (status & 3) == 1

  def markStatus(fS : FormulaStore) : FormulaStore = Store(fS.clause, Role_Plain, fS.context, (fS.status | 2) -1)
}
