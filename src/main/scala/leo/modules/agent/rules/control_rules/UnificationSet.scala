package leo.modules.agent.rules.control_rules

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}
import leo.modules.SZSException
import leo.modules.output.SZS_Error

import scala.collection.mutable

case object Unify extends DataType[AnnotatedClause]{
  override def convert(d: Any): AnnotatedClause = d match {
    case c : AnnotatedClause => c
    case _ => throw new SZSException(SZS_Error, s"Expected AnnotatedClause, but got $d")
  }
}

/**
  * Stores Formulas that are potentially to unify with
  * the algorithm execution in [[leo.modules.control.Control]]
  */
class UnificationSet extends DataStore{

  private final val set : mutable.Set[AnnotatedClause] = mutable.HashSet[AnnotatedClause]()

  /**
    * Gets the set of unprocessed clauses.
    * The returned set is immutable.
    *
    * @return Set of unprocessed clauses
    */
  def get : Set[AnnotatedClause] = synchronized(set.toSet)


  override def isEmpty: Boolean = synchronized(set.isEmpty)

  /**
    * This method returns all Types stored by this data structure.
    *
    * @return all stored types
    */
  override val storedTypes: Seq[DataType[Any]] = Seq(Unify)

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  override def updateResult(r: Delta): Delta = synchronized {
    val ins1 = r.inserts(Unify)
    val del1 = r.removes(Unify)
    val (del2, ins2) = r.updates(Unify).unzip

    val ins = (ins1 ++ ins2).iterator
    val del = (del1 ++ del2).iterator

    val delta = Result()

    while(del.hasNext){
      val c = del.next
      if(set.remove(c)) delta.remove(Unify)(c)
    }

    while(ins.hasNext) {
      val c = ins.next
      if(set.add(c)) delta.insert(Unify)(c)
    }
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
    case Unify => synchronized(set.toSet.asInstanceOf[Set[T]])
    case _ => Set()

  }
}
