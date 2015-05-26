package leo
package agents
package impl

import leo.datastructures.TimeStamp
import leo.datastructures.blackboard.impl.{SelectionTimeStore, TimeData, SelectionTimeType}
import leo.datastructures.blackboard._
import leo.modules.calculus.{Subsumption, FuncExt, BoolExt, StdPrimSubst}

/**
 * Created by lex on 20.05.15.
 */
object FormulaSelectionAgent extends Agent {
  val name = "Formula Selection Agent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  def run(t: Task): Result = {
    t match {
      case AddTimeStampTask(f) =>
        //if(f.clause.lits.size == 1) comment(s"Has selected clause ${f.pretty}.(weight=${t.bid(100)})")
        val r = Result()
        r.insert(SelectionTimeType)(TimeData(f, TimeStamp()))
        /* remove subsumed clauses from active */
//        val remove = SelectionTimeStore.noSelect(f.context).filterNot(f2 => Subsumption.subsumes(f2.clause, f.clause))
//        if (remove.nonEmpty) trace(s"Found by ${f.pretty} subsumable clauses in active, remove them.")
//        remove.foreach {case fs =>
//        r.remove(FormulaType)(fs)
//        }
        /* */
        r
      case RemoveFormulaTask(f) =>
        trace(s"Removed ${f.pretty}.")
        Result().remove(FormulaType)(f)
      case _ => Out.warn(s"[$name]: Got a wrong task to execute."); Result()
    }
  }

  def toFilter(event: Event) = {
    event match {
      case DataEvent(f: FormulaStore, FormulaType) => {
        // new formula, add timestamp to selectiontimestore
        if (!SelectionTimeStore.get(f).isDefined) {
          // Not yet paramodulated
          if (!Subsumption.canApply(f.clause, SelectionTimeStore.wasSelected(f.context).map(_.clause).toSet)) {
            Seq(AddTimeStampTask(f))
          } else {
            trace(s"Found subsumed clause ${f.pretty}, create remove task")
            Seq(RemoveFormulaTask(f))
          }
        } else {
          Seq()
        }
      }
      case _ => Seq()
    }
  }

  final private case class RemoveFormulaTask(f: FormulaStore) extends Task {
    val name = "RemoveFormulaTask"
    def writeSet() = Set(f)
    def readSet() = Set()
    def bid(budget: Double) = budget/f.clause.weight
    lazy val pretty = s"Task: Remove subsumed formula ${f.pretty}"
  }

  final private case class AddTimeStampTask(f: FormulaStore) extends Task {
    val name = "AddTimeStampTask"
    def writeSet() = Set()
    def readSet() = Set(f)
    def bid(budget: Double) = budget/f.clause.weight
    lazy val pretty = s"Task: Add time stamp to ${f.pretty}"
  }
}
