package leo
package modules
package parsers


/**
 * This suite tests parsing of the SYN000-sample problems of the TPTP library.
 * The module [[leo.modules.parsers.TPTP]], i.e., the method parseFile() is tested.
 * A tests succeeds if the parser can successfully parse the input and
 * fails otherwise (i.e. if a parse error occurs).
 *
 * @author Alexander Steen
 * @since 22.04.2014
 * @note Updated 04.03.2015 -- Only test SYN000-files that cover most of the syntax features.
 */
class ParserTestSuite extends LeoTestSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

  val problems = Seq(
//    "SYN000-1" -> "TPTP CNF basic syntax features",
//    "SYN000+1" -> "TPTP FOF basic syntax features",
//    "SYN000_1" -> "TPTP TF0 basic syntax features",
//    "SYN000^1" -> "TPTP THF basic syntax features",
//    "SYN000-2" -> "TPTP CNF advanced syntax features",
//    "SYN000^2" -> "TPTP THF advanced syntax features",
//    "SYN000+2" -> "TPTP FOF advanced syntax features",
//    "SYN000_2" -> "TPTP TF0 advanced syntax features",
//    "SYN000=2" -> "TPTP TFA with arithmetic advanced syntax features",
    "modal_test" -> "Modal"
  )

  for (p <- problems) {
    test(p._2, Ignored) {
      printHeading(s"Parsing test for ${p._2}")
      Out.output(s"## Parsing ${p._1} ...")

      val res = Input.parseProblemFileShallow(source + "/" +  p._1 + ".p")
      Out.output(s"Parsing succeeded. Parsed ${res.getFormulaeCount} formulae and ${res.getIncludeCount} include statements.")
      Out.output(s"${res.inputs.toString()}")
    }
  }
}
