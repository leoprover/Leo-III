package leo.modules.agent.rules
package control_rules

import leo.modules.control.Control
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}

/**
  * Applies paramodulation for a newly selected formula
  * with all previously processed formulas.
  */
class ParamodRule(processed : ProcessedSet)(implicit signature : Signature) extends Rule{
  override val name: String = "paramod"

  override val observedDataStructures: Seq[DataStore] = Seq(processed)
  override final val interest: Seq[DataType[Any]] = Seq(Processed)

  override def canApply(r: Delta): Seq[Hint] = {
    // All new selected clauses
    val ins = r.inserts(Processed).map(x => x.asInstanceOf[AnnotatedClause]).iterator

    var p = processed.get
    var res : Seq[ParamodHint] = Seq()
    //
    while(ins.hasNext){
      val c = ins.next()
      val ps = Control.paramodSet(c, p)
      p += c   // Take the new one into consideration for the next selected clause
      res = new ParamodHint(c, ps) +: res
    }
    res
  }
}

// Paramodulation of all processed clauses with a new one
class ParamodHint(sClause : AnnotatedClause, nClauses : Set[AnnotatedClause]) extends Hint{
  override def apply(): Delta = {
    val r = Result()
    val it = nClauses.iterator
    while(it.hasNext){
      r.insert(Unprocessed)(it.next())
    }
    r
  }

  override lazy val read: Map[DataType[Any], Set[Any]] = Map(Processed -> Set(sClause))
  override lazy val write: Map[DataType[Any], Set[Any]] = Map()
}
