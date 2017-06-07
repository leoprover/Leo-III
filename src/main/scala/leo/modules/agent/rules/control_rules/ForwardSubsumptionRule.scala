package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Result}
import leo.modules.{FVState, GeneralState}
import leo.modules.control.Control
import leo.modules.control.Control.LocalFVState

/**
  * Tests forward subsumption for a selected clause and the processed set.
  * If the optional parameter is set, the subsumption works as a conditional move rule.
  */
class ForwardSubsumptionRule(inType : DataType[AnnotatedClause],
                             processed : ProcessedSet,
                             move : Option[(DataType[AnnotatedClause], AgentBarrier[AnnotatedClause])])
                            (implicit val state : FVState[AnnotatedClause])
  extends Rule{
  override val name: String = "forwardSubsumption"
  implicit val sig : Signature = state.signature
  override val inTypes: Seq[DataType[Any]] = Seq(inType) ++ move.fold(Seq.empty[DataType[AnnotatedClause]])(x => Seq(x._2.lockType))
  override val moving: Boolean = move.isDefined
  override val outTypes: Seq[DataType[Any]] = move.fold(Seq.empty[DataType[AnnotatedClause]])(x => Seq(x._1))
  override def canApply(r: Delta): Seq[Hint] = {
    if(moving){
      val (outType, barrier) = move.get
      val removedLocks = r.removes(barrier.lockType)
      val actProcessed = processed.get
      removedLocks map(c => new ForwardSubsumptionHint(c, actProcessed))
    } else {
      val newForms = r.inserts(inType)
      val actProcessed = processed.get
      newForms map(c => new ForwardSubsumptionHint(c, actProcessed))
    }
  }

  class ForwardSubsumptionHint(c : AnnotatedClause, processed : Set[AnnotatedClause])(implicit state: LocalFVState) extends Hint{
    override def apply(): Delta = {
      val red = Control.redundant(c, processed)
      val r = Result()
      if(red && !moving) {
        leo.Out.debug(s"[Subsumption] Removed clause ($inType) ${c.pretty}")
        r.remove(inType)(c)
      } else if (moving){
        r.remove(inType)(c)
        if(!red) {
          val (outType, barrier) = move.get
          leo.Out.debug(s"[Subsumption] Moved clause ($inType -> $outType)\n  ${c.pretty(state.signature)}")
          r.insert(outType)(c)
        } else {
          leo.Out.debug(s"[Subsumption] Removed clause ($inType) ${c.pretty(state.signature)}")
        }
      }
      r
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(c))
  }
}
