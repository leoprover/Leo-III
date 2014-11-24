package leo.datastructures

/**
 * Clause interface, the companion object `Clause` offers several constructors methods.
 * The `id` of a clause is unique and is monotonously increasing.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Clause {
  /** The unique, increasing clause number. */
  def id: Int
  /** The underlying sequence of literals. */
  def lits: Seq[Literal]
  /** The clause's weight. */
  def weight: Int
  /** The source from where the clause was created, See `ClauseOrigin`. */
  def origin: ClauseOrigin
}

object Clause {
  def mkClause(lits: Seq[Literal], origin: ClauseOrigin): Clause = ???
  def mkDerivedClause(lits: Seq[Literal]): Clause = mkClause(lits, Derived)

  def lastClauseId: Int = ???
}

