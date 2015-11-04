package leo.modules.seqpproc

import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.Term.:::>
import leo.modules.calculus.CalculusRule
import leo.modules.calculus.HuetsPreUnification
import leo.modules.normalization.Simplification
import leo.modules.output.{SZS_EquiSatisfiable, SZS_Theorem, SZS_CounterSatisfiable}
import leo.Out

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

////////////////////////////////////////////////////////////////
////////// Normalization
////////////////////////////////////////////////////////////////

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
    case _ => t
  }
}

/** Non-extensional CNF rule. */
object CNF extends CalculusRule {
  val name = "cnf"
  override val inferenceStatus = Some(SZS_Theorem)

  type FormulaCharacter = Byte
  final val none: FormulaCharacter = ???
  final val alpha: FormulaCharacter = ???
  final val beta: FormulaCharacter = ???

  def canApply(l: Literal): Boolean = if (!l.equational) {
    l.left match {
      case _ => true
    }
  } else false

  def apply(vargen: leo.modules.calculus.FreshVarGen, fvs: Seq[(Int, Type)], l: Literal): (FormulaCharacter, Seq[Literal]) = {
    import leo.datastructures.{|||, &, Not, Forall, Exists, Impl, <=>}
    if (l.equational) {
      (none, Seq(l))
    } else {
      if (l.polarity) {
        l.left match {
          case Not(t) => (alpha, Seq(Literal(t, false)))
          case s ||| t => (beta, Seq(Literal(s, true),Literal(t,true)))
          case s & t => (alpha, Seq(Literal(s, true),Literal(t,true)))
          case s Impl t => (beta, Seq(Literal(s, false),Literal(t,true)))
          case s <=> t => ???
          case Forall(ty :::> t) => (none, Seq(Literal(t.substitute(Subst.singleton(1, vargen.apply(ty))),true)))
          case Exists(ty :::> t) => (none, Seq(Literal(t.substitute(Subst.singleton(1, leo.modules.calculus.skTerm(ty, fvs))),true)))
          case _ => (none, Seq(l))
        }
      } else {
        l.left match {
          case Not(t) => (alpha, Seq(Literal(t, true)))
          case s ||| t => (alpha, Seq(Literal(s, false),Literal(t,false)))
          case s & t => (beta, Seq(Literal(s, false),Literal(t,false)))
          case s Impl t => (alpha, Seq(Literal(s, true),Literal(t,false)))
          case s <=> t => ???
          case Forall(ty :::> t) => (none, Seq(Literal(t.substitute(Subst.singleton(1, leo.modules.calculus.skTerm(ty, fvs))),false)))
          case Exists(ty :::> t) => (none, Seq(Literal(t.substitute(Subst.singleton(1, vargen.apply(ty))),false)))
          case _ => (none, Seq(l))
        }
      }
    }
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
  def canApply(lit: Literal): Boolean = !lit.equational && canApply(lit.left)
  type LiftLits = Seq[Literal]
  type OtherLits = Seq[Literal]
  def canApply(cl: Clause): (Boolean, LiftLits, OtherLits) = {
    var can = false
    var liftLits: LiftLits = Seq()
    var otherLits: OtherLits = Seq()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (canApply(l)) {
        liftLits = liftLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, liftLits, otherLits)
  }
  def apply(liftLits: LiftLits, otherLits: OtherLits): Seq[Literal] = {
    liftLits.map(l => apply(l.left, l.polarity)) ++ otherLits
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

object Simp extends CalculusRule {
  val name = "simp"
  override val inferenceStatus = Some(SZS_Theorem)

  def apply(lit: Literal): Literal = if (lit.equational) {
    PolaritySwitch(Literal(Simplification.normalize(lit.left), Simplification.normalize(lit.right), lit.polarity))
  } else {
    PolaritySwitch(Literal(Simplification.normalize(lit.left), lit.polarity))
  }

  def apply(cl: Clause): Clause  = {
    var newLits: Seq[Literal] = Seq()
    val litIt = cl.lits.iterator
    while (litIt.hasNext) {
      val lit = apply(litIt.next())
//      val lit = litIt.next()
      if (!Literal.isFalse(lit)) {
        newLits = newLits :+ lit
      }
    }
    val prefvs = newLits.map{_.fv}.fold(Set())((s1,s2) => s1 ++ s2)
    val fvs = prefvs.map(_._1).toSeq.sortWith {case (a,b) => a > b}
    assert(prefvs.size == fvs.size)
    if (fvs.nonEmpty) {
      if (fvs.size != fvs.head) {
        Out.finest(s"FV Optimization needed on ${cl.pretty}")
        Out.finest(s"Old: \t${fvs.mkString("-")}")
        // gaps in fvs
        val newFvs = Seq.range(fvs.size, 0, -1)
        Out.finest(s"New: \t${newFvs.mkString("-")}")
        val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
        return Clause(newLits.map(_.substitute(subst)))
      }
    }
    Clause(newLits)
  }

}


////////////////////////////////////////////////////////////////
////////// Extensionality
////////////////////////////////////////////////////////////////

object FuncExt extends CalculusRule {
  val name = "func_ext"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  def canApply(l: Literal): Boolean = l.equational && l.left.ty.isFunType
  type ExtLits = Literal
  type OtherLits = Literal
  def canApply(cl: Clause): (Boolean, Seq[ExtLits], Seq[OtherLits]) = {
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

  def apply(lit: Literal, fvs: Seq[(Int, Type)]): Literal = {
    assert(lit.left.ty.isFunType)
    assert(lit.equational)

    val funArgTys = lit.left.ty.funParamTypes
    if (lit.polarity) {
      // Fresh variables
      // TODO: Maybe set implicitly quantified variables manually? Otherwise the whole terms is
      // traversed again and again
      val lastVar = fvs.head._1
      val funArgTysWithIndex = funArgTys.zipWithIndex
      val newVars = funArgTysWithIndex.map {case (ty, ind) => Term.mkBound(ty, lastVar + ind + 1)}
      Literal(Term.mkTermApp(lit.left, newVars), Term.mkTermApp(lit.right, newVars), true)
    } else {
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, fvs))
      Literal(Term.mkTermApp(lit.left, skTerms), Term.mkTermApp(lit.right, skTerms), false)
    }
  }

  def apply(lits: Seq[Literal], fvs: Seq[(Int, Type)]): Seq[Literal] = lits.map(apply(_,fvs))
}

object BoolExt extends CalculusRule {
  val name = "bool_ext"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  def canApply(l: Literal): Boolean = l.equational && l.left.ty == Signature.get.o
  type ExtLits = Seq[Literal]
  type OtherLits = Seq[Literal]
  def canApply(cl: Clause): (Boolean, ExtLits, OtherLits) = {
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

  def apply(extLits: ExtLits, otherLits: OtherLits): Set[Clause] = {
    var transformed = Set(otherLits)
    val extIt = extLits.iterator
    while (extIt.hasNext) {
      val extLit = extIt.next()
      val nu = apply(extLit)
      transformed = transformed.map(_ ++ nu._1) union transformed.map(_ ++ nu._2)
    }
    transformed.map(Clause.mkClause)
  }

  def apply(l: Literal): (ExtLits, ExtLits) = {
    assert(l.equational)
    assert(l.term.ty == Signature.get.o)

    if (l.polarity) {
       (Seq(Literal.mkLit(l.left, false), Literal.mkLit(l.right, true)), Seq(Literal.mkLit(l.left, true), Literal.mkLit(l.right, false)))
    } else {
      (Seq(Literal.mkLit(l.left, false), Literal.mkLit(l.right, false)), Seq(Literal.mkLit(l.left, true), Literal.mkLit(l.right, true)))
    }
  }

}

////////////////////////////////////////////////////////////////
////////// pre-Unification
////////////////////////////////////////////////////////////////
object PreUni extends CalculusRule {
  val name = "pre_uni"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)
  type UniLits = Seq[Literal]
  type OtherLits = Seq[Literal]

  def canApply(l: Literal): Boolean = l.uni

  def canApply(cl: Clause): (Boolean, UniLits, OtherLits) = {
    var can = false
    var uniLits:Seq[Literal] = Seq()
    var otherLits: Seq[Literal] = Seq()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (canApply(l)) {
        uniLits = uniLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, uniLits, otherLits)
  }


  def apply(uniLits: UniLits, otherLits: OtherLits): Set[Clause] = ???
}




////////////////////////////////////////////////////////////////
////////// Inferences
////////////////////////////////////////////////////////////////

class PrimSubst(hdSymbs: Set[Term]) extends CalculusRule {
  val name = "prim_subst"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  type FlexHeads = Set[Term]

  def canApply(cl: Clause): (Boolean, FlexHeads) = {
    var can = false
    var flexheads: FlexHeads = Set()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (l.flexHead) {
        flexheads = flexheads + l.left.headSymbol
        can = true
      }
    }
    Out.finest(s"flexHeads: ${flexheads.map(_.pretty).mkString(",")}")
    (can, flexheads)
  }

  def apply(cl: Clause, flexHeads: FlexHeads): Set[(Clause, Subst)] = hdSymbs.flatMap {hdSymb =>
    flexHeads.map { case hd =>
      val vargen = leo.modules.calculus.freshVarGen(cl)
      val binding = leo.modules.calculus.partialBinding(vargen,hd.ty, hdSymb)
      val subst = Subst.singleton(hd.fv.head._1, binding)
      (cl.substitute(subst),subst)
    }
  }
}
object StdPrimSubst extends PrimSubst(Set(Not, LitFalse, LitTrue, |||))


object EqFac extends CalculusRule {
  val name = "eq_fac"
  override val inferenceStatus = Some(SZS_Theorem)

  def canApply(cl: Clause): Boolean = ???
}






