package leo
package agents
package impl

import leo.datastructures.{ClauseAnnotation, Role_Plain}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.UnificationTaskType
import leo.modules.calculus.RestrFac

/**
 * Created by lex on 24.05.15.
 */
object RestrFactAgent extends Agent {
  val name = "Restricted Factorization Agent"
  override val interest = Some(Seq(FormulaType))

  def run(t: Task): Result = {
    t match {
      case RestrFacTask(fs, hint) => {
        val res = RestrFac(fs.clause, hint)
        trace(s"Applied restricted factorization on ${fs.pretty}\n New clause: ${res.pretty}.")
        Result().insert(UnificationTaskType)(Store(res, Role_Plain, fs.context, fs.status, ClauseAnnotation(RestrFac, fs)))
      }
      case _ => Out.warn(s"[$name]: Got a wrong task to execute.");
        Result()
    }
  }


  def toFilter(event: Event) = {
    event match {
      case DataEvent(f: FormulaStore, FormulaType) => {
        val (canApply, hint) = RestrFac.canApply(f.clause)
        if (canApply)
          Seq(RestrFacTask(f, hint))
        else
          Seq()
      }
      case _ : Event => Seq()
    }
  }

  final private case class RestrFacTask(fs: FormulaStore, hint: RestrFac.HintType) extends Task {
    val name = "Restr Fac Task"
    def writeSet() = Set()
    def readSet() = Set(fs)
    def bid(budget: Double) = budget / 5
    lazy val pretty = s"Restr Fac Task on ${fs.pretty}"
  }
}
