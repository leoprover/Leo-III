package leo.datastructures.blackboard.impl

import leo.datastructures.{ClauseProxy, Clause, AnnotatedClause}
import leo.datastructures.blackboard._
import leo.datastructures.context.impl.{TreeContextMap, TreeContextSet}
import leo.datastructures.context.{ContextMap, ContextSet, Context}

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
  def getFormulas: Iterable[ClauseProxy] = FormulaSet.getAll.map(_._1)

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  def getAll(p: ClauseProxy => Boolean): Iterable[ClauseProxy] = getFormulas.filter(p)


  /**
   * <p>
   * Filters the formulas of a given context.
   * </p>
   *
   * @param c - A given Context
   * @param p Predicate the formulas have to satisfy
   * @return All formulas in `c` satisfying `p`
   */
  def getAll(c: Context)(p: (ClauseProxy) => Boolean): Iterable[ClauseProxy] = FormulaSet.getAll(c).filter(p)


  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  def getFormulaByName(name: Long): Option[ClauseProxy] = FormulaSet.getName(name)


  /**
   * <p>
   * Adds a formula to the blackboard, if it does not exist. If it exists
   * the old formula is returned.
   * </p>
   *
   * @param formula to be added.
   * @return The inserted Formula, or the already existing one.
   */
  def addFormula(formula : ClauseProxy) : Boolean = {
    addFormula(formula, Context())
  }

  /**
    * <p>
    * Adds a formula to the blackboard, if it does not exist. If it exists
    * the old formula is returned.
    * </p>
    *
    * @param formula to be added.
    * @return The inserted Formula, or the already existing one.
    */
  def addFormula(formula : ClauseProxy, c : Context) : Boolean = {
    val f = FormulaSet.add(formula, c)
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
  def addNewFormula(formula : ClauseProxy) : Boolean = {
    addNewFormula(formula, Context())
  }

  /**
    * Adds a formula to the Blackboard.
    * Returns true, if the adding was successful
    * and false, if the formula already existed.
    *
    * @param formula - New to add formula
    * @return true if the formula was not contained in the blackboard previously
    */
  def addNewFormula(formula : ClauseProxy, context: Context) : Boolean = {
    // TODO: Implement Sets to check containment of Clauses.
    if(Clause.trivial(formula.cl)) return false
    if (FormulaSet.getAll(context).exists(c => c.cl == formula.cl))
      false
    else {
      FormulaSet.add(formula, context)
      true
    }
    true
  }

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
 *
   * @return true if the formula was removed, false if the formula does not exist.
   */
  def removeFormula(formula: ClauseProxy): Boolean = removeFormula(formula, Context())

  /**
    * <p>
    * Removes a formula from the Set fo formulas of the Blackboard.
    * </p>
    *
    * @return true if the formula was removed, false if the formula does not exist.
    */
  def removeFormula(formula: ClauseProxy, context: Context): Boolean = FormulaSet.rm(formula, context)


  /**
   * <p>
   * Removes a Formula by its name.
   * </p>
   *
   * @param name - Name of the Formula to be removed
   * @return true, iff the element existed.
   */
  def rmFormulaByName(name: Long): Boolean = FormulaSet.rmName(name, Context())


  /**
    * <p>
    * Removes a Formula by its name.
    * </p>
    *
    * @param name - Name of the Formula to be removed
    * @return true, iff the element existed.
    */
  def rmFormulaByName(name: Long, context: Context): Boolean = FormulaSet.rmName(name, context)

  /**
   *
   * <p>
   * Retrieves all formulas in a given context.
   * </p>
   *
   * @param c - A given Context
   * @return All formulas in the context `c`
   */
  def getFormulas(c: Context): Iterable[ClauseProxy] = FormulaSet.getAll(c)

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p: ClauseProxy => Boolean) = FormulaSet.getAll.foreach{ case (f,c) => if(p(f)) FormulaSet.rm(f,c)}


  /**
   * <p>
   * Removes all formulas in the context `c` satisfiying `p`.
   * </p>
 *
   * @param c - A given Context
   * @param p - Predicate the formulas have to satisfy
   * @return Removes all formulas in `c` satisfying `p`
   */
  def rmAll(c: Context)(p: ClauseProxy => Boolean): Unit = FormulaSet.getAll(c) foreach { f => if(p(f)) FormulaSet.rm(f,c)}

  /**
   * Stores the formulas in first in a map from name to the @see{FormulaStore}
   * and secondly a map from @see{Term} to FormulaStore, to see, if a formula has already bee added.
   *
   */
  private object FormulaSet {

    private val formulaSet : ContextSet[ClauseProxy] = new TreeContextSet[ClauseProxy]()
    private val clauseMap : ContextMap[Clause, ClauseProxy] = new TreeContextMap[Clause, ClauseProxy]()

    /**
     * Looks up the termMap, for an already existing store and returns this or the given store
     * after adding it.
     *
     * @return the existing store or the new one
     */
    def add(f: ClauseProxy, c : Context) : Boolean = formulaSet.synchronized {
      formulaSet get (f,c) match {
        case Some(f1) =>
          false
        case None =>
          clauseMap lookup (f.cl, c) match {
            case Some(f1) =>
              false
            case None =>
              formulaSet.add(f, c)
              clauseMap.put(f.cl, f, c)
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
    def getAll : Iterable[(ClauseProxy, Context)] = formulaSet.synchronized{
      val fs = formulaSet.getAll
      val all : Iterable[(ClauseProxy, Context)] = fs flatMap {f => formulaSet.inContext(f) map (c => (f,c))}
      all
    }

    def getAll(c : Context) : Iterable[ClauseProxy] = formulaSet.synchronized(formulaSet.getAll(c))

    def rm(f: ClauseProxy, context: Context) : Boolean = formulaSet.synchronized {
      if(formulaSet.remove(f, context)) {
        clauseMap.remove(f.cl, context)
        return true
      }
      return false
    }

    def rmName(n : Long, context: Context) : Boolean = formulaSet.synchronized {
      formulaSet.getAll.find {f => f.id == n} match {
        case None => false
        case Some(f) =>
          val r = formulaSet.remove(f, context)
          if(r) clauseMap.remove(f.cl, context)
          r
      }
    }

    def getName(n : Long) : Option[ClauseProxy] = formulaSet.synchronized(formulaSet.getAll.find { f => f.id == n})

    def contains(f: ClauseProxy, c : Context) : Boolean = formulaSet.synchronized{
      val oc = clauseMap.lookup(f.cl,c)
      //oc.foreach{f1 => leo.Out.comment(s"[FormulaStore]: contains(${f.pretty}) is true.\n  Found ${f1.pretty} ")}
      oc.isDefined}
  }



  //========================================================
  //
  //          Data Store Implementation
  //
  //========================================================

  override def storedTypes: Seq[DataType] = List(ClauseType)

  override def update(o: Any, n: Any): Boolean = {
    (o,n) match {
      case (fo: ClauseProxy, fn: ClauseProxy)  =>
        removeFormula(fo)
        if(FormulaSet.contains(fn, Context()))
          return false
        addFormula(fn)
      case ((fo : ClauseProxy,co:Context), (fn : ClauseProxy,cn : Context)) =>
        removeFormula(fo, co)
        if(FormulaSet.contains(fn, cn))
          return false
        addFormula(fn,cn)
      case _ =>
        false
    }
  }

  override def insert(n: Any): Boolean = n match {
    case fn: ClauseProxy =>
      if(FormulaSet.contains(fn, Context())){
        return false
      }
      addFormula(fn)
    case (fn : ClauseProxy,cn : Context) =>
      if(FormulaSet.contains(fn, cn)){
        return false
      }
      addFormula(fn, cn)
    case _ =>
      false
  }

  override def clear(): Unit = TaskSet.clear()

  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case ClauseType => FormulaSet.getAll.toSet
  }

  override def delete(d: Any): Unit = d match {
    case fd: AnnotatedClause =>
      removeFormula(fd)
    case (fd: AnnotatedClause, c : Context) =>
      removeFormula(fd, c)
    case _ => ()
  }
}
