package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta}

/**
  * This rule selects a clause from Unprocessed
  * to be processed next.
  */
class SelectionRule(inType : DataType[AnnotatedClause]
                    , outType : DataType[AnnotatedClause])
                   (implicit signature : Signature)
  extends Rule{

  override val name: String = "selection_rule"
  override val inTypes: Seq[DataType[Any]] = Seq(inType)
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  override def moving: Boolean = true
  override def canApply(r: Delta): Seq[Hint] = {
    ???
  }
}


