package leo
package agents

import leo.datastructures.blackboard.DataType

import scala.StringBuilder
import scala.collection.mutable



/**
 * <p>
 * A implementation of TAgent which supports empty implementations
  * for non-essential methods of [[Agent]]
  * <p>
  *
 * @author Max Wisniewski
 * @since 5/14/14
 */
abstract class AbstractAgent extends Agent {

  /**
   * This method is called when an agent is killed by the scheduler
   * during execution. This method does standardized nothing.
   *
   * In the case an external Process / Thread is created during the
   * execution of the agent, this method can clean up the processes.
   */
  def kill(): Unit = {}

  /**
   * Declares the agents interest in specific data.
   *
   * @return None -> The Agent does not register for any data changes. <br />
   *         Some(Nil) -> The agent registers for all data changes. <br />
   *         Some(xs) -> The agent registers only for data changes for any type in xs.
   */
  def interest : Option[Seq[DataType]] = Some(Nil)

  /**
   * Prints the comment in the comment stream of the console.
   * Formated with the name of the agent.
   *
   * @param comment - The comment to be printed
   */
  def comment(comment : String) : Unit = leo.Out.comment(formatOut(comment))
  /**
   * Prints the comment in the debug stream of the console.
   * Formated with the name of the agent.
   *
   * @param debug - The comment to be printed
   */
  def debug (debug : String) : Unit = leo.Out.debug(formatOut(debug))
  /**
   * Prints the comment in the fine stream of the console.
   * Formated with the name of the agent.
   *
   * @param trace - The comment to be printed
   */
  def trace (trace : String) : Unit = leo.Out.trace(formatOut(trace))
  private def formatOut(out : String) : String = {
    val sb = new StringBuilder()
    sb.append(s"[${name}]:")
    out.lines.foreach{s => sb.append("\n  "+s)}
    sb.toString()
  }

  override def taskChoosen(t: Task): Unit = {}
  override def taskFinished(t: Task): Unit = {}
  override def taskCanceled(t : Task) : Unit = {}
  override def maxMoney: Double = 10000
}

