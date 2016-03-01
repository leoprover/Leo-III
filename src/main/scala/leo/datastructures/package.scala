package leo

/**
 * Created by lex on 06.01.15.
 */
package object datastructures {

  @inline final def isPropSet(prop: Int, in: Int): Boolean = (prop & in) == prop

  type CMP_Result = Byte
  final val CMP_EQ: CMP_Result = 0.toByte
  final val CMP_LT: CMP_Result = 1.toByte
  final val CMP_GT: CMP_Result = 2.toByte
  final val CMP_NC: CMP_Result = 3.toByte

  final def fuseMaps[A,B](map1: Map[A,Set[B]], map2: Map[A,Set[B]]): Map[A, Set[B]] = {
    map2.foldLeft(map1)({case (intermediateMap, (k,v)) =>
      if (!intermediateMap.contains(k))
        intermediateMap + (k -> v)
      else
        intermediateMap + (k -> (intermediateMap(k) ++ v))
    })
  }
  final def mergeMapsBy[A, B](map1: Map[A, B], map2: Map[A, B], f: (B,B) => B)(defaultValue: B): Map[A, B] = {
    var resultMap = map2
    val map1It = map1.iterator
    while (map1It.hasNext) {
      val (entry, value) = map1It.next()
      resultMap = resultMap + (entry -> f(resultMap.getOrElse(entry,defaultValue), value))
    }
    map2
  }
  @inline final def addMaps[A](map1: Map[A, Int], map2: Map[A, Int]): Map[A, Int] = mergeMapsBy(map1,map2,(a:Int,b:Int) => a+b)(0)

  /** Class for objects that have a congruence defined on them (that is probably different from equality). */
  trait HasCongruence[A] {
    /** Returns `true` iff `this` is congruent to `that`. */
    def cong(that: A): Boolean
  }

  trait ZippingSeqIterator[A] {
    def hasNext: Boolean
    def hasPrev: Boolean
    def next(): A
    def prev(): A
    def leftOf: Seq[A]
    def rightOf: Seq[A]
  }
  class SeqZippingSeqIterator[A](seq: Seq[A]) extends ZippingSeqIterator[A] {
    var l: Seq[A] = Seq()
    var r: Seq[A] = seq
    var cur: A = null.asInstanceOf[A]

    def hasPrev = l.nonEmpty
    def hasNext = r.nonEmpty
    def next() = {
      if (!hasNext) throw new NoSuchElementException("next on empty right side of zipper")
      val elem = r.head
      if (cur != null) {
        l = cur +: l
      }
      r = r.tail
      cur = elem
      elem
    }
    def prev() = {
      if (!hasPrev) throw new NoSuchElementException("prev on empty left side of zipper")
      val elem = l.head
      r = cur +: r
      l = l.tail
      cur = elem
      elem
    }
    def leftOf = l.reverse
    def rightOf = r
  }
}
