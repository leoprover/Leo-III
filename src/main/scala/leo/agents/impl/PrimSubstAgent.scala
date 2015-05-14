package leo
package agents
package impl

import leo.datastructures.blackboard._
import Store._
import leo.modules.proofCalculi.StdPrimSubst

/**
 * Created by lex on 11.05.15.
 */
object PrimSubstAgent extends Agent {
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
        val ncs = StdPrimSubst.apply(f.clause, ())
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
        if (StdPrimSubst.canApply(f.clause)._1) {
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
  def name = "prim_subst"
  def writeSet() = Set.empty
  def readSet() = Set(f)

  def bid(budget: Double) = budget*f.clause.flexHeadLits.size / f.clause.lits.size

  def pretty = s"prim_subst(${f.pretty})"
}