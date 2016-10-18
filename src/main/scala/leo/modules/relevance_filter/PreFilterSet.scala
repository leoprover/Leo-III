package leo.modules.relevance_filter

import leo.datastructures.Signature
import leo.datastructures.tptp.tff.Atomic
import leo.datastructures.{Role_Type, Role_Definition}
import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.thf.{Eq, Term, Binary}
import leo.modules.parsers.InputProcessing

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
    * Filtered definitions
    */
  private val defn : mutable.Map[String, AnnotatedFormula] = mutable.HashMap[String, AnnotatedFormula]()

  /**
    * Variable -> Set(FormulaName)
    *
    * Indexes each Variable with its occurences in unsed.
    */
  private val reverse : mutable.Map[String, Set[String]] = mutable.HashMap[String, Set[String]]()


  /**
    * Adds a new TPTPFormula to the set of Unused Formulas.
    *
    * @param formula New TPTPFormula
    */
  def addNewFormula(formula : AnnotatedFormula)(implicit sig: Signature) : Unit = synchronized {
    if(formula.role == Role_Type.pretty){
      InputProcessing.process(sig)(formula)
    } else {
      isDefinition(formula) match {
        // TODO Not immediatly add new typ definitions, but postpone
        case Some(name) => defn.put(name, formula)
        case None =>
          unused.put(formula.name, formula)
          formula.function_symbols.foreach{symb => reverse.put(symb, reverse.getOrElse(symb,Set[String]()) + formula.name)}
          freq.addFormula(formula)
      }
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
  def useFormula(formula : AnnotatedFormula)(implicit sig: Signature) : Set[String] = synchronized {
    unused.remove(formula.name)
    formula.function_symbols.foreach{symb => reverse.get(symb).foreach{s =>
      val s1 = s - formula.name
      if(s.isEmpty){
        reverse.remove(symb)
      } else {
        reverse.put(symb, s1)
      }
    }}
    val newSymbs = formula.function_symbols -- usedSymbs
    val it = newSymbs.iterator
    while(it.hasNext){
      val s = it.next
      usedSymbs.add(s)
      defn.get(s).foreach(defi => InputProcessing.process(sig)(defi))
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
          case Logical(Binary(Term(Func(name, Seq())), Eq, right)) => Some(name)
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
