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
  def storedTypes : Seq[DataType]

  /**
   * Inserts a new data into the data structure.
   * If called by the blackboard, it is assured, that
   * the paramater has one of the types stored in `storedTypes`.
   *
   * @param n is new data to be inserted.
   * @return true, if the insertion was sucessfull. False otherwise.
   */
  def insert(n : Any) : Boolean

  /**
   *
   * Updates a data to some new value.
   * If called by the blackboard, it is assured, that
   * the paramater has one of the types stored in `storedTypes`.
   *
   * @param o the old data.
   * @param n the new data.
   * @return True, if the update was sucessfull. Should only return false,
   *         if n was already in the data structure. In any case o is deleted from the data structure.
   */
  def update(o : Any, n : Any) : Boolean

  /**
   * Deletes a data from the data strucutre.
   * If called by the blackboard, it is assured, that
   * the paramater has one of the types stored in `storedTypes`.
   *
   * @param d the data to be deleted.
   */
  def delete(d : Any)

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
  protected[blackboard] def all(t : DataType) : Set[Any]
}
