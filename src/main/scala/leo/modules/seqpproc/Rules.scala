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


object ReplaceLeibnizEq extends CalculusRule {
  val name = "replace_leibeq"
  override val inferenceStatus = Some(SZS_Theorem)
  type Polarity = Boolean


  def canApply(cl: Clause): (Boolean, Map[Int, Term]) = {
    import leo.datastructures.Term.{TermApp, Bound}
    var gbTermMap: Map[Int, Term] = Map()
    var flexHeadSet: Set[Int] = Set()
    val litIt = cl.lits.iterator
    while(litIt.hasNext) {
      val lit = litIt.next()
      if (lit.flexHead) {
        val (head,args) = TermApp.unapply(lit.left).get
        assert(head.isVariable)
        if (args.size == 1) {
          val (headType, headIndex) = Bound.unapply(head).get
          val arg = args.head
          if (!(arg.looseBounds contains headIndex)) {
            if (lit.polarity) {
              flexHeadSet = flexHeadSet + headIndex
            } else {
              if (gbTermMap contains headIndex) {
                val curEntry = gbTermMap(headIndex)
                if (arg.compareTo(curEntry) == CMP_LT) {
                  gbTermMap = gbTermMap + (headIndex -> arg)
                }
              } else {
                gbTermMap = gbTermMap + (headIndex -> arg)
              }

            }
          }

        }
      }
    }
    val resMap = gbTermMap.filterKeys(k => flexHeadSet.contains(k))
    (resMap.nonEmpty, resMap)
  }

  def apply(cl: Clause, bindings: Map[Int, Term]): (Clause, Subst) = {
    val gbMap = bindings.mapValues(t => Term.mkTermAbs(t.ty, ===(t, Term.mkBound(t.ty, 1))))
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substitute(subst))
    (Clause(Simp(newLits)), subst)
  }
}

object ReplaceAndrewsEq extends CalculusRule {
  val name = "replace_andrewseq"
  override val inferenceStatus = Some(SZS_Theorem)

  def canApply(cl: Clause): (Boolean, Map[Int, Type]) = {
    import leo.datastructures.Term.{TermApp, Bound}
    var varMap: Map[Int, Type] = Map()

    val litIt = cl.lits.iterator
    while(litIt.hasNext) {
      val lit = litIt.next()
      if (lit.flexHead) {
        val (head,args) = TermApp.unapply(lit.left).get
        assert(head.isVariable)
        if (args.size == 2) {
          val (headType, headIndex) = Bound.unapply(head).get
          val (arg1,arg2) = (args.head,args.tail.head)
          if (arg1 == arg2 && !(arg1.looseBounds contains headIndex)) {
            if (!lit.polarity) {
              if (!varMap.contains(headIndex)) {
                varMap = varMap + (headIndex -> arg1.ty)
              }
            }
          }

        }
      }
    }

    (varMap.nonEmpty, varMap)
  }

  def apply(cl: Clause, vars: Map[Int, Type]): (Clause, Subst) = {
    val gbMap = vars.mapValues {case ty => Term.λ(ty,ty)(===(Term.mkBound(ty,2), Term.mkBound(ty,1)))}
    val subst = Subst.fromMap(gbMap)
    val newLits = cl.lits.map(_.substitute(subst))
    (Clause(Simp(newLits)), subst)
  }
}

object RewriteSimp extends CalculusRule {
  val name = "rewrite"
  override val inferenceStatus = Some(SZS_Theorem)

  def apply(rewriteRules: Set[Clause], simplify: Clause): Clause = simplify


  def apply(rule: Literal, simplify: Clause): Clause = {
    assert(rule.oriented)
    val toFind = rule.left
    val replaceBy = rule.right

    val simpLitIt = simplify.lits.iterator
    while (simpLitIt.hasNext) {
      val lit = simpLitIt.next()
      ???

    }

    ???
  }

  def apply(rule: Literal, negLiteral: Literal): Literal = {
    assert(!negLiteral.polarity)
    assert(rule.oriented)


    negLiteral
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
    PolaritySwitch(eqSimp(Literal(Simplification.normalize(lit.left), Simplification.normalize(lit.right), lit.polarity)))
  } else {
    PolaritySwitch(Literal(Simplification.normalize(lit.left), lit.polarity))
  }

