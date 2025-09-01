package leo.modules.external

/**
  * A smaller interface for scala's [[scala.concurrent.Future]] class.
  * Implements the bare necessities for a Future in this setting.
  *
  * @tparam T - Type of the Result of the Future
  */
trait Future[+T] {
  /**
    * Checks for the processes termination.
    * @return true, iff the processes has finished.
    */
  def isCompleted: Boolean

  /**
    * Returns the result object after the process has finished.
    *
    * @return Some(result) if the process has finished, None otherwise.
    */
  def value: Option[T]

  /**
    * Blocks until the value is set.
    * After a call to blockValue isCompleted will return true and `value` will
    * always return the value of blockValue.
    * @return
    */
  def blockValue: T

  /**
    * Forcibly kills the underlying process calculating the future's result.
    */
  def kill(): Unit
}