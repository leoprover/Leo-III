package leoshell

import scala.collection.mutable
import scala.tools.nsc.interpreter.ILoop

import commands._

/**
 * Interface for the leo shell to use the functions of leoIII
 * @author{Max Wisniewski}
 */
package object leoshell {
  /**
   * List of all registered commands
   */
  var commandList : mutable.Map[String, Command] = new mutable.HashMap[String, Command]()

  /**
   * List of currently loaded tptp files
   */
  val loadedSet : mutable.Set[String] = new mutable.HashSet[String]()

  var console : ILoop = null

  // For the help Text to align
  var longestName : Int = 0

  val welcome = "\n" +
    "Welcome to Leo-III\n" +
    "--------------------\n" +
    "Call 'help' for help\n"


  val imports : List[String] = List("leoshell._", "commands._", "datastructures.tptp.Commons._", "datastructures.tptp.Commons.{AnnotatedFormula=>Formula}")

  /**
   * If smth has to be done
   */
  def init (console : ILoop) {
    Help.init()
    Info.init()
    Context.init()
    Add.init()
    Load.init()
    Get.init()
    Clear.init()
    Remove.init()
    Parse.init()
    Normalize.init()
    this.console = console
  }

  def addCommand(c : Command) {
    commandList.put(c.name, c)
    longestName = math.max(longestName, c.name.length)
  }

}


