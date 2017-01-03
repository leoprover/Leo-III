package leo.modules.calculus

import leo.datastructures.{Subst, Term}
import leo.modules.calculus.PatternAntiUnification.Merge.Canonizer

import scala.annotation.tailrec

/**
  * General trait for anti-unification algorithms. For general higher order terms
  * there exists no unique lgg/msg.
  */
trait AntiUnification {
  type TermSubst = Subst; type TypeSubst = Subst
  type FullSubst = (TermSubst, TypeSubst)
  type Result = (Term, FullSubst, FullSubst)

  /** Given two terms s and t, this method gives triples (r, σ, ϱ) such that
    * (1) r = sσ
    * (2) r = tϱ
    * (3) r is a least general (most specific) generalization of s and t. */
  def antiUnify(vargen: FreshVarGen, s: Term, t: Term): Iterable[Result]
  // TODO Does Iterable[.] make sense here? For general HO terms there could a series of those results
}

/**
  * Higher-Order Pattern Anti-Unification implementation of
  * Baumgartner et al. "Higher-Order Pattern Anti-Unification in Linear Time"
  * (J. Automated Reasoning, DOI 10.1007/s10817-016-9383-3).
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 21.12.2016
  */
object PatternAntiUnification extends AntiUnification {
  import leo.datastructures.Type

  /** Given two terms s and t, this method gives a triple (r, σ, ϱ) such that
    * (1) r = sσ
    * (2) r = tϱ
    * (3) r is a least general (most specific) generalization of s and t
    * (4) r is a higher-order pattern. */
  final def antiUnify(vargen: FreshVarGen, s: Term, t: Term): Iterable[Result] = {
    Iterable(antiUnify0(vargen, s.etaExpand, t.etaExpand))
  }

  final def antiUnify0(vargen: FreshVarGen, s: Term, t: Term): Result = {
    solve(vargen, s, t)
  }

