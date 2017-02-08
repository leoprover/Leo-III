package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.ClauseProxy
import leo.datastructures.Signature

  @deprecated("Use CPO_OldestFirst instead", "8.2.17")
  object CPO_FIFO extends ClauseProxyOrdering {
    def compare(x: ClauseProxy, y: ClauseProxy): Int = x.id compare y.id
  }
  @deprecated("Use CPO_SmallerFirst instead", "8.2.17")
  object CPO_WeightAge extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Int,Long]]].compare((a.weight, a.id),(b.weight, b.id))
  }
  @deprecated("Use CPO_GoalsFirst2 instead", "8.2.17")
  object CPO_GoalsFirst extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.negLits.size)/(1+a.cl.lits.size)), a.weight), (1 - ((1+b.cl.negLits.size)/(b.cl.lits.size+1)), b.weight))
  }
  @deprecated("Use CPO_NonGoalsFirst2 instead", "8.2.17")
  object CPO_NonGoalsFirst extends ClauseProxyOrdering {
    def compare(a: ClauseProxy, b: ClauseProxy) = implicitly[Ordering[Tuple2[Double, Int]]].compare((1 - ((1+a.cl.posLits.size)/(1+a.cl.lits.size)), a.weight), (1 - ((1+b.cl.posLits.size)/(b.cl.lits.size+1)), b.weight))
  }

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its symbol weight smaller.
  * Symbolweight `w(c)` of a clause `c` is calculated by `w(c) = w(fv(c)) + Σ w(l_i)`, where
  *  - `(l_i)_i` are the literals of `c`,
  *  - `fv(c)` are the free variables (implicitly universally quantified variables) of `c`,
  *  - `w({v_i})` is the weight of the free variables given by `w({v_i}) = |{v_i}| * varWeight`
  *  - `w(l_i)` is the weight of a literal `l_i` given by `w(l_i) = Σ_{s ∈ symbols(l_i).distinct} w(s) * symbols(l_i).mult(s)`
  *     (where `symbols(l_i)` is a multiset of symbol occurrences in `l_i`),
  *  - `w(s)` is the weight of a symbol given by `w(s) = symbWeight` if `s` does not occur in the conjecture and
  *    `w(s) = symbWeight * conjSymbolFactor` otherwise.
  *
  * @see [[leo.datastructures.Multiset]] for multiset operations `mult`, `distinct`, etc.
  */
class CPO_ConjRelativeSymbolWeight(conjSymbols: Set[Signature#Key], conjSymbolFactor: Float, varWeight: Int, symbWeight: Int) extends ClauseProxyOrdering {
  import leo.datastructures.Clause
  final def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aWeight = computeWeight(a.cl)
    val bWeight = computeWeight(b.cl)
    aWeight.compare(bWeight)
  }

  private[this] final def computeWeight(cl: Clause): Float = {
    val symbols = Clause.symbols(cl)
    var weight: Float = 0f
    weight += cl.implicitlyBound.size * varWeight
    val it = symbols.distinctIterator
    while(it.hasNext) {
      val symb = it.next()
      if (conjSymbols.contains(symb))
        weight += symbols.multiplicity(symb) * symbWeight * conjSymbolFactor
      else
        weight += symbols.multiplicity(symb) * symbWeight
    }
    weight
  }
}
/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its symbol weight smaller.
  * Symbolweight `w(c)` of a clause `c` is calculated by `w(c) = w(fv(c)) + Σ w(l_i)`, where
  *  - `(l_i)_i` are the literals of `c`,
  *  - `fv(c)` are the free variables (implicitly universally quantified variables) of `c`,
  *  - `w({v_i})` is the weight of the free variables given by `w({v_i}) = |{v_i}| * varWeight`
  *  - `w(l_i)` is the weight of a literal `l_i` given by `w(l_i) = symbols(l_i).size * symbWeight`
  *     (where `symbols(l_i)` is a multiset of symbol occurrences in `l_i`).
  *
  * @see [[leo.datastructures.Multiset]] for multiset operations `size` etc.
  */
class CPO_SymbolWeight(varWeight: Int, symbWeight: Int) extends ClauseProxyOrdering {
  import leo.datastructures.Clause
  final def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aWeight = computeWeight(a.cl)
    val bWeight = computeWeight(b.cl)
    aWeight.compare(bWeight)
  }
  private[this] final def computeWeight(cl: Clause): Int = {
    val symbols = Clause.symbols(cl)
    var weight: Int = 0
    weight += cl.implicitlyBound.size * varWeight
    weight += symbols.size * symbWeight
    weight
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its literal count is smaller. */
object CPO_SmallerFirst extends ClauseProxyOrdering {
  final def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aLitsCounts = a.cl.lits.size; val bLitsCounts = b.cl.lits.size
    aLitsCounts.compareTo(bLitsCounts)
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its `id` is smaller. */
object CPO_OldestFirst extends ClauseProxyOrdering {
  final def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aAge = a.id; val bAge = b.id
    aAge.compareTo(bAge)
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its ratio of negative literals is greater.
  * Empty clauses are smaller then every other clause.
  * E.g. a clause with five literals, thereof four negative, is smaller than a clause with two literals, there one negative. */
object CPO_GoalsFirst2 extends ClauseProxyOrdering {
  def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aGoalRatio: Float = if (a.cl.lits.isEmpty) -1 else a.cl.posLits.size.toFloat/a.cl.lits.size.toFloat
    val bGoalRatio: Float = if (b.cl.lits.isEmpty) -1 else b.cl.posLits.size.toFloat/b.cl.lits.size.toFloat
    aGoalRatio.compareTo(bGoalRatio)
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its ratio of postive literals is greater.
  * Empty clauses are smaller then every other clause.
  * E.g. a clause with five literals, thereof four positive, is smaller than a clause with two literals, there one positive. */
object CPO_NonGoalsFirst2 extends ClauseProxyOrdering {
  def compare(a: ClauseProxy, b: ClauseProxy) = {
    val aGoalRatio: Float = if (a.cl.lits.isEmpty) -1 else a.cl.negLits.size.toFloat/a.cl.lits.size.toFloat
    val bGoalRatio: Float = if (b.cl.lits.isEmpty) -1 else b.cl.negLits.size.toFloat/b.cl.lits.size.toFloat
    aGoalRatio.compareTo(bGoalRatio)
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if it is marked as member of
  * the set of support. If both are marked that way, they are equal in this ordering.
  * @see [[leo.datastructures.ClauseAnnotation.PropSOS]] */
object CPO_SOSFirst extends ClauseProxyOrdering {
  final def compare(a: ClauseProxy, b: ClauseProxy) = {
    import leo.datastructures.isPropSet
    import leo.datastructures.ClauseAnnotation.PropSOS
    val aProp = a.properties; val bProp = b.properties
    if (isPropSet(PropSOS, aProp)) {
      if (isPropSet(PropSOS, bProp)) 0
      else -1
    } else {
      if (isPropSet(PropSOS, bProp)) 1
      else 0
    }
  }
}
