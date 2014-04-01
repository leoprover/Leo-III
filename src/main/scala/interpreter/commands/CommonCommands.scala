package interpreter.commands

import tptp._
import parsers._
import interpreter._
import scala.collection.mutable
import java.io.FileNotFoundException

/**
 * Help Command
 * @author{Max Wisniewski}
 */
object Help extends Command {
  val name = "help"
  val infoText = "Shows a list of commands to leoIII"
  val helpText = "Shows this list"
  val initText = "def " + name + " = commands.Help.help"

  def init () = interpreter.addCommand(this)

  val front = 5
  val rear = 3

  def help {
    interpreter.commandList.foreach {
      case (n, x) => {
        val offset = interpreter.longestName - x.name.length + front
        println(x.name + (" "*offset) + ":" + (" "*rear) +x.helpText)
      }
    }
  }
}

object Info extends Command {
  val name = "info"
  val infoText = "Displays a more detailed description of a command"
  val helpText = "Displays info to a specific command"
  val initText = "def " + name + "(ask : String) = commands.Info.info(ask)"

  def init () = interpreter.addCommand(this)

  def info(ask: String) {
    println(ask + ":")
    interpreter.commandList.get(ask).fold(println(ask + " is not a command."))(x => println(x.infoText))
  }
}

object Load extends Command {

  val name = "load"
  val infoText = "Given a filename, the file is loaded and then parse into the TPTP format."
  val helpText = "Loads a tptp file and saves the formulas in the context"
  val initText = "def " + name + " (file : String) = commands.Load.load(file)"

  def init () = interpreter.addCommand(this)

  // Stores already loaded paths

  /**
   * Loading a file + its includes.
   */
  def load(file: String) {
    if (file.charAt(0) != '/') {
      val pwd = new java.io.File(".").getCanonicalPath
      loadRelative(file, pwd.split('/'))
    } else {
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]) {
    val (fileAbs, path) = newPath(rel, file)
    if (!interpreter.loadedSet.contains(fileAbs)) {
      try {
        val source = scala.io.Source.fromFile(fileAbs, "utf-8")
        val input = source.getLines().mkString("\n")

        println("Loaded " + fileAbs)
        interpreter.loadedSet.add(fileAbs)


        TPTP.parseFile(input).foreach(x => {
          x.getFormulae.foreach(x => FormulaHandle.formulaMap.put(x.name, (x.role, x)))
          x.getIncludes.foreach(x => loadRelative(x._1, path))
        })
        source.close()
      } catch { case ex : FileNotFoundException => println("'" + fileAbs + "' does not exist.")}
    }
  }

  /**
   *
   * Returns the new absolute Path and the absolute directory
   *
   * @param oldDir - Old absolute Path to directory
   * @param relPath - relative path to new file
   * @return
   */
  private def newPath(oldDir : Array[String], relPath : String) : (String, Array[String])  = {
    val relSplit  = relPath.split('/')
    var path = oldDir.take(oldDir.length - relSplit.filter(x => x == "..").length)
    path = path ++ relSplit.dropWhile(x => x == "..")
    return (path.mkString("/"), path.init)
  }
}

object Add extends Command {
  val name = "add"
  val infoText = "Adds an annotated formula in tptp syntax to the current context."
  val helpText = "Adds a formula."
  val initText = "def " + name + " (f : String) = commands.Add.add(f)"

  def init () = interpreter.addCommand(this)

  def add(s: String) = FormulaHandle.addFormulaString(s)
}

object Get extends Command {
  val name = "get"
  val infoText = "Returns the formula with the given name in the context."
  val helpText = "Shows a specific formula"
  val initText = "def " + name + " (s : String) = commands.Get.get(s)"

  def init () = interpreter.addCommand(this)

  def get(s: String) = FormulaHandle.getFormula(s)
}

object Context extends Command {
  val name = "context"
  val infoText = "Shows all forumlas in the current context."
  val helpText = "Lists all formulas"
  val initText = "def " + name + " = commands.Context.context"

  def init () = interpreter.addCommand(this)

  def context = {
    println("Name\t\tRole\t\tFormula");
    println("-----------------------------------")
    FormulaHandle.formulaMap.foreach(x => println(x._1 + "\t\t" + x._2._1 + "\t\t" + x._2._2))
    println()
  }
}

object Clear extends Command {
  val name = "clear"
  val infoText = "Deletes all formulas from the current context."
  val helpText = "Clears the context"
  val initText = "def " + name + " = commands.Clear.clear"

  def init () = interpreter.addCommand(this)

  def clear {
    FormulaHandle.clearContext
    interpreter.loadedSet.clear()
  }
}

object Remove extends Command {
  val name = "rm"
  val infoText = "Deletes a formula by name from the context"
  val helpText = "Removes a formula"
  val initText = "def " + name + " (s : String) = commands.Remove.rm(s)"

  def init () = interpreter.addCommand(this)

  def rm(s: String) = FormulaHandle.removeFormula(s)
}
