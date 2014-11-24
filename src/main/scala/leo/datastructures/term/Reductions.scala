package leo.datastructures.term

/**
 * Created by lex on 04.09.14.
 */
object Reductions extends Function0[Long] {
  var r : Long = 0
  def reset(): Unit = {
    r = 0
  }

  def apply(): Long = r

  def tick(): Unit = {
    r += 1
  }
}
