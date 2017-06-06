package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Clause, ClauseAnnotation, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.control.Control
import leo.modules.interleavingproc.{DerivedClause, OpenUnification, SZSStatus, UnprocessedClause}
import leo.modules.output.{SZS_ContradictoryAxioms, SZS_Theorem}

/**
  * Created by mwisnie on 1/10/17.
  */
class UnificationRule(inType : DataType[AnnotatedClause],   // DataType of incomming data
                      outType : DataType[AnnotatedClause], // DataType of outgoing data
                      val moving : Boolean = true)
                     (implicit state : GeneralState[AnnotatedClause]) extends Rule {
  implicit val sig : Signature = state.signature
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def name: String = "unify"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()

    // Split unifiable and non-unifiable clauses
    var toUnify = Set[AnnotatedClause]()
    while (ins.hasNext) {
      var ncl = ins.next()
      if (!Clause.trivial(ncl.cl)) {
        res = new UnificationHint(ncl) +: res
      }
    }
    res
  }

  class UnificationHint(sClause: AnnotatedClause) extends Hint {
    override def apply(): Delta = {
//      println(s"[Unification] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      r.remove(inType)(sClause)

      var newclauses = Control.unifyNewClauses(Set(sClause))

      /* exhaustively CNF new clauses */
      newclauses = newclauses.flatMap(Control.cnf)
      /* Replace eq symbols on top-level by equational literals. */
      newclauses = newclauses.map(cw => Control.shallowSimp(Control.liftEq(cw)))

      val newIt = newclauses.iterator

      if(newIt.isEmpty){
        leo.Out.debug(s"[Unification] No Unifier found ${sClause.pretty(state.signature)}")
        r.insert(outType)(sClause)
      }

      while (newIt.hasNext) {
        val newCl = newIt.next()
//        newCl = Control.simp(newCl)
        assert(Clause.wellTyped(newCl.cl), s"Clause [${newCl.id}] is not well-typed")
        if (!Clause.trivial(newCl.cl)) {
          leo.Out.debug(s"[Unification] Unified\n  ${sClause.pretty(state.signature)}\n >\n   ${newCl.pretty(state.signature)}")
          r.insert(outType)(newCl)
        } else {
          //          sb.append(s"${ac.pretty(sig)} was trivial")
        }
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
                 (implicit state : GeneralState[AnnotatedClause]) extends Rule {
  implicit val sig : Signature = state.signature
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def name: String = "rewrite"

  override def canApply(r: Delta): Seq[Hint] = {
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()

    while (ins.hasNext) {
      val c = ins.next()
      val cnew = Control.rewriteSimp(c, rewriteRules)
      if(cnew != c & !Clause.trivial(cnew.cl)){
        leo.Out.debug(s"[Rewrite] on ${c.pretty(sig)}\n  is now ${cnew.pretty(sig)}")
        res = new RewriteHint(c, cnew) +: res
      } else {
//        println(s"[Rewrite] Could not apply to ${c.pretty(signature)}")
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

