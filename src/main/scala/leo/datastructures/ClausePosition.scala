package leo.datastructures



case class ClausePosition(cl: Clause, litIdx: Int,
                          side: Literal.Side, pos: Position)
  extends Pretty {
  final def pretty = s"(lit($litIdx);side(${sidePretty(side)});pos(${pos.pretty}))"

  @inline final private def sidePretty(side: Literal.Side): String =
    if (side == Literal.leftSide) "L" else "R"
}

/**
  * Created by lex on 5/24/17.
  */
trait ClausePositionSet {
  def clause: Clause

  def insert(pos: ClausePosition): Unit
  def remove(pos: ClausePosition): Unit

  def contains(pos: ClausePosition): Boolean

  def isEmpty: Boolean
  def nonEmpty: Boolean

  def bfsIterator: Iterator[ClausePosition]
}

object ClausePositionSet {
  def empty: ClausePositionSet = new impl.ClausePositionTrieSet()
  def apply(elem: ClausePosition, elems: ClausePosition*): ClausePositionSet = {
    val set = empty
    set.insert(elem)
    elems.foreach(set.insert)
    set
  }
}
