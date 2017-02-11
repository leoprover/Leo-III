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
    override def hashCode: Int = if (elem == null) 0 else elem.hashCode()
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
  private var deletedObjects: Set[A] = Set()

  def insert(x: A): Unit = {
    if (!initialized) return
    if (deletedObjects.contains(x))
      deletedObjects = deletedObjects - x
    else {
      val queues = priorityQueues.size
      var i = 0
      val op = new ObjectProxy(x)
      while (i < queues) {
        priorityQueues(i).enqueue(op)
        i = i+1
      }
    }
  }

  def remove(x: A): Unit = {
    if (!initialized) return
    deletedObjects = deletedObjects + x
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
        if (deletedObjects.contains(resultElement)) {
          deletedObjects = deletedObjects - resultElement
          dequeue(k)
        }
        else resultElement
      }
    }
  }

  def size: Int = {
    if (!initialized) throw new IllegalStateException
    else {
      priorityQueues.head.size // FIXME: ALso possibily counts the removed ones
    }
  }

  def isEmpty: Boolean = {
    if (!initialized) throw new IllegalStateException
    else {
      val pq = priorityQueues.head
      if (pq.isEmpty) true
      else {
        val result = pq.head
        if (result.get == null) {
          priorityQueues.head.dequeue()
          isEmpty
        } else {
          val elem = result.get
          if (deletedObjects.contains(elem)) {
            priorityQueues.head.dequeue()
            isEmpty
          } else false
        }
      }

    }
  }

  def head(k: OrderingKey): A = {
    if (priorityQueues.size-1 < k) throw new NoSuchElementException
    else {
      val result = priorityQueues(k).head
      if (result.get == null) {
        priorityQueues(k).dequeue()
        head(k)
      } else {
        val elem = result.get
        if (deletedObjects.contains(elem)) {
          priorityQueues(k).dequeue()
          head(k)
        } else elem
      }
    }
  }

  def toSet: Set[A] = if (priorityQueues.isEmpty) Set() else priorityQueues.head.view.filterNot(elem => elem.get == null || deletedObjects.contains(elem.get)).map(_.get).toSet
}
