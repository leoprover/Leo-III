package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.{Clause, ClauseOrigin, ClauseProxy}

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLO_Lex_WeightAgeOrigin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = implicitly[Ordering[Tuple3[Int,Int,ClauseOrigin]]].compare((a.weight, a.id, a.origin),(b.weight, b.id, b.origin))
}

object CLPO_FIFO extends ClauseProxyOrdering {
  def compare(x: ClauseProxy, y: ClauseProxy): Int = x.id compare y.id
}
//object CLPO_Lex_WeightAge extends ClauseProxyOrdering {
//  import scala.math.Ordered.orderingToOrdered
//  def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Int,Int]]].compare((a.cl.weight, a.id),(b.cl.weight, b.id))
//}

//object PreferGoals extends ClauseProxyOrdering {
//  def compare(x: ClauseProxy, y: ClauseProxy): Int =
//}