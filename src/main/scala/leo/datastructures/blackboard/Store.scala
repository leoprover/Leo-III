package leo.datastructures.blackboard

import scala.concurrent.stm._
import leo.datastructures.tptp.Commons.{ AnnotatedFormula => Formula }

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
  def action(f : A => Unit) : Unit = atomic {implicit txn =>
    Txn.beforeCommit(t => _internalObject.get(t).writeCommit(this))   // Tries to write changes back to the blackboard, after the changes are done
    f(_internalObject())                                              // Performs an action on the stored data
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
  def writeCommit[B <: Storable](r : Store[B]) : Unit
}

object Store {
  def apply(s : FormulaStore) : Store[FormulaStore] = {
    val s1 = new Store[FormulaStore]()
    s1._internalObject = Ref(s)
    return s1
  }

  def apply(name : String, initFormula : Formula,  blackboard : Blackboard) : Store[FormulaStore]
    = {
    var r = new FormulaStoreImpl(blackboard)
    r._formula = initFormula
    r._name = name
    r._changedName = name
    Store(r)
  }
  def apply(initFormula : Formula, blackboard : Blackboard) : Store[FormulaStore]
    = {
    var r = new FormulaStoreImpl(blackboard)
    r._formula = initFormula
    Store(r)
  }
}

sealed trait FormulaStore extends Storable{
  //Access to Formula
  def formula : Formula
  def formula_= (value : Formula) : Unit

  def name : String
  def name_= (value : String) : Unit
}

/**
 * Class to Store Formula Information.
 *
 */
protected[blackboard] class FormulaStoreImpl(blackboard : Blackboard) extends FormulaStore{

  // ATM senseless, but for later when we order by formula
  protected[blackboard] var _formula : Formula = null
  def formula = _formula
  def formula_= (value : Formula) : Unit = _formula = value

  // Introduce another variable for caching. If
  // The name changed, we
  protected[blackboard] var _changedName : String = ""
  protected[blackboard] var _name : String = ""
  def name = _name
  def name_= (value : String) {
    _changedName = value
  }

  def writeCommit[B <: Storable](r : Store[B]) {
    r match {
      case r1 : Store[FormulaStore] =>
        if (_changedName != _name) {
          blackboard.rmFormulaByName(_name)
          _name = _changedName
          blackboard.addFormula(r1)
      }
      case _ => throw new ClassCastException("Returned not the right class.")
    }

  }
}