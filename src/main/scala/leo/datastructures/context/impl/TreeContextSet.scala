package leo
package datastructures.context
package impl

import scala.collection._



/**
 *
 * Implementation of a {@see ContextSet} that mimics the tree structure
 * of the contexts itself to obtain the different context elements.
 * The elements are hereby saved exactly once. Looking through the
 * complete context will require to walk backwards through the graph
 * and collect the elements.
 *
 *
 * @author Max Wisniewski
 * @since 11/24/14
 */
class TreeContextSet[A] extends ContextSet[A] {

  /**
   * A map from context to the sets of elements. Since pointers
   * are stored in the contexts, there is no need to implement
   * the tree again.
   */
  private val contextSets : mutable.Map[Context,mutable.Set[A]] = new mutable.HashMap[Context,mutable.Set[A]] with mutable.SynchronizedMap[Context,mutable.Set[A]]

  /**
   * Checks if an element `a` is contained in a given context.
   *
   * @param a - The element to check
   * @param c - The context to check
   * @return true, iff a is contained in c
   */
  override def contains(a: A, c: Context): Boolean = Context.getPath(c) exists {c1 => contextSets.get(c1).fold(false)(_.contains(a))}

  /**
   * Returns an element in context `c`
   * that is equal to `a`.
   *
   * @param a - Element to search
   * @param c - The context
   * @return THe element in the context, matching A
   */
  override def get(a: A, c: Context): Option[A] =  {
    if (contains(a, c))
      Some(a)
    else
      None
  }

  /**
   * Clears a context and all its sub contexts of all elements.
   * @param c
   */
  override def clear(c: Context): Unit = ???

  /**
   * Resets the data structure to an initial state
   */
  override def clear(): Unit = {
    contextSets.clear()
  }

  /**
   *
   * Removes an element form the context c
   *
   * @param a - The element to be removed
   * @param c - The context in which it is removed
   * @return true, iff deletion was successful
   */
  override def remove(a: A, c: Context): Boolean = {
    val p = Context.getPath(c).iterator

    while(p.hasNext) {
      val n = p.next()
      contextSets.get(n) match {
        case Some(s) => if(s.contains(a)) {s.remove(a); return true}
        case None  => ()
      }
    }
    return false
  }

  /**
   * Returns a sequence of all context c, in which a is contained
   *
   * @param a - Element to be searched
   * @return All containing contexts
   */
  override def inContext(a: A): Iterable[Context] = contextSets.filter{case (_,s) => s.contains(a)}.keys

  /**
   *
   * Inserts an element `a` into the context c.
   *
   * @param a - The element to insert
   * @param c - The context to insert
   * @return true, iff insertion was successful
   */
  override def add(a: A, c: Context): Boolean =
    if(contains(a,c)) false
    else
    {
      (contextSets.get(c) match {
        case Some(s) => s.add(a)
        case None =>
            val s = new mutable.HashSet[A] with mutable.SynchronizedSet[A]
            contextSets.put(c,s)
            s.add(a)
      })
      true
    }

  /**
   * Returns a set of all elements in the context c.
   *
   * @param c - The context of the elements
   * @return All elements in c
   */
  override def getAll(c: Context): Set[A] = contextSets.filter{case (c1,_) => Context.getPath(c).contains(c1)}.values.toSet.flatten

  /**
   * Returns a set of all elements. Independent of
   * their context.
   *
   * @return ALl element in the data structure
   */
  override def getAll: Set[A] = contextSets.values.toSet.flatten


}
