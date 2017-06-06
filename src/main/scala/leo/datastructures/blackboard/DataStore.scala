package leo.datastructures.blackboard

/**
 *
 * <p>
 * DataStore is a common interface for all data structures to be maintained by the blackboard
 * and updated by the agents.
 * </p>
 * <p>
 * The DataStore supplies a list of stored [[leo.datastructures.blackboard.DataType]] and
 * unsave methods to add Data to the DataStore.
 * </p>
 * @author Max Wisniewski
 * @since 4/15/15
 */
trait DataStore {

  def storedTypes : Seq[DataType[Any]]

  /**
    * Applies a Delta, the indendet change, to the data structure.
    * After the application only a slice of the delta is returned,
    * namely only the part of the delta, that changed the data structure.
    *
    * Formally
    * <p>
    * updateResult(r) = min { d | d(this) = r(this)},<br />
    * where min sorts with the subset relation.
    * <p>
    *
    * @param r The delta to be applied to the data structure
    * @return The slice of the delta really applied.
    */
  def updateResult(r : Delta) : Delta

  def clear()

  def get[T](t : DataType[T]) : Set[T]

  def isEmpty : Boolean = !synchronized(storedTypes exists (t => get(t).nonEmpty))

  def insertData[T](d : DataType[T])(n : T) : Boolean = {
    val r = Result().insert(d)(n)
    val res = updateResult(r)
    !res.isEmpty
  }

  def deleteData[T](d : DataType[T])(n : T) : Boolean = {
    val r = Result().remove(d)(n)
    val res = updateResult(r)
    !res.isEmpty
  }

  def updateData[T](d : DataType[T])(o : T)(n : T) : Boolean = {
    val r = Result().update(d)(o)(n)
    val res = updateResult(r)
    !res.isEmpty
  }
}
