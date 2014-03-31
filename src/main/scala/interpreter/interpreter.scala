package interpreter

import parsers._
import tptp._
import tptp.Commons.{AnnotatedFormula => Formula}
import scala.io.Source
import scala.collection.mutable

/**
 * Created by ryu on 3/28/14.
 */
package object interpreter {

  val welcome = "\n" +
    "Welcome to Leo-III\n" +
    "--------------------\n" +
    "Call 'help' for help\n"

  def help {
    println("help     :   Shows this list")
    println("info     :   Displays info to a specific command")
    println("load     :   loads a tptp file and saves the formulas")
    println("add      :   Adds formula.")
    println("context  :   Lists all formulas")
    println("get      :   Shows a specific formula")
    println("clear    :   Clears the context")
    println("rm       :   Removes a formula")
  }

  def info(ask : String) {
    println(ask+":")
    ask match {
      case "info" => println("Displays a more detailed description of a command")
      case "load" => println("Given a filename, the file is loaded and then parsed into the TPTP format.")
      case "add"  => println("Adds a formula string in tptp syntax to the current context")
      case "help" => println("Shows a list of commands to leoIII")
      case "context" => println("Shows all forumlas in the current context")
      case "get"  => println("Returns the formula with the given name in the context")
      case "rm"   => println("Removes the formula with the given name from the context")
      case "clear" => println("Clears all formulas from the current context")
      case _      => println("No recognized command found.\nType 'help' for a list of commands.")
    }
  }

  /**
   * Loading a file + its includes.
   * No circle detection or double imports removed at the time.
   * No relative paths either,
   */
  def load (file : String) {
    val source = scala.io.Source.fromFile(file, "utf-8")
    val input = source.getLines().mkString("\n")

    TPTP.parseFile(input).foreach(x => {
      x.getFormulae.foreach(x => FormulaHandle.formulaMap.put(x.name, (x.role, x)))
      x.getIncludes.foreach(x => load(x._1))
    })
    source.close()
  }

  def add(s : String) = FormulaHandle.addFormulaString(s)

  def get(s : String) = FormulaHandle.getFormula(s)

  def display = {
    println("Name\t\tRole\t\tFormula");
    println("-----------------------------------")
    FormulaHandle.formulaMap.foreach(x => print(x._1 + "\t\t" + x._2._1 + "\t\t" + x._2._2))
    println()}

  def clear = FormulaHandle.clearContext

  def rm(s : String) = FormulaHandle.removeFormula(s)
}

/**
 * At the moment a bit silly but later one we will use the blackboard anyway, so from the
 * outside the access should not differ that much.
 */
object FormulaHandle {
  protected[interpreter] var formulaMap: mutable.HashMap[String, (String, Formula)] = new mutable.HashMap[String, (String, Formula)]()

  def addFormula(f : Formula) = formulaMap.put(f.name, (f.role, f))

  def addFormulaString(s : String) = addFormula(TPTP.parseFormula(s).get)

  def removeFormula(name : String) = formulaMap.remove(name)

  def clearContext = formulaMap.empty

  def getFormula(name: String) : Formula = formulaMap.apply(name)._2

}
