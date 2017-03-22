package leo.datastructures.blackboard.impl

import leo.datastructures.{Clause, ClauseProxy}
import leo.datastructures.blackboard._

import scala.collection.mutable

/**
 *
 * A simple tree context set implementation.
 *
 * Stores all formulas (enriched clauses) in the program.
 *
 */
object FormulaDataStore extends DataStore {

  val formulaStore : mutable.Set[ClauseProxy] = new mutable.HashSet[ClauseProxy]

  /**
   * <p>
   * Returns a List of all Formulas of the Blackboard.
   * </p>
   *
   * @return All formulas of the blackboard.
   */
  def getFormulas: Iterable[ClauseProxy] = formulaStore

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  def getAll(p: ClauseProxy => Boolean): Iterable[ClauseProxy] = synchronized(formulaStore.filter(p))

  /**
    * <p>
    * Adds a formula to the blackboard, if it does not exist. If it exists
    * the old formula is returned.
    * </p>
    *
    * @param formula to be added.
    * @return The inserted Formula, or the already existing one.
    */
  def addFormula(formula : ClauseProxy) : Boolean = synchronized{
    if(formulaStore.contains(formula)) return false
    val f = formulaStore.add(formula)
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
  def addNewFormula(formula : ClauseProxy) : Boolean = synchronized {
    // TODO: Implement Sets to check containment of Clauses.
    if(Clause.trivial(formula.cl)) return false
    if (formulaStore.exists(c => c.cl == formula.cl))
      false
    else {
      addFormula(formula)
    }
  }

  /**
    * <p>
    * Removes a formula from the Set fo formulas of the Blackboard.
    * </p>
    *
    * @return true if the formula was removed, false if the formula does not exist.
    */
  def removeFormula(formula: ClauseProxy): Boolean = synchronized {
    formulaStore.remove(formula)
  }

  /**
   * <p>
   * Remove all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  def rmAll(p: ClauseProxy => Boolean) = synchronized {
    val filter = formulaStore.filter(p).toIterator
    while(filter.nonEmpty){
      formulaStore.remove(filter.next())
    }
  }



  //========================================================
  //
  //          Data Store Implementation
  //
  //========================================================

  override def storedTypes: Seq[DataType[Any]] = List(ClauseType)

  override def updateResult(r: Delta): Boolean = {
    val del = r.removes(ClauseType).iterator
    val up = r.updates(ClauseType).iterator
    val ins = r.inserts(ClauseType).iterator

    var doneSmth = false

    while(del.nonEmpty)
      doneSmth |= removeFormula(del.next())   // TODO correct?
    while(up.nonEmpty) {
      val (oldV, newV) = up.next()
      removeFormula(oldV)
      doneSmth |= addNewFormula(newV)
    }
    while(ins.nonEmpty)
      doneSmth |= addNewFormula(ins.next())

    doneSmth
  }

  override def clear(): Unit = formulaStore.clear()

  override protected[blackboard] def all[T](t: DataType[T]): Set[T] = t match {
    case ClauseType => getFormulas.toSet.asInstanceOf[Set[T]]
    case _ => Set()
  }

}
