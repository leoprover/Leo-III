package leo.datastructures.blackboard.impl

import leo.datastructures.ClauseProxy
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.output.StatusSZS

import scala.collection.mutable

/**
 *
 * A tree context set of all SZS stati saved in the blackboard.
 *
 */
object SZSDataStore extends DataStore {


  private var szs : StatusSZS = null

  /**
   * Forces the SZS Status to a new one.
   * Does not fail, if a status is already set.
   *
   * @param s - The status to set
   */
  def forceStatus(s: StatusSZS): Unit = synchronized(szs = s)


  override def isEmpty: Boolean = szs == null

  def setIfEmpty(s : StatusSZS) : Unit = synchronized{
    if(szs != null)
      szs = s
  }

  /**
   * Returns to a given context the set status.
   * None if no status was previously set.
   *
   * @param c - The searched context
   * @return Some(status) if set, else None.
   */
  def getStatus(c: Context): Option[StatusSZS] = if(szs == null) None else Some(szs)  // TODO convert to "null" representation


  @inline override val storedTypes: Seq[DataType[Any]] = List(StatusType)

  override def updateResult(r: Delta): Delta  = synchronized {
    val ins = r.inserts(StatusType)
    if(ins.nonEmpty & szs == null){
      val value = ins.head
      szs = value
      return Result().insert(StatusType)(szs)
    }
    val ups = r.updates(StatusType)
    if (ups.nonEmpty) {
      val (oldV, newV) = ups.head
      if (oldV != szs || oldV == newV) return EmptyDelta
      szs = newV
      return Result().insert(StatusType)(szs)
    }
    EmptyDelta
  }

  override def clear(): Unit = synchronized(szs)

  override def get[T](t: DataType[T]): Set[T] = t match {
    case StatusType => Set(szs).asInstanceOf[Set[T]]
    case _ => Set.empty
  }
}

case class SZSStore (szsStatus : StatusSZS) {
  override val toString : String = s"SZSStore(${szsStatus()})"
}

