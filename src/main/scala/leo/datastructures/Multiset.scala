package leo.datastructures

import scala.collection.GenTraversable

/**
  * Multisets.
  *
  * @tparam A The type of the elements in the multiset
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
trait Multiset[A]
  extends Iterable[A] {

  /** Returns an iterator over the distinct elements of this multiset. */
  def distinctIterator: Iterator[A]

  /** Returns true iff `elem` is contained within the multiset. */
  def contains(elem: A): Boolean

  /** Returns a new multiset that contains all elements of `this` plus `elem`, */
  def +(elem: A): Multiset[A] = this.+(elem,1)
  /** Returns a new multiset that contains all elements of `this` plus `count` times `elem`, */
  def +(elem: A, count: Int): Multiset[A]

  /** Returns a new multiset that contains all elements of `this` without `elem`, */
  def -(elem: A): Multiset[A]

  /** The number of occurrences of `elem`. */
  def multiplicity(elem: A): Int

  /** Returns a set of each distinct element. */
  def distinct: Set[A]
  /** The size of the multiset when regarded as a set. */
  def distinctSize: Int

  /** Returns a new multiset that is the intersection of `this` and `that`. */
  def intersect(that: Multiset[A]): Multiset[A]
  /** Returns a new multiset that is the sum of `this` and `that`. */
  def sum(that: Multiset[A]): Multiset[A]
  /** Returns a new multiset that is the union of `this` and `that`. Note that
    * this is NOT the sum (i.e. it contains the maximal number of element occurences on both
    * multisets, not the sum of both). */
  def union(that: Multiset[A]): Multiset[A]
  /** Returns true off `this` is a subset of `that`. */
  def subset(that:  Multiset[A]): Boolean = {
    distinctIterator.forall(elem =>
      multiplicity(elem) <= that.multiplicity(elem)
    )
  }

  override def equals(that: Any): Boolean = that match {
    case thatSet: Multiset[_] =>
      (this eq thatSet) || ((this.size == thatSet.size) && (try
        this subset thatSet.asInstanceOf[Multiset[A]]
     catch {case _: ClassCastException => false} ))
    case _ => false
  }

  override def toString: String = {
    s"{${distinct.map(e => multiplicityToString(e, multiplicity(e))).mkString(";")}}"
  }
  @inline final private def multiplicityToString(elem: A, mult: Int): String = {
    var idx = mult
    var result = ""
    while (idx > 0) {
      result = result + elem.toString
      idx = idx - 1
      if (idx > 0) result = result + ","
    }
    result
  }
}

object Multiset {
  def empty[A]: Multiset[A] = impl.MultisetImpl.empty
  def apply[A](elem: A, elems: A*): Multiset[A] = impl.MultisetImpl.apply(elem, elems:_*)
  def apply[A](coll: GenTraversable[A]): Multiset[A] = impl.MultisetImpl.apply(coll)

  def fromMap[A](map: Map[A, Int]) = impl.MultisetImpl.fromMap(map)
}