package leo
package agents
package impl

import leo.datastructures.blackboard._
import Store._
import leo.modules.proofCalculi.FuncExt

/**
 * Created by lex on 11.05.15.
 */
object FuncExtAgent extends Agent {
  /**
   *
   * @return the name of the agent
   */
  def name = "Functional Extensionality Agent"

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t: Task): Result = {
    t match {
      case FuncExtTask(f, hint) => {
        val nc = FuncExt.apply(f.clause, hint)
        Result().insert(FormulaType)(Store(nc, f.status, f.context))
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
        val (canApply, hint) = FuncExt.canApply(f.clause)
        if (canApply) {
          Seq(FuncExtTask(f, hint))
        } else {
          Seq()
        }
      }
      case _ : Event => Nil
    }
  }
}

private case class FuncExtTask(f: FormulaStore, hint: FuncExt.HintType) extends Task {
  def name = "func_ext"
  def writeSet() = Set.empty
  def readSet() = Set(f)

  def bid(budget: Double) = budget*hint._1.size / (hint._1.size + hint._2.size)

  def pretty = s"func_ext(${f.pretty})"
}