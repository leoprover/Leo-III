package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Role_Plain}
import leo.datastructures.blackboard._
import leo.modules.calculus.StdPrimSubst

/**
 * Created by lex on 11.05.15.
 */
object PrimSubstAgent extends Agent {
  /**
   *
   * @return the name of the agent
   */
  def name = "Agent PrimSubst"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t: Task): Result = {
    t match {
      case PrimSubstTask(f, hint) => {
        import leo.modules.calculus.StdPrimSubst
        val ncs = StdPrimSubst.apply(f.clause, hint)
        val res = Result()
        for (cl <- ncs) {
          res.insert(FormulaType)(Store(cl, Role_Plain, f.context, f.status, ClauseAnnotation(StdPrimSubst, f)))
        }
        Out.trace(s"[$name:]\n  Clause of ${f.pretty}:\nflex-heads instantiated, new clauses:\n ${ncs.map(_.pretty).mkString("\n")}")
        res
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
        val (canApply, hint) = StdPrimSubst.canApply(f.clause)
        if (canApply) {
          Seq(PrimSubstTask(f, hint))
        } else {
          Seq()
        }
      }
      case _ : Event => Nil
    }
  }

  final private case class PrimSubstTask(f: FormulaStore, hint: StdPrimSubst.HintType) extends Task {
    val name = "prim_subst"
    def writeSet() = Set.empty
    def readSet() = Set(f)
    def bid(budget: Double) = budget*f.clause.flexHeadLits.size / f.clause.lits.size
    lazy val pretty = s"prim_subst(${f.pretty})"
  }
}

