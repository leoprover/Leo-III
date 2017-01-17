package leo.modules.agent.rules
package control_rules

import leo.Configuration
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class PrimsubstRule(implicit signature : Signature) extends Rule{
  override final val interest: Seq[DataType] = Seq(Processed)

  private lazy val primsubstlevel = Configuration.PRIMSUBST_LEVEL

  override def name: String = "primsubst"
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(Processed).map(x => x.asInstanceOf[AnnotatedClause]).iterator

    var res : Seq[PrimsubstHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.primsubst(c, primsubstlevel)
      res = new PrimsubstHint(c, ps) +: res
    }
    res
  }
}

class PrimsubstHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
  override def apply(): Result = {
    val r = Result()
    val it = nClauses.iterator
    while(it.hasNext){
      val c = it.next()
      if(c.cl.lits.exists(l => l.uni))
        r.insert(Unify)(it.next())
      else
        r.insert(Unprocessed)(it.next())
    }
    r
  }

  override lazy val read: Map[DataType, Set[Any]] = Map(Processed -> Set(sClause))
  override lazy val write: Map[DataType, Set[Any]] = Map()
}
