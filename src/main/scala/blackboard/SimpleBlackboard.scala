package blackboard

import datastructures.tptp.Commons.{ AnnotatedFormula => Formula }
import scala.collection.mutable._
import java.util.concurrent.locks.{ ReentrantLock, Condition }

/**
 * Starting Blackboard. Just to replace @see{leoshell.FormulaHandle}
 *
 * @author Max Wisniewski
 * @author Daniel Jentsch <d.jentsch@fu-berlin.de>
 * @since 29.04.2014
 */
object SimpleBlackboard extends Blackboard {

  import FormulaStore._

  private def observerSet =
    new HashSet[BlackboardObserver] with SynchronizedSet[BlackboardObserver]

  // Observers
  private val observeAddAllSet = observerSet
  private val observeRmAllSet = observerSet

  def getFormulas(): List[Formula] = getAll(_ => true)

  def getAll(p: (Formula) => Boolean): List[Formula] = read { formulas =>
    formulas.values.filter(p).toList
  }

  def getFormulaByName(name: String): Option[Formula] = read { formulas =>
    formulas get name
  }

  def addFormula(formula: Formula) = write { formulas =>
    formulas += formula.name -> formula
  }

  def removeFormula(formula: Formula): Boolean =
    rmFormulaByName(formula.name)

  def rmFormulaByName(name: String): Boolean = write { formulas =>
    formulas.remove(name).isDefined
  }

  def rmAll(p: (Formula) => Boolean) = write { formulas =>
    formulas.values.
      filter(p).
      foreach { f =>
        formulas -= f.name
      }
  }

  def observeAllAdds(o: BlackboardObserver): Unit =
    observeAddAllSet += o

  def observeAddPredicate(p: (Formula) => Boolean, o: BlackboardObserver) =
    ???

  override def observeAllRem(o: BlackboardObserver): Unit =
    observeRmAllSet.add(o)

  def observeRemPredicate(p: (Formula) => Boolean, o: BlackboardObserver) =
    ???
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
