package leo.modules.seqpproc

import leo.datastructures.{Term, Forall, Exists, Literal}
import leo.datastructures.Term.:::>
import leo.modules.calculus.CalculusRule
import leo.modules.normalization.Simplification
import leo.modules.output.{SZS_Theorem, SZS_CounterSatisfiable}

object DefExpSimp extends CalculusRule {
  val name = "defexp_and_simp"
  override val inferenceStatus = Some(SZS_Theorem)
  def apply(t: Term): Term = Simplification.normalize(t.full_δ_expand)
}

object CNF_Forall extends CalculusRule {
  val name = "cnf_forall"
  override val inferenceStatus = Some(SZS_Theorem)
  def apply(t: Term, polarity: Boolean): Term =  {
    removeLeadingQuants(t, polarity)
  }
  def removeLeadingQuants(t: Term, polarity: Boolean): Term = t match {
    case Forall(ty :::> body) => removeLeadingQuants(body, polarity)
    case t => t
  }
}


object LiftEq extends CalculusRule {
  val name = "lifteq"
  override val inferenceStatus = Some(SZS_Theorem)

  import leo.datastructures.{=== => EQ}
  def canApply(t: Term): Boolean = t match {
    case EQ(_,_) => true
    case _ => false
  }

  def apply(left: Term, right: Boolean, polarity: Boolean): Literal = {
    val (l,r) = EQ.unapply(left).get
    if (right == polarity) {
      Literal(l,r,true)
    } else {
      Literal(l,r,false)
    }
  }
}
