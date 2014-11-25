package leo.datastructures.internal.terms

import leo.datastructures._
import leo.datastructures.term._
import Term._
import Term.{mkTermApp => ap, mkTypeApp => tyAp}
import Type._
import leo.datastructures.impl.Signature
import leo.modules.churchNumerals.Numerals
import Numerals._
import java.io.{FileNotFoundException, File}

/**
 * Created by lex on 13.08.14.
 */
object BenchmarkBound {

  private var _pwd : String = new File(".").getCanonicalPath

  /**
   * List of currently loaded tptp files
   */
  private val loadedSet = collection.mutable.Set.empty[String]

  /**
   * Loads a tptp file and saves the formulas in the context.
   */
  def load(file: String): Seq[(String, Term, String)] = {
    if (file.charAt(0) != '/') {
      // Relative load
      loadRelative(file, _pwd.split('/'))
    } else {
      // Absolute load
      val pwd = file.split('/')
      loadRelative(pwd.last, pwd.init)
    }
  }

  private def loadRelative(file : String, rel : Array[String]): Seq[(String, Term, String)] = {
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
            processed.filter({case (_, _, role) => role != "definition" && role != "type"})
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

  def time[A](a: => A): (A, Long) = {
    val now = System.nanoTime
    val result = a
    val micros = (System.nanoTime - now) / 1000
    //       println("%d microseconds".format(micros))
    (result,micros)
  }
  val __TPTPPROBLEMPATH__ = "/home/lex/Downloads/TPTP-v6.0.0/Problems/"
  //  val FILE = __TPTPPROBLEMPATH__ + "NUM/NUM542+2.p"
  val FILE = "benchmark/s4-cumul-GSE014+4.p.syn"

  //  val FILES = {new File("benchmark").listFiles.filter(_.getName.endsWith(".p.sem")).toSet ++
  //               new File("benchmark").listFiles.filter(_.getName.endsWith(".p.syn")).toSet }

  val FILES = {new File(__TPTPPROBLEMPATH__ + "QUA").listFiles.filter(x => !(x.getName.contains("-")) && x.getName.endsWith(".p")).toSet}

  private def benchmark(name: String, term: Term, role: String): Long = {
    //    print(s"Benchmarking $name: \t")
    Reductions.reset()
    term.betaNormalize
    Reductions()
    //    val t: Long = time(term.betaNormalize)._2

    //    println("%d microseconds".format(t))
    //    t
  }

  var times: Map[String, Seq[Long]] = Map()



  var numberOfsubtreesWithPosIdx: Int = 0
  var subtreeWithPosIdxSizes : Seq[Int] = Seq.empty
  var minSize = Int.MaxValue
  var maxSize = 0

  def doit2(term: Term): Unit = {
    term.scopeNumber match {
      case (a,b) if a >= 0 && b >= 0 && !term.isTermAbs=> {
       numberOfsubtreesWithPosIdx += 1
       subtreeWithPosIdxSizes = subtreeWithPosIdxSizes :+ term.size
       minSize = Math.min(minSize, term.size)
        maxSize = Math.max(maxSize, term.size)
      }
      case _ => term match {
        case Bound(t,scope) => ()
        case Symbol(id)     => ()
        case s @@@ t        => doit2(s); doit2(t)
        case f ∙ args       => doit2(f); args.foreach(_ match { case Left(t) => doit2(t)
                                                                case Right(_) => ()})
        case s @@@@ ty      => doit2(s)
        case ty :::> s      => doit2(s)
        case TypeLambda(t)  => doit2(t)
      }
    }


  }

  def doit(file: File): Unit = {
    val sig = Signature.get
    print(s"${file.getName} : ")
    val fs = load(file.getAbsolutePath)



    fs.foreach({case (name, term, role) =>
      doit2(term)

    })

    var avg: Float = 0
    subtreeWithPosIdxSizes.foreach( a => {avg = avg + a})
    avg = avg/(subtreeWithPosIdxSizes.size.toFloat)

    println(s"$numberOfsubtreesWithPosIdx \t $minSize \t $maxSize \t $avg")
    numberOfsubtreesWithPosIdx = 0
    subtreeWithPosIdxSizes = Seq.empty
    minSize = Int.MaxValue
    maxSize = 0

  }

  def main(args: Array[String]) {
    var all: Long = 0
    val sig = Signature.get
    // Files


    println("#Subtrees \t min size \t max size \t avg size")
    for(f <- FILES) {
      Signature.resetWithHOL(sig)
      loadedSet.clear()
      doit(f)
    }

  }


  //
  //    val a = fromInt(10)
  //    val b = fromInt(10)
  //
  //    var t: Long = 0
  //    val rep = 20
  //    for(i <- 1 to rep) {
  //      t += time(mult(a,b))._2
  //    }
  //    println("%d microseconds on avg".format(t / rep))
  //
  //  }
}