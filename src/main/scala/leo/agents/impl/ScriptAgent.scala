package leo.agents
package impl

import leo.datastructures.blackboard
import leo.datastructures.context.Context
import leo.datastructures.blackboard.{DataType, AnnotatedClause, Result, FormulaType}
import java.io.{PrintWriter, File}
import leo.modules.external.{ExternalCall, ExternalResult}
import leo.modules.output.{ToTPTP, Output}
import leo.modules.output.logger._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer

import scala.sys.process._
import java.io.IOException

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
abstract class ScriptAgent(path : String) extends Agent {

  def handle(c: Context, input: Iterator[String], err: Iterator[String], retValue: Int): blackboard.Result

  def encode(fs: Set[AnnotatedClause]): Seq[String]

  /**
    *
    * @return the name of the agent
    */
  override def name: String = s"ScriptAgent {$path}"

  private val extSet: mutable.Set[ExternalResult] = new mutable.HashSet[ExternalResult]()


  /**
    * The script agent terminates all external processes if the kill command occures.
    */
  override def kill() = extSet.synchronized {
    super.kill()
    val it = extSet.iterator
    while(it.hasNext){
      val next = it.next()
      next.kill()
    }
    extSet.clear()
  }



  final case class ScriptTask(script : String, fs: Set[AnnotatedClause], c: Context, a : ScriptAgent) extends Task {
    override def readSet: Map[DataType, Set[Any]] = Map.empty[DataType, Set[Any]] + (FormulaType -> fs.asInstanceOf[Set[Any]])

    override def writeSet(): Map[DataType, Set[Any]] = Map.empty

    override def bid: Double = 1 // TODO Better value

    override val pretty: String = "ScriptTask (BIG)"
    override val name: String = "Script Call"

    override val getAgent : ScriptAgent = a

    /**
      * This function runs the specific agent on the registered Blackboard.
      */
    override def run: Result = {
      val process : ExternalResult = ExternalCall.run(script, encode(fs))
      extSet.synchronized(extSet.add(process))

      val retValue = process.exitValue
      val out = process.out
      val err = process.error
      a.handle(c, out, err, retValue)
    }
  }

}