  def apply(lits: Seq[Literal]): Seq[Literal] = {
    //Out.finest(s"FVs:\n\t${cl.implicitlyBound.map(f => f._1 + ":" + f._2.pretty).mkString("\n\t")}")
    var newLits: Seq[Literal] = Seq()
    val litIt = lits.iterator
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
        Out.finest(s"FV Optimization needed")
        Out.finest(s"Old: \t${fvs.mkString("-")}")
        // gaps in fvs
        val newFvs = Seq.range(fvs.size, 0, -1)
        val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
        Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
        return (newLits.map(_.substitute(subst)))
      }
    }
    newLits
  }

  def apply(cl: Clause): Clause  = Clause(apply(cl.lits))

  def shallowSimp(cl: Clause): Clause = {
    Clause(shallowSimp0(cl))
  }

  def shallowSimp0(cl: Clause): Seq[Literal] = {
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
    newLits
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
      if (cl.maxLits contains nextLit) {
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
  final val name = "paramod_ordered"
  final override val inferenceStatus = Some(SZS_Theorem)

  /**
    * Preconditions:
    * - withClause.lits(withIndex).polarity == true
    * - withSide == right => !withClause.lits(withIndex).oriented
    * - intoSide == right => !intoClause.lits(intoIndex).oriented
    * - if `t` is the `intoSide` of intoClause.lits(intoIndex), then
    *   u.fv = intoClause.implicitlyBound where `u` is a subterm of `t`
    *
    * @param withClause clause that contains the literal used for rewriting
    * @param withIndex index of literal `s=t` in `withClause` that is used for rewriting
    * @param withSide `left` or `right`, depending on which side of `s=t` we search in `into`
    * @param intoClause clause that is rewritten
    * @param intoIndex index of literal `l=r` in `intoClause` that is rewritten
    * @param intoSide side of `l=r` that is rewritten
    * @param intoPosition position in `side(l=r)` that is rewritten
    * @return
    */
  final def apply(withClause: Clause, withIndex: Int, withSide: Literal.Side,
            intoClause: Clause, intoIndex: Int, intoSide: Literal.Side, intoPosition: Position, intoSubterm: Term): Clause = {
    assert(withClause.lits.isDefinedAt(withIndex))
    assert(intoClause.lits.isDefinedAt(intoIndex))
    assert(withClause.lits(withIndex).polarity)
    assert(!(withSide == Literal.rightSide) || !withClause.lits(withIndex).oriented)
    assert(!(intoSide == Literal.rightSide) || !intoClause.lits(intoIndex).oriented)

    val withLiteral = withClause.lits(withIndex)
    val (toFind, replaceBy) = if (withSide == Literal.leftSide) (withLiteral.left,withLiteral.right) else (withLiteral.right,withLiteral.left)

    /* We cannot delete an element from the list, thats way we replace it by a trivially false literal,
    * i.e. it is lated eliminated using Simp. */
    val withLits_without_withLiteral = withClause.lits.updated(withIndex, Literal.mkLit(LitTrue(),false))

    /* We shift all lits from intoClause to make the universally quantified variables distinct from those of withClause. */
    val shiftedIntoLits = intoClause.lits.map(_.substitute(Subst.shift(withClause.maxImplicitlyBound)))

    val intoLiteral = shiftedIntoLits(intoIndex)
    val (findWithin, otherSide) = if (intoSide == Literal.leftSide) (intoLiteral.left,intoLiteral.right) else (intoLiteral.right,intoLiteral.left)

    /* Replace subterm (and shift accordingly) */
    val rewrittenIntoLit = Literal(findWithin.replaceAt(intoPosition,replaceBy.substitute(Subst.shift(intoPosition.abstractionCount))),otherSide,intoLiteral.polarity)
    /* Replace old literal in intoClause (at index intoIndex) by the new literal `rewrittenIntoLit` */
    val rewrittenIntoLits = shiftedIntoLits.updated(intoIndex, rewrittenIntoLit)
    /* unification literal between subterm of intoLiteral (in findWithin side) and right side of withLiteral. */
    val unificationLit = Literal.mkNeg(toFind, intoSubterm.substitute(Subst.shift(withClause.maxImplicitlyBound)))

    val newlits = withLits_without_withLiteral ++ rewrittenIntoLits :+ unificationLit
    val newlits_simp = Simp.apply(newlits)
    val resultingClause = Clause(newlits_simp)

    resultingClause
  }
}
