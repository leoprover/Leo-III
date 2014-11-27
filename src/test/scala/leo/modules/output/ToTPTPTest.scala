package leo.modules.output

import java.io.{FileNotFoundException, File}
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import leo.datastructures.{Clause, Role, Role_Type, Role_Definition, Role_Unknown}

/**
 * Created by lex on 10.11.14.
 */
object ToTPTPTest {
  private var _pwd : String = new File(".").getCanonicalPath

  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  def load(file: String): Seq[(String, Clause, Role)] = {
    if (file.charAt(0) != '/') {
      // Relative load
      loadRelative(file, _pwd.split('/'))
    } else {
      // Absolute load
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]): Seq[(String, Clause, Role)] = {
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
            Seq()
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => loadRelative(x._1, path))
            //            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            //            processed foreach { case (name, form, role) => if(role != "definition" && role != "type")
            //              benchmark(name, form, role)
            //            }
            processed.filter({case (_, _, role) => role != Role_Definition && role != Role_Type && role != Role_Unknown})
        }

      } catch {
        case ex : FileNotFoundException =>
          println(s"'$fileAbs' does not exist.")
          Seq()
      }
    } else {
      Seq()
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


  val __TPTPPROBLEMPATH__ = "/home/lex/Downloads/TPTP-v6.0.0/Problems/"

  //  val FILES = {new File("benchmark").listFiles.filter(_.getName.endsWith(".p.sem")).toSet ++
  //               new File("benchmark").listFiles.filter(_.getName.endsWith(".p.syn")).toSet }

  val FILES = {new File(__TPTPPROBLEMPATH__ + "QUA").listFiles.filter(x => !(x.getName.contains("-")) && x.getName.endsWith(".p")).toSet}



  def test(file: File) {
    val sig = Signature.get
    print(s"${file.getName} : ")
    val fs = load(file.getAbsolutePath)

    for (f <- fs) {
      println(ToTPTP.output(f._1, f._2, f._3))
    }

  }


  def main(args: Array[String]) {
    val sig = Signature.get
    // Files
    for(f <- FILES.take(2)) {
      Signature.resetWithHOL(sig)
      loadedSet.clear()
      test(f)
    }
  }

}
