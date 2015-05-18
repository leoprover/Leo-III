package leo
package datastructures.context
package impl

import leo.datastructures.blackboard.FormulaStore

import scala.collection.mutable

/**
 *
 * Implementation of a [[leo.datastructures.context.ContextMap]] that mimics the tree structure
 * of the contexts itself to obtain the different context elements.
 * The elements are hereby saved exactly once. Looking through the
 * complete context will require to walk backwards through the graph
 * and collect the elements.
 *
 *
 * @author Max Wisniewski
 * @since 5/18/15
 */
class TreeContextMap[K,V] extends ContextMap[K,V] {

  private val contextMaps : mutable.Map[Context, mutable.Map[K,V]] = new mutable.HashMap[Context, mutable.Map[K,V]]()

  /**
   *
   * Obtains the value of a key k in the context c, if one exists.
   * An error will be raised otherwise
   *
   * @param k - The key to look up
   * @param c - The context in which to look up
   * @return the value if it exists and an error otherwise
   */
  override def get(k: K, c: Context): V = lookup(k,c).get

  /**
   *
   * Looks up the value for a key.
   *
   * @param k - The key to look up
   * @param c - THe context in which to look
   * @return some value if it exists and non otherwise
   */
  override def lookup(k: K, c: Context): Option[V] = {
    val e = Context.getPath(c).map{c1 => contextMaps.get(c1).fold(None : Option[V]){m => m.get(k)}}.find(_.isDefined).fold(None : Option[V]){x => x}
    e
  }

  /**
   *
   * Removes all key - value pairs from the context c and
   * all its subcontexts
   *
   * @param c - The context to be cleared
   */
  override def clear(c: Context): Unit = contextMaps.remove(c)

  /**
   * Convert the datastrucutre into an initial state.
   */
  override def clear(): Unit = contextMaps.clear()

  /**
   *
   * Inserts an new key value
   *
   * @param k - The new key
   * @param v - The new value
   * @param c - The context c, in which it is to be inserted
   * @return true, iff the insertion was successful
   */
  override def put(k: K, v: V, c: Context): Boolean = {
    if(lookup(k,c).isDefined) return false
    (contextMaps.get(c) match {
    case Some(m) =>
      m.put(k,v)
    case None =>
      val m = new mutable.HashMap[K,V]()
      contextMaps.put(c,m)
      m.put(k,v)
    })
    true
  }

  /**
   *
   * Removes a key k and its value from a context.
   *
   * @param k - The key to remove
   * @param c - The context in which to remove
   * @return true, iff the key was removed successful
   */
  override def remove(k: K, c: Context): Boolean = Context.getPath(c).find{c1 =>
    contextMaps.get(c).fold(false)(_.contains(k))}.fold(false){c2 =>
      contextMaps.get(c2).fold(false){m => m.remove(k).isDefined}
  }

  /**
   *
   * Returns all values in a specific context
   *
   * @param c - The context to look at
   * @return All values in c
   */
  override def valueSet(c: Context): Set[V] = Context.getPath(c).map{c1 => contextMaps.get(c1).fold(Iterable[V]())(_.values)}.flatten.toSet

  /**
   *
   * Returns all keys in a specific context
   *
   * @param c - The context to look at
   * @return All keys in c
   */
  override def keySet(c: Context): Set[K] = Context.getPath(c).map{c1 => contextMaps.get(c1).fold(Iterable[K]())(_.keys)}.flatten.toSet
}
