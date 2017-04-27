package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/21/17.
  */
class CNFRule(inType : DataType[AnnotatedClause],
             outType : DataType[AnnotatedClause])
             (implicit sig : Signature) extends Rule{
  override final val name: String = "cnf_rule"
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  private final val withUpdate = inType != outType

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[CNFHint] = Seq()
    while(ins.hasNext) {
      val org = ins.next()
      val cnf = Control.cnf(org)
      if(!(cnf.size == 1 && cnf.head == org) || withUpdate) {
        println(s"[CNF] can apply on ${org.pretty(sig)}")
        res = new CNFHint(org, cnf) +: res
      } else {
        println(s"[CNF] cannot apply on ${org.pretty(sig)}")
      }
    }
    res
  }

  class CNFHint(oldClause : AnnotatedClause, newClauses : Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[CNF] on ${oldClause.pretty(sig)}\n  > ${newClauses.map(_.pretty(sig)).mkString("\n  > ")}")
      val r = Result()
      r.remove(inType)(oldClause)
      val it = newClauses.iterator
      while(it.hasNext){
        val simpClause = Control.simp(it.next())
        r.insert(outType)(simpClause)
      }
      r
    }

    override final val read: Map[DataType[Any], Set[Any]] = Map()
    override final val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldClause))
  }
}
