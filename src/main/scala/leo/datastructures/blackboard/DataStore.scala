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

  def updateResult(r : Delta) : Boolean  // TODO Return really updated values for further consideration not only Bool

  def clear()

  protected[blackboard] def all[T](t : DataType[T]) : Set[T]

  def insertData[T](d : DataType[T])(n : T) : Boolean = {
    val r = Result().insert(d)(n)
    updateResult(r)
    true
  }

  def deleteData[T](d : DataType[T])(n : T) : Boolean = {
    val r = Result().remove(d)(n)
    updateResult(r)
    true
  }

  def updateData[T](d : DataType[T])(o : T)(n : T) : Boolean = {
    val r = Result().update(d)(o)(n)
    updateResult(r)
    true
  }
}
