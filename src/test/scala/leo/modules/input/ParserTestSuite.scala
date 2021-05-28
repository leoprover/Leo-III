package leo
package modules.input

import leo.Ignored
import leo.modules.SZSException

/**
 * This suite tests parsing of the SYN000-sample problems of the TPTP library.
 * A tests succeeds if the parser can successfully parse the input and
 * fails otherwise (i.e. if a parse error occurs).
 *
 * @author Alexander Steen
 * @since 22.04.2014
 * @note Updated January 2021 -- cover more files.
 */
class ParserTestSuite extends LeoTestSuite {
  private val source = getClass.getResource("/problems/SYN000").getPath

  private val problems = Seq(
    "SYN000-1.p" -> "TPTP CNF basic syntax features",
    "SYN000+1.p" -> "TPTP FOF basic syntax features",
    "SYN000_1.p" -> "TPTP TF0 basic syntax features",
    "SYN000^1.p" -> "TPTP THF basic syntax features",
    "SYN000-2.p" -> "TPTP CNF advanced syntax features",
    "SYN000^2.p" -> "TPTP THF advanced syntax features",
    "SYN000+2.p" -> "TPTP FOF advanced syntax features",
    "SYN000_2.p" -> "TPTP TF0 advanced syntax features",
    "SYN000^3.p" -> "TPTP TH1 syntax features",
    "SYN000_3.p" -> "TPTP TF1 syntax features",
    "SYN000=2.p" -> "TPTP TFA with arithmetic advanced syntax features",
    "SYN000-CNF.p" -> "Modal THF format with logic specification"
  )

  for (p <- problems) {
    test(p._2) {
      printHeading(s"Parsing test for ${p._2} ...")
      print(s"Parsing ${p._1} ...")
      try {
        val (t, res) = time(Input.parseProblemFileShallow(s"$source/${p._1}"))
        println(s"done (${t/1000}ms).")
        println(s"Parsed ${res.formulas.size} formulae and ${res.includes.size} include statements.")
      } catch {
        case e: SZSException =>
          println()
          println(e.getMessage)
          fail()
      }
    }
  }
}
