package leo.modules.relevance_filter

import leo.datastructures.TPTPAST.AnnotatedFormula

import scala.collection.mutable._

/**
  * Created by mwisnie on 3/9/16.
  */
class FuncFrequency {
  val symbols : Map[String, Int] = HashMap[String, Int]()

  /**
    * Updates every smybol in a formula.
    *
    * @param formula New formula from the problem
    */
  def addFormula(formula: AnnotatedFormula) : Unit = {
    val it = formula.symbols.iterator
    while(it.hasNext){
      val symb = it.next()
        val freq = symbols.getOrElse(symb,0: Int)
        symbols.put(symb, freq+1)
    }
  }

  /**
    * Returns the number of formulas a symbol is occuring in
    *
    * @param symbol The symbol we want to know the number of occurences
    * @return The number of occurences
    */
  def apply(symbol : String) : Int = symbols.getOrElse(symbol, 0 : Int)

  def clear() : Unit = symbols.clear()
}
