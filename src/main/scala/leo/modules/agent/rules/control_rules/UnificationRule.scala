package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.control.Control

/**
  * Created by mwisnie on 1/10/17.
  */
class UnificationRule(inType : DataType[AnnotatedClause],   // DataType of incomming data
                      outType : DataType[AnnotatedClause], // DataType of outgoing data
                      val moving : Boolean = true)
                     (implicit signature : Signature) extends Rule {
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def name: String = "unify"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      val ps = Control.unifyNewClauses(Set(c))
      if(ps.nonEmpty) {
        res = new UnificationHint(c, ps) +: res
      }  else {
        println(s"[Unify] Could not apply to ${c.cl.pretty(signature)}")
        if(moving){
          res = new MoveHint(c, inType, outType) +: res
        } else {
          res = new ReleaseLockHint(inType, c) +: res
        }
      }
    }
    res
  }

  class UnificationHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[Unification] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
      r.remove(inType)(sClause)
      while (it.hasNext) {
        val ne = it.next()
        r.insert(outType)(ne)
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(Unify -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }
}

class RewriteRule(inType : DataType[AnnotatedClause],
                  outType : DataType[AnnotatedClause],
                  rewriteRules : Set[AnnotatedClause], // TODO export an interface
                  val moving : Boolean = false)
                 (implicit signature: Signature) extends Rule {
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def name: String = "rewrite"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()

    while (ins.hasNext) {
      val c = ins.next()
      val cnew = Control.rewriteSimp(c, rewriteRules)
      if(cnew != c){
        println(s"[Rewrite] on ${c.pretty(signature)}\n  is now ${cnew.pretty(signature)}")
        res = new RewriteHint(c, cnew) +: res
      } else {
        println(s"[Rewrite] Could not apply to ${c.pretty(signature)}")
        if(moving){
          res = new MoveHint(c, inType, outType) +: res
        } else {
          res = new ReleaseLockHint(inType, c) +: res
        }
      }
    }
    res
  }

  class RewriteHint(oldClause : AnnotatedClause, newClause : AnnotatedClause) extends Hint {
    override def apply(): Delta = {
      val r = Result()
      r.remove(inType)(oldClause)
      val simp = Control.simp(newClause)
      r.insert(outType)(simp)

      r
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(oldClause))
  }
}

