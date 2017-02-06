package leo
package datastructures.blackboard

import leo.datastructures.Pretty

import scala.collection.mutable

/**
  * A delta for the data stored in the blackboard..
  */
trait Delta extends Event {
  /**
    * Checks if this Delta is empty, i.e. it changes nothing
    * @return true, iff it is empty
    */
  def isEmpty : Boolean

  /**
    * Returns a sequence of all stored datatypes by this result.
    *
    * @return all stored datatypes
    */
  def types : Seq[DataType[Any]]


  /**
    * Returns all inserts of type t.
    *
    * @param t is the requested type.
    * @return all inserted data of type t.
    */
  def inserts[T](t : DataType[T]) : Seq[T]

  /**
    *
    * All updates of type t.
    *
    * @param t is the requested type.
    * @return all inserted data of type t.
    */
  def updates[T](t : DataType[T]) : Seq[(T, T)]

  /**
    *
    * All removes of type t.
    *
    * @param t is the requested type.
    * @return all inserted data of type t.
    */
  def removes[T](t : DataType[T]) : Seq[T]

  /**
    *
    * Returns an immutable version of this Delta.
    *
    * @return this delta, but immutable
    */
  def immutable : Delta = this

  /**
    * This delta into another one.
    *
    * @param d - the other delta
    * @return the merged delta
    */
  def merge(d : Delta) : Delta
}

/**
 * Compagnion Object for Result
 */
object Result {

  /**
   * Creates an Empty Result Object.
   *
   * @return an empty Result Object.
   */
  def apply() : MutableDelta = new MutableDelta
}

/**
 * The Result / Delta of data an agent has
 * as an action on the data structures in the blackboard.
 *
 * The class allows adding of various types and a retrieval per type.
 *
 * This is a <b>mutable</b> class. The manipulating operations return
 * the changed object for conviniece.
 */
class MutableDelta extends Delta {

  private val insertM : mutable.HashMap[DataType[Any], Seq[Any]] = new mutable.HashMap[DataType[Any], Seq[Any]]()
  private val updateM : mutable.HashMap[DataType[Any], Seq[(Any,Any)]] = new mutable.HashMap[DataType[Any], Seq[(Any,Any)]]()
  private val removeM : mutable.HashMap[DataType[Any], Seq[Any]] = new mutable.HashMap[DataType[Any], Seq[Any]]()
  private var prio : Int = 5


  def isEmpty : Boolean = insertM.isEmpty && updateM.isEmpty && removeM.isEmpty

  /**
   * Inserts given data d of type t into
   * the registered data structures.
   *
   *
   * @param t is the type of the data.
   * @param d is the data itself
   */
  def insert[T](t : DataType[T])(d : T): MutableDelta = {
    insertM.put(t, d +: insertM.getOrElse(t, Nil))
    this
  }

  /**
   * Updates a given data d1 to a new value d2
   * of type t in the registered data structures.
   *
   * @param t is the type of the data.
   * @param d1 is the old data
   * @param d2 is the new data
   */
  def update[T](t : DataType[T])(d1 : T)(d2 : T): MutableDelta = {
    updateM.put(t, (d1,d2) +: updateM.getOrElse(t, Nil))
    this
  }

  /**
   * Removes a given data d of type t
   *
   * @param t is the type of data.
   * @param d is the data itself.
   */
  def remove[T](t : DataType[T])(d : T): MutableDelta = {
    removeM.put(t, d +: removeM.getOrElse(t, Nil))
    this
  }


  /**
   * Returns all inserts of type t.
   *
   * @param t is the requested type.
   * @return all inserted data of type t.
   */
  def inserts[T](t : DataType[T]) : Seq[T] = insertM.getOrElse(t, Nil).map(x => t.convert(x))

  /**
   *
   * All updates of type t.
   *
   * @param t is the requested type.
   * @return all inserted data of type t.
   */
  def updates[T](t : DataType[T]) : Seq[(T, T)] = updateM.getOrElse(t,Nil).map{ case (x,y) => (t.convert(x), t.convert(y))}

  /**
   *
   * All removes of type t.
   *
   * @param t is the requested type.
   * @return all inserted data of type t.
   */
  def removes[T](t : DataType[T]) : Seq[T] = removeM.getOrElse(t,Nil).map(x => t.convert(x))

  /**
   * Returns a sequence of all stored datatypes by this result.
   *
   * @return all stored datatypes
   */
  def types : Seq[DataType[Any]] = ((removeM.keySet union insertM.keySet) union updateM.keySet).toSeq


  /**
    * This delta into another one.
    *
    * @param d - the other delta
    * @return the merged delta
    */
  override def merge(d: Delta): Delta = ???

  final override def toString : String = {
    val sb : mutable.StringBuilder = new mutable.StringBuilder()
    sb.append("Result(\n")

    sb.append(" Insert(\n")
    insertM.keysIterator.foreach{dt =>
      sb.append(s"   $dt ->\n")
      insertM.get(dt).foreach{ds =>
        ds.foreach{_ match {
          case p : Pretty => sb.append(s"     ${p.pretty}\n")
          case data => sb.append(s"     ${data}\n")
        }
        }
      }
    }
    sb.append(" )\n")

    sb.append(" Update(\n")
    updateM.keysIterator.foreach{dt =>
      sb.append(s"   $dt ->\n")
      updateM.get(dt).foreach{ds =>
        ds.foreach{
          _ match {
            case p : Pretty => sb.append(s"     ${p.pretty}\n")
            case (d1,d2) =>
              sb.append(s"      $d1\n   ->\n     $d2\n")
          }
        }
      }
    }
    sb.append(" )\n")

    sb.append(" Delete(\n")
    removeM.keysIterator.foreach{dt =>
      sb.append(s"   $dt ->\n")
      removeM.get(dt).foreach{ds =>
        ds.foreach{_ match {
          case p : Pretty => sb.append(s"     ${p.pretty}\n")
          case data => sb.append(s"     ${data}\n")
        }
        }
      }
    }
    sb.append(" )\n")

    sb.append(")")
    sb.toString()
  }
}
