package leo.datastructures.blackboard

import leo.datastructures.{AnnotatedClause, ClauseProxy}
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.SZSException
import leo.modules.output.{SZS_Error, StatusSZS}

/**
 * <p>
 * DataType is a marker interface for all Storable Data in the Blackboard.
 * </p>
 * <p>
 * It is used to try to inform Agents only of interesting changes in the blackboard
 * and to register Datastructures only for their changes.
 * </p>
 *
 * @author Max Wisniewski
 * @since 4/15/15
 */
trait DataType[+T] {
  def convert(d : Any) : T
}


/**
 * The FormulaType marks any formula data for the blackboard.
 */
case object ClauseType extends DataType[ClauseProxy] {
  def convert(d : Any) : AnnotatedClause = d match {
    case ac : AnnotatedClause => ac
    case _ =>
      leo.Out.severe(s"Tried to cast $d to ClauseType.")
      throw new SZSException(SZS_Error, s"$d is not of ClauseType.")
  }
}

/**
 * SZS status Type information.
 */
case object StatusType extends DataType[StatusSZS] {
  def convert(d : Any) : StatusSZS = d match {
    case s : StatusSZS => s
    case _ =>
      leo.Out.severe(s"Tried to cast $d to StatusSZS.")
      throw new SZSException(SZS_Error, s"$d is not of StatusType.")
  }
}
