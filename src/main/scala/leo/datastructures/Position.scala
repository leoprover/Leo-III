package leo.datastructures

/**
  * Positions represent paths to subterms with respect
  * to a given term (the super term). Positions `p` are
  * {{{p ∈ ε ∪ ℤ*}}}
  * where `ε` denotes the empty position (also called root position)
  * and elements from `ℤ*` denote a sequence of integers.
  * Positions are defined by
  *   -  `t|ε = t`
  *   -  `(f arg1 ... argn|0) = f`
  *   -  `(f arg1 ... argn|i) = argi`
  *   -  `λx. s|-1 = s`
  *
  * @param seq The sequence of integers; the empty sequence represents `ε`
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
case class Position(val seq: Vector[Int]) extends AnyVal {

  def posHead: Int = seq.head
  def tail: Position = {
    val t = seq.tail
    if (t.isEmpty) Position.root
    else Position(t)
  }

  def abstrPos: Position = Position(seq :+ -1)
  def headPos: Position = Position(seq :+ 0)
  def argPos(i: Int): Position = Position(seq :+ i)

  def prependAbstrPos: Position = Position(-1 +: seq)
  def prependHeadPos: Position = Position(0 +: seq)
  def preprendArgPos(i: Int): Position = Position(i +: seq)

  def abstractionCount: Int = seq.count(_ == -1)

  def pretty: String = if (seq.isEmpty) "ε" else seq.mkString(",")
}


object Position {
  final val root: Position = Position(Vector.empty)

  object AbstrPos {
    final def unapply(pos: Position): Boolean =
      pos.seq.nonEmpty && pos.seq.head == -1
  }

  object HeadPos {
    final def unapply(pos: Position): Boolean =
      pos.seq.nonEmpty && pos.seq.head == 0
  }

  object ArgsPos {
   final def unapply(pos: Position): Boolean =
      pos.seq.nonEmpty
  }
}
