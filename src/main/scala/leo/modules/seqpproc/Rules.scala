package leo.modules.seqpproc

import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.Term.:::>
import leo.modules.SZSException
import leo.modules.calculus.CalculusRule
import leo.modules.calculus.HuetsPreUnification
import leo.modules.normalization.Simplification
import leo.modules.output.{SZS_Error, SZS_EquiSatisfiable, SZS_Theorem, SZS_CounterSatisfiable}
import leo.Out

import scala.annotation.tailrec
import scala.collection.SortedSet

object DefExpSimp extends CalculusRule {
  val name = "defexp_and_simp_and_etaexpand"
  override val inferenceStatus = Some(SZS_Theorem)
  def apply(t: Term): Term = {
    val sig = Signature.get
    val symb: Set[Signature#Key] = Set(sig("?").key, sig("&").key, sig("=>").key)
    Simplification.normalize(t.exhaustive_δ_expand_upTo(symb).betaNormalize.etaExpand)
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

object Instantiate extends CalculusRule {
  val name = "inst"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  def apply(t: Term, polarity: Boolean): Term =  {
    removeLeadingQuants(t, polarity, Seq())
  }
  def removeLeadingQuants(t: Term, polarity: Boolean, fv: Seq[(Int, Type)]): Term = t match {
    case Forall(ty :::> body) if polarity => removeLeadingQuants(body, polarity, (fv.size+1, ty) +: fv)
    case Exists(ty :::> body) if !polarity => removeLeadingQuants(body, polarity, (fv.size+1, ty) +: fv)
    case Exists(ty :::> body) if polarity => {
      leo.Out.debug(s"Polarity true and Exists case")
      Out.debug(s"fv are: ${fv.map(f => f._1.toString + ":" + "")}")
      removeLeadingQuants(body.closure(Subst.singleton(1, leo.modules.calculus.skTerm(ty, fv))).betaNormalize, polarity, fv)
    }
    case Forall(ty :::> body) if !polarity => removeLeadingQuants(body.closure(Subst.singleton(1, leo.modules.calculus.skTerm(ty, fv))).betaNormalize, polarity, fv)
    case _ => t
  }
}

/** Non-extensional CNF rule. */
object CNF extends CalculusRule {
  // TODO: Can be optimize this? E.g. dependencies for skolemterm
  final val name = "cnf"
  final override val inferenceStatus = Some(SZS_Theorem)

  type FormulaCharacter = Byte
  final val none: FormulaCharacter = 0.toByte
  final val alpha: FormulaCharacter = 1.toByte
  final val beta: FormulaCharacter = 2.toByte
  final val one: FormulaCharacter = 3.toByte  // A bit hacky, we want to omit ++ operations below
//  final val four: FormulaCharacter = 4.toByte  // A bit hacky, we want to omit ++ operations below

  final def canApply(l: Literal): Boolean = if (!l.equational) {
    l.left match {
      case Not(t) => true
      case s ||| t => true
      case s & t => true
      case s Impl t => true
//      case s <=> t => true
      case Forall(ty :::> t) => true
      case Exists(ty :::> t) => true
      case _ => false
    }
  } else false

  final def apply(vargen: leo.modules.calculus.FreshVarGen, l: Literal): (FormulaCharacter, Seq[Literal]) = {
    import leo.datastructures.{|||, &, Not, Forall, Exists, Impl, <=>}
    if (l.equational) {
      (none, Seq(l))
    } else {
      if (l.polarity) {
        l.left match {
          case Not(t) => (one, Seq(Literal(t, false)))
          case s ||| t => (beta, Seq(Literal(s, true),Literal(t,true)))
          case s & t => (alpha, Seq(Literal(s, true),Literal(t,true)))
          case s Impl t => (beta, Seq(Literal(s, false),Literal(t,true)))
//          case s <=> t => ???
//          case Forall(ty :::> t) => (beta, Seq(Literal(t.substitute(Subst.singleton(1, vargen.apply(ty))),true)))
          case Forall(a@ty :::> t) => (beta, Seq(Literal(Term.mkTermApp(a, vargen.apply(ty)).betaNormalize,true)))
          case Exists(a@ty :::> t) => (beta, Seq(Literal(Term.mkTermApp(a,leo.modules.calculus.skTerm(ty, vargen.existingVars)).betaNormalize ,true)))
          case _ => (none, Seq(l))
        }
      } else {
        l.left match {
          case Not(t) => (one, Seq(Literal(t, true)))
          case s ||| t => (alpha, Seq(Literal(s, false),Literal(t,false)))
          case s & t => (beta, Seq(Literal(s, false),Literal(t,false)))
          case s Impl t => (alpha, Seq(Literal(s, true),Literal(t,false)))
//          case s <=> t => ???
          case Forall(a@ty :::> t) => (beta, Seq(Literal(Term.mkTermApp(a,leo.modules.calculus.skTerm(ty, vargen.existingVars)).betaNormalize ,false)))
          case Exists(a@ty :::> t) => (beta, Seq(Literal(Term.mkTermApp(a, vargen.apply(ty)).betaNormalize,false)))
          case _ => (none, Seq(l))
        }
      }
    }
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, cl: Clause): Seq[Clause] = {
    apply0(vargen, cl.lits, Seq(Seq())).map(Clause.apply)
  }

  final private def apply0(vargen: leo.modules.calculus.FreshVarGen, lits: Seq[Literal], acc: Seq[Seq[Literal]]): Seq[Seq[Literal]] = {
    if (lits.isEmpty) {
      acc
    } else {
      val hd = lits.head
      val tail = lits.tail
      val (resChar, res) = apply(vargen, hd)
      if (resChar == none) {
        // Already normalized
        apply0(vargen, tail, acc.map(hd +: _))
      } else if (resChar == one) {
        val deepRes = apply0(vargen, res, Seq(Seq()))
        apply0(vargen, tail, deepRes.flatMap(res => res.flatMap(r => acc.map(_ :+ r))))
      } else if (resChar == alpha) {
        val deepRes0 = apply0(vargen, res.take(1), Seq(Seq()))
        val deepRes1 = apply0(vargen, res.drop(1), Seq(Seq()))
        val deepRes = deepRes0 ++ deepRes1
//        leo.Out.comment(s"Deep res alpha: ${deepRes.map(_.map(_.pretty))}")
        apply0(vargen, tail, deepRes.flatMap(res => acc.map(r => r ++ res)))
      } else if (resChar == beta) {
        val deepRes0 = apply0(vargen, res.take(1), Seq(Seq()))
        val deepRes1 = apply0(vargen, res.drop(1), Seq(Seq()))
        val deepRes = deepRes0.flatMap(res => deepRes1.map(res2 => res ++ res2))
//        leo.Out.comment(s"Deep res beta: ${deepRes.map(_.map(_.pretty))}")
        apply0(vargen, tail, deepRes.flatMap(res => acc.map(r => r ++ res)))
      } else {
        throw new SZSException(SZS_Error,
          "cnf calculus error: returning something other than alpha or beta",
          s"Returning ${resChar} while normalizing ${hd.pretty}")
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

object ACSimp extends CalculusRule {
  val name = "ac_simp"
  override val inferenceStatus = Some(SZS_Theorem)

  def lt(a: Term, b: Term): Boolean = {
    import leo.datastructures.Term.{Symbol, Bound}
    (a,b) match {
      case (Bound(_,i1), Bound(_, i2)) => i1 < i2
      case (Bound(_,_), _) => true
      case (Symbol(k1), Symbol(k2)) => k1 < k2
      case (Symbol(_), Bound(_,_)) => false
      case (Symbol(_), _) => true
      case (_, Bound(_,_)) => false
      case (_, Symbol(_)) => false
      case (a,b) => a.size < b.size

    }
  }

  def apply(t: Term, acSymbols: Set[Signature#Key]): Term = {
    acSymbols.foldLeft(t){case (term,symbol) => apply(term, symbol)}
  }

  def apply(t: Term, acSymbol: Term): Term = {
    import leo.datastructures.Term.{:::>, TypeLambda, ∙, TermApp}
    t match {
      case (ty :::> body) => Term.mkTermAbs(ty, apply(body, acSymbol))
      case TypeLambda(body) => Term.mkTypeAbs(apply(body, acSymbol))
      case TermApp(f, args) if f == acSymbol => {
        val acArgRes = apply0(args, acSymbol, Set()).toSeq.sortWith(lt)

        val newArgs = acArgRes.tail.foldRight(acArgRes.head) {case (arg,term) => Term.mkTermApp(acSymbol, Seq(arg, term))}
//        Term.mkTermApp(f, newArgs)
        newArgs
      }
      case (f ∙ args) => Term.mkApp(f, args.map {case arg => arg.fold({case t => Left(apply(t, acSymbol))}, {case ty => Right(ty)})})
      case _ => t
    }
  }

  def apply0(symbolArgs: Seq[Term], acSymbol: Term, collectedArgs: Set[Term]): Set[Term] = {
    import leo.datastructures.Term.{TermApp}

    if (symbolArgs.isEmpty) collectedArgs
    else {
      val (hdArg, restArgs) = (symbolArgs.head, symbolArgs.tail)
      hdArg match {
        case TermApp(f, moreArgs) if f == acSymbol => apply0(restArgs, acSymbol, collectedArgs ++ apply0(moreArgs, acSymbol, Set()))
        case a => apply0(restArgs, acSymbol, collectedArgs + a)
      }
    }

//    val argIt = symbolArgs.iterator
//    while (argIt.hasNext) {
//      val arg = argIt.next()
//
//      arg match {
//        case TermApp(f, moreArgs) if f == acSymbol => apply0(moreArgs, acSymbol, Set())
//        case _ => ???
//      }
//    }
//
    //    l match {
    //      case TermApp(hd, args) if hd == acSymbol => ???
    //      case _ => l
    //    }
//    ???
  }

  def apply(lit: Literal, allACSymbols: Set[Signature#Key]): Literal = {
    val leftAC = lit.left.symbols intersect allACSymbols
    if (lit.equational) {
      val newLeft = if (leftAC.isEmpty) lit.left else apply(lit.left, leftAC)
      val rightAC = lit.right.symbols intersect allACSymbols
      val newRight = if (rightAC.isEmpty) lit.right else apply(lit.right, rightAC)
      if (newLeft == lit.left && newRight == lit.right) lit
      else Literal(newLeft, newRight, lit.polarity)
    } else {
      if (leftAC.isEmpty) lit
      else {
        val norm = apply(lit.left, leftAC)
        if (norm != lit.left)
          Literal(norm, lit.polarity)
        else
          lit
      }
    }

  }

  def apply(cl: Clause, acSymbols: Set[Signature#Key]): Clause = {
    Clause(cl.lits.map(apply(_, acSymbols)))
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

    Out.finest(s"FVs:\n\t${cl.implicitlyBound.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    var newLits: Seq[Literal] = Seq()
    val litIt = cl.lits.iterator
    while (litIt.hasNext) {
      val lit = apply(litIt.next())
//      val lit = litIt.next()
      if (!Literal.isFalse(lit)) {
        if (!newLits.contains(lit)) {
          newLits = newLits :+ lit
        }
      }
    }
    val prefvs = newLits.map{_.fv}.fold(Set())((s1,s2) => s1 ++ s2)
    Out.finest(s"PREFVS:\n\t${prefvs.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    val fvs = prefvs.map(_._1).toSeq.sortWith {case (a,b) => a > b}

    assert(prefvs.size == fvs.size, "Duplicated free vars with different types")
    if (fvs.nonEmpty) {
      if (fvs.size != fvs.head) {
        Out.finest(s"FV Optimization needed on ${cl.pretty}")
        Out.finest(s"Old: \t${fvs.mkString("-")}")
        // gaps in fvs
        val newFvs = Seq.range(fvs.size, 0, -1)
        val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
        Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
        return Clause(newLits.map(_.substitute(subst)))
      }
    }
    Clause(newLits)
  }

  def shallowSimp(cl: Clause): Clause = {
    var newLits: Seq[Literal] = Seq()
    val litIt = cl.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      val normLit = eqSimp(lit)
      if (!Literal.isFalse(normLit)) {
        if (!newLits.contains(normLit)) {
          newLits = newLits :+ normLit
        }
      }
    }
    Clause(newLits)
  }

  def eqSimp(l: Literal): Literal = {
    if (!l.equational) l
    else (l.left, l.right) match {
      case (a,b) if a == b => Literal(LitTrue(), l.polarity)
      case (a,b) => l
    }
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

  def apply(lit: Literal, vargen: leo.modules.calculus.FreshVarGen, initFV: Seq[(Int, Type)]): Literal = {
    assert(lit.left.ty.isFunType, "Trying to apply func ext on non fun-ty literal")
    assert(lit.equational, "Trying to apply func ext on non-eq literal")

    val funArgTys = lit.left.ty.funParamTypes
    if (lit.polarity) {
      // TODO: Maybe set implicitly quantified variables manually? Otherwise the whole terms is
      // traversed again and again
      val newVars = funArgTys.map {case ty => vargen(ty)}
      Literal(Term.mkTermApp(lit.left, newVars).betaNormalize, Term.mkTermApp(lit.right, newVars).betaNormalize, true)
    } else {
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, initFV)) //initFV: We only use the
      // free vars that were existent at the very beginning, i.e. simulating
      // that we applies func_ext to all negative literals first
      // in order to minimize the FVs inside the sk-term
      Literal(Term.mkTermApp(lit.left, skTerms).betaNormalize, Term.mkTermApp(lit.right, skTerms).betaNormalize, false)
    }
  }

  def apply(vargen: leo.modules.calculus.FreshVarGen, lits: Seq[Literal]): Seq[Literal] = {
    val initFV = vargen.existingVars
    lits.map(apply(_,vargen, initFV))
  }

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
    assert(l.equational, "Trying to apply bool ext on non-eq literal")
    assert(l.term.ty == Signature.get.o, "Trying to apply bool ext on non-bool literal")

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


  def apply(vargen: leo.modules.calculus.FreshVarGen, uniLits: UniLits, otherLits: OtherLits): Set[(Clause, Subst)] = {
    var unsolved: Set[Literal] = Set()
    var subst = Subst.id
    val uniLitIt = uniLits.iterator
    while (uniLitIt.hasNext) {
      val uniLit = uniLitIt.next()
      Out.debug(s"Working on ${uniLit.pretty}")
      val substUniLit = uniLit.substitute(subst)
      Out.debug(s"After applying subst ${substUniLit.pretty}")
      val unires = HuetsPreUnification.unify(vargen, substUniLit.left, substUniLit.right).iterator
      if (unires.hasNext) {
        val unifier = unires.next().normalize
        Out.debug(s"Was solved! Unifier ${unifier.pretty}")
        subst = subst.comp(unifier)
      } else {
        unsolved = unsolved + uniLit
      }
    }

    Set((Clause(unsolved.map(_.substitute(subst)) ++ otherLits.map(_.substitute(subst))), subst))
  }
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
//      println(s"${hd.pretty} - ${hd.fv.head._1}")
//      println(s"max fv: ${cl.maxImplicitlyBound}")
      val vargen = leo.modules.calculus.freshVarGen(cl)
      val binding = leo.modules.calculus.partialBinding(vargen,hd.ty, hdSymb)
      val subst = Subst.singleton(hd.fv.head._1, binding)
//      println(s"${subst.pretty}")
//      println(Literal(Term.mkBound(hd.fv.head._2, hd.fv.head._1), true).substitute(subst).pretty)
      (cl.substitute(subst),subst)
    }
  }
}
object StdPrimSubst extends PrimSubst(Set(Not, LitFalse, LitTrue, |||))

// TODO: Thats not right!
// Ordering prevents non-equational clauses from being
// enabled for factoring.
object EqFac extends CalculusRule {
  val name = "eq_fac"
  override val inferenceStatus = Some(SZS_Theorem)

  def apply(cl: Clause): Set[Clause] = {
    if (Clause.horn(cl)) return Set()

    var result: Set[Clause] = Set()

    //    val posMaxLits = Clause.maxOf(cl).filter(_.polarity)
    val posMaxLitsIt = new SeqZippingSeqIterator(cl.posLits)

    while (posMaxLitsIt.hasNext) {
      val maxLit = nextMaxLit(cl, posMaxLitsIt) // s = t

      if (maxLit != null) {
        Out.debug(s"########## Next max pos lit: ${maxLit.pretty}")
        val otherLitIt = cl.posLits.iterator

        while (otherLitIt.hasNext) {
          val otherLit = otherLitIt.next()
          if (maxLit != otherLit) {
            Out.debug(s"########## Next other lit: ${otherLit.pretty}")
            val stayLits = posMaxLitsIt.leftOf ++ posMaxLitsIt.rightOf

            if (!maxLit.left.isVariable || otherLit.equational) {
              Out.debug(s"########## Not Var 1")
              if (!otherLit.left.isVariable || maxLit.equational) {
                Out.debug(s"########## Not Var 2a")
                if (leo.modules.calculus.mayUnify(maxLit.left, otherLit.left) &&
                  leo.modules.calculus.mayUnify(maxLit.right, otherLit.right)) {
                  Out.debug(s"########## May unify")
                  result = result + factor((maxLit.left, maxLit.right), (otherLit.left, otherLit.right), stayLits)
                } else {
                  Out.debug(s"########## May not unify")
                }
              }
              if (!otherLit.right.isVariable || maxLit.equational) {
                Out.debug(s"########## Not Var 2b")
                if (leo.modules.calculus.mayUnify(maxLit.left, otherLit.right) &&
                  leo.modules.calculus.mayUnify(maxLit.right, otherLit.left)) {
                  Out.debug(s"########## May unify")
                  result = result + factor((maxLit.left, maxLit.right), (otherLit.right, otherLit.left), stayLits)
                } else {
                  Out.debug(s"########## May not unify")
                }
              }

            }


            // TODO: Do we need that?
//            if (!maxLit.oriented) {
//              // the same with t as maxTerm
//              if (leo.modules.calculus.mayUnify(maxLit.right, otherLit.left) &&
//                leo.modules.calculus.mayUnify(maxLit.left, otherLit.right)) {
//                result = result + factor((maxLit.right, maxLit.left), (otherLit.left, otherLit.right), stayLits)
//              }
//              if (leo.modules.calculus.mayUnify(maxLit.right, otherLit.right) &&
//                leo.modules.calculus.mayUnify(maxLit.left, otherLit.left)) {
//                result = result + factor((maxLit.right, maxLit.left), (otherLit.right, otherLit.left), stayLits)
//              }
//            }
          }
        }


      }


    }

    result
  }

//    val it = new SeqZippingSeqIterator(cl.posLits)
////    val it = new SeqZippingSeqIterator(Clause.maxOf(cl).filter(_.polarity))
//
//    while (it.hasNext) {
//      val lit = it.next()
//      // lit: s = t
//      val s = lit.left
//      val t = lit.right
//      val litsWithSyntacticSameSide = it.rightOf.filter {l => l.left == s || l.right == s}
//      val lwSSSIt = litsWithSyntacticSameSide.iterator
//      while (lwSSSIt.hasNext) {
//        val lwSSS = lwSSSIt.next()
//        if (lwSSS.left == s) {
//          if (leo.modules.calculus.mayUnify(t, lwSSS.right)) {
//            val newCl = Clause(it.leftOf ++ it.rightOf :+ Literal.mkNeg(t, lwSSS.right))
//            result = result + newCl
//          }
//        } else {
//          // Right side equivalent to s
//          if (leo.modules.calculus.mayUnify(t, lwSSS.left)) {
//            val newCl = Clause(it.leftOf ++ it.rightOf :+ Literal.mkNeg(t, lwSSS.left))
//            result = result + newCl
//          }
//        }
//      }
//
//      if (!lit.oriented) {
//        val litsWithSyntacticSameSide = it.rightOf.filter {l => l.left == t || l.right == t}
//        val lwSSSIt = litsWithSyntacticSameSide.iterator
//        while (lwSSSIt.hasNext) {
//          val lwSSS = lwSSSIt.next()
//          if (lwSSS.left == t) {
//            if (leo.modules.calculus.mayUnify(s, lwSSS.right)) {
//              val newCl = Clause(it.leftOf ++ it.rightOf :+ Literal.mkNeg(s, lwSSS.right))
//              result = result + newCl
//            }
//          } else {
//            // Right side equivalent to s
//            if (leo.modules.calculus.mayUnify(s, lwSSS.left)) {
//              val newCl = Clause(it.leftOf ++ it.rightOf :+ Literal.mkNeg(s, lwSSS.left))
//              result = result + newCl
//            }
//          }
//        }
//      }
//
//    }
//    result
//  }


  // Local definitions
  // sets the iterator to the position where the next maximal literal is
  private final def nextMaxLit(cl: Clause, iterator: ZippingSeqIterator[Literal]): Literal = {
    while (iterator.hasNext) {
      val nextLit = iterator.next()
      if (Clause.maxOf(cl) contains nextLit) {
        return nextLit
      }
    }

    null
  }

  private final def factor(maxLitTerms: (Term,Term), otherLitTerms: (Term,Term), remainingLits: Seq[Literal]): Clause = {
    val constraint1 = Literal.mkNeg(maxLitTerms._1, otherLitTerms._1)
    val constraint2 = Literal.mkNeg(maxLitTerms._2, otherLitTerms._2)
    val litList = remainingLits :+ constraint1 :+ constraint2
    val litList2 = litList.distinct.filterNot(Literal.isFalse)
    Clause(litList2)
  }


}


object OrderedParamod extends CalculusRule {
  val name = "paramod_ordered"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  def apply(cl: Clause, withCl: Clause): Set[Clause] = {
    var result: Set[Clause] = Set()
    val leftMax = (Clause.maxOf(cl))// ++ cl.negLits).distinct
    val rightMax = (Clause.maxOf(withCl))// ++ withCl.negLits).distinct

//    Out.trace(s"leftMax:\n\t${leftMax.map(_.pretty).mkString("\n\t")}")
//    Out.trace(s"rightMax:\n\t${rightMax.map(_.pretty).mkString("\n\t")}")
    val leftIt = leftMax.iterator
    while (leftIt.hasNext) {
      val l = leftIt.next() // Thats the literal we use for rewriting, that should be equational?
      if (l.polarity) {
      val toFind = l.left

      val rightIt = rightMax.iterator
      while (rightIt.hasNext) {
        val r = rightIt.next()
        //        if (r.equational) {
        val findWithin = r.left.occurrences

        val findWithinIt = findWithin.iterator
        while (findWithinIt.hasNext) {
          val st = findWithinIt.next()
          if (!st._1.isVariable && leo.modules.calculus.mayUnify(toFind, st._1)) {
            // paramodulate
            Out.finest(s"May unify: ${toFind.pretty} with ${st._1.pretty} at ${st._2.map(_.pretty).mkString(" and ")} in ${r.left.pretty}")
            // get all literals of cl without l
            val litsOfClWithoutL = cl.lits.diff(Seq(l))
            val litsOfWithClWithoutR = withCl.lits.diff(Seq(r))
            // TODO: Do that more efficiently, e.g. with zippers
            val fused = leo.modules.calculus.fuseLiterals(litsOfClWithoutL, cl.implicitlyBound, litsOfWithClWithoutR, withCl.implicitlyBound)
            val liftedR = r.substitute(Subst.shift(cl.maxImplicitlyBound))
            val repls = st._2.map(pos => Literal(liftedR.left.replaceAt(pos, l.right), liftedR.right, r.polarity))
            val uni = Literal.mkNeg(toFind, st._1.closure(Subst.shift(cl.maxImplicitlyBound)).betaNormalize)

            result = result ++ repls.map(l => Clause(l +: uni +: fused))
          }
        }

        if (!r.oriented) {
          // Do same stuff for r.right
          val findWithin = r.right.occurrences

          val findWithinIt = findWithin.iterator
          while (findWithinIt.hasNext) {
            val st = findWithinIt.next()
            if (!st._1.isVariable && leo.modules.calculus.mayUnify(toFind, st._1)) {
              // paramodulate
              // get all literals of cl without l
              val litsOfClWithoutL = cl.lits.diff(Seq(l))
              val litsOfWithClWithoutR = withCl.lits.diff(Seq(r))
              // TODO: Do that more efficiently, e.g. with zippers
              val fused = leo.modules.calculus.fuseLiterals(litsOfClWithoutL, cl.implicitlyBound, litsOfWithClWithoutR, withCl.implicitlyBound)
              val repls = st._2.map(pos => Literal(r.left, r.right.replaceAt(pos, l.right), r.polarity))
              val uni = Literal.mkNeg(toFind, st._1.closure(Subst.shift(cl.maxImplicitlyBound)).betaNormalize)
              result = result ++ repls.map(l => Clause(l +: uni +: fused))
            }
          }
        }
        //        } else {
        //
        //        }
      }

      // ...
      if (!l.oriented) {
        // Same as above with toFind = l.right
        val toFind = l.right

        val rightIt = rightMax.iterator
        while (rightIt.hasNext) {
          val r = rightIt.next()
          //          if (r.equational) {
          val findWithin = r.left.occurrences

          val findWithinIt = findWithin.iterator
          while (findWithinIt.hasNext) {
            val st = findWithinIt.next()
            if (!st._1.isVariable && leo.modules.calculus.mayUnify(toFind, st._1)) {
              // paramodulate
              // get all literals of cl without l
              val litsOfClWithoutL = cl.lits.diff(Seq(l))
              val litsOfWithClWithoutR = withCl.lits.diff(Seq(r))
              // TODO: Do that more efficiently, e.g. with zippers
              val fused = leo.modules.calculus.fuseLiterals(litsOfClWithoutL, cl.implicitlyBound, litsOfWithClWithoutR, withCl.implicitlyBound)
              val repls = st._2.map(pos => Literal(r.left.replaceAt(pos, l.right), r.right, r.polarity))
              val uni = Literal.mkNeg(toFind, st._1.closure(Subst.shift(cl.maxImplicitlyBound)).betaNormalize)
              result = result ++ repls.map(l => Clause(l +: uni +: fused))
            }
          }

          if (!r.oriented) {
            // Do same stuff for r.right
            val findWithin = r.right.occurrences

            val findWithinIt = findWithin.iterator
            while (findWithinIt.hasNext) {
              val st = findWithinIt.next()
              if (!st._1.isVariable && leo.modules.calculus.mayUnify(toFind, st._1)) {
                // paramodulate
                // get all literals of cl without l
                val litsOfClWithoutL = cl.lits.diff(Seq(l))
                val litsOfWithClWithoutR = withCl.lits.diff(Seq(r))
                // TODO: Do that more efficiently, e.g. with zippers
                val fused = leo.modules.calculus.fuseLiterals(litsOfClWithoutL, cl.implicitlyBound, litsOfWithClWithoutR, withCl.implicitlyBound)
                val repls = st._2.map(pos => Literal(r.left, r.right.replaceAt(pos, l.right), r.polarity))
                val uni = Literal.mkNeg(toFind, st._1.closure(Subst.shift(cl.maxImplicitlyBound)).betaNormalize)
                result = result ++ repls.map(l => Clause(l +: uni +: fused))
              }
            }
          }
          //          } else {
          //
          //          }
        }
      }

      } else { /* skip since its not positive*/ }
    }
    result
  }
}




