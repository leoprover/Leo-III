package blackboard

import datastructures.tptp.Commons.{AnnotatedFormula => Formula}
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

  // Monitor for the formulas
  private val formulaLock : ReentrantLock = new ReentrantLock(true)
  private val writeCond : Condition = formulaLock.newCondition()
  private var writeCount = 0
  private val readCond : Condition = formulaLock.newCondition()
  private var readCount = 0

  // Monitor for the observer Lists (One because we assume not that many changes
  private val observerLock : ReentrantLock = new ReentrantLock(true)

  /**
   * <p>
   * Adds a formula to the Set of formulas of the Blackboard.
   * </p>
   * @param formula to be added.
   */
  override def addFormula(formula: Formula) {
    // Entry
    formulaLock.lock()
    try {
      while(readCount > 0 || writeCount > 0) writeCond.await()
      writeCount += 1
    } finally {
      formulaLock.unlock()
    }

    //CS:
    formulaMap.put(formula.name, (formula.name, formula))

    // Exit
    formulaLock.lock()
    try {
      writeCount -= 1
      if(formulaLock.hasWaiters(readCond)) readCond.signalAll()
      else writeCond.signal()
    } finally {
      formulaLock.unlock()
    }
  }

  /**
   * <p>
   * Informs an Observer over all Add Operations.
   * </p>
   * @param o - Observer to add.
   */
  override def observeAllAdds(o: BlackboardObserver) {
    observerLock.lock()
    try {
      observeAddAllSet.add(o)
    } finally {
      observerLock.unlock()
    }
  }

  /**
   * <p>
   * Returns a List of all Formulas of the Blackboard.
   * </p>
   *
   * @return All formulas of the blackboard.
   */
  override def getFormulas(): List[Formula] = getAll(_ => true)

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
    observerLock.lock()
    try {
      observeAddPredSet.add(o)
    } finally {
      observerLock.unlock()
    }
  }

  /**
   * Informs an Observer over all Remove Actions satisfying
   * a Predicate p.
   *
   * @param p - Predicate to be satisfied.
   * @param o - Observer.
   */
  override def observeRemPredicate(p: (Formula) => Boolean, o: BlackboardObserver) {
    observerLock.lock()
    try {
      observeRmPredSet.add(o)
    } finally {
      observerLock.unlock()
    }
  }

  /**
   * <p>
   * Removes a formula from the Set fo formulas of the Blackboard.
   * </p>
   * @return true if the formula was removed, false if the formula does not exist.
   */
  override def removeFormula(formula: Formula): Boolean = rmFormulaByName(formula.name)

  /**
   * <p>
   * Informs an Observer over all Remove Operations.
   * </p>
   * @param o - Observer
   */
  override def observeAllRem(o: BlackboardObserver) {
    observerLock.lock()
    try {
      observeRmAllSet.add(o)
    } finally {
      observerLock.unlock()
    }
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
  override def getAll(p: (Formula) => Boolean): List[Formula] = {
    // Entry
    formulaLock.lock()
    try {
      while(writeCount > 0 || formulaLock.hasWaiters(writeCond)) readCond.await()
      readCount += 1
    } finally {
      formulaLock.unlock()
    }

    val a = formulaMap.values.toList.map(_._2).filter(p)

    formulaLock.lock()
    try{
      readCount -= 1
      if(formulaLock.hasWaiters(writeCond)) {
        if (readCount == 0) writeCond.signal()
      }
      else readCond.signal()  // Nur zur Sicherheit, falls noch jmd rein gelaufen ist.
    } finally {
      formulaLock.unlock()
    }
    return a
  }

  /**
   * <p>
   * Returns possibly a formula with a given name.
   * </p>
   *
   * @param name - Name of the Formula
   * @return Some(x) if x.name = name exists otherwise None
   */
  override def getFormulaByName(name: String): Option[Formula] = {

    // Entry
    formulaLock.lock()
    try {
      while(writeCount > 0 || formulaLock.hasWaiters(writeCond)) readCond.await()
      readCount += 1
    } finally {
      formulaLock.unlock()
    }

    val a = formulaMap.get(name).map(_._2)

    formulaLock.lock()
    try{
      readCount -= 1
      if(formulaLock.hasWaiters(writeCond)) {
        if (readCount == 0) writeCond.signal()
      }
      else readCond.signal()  // Nur zur Sicherheit, falls noch jmd rein gelaufen ist.
    } finally {
      formulaLock.unlock()
    }
    return a
  }

  /**
   * <p>
   * Removes a Formula by its name.
   * </p>
   *
   * @param name - Name of the Formula to be removed
   * @return true, iff the element existed.
   */
  override def rmFormulaByName(name: String): Boolean = {
    // Entry
    formulaLock.lock()
    try {
      while(readCount > 0 || writeCount > 0) writeCond.await()
      writeCount += 1
    } finally {
      formulaLock.unlock()
    }

    //CS:
    val r : Boolean = formulaMap.remove(name) match {
      case Some(_) => true
      case None => false
    }

    // Exit
    formulaLock.lock()
    try {
      writeCount -= 1
      if(formulaLock.hasWaiters(readCond)) readCond.signalAll()
      else writeCond.signal()
    } finally {
      formulaLock.unlock()
    }
    return r

  }

  /**
   * <p>
   * Removex all Formulas from the Blackboard satisfying a Predicate.
   * </p>
   *
   * @param p - All x with p(x) will be removed.
   */
  override def rmAll(p: (Formula) => Boolean) {
    formulaLock.lock()
    try {
      while(readCount > 0 || writeCount > 0) writeCond.await()
      writeCount += 1
    } finally {
      formulaLock.unlock()
    }

    //CS:
    formulaMap.values.filter(x=>p(x._2)).foreach(x => formulaMap.remove(x._1))

    // Exit
    formulaLock.lock()
    try {
      writeCount -= 1
      if(formulaLock.hasWaiters(readCond)) readCond.signalAll()
      else writeCond.signal()
    } finally {
      formulaLock.unlock()
    }
  }
}
