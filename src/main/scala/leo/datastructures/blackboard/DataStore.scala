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

  /**
   * This method returns all Types stored by this data structure.
   *
   * @return all stored types
   */
  def storedTypes : Seq[DataType[Any]]

  /**
    *
    * Inserts all results produced by an agent into the datastructure.
    *
    * @param r - A result inserted into the datastructure
    */
  def updateResult(r : Delta) : Boolean  // TODO Return really updated values for further consideration not only Bool

  /**
   * Removes everything from the data structure.
   * After this call the ds should behave as if it was newly created.
   */
  def clear()

  /**
   * Returns a list of all stored data.
   *
   * @param t
   * @return
   */
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
