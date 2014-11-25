package leo.datastructures

import leo.TermOrdering
import leo.datastructures.term.Term

/**
 * Collection of Ordering relations of terms, clauses, etc.
 *
 * @author Alexander Steen
 * @since 20.08.14
 */

/** `SimpleOrdering`s are orderings that are induced by a weighting. */
class SimpleOrdering[A](weighting: Weight[A]) extends Ordering[A] {
  def compare(a: A, b: A) = weighting.weightOf(a) - weighting.weightOf(b)
}

/** Only for debugging and compiling purposes. Will be removed soon. */
object SenselessOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = 0
}
