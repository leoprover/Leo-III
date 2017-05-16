package leo.modules.agent.rules

import leo.datastructures.blackboard.{DataType, Delta, ImmutableDelta}


/**
  * This rule can be instantiated for
  * the connection of two datatypes with
  * non moving rules attached to them.
  *
  * It observes a lock on the set and after
  * release, the data will be moved to the
  * new data type
  */
class MovingRule[A](inType : DataType[A],
                   outType : DataType[A],
                   barrier : AgentBarrier[A]) extends Rule {
  override val name: String = s"move(${inType.toString} -> ${outType})"
  private val lock = LockType(inType)
  override val inTypes: Seq[DataType[Any]] = Seq(inType, lock)
  override val moving: Boolean = true
  override val outTypes: Seq[DataType[Any]] = Seq(outType)

  override def canApply(r: Delta): Seq[Hint] = {
    val releasedLocks = r.removes(lock)
    Seq(new MoveHint(releasedLocks))
  }

  class MoveHint(toMove : Seq[A]) extends Hint{
    override lazy val apply: Delta = new ImmutableDelta(Map(outType -> toMove), Map(inType -> toMove))
    override val read: Map[DataType[Any], Set[Any]] = Map()
    override val write: Map[DataType[Any], Set[Any]] = Map(inType -> toMove.toSet)
  }
}
