package leo.modules.interleavingproc

import leo.datastructures.blackboard.{DataStore, DataType, Result}
import leo.datastructures.{ClauseProxy, IsSignature}
import leo.modules.output.logger.Out
import leo.modules.seqpproc.State

/**
  *
  * A capsule to enclose a state for the interleaving loop
  * in the blackboard to handle the transactions.
  *
  */
class BlackboardState[T <: ClauseProxy](val state : State[T]) extends DataStore {
  val apply : State[T] = state

  override def storedTypes: Seq[DataType] = ???
  override def clear(): Unit = {
    Out.info("Could not clear the state. Not yet implemented.")
  }
  override def all(t: DataType): Set[Any] = ???
  override def updateResult(r: Result): Boolean = ???
}



object BlackboardState {
  def fresh[T <: ClauseProxy](sig: IsSignature)(implicit unprocessedOrdering: Ordering[T]) : BlackboardState[T] = {
    new BlackboardState[T](State.fresh[T](sig)(unprocessedOrdering))
  }
}
