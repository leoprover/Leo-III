package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.Clause

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLO_Lex_WeightAgeOrigin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = ((a.weight, a.id, a.origin)) compare ((b.weight, b.id, b.origin))
}
