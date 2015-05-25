package leo
package agents
package impl

import leo.datastructures.TimeStamp
import leo.datastructures.blackboard.impl.{SelectionTimeStore, TimeData, SelectionTimeType}
import leo.datastructures.blackboard._
import leo.modules.calculus.{FuncExt, BoolExt, StdPrimSubst}

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
        Result().insert(SelectionTimeType)(TimeData(f, TimeStamp()))
      case _ => Out.warn(s"[$name]: Got a wrong task to execute."); Result()
    }
  }

  def toFilter(event: Event) = {
    event match {
      case DataEvent(f: FormulaStore, FormulaType) => {
        // new formula, add timestamp to selectiontimestore
        if (!SelectionTimeStore.get(f).isDefined /*&& !BoolExt.canApply(f.clause)._1 && !FuncExt.canApply(f.clause)._1*/) {
          val t = AddTimeStampTask(f)
          //if(f.clause.lits.size == 1) comment(s"Try select clause ${f.pretty}.(weight=${t.bid(100)})")
          Seq(t)
        } else {
          Seq()
        }
      }
      case _ => Seq()
    }
  }


  private case class AddTimeStampTask(f: FormulaStore) extends Task {
    val name = "AddTimeStampTask"
    def writeSet() = Set()
    def readSet() = Set(f)

    def bid(budget: Double) = budget/f.clause.weight

    lazy val pretty = s"Task: Add time stamp to ${f.pretty}"
  }
}
