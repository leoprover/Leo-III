package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.ClauseProxy


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
object CLPO_NonGoalsFirst extends ClauseProxyOrdering {
  def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.posLits.size)/(1+a.cl.lits.size)), a.cl.weight), (1 - ((1+b.cl.posLits.size)/(b.cl.lits.size+1)), b.cl.weight))
}