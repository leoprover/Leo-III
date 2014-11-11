package leo.agents
package impl

import leo.datastructures.blackboard.FormulaStore
import scala.sys.process._

object ScriptAgent {
  /**
   * Performs an initial check, whether the script is existing
   * and then starts the script.
   *
   * @param path - Path to the script
   * @return ScriptAgent with that script
   */
  def apply(path : String) : Option[Agent] = {
    if(new java.io.File(path).exists())
      Some(new ScriptAgent(path))
    else
      None
  }
}

/**
 * <p>
 * Agent to execute a given script.
 * The existance of the script is not checked.
 * </p>
 *
 * <p>
 * Change later to an abstract class to
 * allow a more specific filter.
 * </p>
 *
 * @author Max Wisniewski
 * @since 11/10/14
 */
class ScriptAgent(path : String) extends AbstractAgent{
  override protected def toFilter(event: FormulaStore): Iterable[Task] = ???

  /**
   *
   * @return the name of the agent
   */
  override def name: String = "ScriptAgent"

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case t1 : ScriptTask =>



      // Only execution at this point. No interpretation of the result.
      return new StdResult(Set.empty, Map.empty, Set.empty)
    case _ => println("Wrong task for execution received.")
      return new StdResult(Set.empty, Map.empty, Set.empty)
  }
}

class ScriptTask(fs : Set[FormulaStore]) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget
}

