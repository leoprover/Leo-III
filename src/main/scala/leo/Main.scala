package leo

import java.io.{File, FileNotFoundException}

import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.agents.impl.UtilAgents._
import leo.datastructures.internal.Signature

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
   * @param args - 1. Argument : TIMEOUT in
   *               2. Argument : absolute Path to TPTP-File
   */
  def main(args : Array[String]){

    val timeout = args(0).toInt
    val path = args(1)

    // Initializing Blackboard
    StdAgents()

    load(path)

    Scheduler().signal()
    delayKill(timeout)
  }

  def delayKill(t : Int) : Unit  = new Thread(new Runnable {
    override def run(): Unit = {
      synchronized{
        try {
          wait(t * 1000)
          Scheduler().killAll()
        } catch {
          case _ => Scheduler().killAll()
        }
      }
    }
  }).start()


  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  private val _pwd = (new File(".")).getCanonicalPath

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  private def load(file: String): Unit = {
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
            println("Parse error in file " + fileAbs + ": " + x)
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            processed foreach { case (name, form, role) => if(role != "definition" && role != "type")
              Blackboard().addFormula(name, form, role)
            }
        }

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

}

