package leo.modules.relevance

import leo.datastructures.TPTP.AnnotatedFormula

final class SymbolDistribution {
  import scala.collection.mutable
  private[this] val termSymbolFrequency: mutable.Map[String, Int] = mutable.Map.empty.withDefaultValue(0)

  def add(formula: AnnotatedFormula): Unit = {
    val symbolsInFormula = formula.symbols
    symbolsInFormula.foreach { s =>
      val currentFreq = termSymbolFrequency(s)
      termSymbolFrequency += (s -> (currentFreq + 1))
    }
  }
  def addAll(formulas: Iterable[AnnotatedFormula]): Unit = formulas.foreach(add)
  def increment(symbol: String): Unit = {
    val currentFreq = termSymbolFrequency(symbol)
    termSymbolFrequency += (symbol -> (currentFreq + 1))
  }
  def incrementAll(symbols: Iterable[String]): Unit = symbols.foreach(increment)

  @inline def apply(symbol: String): Int = getFrequency(symbol)
  @inline def getFrequency(symbol: String): Int = termSymbolFrequency(symbol)

  @inline def size: Int = termSymbolFrequency.size

  override final def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("Symbol distribution = {\n")
    termSymbolFrequency.toSeq.sortBy(_._2).foreach { entry =>
      sb.append(s"\t${entry._1}: ${entry._2},\n")
    }
    sb.append("}")
    sb.toString
  }
}
object SymbolDistribution {
  final def empty: SymbolDistribution = new SymbolDistribution
  final def apply(formulas: Iterable[AnnotatedFormula]): SymbolDistribution = {
    val sd = empty
    sd.addAll(formulas)
    sd
  }
}