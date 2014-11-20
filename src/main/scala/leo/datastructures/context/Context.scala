package leo.datastructures.context

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
   * Stores wether the children are
   * basically OR (Beta) or AND (Alpha)
   * connected.
   *
   * @return
   */
  def splitKind : SplitKind
}


abstract sealed class SplitKind
case object AlphaSplit extends SplitKind
case object BetaSplit extends SplitKind
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
   * Returns a sequence of all context c, in which a is contained
   *
   * @param a - Element to be searched
   * @return All containing contexts
   */
  def inContext(a : A) : Seq[Context]

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

/**
 * Common trait for all context sensitive maps.
 *
 * @tparam K - Key type
 * @tparam V - Value type
 */
trait ContextMap[K,V] {

  def get(k : K, c : Context) : V

  def lookup(k : K, c : Context) : Option[V]

  def put(k : K, v : V, c : Context) : Boolean

  def keySet(c : Context) : Set[K]

  def valueSet(c : Context) : Set[V]

  def remove(k : K, c : Context) : Boolean

  def clear(c : Context)

  def clear() : Unit
}