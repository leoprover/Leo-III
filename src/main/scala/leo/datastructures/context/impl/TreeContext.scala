package leo
package datastructures.context
package impl

import scala.collection._
import java.util.concurrent.atomic.AtomicInteger

object TreeContext {
  val count : AtomicInteger = new AtomicInteger(0)
}

/**
 *
 * Implementation of the Contexts as a tree.
 *
 * A correct use is assumed, hence no cycle detection is done.
 *
 * @author Max Wisniewski
 * @since 11/25/14
 */
class TreeContext extends Context{

  protected[impl] var _parent : TreeContext = null
  protected[impl] var _children : List[TreeContext] = Nil
  protected[impl] var _split : SplitKind = NoSplit
  private val id : Int = TreeContext.count.getAndIncrement

  /**
   *
   * Set of all immediate child contexts.
   *
   * @return All children contexts in a set
   */
  override def childContext: Set[Context] = _children.toSet

  /**
   * Unique ID for the context
   *
   * @return uid
   */
  override def contextID: Int = id

  /**
   *
   * The unique parent context.
   *
   * @return parent Context
   */
  override def parentContext: Context = _parent

  /**
   *
   * Stores whether the children are
   * basically OR (Beta) or AND (Alpha)
   * connected.
   *
   * @return
   */
  override def splitKind: SplitKind = _split

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
  override def split(split: SplitKind, children: Int): Boolean = synchronized(splitKind match {
    case NoSplit =>
      _split = split
      var i : Int = 0
      while(i<children) {
        i += 1
        val newContext = new TreeContext
        newContext._parent = this
        _children = newContext :: _children
      }
      true
    case _ =>
      false
  })

  /**
   * Closes a context.
   *
   * Thereby all subcontexts are deattached from the tree
   * and the splitkind is set to UnSplittable
   */
  override def close(): Unit = synchronized({
    _children foreach {c => c._parent = null}
    _children = Nil
    _split = UnSplittable
  })

  /**
   *
   * Removes all children from the context.
   * If the flag is true, the splitkind is set to UnSplittable.
   * Otherwise the context is fresh set to NoSplit.
   *
   * @param finished - true => UnSplittable, false => NoSplit
   */
  override def close(finished: Boolean): Unit = synchronized({
    _children foreach {c => c._parent = null}
    _children = Nil
    if(finished) _split = UnSplittable else _split = NoSplit
  })

  /**
   * Returns if the context is closed.
   *
   * @return true if the context is closed, i.e. a solution was found
   */
  override def isClosed: Boolean = synchronized(_split == UnSplittable)
}
