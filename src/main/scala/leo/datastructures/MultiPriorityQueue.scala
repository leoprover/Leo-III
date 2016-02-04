package leo.datastructures

/**
  * Created by lex on 1/4/16.
  */
trait MultiPriorityQueue[A] {
  type CMP_Fun = A => A => CMP_Result

  def addQueue(cmp: CMP_Fun, gain: Int): Unit

  def +(elem: A): Unit
  def +(elem1: A, elem2: A, elems: A*): Unit
  def ++(elems: Set[A]): Unit

  def isEmpty: Boolean
  def size: Int

  def pop(): A
}

object MultiPriorityQueue {
  def apply[A]: MultiPriorityQueue[A] = ???
}

protected[datastructures] class MPQImpl[A] extends MultiPriorityQueue[A] {
  private[this] var queues: Map[CMP_Fun, Int] = Map()
  private[this] var elements: Set[A] = Set()

  def addQueue(cmp: CMP_Fun, gain: Int): Unit = {
    queues = queues + (cmp -> gain)
  }

  def +(elem: A): Unit = {
    elements = elements + elem
  }
  def +(elem1: A, elem2: A, elems: A*): Unit = {
    elements = ((elements + elem1) + elem2) ++ elems
  }
  def ++(elems: Set[A]): Unit = {
    elements = elements ++ elems
  }

  def isEmpty: Boolean = elements.isEmpty
  def size: Int = elements.size

  def pop(): A = {
    assert(queues.nonEmpty, throw new IllegalArgumentException("No queue inserted yet"))

    ???
  }

}