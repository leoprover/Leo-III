package leo
package agents
package impl

import leo.datastructures.TimeStamp
import leo.datastructures.blackboard.impl.{SelectionTimeStore, TimeData, SelectionTimeType}
import leo.datastructures.blackboard._

/**
 * Created by lex on 20.05.15.
 */
object FormulaSelectionAgent extends Agent {
  val name = "Formula Selection Agent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  def run(t: Task): Result = {
    t match {
      case AddTimeStampTask(f) => Result().insert(SelectionTimeType)(TimeData(f, TimeStamp()))
      case _ => Out.warn(s"[$name]: Got a wrong task to execute."); Result()
    }
  }

  def toFilter(event: Event) = {
    event match {
      case DataEvent(f: FormulaStore, FormulaType) => {
        // new formula, add timestamp to selectiontimestore
        if (!SelectionTimeStore.get(f).isDefined) {
          Seq(AddTimeStampTask(f))
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
