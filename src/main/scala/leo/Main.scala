package leo

import java.io.{File, FileNotFoundException}

import leo.agents.impl.FinishedAgent
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.agents.impl.UtilAgents._
import leo.datastructures.impl.Signature
import leo.modules.CLParameterParser

/**
 * Entry Point for Leo-III as an executable to
 * proof a TPTP File
 *
 * @author Max Wisniewski
 * @since 7/8/14
 */
object Main {

  /**
   *
   * Tries to proof a Given TPTP file in
   * a given Time.
   *
   * @param args - See [[Configuration]] for argument treatment
   */
  def main(args : Array[String]){
    Configuration.init(new CLParameterParser(args))
    val timeout = Configuration.TIMEOUT
    val path = Configuration.PROBLEMFILE

    // Initializing Blackboard
    StdAgents()
    (new FinishedAgent(timeout)).register()

    load(path)

    Scheduler().signal()
  }

  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  private val _pwd = (new File(".")).getCanonicalPath

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  def load(file: String): Unit = {
    if (file.charAt(0) != '/') {
      // Relative load
      loadRelative(file, _pwd.split('/'))
    } else {
      // Absolute load
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]): Unit = {
    import scala.util.parsing.input.CharArrayReader
    import leo.modules.parsers.TPTP
    import leo.modules.parsers.InputProcessing


    val (fileAbs, path) = newPath(rel, file)
    if (!loadedSet(fileAbs)) {
      try {
        val source = scala.io.Source.fromFile(fileAbs, "utf-8")
        val input = new CharArrayReader(source.toArray)
        val parsed = TPTP.parseFile(input)
        source.close()    // Close at this point. Otherwise we would have many files open with many includes.

        parsed match {
          case Left(x) =>
//            println("Parse error in file " + fileAbs + ": " + x)
              println("% SZS status SyntaxError")
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
//            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) => if(role != "definition" && role != "type")
              Blackboard().addFormula(name, form, role)
            }
        }

      } catch {
        case ex : FileNotFoundException =>
          // If not relative, then search in TPTP env variable
          val tptpHome = System.getenv("TPTP").split("/")
          val (fileAbs, path) = newPath(tptpHome, file)
          if (!loadedSet(fileAbs)) {
            try {
              val source = scala.io.Source.fromFile(fileAbs, "utf-8")
              val input = new CharArrayReader(source.toArray)
              val parsed = TPTP.parseFile(input)
              source.close()    // Close at this point. Otherwise we would have many files open with many includes.

              parsed match {
                case Left(x) =>
                  //            println("Parse error in file " + fileAbs + ": " + x)
                  println("% SZS status SyntaxError")
                case Right(x) =>
                  loadedSet += fileAbs
                  x.getIncludes.foreach(x => loadRelative(x._1, path))
                  //            println("Loaded " + fileAbs)
                  val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
                  processed foreach { case (name, form, role) => if(role != "definition" && role != "type")
                    Blackboard().addFormula(name, form, role)
                  }
              }

            } catch {
              case ex : FileNotFoundException =>
                println("% SZS status InputError")
              case _ : Throwable => println("% SZS status Inappropriate")
            }
          }
      }
    }
  }

  private def parseAbsolute(fileAbs : String, path : String) : Unit = {

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

}

