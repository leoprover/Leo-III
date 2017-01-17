package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class UnificationRule(implicit signature : Signature) extends Rule{
  override final val interest: Seq[DataType] = Seq(Unify)

  override def name: String = "unify"
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(Unify).map(x => x.asInstanceOf[AnnotatedClause]).iterator

    var res : Seq[PrimsubstHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.unifyNewClauses(Set(c))
      res = new PrimsubstHint(c, ps) +: res
    }
    res
  }
}

class UnificationHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
  override def apply(): Result = {
    val r = Result()
    val it = nClauses.iterator
    while(it.hasNext){
      // TODO CNF
      r.insert(Unprocessed)(it.next())
    }
    r
  }

  override lazy val read: Map[DataType, Set[Any]] = Map(Unify -> Set(sClause))
  override lazy val write: Map[DataType, Set[Any]] = Map()
}


class RewriteRule(implicit signature : Signature) extends Rule{
  override final val interest: Seq[DataType] = Seq(Processed)

  override def name: String = "unify"
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(Unify).map(x => x.asInstanceOf[AnnotatedClause]).iterator

    var res : Seq[PrimsubstHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.unifyNewClauses(Set(c))
      res = new PrimsubstHint(c, ps) +: res
    }
    res
  }
}