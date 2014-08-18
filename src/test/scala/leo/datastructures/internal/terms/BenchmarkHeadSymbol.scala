package leo.datastructures.internal.terms

import leo.datastructures.internal.terms.Term._
import leo.datastructures.internal.terms.Term.{mkTermApp => ap, mkTypeApp => tyAp}
import leo.datastructures.internal.terms.Type._
import leo.datastructures.internal.Signature
import leo.modules.churchNumerals.Numerals
import Numerals._
import java.io.{FileNotFoundException, File}

/**
 * Created by lex on 13.08.14.
 */
object BenchmarkHeadSymbol {

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
            println("Loaded " + fileAbs)
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

  val FILE = "benchmark/s4-const-GAL015+1.p.sem"
  val REP = 50



  private def benchmark(name: String, term: Term, role: String): Long = {
    print(s"Benchmarking $name: \t")
    var t: Long = 0
//    for(i <- 1 to REP) {
      t+= time(term.headSymbol)._2
//    }
    println("%d microseconds on avg".format(t))
    t
  }

  def main(args: Array[String]) {
//    Numerals()
    val sig = Signature.get
    val fs = load(FILE)

//    // Print signature
//    println("###########################")
//    println("Signature:")
//    for (s <- sig.allConstants) {
//      print(sig(s).key.toString + "\t\t")
//      print(sig(s).name + "\t\t:\t")
//      sig(s).ty.foreach({ case ty => print(ty.pretty)})
//      sig(s).kind.foreach({ case ty => print(ty.pretty)})
//      println()
//    }
//    println("###########################")
//    // Print parsed formulae
//    println("Parsed normalized formulae:")
//    fs.foreach({case (name, term, role) =>
//      println(s"$name \t $role \t\t ${term.betaNormalize.pretty}")
//    })
    println("###########################")
//
    // Expand definitions
    println("normalized formuluae:")
    val fs2 = fs.map({case (name, term, role) => (name, term.betaNormalize ,role)})
    fs2.foreach({case (name, term, role) =>
      println(s"$name \t $role \t\t ${term}")
    })
    println("###########################")

    // Benchmark beta normalization
    println("benchmark beta NF")
    var time: Long = 0
    fs2.foreach({case (n, t, r) => time += benchmark(n,t,r)})
    println(s"Time overall: $time")
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