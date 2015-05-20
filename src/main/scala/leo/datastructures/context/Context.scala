package leo
package datastructures.context


import scala.collection._

object Context {

  private val root : Context = new impl.TreeContext

  /**
   * Accesspoint and maincontext of leo.
   *
   * @return Main context
   */
  def apply() : Context = root

  /**
   * Parses from a context the complete sequence of contexts
   * from the root context to the given one, if one exists.
   *
   * @param c - The node we want to reach
   * @return Path from the root context to c, in this direction.
   */
  protected[context] def getPath(c : Context) : Seq[Context] = {
    var res = List(c)
    var akk : Context = c
    while(akk.parentContext != null) {
      res = akk.parentContext :: res
      akk = akk.parentContext
    }
    if(res.head.contextID != Context().contextID) {
      Out.severe(s"Deattached head context found contextID=${akk.contextID} reached from contextID=${c.contextID}. (Path = ${pathToString(res)}) (Root =${Context().contextID})")
    }
    res
  }

  private def pathToString(c : List[Context]) : String = c.map(_.contextID).mkString(" , ")


  /**
   * Computes the lca of a context.
   *
   * TODO: ATM only paths are recognized. All others will produce unintended behaviour.
   *
   * @param c
   * @return
   */
  def lca (c : Iterable[Context]) : Context = {
    var maxDepth : Int = 1
    var res : Context = Context()
    val it = c.iterator
    while(it.hasNext) {
      val c = it.next()
      val l = getPath(c).length
      if(l > maxDepth) {
        maxDepth = l
        res = c
      }
    }
    res
  }
}

/**
 *
 * Context Identifier to retrieve a specific context
 *
 * @author Max Wisniewski
 * @since 11/20/14
 */
trait Context {
  /**
   * Unique ID for the context
   *
   * @return uid
   */
  def contextID : Int

  /**
   *
   * Set of all immediate child contexts.
   *
   * @return All children contexts in a set
   */
  def childContext : Set[Context]

  /**
   *
   * The unique parent context.
   *
   * @return parent Context
   */
  def parentContext : Context

  /**
   *
   * Stores whether the children are
   * basically OR (Beta) or AND (Alpha)
   * connected.
   *
   * @return
   */
  def splitKind : SplitKind

  /**
   *
   * Splits the context with a given kind into `children` subcontexts.
   *
   * If splitkind is already set, this action does nothing.
   *
   * The result is true, if a split happend and false, if the node was
   * already splitted or marked for UnSplittable
   *
   * @param split - SplitKind
   * @param children - Amount of children
   * @return true, if the operation was performed successful
   */
  def split(split : SplitKind, children : Int) : Boolean

  /**
   * Closes a context.
   *
   * Thereby all subcontexts are deattached from the tree
   * and the splitkind is set to UnSplittable
   */
  def close() : Unit

  /**
   *
   * Removes all children from the context.
   * If the flag is true, the splitkind is set to UnSplittable.
   * Otherwise the context is fresh set to NoSplit.
   *
   * @param finished - true => UnSplittable, false => NoSplit
   */
  def close(finished : Boolean) : Unit

  /**
   * Returns if the context is closed.
   *
   * @return true if the context is closed, i.e. a solution was found
   */
  def isClosed : Boolean
}


abstract sealed class SplitKind

/**
 * The child contexts are conjunctively connected
 */
case object AlphaSplit extends SplitKind

/**
 * The Childcontexts are disjunctively connected
 */
case object BetaSplit extends SplitKind

/**
 * The Context is marked to allow no further split
 */
case object UnSplittable extends SplitKind

/**
 * The context is not yet splitted.
 */
case object NoSplit extends SplitKind

/**
 * Common trait for all Context sensitive sets
 *
 * @tparam A - Type of the stored data
 */
trait ContextSet[A] {
  /**
   * Checks if an element `a` is contained in a given context.
   *
   * @param a - The element to check
   * @param c - The context to check
   * @return true, iff a is contained in c
   */
  def contains(a : A, c : Context) : Boolean

  /**
   * Returns an element in context `c`
   * that is equal to `a`.
   *
   * @param a - Element to search
   * @param c - The context
   * @return THe element in the context, matching A
   */
  def get(a : A, c : Context) : Option[A]

  /**
   *
   * Inserts an element `a` into the context c.
   *
   * @param a - The element to insert
   * @param c - The context to insert
   * @return true, iff insertion was successful
   */
  def add(a : A, c : Context) : Boolean

  /**
   *
   * Removes an element form the context c
   *
   * @param a - The element to be removed
   * @param c - The context in which it is removed
   * @return true, iff deletion was successful
   */
  def remove(a : A, c : Context) : Boolean

  /**
   * Returns a set of all elements in the context c.
   *
   * @param c - The context of the elements
   * @return All elements in c
   */
  def getAll(c : Context) : Set[A]

  /**
   * Returns a set of all elements. Independent of
   * their context.
   *
   * @return ALl element in the data structure
   */
  def getAll : Set[A]

  /**
   * Returns a sequence of all context c, in which a is contained
   *
   * @param a - Element to be searched
   * @return All containing contexts
   */
  def inContext(a : A) : Iterable[Context]

  /**
   * Clears a context and all its sub contexts of all elements.
   * @param c
   */
  def clear(c : Context) : Unit

  /**
   * Resets the data structure to an initial state
   */
  def clear() : Unit
}

trait OrderedContextSet[A <: Ordered[A]] extends ContextSet[A] {
  /**
   * Returns a set of all `a'` with a' < a.
   *
   * @param a searched parameter.
   * @return set of all elements strictly smaller than a.
   */
  def getSmaller(a : A, c : Context) : Set[A]

  /**
   * Returns a set of all `a'` with a' >= a.
   *
   * @param a searched parameter
   * @return set of all elements strictly smaller than a
   */
  def getBigger(a : A, c : Context) : Set[A]
}

/**
 * Common trait for all context sensitive maps.
 *
 * @tparam K - Key type
 * @tparam V - Value type
 */
trait ContextMap[K,V] {

  /**
   *
   * Obtains the value of a key k in the context c, if one exists.
   * An error will be raised otherwise
   *
   * @param k - The key to look up
   * @param c - The context in which to look up
   * @return the value if it exists and an error otherwise
   */
  def get(k : K, c : Context) : V

  /**
   *
   * Looks up the value for a key.
   *
   * @param k - The key to look up
   * @param c - THe context in which to look
   * @return some value if it exists and non otherwise
   */
  def lookup(k : K, c : Context) : Option[V]

  /**
   *
   * Inserts an new key value
   *
   * @param k - The new key
   * @param v - The new value
   * @param c - The context c, in which it is to be inserted
   * @return true, iff the insertion was successful
   */
  def put(k : K, v : V, c : Context) : Boolean

  /**
   *
   * Returns all keys in a specific context
   *
   * @param c - The context to look at
   * @return All keys in c
   */
  def keySet(c : Context) : Set[K]

  /**
   *
   * Returns all values in a specific context
   *
   * @param c - The context to look at
   * @return All values in c
   */
  def valueSet(c : Context) : Set[V]

  /**
   *
   * Removes a key k and its value from a context.
   *
   * @param k - The key to remove
   * @param c - The context in which to remove
   * @return true, iff the key was removed successful
   */
  def remove(k : K, c : Context) : Boolean

  /**
   *
   * Removes all key - value pairs from the context c and
   * all its subcontexts
   *
   * @param c - The context to be cleared
   */
  def clear(c : Context)

  /**
   * Convert the datastrucutre into an initial state.
   */
  def clear() : Unit
}