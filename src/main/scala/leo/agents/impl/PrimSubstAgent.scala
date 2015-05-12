package leo
package agents
package impl

import leo.datastructures.blackboard._
import Store._

/**
 * Created by lex on 11.05.15.
 */
class PrimSubstAgent extends Agent {
  /**
   *
   * @return the name of the agent
   */
  def name = "Agent PrimSubst"

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t: Task): Result = {
    t match {
      case PrimSubstTask(f) => {
        import leo.modules.proofCalculi.StdPrimSubst
        val ncs = StdPrimSubst.apply(f.clause)
        val res = Result()
        for (cl <- ncs) {
          res.insert(FormulaType)(Store(cl, f.status, f.context))
        }
      }
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.")
    }
    Result()
  }

  /**
   * Triggers the filtering of the Agent.
   *
   * Upon an Event the Agent can generate Tasks, he wants to execute.
   * @param event on the blackboard concerning change of data.
   * @return a List of Tasks the Agent wants to execute.
   */
  def toFilter(event: Event) = {
    event match {
      case DataEvent(f: FormulaStore, FormulaType) => {
        if (f.clause.flexHeadLits.nonEmpty) {
          Seq(PrimSubstTask(f))
        } else {
          Seq()
        }
      }
      case _ : Event => Nil
    }
  }
}

private case class PrimSubstTask(f: FormulaStore) extends Task {
  /**
   * Prints a short name of the task
   * @return
   */
  def name = "prim_subst"

  /**
   *
   * Returns a set of all Formulas, that will be written by the task.
   *
   * @return Write set for the task
   */
  def writeSet() = Set(f)

  /**
   *
   * Returns a set of all Formulas that are read for the task.
   *
   * @return Read set for the Task.
   */
  def readSet() = Set.empty

  /**
   *
   * Defines the gain of a Task, defined for
   * a specific agent.
   *
   * @return - Possible profit, if the task is executed
   */
  def bid(budget: Double) = budget / 20

  def pretty = "prim_subst"
}