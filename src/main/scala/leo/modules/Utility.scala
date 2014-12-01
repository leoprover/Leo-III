package leo
package modules

import java.io.{FileNotFoundException, File}

import leo.datastructures.{Role_Definition, Role_Unknown, Role_Type}
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.modules.output.{Output, SZS_SyntaxError, SZS_InputError, StatusSZS}

/**
 * Stuff to do smth.
 *
 * @author Max Wisniewski
 * @since 12/1/14
 */
object Utility {
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
            throw new SZSException(SZS_SyntaxError)
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
            //            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown)
              Blackboard().addFormula(name, form, role, Context())
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
                  throw new SZSException(SZS_SyntaxError)
                case Right(x) =>
                  loadedSet += fileAbs
                  x.getIncludes.foreach(x => loadRelative(x._1, path))
                  //            println("Loaded " + fileAbs)
                  val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
                  processed foreach { case (name, form, role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown)
                    Blackboard().addFormula(name, form, role, Context())
                  }
              }

            } catch {
              case ex : FileNotFoundException => throw new SZSException(SZS_InputError)
              case _ : Throwable => throw new SZSException(SZS_InputError)
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

  /**
   * Parses and adds a TPTP formula.
   */
  def add(s: String): Unit = {
    import leo.modules.parsers.TPTP
    import leo.modules.parsers.InputProcessing

    TPTP.parseFormula(s) match {
      case Right(a) =>
        val processed = InputProcessing.process(Signature.get)(a)
        processed.foreach {case (name,form,role) => if(role != Role_Definition && role != Role_Type && role != Role_Unknown) Blackboard().addFormula(name,form,role, Context())}
      case Left(err) =>
        Out.severe(s"'$s' is not a valid formula: $err")
    }
  }
}

class SZSException(val status : StatusSZS) extends RuntimeException("SZS status "+status.output)

case class SZSOutput(status : StatusSZS) extends Output {
  override def output: String = "% SZS status "+status.output
}
