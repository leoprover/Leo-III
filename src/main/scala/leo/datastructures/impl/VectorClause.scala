package leo.datastructures.impl

import leo.datastructures._


// TODO
// Keep meta vars, transform to bound? transform bound to meta?
/**
 * Preliminary implementation of clauses using indexed linear sequences (vectors).
 *
 * @author Alexander Steen
 * @since 23.11.2014
 */
 abstract sealed class VectorClause extends Clause {
  /** The types of the implicitly universally quantified variables. */
  final val implicitlyBound: Set[(Type, Int)] = lits.map(_.metaVars).toSet.flatten

  /** Those literals in `lits` that are positive. */
  @inline final val posLits: Seq[Literal] = lits.filter(_.polarity)
  /** Those literals in `lits` that are negative. */
  @inline final val negLits: Seq[Literal] = lits.filter(!_.polarity)

  /** True iff this clause is horn. */
  @inline final val horn: Boolean = posLits.length <= 1
  /** True iff this clause is a unit clause. */
  @inline final val unit: Boolean = lits.length == 1
  /** True iff this clause is a demodulator. */
  @inline final val demodulator: Boolean = posLits.length == 1 && negLits.isEmpty
  /** True iff this clause is a rewrite rule. */
  @inline final val rewriteRule: Boolean = demodulator && posLits.head.oriented
  /** True iff this clause is ground. */
  @inline final val ground: Boolean = lits.view.forall(_.ground)
  /** True iff this clause is purely positive. i.e.
    * if all literals are positive. */
  @inline final val positive: Boolean = negLits.isEmpty
  /** True iff this clause is purely negative. i.e.
    * if all literals are negative. */
  @inline final val negative: Boolean = posLits.isEmpty

  @inline final val empty: Boolean = lits.isEmpty
  @inline final val effectivelyEmpty: Boolean = lits.isEmpty || lits.forall(_.flexflex)
}

object VectorClause {
  private var clauseCounter : Int = 0

  final def mkClause(lits: Iterable[Literal], origin: ClauseOrigin): Clause = {
    clauseCounter += 1
    new VectorClause0(clauseCounter, lits, origin)
  }

  @inline final def lastClauseId = clauseCounter

  private class VectorClause0(val id: Int, literals: Iterable[Literal], val origin: ClauseOrigin) extends VectorClause {
    lazy val lits = literals.toVector
  }
}
