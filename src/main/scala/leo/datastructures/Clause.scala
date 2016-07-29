package leo.datastructures

import leo.Configuration
import Literal.{LitMaxFlag, LitMax, LitStrictlyMax}

/**
 * Clause interface, the companion object `Clause` offers several constructors methods.
 * The `id` of a clause is unique and is monotonously increasing.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Clause extends Ordered[Clause] with Pretty {
  /** The unique, increasing clause number. */
  def id: Int
  /** The underlying sequence of literals. */
  def lits: Seq[Literal]
  /** The types of the implicitly universally quantified variables. */
  def implicitlyBound: Seq[(Int, Type)]
  def maxImplicitlyBound: Int
  /** The implicitly (universally) quantified type variables.
    * It is assumed that we are in rank-1 polymorphism. */
  def typeVars: Set[Int]
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin

  // Further properties
  /** Those literals in `lits` that are positive. */
  def posLits: Seq[Literal]
  /** Those literals in `lits` that are negative. */
  def negLits: Seq[Literal]

  def maxLitsMap: Map[LitMaxFlag, Seq[Literal]]

  @inline final def maxLits: Seq[Literal] = maxLitsMap(LitMax)
  @inline final def strictlyMaxLits: Seq[Literal] = maxLitsMap(LitStrictlyMax)

  // Operations on clauses
  def substitute(s : Subst) : Clause = Clause.mkClause(lits.map(_.substitute(s)))

  @inline final def map[A](f: Literal => A): Seq[A] = lits.map(f)
  @inline final def mapLit(f: Literal => Literal): Clause = Clause.mkClause(lits.map(f), Derived)
  @inline final def replace(what: Term, by: Term): Clause = Clause.mkClause(lits.map(_.replaceAll(what, by)))

  /** The clause's weight. */
  @inline final def weight: Int = Configuration.CLAUSE_WEIGHTING.weightOf(this)
  @inline final def compare(that: Clause) = Configuration.CLAUSE_ORDERING.compare(this, that)

  final lazy val pretty = s"[${lits.map(_.pretty).mkString(" , ")}]"


  // System function adaptions
  override final def equals(obj : Any): Boolean = obj match {
    case co : Clause => (lits forall {co.lits.contains}) && (co.lits forall {lits.contains})
    case _ => false
  }
  override final def hashCode(): Int = if (lits.isEmpty) 0
  else lits.tail.foldLeft(lits.head.hashCode()){case (h,l) => h^l.hashCode()}
}

object Clause {
  import impl.{VectorClause => ClauseImpl}
  import Literal.{LitMax, LitStrictlyMax}

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
  @inline final val empty = mkClause(Seq.empty)

  /** Returns the last clause id that has been issued. */
  @inline final def lastClauseId: Int = ClauseImpl.lastClauseId

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
}

