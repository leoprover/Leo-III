package leo.modules.input

import leo.LeoTestSuite
import leo.datastructures.Signature

import java.io.File

class InputProcessingTestSuite extends LeoTestSuite {
  private[this] val tptp = System.getenv("TPTP")
  private[this] val th0Files0 = new File(tptp ++ "/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("^")).toList
  private[this] val th0Files = th0Files0.filterNot(_.getName == "SYN000^2.p")
  private[this] val cnfFiles = Seq(new File(tptp ++ "/Problems/SYN/SYN000-1.p"), new File(tptp ++ "/Problems/SYN/SYN000-2.p"))

//  val ltbFiles0 = new File("/home/lex/TPTP/CASC-2020/LTB/Problems/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("^")).toList
//  val ltbFiles = ltbFiles0.take(10)

  private[this] val allFiles =  cnfFiles ++ th0Files  //++ ltbFiles

  allFiles.foreach(f =>
    test(f.getName) {
      try {
        val sig = Signature.freshWithHOL()
        val res2 = InputNew.readProblem(f.getAbsolutePath)(sig)
        res2.foreach { case (_, term, _) =>
          assert(leo.datastructures.Term.wellTyped(term), s"Term ${term.pretty(sig)} not well-typed.")
          println(term.pretty(sig))
        }
      } catch {
        case e: TPTPParser.TPTPParseException => println(s"Parse error at ${e.line},${e.offset}: ${e.getMessage}"); throw e
      }
    }
  )
}
