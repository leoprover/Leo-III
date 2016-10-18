package leo.datastructures

import scala.annotation.tailrec
import scala.collection.TraversableOnce

/**
  * A multi-priority priority queue. Essentially a priority queue which operations
  * are indexed by the priority that is to be assumed to that operation.
  *
  * @tparam A
  */
trait MultiPriorityQueue[A] {
  type OrderingKey = Int

  def insert(x: A): Unit
  def insert(xs: TraversableOnce[A]): Unit = {
    @tailrec def loop(xs: scala.collection.LinearSeq[A]) {
      if (xs.nonEmpty) {
        this insert xs.head
        loop(xs.tail)
      }
    }
    xs match {
      case xs: scala.collection.LinearSeq[_] => loop(xs)
      case xs                                => xs foreach insert
    }
  }
  def addPriority(p: Ordering[A]): OrderingKey
  def priorities: Int
  def isEmpty: Boolean
  def size: Int
  def head(k: OrderingKey): A
  def dequeue(k: OrderingKey): A
  def toSet: Set[A]
}


object MultiPriorityQueue {
  def empty[A]: MultiPriorityQueue[A] = new leo.datastructures.impl.MultiPriorityQueueImpl[A]()
}