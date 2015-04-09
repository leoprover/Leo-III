package leo.agents
package impl

import leo.datastructures.context.Context
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
import scala.collection.mutable.ListBuffer

import scala.sys.process._
import java.io.IOException

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

  def handle(c : Context, input : Iterator[String], err : Iterator[String], errno : Int) : Result

  /**
   *
   * @return the name of the agent
   */
  override def name: String = s"ScriptAgent {$path}"

  private val extSet : mutable.Set[Process] = new mutable.HashSet[Process]()

  private val exec : File = new File(path)

//  private val exec : File = {
//    val f = File.createTempFile(path,".sh")
//    f.deleteOnExit()
//    val writer = new PrintWriter(f)
//    try{
//      writer.println("#!/bin/sh")
//      writer.println(path+" $1")
//      writer.println("echo $?")
//      writer.println("exit 0")
//    } finally writer.close()
//    Process(s"chmod u+x ${f.getAbsolutePath}").!
//    f
//  }

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
        val str : mutable.ListBuffer[String] = new ListBuffer[String]
        val errstr : mutable.ListBuffer[String] = new ListBuffer[String]
        val process = res.run(new ProcessIO(in => in.close(), // Input not used
                                            stdout => try{
                                              {scala.io.Source.fromInputStream(stdout).getLines().foreach(str.append(_)); stdout.close()}
                                            } catch {
                                              case e : IOException => stdout.close()
                                            },
                                            err => try {
                                              {scala.io.Source.fromInputStream(err).getLines().foreach(errstr.append(_)); err.close()}
                                            } catch {
                                              case e : IOException => err.close()
                                            }
                              ))
        extSet.synchronized(extSet.add(process))
        val exit = process.exitValue()
        Out.trace(s"[$name]: Got result from external prover.")

        val h = handle(t1.c, str.toIterator, errstr.toIterator, exit)

        // CLean up! I.e. process
        extSet.synchronized(extSet.remove(process))
        process.destroy()                     // In case we finished early and did not read till the end.
        return h
    case _  => Out.info(s"[$name]: Recevied a wrong task $t.")
      return EmptyResult
  }



  private def contextToTPTP(fS : Set[FormulaStore]) : Seq[Output] = ToTPTP(fS)



  /**
   * The script agent terminates all external processes if the kill command occures.
   */
  override def kill() = extSet.synchronized{
    extSet foreach {p => p.destroy()}
    extSet.clear()
  }; super.kill()
}

class ScriptTask(val fs : Set[FormulaStore], val c : Context) extends Task {
  override def readSet(): Set[FormulaStore] = fs
  override def writeSet(): Set[FormulaStore] = Set.empty
  override def bid(budget: Double): Double = budget

  override val pretty : String = "ScriptTask (BIG)"
  override val name : String = "Script Call"
}

