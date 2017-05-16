package leo.modules.agent.rules
package control_rules

import leo.Configuration
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class PrimsubstRule(inType : DataType[AnnotatedClause],
                   outType : DataType[AnnotatedClause])   // TODO One (Unify) / Two (Unprocessed)
                   (implicit signature : Signature) extends Rule {
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  override final val moving: Boolean = false

  private lazy val primsubstlevel = Configuration.PRIMSUBST_LEVEL

  override def name: String = "primsubst"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(Processed).iterator

    var res: Seq[PrimsubstHint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      val ps = Control.primsubst(c, primsubstlevel)   // TODO Check changes and in/out types. Do nothing if possible
      res = new PrimsubstHint(c, ps) +: res
    }
    res
  }

  class PrimsubstHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[PrimSubst] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
      r.remove(LockType(inType))(sClause)
      while (it.hasNext) {
        val c = it.next()
        if (c.cl.lits.exists(l => l.uni))
          r.insert(Unify)(it.next())
        else
          r.insert(Unprocessed)(it.next())
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(Processed -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }

}
