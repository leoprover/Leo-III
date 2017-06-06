package leo.modules.agent.rules
package control_rules

import leo.Configuration
import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class PrimsubstRule(inType : DataType[AnnotatedClause],
                   outType : DataType[AnnotatedClause],
                   noUnify : DataType[AnnotatedClause])
                   (implicit state : GeneralState[AnnotatedClause]) extends Rule {
  implicit val sig : Signature = state.signature
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  override final val moving: Boolean = false

  override def name: String = "primsubst"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      val ps = Control.primsubst(c).filterNot(c => Clause.trivial(c.cl))
      if(ps.nonEmpty) {
        res = new PrimsubstHint(c, ps) +: res
      }
      else {
//        println(s"[PrimSubst] Could not apply to ${c.pretty(signature)}")
      }
      res = new ReleaseLockHint(inType, c) +: res
    }
    res
  }

  class PrimsubstHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      leo.Out.debug(s"[PrimSubst] on ${sClause.pretty(state.signature)}\n  > ${nClauses.map(_.pretty(state.signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
//      r.remove(LockType(inType))(sClause)
      while (it.hasNext) {
        val c = Control.liftEq(it.next())
        if (c.cl.lits.exists(l => l.uni))
          r.insert(outType)(c)
        else {
          var newclauses = Control.cnf(c)
          newclauses = newclauses.map(cw => Control.simp(Control.liftEq(cw)))
          var newIt = newclauses.iterator
          while(newIt.hasNext) {
            r.insert(noUnify)(newIt.next)
          }
        }
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(inType -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }

}
