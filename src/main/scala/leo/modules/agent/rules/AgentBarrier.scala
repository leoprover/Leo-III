package leo.modules.agent.rules

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard._

import scala.collection.mutable

case class LockType[A](dType : DataType[A]) extends DataType[A] {
  override def convert(d: Any): A = dType.convert(d)
}

/**
  * This barrier is initialized with a counter.
  *
  * The method barrier.isLocked(d : A) returns
  * true, if counter manipulation operations
  * have been performed on `d`. Otherwise
  * the barrier will return false.
  *
  * Reacts to two scenarios:
  *   Insert(d : A): Sets counter to maximum
  *   Delete(d : A): Sets counter to 0 (it would be free)
  *   Update(d : A): Decreases counter by 1
  *   Insert/Remove(d : Lock[A]): Decreases counter by 1
  */
class AgentBarrier[A](dType : DataType[A], counter : Int) extends DataStore {

  val lockType = new LockType[A](dType) // Instantiation with A


  private val trackRemaining : mutable.Map[A, Int] = mutable.Map[A, Int]()

  override def isEmpty: Boolean = synchronized{
    trackRemaining.isEmpty
  }

  /**
    * Returns true, if the barrier is closed for `d`.
    * False, if barrier is open
    *
    * @param d Data to be checked for locking at the barrier
    * @return True, iff the barrier is closed
    */
  def isLocked(d : A) : Boolean = synchronized{
    trackRemaining.getOrElse(d, 0) > 0
  }

  override val storedTypes: Seq[DataType[Any]] = Seq(dType, lockType)
  override def clear(): Unit = synchronized(trackRemaining.clear())
  override def get[T](t: DataType[T]): Set[T] = synchronized(t match {
    case `lockType` => trackRemaining.keySet.map(x => (dType, x)).asInstanceOf[Set[T]]
    case _ => Set()
  })
  override def updateResult(r: Delta): Delta = synchronized {
    val ins1 = r.inserts(dType).iterator
    val del1 = r.removes(dType).iterator
    val updates = r.updates(dType).iterator

    val locks = (r.inserts(lockType) ++ r.removes(lockType)).iterator

    while(ins1.hasNext){
      val in = ins1.next()
      if(!trackRemaining.contains(in)) {
        trackRemaining.put(in, counter)
      }
    }
    while(del1.hasNext){
      val del = del1.next()
      trackRemaining.remove(del)
    }
    while(updates.hasNext){
      val (oldV, newV) = updates.next()
      val oldC = trackRemaining.remove(oldV).fold(counter)(x => x)
      if(!trackRemaining.contains(newV)) {
        trackRemaining.put(newV, oldC)
      } else {
        val alt = trackRemaining(newV)
        trackRemaining.put(newV, Math.min(alt, oldC))
      }
    }

    var removedLocks : Seq[A] = Seq()
    while(locks.hasNext){
      val d = locks.next()
      if(trackRemaining.contains(d)){
        val newV = trackRemaining.getOrElse(d, counter) - 1
        if(newV > 0) {
          trackRemaining.put(d, newV)
        } else {
          trackRemaining.remove(d)
          removedLocks = d +: removedLocks
        }
      }
    }

    if(removedLocks.nonEmpty) {
      new ImmutableDelta(Map(), Map(lockType -> removedLocks))
    } else {
      EmptyDelta
    }
  }
}
