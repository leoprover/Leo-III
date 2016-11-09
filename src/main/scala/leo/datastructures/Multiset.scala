package leo.datastructures

/**
  * Created by lex on 11/9/16.
  */
trait Multiset[A]
  extends Iterable[A] {

  def distinctIterator: Iterator[A]

  def contains(elem: A): Boolean

  def +(elem: A): Multiset[A] = this.+(elem,1)
  def +(elem: A, count: Int): Multiset[A]

  def -(elem: A): Multiset[A]

  def multiplicity(elem: A): Int

  def distinct: Set[A]
  def distinctSize: Int

  def intersect(that: Multiset[A]): Multiset[A]
  def sum(that: Multiset[A]): Multiset[A]
  def union(that: Multiset[A]): Multiset[A]

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
}