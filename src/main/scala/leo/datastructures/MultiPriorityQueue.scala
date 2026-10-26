package leo.datastructures

/**
  * A multi-priority priority queue. Essentially a priority queue which operations
  * are indexed by the priority that is to be assumed to that operation.
  *
  * @tparam A
  */
trait MultiPriorityQueue[A] extends Pretty {
  type OrderingKey = Int

  def insert(x: A): Unit
  def insert(xs: Iterable[A]): Unit = {
    xs.foreach(insert)
  }
  def remove(x: A): Unit
  def remove(xs: Iterable[A]): Unit = {
    xs.foreach(remove)
  }
  def addPriority(p: Ordering[A]): OrderingKey
  def priorityCount: Int
  def priority(key: OrderingKey): Ordering[A]
  def isEmpty: Boolean
  def size: Int
  def head(k: OrderingKey): A
  def dequeue(k: OrderingKey): A
  def toSet: Set[A]
}


object MultiPriorityQueue {
  def empty[A]: MultiPriorityQueue[A] = new leo.datastructures.impl.MultiPriorityQueueImpl[A]()
}