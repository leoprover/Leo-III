package interpreter

import parsers._
import tptp._
import tptp.Commons.{AnnotatedFormula => Formula}

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
      case "get"  => println("Returns the i-th formula in the context")
      case "rm"   => println("Removes the i-th formula in the context")
      case "clear" => println("Clears all formulas from the current context")
      case _      => println("No recognized command found.\nType 'help' for a list of commands.")
    }
  }

  /**
   * Calling
   */
  def load (file : String) = ???

  def add(s : String) = FormulaHandle.addFormulaString(s)

  def get(i : Int) = FormulaHandle.getFormula(i)

  def display = FormulaHandle.formula

  def clear = FormulaHandle.clearContext

  def rm(i : Int) = FormulaHandle.removeFormula(i)
}

/**
 * At the moment a bit silly but later one we will use the blackboard anyway, so from the
 * outside the access should not differ that much.
 */
object FormulaHandle {
  protected[interpreter] var formula : List[Formula] = Nil

  def addFormula(f : Formula) = formula = f :: formula

  def addFormulaString(s : String) = TPTP.parseFormula(s).foreach(x => x :: formula)

  def removeFormula(i : Int) = formula.take(i) ++ formula.drop(i+1)

  def clearContext = formula = Nil

  def getFormula(i : Int) : Formula = formula.apply(i)

}
