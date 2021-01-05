package leo.modules.input

import leo.LeoTestSuite
import leo.datastructures.Signature
import leo.modules.SZSException
import leo.modules.output.SZS_TypeError

import java.io.File

class InputProcessingTestSuite extends LeoTestSuite {
  private val source = getClass.getResource("/problems/SYN000").getPath

  private val problems = Seq(
    "SYN000-1.p" -> "TPTP CNF basic syntax features",
    "SYN000+1.p" -> "TPTP FOF basic syntax features",
    "SYN000_1.p" -> "TPTP TF0 basic syntax features",
    "SYN000^1.p" -> "TPTP THF basic syntax features",
    "SYN000-2.p" -> "TPTP CNF advanced syntax features",
//    "SYN000^2.p" -> "TPTP THF advanced syntax features", // Tuples not supported
    "SYN000+2.p" -> "TPTP FOF advanced syntax features",
//    "SYN000_2.p" -> "TPTP TF0 advanced syntax features", // Tuples not supported
    "SYN000^3.p" -> "TPTP TH1 syntax features",
    "SYN000_3.p" -> "TPTP TF1 syntax features",
//    "SYN000=2.p" -> "TPTP TFA with arithmetic advanced syntax features" // Arithmetic not supported.
  )

  for (p <- problems) {
    test(p._2) {
      printHeading(s"InputProcessing test for ${p._2} ...")
      print(s"Parsing ${p._1} ...")
      try {
        val sig = Signature.freshWithHOL()
        val (t, res) = time(Input.readProblem(s"$source/${p._1}")(sig))
        println(s"done (${t/1000}ms).")
        println(s"Parsed ${res.size} annotated formulas.")
        res foreach { case (_, term, _) =>
          if(!leo.datastructures.Term.wellTyped(term)) {
            throw new SZSException(SZS_TypeError, s"Term ${term.pretty(sig)} not well-typed.")
          }
        }
        println("Type checking passed.")
      } catch {
        case e: SZSException =>
          println()
          println(e.getMessage)
          fail()
      }
    }
  }
}
