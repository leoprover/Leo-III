package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.agent.rules.{Hint, MoveHint, ReleaseLockHint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/24/17.
  */
class LiftEqRule(inType : DataType[AnnotatedClause],
                 outType : DataType[AnnotatedClause])
                (implicit state : GeneralState[AnnotatedClause]) extends Rule
{
  implicit val sig : Signature = state.signature
  override val name: String = "lift_eq"
  override val inTypes: Seq[DataType[Any]] = Seq(inType)
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  val moving = inType != outType

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[Hint] = Seq()

    while(ins.hasNext){
      val c = ins.next()
      val lift = Control.liftEq(c)
      if(lift != c && !Clause.trivial(lift.cl)){
        res = new LiftEqHint(c, lift) +: res
      } else {
//        println(s"[LiftEq] Could not apply to ${c.pretty(sig)} ")
        if(moving){
          res = new MoveHint(c, inType, outType) +: res
        } else {
          res = new ReleaseLockHint(outType, c) +: res
        }
      }
    }
    res
  }

  class LiftEqHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint{
    override def apply(): Delta = {
      leo.Out.debug(s"[FuncExt] on ${oldClause.pretty(state.signature)}\n  > ${newClause.pretty(state.signature)}")
      val r = Result()
      r.remove(inType)(oldClause)
      val simp = Control.simp(newClause)
      r.insert(outType)(simp)
      r
    }
    override lazy val read: Map[DataType[Any], Set[Any]] = Map()
    override lazy val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldClause))
  }
}
