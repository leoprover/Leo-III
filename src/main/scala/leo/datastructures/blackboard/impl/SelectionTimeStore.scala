package leo
package datastructures.blackboard
package impl

import leo.datastructures.TimeStamp

import scala.collection.mutable

/**
 * This data store saves the time a clause
 * was selected from active and moved to passive.
 */
object SelectionTimeStore extends DataStore {

  private val sts : mutable.Map[FormulaStore, TimeStamp] = new mutable.HashMap[FormulaStore, TimeStamp]()

  /**
   * Returns the timestamp of the selection of the clause
   *
   * @param f - any FormulaStore
   * @return None, if the clause was not yet selected (still in active), Some(t) if `f` was moved from active to passiv on time `t`
   */
  def get(f : FormulaStore) : Option[TimeStamp] = sts.get(f)

  //==========================================
  //      Blackboard Controlling
  //==========================================
  override def storedTypes: Seq[DataType] = List(FormulaType, SelectionTimeType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (fo : FormulaStore, fn : FormulaStore) =>
      sts.remove(fo).map{t => sts.put(fn,t)}
      false // We do not check for new data
    case ((TimeData(fo,to)), (TimeData(fn,tn))) =>
      sts.remove(fo)
      sts.put(fn,tn)
      true
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case TimeData(f,t) =>
      if(sts.get(f).fold(true)(_ != t)) {
        sts.put(f,t)
        true
      } else
        false
    case _ => false
  }
  override def clear(): Unit = sts.clear()
  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case SelectionTimeType => sts.toSet
    case FormulaType => sts.keys.toSet
    case _ => Set.empty
  }

  override def delete(d: Any): Unit = d match {
    case (f : FormulaStore) => sts.remove(f)
    case (TimeData(f,t)) => sts.get(f).map{t1 => if(t == t1) sts.remove(f)}
  }
}

case object SelectionTimeType extends DataType {}

/**
 * A time attribute for a formula store
 *
 * @param f the formula
 * @param t the time
 */
case class TimeData(f : FormulaStore, t : TimeStamp)
