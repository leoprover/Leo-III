package leo.datastructures.impl

import leo.datastructures.{ClausePosition, ClausePositionSet, Position, Literal, Clause}


protected[datastructures] class ClausePositionTrieSet extends ClausePositionSet {
  import scala.collection.mutable
  type Key = Int

  protected var clause0: Clause = _
  protected final val subtries: mutable.HashMap[Key, ClausePositionTrieSetNode] = mutable.HashMap.empty

  def clause: Clause = clause0

  def insert(pos: ClausePosition): Unit = {
    if (clause0 == null) clause0 = pos.cl

    if (subtries.contains(pos.litIdx)) {
      subtries(pos.litIdx).insert(pos.side, pos.pos, pos)
    } else {
      val entry = new ClausePositionTrieSetNode()
      entry.insert(pos.side, pos.pos, pos)
      subtries += (pos.litIdx -> entry)
    }
  }
  def remove(pos: ClausePosition): Unit = {
    if (subtries.contains(pos.litIdx)) {
      subtries(pos.litIdx).remove(pos.side, pos.pos)
      if (subtries(pos.litIdx).isEmpty) {
        subtries -= pos.litIdx
      }
    }
  }
  def contains(pos: ClausePosition): Boolean = {
    if (subtries.contains(pos.litIdx)) {
      subtries(pos.litIdx).contains(pos.side, pos.pos)
    } else false
  }

  def isEmpty: Boolean = subtries.isEmpty
  def nonEmpty: Boolean = !isEmpty

  def bfsIterator: Iterator[ClausePosition] = new Iterator[ClausePosition] {
    private var todo: Set[ClausePositionTrieSetNode] = subtries.values.toSet
    private var cur: Iterator[ClausePosition] = _

    override def hasNext: Boolean = {
      if (cur == null) {
        if (todo.isEmpty) false
        else {
          cur = todo.head.bfsIterator
          todo = todo.tail
          cur.hasNext
        }
      } else {
        if (cur.hasNext) true
        else {
          cur = null
          hasNext
        }
      }
    }

    override def next(): ClausePosition = {
      if (hasNext) {
        cur.next()
      } else throw new NoSuchElementException
    }
  }
}


protected[impl] class ClausePositionTrieSetNode {
  import scala.collection.mutable
  type Key = Int

  protected[this] final val subtries: mutable.HashMap[Key, ClausePositionTrieSetNode] = mutable.HashMap.empty

  protected[impl] var value: ClausePosition = _
  private var left: Boolean = false
  private var right: Boolean = false

  def insert(side: Literal.Side, termPos: Position, clPos: ClausePosition): Unit = {
    if (termPos == Position.root) {
      value = clPos
      if (side == Literal.leftSide) left = true
      else right = true
    } else {
      val hd = termPos.posHead
      val tail = termPos.tail
      if (subtries.contains(hd)) {
        subtries(hd).insert(side, tail, clPos)
      } else {
        val entry = new ClausePositionTrieSetNode
        entry.insert(side, tail, clPos)
        subtries += (hd -> entry)
      }
    }
  }
  def remove(side: Literal.Side, termPos: Position): Unit = {
    if (termPos == Position.root) {
      if (side == Literal.leftSide) left = false
      else right = false
    } else {
      val hd = termPos.posHead
      val tail = termPos.tail
      if (subtries.contains(hd)) {
        subtries(hd).remove(side, tail)
      }
    }
  }
  def contains(side: Literal.Side, termPos: Position): Boolean = {
    if (termPos == Position.root) {
      if (side == Literal.leftSide) left
      else right
    } else {
      val hd = termPos.posHead
      val tail = termPos.tail
      if (subtries.contains(hd)) {
        subtries(hd).contains(side, tail)
      } else false
    }
  }

  def isEmpty: Boolean = subtries.isEmpty && value == null

  def bfsIterator: Iterator[ClausePosition] = new Iterator[ClausePosition] {
    override def hasNext: Boolean = ???

    override def next(): ClausePosition = ???
  }
}
