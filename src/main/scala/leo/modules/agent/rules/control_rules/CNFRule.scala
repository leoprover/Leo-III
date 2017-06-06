package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.control.Control

/**
  * Created by mwisnie on 4/21/17.
  */
class CNFRule(inType : DataType[AnnotatedClause],
             outType : DataType[AnnotatedClause],
             val moving : Boolean = false)
             (implicit state : GeneralState[AnnotatedClause]) extends Rule{

  implicit val sig : Signature = state.signature
  override final val name: String = "cnf_rule"
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator
    var res : Seq[Hint] = Seq()
    while(ins.hasNext) {
      val org = ins.next()
      val cnf = Control.cnf(org).filterNot(c => Clause.trivial(c.cl))
      if(!(cnf.size == 1 && cnf.head == org) || moving) {
        leo.Out.debug(s"[CNF] can apply on ${org.pretty(state.signature)}")
        res = new CNFHint(org, cnf) +: res
      } else {
//        println(s"[CNF] Could not apply to ${org.pretty(sig)}")
        if(moving){
          res = new MoveHint(org, inType, outType) +: res
        } else {
          res = new ReleaseLockHint(inType, org) +: res
        }
      }
    }
    res
  }

  class CNFHint(oldClause : AnnotatedClause, newClauses : Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      leo.Out.debug(s"[CNF] on ${oldClause.pretty(state.signature)}\n  > ${newClauses.map(_.pretty(state.signature)).mkString("\n  > ")}")
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
