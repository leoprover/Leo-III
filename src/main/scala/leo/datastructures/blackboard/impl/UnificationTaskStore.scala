package leo
package datastructures.blackboard
package impl

import scala.collection.mutable

/**
 *
 * This data structure stores clauses
 * that have unification constraints in them, mostly created by the paramodulation.
 *
 */
object UnificationTaskStore  extends DataStore {

  private val uts : mutable.Set[FormulaStore] = new mutable.HashSet[FormulaStore]()

  // TODO Check for same clauses???

  // TODO does anyone need to look at this data???

  override def storedTypes: Seq[DataType] = List(UnificationTaskType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (fo : FormulaStore, fn : FormulaStore) =>
      uts.remove(fo)
      if(uts.contains(fn))
        false
      else {
        uts.add(fn)
        true
      }
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case f : FormulaStore =>
      if(uts.contains(f))
        false
      else {
        uts.add(f)
      }
    case _ => false
  }
  override def clear(): Unit = uts.clear()
  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case UnificationTaskType => uts.toSet
    case _ => Set()
  }
  override def delete(d: Any): Unit = d match {
    case f : FormulaStore => uts.remove(f)
    case _ => ()
  }
}

case object UnificationTaskType extends DataType {}
