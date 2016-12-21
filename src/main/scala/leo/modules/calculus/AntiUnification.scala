package leo.modules.calculus

import leo.datastructures.{Subst, Term}

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
    val freshAbstractionVar = vargen(s.ty)
    solve(vargen, s, t)
  }

  type AbstractionVar = Term
  type Depth = Seq[Term]
  type Eq = (AbstractionVar, Depth, Term, Term)
  type Unsolved = Seq[Eq]
  type Solved = Unsolved
  private final def solve(vargen: FreshVarGen,
                          s: Term, t: Term): Result = {
    // Exhaustively apply Decomp and Abstraction
    val (unsolved, partialSubst) = phase1(vargen, Seq((vargen(s.ty),Seq(),s,t)), Seq(), Subst.id, Subst.id)
    // Exhaustively apply Solve
    val (solved, partialSubst2) = phase2(vargen, unsolved, Seq(), partialSubst)
    // Exhaustively apply Merge
    val (merged, (resultSubst, resultTySubst)) = phase3(vargen, solved, partialSubst2)


    import leo.datastructures.TermFront
    val resultPattern: Term = resultSubst.substBndIdx(0) match {
      case TermFront(r) => r
      case _ => throw new IllegalArgumentException
    }
    (resultPattern, ???, ???)
  }

  /** Exhaustively applies Decomposition and Abstraction. */
  @tailrec
  private final def phase1(vargen: FreshVarGen,
                           unsolved: Unsolved,
                           processed: Unsolved, partialSubst: Subst, partialTySubst: Subst): (Unsolved, FullSubst) = {
    import Term.{Bound, mkBound, λ, mkTermApp}
    if (unsolved.isEmpty) (processed, (partialSubst, partialTySubst))
    else {
      val hd = unsolved.head
      // hd is {X(xi): h(ti) = h(si)}
      val canApplyDecomp = Decomposition.canApply(hd)
      if (canApplyDecomp.isDefined) { // See Decomposition for description
        val absVar = Bound.unapply(hd._1).get._2 // X
        val bound = hd._2 // xi
        val (newUnsolved, newVars, headSymbol) = Decomposition(vargen, hd, canApplyDecomp.get)
        // newUnsolved: {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn}
        // newVars: [Y1, ... Yn]
        // headsymbol: h
        val approxBinding = λ(bound.map(_.ty))(mkTermApp(headSymbol, newVars.map(v => mkTermApp(v, bound))))
        // approxBinding: λxi.h(Y1(xi), ... Yn(xi))
        val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi.h(Y1(xi), ... Yn(xi))}
        phase1(vargen, newUnsolved ++ unsolved.tail, processed, partialSubst.comp(bindingSubst), partialTySubst)
      } else {
        val canApplyAbstraction = Abstraction.canApply(hd)
        if (canApplyAbstraction.isDefined) { // See Abstraction for description
          val absVar = Bound.unapply(hd._1).get._2 // X
          val newUnsolved = Abstraction(vargen, hd, canApplyAbstraction.get) // {X'(xi,y): s = t}
          val newVar = newUnsolved._1 // X'
          val bound = newUnsolved._2 // yi,y
          val approxBinding = λ(bound.map(_.ty))(mkTermApp(newVar, bound)) // λxi,y.X'(xi,y)
          val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi,y.X'(xi,y)}
          phase1(vargen, newUnsolved +: unsolved.tail, processed, partialSubst.comp(bindingSubst), partialTySubst)
        } else {
          phase1(vargen, unsolved.tail, hd +: processed, partialSubst, partialTySubst)
        }
      }

    }
  }

  /** Exhaustively applies Solve. */
  private final def phase2(vargen: FreshVarGen,
                           unsolved: Unsolved,
                           solved: Solved, partialSubst: FullSubst): (Solved, FullSubst) = {
    ???
  }

  /** Exhaustively applies Merge. */
  private final def phase3(vargen: FreshVarGen,
                           solved: Solved,
                           partialSubst: FullSubst): ((Term, Term), FullSubst) = {
    ???
  }

  /**
    * {X(xi): h(ti) = h(si)} U A; S; σ =>
    *   {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn} U A; S; σ{X <- λxi.h(Y1(xi), ... Yn(xi))}
    *   if h is a constant of h in xi, Yi are fresh.
    */
  object Decomposition {
    type DecompHint = (Term, Seq[Term], Seq[Term])
    /** Returns an appropriate hint if both terms in `eq` are application with same head symbol. */
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
    /** Returns ({Y1(xi): t1 = s1, ..., Yn(xi): tn=sn}, {Y1,..Yn}, h)*/
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
    * @note Modified since we use a nameless representation, renaming of z in λz.s is this unnecessary.
    */
  object Abstraction {
    type AbstractionHint = (Type, Term, Term)
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
    final def apply(vargen: FreshVarGen, eq: Eq, hint: AbstractionHint): Eq = {
      import leo.datastructures.Term.{Bound, mkBound}
      import leo.datastructures.Type.mkFunType
      val bound = eq._2; val abstractionType = hint._1
      val newLeft = hint._2; val newRight = hint._3
      val newBound = bound.map { t =>
        val (ty, idx) = Bound.unapply(t).get
        mkBound(ty, idx+1)
      } :+ mkBound(abstractionType, 1)
      val freshAbstractionVar = vargen(mkFunType(newBound.map(_.ty),newLeft.ty))
      (freshAbstractionVar, newBound, newLeft, newRight)
    }
  }

  object Solve {

  }

  object Merge {

  }
}
