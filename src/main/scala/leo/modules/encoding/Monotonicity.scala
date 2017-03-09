package leo.modules.encoding

import leo.datastructures.{Clause, Type, Signature}

/**
  * Trait for monotonicity inference systems.
  *
  * @tparam Problem The representation of a problem (e.g. set of terms, set of clauses ...)
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
trait Monotonicity[Problem] {
  type InfTypes = Set[Type]

  /**
    * (Approximating) decision procedure for deciding whether
    * type `ty` is monotonic wrt. problem `problem`.
    *
    * @note Precondition:
    *       All [[leo.datastructures.Term]]s in `problem` need to be sentences,
    *       i.e. closed formulas (= closed terms of type [[leo.modules.HOLSignature#o]]).
    * @note Contract:
    *       If `monotone(ty, p)` then type `ty` is indeed monotonic wrt. `p`.
    *
    * @param ty The [[leo.datastructures.Type]] that may be monotonic
    * @param problem The problem which `ty` is relatively evaluated to.
    * @return `true` if `ty` can be shown monotonic wrt. `problem`, else `false`.
    */
  def monotone(ty: Type, problem: Problem, infiniteTypes: InfTypes)(implicit sig: Signature): Boolean

  /** (Approximating) decision procedure for deciding whether
    * each type `ty` in `types` is monotonic wrt. problem `problem`.
    * Default implementation uses `monotone(Type, Problem)` as allowed by Lemma 56.
    * @see monotone(Type, Problem) for details. */
  def monotone(types: Set[Type], problem: Problem, infiniteTypes: InfTypes)(implicit sig: Signature): Boolean = {
    val typesIt = types.iterator
    while(typesIt.hasNext) {
      val ty = typesIt.next()
      if (!monotone(ty, problem, infiniteTypes)) return false
    }
    true
  }
}

/** Monotonicity inference system for CNF problems. */
abstract class ClauseProblemMonotonicity extends Monotonicity[Set[Clause]] {
  type Problem = Set[Clause]
}

/** Monotonicity inference calculus by Blanchette, Böhme, Popescu and Smallbone (BBPS) for
  * polymorphic first-order logic.
  *
  * Monotonicity is defined by (taken from [1]):
  * Let S be a set of types and Φ be a problem. The set S is
  * monotonic in Φ if for all models M = (D_σ∈Type,_,_) of Φ, there exists a model M' =
  * (D'_σ∈Type,_,_) of Φ such that for all types σ, D'_σ is infinite if σ ∈ S and |D'_σ| = |D_σ|
  * otherwise.
  *
  * Definitions of naked variables [1,Chapter 5] are adapted to the representation of problems as sets of clauses.
  * @see [1] Blanchette et al.: Encoding Monomorphic and Polymorphic Types. */
object BBPS extends ClauseProblemMonotonicity {
  import leo.datastructures.{Literal, Term}
  import Type.ground

  /**
    * (Approximating) decision procedure for deciding whether
    * type `ty` is monotonic wrt. problem `problem`.
    *
    * @note Precondition:
    *       - All [[leo.datastructures.Term]]s in `problem` need to be sentences,
    *         i.e. closed formulas (= closed terms of type [[leo.modules.HOLSignature#o]]).
    *       - All terms in `problem` are in clausal normal form.
    *       - Terms need not to be "equational lifted", i.e. literals of the form
    *         `[(= @ s @ t) = $true]^p^` are allowed.
    * @param ty      The [[leo.datastructures.Type]] that may be monotonic
    * @param problem The problem which `ty` is relatively evaluated to.
    * @return `true` if `ty` can be shown monotonic wrt. `problem`, else `false`.
    */
  override def monotone(ty: Type, problem: Problem, infiniteTypes: InfTypes)(implicit sig: Signature): Boolean = {
    // If `ty` is ground, we can skip all naked vars that are ground and not of type `ty` since they are
    // irrelevant to the monotonicity check (they have no common instance with `ty`)
    val naked = if (ground(ty)) {
        nakedVars(problem).filterNot(nTy => ground(nTy) && nTy != ty)
    } else {
      nakedVars(problem)
    }
    monotone0(ty, infiniteTypes, naked)
  }

