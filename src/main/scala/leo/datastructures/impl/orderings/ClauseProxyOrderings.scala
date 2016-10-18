package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.ClauseProxy


  object CPO_FIFO extends ClauseProxyOrdering {
    def compare(x: ClauseProxy, y: ClauseProxy): Int = x.id compare y.id
  }

  object CPO_WeightAge extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Int,Long]]].compare((a.weight, a.id),(b.weight, b.id))
  }

  object CPO_GoalsFirst extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.negLits.size)/(1+a.cl.lits.size)), a.weight), (1 - ((1+b.cl.negLits.size)/(b.cl.lits.size+1)), b.weight))
  }

  object CPO_NonGoalsFirst extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.posLits.size)/(1+a.cl.lits.size)), a.weight), (1 - ((1+b.cl.posLits.size)/(b.cl.lits.size+1)), b.weight))
  }

