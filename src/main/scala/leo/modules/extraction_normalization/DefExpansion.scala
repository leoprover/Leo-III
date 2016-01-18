package leo.modules.extraction_normalization

import leo.datastructures.blackboard.{FormulaStore, Store}
import leo.datastructures.impl.Signature
import leo.datastructures.{Literal, Clause, Role_Plain, Term}

/**
 * Created by lex on 07.07.14.
 */
object DefExpansion extends Normalization {

  // Not stable for resetting the signature
  private val s = Signature.get
  val notExpandedKeys = Set[Signature#Key](s("&").key, s("?").key, s("!").key)

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def apply(formula: Clause): Clause = {
    formula.mapLit(_.termMap {case (l,r) => (l.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize,r.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize)})
  }

  def apply(literal : Literal) : Literal = {
    literal.termMap{case (l,r) => (l.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize,r.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize)}
  }

  def apply(t: Term): Term = t.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize
}
