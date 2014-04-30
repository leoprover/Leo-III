package blackboard

import tptp.Commons.{AnnotatedFormula => Formula}
import scala.collection.mutable
import java.util.concurrent.locks.{ReentrantLock, Condition}

/**
 *
 * Starting Blackboard. Just to replace @see{leoshell.FormulaHandle}
 *
 * @author Max Wisniewski
 * @since 29.04.2014
 */
object SimpleBlackboard extends Blackboard {

  // Formulas
  protected[blackboard] var formulaMap: mutable.HashMap[String, (String, Formula)] = new mutable.HashMap[String, (String, Formula)]

  // Observer
  protected[blackboard] var observeAddAllSet: mutable.HashSet[BlackboardObserver] = new mutable.HashSet[BlackboardObserver]
  protected[blackboard] var observeRmAllSet: mutable.HashSet[BlackboardObserver] = new mutable.HashSet[BlackboardObserver]
  protected[blackboard] var observeAddPredSet: mutable.HashSet[BlackboardObserver] = new mutable.HashSet[BlackboardObserver]
  protected[blackboard] var observeRmPredSet: mutable.HashSet[BlackboardObserver] = new mutable.HashSet[BlackboardObserver]


  private val lock : ReentrantLock = new ReentrantLock(true)
  private val writeCond : Condition = lock.newCondition()
  private val readCond : Condition = lock.newCondition()

  /**
   * <p>
   * Adds a formula to the Set of formulas of the Blackboard.
   * </p>
   * @param formula to be added.
   */
  override def addFormula(formula: Formula) {
    // Entry
    lock.lock()
    try {
      readCond.await()
      if(lock.hasWaiters(writeCond)) writeCond.await()
      readCond.signal()
    } finally {
      lock.unlock()
    }

    formulaMap.put(formula.name, (formula.name, formula))
  }

  /**
   * <p>
   * Informs an Observer over all Add Operations.
   * </p>
   * @param o - Observer to add.
   */
  override def observeAllAdds(o: BlackboardObserver) {
    observeAddAllSet.add(o)
  }

  /**
   * <p>
   * Returns a List of all Formulas of the Blackboard.
   * </p>
   *
   * @return All formulas of the blackboard.
   */
  override def getFormulas(): List[Formula] = formulaMap.values.toList.map(x => x._2)

  /**
   * <p>
   * Informs an Observer over all Add Actions satisfying a
   * Predicate p.
   * </p>
   *
   * @param p - Predicate to be satisfied.
   * @param o - Observer.
   */
  override def observeAddPredicate(p: (Formula) => Boolean, o: BlackboardObserver) {
    observeAddPredSet.add(o)
  }

  /**
   * Informs an Observer over all Remove Actions satisfying
   * a Predicate p.
   *
   * @param p - Predicate to be satisfied.
   * @param o - Observer.
   */
  override def observeRemPredicate(p: (Formula) => Boolean, o: BlackboardObserver) {
    observeRmPredSet.add(o)
  }

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
   * @return true if the formula was removed, false if the formula does not exist.
   */
  override def removeFormula(formula: Formula): Boolean = {
    formulaMap.remove(formula.name) match {
      case Some(x) => true
      case None => false
    }
  }

  /**
   * <p>
   * Informs an Observer over all Remove Operations.
   * </p>
   * @param o - Observer
   */
  override def observeAllRem(o: BlackboardObserver) {
    observeRmAllSet.add(o)
  }

  /**
   *
   * <p>
   * Filters Set of Formulas according to a predicate.
   * </p>
   *
   * @param p Predicate to select formulas
   * @return Set of Formulas satisfying the Predicate
   */
  override def getAll(p: (Formula) => Boolean): List[Formula] = formulaMap.values.toList.map(_._2).filter(p)

  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  override def getFormulaByName(name: String): Option[Formula] = formulaMap.get(name).map(_._2)

  /**
   * <p>
   * Removes a Formula by its name.
   * </p>
   *
   * @param name - Name of the Formula to be removed
   * @return true, iff the element existed.
   */
  override def rmFormulaByName(name: String): Boolean = {
    formulaMap.remove(name) match {
      case Some(_) => true
      case None => false
    }
  }

  /**
   * <p>
   * Removex all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  override def rmAll(p: (Formula) => Boolean) {
    formulaMap.clear()
  }
}
