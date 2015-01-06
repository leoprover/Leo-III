package leo.datastructures

/**
 * Created by lex on 16.10.14.
 */
abstract class Position(protected val seq: Seq[Int]) extends Pretty {
  import leo.datastructures.Position.DerivedPos

  def posHead: Int = seq.head
  def tail: Position = DerivedPos(seq.tail)

  def abstrPos: Position = new DerivedPos(seq :+ 1)
  def headPos: Position = new DerivedPos(seq :+ 0)
  def argPos(i: Int): Position = new DerivedPos(seq :+ i)


  def prependAbstrPos: Position = new DerivedPos(1 +: seq)
  def prependHeadPos: Position = new DerivedPos(0 +: seq)
  def preprendArgPos(i: Int): Position = new DerivedPos(i +: seq)

  def pretty = if (seq.isEmpty)
                "ε"
               else
                seq.mkString(",")
}


object Position {
  def root: Position = RootPos

  protected case class DerivedPos(pos: Seq[Int]) extends Position(pos)
  protected case object RootPos extends Position(Seq.empty)

  object AbstrPos {
    def unapply(pos: Position): Boolean = {
      pos.seq.nonEmpty && pos.seq.head == 1
    }
  }

  object HeadPos {
    def unapply(pos: Position): Boolean = {
      pos.seq.nonEmpty && pos.seq.head == 0
    }
  }

  object ArgsPos {
    def unapply(pos: Position): Boolean = {
      pos.seq.nonEmpty
    }
  }
}