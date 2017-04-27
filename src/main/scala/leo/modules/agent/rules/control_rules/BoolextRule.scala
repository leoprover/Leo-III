package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class BoolextRule(inType : DataType[AnnotatedClause], outType : DataType[AnnotatedClause])(implicit signature : Signature) extends Rule{

  override final val inTypes : Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  private final val withUpdate : Boolean = inType == outType

  override def name: String = "bool_ext"
  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res : Seq[BoolextHint] = Seq()
    //
    while(ins.hasNext){
      val cl = ins.next()
      val fcl = Control.boolext(cl)
      if(!(fcl.size == 1 && cl == fcl.head) || withUpdate){
        res = new BoolextHint(cl, fcl) +: res
      }
    }
    res
  }

  class BoolextHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
    override def apply(): Delta = {
      println(s"[BoolExt] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      if(withUpdate) {
        val simpClause = Control.simp(sClause)
        r.remove(inType)(sClause)
        r.insert(outType)(simpClause)
      }
      val it = nClauses.iterator
      while(it.hasNext){
        val simpClause = Control.simp(it.next())
        r.insert(outType)(simpClause)
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map()
    override lazy val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(sClause))
  }

}
