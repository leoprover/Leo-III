package leo.modules.agent.rules.control_rules

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.DataType
import leo.modules.SZSException
import leo.modules.output.{SZS_Error, StatusSZS}

class ClauseType extends DataType[AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Tried to match ${d} to an annotated clause.")
  }
}

case object ResultType extends ClauseType
