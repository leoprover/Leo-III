package agents
package impl

import blackboard.{FormulaRemoveObserver, Blackboard, FormulaAddObserver}
import datastructures.tptp.Commons.{AnnotatedFormula => Formula}
import normalization.Normalize
import scala.collection.mutable

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

  private val newFormulas : mutable.Set[Formula] = new mutable.HashSet[Formula]()
  private var blackboard : Blackboard = null  // Will be inserted by registering to a blackboard

  /**
   * This function applies the normalization provided by {@see norm}, it is assumed that the formulas added
   * are already checked by {@see Normalize.applicable}.
   */
  override def apply() {
    if (blackboard == null) throw new RuntimeException("An Apply was called for an Agent, that has no Blackboard assigned.")
    // We cannot add the formulas in the synchronized block or we will knock ourselfs out.
    val simplFormulas : mutable.Set[Formula] = new mutable.HashSet[Formula]()
    synchronized{
      newFormulas.foreach(f => {
        val f1: Formula = norm.normalize(f)
        if (!f1.equals(f)) {
          simplFormulas.add(f1)
          println("Simplified '" ++ f.toString ++ "' to '" ++ f1.toString ++ "'.")
        }
       })
      newFormulas.clear()
    }
    simplFormulas.foreach(blackboard.addFormula(_))
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
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filterAdd(f: Formula): Boolean = synchronized {norm.applicable(f)}

  /**
   * Passes the added formula to the Handler.
   * @param f
   */
  override def addFormula(f: Formula) {
    synchronized {
      newFormulas.add(f)
    }
  }

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

}
