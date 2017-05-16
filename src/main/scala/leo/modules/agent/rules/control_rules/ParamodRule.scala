package leo.modules.agent.rules
package control_rules

import leo.modules.control.Control
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}

/**
  * Applies paramodulation for a newly selected formula
  * with all previously processed formulas.
  */
class ParamodRule(inType : DataType[AnnotatedClause],
                 outType : DataType[AnnotatedClause])
                 (processed : ProcessedSet)(implicit signature : Signature) extends Rule {
  override val name: String = "paramod"

  override val observedDataStructures: Seq[DataStore] = Seq(processed)
  override final val inTypes: Seq[DataType[Any]] = Seq(inType)
  override final val outTypes: Seq[DataType[Any]] = Seq(outType)
  override final val moving: Boolean = false

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(inType).iterator

    var p = processed.get
    var res: Seq[ParamodHint] = Seq()
    //
    while (ins.hasNext) {
      val c = ins.next()
      val ps = Control.paramodSet(c, p)
      p += c // Take the new one into consideration for the next selected clause
      res = new ParamodHint(c, ps) +: res
    }
    res
  }

  // Paramodulation of all processed clauses with a new one
  class ParamodHint(sClause: AnnotatedClause, nClauses: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      println(s"[Paramod] on ${sClause.pretty(signature)}\n  > ${nClauses.map(_.pretty(signature)).mkString("\n  > ")}")
      val r = Result()
      val it = nClauses.iterator
      r.remove(LockType(inType))(sClause)
      while (it.hasNext) {
        r.insert(outType)(Control.simp(it.next()))
      }
      r
    }

    override lazy val read: Map[DataType[Any], Set[Any]] = Map(inType -> Set(sClause))
    override lazy val write: Map[DataType[Any], Set[Any]] = Map()
  }

}
