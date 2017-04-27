package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class UnificationRule(inType : DataType[AnnotatedClause], outType : DataType[AnnotatedClause])
                     (implicit signature : Signature) extends Rule {
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  private final val withUpdate : Boolean = inType == outType

  override def name: String = "unify"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[UnificationHint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      val ps = Control.unifyNewClauses(Set(c))
      if(ps.nonEmpty || withUpdate) {
        res = new UnificationHint(c, ps) +: res
      }
    }
    res
  }

  class UnificationHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[Unification] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
      if(withUpdate) {
        r.remove(inType)(sClause)
        r.insert(outType)(sClause)
      }
      while (it.hasNext) {
        // TODO CNF
        r.insert(outType)(it.next())
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(Unify -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }


  class RewriteRule(inType : DataType[AnnotatedClause],
                   outType : DataType[AnnotatedClause],
                   rewriteRules : Set[AnnotatedClause]) // TODO export an interace
                   (implicit signature: Signature) extends Rule {
    override final val inTypes: Seq[DataType[Any]] = Seq(inType)
    override final val outTypes: Seq[DataType[Any]] = Seq(outType)

    override def name: String = "unify"

    override def canApply(r: Delta): Seq[Hint] = {
      val ins = r.inserts(Unify).iterator

      var res: Seq[RewriteHint] = Seq()
      //
      while (ins.hasNext) {
        val c = ins.next()
        val cnew = Control.rewriteSimp(c, rewriteRules)
        res = new RewriteHint(c, cnew) +: res
      }
      res
    }
  }

  class RewriteHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint {
    override def apply(): Delta = {
      val r = Result()
      r.remove(inType)(oldClause)
      r.insert(outType)(Control.simp(newClause))
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldClause))
  }

}