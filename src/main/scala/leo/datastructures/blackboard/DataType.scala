package leo.datastructures.blackboard

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
trait DataType {}


/**
 * The FormulaType marks any formula data for the blackboard.
 */
case object FormulaType extends DataType {}


/**
 * The FormulaType marks any context data for the blackboard.
 */
case object ContextType extends DataType {}

/**
 * SZS status Type information.
 */
case object StatusType extends DataType {}
