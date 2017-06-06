package leo.modules.agent.rules

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}

import scala.collection.mutable

/**
  * Stores the data of a certain data Type
  */
class TypedSet[A](dt : DataType[A]) extends DataStore {

  private val store : mutable.Set[A] = mutable.Set()

  override def isEmpty: Boolean = synchronized(store.isEmpty)

  override val storedTypes: Seq[DataType[Any]] = Seq(dt)
  override def clear(): Unit = synchronized(store.clear())
  override def get[T](t: DataType[T]): Set[T] = synchronized(t match {
    case `dt` => synchronized(store.toSet.asInstanceOf[Set[T]])
    case _ => Set()
  })

  override def updateResult(r: Delta): Delta = synchronized {
    val delta = Result()

    val ins = r.inserts(dt).iterator
    val upds = r.updates(dt).iterator
    val dels = r.removes(dt).iterator

    while(dels.hasNext) {
      val item = dels.next()
      val removed = store.remove(item)
      if(removed) delta.remove(dt)(item)
    }

    while(ins.hasNext) {
      val item = ins.next()
      val inserted = store.add(item)
      if(inserted) delta.insert(dt)(item)
    }

    while(upds.hasNext) {
      val (oldI, newI) = upds.next()
      store.remove(oldI)
      if(!store.contains(newI)){
        store.add(newI)
        delta.update(dt)(oldI)(newI)
      }
    }

    if(!delta.isEmpty) {
      leo.Out.debug(s"TypedSet($dt) : \n   ${
        store.map { d => d match {
          case (id: Long, c: AnnotatedClause) => s"($id) [${c.id}]"
          case c : AnnotatedClause => s"[${c.id}]"
          case _ => d.toString
        }}.mkString("\n    ")
      }")
    }
    delta
  }
}
