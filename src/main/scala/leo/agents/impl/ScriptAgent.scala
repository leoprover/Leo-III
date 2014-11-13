package leo.agents
package impl

import leo.datastructures.blackboard.FormulaStore
import leo.datastructures.internal._
import leo.datastructures.internal.terms.Term._
import java.io.{PrintWriter, File}
import leo.datastructures.internal.Signature
import leo.modules.output.{ToTPTP, Output}
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
      if(path.charAt(0)!='/' && path.charAt(0) != '.') Some(new ScriptAgent("./"++path))
      else Some(new ScriptAgent(path))
    else
      None
  }
}

/**
 * <p>
 * Agent to execute a given script. By passing a formula context in thf syntax through a temporary file
 * to the script as its first parameter.
 *
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
      file.deleteOnExit()
      val writer = new PrintWriter(file)
      try{
        Out.info("Writing to temporary file:")
        contextToTPTP(t1.readSet()) foreach {out =>
          Out.info(out)
          writer.println(out.output)}
      } finally writer.close()

      //Executing the prover
      var success = true
      try {
        Out.info(s"Executing $path on file ${file.getAbsolutePath}")

        // -------------------------------------------------------------
        //   Execution
        // -------------------------------------------------------------
        val res = Seq(path, file.getAbsolutePath).lines
        Out.info("Got result from external prover:")
        res foreach {x => Out.info(x)}

      } catch {
        case _ : Throwable => Out.info(s"External prover $path terminated unsuccessfull.")
          success = false
      }
      if(success)
        Out.info(s"The external prover $path found a proof.")
      else
        Out.info(s"The external prover $path did not found a proof.")
      // Only execution at this point. No interpretation of the result.
      return EmptyResult
    case _ : Throwable => Out.info(s"$name recevied a wrong task $t.")
      return EmptyResult
  }



  private def contextToTPTP(fS : Set[FormulaStore]) : Seq[Output] = {
    var out: List[Output] = List.empty[Output]
    Signature.get.allUserConstants foreach {
      constantToTPTP(_) foreach {t => out = t :: out}
    }
    fS foreach {formula =>
      out = ToTPTP(formula) :: out}
    out.reverse
  }


  private def constantToTPTP(k : Signature#Key) : Seq[Output] = {
    val constant = Signature.get.apply(k)
    (constant.defn) match {
      case Some(defn) => Seq(ToTPTP(s"${constant.name}_type", k), ToTPTP(s"${constant.name}_def", ===(mkAtom(k),defn), Role_Definition.pretty))
      case (None) => Seq(ToTPTP(s"${constant.name}_type", k))
    }
  }
}

class ScriptTask(fs : Set[FormulaStore]) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget
}



