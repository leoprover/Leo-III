package leo.modules.input

import leo.LeoTestSuite
import leo.datastructures.Signature

import java.io.File

class InputProcessingTestSuite extends LeoTestSuite {
  private[this] val tptp = System.getenv("TPTP")
  private[this] val th0Files0 = new File(tptp ++ "/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("^")).toList
  private[this] val th0Files = th0Files0.filterNot(_.getName == "SYN000^2.p")
  private[this] val tffBasicFiles = Seq(new File(tptp ++ "/Problems/SYN/SYN000_1.p"), new File(tptp ++ "/Problems/SYN/SYN000_3.p"))
  private[this] val tffFiles0 = new File(tptp ++ "/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("_")).toList
  private[this] val tffFiles = tffBasicFiles  ++ tffFiles0.filterNot(x => Seq("SYN000_1.p", "SYN000_3.p").contains(x.getName))
  private[this] val fofBasicFiles = Seq(new File(tptp ++ "/Problems/SYN/SYN000+1.p"), new File(tptp ++ "/Problems/SYN/SYN000+2.p"))
  private[this] val fofFiles0 = new File(tptp ++ "/Problems/SYN/").listFiles.filter(x => x.getName.endsWith(".p") && x.getName.contains("+")).toList
  private[this] val fofFiles = fofBasicFiles //++ fofFiles0
  private[this] val cnfBasicFiles = Seq(new File(tptp ++ "/Problems/SYN/SYN000-1.p"), new File(tptp ++ "/Problems/SYN/SYN000-2.p"))

  private[this] val allFiles = tffFiles ++ fofFiles ++ cnfBasicFiles ++ th0Files  //++ ltbFiles

  allFiles.foreach(f =>
    test(f.getName) {
      try {
        val sig = Signature.freshWithHOL()
        val res2 = Input.readProblem(f.getAbsolutePath)(sig)
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
