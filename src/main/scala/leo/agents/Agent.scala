package leo
package agents

import leo.datastructures.Pretty
import leo.datastructures.blackboard.{DataType, Event, FormulaStore, Blackboard, Result}
import leo.datastructures.context.Context


import scala.StringBuilder
import scala.collection.mutable



/**
 * <p>
 * Interface for all Agent Implementations.
 * </p>
 *
 * <p>
 * The Agent itself is not a Thread, but a function to be called, at any
 * time its guard is satisfied.
 * </p>
 *
 * <p>
 * To register an Agent, it has to be passed to an AgentController.
 * (Runnable vs. Thread)
 * </p>
 * @author Max Wisniewski
 * @since 5/14/14
 */
abstract class Agent {

  /**
   *
   * @return the name of the agent
   */
  def name : String

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  def run(t : Task) : Result

  /**
   * This method is called when an agent is killed by the scheduler
   * during execution. This method does standardized nothing.
   *
   * In the case an external Process / Thread is created during the
   * execution of the agent, this method can clean up the processes.
   */
  def kill(): Unit = {}

  /**
   * Triggers the filtering of the Agent.
   *
   * Upon an Event the Agent can generate Tasks, he wants to execute.
   * @param event on the blackboard concerning change of data.
   * @return a List of Tasks the Agent wants to execute.
   */
  def toFilter(event : Event) : Iterable[Task]

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
}


/**
 * Common trait for all Agent Task's. Each agent specifies the
 * work it can do.
 *
 * The specific fields and accessors for the real task will be in
 * the implementation.
 *
 * @author Max Wisniewski
 * @since 6/26/14
 */
abstract class Task extends Pretty {

  /**
   * Prints a short name of the task
   * @return
   */
  def name : String

  /**
   *
   * Returns a set of all Formulas that are read for the task.
   *
   * @return Read set for the Task.
   */
  def readSet() : Set[FormulaStore]

  /**
   *
   * Returns a set of all Formulas, that will be written by the task.
   *
   * @return Write set for the task
   */
  def writeSet() : Set[FormulaStore]

  /**
   * Defines a set of Contexts on which the task will
   * write.
   *
   * @return set of all contexts the task will manipulate
   */
  def contextWriteSet() : Set[Context] = Set.empty

  /**
   * Checks for two tasks, if they are in conflict with each other.
   *
   * @param t2 - Second Task
   * @return true, iff they collide
   */
  def collide(t2 : Task) : Boolean = {
    val t1 = this
    if(t1 equals t2) true
    else {
      t1.readSet().intersect(t2.writeSet()).nonEmpty ||
        t2.readSet().intersect(t1.writeSet()).nonEmpty ||
        t2.writeSet().intersect((t1.writeSet())).nonEmpty ||
        t2.contextWriteSet().intersect((t1.contextWriteSet())).nonEmpty
    }
  }

  /**
   *
   * Defines the gain of a Task, defined for
   * a specific agent.
   *
   * @return - Possible profit, if the task is executed
   */
  def bid(budget : Double) : Double
}