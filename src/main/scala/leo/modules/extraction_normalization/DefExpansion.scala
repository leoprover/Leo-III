package leo.modules.extraction_normalization

import leo.datastructures.blackboard.{FormulaStore, Store}
import leo.datastructures.{Literal, Clause, Role_Plain, Term}
import leo.datastructures.impl.Signature

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
    val s = Signature.get
    val notExpandedKeys = Set[Signature#Key](s("&").key, s("?").key, s("!").key).union(s.allUserConstants)
    formula.mapLit(_.termMap {case (l,r) => (l.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize,r.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize)})
  }

  def apply(literal : Literal) : Literal = {
    val s = Signature.get
    val notExpandedKeys = Set[Signature#Key](s("&").key, s("?").key, s("!").key).union(s.allUserConstants)
    literal.termMap{case (l,r) => (l.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize,r.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize)}
  }

  def apply(t: Term): Term = {
    val s = Signature.get
    val notExpandedKeys = Set[Signature#Key](s("&").key, s("?").key, s("!").key).union(s.allUserConstants)
    t.exhaustive_δ_expand_upTo(notExpandedKeys).betaNormalize
  }


  /*
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
  */
}
