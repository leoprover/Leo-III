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
object CLPO_Lex_WeightAge extends ClauseProxyOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Int,Long]]].compare((a.cl.weight, a.id),(b.cl.weight, b.id))
}
object CLPO_GoalsFirst extends ClauseProxyOrdering {
  def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.negLits.size)/(1+a.cl.lits.size)), a.cl.weight), (1 - ((1+b.cl.negLits.size)/(b.cl.lits.size+1)), b.cl.weight))
}