package leo.modules.seqpproc

import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.Term.:::>
import leo.modules.calculus.CalculusRule
import leo.modules.normalization.Simplification
import leo.modules.output.{SZS_EquiSatisfiable, SZS_Theorem, SZS_CounterSatisfiable}

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

  def apply(t: Term): Term = t match {
    case Not(t2) => t2
    case _ => t
  }

  def apply(l: Literal): Literal = if (l.equational) {
    (l.left, l.right) match {
      case (Not(l2), Not(r2)) => Literal(l2, r2, l.polarity)
      case (Not(l2), _) => Literal(l2, l.right, !l.polarity)
      case (_, Not(r2)) => Literal(l.left, r2, !l.polarity)
      case _ => l
    }
  } else {
    l.left match {
      case Not(l2) => Literal(l2, !l.polarity)
      case _ => l
    }
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

  def apply(left: Term, polarity: Boolean): Literal = {
    val (l,r) = EQ.unapply(left).get
    if (polarity) {
      Literal(l,r,true)
    } else {
      Literal(l,r,false)
    }
  }
}

object FuncExt extends CalculusRule {
  val name = "func_ext"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  def canApply(l: Literal): Boolean = l.equational && l.left.ty.isFunType
  def canApply(cl: Clause): (Boolean, Seq[Literal], Seq[Literal]) = {
    var can = false
    var extLits:Seq[Literal] = Seq()
    var otherLits: Seq[Literal] = Seq()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (canApply(l)) {
        extLits = extLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, extLits, otherLits)
  }

  def apply(lit: Literal, fvs: Seq[Term]): Literal = {
    assert(lit.left.ty.isFunType)
    assert(lit.equational)

    val funArgTys = lit.left.ty.funParamTypesWithResultType
    if (lit.polarity) {
      // Fresh variables
      ???
    } else {
      val skTerms = funArgTys.map(ty => {
        val skFunc = Signature.get.freshSkolemVar(Type.mkFunType(fvs.map(_.ty), ty))
        Term.mkTermApp(Term.mkAtom(skFunc), fvs)
      })
      Literal(Term.mkTermApp(lit.left, skTerms), Term.mkTermApp(lit.right, skTerms), false)
    }
  }

  def apply(lits: Seq[Literal], fvs: Seq[Term]): Seq[Literal] = lits.map(apply(_,fvs))
}
