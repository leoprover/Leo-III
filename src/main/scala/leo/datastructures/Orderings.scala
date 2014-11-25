package leo.datastructures

import leo.{ClauseOrdering, TermOrdering}
import leo.datastructures.term.Term

/**
 * Collection of Ordering relations of terms, clauses, etc.
 *
 * @author Alexander Steen
 * @since 20.08.14
 */



/////////////////////
// Clause Orderings
/////////////////////

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLOrdering_Lex_Weight_Age_Origin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = ((a.weight, a.id, a.origin)) compare ((b.weight, b.id, b.origin))
}


///////////////////////
/// Generic Orderings
///////////////////////

/** `SimpleOrdering`s are orderings that are induced by a weighting. */
class SimpleOrdering[A](weighting: Weight[A]) extends Ordering[A] {
  def compare(a: A, b: A) = weighting.weightOf(a) - weighting.weightOf(b)
}

/** Only for debugging and compiling purposes. Will be removed soon. */
object SenselessOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = 0
}
