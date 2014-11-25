package leo.datastructures

import leo.Configuration

/**
 * Clause interface, the companion object `Clause` offers several constructors methods.
 * The `id` of a clause is unique and is monotonously increasing.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Clause extends Ordered[Clause] {
  /** The unique, increasing clause number. */
  def id: Int
  /** The underlying sequence of literals. */
  def lits: Seq[Literal]
  /** The clause's weight. */
  def weight: Int = Configuration.CLAUSE_WEIGHTING.weightOf(this)
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin

  def compare(that: Clause) = Configuration.CLAUSE_ORDERING.compare(this, that)
}

object Clause {
  import impl.{VectorClause => ClauseImpl}

  /** Create a clause containing the set of literals `lits` with origin `origin`. */
  def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = ClauseImpl.mkClause(lits, origin)
  def mkDerivedClause(lits: Iterable[Literal]): Clause = mkClause(lits, Derived)

  def lastClauseId: Int = ClauseImpl.lastClauseId
}

