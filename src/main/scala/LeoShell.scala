
import parsers._
import java.io.FileNotFoundException
import java.io.File

import scala.util.parsing.input.CharArrayReader
import datastructures.tptp.Commons.AnnotatedFormula
import normalization.{Simplification, NoneSenseSimplify}
import blackboard.SimpleBlackboard

/**
 * Addition commands for an interactive session with the sbt cosole.
 *
 * May commands printing additional information to the console and returning
 * null to indicate an error, it isn't recommended to use LeoShell in a
 * productive environment.
 */
object LeoShell {

  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  def load(file: String): Unit = {
    if (file.charAt(0) != '/') {
      val pwd = new File(".").getCanonicalPath
      loadRelative(file, pwd.split('/'))
    } else {
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]): Unit = {
    val (fileAbs, path) = newPath(rel, file)
    if (!loadedSet(fileAbs)) {
      try {
        val source = scala.io.Source.fromFile(fileAbs, "utf-8")
        val input = new CharArrayReader(source.toArray)
        val parsed = TPTP.parseFile(input)

        parsed match {
          case Left(x) =>
            println("Parse error in file " + fileAbs + ": " + x)
          case Right(x) =>
            x.getFormulae foreach SimpleBlackboard.addFormula
            println("Loaded " + fileAbs)
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
        }
        source.close()
      } catch {
        case ex : FileNotFoundException =>
          println(s"'$fileAbs' does not exist.")
      }
    }
  }

  /**
   * Returns the new absolute Path and the absolute directory
   *
   * @param oldDir - Old absolute Path to directory
   * @param relPath - relative path to new file
   */
  private def newPath(oldDir : Array[String], relPath : String) : (String, Array[String]) = {
    val relSplit  = relPath.split('/')
    val path = oldDir.take(oldDir.length - relSplit.count(_ == ".."))
    val absPath = path ++ relSplit.dropWhile(x => x == "..")
    (absPath.mkString("/"), absPath.init)
  }

  /**
   * Parses and adds a TPTP formula.
   */
  def add(s: String): Unit = {
    TPTP.parseFormula(s) match {
      case Right(a) =>
        add(a);
      case Left(err) =>
        println(s"'$s' is not a valid formula: $err")
    }
  }

  def add(s : AnnotatedFormula): Unit = {
    SimpleBlackboard.addFormula(s)
    println(s"Added '$s' to the context.")
  }

  /**
   * Returns the formula with the given name in the context.
   */
  def get(s: String) =
    SimpleBlackboard.getFormulaByName(s).
    getOrElse{
      println(s"There is no formula named '$s'.")
      null
    }

  def exit() = System.exit(0)

  /**
   * Shows all formulas in the current context.
   */
  def context(): Unit = {
    val maxSize = 80
    val maxNameSize = 25
    val maxRoleSize = 15
    val maxFormulaSize = maxSize -(maxNameSize + maxRoleSize + 6)

    println("Name" + " "*(maxNameSize-4) + " | Role" + " "*(maxRoleSize-4) + " | Formula")
    println("-"*maxSize)
    SimpleBlackboard.getFormulas().foreach{x =>
      val name = x.name.toString.take(maxNameSize)
      val role = x.role.toString.take(maxRoleSize)
      val form = x.f.toString
      val form1 = form.take(maxFormulaSize)
      val form2 = form.drop(maxFormulaSize).sliding(maxFormulaSize, maxFormulaSize)

      val nameOffset = maxNameSize - name.length
      val roleOffset = maxRoleSize - role.length
      println(name + " "*nameOffset + " | " + role + " "*roleOffset + " | " + form1)
      form2.foreach(x => println(" "*maxNameSize+" | "+ " "*maxRoleSize+ " | "+ x))
    }
    println()
  }

  /**
   * Deletes all formulas from the current context.
   */
  def clear(): Unit = {
    SimpleBlackboard.rmAll(_ => true)
    loadedSet.clear()
  }

  /**
   * Deletes a formula by name from the context.
   */
  def rm(s: String) {
    if (SimpleBlackboard.rmFormulaByName(s))
      println(s"Removed $s from the context.")
    else
      println(s"There was no $s. Removed nothing.")
  }

  /**
   * Parse a TPTP Formula.
   */
  def parse(s : String): AnnotatedFormula = {
    TPTP.parseFormula(s) match {
      case Right(x) => x
      case Left(err)   =>
        println(s"'$s' is not a valid formula: $err")
        null
    }
  }

  def simplify(f : AnnotatedFormula) : AnnotatedFormula = Simplification(f)
}