  type AbstractionVar = Term
  type Depth = Seq[Term]
  type Eq = (AbstractionVar, Depth, Term, Term)
  type Unsolved = Seq[Eq]
  type Solved = Unsolved
  private final def solve(vargen: FreshVarGen,
                          s: Term, t: Term): Result = {
    import leo.datastructures.Term.Bound
    leo.Out.debug(s"Solve ${s.pretty} = ${t.pretty}")
    // Exhaustively apply Decomp and Abstraction
    val firstAbtractionVar = vargen(s.ty)
    val (unsolved, partialSubst) = phase1(vargen, Seq((firstAbtractionVar,Seq(),s,t)), Seq(), Subst.id, Subst.id)
    leo.Out.debug(s"Result of phase1:")
    leo.Out.debug(s"unsolved:\n\t${unsolved.map {case (va,de,l,r) =>
      s"${va.pretty}(${de.map(_.pretty).mkString(",")}):" +
        s"${l.pretty} = ${r.pretty}"}.mkString("\n\t")}")
    leo.Out.debug(s"partialSubst: ${partialSubst._1.pretty}")
    // Exhaustively apply Solve
    val (solved, partialSubst2) = phase2(vargen, unsolved, Seq(), partialSubst._1, partialSubst._2)
    leo.Out.debug(s"Result of phase2:")
    leo.Out.debug(s"solved:\n\t${solved.map {case (va,de,l,r) =>
      s"${va.pretty}(${de.map(_.pretty).mkString(",")}):" +
        s"${l.pretty} = ${r.pretty}"}.mkString("\n\t")}")
    leo.Out.debug(s"partialSubst: ${partialSubst2._1.pretty}")
    // Exhaustively apply Merge
    val (merged, (resultSubst, resultTySubst)) = phase3(vargen, solved, partialSubst2._1, partialSubst2._2)
    leo.Out.debug(s"Result of phase3:")
    leo.Out.debug(s"merged:\n\t${merged.map {case (va,de,l,r) =>
      s"${va.pretty}(${de.map(_.pretty).mkString(",")}): " +
        s"${l.pretty} = ${r.pretty}"}.mkString("\n\t")}")
    leo.Out.debug(s"resultSubst: ${resultSubst.normalize.pretty}")

    import leo.datastructures.TermFront
    val resultPattern: Term = resultSubst.normalize.substBndIdx(Bound.unapply(firstAbtractionVar).get._2) match {
      case TermFront(r) => r
      case _ => throw new IllegalArgumentException
    }
    leo.Out.debug(s"Result pattern generalization: ${resultPattern.pretty}")
    (resultPattern, ???, ???)
  }

  /** Exhaustively applies Decomposition and Abstraction. */
  @tailrec
  private final def phase1(vargen: FreshVarGen,
                           unsolved: Unsolved,
                           processed: Unsolved, partialSubst: TermSubst, partialTySubst: TypeSubst): (Unsolved, FullSubst) = {
    import Term.{Bound, λ, mkTermApp}
    if (unsolved.isEmpty) (processed, (partialSubst.normalize, partialTySubst))
    else {
      val hd = unsolved.head
      // hd is {X(xi): h(ti) = h(si)}
      leo.Out.debug(s"subst: ${partialSubst.normalize.pretty}")
      leo.Out.debug(s"solve: ${hd._1.pretty}(${hd._2.map(_.pretty).mkString(",")}):" +
          s"${hd._3.pretty} = ${hd._4.pretty}")
      assert(Term.wellTyped(hd._3))
      assert(Term.wellTyped(hd._4))
      val canApplyDecomp = Decomposition.canApply(hd)
      if (canApplyDecomp.isDefined) { // See Decomposition for description
        leo.Out.debug("Apply Decomp")
        val absVar = Bound.unapply(hd._1).get._2 // X
        val bound = hd._2 // xi
        val (newUnsolved, newVars, headSymbol) = Decomposition(vargen, hd, canApplyDecomp.get)
        // newUnsolved: {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn}
        // newVars: [Y1, ... Yn]
        // headsymbol: h
        val approxBinding = λ(bound.map(_.ty))(mkTermApp(headSymbol, newVars.map(v => mkTermApp(v.lift(bound.size), bound))))
        // approxBinding: λxi.h(Y1(xi), ... Yn(xi))
        val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi.h(Y1(xi), ... Yn(xi))}
        phase1(vargen, newUnsolved ++ unsolved.tail, processed, partialSubst.comp(bindingSubst), partialTySubst)
      } else {
        val canApplyAbstraction = Abstraction.canApply(hd)
        if (canApplyAbstraction.isDefined) { // See Abstraction for description
          leo.Out.debug("Apply Abstraction")
          val absVar = Bound.unapply(hd._1).get._2 // X
          val newUnsolved = Abstraction(vargen, hd, canApplyAbstraction.get) // {X'(xi,y): s = t}
          val newVar = newUnsolved._1 // X'
          val bound = newUnsolved._2 // yi,y
          val approxBinding = λ(bound.map(_.ty))(mkTermApp(newVar.lift(bound.size), bound)) // λxi,y.X'(xi,y)
          val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi,y.X'(xi,y)}
          phase1(vargen, newUnsolved +: unsolved.tail, processed, partialSubst.comp(bindingSubst), partialTySubst)
        } else {
          leo.Out.debug("Apply Nothing")
          phase1(vargen, unsolved.tail, hd +: processed, partialSubst, partialTySubst)
        }
      }

    }
  }

  /** Exhaustively applies Solve. */
  @tailrec
  private final def phase2(vargen: FreshVarGen,
                           unsolved: Unsolved,
                           solved: Solved, partialSubst: TermSubst, partialTySubst: TypeSubst): (Solved, FullSubst) = {
    import Term.{Bound, λ, mkTermApp}
    if (unsolved.isEmpty) (solved, (partialSubst.normalize, partialTySubst))
    else {
      val hd = unsolved.head
      leo.Out.debug(s"subst: ${partialSubst.normalize.pretty}")
      leo.Out.debug(s"solve: ${hd._1.pretty}(${hd._2.map(_.pretty).mkString(",")}):" +
        s"${hd._3.pretty} = ${hd._4.pretty}")
      assert(Term.wellTyped(hd._3))
      assert(Term.wellTyped(hd._4))
      // hd is {X(xi): s = t}
      val absVar = Bound.unapply(hd._1).get._2; val bound = hd._2 // X and xi respectively
      // No need for canApply since the remaining equations should be of the form s = t where
      // head(s) != head(t) or head(s) = head(t) = Z not in xi
      val newSolved = Solve(vargen, hd) // {Y(yi): s = t}
      val newVar = newSolved._1; val newBound = newSolved._2  // Y and yi respectively
      val approxBinding = λ(bound.map(_.ty))(mkTermApp(newVar.lift(bound.size), newBound)) // λxi.Y(yi)
      val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi.Y(yi)}
      phase2(vargen, unsolved.tail, newSolved +: solved, partialSubst.comp(bindingSubst), partialTySubst)
    }
  }

  /** Exhaustively applies Merge. */
  private final def phase3(vargen: FreshVarGen,
                           solved: Solved,
                           partialSubst: TermSubst, partialTySubst: TypeSubst): (Seq[Eq], FullSubst) = {
    // Traverse all terms in solved to get unique representation (cf. Lemma 4 in paper)
    // partition to alpha-equivalent terms
    /////////////////////////////
    var partition: Map[(Term, Term), Set[(Eq, Canonizer)]] = Map()
    val solvedIt = solved.iterator
    leo.Out.debug(s"phase 3")
    while (solvedIt.hasNext) {
      val eq = solvedIt.next()
      val (l,r,canonizer) = Merge.canonize(eq)
      if (partition.contains((l,r)))
        partition = partition + ((l,r) -> (partition((l,r)) + ((eq, canonizer))))
      else
        partition = partition + ((l,r) -> Set((eq, canonizer)))
    }
    leo.Out.debug(s"Partition finished:")
    partition.foreach {case ((l,r),set) =>
        leo.Out.debug(s"canon left: ${l.pretty}")
      leo.Out.debug(s"canon right: ${r.pretty}")
        leo.Out.debug(s"eqs: ${set.size}")
    }
    /////////////////////////////
    // apply merge in each equivalence class
    /////////////////////////////
    val canonIt = partition.keys.iterator
    var resultEqs: Seq[Eq] = Seq()
    var resultTermSubst: TermSubst = partialSubst; var resultTypeSubst: TypeSubst = partialTySubst
    import Term.{Bound, λ, mkTermApp}
    while (canonIt.hasNext) {
      val canon = canonIt.next()
      var eqs = partition(canon)
      assert(eqs.nonEmpty)
      val (eq1, canonizer1) = eqs.head // eq1 is {X(xi): t1 = t2}
      val invCanonizer1 = canonizer1.map(_.swap) // since canonizer is a bijection, invCanonizer is also a map
      assert(invCanonizer1.size == canonizer1.size) // TODO: Maybe exactly the other way around?
      val absVar1 = eq1._1 // X
      // loop over the remaining representatives of this class and reduce them to eq1
      eqs = eqs.tail
      while (eqs.nonEmpty) {
        val (eq2, canonizer2) = eqs.head // // eq2 is {Y(yi): s1 = s2}
        val absVar2 = Bound.unapply(eq2._1).get._2 // Y as int
        val bound2 = eq2._2 // yi
        val bindingArgs = Merge(invCanonizer1, bound2, canonizer2) // xiπ
        assert(bound2.size == bindingArgs.size)
        val approxBinding = λ(bound2.map(_.ty))(mkTermApp(absVar1.lift(bound2.size), bindingArgs)) // λyi.X(xiπ)
        val bindingSubst = Subst.singleton(absVar2, approxBinding) // {Y -> λyi.X(xiπ)}
        resultTermSubst = resultTermSubst.comp(bindingSubst)
        eqs = eqs.tail
      }
      resultEqs = eq1 +: resultEqs
    }
    (resultEqs, (resultTermSubst, resultTypeSubst))
  }

  /**
    * {{{{X(xi): h(ti) = h(si)} U A; S; σ =>
    *   {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn} U A; S; σ{X <- λxi.h(Y1(xi), ... Yn(xi))} }}}
    *   if `h` is a constant or `h` in `xi`, the `Yi` are fresh.
    */
  object Decomposition {
    /** (head symbol, left args, right args) */
    type DecompHint = (Term, Seq[Term], Seq[Term])
    /** Returns an appropriate [[Decomposition.DecompHint]] if [[Decomposition]] is applicable, `None` otherwise. */
    final def canApply(eq: Eq): Option[DecompHint] = { // TODO: Generalize to polymorphism
      import leo.datastructures.Term.{TermApp, Symbol, Bound}
      val left = eq._3; val right = eq._4
      val bound = eq._2
      left match {
        case TermApp(hd,args1) => right match {
          case TermApp(`hd`,args2) =>
            hd match {
              case Symbol(_) => Some((hd, args1, args2))
              case Bound(_,idx) if idx <= bound.size => Some((hd, args1, args2))
              case _ => None
            }
          case _ => None
        }
        case _ => None
      }
    }
    /** Returns `({Y1(xi): t1 = s1, ..., Yn(xi): tn=sn}, {Y1,..Yn}, h)`
      * @param vargen Vargen
      * @param hint The precomputed values from [[Decomposition#canApply()]].*/
    final def apply(vargen: FreshVarGen, eq: Eq, hint: DecompHint): (Unsolved, Seq[Term], Term) = {
      import leo.datastructures.Type.mkFunType
      val headSymbol = hint._1; val args1 = hint._2; val args2 = hint._3
      assert(args1.length == args2.length)
      val bound = eq._2
      val freshAbstractionVars = args1.map(t => vargen(mkFunType(bound.map(_.ty),t.ty)))
      val newEqs = freshAbstractionVars.zip(args1.zip(args2)).map {case (variable, (arg1, arg2)) =>
        (variable, bound, arg1, arg2)
      }
      (newEqs, freshAbstractionVars, headSymbol)
    }
  }

  /**
    * {{{ {X(xi): λy.t = λz.s} U A; S; σ =>
    *   {X'(xi,y): t = s{z <- y}} U A; S; σ{X <- λxi,y.X'(xi,y)} }}}
    *   where X' is fresh.
    * @note Modified since we use a nameless representation, renaming of z in λz.s is thus unnecessary.
    */
  object Abstraction {
    /** (Type of abstracted variable, body of left abstraction, body of right abstraction) */
    type AbstractionHint = (Type, Term, Term)
    /** Returns an [[Abstraction.AbstractionHint]] if [[Abstraction]] is applicable, `None` otherwise. */
    final def canApply(eq: Eq): Option[AbstractionHint] = {
      import leo.datastructures.Term.:::>
      val left = eq._3; val right = eq._4
      left match {
        case ty :::> bodyLeft => right match {
          case ty2 :::> bodyRight => assert(ty == ty2)
            Some((ty, bodyLeft, bodyRight))
          case _ => None
        }
        case _ => None
      }
    }
    /** Return X'(xi,y): t = s{z <- y}, adjusted to nameless representation.
      * @param vargen Vargen
      * @param hint The already (from [[Abstraction#canApply]]) calculated details for the computation. */
    final def apply(vargen: FreshVarGen, eq: Eq, hint: AbstractionHint): Eq = {
      import leo.datastructures.Term.{Bound, mkBound}
      import leo.datastructures.Type.mkFunType
      val bound = eq._2; val abstractionType = hint._1
      // bound is `xi`, abstractionType is the type of `y`
      val newLeft = hint._2; val newRight = hint._3
      // newLeft and newRight are the bodies of the lambda abstractions (i.e. t and s)
      val newBound = bound.map { t =>
        val (ty, idx) = Bound.unapply(t).get
        mkBound(ty, idx+1)
      } :+ mkBound(abstractionType, 1)
      // newBound is `xi,y`: lift all `xi` by one and add var to it.
      val freshAbstractionVar = vargen(mkFunType(newBound.map(_.ty),newLeft.ty))
      // freshAbstractionVar is `X'`
      (freshAbstractionVar, newBound, newLeft, newRight)
    }
  }

  /**
    * {{{ {X(xi): t = s} U A; S; σ =>
    *   A; {Y(yi): t = s} U S; σ{X <- λxi.Y(yi)} }}}
    *   where `Y` is fresh and the `yi` are those `xi` that occur free in t or s.
    */
  object Solve {
    final def apply(vargen: FreshVarGen, eq: Eq): Eq = {
      import leo.datastructures.Term.Bound
      import leo.datastructures.Type.mkFunType
      val bound = eq._2; val left = eq._3; val right = eq._4
      // bound is `{xi}`, left is `t`, right is `s`
      assert(left.headSymbol != right.headSymbol
        || (Bound.unapply(left.headSymbol).isDefined
            && Bound.unapply(left.headSymbol).get._2 > bound.size))
      val boundOccurrences = left.freeVars union right.freeVars
      val newBound: Seq[Term] = bound.filter (boundOccurrences.contains)
      // newBound is `yi`: those `xi` which occur in left union right.
      val freshAbstractionVar = vargen(mkFunType(newBound.map(_.ty),left.ty)) // Y
      (freshAbstractionVar, newBound, left, right)
    }
  }

  /**
    * {{{A; {X(xi): t1 = t2, Y(yi): s1 = s2} U S; σ =>
    *   A; {X(xi): t1 = t2} U S; σ{Y <- λyi.X(xiπ)} }}}
    *   where π: {xi} -> {yi} is a bijection extended as a substitution  with `t1π = s1` and `t2π = s2`.
    */
  object Merge {
    /** Match eq2 -> eq1 and return the arguments z1...zn where zi = xiπ as above. */
    final def apply(invCanonizer: Canonizer, bounds0: Depth, canonizer2: Canonizer): Seq[Term] = {
      import leo.datastructures.Term.mkBound
      var bounds = bounds0
      var result: Seq[Term] = Seq()
      while (bounds.nonEmpty) { // Construct substitution as explicit argument list (decreasing indices)
        val hd = bounds.head // this is a loose bound variable with index: "bounds.size" (current size in loop)
        assert(canonizer2.isDefinedAt(bounds.size))
        // Check to what index hd is mapped to by canonizer2
        val hdMappedTo = canonizer2(bounds.size)
        leo.Out.debug(s"hdMappedTo: ${hdMappedTo}")
        // Now retrieve the index which var index (from eq1) maps to `hdMappedTo` by canonizer1
        val mappedToHd = invCanonizer(hdMappedTo)
        result = result :+ mkBound(hd.ty, mappedToHd)
        leo.Out.debug(s"${bounds.size} is mapped to ${mappedToHd}")
        bounds = bounds.tail
      }
      leo.Out.debug(s"result is: ${result.map(_.pretty).mkString(" , ")}")
      result
    }

    type Canonizer = Map[Int, Int]
    final protected[calculus] def canonize(eq: Eq): (Term, Term, Canonizer) = {
      leo.Out.debug(s"###")
      val left = eq._3; val right = eq._4; val depth = eq._2.size
      leo.Out.debug(s"left: ${left.pretty}")
      leo.Out.debug(s"right: ${right.pretty}")
      val (canonLeft, canonizer) = Merge.canonizeTerm(left, depth)
      val (canonRight, finalCanonzier) = Merge.canonizeTerm(right, depth, canonizer)
      leo.Out.debug(s"canon left: ${canonLeft.pretty}")
      leo.Out.debug(s"canon right: ${canonRight.pretty}")
      (canonLeft, canonRight, finalCanonzier)
    }

    final private def canonizeTerm(term: Term, depth: Int, canonizer: Canonizer = Map()): (Term, Canonizer) =
      canonizeTerm0(term, depth, 0, canonizer)

    final private def canonizeTerm0(term: Term, originalDepth: Int, extraDepth: Int,
                                   canonizer: Canonizer): (Term, Canonizer) = {
      import leo.datastructures.Term._
      term match {
          // if bound: rename if necessary
        case Bound(ty,scope) if scope > extraDepth && scope <= originalDepth+extraDepth =>
          if (canonizer.contains(scope+extraDepth))
            (mkBound(ty, canonizer(scope+extraDepth)), canonizer) // Swap entry to the one saved in the canonization substitution
          else
            (mkBound(ty, canonizer.size+1), canonizer + (scope+extraDepth -> (canonizer.size+1)))
          // rest: recurse
        case t@Bound(_,_) => (t, canonizer)
        case t@Symbol(_) => (t, canonizer)
        case hd ∙ args =>
          val (canonicalHead, canonizer1) = canonizeTerm0(hd, originalDepth, extraDepth, canonizer)
          var newCanonizer: Canonizer = canonizer1
          var args0 = args
          var canonicalArgs:Seq[Either[Term, Type]] = Seq()
          while (args0.nonEmpty) {
            val hd = args0.head
            if (hd.isLeft) {
              val res = canonizeTerm0(hd.left.get, originalDepth, extraDepth, newCanonizer)
              newCanonizer = res._2
              canonicalArgs = canonicalArgs :+ Left(res._1)
            } else {
              // Types are unchanged for now
              canonicalArgs = canonicalArgs :+ hd
            }
            args0 = args0.tail
          }
          (mkApp(canonicalHead, canonicalArgs), newCanonizer)
        case ty :::> body =>
          val (newBody, newCanonizer) = canonizeTerm0(body, originalDepth, extraDepth+1, canonizer)
          (mkTermAbs(ty, newBody),newCanonizer)
        case TypeLambda(body) =>
          val (newBody, newCanonizer) = canonizeTerm0(body, originalDepth, extraDepth+1, canonizer)
          (mkTypeAbs(newBody),newCanonizer)
      }
    }
  }
}
