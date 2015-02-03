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
import scala.collection.mutable

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
abstract class ScriptAgent(path : String) extends FifoAgent {

  def handle(input : Stream[String], err : Stream[String], errno : Int) : Result

  /**
   *
   * @return the name of the agent
   */
  override def name: String = s"ScriptAgent {$path}"

  private val extSet : mutable.Set[Process] = new mutable.HashSet[Process]()


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
      val b = new StringBuilder
      try{
        contextToTPTP(t1.readSet()) foreach {out =>
          b.append(out.output+"\n")
          writer.println(out.output)}
      } finally writer.close()
      Out.trace(s"[$name]: Writing to temporary file:\n"+b.toString())
      //Executing the prover
      var success = true
        Out.trace(s"[$name]: Executing $path on file ${file.getAbsolutePath}")

        // -------------------------------------------------------------
        //   Execution
        // -------------------------------------------------------------
        //val res = Seq(s"${exec.getAbsolutePath}", file.getAbsolutePath).lines
        val res = Seq(s"${exec.getAbsolutePath}", file.getAbsolutePath)
        var str : Iterator[String] = null
        val process = res.run(new ProcessIO(in => in.close(), // Input not used
                                            stdout => {str = scala.io.Source.fromInputStream(stdout).getLines(); stdout.close()},
                                            err => err.close())
                              )
        extSet.synchronized(extSet.add(process))
        Out.trace(s"[$name]: Got result from external prover.")
        val erg = str.toStream
        // Filter for exit code
        // TODO: Insert error stream
//        res foreach {l => Console.info(l)}
        val h = handle(erg.init, Stream.empty, erg.last.toInt)

        // CLean up! I.e. process
        extSet.synchronized(extSet.remove(process))
        process.destroy()                     // In case we finished early and did not read till the end.
        return h
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

  /**
   * The script agent terminates all external processes if the kill command occures.
   */
  override def kill() = extSet.synchronized{
    extSet foreach {p => p.destroy()}
    extSet.clear()
  }; super.kill()
}

class ScriptTask(fs : Set[FormulaStore]) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget

  override val pretty : String = "ScriptTask (BIG)"
  override val name : String = "Script Call"
}

