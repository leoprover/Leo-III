package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.agent.rules.{Hint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/24/17.
  */
class LiftEqRule(inType : DataType[AnnotatedClause],
                 outType : DataType[AnnotatedClause])
                (implicit sig : Signature) extends Rule
{
  override val name: String = "lift_eq"
  override val inTypes: Seq[DataType[Any]] = Seq(inType)
  override val outTypes: Seq[DataType[Any]] = Seq(outType)
  private val withUpdate = inType != outType
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[Hint] = Seq()

    while(ins.hasNext){
      val c = ins.next()
      val lift = Control.liftEq(c)
      if(lift != c || withUpdate){
        res = new LiftEqHint(c, lift) +: res
      }
    }
    res
  }

  class LiftEqHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint{
    override def apply(): Delta = {
      println(s"[FuncExt] on ${oldClause.pretty(sig)}\n  > ${newClause.pretty(sig)}")
      val r = Result()
      r.remove(inType)(oldClause)
      r.insert(outType)(Control.simp(newClause))
    }
    override lazy val read: Map[DataType[Any], Set[Any]] = Map()
    override lazy val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldClause))
  }
}
