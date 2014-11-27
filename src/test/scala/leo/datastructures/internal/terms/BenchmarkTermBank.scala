package leo.datastructures.internal.terms

import java.io.{FileNotFoundException, File}
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import leo.datastructures.{Role, Clause,  Role_Type, Role_Definition, Role_Unknown}

/**
 * Created by lex on 19.09.14.
 */
object BenchmarkTermBank {
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
        var formulae: Seq[(String, Clause, Role)] = Seq()
        parsed match {
          case Left(x) =>
            println("Parse error in file " + fileAbs + ": " + x)
            Seq()
          case Right(x) =>
            loadedSet += fileAbs
            x.getIncludes.foreach(x => formulae = formulae ++ loadRelative(x._1, path))
            //            println("Loaded " + fileAbs)
            val processed = InputProcessing.processAll(Signature.get)(x.getFormulae)
            //            processed foreach { case (name, form, role) => if(role != "definition" && role != "type")
            //              benchmark(name, form, role)
            //            }
            formulae ++  processed.filter({case (_, _, role) => role != Role_Definition && role != Role_Type && role != Role_Unknown})
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
//  val FILES = Set(new File("benchmark/s4-vary-GSV100+1.p.sem"))

    val FILES = {new File("benchmark").listFiles.filter(x => x.getName.endsWith(".p.sem")).toSet ++
                 new File("benchmark").listFiles.filter(x => x.getName.endsWith(".p.syn")).toSet }

//  val FILES = {new File(__TPTPPROBLEMPATH__ + "SET").listFiles.filter(x => !(x.getName.contains("-")) && x.getName.endsWith(".p")).toSet}

  private def benchmark: (Int, Int, Int, Int, Int, Int) = {
    //    print(s"Benchmarking $name: \t")
//    Reductions.reset()
//    term.betaNormalize

    val a = Term.statistics
    (a._1, a._2, a._3, a._4, a._5, a._6)
//    Reductions()
    //    val t: Long = time(term.betaNormalize)._2

    //    println("%d microseconds".format(t))
    //    t
  }

  var times: Map[String, Seq[Long]] = Map()

  def doit(file: File) = {
    val sig = Signature.get
    print(s"${file.getName} \t ")
    val fs = load(file.getAbsolutePath)
    var localSize = 0
    var minSize = 0
    var maxSize = 0
    var terms = fs.size
    for(f <- fs) {
      assert(f._2.lits.size == 1) // freshly parsed
      val t = f._2.lits.head.term
      localSize += t.size
      minSize = Math.min(minSize, t.size)
      maxSize = Math.max(maxSize, t.size)
    }


    for (sym <- sig.definedSymbols) {
      val s = sig(sym)._defn.size
      terms += 1
      localSize += s
      minSize = Math.min(minSize, s)
      maxSize = Math.max(maxSize, s)
    }

    for (sym <- sig.uninterpretedSymbols) {
      localSize += 1
    }
//    for (f <- includeFormulae) {
//      f._3 match {
//        case "definition" => {
//          sig()
//        }
//      }
//    }

    val time = benchmark
    val sharing = 2*time._6.toDouble / (time._5.toDouble*(time._5.toDouble - 1))
    print(s"${terms} \t")
    print(s"${localSize.toDouble/terms.toDouble}/${minSize}/${maxSize} \t")
    print(s"${time._5} \t ${time._6} \t")
    print(s"$sharing \t ${time._5.toDouble/(terms.toDouble)}\t")
    println(s"$localSize \t ${time._5.toDouble/(localSize.toDouble)}")
    time
    //    val agl = localTimes.
    //    println(s"Time average: $avg")
  }

  def main(args: Array[String]) {
    var all: Seq[(Int, Int, Int, Int, Int, Int)] = Seq.empty
    val sig = Signature.get
    // Files
    println("file \t #t \t avg/min/max s(t) \t #n \t #e \t density \t #n/#t \t sum(#t) \t #n/sum(#t)")
    for(f <- FILES) {
      Signature.resetWithHOL(sig)
      loadedSet.clear()
      all = all :+ doit(f)
    }
    // Numerals
    //    Numerals()
    //    val mult = sig("mult").key
    //    val power = sig.apply("power").key
    //    for (i <- 5 to 100) {
    ////      print(s"$i : ")
    //      val a = mkTermApp(mkAtom(mult), Seq(fromInt(i), fromInt(i)))
    //      val b = a.betaNormalize
    //      val c = b.full_δ_expand
    ////      val localTime = time(c.betaNormalize)._2
    //      val localTime = benchmark("", c, "")
    //      print(s"$localTime,")
    //      all += localTime
    //    }
//    println(s"Overall time: $all")
  }
}
