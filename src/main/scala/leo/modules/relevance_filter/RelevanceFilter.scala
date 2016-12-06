package leo.modules.relevance_filter

import leo.datastructures.tptp.Commons.AnnotatedFormula

/**
  *
  *
  * Implements the Relevance Filter of C. Paulson 'Lightweight Relevance Filtering for Machine-Generated Resolution Problems'
  *
  */
object RelevanceFilter {

  val std_passmark = 0.6  // TODO Experiment, but these are the values of the 'Lightweight Relevance Filtering for Machine-Generated Resolution Problems' paper
  val std_aging = 2.4

  /**
    * Applies a relevance filter for the standardvalue
    * of the passmark [[std_passmark]] and the aging factor [[std_aging]].
    * And assumes, that it is the first round of the filter process.
    * @param formula The formula to look at in the filtering
    * @return true, iff the formula
    */
  def apply(formula : AnnotatedFormula) : Boolean = apply(0)(formula)

  /**
    * Applies a relevance filter for the standardvalue
    * of the passmark [[std_passmark]] and the aging factor [[std_aging]].
    *
    * @param phase The phase we are in
    * @param formula The formula we want to filter
    * @return true iff the formula should be taken
    */
  def apply(phase : Int)(formula: AnnotatedFormula) : Boolean = apply(std_passmark)(std_aging)(phase)(formula)

  /**
    *
    * Applies a relevance filter for a given passmark and aging factor for the round.
    *
    *
    * @param passmark The initial passmark
    * @param aging An aging factor to increase the passmark
    * @param phase The current phase of the filtering
    * @param formula The formula we want to filter
    * @return true iff the formula should be taken
    */
  def apply(passmark : Double)(aging : Double)(phase : Int)(formula : AnnotatedFormula) : Boolean = {
    val symbs = formula.function_symbols
    if(symbs.size == 0) return true  // TODO is there a better solution?
    val common_symbols = PreFilterSet.usedSymbols & symbs
    val size = common_symbols.size
    val new_size = formula.function_symbols.size - size

    var sum = symbs.foldRight(0 : Double){case (symb, m) => m + frequency_fun(PreFilterSet.freq(symb).toDouble)}

    var pass = passmark
    var n = 0
    while(n < phase){
      pass = pass + ((1-pass)/aging)
      n += 1
    }

    val grade = sum / (sum + new_size)
    leo.Out.finest(s"$formula : \n  $grade >= $pass\n ${if(grade >= pass) "taken" else "not taken"}")

    grade >= pass
  }

  /**
    * Definition from the 'Lightweight Relevance Filtering for Machine-Generated Resolution Problems' paper
    * @param n amount of occurences for a symbol
    * @return a smooth frequency value
    */
  private def frequency_fun(n : Double) : Double = 1 + 2/Math.log(n+1)
}
