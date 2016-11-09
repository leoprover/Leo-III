package leo.datastructures.impl


import leo.datastructures.Multiset

import scala.collection.GenTraversable

/**
  * Created by lex on 11/9/16.
  */
protected[impl] class MultisetImpl[A](contents: Map[A, Int]) extends Multiset[A] {

  override def distinctIterator: Iterator[A] = contents.keysIterator

  override def contains(elem: A): Boolean = contents.contains(elem)

  override def +(elem: A, count: Int): Multiset[A] = {
    if (count <= 0) throw new IllegalArgumentException
    if (contains(elem)) {
      new MultisetImpl[A](contents.+((elem, contents(elem)+1)))
    } else {
      new MultisetImpl[A](contents.+((elem, 1)))
    }
  }

  override def -(elem: A): Multiset[A] = {
    if (!contains(elem)) this
    else {
      val newCount = contents(elem) - 1
      if (newCount == 0) {
        new MultisetImpl[A](contents.-(elem))
      } else {
        new MultisetImpl[A](contents.+((elem, newCount)))
      }
    }
  }
  override def size: Int = contents.values.sum
  def distinctSize: Int = contents.keySet.size

  override def multiplicity(elem: A): Int = {
    contents.applyOrElse(elem, {_:A => 0})
  }

  override def distinct: Set[A] = contents.keySet

  override def intersect(that: Multiset[A]): Multiset[A] = {
    var newContents: Map[A, Int] = Map()
    val thatIt = that.distinctIterator
    while (thatIt.hasNext) {
      val thatElem = thatIt.next()
      if (contents.contains(thatElem)) {
        newContents = newContents + ((thatElem, contents(thatElem).min(that.multiplicity(thatElem))))
      }
    }
    new MultisetImpl(newContents)
  }

  override def union(that: Multiset[A]): Multiset[A] = {
    var newContents = contents
    val thatIt = that.distinctIterator
    while (thatIt.hasNext) {
      val thatElem = thatIt.next()
      newContents = newContents + ((thatElem, contents(thatElem).max(that.multiplicity(thatElem))))
    }
    new MultisetImpl(newContents)
  }

  override def sum(that: Multiset[A]): Multiset[A] = {
    var newContents = contents
    val thatIt = that.distinctIterator
    while (thatIt.hasNext) {
      val thatElem = thatIt.next()
      newContents = newContents + ((thatElem, that.multiplicity(thatElem) + multiplicity(thatElem)))
    }
    new MultisetImpl(newContents)
  }

  override def iterator: Iterator[A] = {
    new Iterator[A] {
      val mapIterator = contents.keysIterator
      var curMult: Int = -1
      var curElem: A = _

      override def hasNext: Boolean = {
        if (curMult <= 0) {
          // empty, possibly next element or not initialized
          if (mapIterator.hasNext) {
            curElem = mapIterator.next()
            curMult = contents(curElem)
            true
          } else false
        } else true // next element available
      }

      override def next(): A = {
        curMult = curMult - 1
        curElem
      }
    }
  }
}

object MultisetImpl {
  def empty[A]: MultisetImpl[A] = new MultisetImpl(Map.empty)
  def apply[A](elem: A, elems: A*): Multiset[A] = {
    var contents: Map[A, Int] = Map((elem, 1))
    var elemIt = elems.iterator
    while (elemIt.hasNext) {
      var e = elemIt.next()
      if (contents.contains(e)) {
        contents = contents + ((e, contents(e)+1))
      } else {
        contents = contents + ((e, 1))
      }
    }
    new MultisetImpl(contents)
  }
  def apply[A](coll: GenTraversable[A]): Multiset[A] = {
    var contents: Map[A, Int] = Map()
    var curColl = coll
    while (curColl.nonEmpty) {
      var e = curColl.head
      if (contents.contains(e)) {
        contents = contents + ((e, contents(e)+1))
      } else {
        contents = contents + ((e, 1))
      }
      curColl = curColl.tail
    }
    new MultisetImpl(contents)
  }
  def fromMap[A](map: Map[A, Int]): MultisetImpl[A] = new MultisetImpl(map)
}
