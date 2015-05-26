package leo
package datastructures.blackboard
package impl

import leo.datastructures.{TimeStamp}
import leo.datastructures.context.{OrderedContextSet, Context}
import leo.datastructures.context.impl.{TreeOrderedContextSet}

import scala.collection.mutable

/**
 * This data store saves the time a clause
 * was selected from active and moved to passive.
 */
object SelectionTimeStore extends DataStore {

  private val sts : mutable.Map[FormulaStore, TimeStamp] = new mutable.HashMap[FormulaStore, TimeStamp]()
  private val csts : OrderedContextSet[TimeData] = new TreeOrderedContextSet[TimeData]()  // To query for ranges of time
  // private val nsts : mutable.Set[FormulaStore] = new mutable.HashSet[FormulaStore]()


  /**
   * Returns the timestamp of the selection of the clause
   *
   * @param f - any FormulaStore
   * @return None, if the clause was not yet selected (still in active), Some(t) if `f` was moved from active to passiv on time `t`
   */
  def get(f : FormulaStore) : Option[TimeStamp] = sts.get(f)

  /**
   * Returns a set of all formula stores, that were selected
   * before or at the given TimeStamp.
   *
   * @param t - Upper Bound of the TimeStamp
   * @return set of all formulas with a TimeStamp smaller or equal to t.
   */
  def before(t : TimeStamp, c : Context) : Iterable[FormulaStore] = csts.getSmaller(TimeData(null,t), c).map(_.f)


  /**
   * Returns a set of all formula stores, that were selected
   * after or at the given TimeStamp.
   *
   * @param t - Upper Bound of the TimeStamp
   * @return set of all formulas with a TimeStamp bigger or equal to t.
   */
  def after(t : TimeStamp, c : Context) : Iterable[FormulaStore] = csts.getSmaller(TimeData(null,t), c).map(_.f)


  def wasSelected(c: Context): Iterable[FormulaStore] = csts.getAll(c).map(_.f)

  /**
   * Returns a set of all formulas, that have no selection time.
   *
   * @return set of non selected formula stores
   */
  def noSelect(c : Context) : Iterable[FormulaStore] = FormulaDataStore.getFormulas(c).filter{get(_).isEmpty}

  //==========================================
  //      Blackboard Controlling
  //==========================================
  override def storedTypes: Seq[DataType] = List(FormulaType, SelectionTimeType)
  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (fo : FormulaStore, fn : FormulaStore) => synchronized {
      sts.remove(fo).map { t => sts.put(fn, t); csts.remove(TimeData(fo, t), fo.context); csts.add(TimeData(fn, t), fn.context) }
      false // We do not check for new data
    }
    case ((TimeData(fo,to)), (TimeData(fn,tn))) => synchronized {
      sts.remove(fo)
      csts.remove(TimeData(fo,to), fo.context)
      sts.put(fn, tn)
      csts.add(TimeData(fn,tn), fn.context)
      true
    }
    case _ => false
  }
  override def insert(n: Any): Boolean = n match {
    case TimeData(f,t) => synchronized {
      if (sts.get(f).fold(true)(_ != t)) {
        sts.put(f, t)
        csts.add(TimeData(f,t), f.context)
        true
      } else
        false
    }
    case _ => false
  }

  override def clear(): Unit = {
    sts.clear()
    csts.clear()
  }
  override def all(t: DataType): Set[Any] = t match {
    case SelectionTimeType => sts.toSet
    case FormulaType => sts.keys.toSet
    case _ => Set.empty
  }

  override def delete(d: Any): Unit = d match {
    case (f : FormulaStore) => synchronized {
      sts.remove(f).map{t => csts.remove(TimeData(f,t), f.context)}
    }
    case (TimeData(f,t)) => synchronized {
      sts.get(f).map{t1 => if(t == t1) {sts.remove(f); csts.remove(TimeData(f,t), f.context)}}
    }
  }
}

case object SelectionTimeType extends DataType {}

/**
 * A time attribute for a formula store
 *
 * @param f the formula
 * @param t the time
 */
case class TimeData(f : FormulaStore, t : TimeStamp) extends Ordered[TimeData]{
  override def compare(that: TimeData): Int = t.compareTo(that.t)
}
