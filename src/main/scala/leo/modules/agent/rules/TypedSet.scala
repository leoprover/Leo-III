package leo.modules.agent.rules

import leo.datastructures.blackboard.{DataStore, DataType, Delta, Result}

import scala.collection.mutable

/**
  * Stores the data of a certain data Type
  */
class TypedSet[A](dt : DataType[A]) extends DataStore {

  private val store : mutable.Set[A] = mutable.Set()

  override val storedTypes: Seq[DataType[Any]] = Seq(dt)
  override def clear(): Unit = synchronized(store.clear())
  override def get[T](t: DataType[T]): Set[T] = t match {
    case `dt` => synchronized(store.toSet.asInstanceOf[Set[T]])
    case _ => Set()
  }

  override def updateResult(r: Delta): Delta = {
    val delta = Result()

    val ins = r.inserts(dt).iterator
    val upds = r.updates(dt).iterator
    val dels = r.removes(dt).iterator

    while(dels.hasNext) {synchronized {
      val item = dels.next()
      val removed = store.remove(item)
      if(removed) delta.insert(dt)(item)
    }}

    while(ins.hasNext) {synchronized {
      val item = dels.next()
      val inserted = store.add(item)
      if(inserted) delta.insert(dt)(item)
    }}

    while(upds.hasNext) {synchronized {
      val (oldI, newI) = upds.next()
      store.remove(oldI)
      if(!store.contains(newI)){
        store.add(newI)
        delta.update(dt)(oldI)(newI)
      }
    }}

    delta
  }
}
