package leo.agents
package impl

import leo.datastructures.blackboard.{FormulaStore, Blackboard}
import leo.modules.normalization.Normalize


/**
 *
 * <p>
 * Normalization Agent for one normalization function.
 * </p>
 *
 * <p>
 * This Agent should register for formula Adds/Changes and applies Clause Normalization
 * as long as its possible. (Predicate is full filled.
 * </p>
 *
 * @author Max Wisniewski
 * @since 5/14/14
 */
class NormalClauseAgent(norm : Normalize) extends Agent {

  private var _isActive : Boolean = false

  override def isActive : Boolean = _isActive

  override def setActive(a : Boolean) = _isActive = a

  override def run(t : Task) : Result = ???

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  override def register() {
    Blackboard().registerAgent(this)
  }

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filter(f: FormulaStore): Set[Task] = if (norm.applicable(f.formula,f.status)) Set(new NormalTask(f)) else Set.empty
}

/**
 * Normalization applies to one Formula only and this one is read and written.
 * @param f - Formula to be normalized
 */
class NormalTask(f : FormulaStore) extends Task {
  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)
}
