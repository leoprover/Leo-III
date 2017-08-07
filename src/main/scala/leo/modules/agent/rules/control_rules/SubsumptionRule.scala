package leo.modules.agent.rules
package control_rules
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, MutableDelta, Result}
import leo.modules.{FVState, GeneralState}
import leo.modules.control.Control
import leo.modules.control.Control.LocalFVState

/**
  * Tests forward subsumption for a selected clause and the processed set.
  * If the optional parameter is set, the subsumption works as a conditional move rule.
  */
class SubsumptionRule(inType : DataType[AnnotatedClause],
                      processedSet : ProcessedSet,
                      move : Option[(DataType[AnnotatedClause], AgentBarrier[AnnotatedClause])])
                     (implicit val state : FVState[AnnotatedClause])
  extends Rule{
  override val name: String = "subsumption"
  implicit val sig : Signature = state.signature
  override val inTypes: Seq[DataType[Any]] = Seq(inType) ++ move.fold(Seq.empty[DataType[AnnotatedClause]])(x => Seq(x._2.lockType))
  val moving: Boolean = move.isDefined
  override val outTypes: Seq[DataType[Any]] = move.fold(Seq.empty[DataType[AnnotatedClause]])(x => Seq(x._1))
  override def canApply(r: Delta): Seq[Hint] = {
    if(moving){
      val (outType, barrier) = move.get
      val removedLocks = r.removes(barrier.lockType)
      val actProcessed = processedSet.get
      removedLocks map(c => new MovingSubsumptionHint(c, actProcessed))
    } else {
      val newForms = r.inserts(inType)
      val actProcessed = processedSet.get
      newForms map(c => new StaySubsumptionHint(c, actProcessed))
    }
  }

  private def backward(c : AnnotatedClause, processed : Set[AnnotatedClause], r : MutableDelta) : MutableDelta = {
    val red = Control.backwardSubsumptionTest(c, processed).iterator
    while(red.hasNext){
      val rc = red.next()
      leo.Out.debug(s"[Subsumption] Backward subsumed clause ${rc.pretty(sig)}\n by ${c.pretty(sig)}")
      r.remove(processedSet.processedType)(rc)
    }
    r
  }

  class StaySubsumptionHint(c : AnnotatedClause, processed : Set[AnnotatedClause]) extends Hint{
    override def apply(): Delta = {
      val red = Control.redundant(c, processed)
      val r = Result()
      if(red) {
        leo.Out.debug(s"[Subsumption] Removed clause ($inType) ${c.pretty(sig)}")
        r.remove(inType)(c)
        r
      } else {
        backward(c, processed, r)
      }
    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(c))
  }

  class MovingSubsumptionHint(c : AnnotatedClause, processed: Set[AnnotatedClause]) extends Hint {
    override def apply(): Delta = {
      val red = Control.redundant(c, processed)
      val r = Result()
      r.remove(inType)(c)
      if(!red) {
        val (outType, barrier) = move.get
        leo.Out.debug(s"[Subsumption] Moved clause ($inType -> $outType)\n  ${c.pretty(state.signature)}")
        r.insert(outType)(c)
        backward(c, processed, r)
      } else {
        leo.Out.debug(s"[Subsumption] Removed clause ($inType) ${c.pretty(state.signature)}")
        r
      }

    }
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> Set(c))
  }
}
