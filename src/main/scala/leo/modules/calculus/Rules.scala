package leo.modules.calculus

import leo.Out
import leo.datastructures.Literal.Side
import leo.datastructures._
import leo.modules.HOLSignature.{LitTrue, o}
import leo.modules.output.{SZS_CounterTheorem, SZS_EquiSatisfiable, SZS_Theorem}

import scala.annotation.tailrec

////////////////////////////////////////////////////////////////
////////// Extensionality
////////////////////////////////////////////////////////////////

object FuncExt extends CalculusRule {
  final val name = "func_ext"
  final val inferenceStatus = SZS_EquiSatisfiable

  type ExtLits = Literal
  type OtherLits = Literal

  final def canApply(l: Literal): Boolean = l.equational && l.left.ty.isFunType

  final def canApply(lits: Seq[Literal]): (Boolean, Seq[ExtLits], Seq[OtherLits]) = {
    var can = false
    var extLits:Seq[Literal] = Vector()
    var otherLits: Seq[Literal] = Vector()
    val literals = lits.iterator
    while (literals.hasNext) {
      val l = literals.next()
      if (canApply(l)) {
        extLits = extLits :+ l
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, extLits, otherLits)
  }

  final def canApply(cl: Clause): (Boolean, Seq[ExtLits], Seq[OtherLits]) = canApply(cl.lits)

  final def applyExhaust(lit: Literal, vargen: FreshVarGen)(implicit sig: Signature): Literal = {
    assert(lit.left.ty.isFunType, "Trying to apply func ext on non fun-ty literal")
    assert(lit.equational, "Trying to apply func ext on non-eq literal")

    val funArgTys = lit.left.ty.funParamTypes
    if (lit.polarity) {
      val newVars = funArgTys.map {ty => vargen(ty)}
      val appliedLeft = Term.mkTermApp(lit.left, newVars).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, newVars).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Positive polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Positive polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, true)(sig)
    } else {
      val skTerms = funArgTys.map(skTerm(_, vargen.existingVars, vargen.existingTyVars)(sig))
      val appliedLeft = Term.mkTermApp(lit.left, skTerms).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, skTerms).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Negative polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Negative polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, false)(sig)
    }
  }

  final def applyNew(lit: Literal, vargen: FreshVarGen)(implicit sig: Signature): Literal = {
    assert(lit.left.ty.isFunType, "Trying to apply func ext on non fun-ty literal")
    assert(lit.equational, "Trying to apply func ext on non-eq literal")

    val argType = lit.left.ty._funDomainType
    if (lit.polarity) {
      val newVar = vargen(argType)
      val appliedLeft = Term.mkTermApp(lit.left, newVar).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, newVar).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Positive polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Positive polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, true)(sig)
    } else {
      val newSkArg = skTerm(argType, vargen.existingVars, vargen.existingTyVars)(sig)
      val appliedLeft = Term.mkTermApp(lit.left, newSkArg).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, newSkArg).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Negative polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Negative polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, false)(sig)
    }
  }

  final def apply(lit: Literal, vargen: leo.modules.calculus.FreshVarGen, initFV: Seq[(Int, Type)])(implicit sig: Signature): Literal = {
    assert(lit.left.ty.isFunType, "Trying to apply func ext on non fun-ty literal")
    assert(lit.equational, "Trying to apply func ext on non-eq literal")

    val funArgTys = lit.left.ty.funParamTypes
    if (lit.polarity) {
      val newVars = funArgTys.map {ty => vargen(ty)}
      val appliedLeft = Term.mkTermApp(lit.left, newVars).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, newVars).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Positive polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Positive polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, true)(sig)
    } else {
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, initFV, vargen.existingTyVars)(sig)) //initFV: We only use the
      // free vars that were existent at the very beginning, i.e. simulating
      // that we applies func_ext to all negative literals first
      // in order to minimize the FVs inside the sk-term
      val appliedLeft = Term.mkTermApp(lit.left, skTerms).betaNormalize
      val appliedRight = Term.mkTermApp(lit.right, skTerms).betaNormalize
      assert(Term.wellTyped(appliedLeft), s"[FuncExt]: Negative polarity left result not well typed: ${appliedLeft.pretty(sig)}")
      assert(Term.wellTyped(appliedRight), s"[FuncExt]: Negative polarity right result not well typed: ${appliedRight.pretty(sig)}")
      Literal.mkOrdered(appliedLeft, appliedRight, false)(sig)
    }
  }

  final def apply(vargen: leo.modules.calculus.FreshVarGen, lits: Seq[Literal])(implicit sig: Signature): Seq[Literal] = {
    val initFV = vargen.existingVars
    lits.map(apply(_,vargen, initFV)(sig))
  }
}

