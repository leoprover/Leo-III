package leo

import leo.datastructures.impl.Signature

/**
 * Collection of functions and values for testing related purposes.
 *
 * @author Alexander Steen
 * @since 04.03.15
 */
trait TestUtility {
  /** Measures the time it takes to calculate the argument.
    * Returns a tuple (t, res) where `t` is the time that was needed to calculate result `res`.
    */
  def time[A](a: => A): (Long, A) = {
    val now = System.nanoTime
    val result = a
    ((System.nanoTime - now) / 1000, result)
  }

  def getFreshSignature: Signature = {
    val sig = Signature.get
    Signature.resetWithHOL(sig)
    sig
  }

  val hLine: String = "#" * 30
  def printHLine(): Unit = println(hLine)
  def printLongHLine(): Unit = println(hLine * 3)

  def printHeading(title: String): Unit = {
    printHLine()
    println("## " + title)
    printHLine()
  }
}

object TestUtility extends TestUtility