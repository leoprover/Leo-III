package leo.modules.relevance_filter

import leo.datastructures.tptp.tff.Atomic
import leo.datastructures.{Role_Definition}
import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.thf.{Eq, Binary, Function}

/**
  *
  * Set of all formulas read from the problem file, but not yet added to the
  * proof.
  *
  * @since 3/9/2016
  * @author Max Wisniewski
  */
object PreFilterSet {
  import scala.collection._

  /**
    * Map of FormulaName to the Formula
    */
  private val unused : mutable.Map[String, AnnotatedFormula] = mutable.HashMap[String, AnnotatedFormula]()
  /**
    * List of the used symbols
    */
  private val usedSymbs : mutable.Set[String] = mutable.HashSet[String]()
  /**
    * Frequency of the used symbols
    */
  private val freq : FuncFrequency = new FuncFrequency()

  /**
    * Definition_name -> Set of used symbols
    */
  private val defn : mutable.Map[String, Set[String]] = mutable.HashMap[String, Set[String]]()

  /**
    * Variable -> Set(FormulaName)
    *
    * Indexes each Variable with its occurences in unsed.
    */
  private val reverse : mutable.Map[String, Set[String]] = mutable.HashMap[String, Set[String]]()


  /**
    * Adds Axioms, Conjectures and Definitions to the prefilter set.
    *
    * If definitions are used inside the formulas it is required
    * to add them beforehand.
    *
    * @param formula New TPTPFormula
    */
  def addNewFormula(formula : AnnotatedFormula) : Unit = synchronized {
    isDefinition(formula) match {
      case Some(name) =>
        // Delta search
        var todo = (formula.function_symbols - name).toIterator // Cut the own name away
        var delta_symbs : Set[String] = Set[String]() // All symbols with delta expansion
        while(todo.hasNext) {
          val next = todo.next()
          defn.get(next) match {                      // This method requires the input to be ordered correctly
            case Some(symbs) => delta_symbs |= symbs
            case None => delta_symbs += next
          }
        }
        defn.put(name, delta_symbs)
      case None =>
        unused.put(formula.name, formula)
        // Symbols from the delta exanded term
        expaned_symbols(formula).foreach{symb => reverse.put(symb, reverse.getOrElse(symb,Set[String]()) + formula.name)}
        freq.addFormula(formula)
    }
  }

  /**
    * Gets the frequency of a symbol, between the inserted formulas
    *
    * @param symbol The symbol we want to know the frequency of
    * @return The frequency.
    */
  def freq(symbol : String) : Int = synchronized(freq.apply(symbol))

  /**
    * A list of used symbols
    *
    * @return The list of used symbols
    */
  def usedSymbols : Set[String] = usedSymbs.toSet

  /**
    * Marks a TPTPFormula as used. And updates List of used symbols
    *
    * @param formula
    * @return A set of newly taken symbols
    */
  def useFormula(formula : AnnotatedFormula) : Set[String] = synchronized {
    unused.remove(formula.name)
    val f_symbols = expaned_symbols(formula)
    f_symbols.foreach{symb => reverse.get(symb).foreach{s =>
      val s1 = s - formula.name
      if(s.isEmpty){
        reverse.remove(symb)
      } else {
        reverse.put(symb, s1)
      }
    }}
    val newSymbs = f_symbols -- usedSymbs
    val it = newSymbs.iterator
    while(it.hasNext){
      val s = it.next
      usedSymbs.add(s)
    }
    newSymbs
  }

  /**
    * Returns all formulas of the PreFilterSet
    * @return
    */
  def getFormulas : Iterable[AnnotatedFormula] = synchronized(unused.values)

  /**
    * Returns all formulas of the PreFilterSet that share common
    * symbols.
    *
    * @param symb The symbol we want to obtain.
    * @return
    */
  def getCommonFormulas(symb : String) : Iterable[AnnotatedFormula] = synchronized{
    val names : Set[String] = reverse.getOrElse(symb, Set[String]())
    var s : Seq[AnnotatedFormula] = Seq()
    val it = names.iterator
    while(it.hasNext){
      val name = it.next()
      unused.get(name).foreach(f => s = f +: s)
    }
    s
  }

  def getCommonFormulas(symbs : Iterable[String]) : Iterable[AnnotatedFormula] = synchronized{
    symbs.flatMap(getCommonFormulas(_))
  }


  private def isDefinition(formula : AnnotatedFormula) : Option[String] = {
    if(formula.role != Role_Definition.pretty) None
    else {
      formula match {
        case f : THFAnnotated =>
          import leo.datastructures.tptp.thf.Logical
          f.formula match {
            case Logical(Binary(Function(name, Seq()), Eq, right)) => Some(name)
            case _ => None
        }
        case f : TFFAnnotated =>
          import leo.datastructures.tptp.tff.Logical
          f.formula match {
            case Logical(Atomic(Equality(Func(name, Nil),right))) => Some(name)
            case _ => None
          }
        case _ => None
      }
    }
  }

  private def expaned_symbols(formula : AnnotatedFormula) : Set[String] = {
    var todo = formula.function_symbols.toIterator
    var delta_symbs : Set[String] = Set[String]() // All symbols with delta expansion
    while(todo.hasNext) {
      val next = todo.next()
      defn.get(next) match {                      // This method requires the input to be ordered correctly
        case Some(symbs) => delta_symbs = (delta_symbs union symbs) + next // Also put "next" in to consider formulas that use
          // the same defined symbol
        case None => delta_symbs += next
      }
    }
    delta_symbs
  }

  /**
    * Checks in the PreFilterSet, if a formumla is still unused.
    *
    * @param formula The formula to check for
    * @return true, iff the formula is unused
    */
  def isUnused(formula : AnnotatedFormula) : Boolean = unused.contains(formula.name)

  def clear() = {
    unused.clear()
    usedSymbs.clear()
    freq.clear()
    defn.clear()
    reverse.clear()
  }
}