  /** (Approximating) decision procedure for deciding whether
    * each type `ty` in `types` is monotonic wrt. problem `problem`.
    * @see monotone(Type, Problem) for details. */
  override def monotone(types: Set[Type], problem: Problem, infiniteTypes: InfTypes)(implicit sig: Signature): Boolean = {
    // Filtering: See comment in monotone(Type, Problem, InfTypes)
    val naked = nakedVars(problem).filterNot(nTy => ground(nTy) && !types.contains(nTy))
    val typesIt = types.iterator
    while (typesIt.hasNext) {
      val ty = typesIt.next()
      if (!monotone0(ty, infiniteTypes, naked)) return false
    }
    true
  }

  private final def monotone0(ty: Type, infTypes: InfTypes, nakedVars: Set[Type])(implicit sig: Signature): Boolean = {
    import leo.datastructures.Subst
    import leo.datastructures.Type.BoundType
    leo.Out.info(s"Monotonicity check for ${ty.pretty(sig)}")
    val nakedIt = nakedVars.iterator
    while (nakedIt.hasNext) {
      val maxTyVarInTy = if (ty.typeVars.isEmpty) 0 else ty.typeVars.map(BoundType.unapply(_).get).max
      val nTy = nakedIt.next().substitute(Subst.shift(maxTyVarInTy))
      val commonInstance = mgi(ty, nTy)
      if (commonInstance.isDefined) {
        val instance = commonInstance.get
        leo.Out.info(s"mgi(${ty.pretty(sig)}, ${nTy.pretty(sig)}) exists: ${instance.pretty(sig)}")
        if (!instanceOfInfType(instance, infTypes)) return false
      } else {
        leo.Out.info(s"mgi(${ty.pretty(sig)}, ${nTy.pretty(sig)}) does not exist")
        // everything okay, no common instance existent.
      }
    }
    true
  }

  private final def nakedVars(problem: Problem): Set[Type] = {
    var vars: Set[Type] = Set.empty
    val clIt = problem.iterator
    while (clIt.hasNext) {
      val cl = clIt.next()
      vars = vars union nakedVars(cl)
    }
    vars
  }

  private final def nakedVars(clause: Clause): Set[Type] = {
    var vars: Set[Type] = Set.empty
    val litIt = clause.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      vars = vars union nakedVars(lit)
    }
    vars
  }

  private final def nakedVars(lit: Literal): Set[Type] = {
    if (lit.equational) {
      if (lit.polarity) {
        nakedVars0(lit.left, lit.right)
      } else Set()
    } else {
      nakedVars(lit.left, lit.polarity)
    }
  }

  private final def nakedVars(formula: Term, polarity: Boolean): Set[Type] = {
    import leo.modules.HOLSignature._
    formula match {
      case ===(l,r) if polarity => nakedVars0(l,r)
      case !===(l,r) if !polarity => nakedVars0(l,r)
      case _ => Set()
    }
  }
  private final def nakedVars0(l: Term, r: Term): Set[Type] = {
    if (l.isVariable) {
      if (r.isVariable) Set(l.ty, r.ty) else Set(l.ty)
    } else {
      if (r.isVariable) Set(r.ty) else Set()
    }
  }

  /** This method returns `Some(ty)` where `ty` is the most general instance of both `ty1` and `ty2`
    * or `None` if no such instance exists. */
  private final def mgi(ty1: Type, ty2: Type): Option[Type] = {
    import leo.modules.calculus.{TypeUnification => Uni}
    if (ty1 == ty2) Some(ty1)
    else {
      val unifier = Uni(ty1, ty2)
      if (unifier.isDefined) {
        val subst = unifier.get
        assert(ty1.substitute(subst) == ty2.substitute(subst))
        Some(ty1.substitute(subst))
      } else None
    }
  }

  private final def instanceOfInfType(ty: Type, infTypes: Set[Type])(implicit sig: Signature): Boolean = {
    import leo.modules.calculus.TypeMatching
    leo.Out.info(s"Checking wheter ${ty.pretty(sig)} is an instance of an inifite type ... ")
    val infTypesIt = infTypes.iterator
    while (infTypesIt.hasNext) {
      val infType = infTypesIt.next()
      if (TypeMatching(infType, ty).isDefined) return true
    }
    false
  }
}
