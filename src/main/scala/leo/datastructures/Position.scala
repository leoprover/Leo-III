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


sealed trait PosString extends Pretty {
  def isBranch: Boolean
  def isEmpty: Boolean

  def head: Int
  def tail: PosString
  def branches: Seq[PosString]

  def *(append: PosString): PosString
}

object PosString extends Function1[Seq[Int],PosString]{
  type RelPos = Int

  case class Branch(branches: Seq[Decend]) extends PosString {
    lazy val pretty: String = branches.map{b => s"(${b.pretty})"}.mkString(" + ")

    final val isBranch = branches.nonEmpty
    final val isEmpty = branches.isEmpty
    final def tail = throw new IllegalArgumentException
    final def head = throw new IllegalArgumentException

    final def *(append: PosString) = throw new IllegalArgumentException
  }
  case class Decend(path: Seq[Int], terminal: Option[Branch]) extends PosString {
    lazy val pretty = s"${path.toString}${terminal.fold("")(_.pretty)}"

    final val isBranch = false
    final val isEmpty = false
    final lazy val tail:PosString = {
      if (path.size == 1) {
        terminal.fold(Branch(Seq()):PosString)(sr => sr:PosString)
      } else {
        Decend(path.tail, terminal)
      }
    }
    final def head = path.head
    final def branches = throw new IllegalArgumentException

    final def *(append: PosString) = if (terminal.isEmpty) { append match {
      case b@Branch(_) => Decend(path, Some(b))
      case Decend(p2, t) => Decend(path ++ p2, t)
    } } else {
      throw new IllegalArgumentException
    }
  }

  final val abs: RelPos = -1
  final val head: RelPos = 0
  final def arg(argno: Int) = argno

  def branch(branch1: => PosString, branch2: => PosString, more: PosString*): PosString = {
    if (branch1.isBranch || branch2.isBranch || more.exists(_.isBranch)) {
      throw new IllegalArgumentException
    } else {
      Branch(branch1.asInstanceOf[Decend] +: branch2.asInstanceOf[Decend] +: more.asInstanceOf[Seq[Decend]])
    }
  }
  def apply(steps: Seq[RelPos]): PosString = Decend(steps, None)
  def apply(step1: RelPos, more: RelPos*): PosString = Decend(step1 +: more, None)
}


