package leo.agents
package impl

import leo.datastructures.{ClauseProxy, blackboard}
import leo.datastructures.context.Context
import leo.datastructures.blackboard.{DataType, Result, ClauseType}
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
abstract class ScriptAgent(path : String) extends AbstractAgent {
  override val interest : Option[Seq[DataType]] = None

  def handle(input: Iterator[String], err: Iterator[String], retValue: Int, orgClauses : Set[ClauseProxy]): blackboard.Result

  def encode(fs: Set[ClauseProxy]): Seq[String]


  /**
    * Searches the Blackboard for possible tasks on initialization.
    *
    * @return All initial available tasks
    */
  override def init(): Iterable[Task] = Seq()

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



  final case class ScriptTask(script : String, fs: Set[ClauseProxy], a : ScriptAgent) extends Task {
    override val readSet: Map[DataType, Set[Any]] = Map()

    override val writeSet: Map[DataType, Set[Any]] = Map()

    override def bid: Double = 1 // TODO Better value

    override val pretty: String = s"ScriptTask($script, numerOfClauses = ${fs.size})"
    override val name: String = "Script Call"

    override val getAgent : ScriptAgent = a

    /**
      * This function runs the specific agent on the registered Blackboard.
      */
    override def run: Result = {
      val process : ExternalResult = ExternalCall.exec(script, encode(fs))
      extSet.synchronized(extSet.add(process))
      val retValue = process.exitValue
      val out = process.out
      val err = process.error
      val res = a.handle(out, err, retValue, fs)
      extSet.synchronized(extSet.remove(process))
      res
    }

    override val toString : String = s"ScriptTask($script,  numerOfClauses = ${fs.size})"
  }

}

