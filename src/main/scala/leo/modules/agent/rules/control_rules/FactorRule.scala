package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.agent.rules.{Hint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class FactorRule(inType : DataType[AnnotatedClause], outType : DataType[AnnotatedClause])
                (implicit signature : Signature) extends Rule {
  override val name: String = "factor"

  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(inType).iterator

    var res: Seq[FactorHint] = Seq()
    //
    while (ins.hasNext) {
      val c: AnnotatedClause = ins.next()
      val ps = Control.factor(c)
      if(!(ps.size == 1 && ps.head == c)) {
        res = new FactorHint(c, ps) +: res
      }
    }
    res
  }

  class FactorHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      val r = Result()
      val it = nClauses.iterator
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