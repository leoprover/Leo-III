package blackboard

import datastructures.tptp.Commons.{ AnnotatedFormula => Formula }
import scala.collection.mutable._
import java.util.concurrent.locks.{ ReentrantLock, Condition }
import scheduler.Scheduler

/**
 * Starting Blackboard. Just to replace @see{leoshell.FormulaHandle}
 *
 * @author Max Wisniewski
 * @author Daniel Jentsch <d.jentsch@fu-berlin.de>
 * @since 29.04.2014
 */
object SimpleBlackboard extends Blackboard {

  import FormulaStore._

  // Observer
  protected[blackboard] val observeAddSet = new HashSet[FormulaAddObserver] with SynchronizedSet[FormulaAddObserver]
  protected[blackboard] val observeRemoveSet = new HashSet[FormulaRemoveObserver] with SynchronizedSet[FormulaRemoveObserver]

  // Scheduler ATM Here because accessibility in prototype version
  protected[blackboard] val _scheduler = new Scheduler(5)
  def scheduler = _scheduler

  def getFormulas(): List[Formula] = getAll(_ => true)

  def getAll(p: (Formula) => Boolean): List[Formula] = read { formulas =>
    formulas.values.filter(p).toList
  }

  def getFormulaByName(name: String): Option[Formula] = read { formulas =>
    formulas get name
  }

  def addFormula(formula: Formula) {
    write { formulas =>
      formulas += formula.name -> formula
    }
    observeAddSet.filter(_.filterAdd(formula)).foreach{ o=> o.addFormula(formula); o.wakeUp() }
  }

  def removeFormula(formula: Formula): Boolean = rmFormulaByName(formula.name)

  def rmFormulaByName(name: String): Boolean = write { formulas =>
    formulas.remove(name) match {
      case Some(x) => {
        observeRemoveSet.filter(_.filterRemove(x)).foreach{ o => o.removeFormula(x); o.wakeUp() }
        true
      }
      case None => false
    }
  }

  def rmAll(p: (Formula) => Boolean) = write { formulas =>
    val toWakeUp : Set[FormulaRemoveObserver] = HashSet.empty[FormulaRemoveObserver]
    formulas.values.
      filter(p).
      foreach { f =>
        formulas -= f.name
        observeRemoveSet.filter(_.filterRemove(f)).foreach { o =>
          o.removeFormula(f)
          toWakeUp += o
        }
      }
    toWakeUp.foreach(_.wakeUp())
  }

  /**
   * Register a new Handler for Formula adding Handlers.
   * @param o - The Handler that is to register
   */
  override def registerAddObserver(o: FormulaAddObserver): Unit = observeAddSet.add(o)

  /**
   * <p>
   * Method to add an Handler for the removing of a Formula of the Blackboard.
   * </p>
   *
   * @param o - The Handler that is registered.
   */
  override def registerRemoveObserver(o: FormulaRemoveObserver): Unit = observeRemoveSet.add(o)
}

/**
 * Handles multi threaded access to a mutable map.
 */
private object FormulaStore {
  // Formulas
  private val formulaMap = Map.empty[String, Formula]
  // Monitor for the formulas
  private val formulaLock = new ReentrantLock(true)
  private val writeCond = formulaLock.newCondition()
  private var writeCount = 0
  private val readCond = formulaLock.newCondition()
  private var readCount = 0

  /**
   * Locks the formula for modifing.
   */

  def write[R](action: Map[String, Formula] => R): R = {
    // Entry
    formulaLock.lock()
    try {
      while (readCount > 0 || writeCount > 0)
        writeCond.await()
      writeCount += 1
    } finally {
      formulaLock.unlock()
    }
    // CS
    val result = action(formulaMap)

    // Exit
    formulaLock.lock()
    try {
      writeCount -= 1
      if (formulaLock.hasWaiters(readCond))
        readCond.signalAll()
      else
        writeCond.signal()
    } finally {
      formulaLock.unlock()
    }
    return result
  }

  def read[R](action: scala.collection.Map[String, Formula] => R): R = {
    // Entry
    formulaLock.lock()
    try {
      while (writeCount > 0 || formulaLock.hasWaiters(writeCond))
        readCond.await()
      readCount += 1
    } finally {
      formulaLock.unlock()
    }

    // CS
    val result = action(formulaMap)

    // Exit
    formulaLock.lock()
    try {
      readCount -= 1
      if (formulaLock.hasWaiters(writeCond)) {
        if (readCount == 0)
          writeCond.signal()
      } else
        readCond.signal() // Nur zur Sicherheit, falls noch jmd rein gelaufen ist.
    } finally {
      formulaLock.unlock()
    }

    return result
  }
} 
