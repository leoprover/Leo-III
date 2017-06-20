package leo.modules.agent.rules.control_rules

import java.util.concurrent.atomic.AtomicLong

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}
import leo.modules.{GeneralState, SZSException}
import leo.modules.control.Control
import leo.modules.output.SZS_Error

import scala.collection.mutable

//case object Processed extends DataType[AnnotatedClause]{
//  override def convert(d: Any): AnnotatedClause = d match {
//    case c : (AnnotatedClause) => c
//    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
//  }
//}

/**
  * Stores the processed Formulas for
  * the algorithm execution in [[leo.modules.control.Control]]
  */
class ProcessedSet(processedType : DataType[AnnotatedClause])(implicit state : Control.LocalFVState)  extends DataStore{

  implicit val sig : Signature = state.signature
  private final val set : mutable.Set[AnnotatedClause] = mutable.HashSet[AnnotatedClause]()
  private final val idMap : mutable.Map[Long, Long] = mutable.Map[Long, Long]()
  private final val nextID : AtomicLong = new AtomicLong(-1)

  /**
    * Gets the set of unprocessed clauses.
    * The returned set is immutable.
    *
    * @return Set of unprocessed clauses
    */
  def get : Set[AnnotatedClause] = synchronized(set.toSet)

  private var maxIDv : Long = -1

  def maxID : Long = synchronized(maxIDv)

  def getID(c : AnnotatedClause) : Long = synchronized(idMap.getOrElse(c.id, -1))

  override def isEmpty: Boolean = synchronized(set.isEmpty)

  /**
    * This method returns all Types stored by this data structure.
    *
    * @return all stored types
    */
  override val storedTypes: Seq[DataType[Any]] = Seq(processedType)

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  override def updateResult(r: Delta) : Delta = synchronized {
    val delta = Result()
    val ins1 = r.inserts(processedType)
    val del1 = r.removes(processedType)
    val (del2, ins2) = r.updates(processedType).unzip

    val ins = (ins1 ++ ins2).iterator
    val del = (del1 ++ del2).iterator

    while(del.hasNext){
      val c = del.next
      if(set.remove(c)) {
        delta.remove(processedType)(c)
        idMap.remove(c.id)
        Control.removeFromIndex(c)
      }
      else leo.Out.finest(s"% ${c.pretty} was not in the processed set.")
    }

    while(ins.hasNext) {
      val c = ins.next
      if(set.add(c)) {
        delta.insert(processedType)(c)
        val newID = nextID.incrementAndGet()
        idMap.put(c.id, newID)
        maxIDv = newID
        Control.insertIndexed(c)
      }
      else leo.Out.finest(s"% ${c.pretty} was already in the processed set.")
    }
    leo.Out.debug(s"Processed after update:\n  ${set.map(c => s"(${idMap(c.id)})" ++ c.pretty(state.signature)).mkString("\n  ")}")

    delta
  }

  /**
    * Removes everything from the data structure.
    * After this call the ds should behave as if it was newly created.
    */
  override def clear(): Unit = synchronized(set.clear())

  /**
    * Returns a list of all stored data.
    *
    * @param t
    * @return
    */
  override def get[T](t: DataType[T]): Set[T] = t match{
    case `processedType` => synchronized(set.toSet.asInstanceOf[Set[T]])
    case _ => Set()

  }
}
