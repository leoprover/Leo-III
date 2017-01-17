package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.agent.rules.{Hint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class FactorRule(implicit signature : Signature) extends Rule{
  override val name: String = "factor"

  override final val interest: Seq[DataType] = Seq(Processed)

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(Processed).map(x => x.asInstanceOf[AnnotatedClause]).iterator

    var res : Seq[FactorHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.factor(c)
      res = new FactorHint(c, ps) +: res
    }
    res
  }
}

class FactorHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
  override def apply(): Result = {
    val r = Result()
    val it = nClauses.iterator
    while(it.hasNext){
      r.insert(Unprocessed)(it.next())
    }
    r
  }

  override lazy val read: Map[DataType, Set[Any]] = Map(Processed -> Set(sClause))
  override lazy val write: Map[DataType, Set[Any]] = Map()
}
