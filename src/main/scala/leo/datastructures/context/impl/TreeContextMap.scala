package leo
package datastructures.context
package impl

import scala.collection.{Iterator, mutable}

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
    if(lookup(k,c).fold(false : Boolean)(v1 => v1 == v)) return false
    remove(k,c)   // If it is in fact an update
    contextMaps.get(c) match {
    case Some(m) =>
      m.put(k,v)
    case None =>
      val m = new mutable.HashMap[K,V]()
      contextMaps.put(c,m)
      m.put(k,v)
    }
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
  override def remove(k: K, c: Context): Boolean = {
    val p = Context.getPath(c).iterator

    while(p.hasNext) {
      val n = p.next()
      contextMaps.get(n) match {
        case Some(s) =>
          if(s.contains(k)) {
            val v = s.remove(k).get
            distributeAlongPath(n, p, k, v)
            // Removed from the context, BUT distribute to the other contexts
            return true
          }
        case None  => ()
      }
    }
    return false
  }

  /**
    * Inserts the Element a into all childrens of c, that are not p[0] and rekurses into p[0] with the tail p[1..n].
    */
  private def distributeAlongPath(c : Context, p : Iterator[Context], k : K, v : V) : Unit = {
    if(p.hasNext){
      // If we are on the path, then we push the element to the sides
      val nc = p.next()
      c.childContext.foreach{cc =>
        if(cc != nc){
          contextMaps.get(cc) match {
            case Some(s) =>
              s.put(k,v)
            case None =>
              val m = new mutable.HashMap[K,V]()
              contextMaps.put(cc,m)
              m.put(k,v)
          }
        }
      }
      distributeAlongPath(nc, p, k, v)
    }
  }

  /**
   *
   * Returns all values in a specific context
   *
   * @param c - The context to look at
   * @return All values in c
   */
  override def valueSet(c: Context): Set[V] = Context.getPath(c).flatMap{c1 => contextMaps.get(c1).fold(Iterable[V]())(_.values)}.toSet

  /**
   *
   * Returns all keys in a specific context
   *
   * @param c - The context to look at
   * @return All keys in c
   */
  override def keySet(c: Context): Set[K] = Context.getPath(c).flatMap{c1 => contextMaps.get(c1).fold(Iterable[K]())(_.keys)}.toSet
}