object BoolExt extends CalculusRule {
  final val name = "bool_ext"
  final val inferenceStatus = SZS_Theorem

  type ExtLits = Seq[Literal]
  type OtherLits = Seq[Literal]

  final def canApply(l: Literal): Boolean = l.equational && l.left.ty == o

  final def canApply(cl: Clause): (Boolean, ExtLits, OtherLits) = {
    var can = false
    var extLits:Seq[Literal] = Vector()
    var otherLits: Seq[Literal] = Vector()
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

  final def apply(extLits: ExtLits, otherLits: OtherLits): Set[Clause] = {
    var transformed = Set(otherLits)
    val extIt = extLits.iterator
    while (extIt.hasNext) {
      val extLit = extIt.next()
      val nu = apply(extLit)
      transformed = transformed.map(_ ++ nu._1) union transformed.map(_ ++ nu._2)
    }
    transformed.map(Clause.mkClause)
  }

  final def apply(l: Literal): (ExtLits, ExtLits) = {
    assert(l.equational, "Trying to apply bool ext on non-eq literal")
    assert(l.left.ty == o && l.right.ty == o, "Trying to apply bool ext on non-bool literal")

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
protected[calculus] abstract class AnyUni extends CalculusRule {
  final val inferenceStatus = SZS_Theorem

  type UniLits = Seq[(Term, Term)]
  type OtherLits = Seq[Literal]
  type UniResult = (Clause, (Unification#TermSubst, Unification#TypeSubst))

  def canApply(l: Literal): Boolean

  final def canApply(cl: Clause): (Boolean, UniLits, OtherLits) = {
    var can = false
    var uniLits: UniLits = Vector()
    var otherLits: OtherLits = Vector()
    val lits = cl.lits.iterator
    while (lits.hasNext) {
      val l = lits.next()
      if (canApply(l)) {
        uniLits = uniLits :+ (l.left, l.right)
        can = true
      } else {
        otherLits = otherLits :+ l
      }
    }
    (can, uniLits, otherLits)
  }
}

object PreUni extends AnyUni {
  final val name = "pre_uni"

  final def canApply(l: Literal): Boolean = l.uni

  final def apply(vargen: FreshVarGen, uniLits: UniLits,
                  otherLits: OtherLits, uniDepth: Int)(implicit sig: Signature): Iterator[UniResult] = {
    import leo.modules.myAssert
    Out.trace(s"Unification on:\n\t${uniLits.map(eq => eq._1.pretty(sig) + " = " + eq._2.pretty(sig)).mkString("\n\t")}")
    myAssert(uniLits.forall{case (l,r) => Term.wellTyped(l) && Term.wellTyped(r) && l.ty == r.ty})
    val result = HuetsPreUnification.unifyAll(vargen, uniLits, uniDepth).iterator
    result.map {case (subst, flexflex) =>
      val newLiteralsFromFlexFlex = flexflex.map(eq => Literal.mkNeg(eq._1, eq._2))
      val updatedOtherLits = otherLits.map(_.substituteOrdered(subst._1, subst._2)(sig)) // FIXME this one is slow
      val resultClause = Clause(updatedOtherLits ++ newLiteralsFromFlexFlex)
      (resultClause, subst)
    }
  }
}

object PatternUni extends AnyUni {
  final val name = "pattern_uni"

  final def canApply(l: Literal): Boolean =
    l.uni && PatternUnification.isPattern(l.left) && PatternUnification.isPattern(l.right)

  final def apply(vargen: FreshVarGen, uniLits: UniLits,
                  otherLits: OtherLits)(implicit sig: Signature): Option[UniResult] = {
    import leo.modules.myAssert
    Out.trace(s"Pattern unification on:\n\t${uniLits.map(eq => eq._1.pretty(sig) + " = " + eq._2.pretty(sig)).mkString("\n\t")}")
    myAssert(uniLits.forall{case (l,r) => Term.wellTyped(l) && Term.wellTyped(r) && l.ty == r.ty})
    val result = PatternUnification.unifyAll(vargen, uniLits, -1) // depth is dont care
    if (result.isEmpty) {
      Out.trace(s"Pattern unification failed.")
      None
    } else {
      val subst = result.head._1
      Out.trace(s"Pattern unification successful: ${subst._1.pretty}")
      Out.trace(s"ty subst: ${subst._2.pretty}")
      val updatedOtherLits = otherLits.map(_.substituteOrdered(subst._1, subst._2)(sig))
      val resultClause = Clause(updatedOtherLits)
      Some((resultClause, subst))
    }
  }
}

////////////////////////////////////////////////////////////////
////////// Choice
////////////////////////////////////////////////////////////////

object Choice extends CalculusRule {
  final val name = "choice"
  final val inferenceStatus = SZS_EquiSatisfiable

  final def detectChoice(clause: Clause): Option[Term] = {
    import leo.datastructures.Term.TermApp
    if (clause.lits.size == 2) {
      val lit1 = clause.lits.head
      val lit2 = clause.lits.tail.head

      val posLit = if (lit1.polarity) lit1 else if (lit2.polarity) lit2 else null
      val negLit = if (!lit1.polarity) lit1 else if (!lit2.polarity) lit2 else null
      if (posLit == null || negLit == null || posLit.equational || negLit.equational) None
      else {
        val witnessTerm = negLit.left
        val choiceTerm = posLit.left

        witnessTerm match {
          case TermApp(prop, Seq(witness)) if prop.isVariable && isVariableModuloEta(witness) =>
            choiceTerm match {
              case TermApp(`prop`, Seq(arg0)) =>
                val arg = arg0.etaContract
                arg match {
                  case TermApp(f, Seq(prop0)) if prop0.etaContract == prop.etaContract => Some(f)
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      }
    } else
      None
  }


  final def canApply(clause: Clause, choiceFuns: Map[Type, Set[Term]])(implicit sig: Signature): Set[Term] = {
    var result: Set[Term] = Set()
    val litIt = clause.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()

      val leftOcc = lit.left.feasibleOccurrences
      val leftOccIt = leftOcc.keysIterator
      while (leftOccIt.hasNext) {
        val o = leftOccIt.next()
        val occ0 = prefixApplications(o)
        occ0.foreach { occ =>
          leo.Out.trace(s"[Choice Rule] Current occurence: ${occ.pretty(sig)}")
          val findResult = findChoice(occ, choiceFuns, leftOcc(o).head)
          if (findResult != null) leo.Out.trace(s"[Choice Rule] Taken: ${findResult.pretty(sig)}")
          if (findResult != null)
            result = result + findResult
        }
      }
      if (lit.equational) {
        val rightOcc = lit.right.feasibleOccurrences
        val rightOccIt = rightOcc.keysIterator
        while (rightOccIt.hasNext) {
          val occ = rightOccIt.next()
          val findResult = findChoice(occ, choiceFuns, rightOcc(occ).head)
          if (findResult != null)
            result = result + findResult
        }
      }
    }
    result
  }
  private final def findChoice(occ: Term, choiceFuns: Map[Type, Set[Term]], occPos: Position): Term =
    findChoice0(occ, choiceFuns, occPos, 0)


  private final def findChoice0(occ: Term, choiceFuns: Map[Type, Set[Term]], occPos: Position, depth: Int): Term = {
    import leo.datastructures.Term.{Symbol, Bound,TermApp}
    import leo.modules.HOLSignature.{Choice => ChoiceSymb}
    occ match {
      case ChoiceSymb(arg) => arg
      case TermApp(hd, args) if compatibleType(hd.ty) && args.size == 1 =>
        val arg = args.head
        hd match {
          case Bound(_,idx) if idx > occPos.abstractionCount+depth =>
            arg
          case Symbol(_) =>
            // hd.ty = (a -> o) -> a
            val choiceType =hd.ty._funDomainType._funDomainType
            // choiceType = a
            val choiceFuns0 = choiceFuns.getOrElse(choiceType, Set.empty)
            if (choiceFuns0.contains(hd)) arg else null
          case _ => null/* skip */
        }
      case _ => null/* skip */
    }
  }

  private final def compatibleType(ty: Type): Boolean = {
    if (ty.isFunType) {
      val domain = ty._funDomainType
      val codomain = ty.codomainType
      if (domain.isFunType)
        if (domain._funDomainType == codomain && domain.codomainType == o) true
        else false
      else false
    } else false
  }

  final def apply(term: Term, choiceFun: Term): Clause = {
    // We dont need to adjust the free variables of `term` since there
    // is no variable capture (we create a fresh clause).
    val newVarIndex = if (term.looseBounds.isEmpty) 1 else term.looseBounds.max + 1
    val newVar: Term = Term.mkBound(term.ty._funDomainType, newVarIndex)
    // lit1: [term y]^f
    val lit1 = Literal.mkLit(Term.mkTermApp(term, newVar).betaNormalize.etaExpand, false)
    // lit2: [term (choicefun term)]^t
    val lit2 = Literal.mkLit(Term.mkTermApp(term, Term.mkTermApp(choiceFun, term)).betaNormalize.etaExpand, true)
    Clause(Vector(lit1, lit2))
  }
}

object SolveFuncSpec extends CalculusRule {
  import leo.datastructures.Term.{λ, mkBound}
  import leo.modules.HOLSignature.{Choice => ε, Impl, &, ===}
  import leo.modules.myAssert

  final val name: String = "solveFuncSpec"
  override final val inferenceStatus = SZS_Theorem

  type Argument = Term
  type Result = Term

  /**
    * Suppose we have a specification of a function F with
    * {{{F(s11,s12,...,s1J) = t1,
    * ...
    * F(sN1,sN2,...,sNJ) = tN}}},
    * represented as an input `((s_ij)_{1<=j<=J},t_i)_{1<=i<=N}`,
    * return the term
    * `λx_1....λ.x_J.ε(λy. ⋀_i<=N. (⋀_j<=J. x_j = s_ij) => y = t_i)`.
    *
    * This term represents the specfication as a choice-term.
    *
    * @param funTy The type of the function `F`
    * @param spec The specification of the function `F`.
    * @return A choice term representing a function with specification `spec`
    */
  final def apply(funTy: Type, spec: Seq[(Seq[Argument], Result)])
                 (implicit sig: Signature): Term = {
    assert(spec.nonEmpty)

    val (paramTypes, resultType) = funTy.splitFunParamTypes
    val paramCount = paramTypes.size
    myAssert(spec.forall(s => s._1.size == paramCount))
    myAssert(spec.forall(s => s._1.map(_.ty) == paramTypes))
    myAssert(spec.forall(s => s._2.ty == resultType))
    /* Result var is the y in `SOME y. p`, i.e.
     * ε(λy.p). */
    val resultVar: Term = mkBound(resultType, 1)
    /* paramVar(i) is the i+1-th input variable for the term as in
    * `λx_1....λxi...λx_J.ε(λy. ...)`, 0<=0<J */
    def paramVar(i: Int): Term = mkBound(paramTypes(i), paramCount-i+1) // +1 b/c of y

    val specIt = spec.iterator
    /* Iteratively build-up `choiceTerm` */
    var choiceTerm: Term = null
    while (specIt.hasNext) {
      val (args,res0) = specIt.next() // (sij_j,ti)
      val res = res0.lift(paramCount+1)
      val argsIt = args.iterator
      var i = 0
      var caseTerm: Term = null // a single input `⋀_j<=J. x_j = s_ij` for a fixed i
      while (argsIt.hasNext) {
        val arg0 = argsIt.next()
        val arg = arg0.lift(paramCount+1)
        if (caseTerm == null) {
          caseTerm = ===(paramVar(i), arg)
        } else {
          caseTerm = &(caseTerm, ===(paramVar(i), arg))
        }
        i = i+1
      }
      val caseTerm0: Term = Impl(caseTerm, ===(resultVar,res))
      if (choiceTerm == null) {
        choiceTerm = caseTerm0
      } else {
        choiceTerm = &(choiceTerm, caseTerm0)
      }
    }
    val result: Term = λ(paramTypes)(ε(λ(resultType)(choiceTerm)))
    leo.Out.trace(s"[SolveFuncSpec] Result: ${result.pretty(sig)}")
    result
  }

}

////////////////////////////////////////////////////////////////
////////// Inferences
////////////////////////////////////////////////////////////////

object PrimSubst extends CalculusRule {
  type FlexHeads = Set[Term]
  final val name = "prim_subst"
  final val inferenceStatus = SZS_Theorem

  final def canApply(cl: Clause): (Boolean, FlexHeads) = {
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
    Out.trace(s"flexHeads: ${flexheads.map(_.pretty).mkString(",")}")
    (can, flexheads)
  }

  final def apply(cl: Clause, flexHeads: FlexHeads, hdSymbs: Set[Term])(implicit sig: Signature): Set[(Clause, Subst)] = hdSymbs.flatMap {hdSymb =>
    flexHeads.map {hd =>
      val vargen = leo.modules.calculus.freshVarGen(cl)
      val binding = leo.modules.calculus.partialBinding(vargen,hd.ty, hdSymb)
      val subst = Subst.singleton(hd.fv.head._1, binding)
      (cl.substituteOrdered(subst)(sig),subst)
    }
  }
}

/**
  * Representation of an (ordered) equality factoring step.
  * For details, see [[leo.modules.calculus.OrderedEqFac#apply]].
  */
object OrderedEqFac extends CalculusRule {
  final val name = "eqfactor_ordered"
  final val inferenceStatus = SZS_Theorem

  /**
    * Let `l = cl.lits(maxLitIndex)` and `l' = cl.lits(withLitIndex)` be literals
    * called `maxLit` and `withLit` in the following.
    * The method performs a single factoring step between `maxLit` and `withLit`.
    * Unification constraints `c1 = [a = b]^f` and `c2 = [c = d]^f` are appended to the literal list,
    * where `a` and `b` are the sides of the `maxLit` and `withLit`, respectively,
    * according to `maxLitSide` and `withLitSide`. `c` and `d` are the remaining terms in those literals.
    *
    * @note Precondition:
    *       - `maxLit` and `withLit` have the same polarity.
    *       - `maxLitIndex != otherLitIndex`
    * @note The rule does not validate that `maxLit` is indeed a maximal literal, i.e.
    *       this is not required for the soundness of the application.
    *
    * @param cl The clause in which the factoring step is performed
    * @param maxLitIndex The index of the (maximal) literal `l`
    * @param maxLitSide The side of the literal that is taken as the left side `s` of literal `l`
    * @param withLitIndex The index of the literal `l'`
    * @param withLitSide The side of the literal that is taken as the left side `t` of literal `l'`
    * @param sig The signature
    * @return A new clause containing of all literals of `cl` except for `maxLit` add two appended unification contraints
    *         `c1` and `c2`.
    */
  final def apply(cl: Clause, maxLitIndex: Int, maxLitSide: Side,
                  withLitIndex: Int, withLitSide: Side)(implicit sig: Signature): Clause = {
    assert(cl.lits.isDefinedAt(maxLitIndex))
    assert(cl.lits.isDefinedAt(withLitIndex))
    assert(maxLitIndex != withLitIndex)

    val maxLit = cl.lits(maxLitIndex)
    val withLit = cl.lits(withLitIndex)
    assert(maxLit.polarity == withLit.polarity)

    val (maxLitSide1, maxLitSide2) = Literal.getSidesOrdered(maxLit, maxLitSide)
    val (withLitSide1, withLitSide2) = Literal.getSidesOrdered(withLit, withLitSide)

    /* We cannot delete an element from the list, thats way we replace it by a trivially false literal,
    * that is later eliminated using Simp. */
    val lits_without_maxLit = cl.lits.updated(maxLitIndex, Literal.mkLit(LitTrue(),false))
    val unification_task1: Literal = Literal.mkNegOrdered(maxLitSide1, withLitSide1)(sig)
    val unification_task2: Literal = Literal.mkNegOrdered(maxLitSide2, withLitSide2)(sig)

    val newlitsSimp = Simp.shallowSimp(lits_without_maxLit)(sig):+ unification_task1 :+ unification_task2
    Clause(newlitsSimp)
  }


}

/**
  * Representation of an (ordered) paramodulation step.
  * For details, see [[leo.modules.calculus.OrderedParamod#apply]].
  */
object OrderedParamod extends CalculusRule {
  final val name = "paramod_ordered"
  final val inferenceStatus = SZS_Theorem

  /**
    * Performs a paramodulation step on the given configuration.
    * @note It is assumed that both clauses have distinct variables. This must be ensured
    *       before using this method.
    * @note Preconditions:
    * - withClause.lits(withIndex).polarity == true
    * - withSide == right => !withClause.lits(withIndex).oriented || simulateResolution
    * - intoSide == right => !intoClause.lits(intoIndex).oriented || simulateResolution
    * - if `t` is the `intoSide` of intoClause.lits(intoIndex), then
    *   u.fv = intoClause.implicitlyBound where `u` is a subterm of `t`
    * @param withClause clause that contains the literal used for rewriting
    * @param withIndex index of literal `s=t` in `withClause` that is used for rewriting
    * @param withSide `left` or `right`, depending on which side of `s=t` we search in `into`
    * @param intoClause clause that is rewritten
    * @param intoIndex index of literal `l=r` in `intoClause` that is rewritten
    * @param intoSide side of `l=r` that is rewritten
    * @param intoPosition position in `side(l=r)` that is rewritten
    */
  final def apply(withClause: Clause, withIndex: Int, withSide: Literal.Side,
            intoClause: Clause, intoIndex: Int, intoSide: Literal.Side, intoPosition: Position, intoSubterm: Term,
                  simulateResolution: Boolean = false)(implicit sig: Signature): Clause = {
    assert(withClause.lits.isDefinedAt(withIndex))
    assert(intoClause.lits.isDefinedAt(intoIndex))
    assert(withClause.lits(withIndex).polarity)
    assert(!(withSide == Literal.rightSide) || !withClause.lits(withIndex).oriented || simulateResolution)
     assert(!(intoSide == Literal.rightSide) || !intoClause.lits(intoIndex).oriented || simulateResolution)

    val withLiteral = withClause.lits(withIndex)
    val (toFind, replaceBy) = if (withSide == Literal.leftSide) (withLiteral.left,withLiteral.right) else (withLiteral.right,withLiteral.left)

    Out.finest(s"toFind: ${toFind.pretty(sig)}")
    Out.finest(s"replaceBy: ${replaceBy.pretty(sig)}")

    /* We cannot delete an element from the list, thats way we replace it by a trivially false literal,
    * i.e. it is lated eliminated using Simp. */
    val withLits_without_withLiteral = withClause.lits.updated(withIndex, Literal.mkLit(LitTrue(),false)).map(l =>
      Literal.mkLit(l.left.etaExpand, l.right.etaExpand, l.polarity, l.oriented)
    )
    Out.finest(s"withLits_without_withLiteral: \n\t${withLits_without_withLiteral.map(_.pretty(sig)).mkString("\n\t")}")

    /* We shift all lits from intoClause to make the universally quantified variables distinct from those of withClause. */
    val shiftedIntoLits = intoClause.lits

    val intoLiteral = shiftedIntoLits(intoIndex)
    val (findWithin, otherSide) = Literal.getSidesOrdered(intoLiteral, intoSide)

    Out.finest(s"findWithin: ${findWithin.pretty(sig)}")
    Out.finest(s"otherSide (rewrittenIntolit right): ${otherSide.pretty(sig)}")
    Out.finest(s"rewrittenIntoLit left: ${findWithin.replaceAt(intoPosition,replaceBy.substitute(Subst.shift(intoPosition.abstractionCount))).betaNormalize.pretty(sig)}")
    /* Replace subterm (and shift accordingly) */
    val rewrittenIntoLit = Literal.mkOrdered(findWithin.replaceAt(intoPosition,replaceBy.substitute(Subst.shift(intoPosition.abstractionCount))).betaNormalize,otherSide,intoLiteral.polarity)(sig)
    /* Replace old literal in intoClause (at index intoIndex) by the new literal `rewrittenIntoLit` */
    val rewrittenIntoLits = shiftedIntoLits.updated(intoIndex, rewrittenIntoLit).map(l =>
      Literal.mkLit(l.left.etaExpand, l.right.etaExpand, l.polarity, l.oriented)
    )
    /* unification literal between subterm of intoLiteral (in findWithin side) and right side of withLiteral. */
    Out.finest(s"withClause.maxImpBound: ${withClause.maxImplicitlyBound}")
    Out.finest(s"intoSubterm: ${intoSubterm.pretty(sig)}")
    val unificationLit = Literal.mkNegOrdered(toFind.etaExpand, intoSubterm.etaExpand)(sig)
    Out.finest(s"unificationLit: ${unificationLit.pretty(sig)}")

    val newlits_simp = Simp.shallowSimp(withLits_without_withLiteral ++ rewrittenIntoLits)(sig)  :+ unificationLit
    val result = Clause(newlits_simp)
    Out.finest(s"result: ${result.pretty(sig)}")
    result
  }
}

object NegateConjecture extends CalculusRule {
  final val name: String = "neg_conjecture"
  final val inferenceStatus = SZS_CounterTheorem
}
