package leo.datastructures.internal.terms

import leo.datastructures.impl.Signature
import leo.datastructures.term.{Term, Reductions}
import leo.datastructures.{Type, Clause, Role, Role_Type, Role_Definition, Role_Unknown}
import Term._
import Term.{mkTermApp => ap, mkTypeApp => tyAp}
import Type._
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

  val FILES = {new File("benchmark").listFiles.filter(_.getName.endsWith(".p.sem")).toSet ++
    new File("benchmark").listFiles.filter(_.getName.endsWith(".p.syn")).toSet }

//  val FILES = {new File(__TPTPPROBLEMPATH__ + "SET").listFiles.filter(x => !(x.getName.contains("-")) && x.getName.endsWith(".p")).toSet}



  private def benchmark(name: String, term: Term, role: Role): Long = {
    //    print(s"Benchmarking $name: \t")
    Reductions.reset()
    term.headSymbol
    Reductions()
    //    val t: Long = time(term.betaNormalize)._2

    //    println("%d microseconds".format(t))
    //    t
  }

  var times: Map[String, Seq[Long]] = Map()

  def doit(file: File): Long = {
    val sig = Signature.get
    print(s"${file.getName} : ")
    val fs = load(file.getAbsolutePath)

    //    Print signature
    //        println("###########################")
    //        println("Signature:")
    //        for (s <- sig.allConstants) {
    //          print(sig(s).key.toString + "\t\t")
    //          print(sig(s).name + "\t\t:\t")
    //          sig(s).ty.foreach({ case ty => print(ty.pretty)})
    //          sig(s).kind.foreach({ case ty => print(ty.pretty)})
    //          println()
    //        }
    //    println("###########################")
    //    val power = sig.apply("power").key
    //    val fs = Seq(("10^2", mkTermApp(mkAtom(power), Seq(fromInt(10), fromInt(2))),  "axiom"),
    //      ("20^2", mkTermApp(mkAtom(power), Seq(fromInt(20), fromInt(2))),  "axiom"),
    //      ("10^3", mkTermApp(mkAtom(power), Seq(fromInt(10), fromInt(3))),  "axiom"))
    // Print parsed formulae
    //    println("Parsed formulae:")
    //    fs.foreach({case (name, term, role) =>
    //      println(s"$name \t $role \t\t ${term.pretty}")
    //    })
    //    println("###########################")

    // Expand definitions
    //    println("Normalize parsed formulae:")
    val fs2 = fs.map({case (name, clause, role) => (name, clause.mapLit(_.termMap(_.betaNormalize)) ,role)})
    //    fs2.foreach({case (name, term, role) =>
    //      println(s"$name \t $role \t\t ${term.pretty}")
    //    })
    //    println("###########################")

    // Expand definitions
    //    println("Expand definitions:")
//    val fs3 = fs2.map({case (name, term, role) => (name, term.full_δ_expand ,role)})
    //    fs3.foreach({case (name, term, role) =>
    //      println(s"$name \t $role \t\t ${term.pretty}")
    //    })
    //    println("###########################")

    // Benchmark beta normalization
    //    println("benchmark beta NF")
    //    var localTimes: Seq[Long] = Seq()
    var time: Long = 0
    fs2.foreach({case (n, t, r) => {
      assert(t.lits.size == 1) // freshly parsed

      val erg = benchmark(n,t.lits.head.term,r)
      //      localTimes.+:(erg)
      time += erg
    }})
    //    times += ((FILE, localTimes))
    println(s"$time")
    time
    //    val agl = localTimes.
    //    println(s"Time average: $avg")
  }

  def main(args: Array[String]) {
    var all: Long = 0
    val sig = Signature.get
    // Files
        for(f <- FILES) {
          Signature.resetWithHOL(sig)
          loadedSet.clear()
          all += doit(f)
        }
    // Numerals
//    Numerals()
//    val mult = sig("mult").key
//    val power = sig.apply("power").key
//    for (i <- 10 to 30) {
//      //      print(s"$i : ")
//      val a = mkTermApp(mkAtom(power), Seq(fromInt(3), fromInt(i)))
////      val b = a.betaNormalize
////      val c = b.full_δ_expand
//      val localTime = time(a.headSymbol)._2
////            val localTime = benchmark("", a, "")
//      print(s"$localTime,")
//      all += localTime
//    }
    println(s"Overall reductions: $all")
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