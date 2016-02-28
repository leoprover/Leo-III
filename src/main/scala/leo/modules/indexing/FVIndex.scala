package leo.modules.indexing

import leo.datastructures.{Clause, Literal, FixedLengthTrie}
import leo.datastructures.impl.Signature
/**
  * Created by lex on 28.02.16.
  */
object FVIndex {
  val fvTrie: FixedLengthTrie[ClauseFeature, Clause] = FixedLengthTrie()
  var initialized = false

  def initIndex(maxNumberOfFeatures: Int): Unit = {
    assert(!initialized)


    initialized = true
  }
  def registerSymbols(symbols: Set[Signature#Key]): Unit = ???
  def add(cl: Clause, featureVector: FeatureVector): Unit = ???
  def asTrie: FixedLengthTrie[ClauseFeature, Clause] = ???

  @inline final def posLitsFeature(cl: Clause): Int = cl.posLits.size
  @inline final def negLitsFeature(cl: Clause): Int = cl.negLits.size
  @inline final def posLitsSymbolCountFeature(symb: Signature#Key, cl: Clause): Int = countSymbol(symb, cl.posLits)
  @inline final def negLitsSymbolCountFeature(symb: Signature#Key, cl: Clause): Int = countSymbol(symb, cl.negLits)
  @inline final def posLitsSymbolDepthFeature(symb: Signature#Key, cl: Clause): Int = maxDepthOfSymbol(symb, cl.posLits)
  @inline final def negLitsSymbolDepthFeature(symb: Signature#Key, cl: Clause): Int = maxDepthOfSymbol(symb, cl.negLits)

  final private def countSymbol(symb: Signature#Key, lits: Seq[Literal]): Int = {
//    lits.head.left.
    ???
  }
  final private def maxDepthOfSymbol(symb: Signature#Key, lits: Seq[Literal]): Int = ???
}
