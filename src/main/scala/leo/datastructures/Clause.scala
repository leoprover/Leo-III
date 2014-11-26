package leo.datastructures

import leo.Configuration
import leo.datastructures.term.Term

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
  /** The clause's weight. */
  def weight: Int = Configuration.CLAUSE_WEIGHTING.weightOf(this)
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin
  /** The types of the implicitly universally quantified variables. */
  def implicitBindings: Seq[Type]

  def isEmpty: Boolean = lits.isEmpty

  def compare(that: Clause) = Configuration.CLAUSE_ORDERING.compare(this, that)

  def map[A](f: Literal => A): Seq[A] = lits.map(f)
  def mapLit(f: Literal => Literal): Clause = Clause.mkClause(lits.map(f), Derived)

  lazy val pretty = s"[${lits.map(_.pretty).mkString(" , ")}]"

  lazy val toTerm: Term = ???
}

object Clause {
  import impl.{VectorClause => ClauseImpl}

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  def mkClause(lits: Iterable[Literal], implicitBindings: Seq[Type], origin: ClauseOrigin): Clause = ClauseImpl.mkClause(lits, implicitBindings, origin)
  def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = ClauseImpl.mkClause(lits, Seq(), origin)
  def mkDerivedClause(lits: Iterable[Literal], implicitBindings: Seq[Type]): Clause = mkClause(lits, implicitBindings, Derived)

  def lastClauseId: Int = ClauseImpl.lastClauseId
}

