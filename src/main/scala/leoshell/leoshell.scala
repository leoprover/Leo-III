package leoshell

import parsers._
import tptp._
import tptp.Commons.{AnnotatedFormula => Formula}
import scala.io.Source
import scala.collection.mutable
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

  // For the help Text to align
  var longestName : Int = 0

  val welcome = "\n" +
    "Welcome to Leo-III\n" +
    "--------------------\n" +
    "Call 'help' for help\n"


  val imports : List[String] = List("leoshell._", "commands._", "tptp.Commons._", "tptp.Commons.{AnnotatedFormula=>Formula}")

  /**
   * If smth has to be done
   */
  def init {
    Help.init()
    Info.init()
    Context.init()
    Add.init()
    Load.init()
    Get.init()
    Clear.init()
    Remove.init()
  }

  def addCommand(c : Command) {
    commandList.put(c.name, c)
    longestName = math.max(longestName, c.name.length)
  }

}

/**
 * At the moment a bit silly but later one we will use the blackboard anyway, so from the
 * outside the access should not differ that much.
 */
object FormulaHandle {
  protected[leoshell] var formulaMap: mutable.HashMap[String, (String, Formula)] = new mutable.HashMap[String, (String, Formula)]()

  def addFormula(f : Formula) {
    formulaMap.put(f.name, (f.role, f))
    return
  }

  def addFormulaString(s : String) {
    TPTP.parseFormula(s) match {
      case Some(a)  => {
        addFormula(a)
        println("Added '"++a++"' to the context.")
      }
      case None     => println("'"++s ++ "' is not a valid formula.")
    }
    return
  }

  def removeFormula(name : String)  {

    formulaMap.remove(name) match {
      case Some((a,b))  => println("Removed '"++b.toString++"' from formula set")
      case None         => println("There is no formula '"++name++"'\nNothing was removed.")
    }
    return
  }

  def clearContext {
    formulaMap.clear()
    println("Context cleared")
    return
  }

  def getFormula(name: String) : Formula = formulaMap.apply(name)._2

}
