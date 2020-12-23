package leo.datastructures

import scala.annotation.tailrec
import scala.collection.TraversableOnce

/**
  * A multi-priority priority queue. Essentially a priority queue which operations
  * are indexed by the priority that is to be assumed to that operation.
  *
  * @tparam A
  */
trait MultiPriorityQueue[A] extends Pretty {
  type OrderingKey = Int

  def insert(x: A): Unit
  def insert(xs: IterableOnce[A]): Unit = {
    @tailrec def loop(xs: scala.collection.LinearSeq[A]): Unit = {
      if (xs.nonEmpty) {
        this insert xs.head
        loop(xs.tail)
      }
    }
    xs match {
      case xs: scala.collection.LinearSeq[_] => loop(xs)
      case xs                                => xs.iterator.foreach(insert)
    }
  }
  def remove(x: A): Unit
  def remove(xs: IterableOnce[A]): Unit = {
    @tailrec def loop(xs: scala.collection.LinearSeq[A]) : Unit = {
      if (xs.nonEmpty) {
        this remove xs.head
        loop(xs.tail)
      }
    }
    xs match {
      case xs: scala.collection.LinearSeq[_] => loop(xs)
      case xs                                => xs.iterator.foreach(remove)
    }
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