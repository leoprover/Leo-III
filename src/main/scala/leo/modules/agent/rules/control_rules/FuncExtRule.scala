package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.agent.rules.{Hint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/21/17.
  */
class FuncExtRule(inType : DataType[AnnotatedClause],
                 outType : DataType[AnnotatedClause])
                 (implicit sig : Signature) extends Rule
{
  override final val name: String = "func_ext"
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  private final val withUpdate = inType != outType
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[FuncExtHint] = Seq()

    while(ins.hasNext){
      val cl = ins.next()
      val fcl = Control.funcext(cl)
      if(cl != fcl || withUpdate){
        res = new FuncExtHint(cl, fcl) +: res
      }
    }
    res
  }

  class FuncExtHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint{
    override def apply(): Delta = {
      val r = Result()
      r.remove(inType)(oldClause)
      r.insert(outType)(Control.simp(newClause))
      r
    }
    override lazy val read: Map[DataType[Any], Set[Any]] = Map()
    override lazy val write: Map[DataType[Any], Set[Any]] = Map(outType -> Set(oldClause))
  }
}
