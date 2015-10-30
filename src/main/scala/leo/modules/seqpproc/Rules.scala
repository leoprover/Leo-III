package leo.modules.seqpproc

import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.Term.:::>
import leo.modules.calculus.CalculusRule
import leo.modules.normalization.Simplification
import leo.modules.output.{SZS_Theorem, SZS_CounterSatisfiable}

import scala.collection.SortedSet

object DefExpSimp extends CalculusRule {
  val name = "defexp_and_simp"
  override val inferenceStatus = Some(SZS_Theorem)
  def apply(t: Term): Term = {
    val sig = Signature.get
    val symb: Set[Signature#Key] = Set(sig("?").key, sig("&").key, sig("=>").key, sig("<=>").key)
    Simplification.normalize(t.exhaustive_δ_expand_upTo(symb))
  }
}

object PolaritySwitch extends CalculusRule {
  val name = "polarity_switch"
  override val inferenceStatus = Some(SZS_Theorem)
  def canApply(t: Term): Boolean = t match {
    case Not(_) => true
    case _ => false
  }
  def canApply(l: Literal): Boolean = canApply(l.left) || canApply(l.right)

  def apply(l: Literal): (Boolean, Literal) = {
    var switch = false

  }
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
