package leo.modules.parsers

import leo.LeoTestSuite
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.impl.Signature

import leo.modules.Utility

/**
 * This suite tests the parsing and input processing of all the TPTP dialects except for CNF.
 * The suite is based on the SYN000-files that cover basic and advanced syntax features for all
 * dialects.
 * @author Alexander Steen
 * @since 09.02.2015
 */
class InputTestSuite extends LeoTestSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

  val problems = Seq( //"SYN000-1" -> "TPTP CNF basic syntax features",
                      "SYN000+1" -> "TPTP FOF basic syntax features",
                      "SYN000_1" -> "TPTP TF0 basic syntax features",
                      "SYN000^1" -> "TPTP THF basic syntax features",
                      "SYN000^2" -> "TPTP THF advanced syntax features",
                      "SYN000+2" -> "TPTP FOF advanced syntax features",
                      "SYN000_2" -> "TPTP TF0 advanced syntax features",
                      "SYN000=2" -> "TPTP TFA with arithmetic advanced syntax features"
  )

  val sig = Signature.get

  for (p <- problems) {
    test(p._2) {
      Signature.resetWithHOL(sig)
      Blackboard().clear()

      printHeading(s"Processing test for ${p._2}")
      print(s"## Parsing ${p._1} ...")

      Utility.load(source + "/" +  p._1 + ".p")
      println("Success!")
      println(s"Parsed ${sig.allUserConstants.size} symbols into signature, ${Blackboard().getFormulas.size} formulae added to blackboard.")
      println()
      println("## Problem signature:")
      printLongHLine()
      Utility.printSignature()
      println()
      println("## Formulae converted to internal representation:")
      printLongHLine()
      Utility.formulaContext()
      println()
    }
  }
}
