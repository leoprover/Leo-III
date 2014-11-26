package leo.agents
package impl

import leo.datastructures.impl.Signature
import leo.datastructures.===
import leo.datastructures.blackboard.FormulaStore
import leo.datastructures.term.Term
import leo.datastructures.Role_Definition
import Term._
import java.io.{PrintWriter, File}
import leo.modules.output.{ToTPTP, Output}
import leo.modules.output.logger._

import scala.sys.process._

//object ScriptAgent {
//  /**
//   * Performs an initial check, whether the script is existing
//   * and then starts the ScriptAgent.
//   *
//   * @param path - Path to the script
//   * @return ScriptAgent with that script
//   */
//  def apply(path : String) : Option[Agent] = {
//    if(new java.io.File(path).exists())
//      if(path.charAt(0)!='/' && path.charAt(0) != '.') Some(new ScriptAgent("./"++path))
//      else Some(new ScriptAgent(path))
//    else
//      None
//  }
//}

/**
 * <p>
 * Agent to execute a given script. By passing a formula context in thf syntax through a temporary file
 * to the script as its first parameter.
 *
 * The existance of the script is not checked.
 * </p>
 *
 * <p>
 * IMPORTANT :  The script will delete the exit value and append it to the output stream.
 * </p>
 *
 * @author Max Wisniewski
 * @since 11/10/14
 */
abstract class ScriptAgent(path : String) extends AbstractAgent {

  def handle(input : Stream[String], err : Stream[String], errno : Int) : Result

  /**
   *
   * @return the name of the agent
   */
  override def name: String = s"ScriptAgent {$path}"



  private val exec : File = {
    val f = File.createTempFile(path,".sh")
    f.deleteOnExit()
    val writer = new PrintWriter(f)
    try{
      writer.println("#!/bin/sh")
      writer.println(path+" $1")
      writer.println("echo $?")
      writer.println("exit 0")
    } finally writer.close()
    Process(s"chmod u+x ${f.getAbsolutePath}").!
    f
  }

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
        Out.trace(s"[$name]: Writing to temporary file:")
        contextToTPTP(t1.readSet()) foreach {out =>
          Out.trace(out)
          writer.println(out.output)}
      } finally writer.close()

      //Executing the prover
      var success = true
        Out.info(s"[$name]: Executing $path on file ${file.getAbsolutePath}")

        // -------------------------------------------------------------
        //   Execution
        // -------------------------------------------------------------
        val res = Seq(s"${exec.getAbsolutePath}", file.getAbsolutePath).lines
        Out.trace(s"[$name]: Got result from external prover:")

        // Filter for exit code
        // TODO: Insert error stream
//        res foreach {l => Console.info(l)}
        return handle(res.init, Stream.empty, res.last.toInt)
    case _  => Out.info(s"[$name]: Recevied a wrong task $t.")
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
      case Some(defn) => Seq(ToTPTP(s"${constant.name}_type", k), ToTPTP(s"${constant.name}_def", ===(mkAtom(k),defn), Role_Definition))
      case (None) => Seq(ToTPTP(s"${constant.name}_type", k))
    }
  }
}

class ScriptTask(fs : Set[FormulaStore]) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget
}

