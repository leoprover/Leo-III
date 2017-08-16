package leo.datastructures.impl.orderings

import leo.datastructures.ClauseProxy
import leo.datastructures.Signature
import leo.datastructures.ClauseProxyOrdering

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
class CPO_ConjRelativeSymbolWeight(conjSymbols: Set[Signature.Key], conjSymbolFactor: Float, varWeight: Int, symbWeight: Int) extends ClauseProxyOrdering[Double] {
  import leo.datastructures.Clause
  final def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aWeight = weightOf(a)
    val bWeight = weightOf(b)
    aWeight.compare(bWeight)
  }

  final def weightOf(cl: ClauseProxy): Double = {
    val symbols = Clause.symbols(cl.cl)
    val vars = Clause.vars(cl.cl)
    var weight: Float = 0f
    weight += vars.size * varWeight
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
class CPO_SymbolWeight(varWeight: Int, symbWeight: Int) extends ClauseProxyOrdering[Double] {
  import leo.datastructures.Clause
  final def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aWeight = weightOf(a)
    val bWeight = weightOf(b)
    aWeight.compare(bWeight)
  }
  final def weightOf(cl: ClauseProxy): Double = {
    val symbols = Clause.symbols(cl.cl)
    val vars = Clause.vars(cl.cl)
    var weight: Int = 0
    weight += vars.size * varWeight
    weight += symbols.size * symbWeight
    weight
  }
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its literal count is smaller. */
object CPO_SmallerFirst extends ClauseProxyOrdering[Double] {
  final def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aWeight = weightOf(a); val bWeight = weightOf(b)
    aWeight.compareTo(bWeight)
  }

  final def weightOf(cl: ClauseProxy): Double = cl.cl.lits.size
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its `id` is smaller. */
object CPO_OldestFirst extends ClauseProxyOrdering[Double] {
  final def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aAge = a.id; val bAge = b.id
    aAge.compareTo(bAge)
  }

  final def weightOf(cl: ClauseProxy): Double = cl.id
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its ratio of negative literals is greater.
  * Empty clauses are smaller then every other clause.
  * E.g. a clause with five literals, thereof four negative, is smaller than a clause with two literals, there one negative. */
object CPO_GoalsFirst2 extends ClauseProxyOrdering[Double] {
  def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aGoalRatio: Float = if (a.cl.lits.isEmpty) -1 else a.cl.posLits.size.toFloat/a.cl.lits.size.toFloat
    val bGoalRatio: Float = if (b.cl.lits.isEmpty) -1 else b.cl.posLits.size.toFloat/b.cl.lits.size.toFloat
    aGoalRatio.compareTo(bGoalRatio)
  }

  final def weightOf(cl: ClauseProxy): Double = if (cl.cl.lits.isEmpty) -1 else cl.cl.posLits.size.toFloat/cl.cl.lits.size.toFloat
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if its ratio of postive literals is greater.
  * Empty clauses are smaller then every other clause.
  * E.g. a clause with five literals, thereof four positive, is smaller than a clause with two literals, there one positive. */
object CPO_NonGoalsFirst2 extends ClauseProxyOrdering[Double] {
  def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aGoalRatio: Float = if (a.cl.lits.isEmpty) -1 else a.cl.negLits.size.toFloat/a.cl.lits.size.toFloat
    val bGoalRatio: Float = if (b.cl.lits.isEmpty) -1 else b.cl.negLits.size.toFloat/b.cl.lits.size.toFloat
    aGoalRatio.compareTo(bGoalRatio)
  }

  final def weightOf(cl: ClauseProxy): Double = if (cl.cl.lits.isEmpty) -1 else cl.cl.negLits.size.toFloat/cl.cl.lits.size.toFloat
}

/** Ordering in which a [[leo.datastructures.ClauseProxy]] is smaller, if it is marked as member of
  * the set of support. If both are marked that way, they are equal in this ordering.
  * @see [[leo.datastructures.ClauseAnnotation.PropSOS]] */
object CPO_SOSFirst extends ClauseProxyOrdering[Double] {
  final def compare(a: ClauseProxy, b: ClauseProxy): Int = {
    val aWeight = weightOf(a)
    val bWeight = weightOf(b)
    aWeight.compare(bWeight)
  }

  final def weightOf(cl: ClauseProxy): Double = {
    import leo.datastructures.isPropSet
    import leo.datastructures.ClauseAnnotation.PropSOS
    val prop = cl.properties
    if (isPropSet(PropSOS, prop)) 0
    else 1000
  }
}
