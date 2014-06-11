package leo.agents
package impl

import leo.datastructures.blackboard.{FormulaStore, FormulaRemoveObserver, Blackboard, FormulaAddObserver}
import leo.datastructures.blackboard.{FormulaStore, Store}
import leo.datastructures.tptp.Commons.{AnnotatedFormula => Formula}
import leo.modules.normalization.Normalize
import scala.collection.mutable
import scala.concurrent.stm._
import leo.datastructures.blackboard.impl.SimpleBlackboard

/**
 *
 * <p>
 * Normalization Agent for one normalization function.
 * </p>
 *
 * <p>
 * This Agent should register for formula Adds/Changes and applies Clause Normalization
 * as long as its possible. (Predicate is fullfilled.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
class NormalClauseAgent(norm : Normalize) extends FormulaAddObserver {

  private val newFormulas : mutable.Set[Store[FormulaStore]] = new mutable.HashSet[Store[FormulaStore]]() with mutable.SynchronizedSet[Store[FormulaStore]]
  private var blackboard : Blackboard = null  // Will be inserted by registering to a blackboard

  /**
   * This function applies the normalization provided by {@see norm}, it is assumed that the formulas added
   * are already checked by {@see Normalize.applicable}.
   */
  override def apply() {
    if (blackboard == null) throw new RuntimeException("An Apply was called for an Agent, that has no Blackboard assigned.")
    // We cannot add the formulas in the synchronized block or we will knock ourselves out.
    var workedFormulas = Set.empty[Store[FormulaStore]]
    var output = ""
    newFormulas foreach {store =>
      store action { fS =>
        output = ""           // Reset at start of transaction, because it will not be reseted thorugh STM
        val form = fS.formula
        val form1 = form //norm.normalize(form)
        if (form != form1) {
          if (SimpleBlackboard.DEBUG) output = "Simplified : '"+form+"' to '"+form1+"'."
          fS.formula = form1
        }
        workedFormulas += store
        fS
      }
    }
    if(output != "") println(output)
    workedFormulas foreach (newFormulas remove _)
  }

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   * @param blackboard - The Blackboard the Agent will work on
   */
  override def register(blackboard: Blackboard) {
    this.blackboard = blackboard
    blackboard.registerAddObserver(this)
  }

  /**
   * Takes the current state of the Blackboard or variables set by
   * TriggerHandlers to check whether to execute the agent.
   * @return true if the agent can be executed, otherwise false.
   */
  override def guard(): Boolean = synchronized(!this.newFormulas.isEmpty)

  /**
   * Method that cancels an execution and possibly reverts its changes.
   */
  override def cancel(): Unit = return

  /**
   * <p>
   * If an Agent goes to sleep one execution should be done
   * </p>
   * @deprecated
   */
  override def goSleep(): Unit = return

  /**
   * <p>
   * Wakes Up an Observer after a change.
   * </p>
   * <p>
   * What happened during the change can be
   * given to the observer in a specialization.
   * </p>
   */
  override def wakeUp(): Unit = blackboard.scheduler.toWork(this)  // There should only be this thread waiting.
  /**
   * Passes the added formula to the Handler.
   * @param f
   */
  override def addFormula(f: Store[FormulaStore]): Unit = newFormulas.add(f)

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filterAdd(f: Store[FormulaStore]): Boolean = false //f read {norm applicable _.formula}
}
