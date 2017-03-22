package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class BoolextRule(implicit signature : Signature) extends Rule{

  override final val interest: Seq[DataType[Any]] = Seq(Processed)

  override def name: String = "boolext"
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(Processed).iterator

    var res : Seq[BoolextHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.boolext(c)
      res = new BoolextHint(c, ps) +: res
    }
    res
  }
}

class BoolextHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
  override def apply(): Delta = {
    val r = Result()
    val it = nClauses.iterator
    while(it.hasNext){
      r.insert(Unprocessed)(it.next())
    }
    r
  }

  override lazy val read: Map[DataType[Any], Set[Any]] = Map(Processed -> Set(sClause))
  override lazy val write: Map[DataType[Any], Set[Any]] = Map()
}
