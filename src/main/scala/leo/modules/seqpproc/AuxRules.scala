package leo.modules.seqpproc

import leo.datastructures._
import leo.modules.calculus.CalculusRule
import leo.modules.output.SZS_CounterSatisfiable

//private object NegConjRule extends CalculusRule {
//  val name = "neg_conjecture"
//  override val inferenceStatus = Some(SZS_CounterSatisfiable)
//  def canApply(cw: ClauseWrapper) = cw.role == Role_Conjecture
//  def apply(cw: ClauseWrapper) = ClauseWrapper(cw.id + "_neg", Clause.mkClause(Seq(Literal.mkLit(cw., false))), Role_NegConjecture, ClauseAnnotation(this, cw)) // TODO: This is not generally not valid, fix me
//}