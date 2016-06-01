package leo.modules.indexing

import leo.datastructures.{Clause, Literal, FixedLengthTrie}
import leo.datastructures.impl.Signature
/**
  * Created by lex on 28.02.16.
  */
object FVIndex {
  final private val fvTrie: FixedLengthTrie[ClauseFeature, Clause] = FixedLengthTrie()
  def add(cl: Clause, featureVector: FeatureVector): Unit = {
    fvTrie.insert(featureVector, cl)
  }
  def asTrie: FixedLengthTrie[ClauseFeature, Clause] = fvTrie

  @inline final def posLitsFeature(cl: Clause): Int = {
    val litIt = cl.lits.iterator
    var n = 0
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (!lit.flexHead) {
        if (lit.polarity)
          n = n + 1
      }
    }
    n
  }
  @inline final def negLitsFeature(cl: Clause): Int = {
    val litIt = cl.lits.iterator
    var n = 0
    while (litIt.hasNext) {
      val lit = litIt.next()
      if (!lit.flexHead) {
        if (!lit.polarity)
          n = n + 1
      }
    }
    n
  }
  @inline final def posLitsSymbolCountFeature(symb: Signature#Key, cl: Clause): Int = countSymbol(symb, cl.posLits)
  @inline final def negLitsSymbolCountFeature(symb: Signature#Key, cl: Clause): Int = countSymbol(symb, cl.negLits)
  @inline final def posLitsSymbolDepthFeature(symb: Signature#Key, cl: Clause): Int = maxDepthOfSymbol(symb, cl.posLits)
  @inline final def negLitsSymbolDepthFeature(symb: Signature#Key, cl: Clause): Int = maxDepthOfSymbol(symb, cl.negLits)

  final private def countSymbol(symb: Signature#Key, lits: Seq[Literal]): Int = {
    var count = 0
    val litsIt = lits.iterator
    while (litsIt.hasNext) {
      val lit = litsIt.next()
      count = count + lit.left.symbolFreqOf(symb)
      count = count + lit.right.symbolFreqOf(symb)
    }
    count
  }
  final private def maxDepthOfSymbol(symb: Signature#Key, lits: Seq[Literal]): Int = {
    var depth = 0
    val litsIt = lits.iterator
    while (litsIt.hasNext) {
      val lit = litsIt.next()
      depth = Math.max(depth,lit.left.symbolDepthOf(symb))
      depth = Math.max(depth,lit.right.symbolDepthOf(symb))
    }
    depth
  }
}
