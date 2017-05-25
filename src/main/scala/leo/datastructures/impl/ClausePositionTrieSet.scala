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


protected[impl] class ClausePositionTrieSetNode { self =>
  import scala.collection.mutable
  type Key = Int

  protected final val subtries: mutable.HashMap[Key, ClausePositionTrieSetNode] = mutable.HashMap.empty

  private var leftValue: ClausePosition = _
  private var rightValue: ClausePosition = _

  def insert(side: Literal.Side, termPos: Position, clPos: ClausePosition): Unit = {
    if (termPos == Position.root) {
      if (side == Literal.leftSide) leftValue = clPos
      else rightValue = clPos
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
      if (side == Literal.leftSide) leftValue = null
      else rightValue = null
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
      if (side == Literal.leftSide) leftValue != null
      else rightValue != null
    } else {
      val hd = termPos.posHead
      val tail = termPos.tail
      if (subtries.contains(hd)) {
        subtries(hd).contains(side, tail)
      } else false
    }
  }

  def isEmpty: Boolean = subtries.isEmpty && (leftValue != null || rightValue != null)

  def bfsIterator: Iterator[ClausePosition] = new Iterator[ClausePosition] {
    private final val queue: mutable.Queue[ClausePositionTrieSetNode] = mutable.Queue(self)
    private var leftDone: Boolean = false
    private var rightDone: Boolean = false

    override def hasNext: Boolean = {
      if (queue.isEmpty) false
      else {
        val hd = queue.front
        if (hd.leftValue != null && !leftDone) true
        else if (hd.rightValue != null && !rightDone) true
        else {
          queue.dequeue()
          queue ++= hd.subtries.values.toSet
          hasNext
        }
      }
    }

    override def next(): ClausePosition = {
      if (hasNext) {
        val hd = queue.front
        assert(hd.leftValue != null || hd.rightValue != null)
        if (!leftDone) {
          leftDone = true
          hd.leftValue
        } else if (!rightDone) {
          rightDone = true
          hd.rightValue
        } else {
          // both done, should not happen
          assert(false)
          throw new IllegalStateException
        }
      } else throw new NoSuchElementException
    }
  }
}
