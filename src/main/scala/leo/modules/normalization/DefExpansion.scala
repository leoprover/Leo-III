package leo.modules.normalization

import leo.datastructures.{Role_Plain, Term, Clause}
import leo.datastructures.blackboard.{Store, AnnotatedClause}

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
    formula.mapLit(_.termMap {case (l,r) => (l.full_δ_expand.betaNormalize,r.full_δ_expand.betaNormalize)})
  }

  def normalize(t: Term): Term = t.full_δ_expand
}
