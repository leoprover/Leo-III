package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.agent.rules.{Hint, LockType, ReleaseLockHint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class FactorRule(inType : DataType[AnnotatedClause], outType : DataType[AnnotatedClause])
                (implicit signature : Signature) extends Rule {
  override val name: String = "factor"

  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  override final val moving: Boolean = false

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()
    //
    while (ins.hasNext) {
      val c: AnnotatedClause = ins.next()
      val ps = Control.factor(c)
      if(!(ps.size == 1 && ps.head == c) && ps.size > 1) {
        res = new FactorHint(c, ps) +: res
      } else {
        println(s"[Factor] Could not apply to ${c.pretty(signature)} ")
      }
      res = new ReleaseLockHint(inType, c) +: res
    }
    res
  }

  class FactorHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[Factor] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
//      r.insert(LockType(inType))(sClause)
      while (it.hasNext) {
        val simpClause = Control.simp(it.next())
        r.insert(outType)(simpClause)
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(inType -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }

}