package leo.modules.agent.rules.control_rules

import leo.datastructures.{AnnotatedClause, Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.GeneralState
import leo.modules.agent.rules.{Hint, ReleaseLockHint, Rule}
import leo.modules.control.Control

/**
  * Created by mwisnie on 6/12/17.
  */
class ChoiceRule (inType : DataType[AnnotatedClause],
                  outType : DataType[AnnotatedClause],
                  noUnifyType : DataType[AnnotatedClause])
                 (implicit state : GeneralState[AnnotatedClause]) extends Rule {

  override val name: String = "choice"

  implicit val sig : Signature = state.signature
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType, noUnifyType)
  override final val moving: Boolean = false

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(inType).iterator

    var res: Seq[Hint] = Seq()
    //
    while (ins.hasNext) {
      val c: AnnotatedClause = ins.next()
      val ps = Control.instantiateChoice(c).filterNot(c => Clause.trivial(c.cl))
      if(ps.nonEmpty) {
        res = new ChoiceHint(c, ps) +: res
      } else {
        //        println(s"[Factor] Could not apply to ${c.pretty(signature)} ")
      }
      res = new ReleaseLockHint(inType, c) +: res
    }
    res
  }

  class ChoiceHint(c : AnnotatedClause, ps : Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      leo.Out.debug(s"[Choice] on ${c.pretty(state.signature)}\n  > ${ps.map(_.pretty(state.signature)).mkString("\n  > ")}")
      val r = Result()
      val it = ps.iterator
      //      r.insert(LockType(inType))(sClause)
      while (it.hasNext) {
        val simpClause = Control.liftEq(it.next())
        if(simpClause.cl.lits.exists(l => l.uni)) {
          r.insert(outType)(simpClause)
        } else {
          var newclauses = Control.cnf(simpClause)
          newclauses = newclauses.map(cw => Control.simp(Control.liftEq(cw)))
          var newIt = newclauses.iterator
          while(newIt.hasNext) {
            r.insert(noUnifyType)(newIt.next)
          }
        }
      }
      r
    }
    override def read: Map[DataType[Any], Set[Any]] = Map()
    override def write: Map[DataType[Any], Set[Any]] = Map()
  }
}