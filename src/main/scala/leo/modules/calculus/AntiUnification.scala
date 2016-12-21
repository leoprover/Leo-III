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
      if (Decomposition.canApply(hd)) {
        val absVar = Bound.unapply(hd._1).get._2 // X
        val bound = hd._2 // xi
        val (newUnsolved, newVars, headSymbol) = Decomposition(vargen, hd)
        // newUnsolved: {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn}
        // newVars: [Y1, ... Yn]
        // headsymbol: h
        val approxBinding = λ(hd._2.map(_.ty))(mkTermApp(headSymbol, newVars.map(v => mkTermApp(v, bound))))
        // approxBinding: λxi.h(Y1(xi), ... Yn(xi))
        val bindingSubst = Subst.singleton(absVar, approxBinding) // {X -> λxi.h(Y1(xi), ... Yn(xi))}
        phase1(vargen, newUnsolved ++ unsolved.tail, processed, partialSubst.comp(bindingSubst), partialTySubst)
      } else if (Abstraction.canApply(hd)) {
        val absVar = Bound.unapply(hd._1).get._2 // X
        val newUnsolved = Abstraction(vargen, hd) // {X'(xi,y): s = t}
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

  private final def phase2(vargen: FreshVarGen,
                           unsolved: Unsolved,
                           solved: Solved, partialSubst: FullSubst): (Solved, FullSubst) = {
    ???
  }

  private final def phase3(vargen: FreshVarGen,
                           solved: Solved,
                           partialSubst: FullSubst): ((Term, Term), FullSubst) = {
    ???
  }

  /**
    * {X(xi): h(ti) = h(si)} U A; S; sigma =>
    *   {Y1(xi): t1 = s1, ..., Yn(xi): tn = sn} U A; S; sigma{X <- λxi.h(Y1(xi), ... Yn(xi))}
    *   if h is a constant of h in xi, Yi are fresh.
    */
  object Decomposition {
    def canApply(eq: Eq): Boolean = {
      import leo.datastructures.Term.{∙, Symbol, Bound}
      val left = eq._3; val right = eq._4
      val bound = eq._2
      left match {
        case hd ∙ args1 => right match {
          case `hd` ∙ args2 =>
            hd match {
              case Symbol(_) => true
              case Bound(_,idx) if idx <= bound.size => true
              case _ => false
            }
          case _ => false
        }
        case _ => false
      }
    }
    def apply(vargen: FreshVarGen, eq: Eq): (Unsolved, Seq[Term], Term) = ???
  }

  object Abstraction {
    def canApply(eq: Eq): Boolean = ???
    def apply(vargen: FreshVarGen, eq: Eq): Eq = ???
  }

  object Solve {

  }

  object Merge {

  }
}
