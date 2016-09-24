package leo

import scala.annotation.tailrec


/**
  * Package object for datastructure-related traits, functions and stuff.
  * @since 01/06/15
  */
package object datastructures {

  @inline final def isPropSet(prop: Int, in: Int): Boolean = (prop & in) == prop
  @inline final def deleteProp(prop: Int, in: Int): Int = prop & ~in

  type CMP_Result = Byte
  final val CMP_EQ: CMP_Result = 0.toByte
  final val CMP_LT: CMP_Result = 1.toByte
  final val CMP_GT: CMP_Result = 2.toByte
  final val CMP_NC: CMP_Result = 3.toByte


  /**
    * Configuration (i.e. state) for NDStream.
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait SearchConfiguration[S] {
    def result: Option[S]
    def isTerminal: Boolean // terminal nodes are not added to the configuration queue
  }

  /**
    * Mutable, non deterministic, stream
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  abstract class NDStream[S /*result type*/ ](val initial: SearchConfiguration[S], val myFun: SearchConfiguration[S] => Iterable[SearchConfiguration[S]] ) extends Iterable[S] with SearchAlgorithm {
    import scala.collection.mutable

    type T = SearchConfiguration[S]
    private val results: mutable.Queue[S] = new mutable.Queue[S]()
    protected var hd: Option[S] = None
    protected val hdFunc: () => Option[S] = () => nextVal
    protected var terminal: Boolean = false
    protected def initDS(): Unit = {
      add(initial)
      hd = hdFunc()
    }

    @tailrec
    protected final def nextVal: Option[S] = {
      val res = results.headOption
      if (res.isDefined) {
        results.dequeue
        res
      } else {
        val conf = get
        if (conf.isEmpty) None
        else {
          val confs: Iterable[SearchConfiguration[S]] = { myFun( conf.get )}
          confs.foreach( x => {
            if (x.result.isDefined)
              results.enqueue( x.result.get )
            if (!x.isTerminal) {
              add(x)
            }
          } )
          nextVal
        }
      }
    }

    // TOFIX: iterator can only be called once right now as the ndstream is mutable!
    var wasCalled = false
    def iterator: Iterator[S] =
      if (!wasCalled) new Iterator[S] {
        wasCalled = true
        def next: S = {
          if (hd.isEmpty && terminal) throw new NoSuchElementException("Stream is empty")
          else {
            if (hd.isEmpty) {hd = hdFunc(); if (hd.isEmpty) {terminal = true;throw new NoSuchElementException("Stream is empty")} }
            val ret = hd.get
            hd = None
            ret
          }
        }
        def hasNext: Boolean = {
          if (hd.isEmpty) {
            if (terminal) false
            else {
              hd = hdFunc()
              if (hd.isEmpty) {
                terminal = true
                false
              } else
                true
            }
          }
          else true
        }
      }
      else throw new UnsupportedOperationException("iterator for NDStream can right now be called only once!")
  }

  /**
    * Trait for search algorithms
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait SearchAlgorithm {
    type T
    protected def initDS(): Unit // called by the algorithm and implemented by some object using it as the object is initialized before the trait
    protected def add( t: T ): Unit
    protected def get: Option[T]
  }

  /**
    * Abstract breadth-first search implementation of a search algorithm.
    *
    * @author Tomer Libal <shaolintl@gmail.com>
    * @since 15/04/2015
    */
  trait BFSAlgorithm extends SearchAlgorithm {
    import collection.mutable

    private val ds: mutable.Queue[T] = new mutable.Queue[T]()
    initDS() // if the object requires the ds to be already existing, then it will not fail now
    protected def add( conf: T ): Unit = ds += conf
    protected def get: Option[T] = {
      val res = ds.headOption
      if (res.isDefined) ds.dequeue
      res
    }
  }


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


  ///////////////////////////////
  // Utility functions
  ///////////////////////////////
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
    resultMap
  }
  @inline final def addMaps[A](map1: Map[A, Int], map2: Map[A, Int]): Map[A, Int] = mergeMapsBy(map1,map2,(a:Int,b:Int) => a+b)(0)
}