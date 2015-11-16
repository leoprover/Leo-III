package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.{ClauseOrigin, Clause}

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLO_Lex_WeightAgeOrigin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = implicitly[Ordering[Tuple3[Int,Int,ClauseOrigin]]].compare((a.weight, a.id, a.origin),(b.weight, b.id, b.origin))
}
