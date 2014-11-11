package leo.agents
package impl

import leo.datastructures.blackboard.FormulaStore
import java.io.{PrintWriter, File}
import leo.modules.output.ToTPTP
import leo.modules.output.logger._

import scala.sys.process._

object ScriptAgent {
  /**
   * Performs an initial check, whether the script is existing
   * and then starts the ScriptAgent.
   *
   * @param path - Path to the script
   * @return ScriptAgent with that script
   */
  def apply(path : String) : Option[Agent] = {
    if(new java.io.File(path).exists())
      if(path.charAt(0)!='/') Some(new ScriptAgent("./"++path))
      else Some(new ScriptAgent(path))
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

      // Writing the context into a temporary file
      val file = File.createTempFile("remoteInvoke",".p")
      //file.deleteOnExit()
      val writer = new PrintWriter(file)
      try{
        Console.output("Writing to temporary file:")
        t1.readSet() foreach {formula =>
          Console.output(ToTPTP(formula))
          writer.println(ToTPTP(formula).output)}
      } finally writer.close()

      //Executing the prover
      try {
        Console.output(s"Executing $path on file ${file.getAbsolutePath}")
        val res = Seq(path, file.getAbsolutePath).!!
        Console.output("Got result from external prover:")
        Console.output(res.toString)

      } catch {
        case _ => Console.trace("External prover "+path+" terminated unsuccessfull.")
          return EmptyResult
      }
      // Only execution at this point. No interpretation of the result.
      return EmptyResult
    case _ => Console.info(s"$name recevied a wrong task $t.")
      return EmptyResult
  }
}

class ScriptTask(fs : Set[FormulaStore]) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget
}

