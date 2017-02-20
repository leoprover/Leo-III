package leo.modules.encoding

import leo.datastructures.{Clause, Type, Signature}

/**
  * Trait for monotonicity inference systems.
  *
  * Contract:
  * If `monotone(ty, p)` return `true` then type `ty` is monotonic wrt. `p`.
  *
  * Monotonicity is defined by
  * (taken from Blanchette et al.: Encoding Monomorphic and Polymorphic Types):
  * Let S be a set of types and Φ be a problem. The set S is
  * monotonic in Φ if for all models M = (D_σ∈Type,_,_) of Φ, there exists a model M' =
  * (D'_σ∈Type,_,_) of Φ such that for all types σ, D'_σ is infinite if σ ∈ S and |D'_σ| = |D_σ|
  * otherwise.
  * @tparam Problem The representation of a problem (e.g. set of terms, set of clauses ...)
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
trait Monotonicity[Problem] {

  /**
    * (Approximating) decision procedure for deciding whether
    * type `ty` is monotonic wrt. problem `problem`.
    *
    * @note Precondition:
    *       All [[leo.datastructures.Term]]s in `problem` need to be sentences,
    *       i.e. closed formulas (= closed terms of type [[leo.modules.HOLSignature#o]]).
    *
    * @param ty The [[leo.datastructures.Type]] that may be monotonic
    * @param problem The problem which `ty` is relatively evaluated to.
    * @return `true` if `ty` can be shown monotonic wrt. `problem`, else `false`.
    */
  def monotone(ty: Type, problem: Problem)(implicit sig: Signature): Boolean

  /** (Approximating) decision procedure for deciding whether
    * each type `ty` in `types` is monotonic wrt. problem `problem`.
    * Default implementation uses `monotone(Type, Problem)` as allowed by Lemma 56.
    * @see monotone(Type, Problem) for details. */
  def monotone(types: Set[Type], problem: Problem)(implicit sig: Signature): Boolean = {
    val typesIt = types.iterator
    while(typesIt.hasNext) {
      val ty = typesIt.next()
      if (!monotone(ty, problem)) return false
    }
    true
  }

  /** (Approximating) decision procedure for deciding whether
    * problem `problem` is (globally) monotonic, i.e. each type
    * in the signature is monotonic wrt. `problem`.
    * @see monotone(Type, Problem) for details. */
  def monotone(problem: Problem)(implicit sig: Signature): Boolean = {
    val types = sig.typeSymbols.map(Type.mkType)
    monotone(types, problem)
  }
}

/** Monotonicity inference system for CNF problems. */
abstract class ClauseProblemMonotonicity extends Monotonicity[Set[Clause]] {
  type Problem = Set[Clause]
}

/** Monotonicity inference calculus by Blanchette, Böhme, Popescu and Smallbone (BBPS).
  * Definitions of naked variables are adapted to the representation of problems as sets of clauses.
  * @see  Blanchette et al.: Encoding Monomorphic and Polymorphic Types, chapter 5. */
object BBPS extends ClauseProblemMonotonicity {
  import leo.datastructures.{Literal, Term}
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
  override def monotone(ty: Type, problem: Problem)(implicit sig: Signature): Boolean = {
    val naked = nakedVars(problem)
    !naked.contains(ty)
  }

  /** (Approximating) decision procedure for deciding whether
    * each type `ty` in `types` is monotonic wrt. problem `problem`.
    * @see monotone(Type, Problem) for details. */
  override def monotone(types: Set[Type], problem: Problem)(implicit sig: Signature): Boolean = {
    val naked = nakedVars(problem)
    naked.intersect(types).isEmpty
  }

  protected final def nakedVars(problem: Problem): Set[Type] = {
    var vars: Set[Type] = Set.empty
    val clIt = problem.iterator
    while (clIt.hasNext) {
      val cl = clIt.next()
      vars = vars union nakedVars(cl)
    }
    vars
  }

  protected final def nakedVars(clause: Clause): Set[Type] = {
    var vars: Set[Type] = Set.empty
    val litIt = clause.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      vars = vars union nakedVars(lit)
    }
    vars
  }

  protected final def nakedVars(lit: Literal): Set[Type] = {
    if (lit.equational) {
      if (lit.polarity) {
        nakedVars0(lit.left, lit.right)
      } else Set()
    } else {
      nakedVars(lit.left, lit.polarity)
    }
  }

  protected final def nakedVars(formula: Term, polarity: Boolean): Set[Type] = {
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
}
