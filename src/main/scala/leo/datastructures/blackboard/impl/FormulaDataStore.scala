package leo.datastructures.blackboard.impl

import leo.datastructures.Clause
import leo.datastructures.blackboard._
import leo.datastructures.context.impl.{TreeContextMap, TreeContextSet}
import leo.datastructures.context.{ContextMap, ContextSet, Context}
import leo.modules.calculus.TrivRule

/**
 *
 * A simple tree context set implementation.
 *
 * Stores all formulas (enriched clauses) in the program.
 *
 */
object FormulaDataStore extends DataStore {

  /**
   * <p>
   * Returns a List of all Formulas of the Blackboard.
   * </p>
   *
   * @return All formulas of the blackboard.
   */
  def getFormulas: Iterable[FormulaStore] = FormulaSet.getAll

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  def getAll(p: FormulaStore => Boolean): Iterable[FormulaStore] = FormulaSet.getAll.filter(p)


  /**
   * <p>
   * Filters the formulas of a given context.
   * </p>
   *
   * @param c - A given Context
   * @param p Predicate the formulas have to satisfy
   * @return All formulas in `c` satisfying `p`
   */
  def getAll(c: Context)(p: (FormulaStore) => Boolean): Iterable[FormulaStore] = FormulaSet.getAll(c).filter(p)


  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  def getFormulaByName(name: String): Option[FormulaStore] = FormulaSet.getName(name)


  /**
   * <p>
   * Adds a formula to the blackboard, if it does not exist. If it exists
   * the old formula is returned.
   * </p>
   *
   * @param formula to be added.
   * @return The inserted Formula, or the already existing one.
   */
  def addFormula(formula : FormulaStore) : Boolean = {
    val f = FormulaSet.add(formula)
    // TODO: handle merge
    f
  }

  /**
   * Adds a formula to the Blackboard.
   * Returns true, if the adding was successful
   * and false, if the formula already existed.
   *
   * @param formula - New to add formula
   * @return true if the formula was not contained in the blackboard previously
   */
  def addNewFormula(formula : FormulaStore) : Boolean = {
    // TODO: Implement Sets to check containment of Clauses.
    if(TrivRule.teqt(formula.clause)) return false
    if (FormulaSet.getAll(formula.context).exists(_.cong(formula)))
      false
    else {
      FormulaSet.add(formula)
      true
    }
  }

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
   * @return true if the formula was removed, false if the formula does not exist.
   */
  def removeFormula(formula: FormulaStore): Boolean = FormulaSet.rm(formula)



  /**
   * <p>
   * Removes a Formula by its name.
   * </p>
   *
   * @param name - Name of the Formula to be removed
   * @return true, iff the element existed.
   */
  def rmFormulaByName(name: String): Boolean = FormulaSet.rmName(name)



  /**
   *
   * <p>
   * Retrieves all formulas in a given context.
   * </p>
   *
   * @param c - A given Context
   * @return All formulas in the context `c`
   */
  def getFormulas(c: Context): Iterable[FormulaStore] = FormulaSet.getAll(c)

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p: FormulaStore => Boolean) = FormulaSet.getAll.foreach{f => if(p(f)) FormulaSet.rm(f)}


  /**
   * <p>
   * Removes all formulas in the context `c` satisfiying `p`.
   * </p>
   * @param c - A given Context
   * @param p - Predicate the formulas have to satisfy
   * @return Removes all formulas in `c` satisfying `p`
   */
  def rmAll(c: Context)(p: FormulaStore => Boolean): Unit = FormulaSet.getAll(c) foreach {f => if(p(f)) FormulaSet.rm(f)}

  /**
   * Stores the formulas in first in a map from name to the @see{FormulaStore}
   * and secondly a map from @see{Term} to FormulaStore, to see, if a formula has already bee added.
   *
   */
  private object FormulaSet {

    private val formulaSet : ContextSet[FormulaStore] = new TreeContextSet[FormulaStore]()
    private val clauseMap : ContextMap[Clause, FormulaStore] = new TreeContextMap[Clause,FormulaStore]()

    /**
     * Looks up the termMap, for an already existing store and returns this or the given store
     * after adding it.
     *
     * @return the existing store or the new one
     */
    def add(f : FormulaStore) : Boolean = formulaSet.synchronized {
      formulaSet get (f,f.context) match {
        case Some(f1) =>
          false
        case None =>
          clauseMap lookup (f.clause, f.context) match {
            case Some(f1) =>
              false
            case None =>
              formulaSet.add(f, f.context)
              clauseMap.put(f.clause, f, f.context)
              true
          }
      }
    }

    /**
     * ATM no filter support added (no optimization).
     * Therefor we can savely return everything and filter later.
     *
     * @return All stored formulas
     */
    def getAll : Iterable[FormulaStore] = formulaSet.synchronized(formulaSet.getAll)

    def getAll(c : Context) : Iterable[FormulaStore] = formulaSet.synchronized(formulaSet.getAll(c))

    def rm(f : FormulaStore) : Boolean = formulaSet.synchronized {
      if(formulaSet.remove(f, f.context)) {
        clauseMap.remove(f.clause, f.context)
        return true
      }
      return false
    }

    def rmName(n : String) : Boolean = formulaSet.synchronized {
      formulaSet.getAll.find {f => f.name == n} match {
        case None => false
        case Some(f) =>
          val r = formulaSet.remove(f, f.context)
          if(r) clauseMap.remove(f.clause, f.context)
          r
      }
    }

    def getName(n : String) : Option[FormulaStore] = formulaSet.synchronized(formulaSet.getAll.find {f => f.name == n})

    def contains(f : FormulaStore) : Boolean = formulaSet.synchronized{
      val oc = clauseMap.lookup(f.clause,f.context)
      //oc.foreach{f1 => leo.Out.comment(s"[FormulaStore]: contains(${f.pretty}) is true.\n  Found ${f1.pretty} ")}
      oc.isDefined}
  }



  //========================================================
  //
  //          Data Store Implementation
  //
  //========================================================

  override def storedTypes: Seq[DataType] = List(FormulaType)

  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (fo : FormulaStore,fn : FormulaStore)  =>
      removeFormula(fo)
      if(FormulaSet.contains(fn)){
//        leo.Out.comment(s"[FormulaStore]: ${fn.pretty} was already contained.")
        return false
      }
//      leo.Out.comment(s"[FormulaStore]: \n    ${fo.pretty} \n  is updated to \n    ${fn.pretty}.")
      addFormula(fn)
    case _ => false
  }

  override def insert(n: Any): Boolean = n match {
    case fn : FormulaStore =>
      if(FormulaSet.contains(fn)){
      // leo.Out.comment(s"[FormulaStore]: ${fn.pretty} was already contained.")
        return false
      }
//      leo.Out.comment(s"[FormulaStore]: ${fn.pretty} is added.")
      addFormula(fn)
    case _ => false
  }

  override def clear(): Unit = TaskSet.clear()

  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case FormulaType => FormulaSet.getAll.toSet
  }

  override def delete(d: Any): Unit = d match {
    case fd : FormulaStore =>
      removeFormula(fd)
  }
}
