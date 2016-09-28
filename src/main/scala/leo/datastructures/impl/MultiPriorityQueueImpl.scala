package leo.datastructures.impl

import java.util.NoSuchElementException
import leo.datastructures.MultiPriorityQueue
import scala.collection.mutable

/**
  * Created by lex on 13.06.16.
  */
class MultiPriorityQueueImpl[A] extends MultiPriorityQueue[A] {
  private final class ObjectProxy(x: A) {
    private var elem: A = x
    def get: A = elem
    def clear(): Unit = {elem = null.asInstanceOf[A]}
    override def equals(o: Any) = o match {
      case x:ObjectProxy => x.elem == elem
      case _ => false
    }
    override val hashCode: Int = elem.hashCode()
  }
  private final def toProxyOrdering(ord: Ordering[A]): Ordering[ObjectProxy] = {
    new Ordering[ObjectProxy] {
      @inline final def compare(x: ObjectProxy, y: ObjectProxy): OrderingKey =
        if (x.get == null) -1
        else if (y.get == null) -1
        else ord.compare(x.get, y.get)
    }
  }

  private var priorityQueues: Seq[mutable.PriorityQueue[ObjectProxy]] = Vector()
  private var initialized = false

  def insert(x: A): Unit = {
    if (!initialized) return
    val queues = priorityQueues.size
    var i = 0
    val op = new ObjectProxy(x)
    while (i < queues) {
      priorityQueues(i).enqueue(op)
      i = i+1
    }
  }

  def addPriority(p: Ordering[A]): OrderingKey = {
    val newPrioQueue = mutable.PriorityQueue.empty(toProxyOrdering(p))
    val key = priorityQueues.size
    if (!initialized) initialized = true
    else newPrioQueue ++= priorityQueues.head
    priorityQueues = priorityQueues :+ newPrioQueue
    key
  }
  def priorities = priorityQueues.size

  def dequeue(k: OrderingKey): A = {
    if (priorityQueues.size-1 < k) throw new NoSuchElementException
    else {
      val result = priorityQueues(k).dequeue()
      if (result.get == null) dequeue(k)
      else {
        val resultElement = result.get
        result.clear()

        resultElement
      }
    }
  }

  def size: Int = {
    if (!initialized) throw new IllegalStateException
    else {
      priorityQueues.head.size
    }
  }

  def isEmpty: Boolean = size == 0

  def head(k: OrderingKey): A = {
    if (priorityQueues.size-1 < k) throw new NoSuchElementException
    else {
      val result = priorityQueues(k).head
      if (result.get == null) {
        priorityQueues(k).dequeue()
        head(k)
      } else {
        result.get
      }
    }
  }
}
