package blackboard.impl

import datastructures.tptp.Commons.{ AnnotatedFormula => Formula }
import scala.collection.mutable._
import java.util.concurrent.locks.ReentrantLock
import scheduler.Scheduler
import scala.collection.concurrent.TrieMap
import blackboard._
import scala.concurrent.stm._
import scala.Some

/**
 * Starting Blackboard. Just to replace @see{leoshell.FormulaHandle}
 *
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @author Daniel Jentsch <d.jentsch@fu-berlin.de>
 * @since 29.04.2014
 */
object SimpleBlackboard extends Blackboard {

  import FormulaSet._

  var DEBUG : Boolean = true

  // Observer
  protected[blackboard] val observeAddSet = new HashSet[FormulaAddObserver] with SynchronizedSet[FormulaAddObserver]
  protected[blackboard] val observeRemoveSet = new HashSet[FormulaRemoveObserver] with SynchronizedSet[FormulaRemoveObserver]

  // Scheduler ATM Here because accessibility in prototype version
  protected[blackboard] val _scheduler = new Scheduler(5)
  def scheduler = _scheduler

  def getFormulas(): List[Store[FormulaStore]] = getAll(_ => true)

  def getAll(p: (Formula) => Boolean): List[Store[FormulaStore]] = read { formulas =>
    formulas.values.filter { store =>
      store.read(fS => p(fS.formula) )
    }.toList
  }

  def getFormulaByName(name: String): Option[Store[FormulaStore]] = read { formulas =>
    formulas get name
  }

  override def addFormula(formula: Formula) {
    //println("Form Name : "+formula.name+", formula : '"+formula.toString+"'")
    addFormula(Store.apply(formula.name, formula, SimpleBlackboard))
  }

  override def addFormula(formula : Store[FormulaStore]) {
    write { formulas =>
      formulas put (formula read {_.name}, formula)
    }
    observeAddSet.filter(_.filterAdd(formula)).foreach{ o=> o.addFormula(formula); o.wakeUp() }
  }

  def removeFormula(formula: Store[FormulaStore]): Boolean = rmFormulaByName(formula read {_.name})

  def rmFormulaByName(name: String): Boolean = write { formulas =>
    formulas.remove(name) match {
      case Some(x) => {
        observeRemoveSet.filter(_.filterRemove(x)).foreach {o => o.removeFormula(x); o.wakeUp()}
        true
      }
      case None => false
    }
  }

  def rmAll(p: (Formula) => Boolean) = write { formulas =>
    val toWakeUp : Set[FormulaRemoveObserver] = HashSet.empty[FormulaRemoveObserver]
    formulas.values foreach { form =>
          if(p(form read {_.formula})) {
            formulas.remove(form read {_.name})
            observeRemoveSet.filter(_.filterRemove(form)).foreach { o => o.removeFormula(form); toWakeUp += o}
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

  /**
   * Used by Stores to mark a FormulaStore as Changed, if nothing
   * has to be updated. Handlers can register to these updates
   * @param f
   */
  override protected[blackboard] def emptyUpdate(f: Store[FormulaStore]) {
    observeAddSet foreach {o =>
      if (o.filterAdd(f)) o.addFormula(f); o.wakeUp()
    }
  }
}

/**
 * Handles multi threaded access to a mutable map.
 */
private object FormulaSet {
  // Formulas

  private val formulaMap = new TrieMap[String, Store[FormulaStore]]()

  /**
   * Per se se an action itself. Maybe try different syntax, s.t. we know this one locks,
   * the other one not.
   *
   * Not a Problem ATM: writing the same Key twice may introduce inconsitencies, if two
   * distinct formula stores are used.
   */

  def write[R](action: Map[String, Store[FormulaStore]] => R): R = action(formulaMap)

  def read[R](action: Map[String, Store[FormulaStore]] => R): R = action(formulaMap)
} 
