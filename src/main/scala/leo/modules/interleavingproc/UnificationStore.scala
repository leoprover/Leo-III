package leo.modules.interleavingproc

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.{DataStore, DataType, Delta}
import leo.modules.SZSException
import leo.modules.output.SZS_Error

import scala.collection.mutable

/**
  *
  * Stores Clauses with open Unification Constraints.
  *
  * @author Max Wisniewski
  * @since 11/17/16
  */
class UnificationStore[T <: AnnotatedClause] extends DataStore{

  private val openUnifications : mutable.Set[T] = new mutable.HashSet[T]()
  private val openUnificationsID : mutable.Set[Long] = new mutable.HashSet[Long]()

  def getOpenUni : Seq[T] = synchronized(openUnifications.toSeq)

  def containsUni(c : T) = synchronized(openUnificationsID.contains(c.id))

  def openUni : Boolean = synchronized(openUnifications.nonEmpty)

  /**
    * This method returns all Types stored by this data structure.
    *
    * @return all stored types
    */
  override val storedTypes: Seq[DataType[Any]] = Seq(OpenUnification)

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  override def updateResult(r: Delta): Boolean = synchronized {
    val itr = r.removes(OpenUnification).iterator
    while(itr.hasNext){
      val r = itr.next().asInstanceOf[T]
      if(openUnificationsID.contains(r.id)){
        openUnificationsID.remove(r.id)
        openUnifications.remove(r)
      }
    }
    val iti = r.inserts(OpenUnification).iterator
    while(iti.hasNext){
      val i = iti.next().asInstanceOf[T]
      if(!openUnifications.contains(i)){
//      if(!openUnifications.exists(x => x.cl == i.cl)) {
        openUnifications.add(i)
        openUnificationsID.add(i.id)
      }
    }
    iti.nonEmpty    // TODO Check in loop for already existence
  }

  /**
    * Removes everything from the data structure.
    * After this call the ds should behave as if it was newly created.
    */
  override def clear(): Unit = synchronized(openUnifications.clear())

  /**
    * Returns a list of all stored data.
    *
    * @param t
    * @return
    */
  override def all[T](t: DataType[T]): Set[T] = if(t == OpenUnification) synchronized(openUnifications.toSet.asInstanceOf[Set[T]]) else Set.empty
}

case object OpenUnification extends DataType [AnnotatedClause] {
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}
