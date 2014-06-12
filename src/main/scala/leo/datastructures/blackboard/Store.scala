package leo.datastructures.blackboard

import scala.concurrent.stm._
import leo.datastructures.internal.{ Term => Formula }
import java.util.concurrent.atomic.AtomicInteger

/**
 * <p>
 * Common Trait for all Infos to be stored in the Blackboard.
 * <p>
 * Will be used inside a ref
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @since 5/21/14
 */
sealed class Store[A <: Storable] extends Function0[Ref[A]] {

  protected[blackboard] var _internalObject : Ref[A] = null
  def apply() : Ref[A] = _internalObject

  /**
   * Performs an action on the stored
   * values, by accessing the data through STM
   * and writing the changes back to the blackboard.
   * @param f - Function applied to A's
   */
  def action(f : A => A) : Unit = atomic {implicit txn =>
    Txn.beforeCommit(t => _internalObject.get(t).writeCommit(this, _internalObject()))   // Tries to write changes back to the blackboard, after the changes are done
    _internalObject() = f(_internalObject())                                              // Performs an action on the stored data
  }

  /**
   * This method is supposed to apply a read function.
   * No writing back is applied, if this function is called.
   * @param f
   * @tparam B
   * @return
   */
  def read[B](f : A => B) : B = atomic {implicit txn =>
    f(_internalObject())
  }
}

sealed trait Storable {
  /**
   * <p>
   * Method specified to write the changes
   * of the Store Back to the Blackboard.
   *
   * (Changes in data structure ordering e.g.)
   * <p>
   *
   * <p>
   * Should only be called during @see {Txn.whileCommit} Phase
   * to guarantee rollback problems in the blackboard.
   * <p>
   */
  protected[blackboard] def writeCommit[B <: Storable](r : Store[B], diffStore : B) : Unit
}

object Store {
  private var unnamedFormulas : AtomicInteger = new AtomicInteger(0)

  def apply(s : FormulaStore) : Store[FormulaStore] = {
    val s1 = new Store[FormulaStore]()
    s1._internalObject = Ref(s)
    return s1
  }

  def apply(name : String, initFormula : Formula,  blackboard : Blackboard) : Store[FormulaStore]
    = Store(new FormulaStore(name, initFormula, blackboard))

  def apply(initFormula : Formula, blackboard : Blackboard) : Store[FormulaStore]
    = Store(new FormulaStore("gen_formula_"+unnamedFormulas.incrementAndGet(),initFormula,blackboard))
}

/**
 * Class to Store Formula Information.
 *
 */
class FormulaStore(_name : String, _formula : Formula, blackboard : Blackboard) extends Storable{

  def name : String = _name
  def formula : Formula = _formula

  def newName(nname : String) : FormulaStore = new FormulaStore(nname, formula, blackboard)
  def newFormula(nformula : Formula) : FormulaStore = new FormulaStore(name, nformula, blackboard)

  /**
   * Can only be called with the FormulaStore, otherwise an error is thrown.
   *
   * @param r           - The store object this formula store is contained in
   * @param diffStore   - The object from the start of the transaction
   * @tparam B          - Should only be FomrulaStore
   */
  protected[blackboard] def writeCommit[B <: Storable](r : Store[B], diffStore : B) {
    val d = r.asInstanceOf[FormulaStore]
    val r1 = diffStore.asInstanceOf[Store[FormulaStore]]
    if (name != d.name) {
          blackboard.rmFormulaByName(d.name)
          blackboard.addFormula(r1)
    }
  }
}