package leo.datastructures

/**
 * Clause interface, the companion object `Clause` offers several constructors methods.
 * The `id` of a clause is unique and is monotonously increasing.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Clause extends Pretty with Prettier with HasCongruence[Clause] {
  /** The underlying sequence of literals. */
  def lits: Seq[Literal]
  /** The types of the implicitly universally quantified variables. */
  def implicitlyBound: Seq[(Int, Type)]
  def maxImplicitlyBound: Int
  /** The implicitly (universally) quantified type variables.
    * It is assumed that we are in rank-1 polymorphism. */
  def typeVars: Seq[Int]
  def maxTypeVar: Int
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin

  // Further properties
  /** Those literals in `lits` that are positive. */
  def posLits: Seq[Literal]
  /** Those literals in `lits` that are negative. */
  def negLits: Seq[Literal]

  // Operations on clauses
  def substitute(termSubst: Subst, typeSubst: Subst = Subst.id): Clause = Clause.mkClause(lits.map(_.substitute(termSubst, typeSubst)))
  def substituteOrdered(termSubst: Subst, typeSubst: Subst = Subst.id)(implicit sig: Signature): Clause = Clause.mkClause(lits.map(_.substituteOrdered(termSubst, typeSubst)(sig)))

  @inline final def map[A](f: Literal => A): Seq[A] = lits.map(f)
  @inline final def mapLit(f: Literal => Literal): Clause = Clause.mkClause(lits.map(f), Derived)
  @inline final def replace(what: Term, by: Term): Clause = Clause.mkClause(lits.map(_.replaceAll(what, by)))

  final def pretty = s"[${lits.map(_.pretty).mkString(" , ")}]"
  final def pretty(sig: Signature) = s"[${lits.map(_.pretty(sig)).mkString(" , ")}]"

  /** Conquence `cong(c1,c2)` on two clauses is more semantical that equals: Two clauses `c1` and `c2` are congruence
    * if `c1` contains all literals of `c2` set-wise and vice-versa, i.e. we have that
    * `c1 = l1 ∨ l1` is congruent to `c2 = l1` (and vice versa) while `!(c1.equals(c2))`.*/
  final def cong(that: Clause): Boolean = (lits forall {that.lits.contains}) && (that.lits forall {lits.contains})

  // System function adaptions
  /** Two clauses `c1` and `c2` are equal if and only if their underlying multi-sets of literals are equal, i.e.
    * `c1 = l1 ∨ l2` is equal to `c2 = l2 ∨ l1` while `c1` is not equal to `c2' = l2 ∨ l1 ∨ l1`. */
  override final def equals(obj : Any): Boolean = obj match {
    case co : Clause => lits.diff(co.lits).isEmpty && co.lits.diff(lits).isEmpty
    case _ => false
  }
  override final def hashCode(): Int = if (lits.isEmpty) 0
  else lits.tail.foldLeft(lits.head.hashCode()){case (h,l) => h^l.hashCode()}
}

object Clause {
  import impl.{VectorClause => ClauseImpl}

  /** Create a unit clause containing only literal `lit` with origin `Derived`. */
  def apply(lit: Literal): Clause = mkUnit(lit)
  /** Create a clause containing the set of literals `lits` with origin `Derived`. */
  def apply(lits: Iterable[Literal]): Clause = mkClause(lits)

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  @inline final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause =  ClauseImpl.mkClause(lits, origin)
  /** Create a clause containing the set of literals `lits` with origin `Derived`. */
  @inline final def mkClause(lits: Iterable[Literal]): Clause = mkClause(lits, Derived)
  /** Create a unit clause containing only literal `lit` with origin `Derived`. */
  @inline final def mkUnit(lit: Literal): Clause = mkClause(Vector(lit), Derived)

  /** The empty clause. */
  @inline final val empty: Clause = mkClause(Seq.empty)

  // Utility
  /** Returns true iff clause `c` is empty. */
  @inline final def empty(c: Clause): Boolean = c.lits.isEmpty
  /** Returns true iff clause `c` is either empty or only contains flex-flex literals. */
  final def effectivelyEmpty(c: Clause): Boolean = empty(c) || c.lits.forall(_.flexflex)
  /** Returns true iff the clause is trivially equal to `$true` since either
    * (1) one literal always evaluates to true, or
    * (2) it contains two literals that are equal except for their polarity. */
  final def trivial(c: Clause): Boolean = c.lits.exists(Literal.isTrue) || c.posLits.exists(l => c.negLits.exists(l2 => l.unsignedEquals(l2)))

  /** True iff this clause is ground. */
  @inline final def ground(c: Clause): Boolean = c.lits.forall(_.ground)
  /** True iff this clause is purely positive. i.e.
    * if all literals are positive. */
  @inline final def positive(c: Clause): Boolean = c.negLits.isEmpty
  /** True iff this clause is purely negative. i.e.
    * if all literals are negative. */
  @inline final def negative(c: Clause): Boolean = c.posLits.isEmpty

  /** True iff this clause is horn. */
  @inline final def horn(c: Clause): Boolean = c.posLits.length <= 1
  /** True iff this clause is a unit clause. */
  @inline final def unit(c: Clause): Boolean = c.lits.length == 1
  /** True iff this clause is a demodulator. */
  @inline final def demodulator(c: Clause): Boolean = c.posLits.length == 1 && c.negLits.isEmpty
  /** True iff this clause is a rewrite rule. */
  @inline final def rewriteRule(c: Clause): Boolean = demodulator(c) && c.posLits.head.oriented
  /** Returns the multiset of symbols occuring in the clause. */
  final def symbols(c: Clause): Multiset[Signature.Key] = c.lits.map(Literal.symbols).foldLeft(Multiset.empty[Signature.Key]){case (a,b) => a.sum(b)}
  /** Returns a representation of the clause `c` as term. */
  final def asTerm(c: Clause): Term = {
    val body = mkDisjunction(c.lits.map(Literal.asTerm))
    mkPolyUnivQuant(c.implicitlyBound.map(_._2), body)
  }
  /** Returns true iff all literals are well-typed. */
  final def wellTyped(c: Clause): Boolean = {
    import leo.datastructures.Literal.{wellTyped => wt}
    val litIt = c.lits.iterator
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (!wt(lit)) return false
    }
    true
  }
}

