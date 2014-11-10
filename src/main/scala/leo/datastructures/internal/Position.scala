package leo.datastructures.internal

/**
 * Created by lex on 16.10.14.
 */
class Position {
  val seq: List[Int] = List()

  def abstrPos: Position = ???
  def headPos: Position = ???
  def spinePos: Position = ???
  def argPos(i: Int): Position = ???


  def prependAbstrPos: Position = ???
  def prependHeadPos: Position = ???
  def prependSpinePos: Position = ???
  def preprendArgPos(i: Int): Position = ???
}


object Position {
  def root: Position = ???
}