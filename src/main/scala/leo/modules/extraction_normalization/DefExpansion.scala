package leo.modules.extraction_normalization

import leo.datastructures.blackboard.{FormulaStore, Store}
import leo.datastructures.{Literal, Clause, Role_Plain, Term}

/**
 * Created by lex on 07.07.14.
 */
object DefExpansion extends Normalization {

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def apply(formula: Clause): Clause = {
    formula.mapLit(_.termMap {case (l,r) => (l.full_δ_expand.betaNormalize,r.full_δ_expand.betaNormalize)})
  }

  def apply(literal : Literal) : Literal = {
    literal.termMap{case (l,r) => (l.full_δ_expand.betaNormalize,r.full_δ_expand.betaNormalize)}
  }

  def apply(t: Term): Term = t.full_δ_expand.betaNormalize
}
