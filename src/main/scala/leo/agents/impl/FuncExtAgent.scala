package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Role_Plain}
import leo.datastructures.blackboard._
import Store._
import leo.modules.calculus.FuncExt

/**
 * Created by lex on 11.05.15.
 */
object FuncExtAgent extends Agent {
  /**
   *
   * @return the name of the agent
   */
  def name = "Functional Extensionality Agent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t: Task): Result = {
    t match {
      case FuncExtTask(f, hint) => {
        val nc = FuncExt.apply(f.clause, hint)
        Out.trace(s"[$name:]\n  Equalities in clause of ${f.pretty} type grounded\n New clause: ${nc.pretty}")
        Result().insert(FormulaType)(Store(nc, Role_Plain, f.context, f.status, ClauseAnnotation(FuncExt, f)))
      }
      case _: Task =>
        Out.warn(s"[$name]: Got a wrong task to execute.");
        Result()
    }

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
          Out.trace(s"[$name:]\n  Equalities in clause of ${f.pretty} can be type grounded")
          Seq(FuncExtTask(f, hint))
        } else {
          Seq()
        }
      }
      case _ : Event => Nil
    }
  }

  final private case class FuncExtTask(f: FormulaStore, hint: FuncExt.HintType) extends Task {
    val name = "func_ext"
    def writeSet() = Set.empty
    def readSet() = Set(f)
    def bid(budget: Double) = budget*hint._1.size / (hint._1.size + hint._2.size)
    lazy val pretty = s"func_ext(${f.pretty})"
  }
}

