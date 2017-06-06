package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.agent.rules.{Hint, MoveHint, ReleaseLockHint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/21/17.
  */
class FuncExtRule(inType : DataType[AnnotatedClause],
                 outType : DataType[AnnotatedClause],
                 val moving : Boolean = false)
                 (implicit state : GeneralState[AnnotatedClause]) extends Rule
{
  implicit val sig : Signature = state.signature
  override final val name: String = "func_ext"
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[Hint] = Seq()

    while(ins.hasNext){
      val cl = ins.next()
      val fcl = Control.funcext(cl)
      if(cl != fcl && !Clause.trivial(fcl.cl)){
        res = new FuncExtHint(cl, fcl) +: res
      }
      else {
//        println(s"[FuncExt] Could not apply to ${cl.pretty(sig)} ")
        if(moving){
          res = new MoveHint(cl, inType, outType) +: res
        } else {
          res = new ReleaseLockHint(outType, cl) +: res
        }
      }
    }
    res
  }

  class FuncExtHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint{
    override def apply(): Delta = {
      leo.Out.debug(s"[FuncExt] on ${oldClause.pretty(state.signature)}\n  > ${newClause.pretty(state.signature)}")
      val r = Result()
      r.remove(inType)(oldClause)
      val simp = Control.simp(newClause)
      r.insert(outType)(simp)
      r
    }
    override lazy val read: Map[DataType[Any], Set[Any]] = Map()
    override lazy val write: Map[DataType[Any], Set[Any]] = Map(outType -> Set(oldClause))
  }
}
