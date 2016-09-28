package leo.datastructures.blackboard.impl

import leo.datastructures.blackboard.{StatusType, DataType, DataStore}
import leo.datastructures.context.Context
import leo.modules.output.StatusSZS

import scala.collection.mutable

/**
 *
 * A tree context set of all SZS stati saved in the blackboard.
 *
 */
object SZSDataStore extends DataStore {


  private val szsSet : mutable.Map[Context, SZSStore] = new mutable.HashMap[Context, SZSStore]()

  /**
   * Forces the SZS Status in a context to a new one.
   * Does not fail, if a status is already set.
   *
   * @param c - The context to update
   * @param s - The status to set
   */
  def forceStatus(c: Context)(s: StatusSZS): Unit = szsSet.synchronized(szsSet.put(c,SZSStore(s,c)))


  def setIfEmpty(c : Context)(s : StatusSZS) : Unit = szsSet.synchronized{
    if(szsSet.get(c).isEmpty){
      szsSet.put(c, SZSStore(s,c))
    }
  }

  /**
   * Returns to a given context the set status.
   * None if no status was previously set.
   *
   * @param c - The searched context
   * @return Some(status) if set, else None.
   */
  def getStatus(c: Context): Option[StatusSZS] = szsSet.synchronized(szsSet.get(c).map(_.szsStatus))


  override def storedTypes: Seq[DataType] = List(StatusType)

  override def update(o: Any, n: Any): Boolean = (o,n) match {
    case (SZSStore(so, co), SZSStore(sn, cn)) =>
      szsSet.remove(co)
      szsSet.put(cn, SZSStore(sn, cn))
      true
    case _ => false
  }

  override def insert(n: Any): Boolean = n match {
    case SZSStore(s, c) => szsSet.put(c, SZSStore(s,c)); true
    case _ => false
  }

  override def clear(): Unit = szsSet.empty

  override protected[blackboard] def all(t: DataType): Set[Any] = t match {
    case StatusType => szsSet.values.toSet
    case _ => Set.empty
  }

  override def delete(d: Any): Unit = d match {
    case SZSStore(s, c) => szsSet.remove(c)
    case _ => ()
  }
}

case class SZSStore (szsStatus : StatusSZS, context : Context) {
  override val toString : String = s"SZSStore(${szsStatus.apply} -> ${context.contextID})"
}

