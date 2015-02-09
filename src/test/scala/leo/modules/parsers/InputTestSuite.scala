package leo.modules.parsers

import leo.datastructures.blackboard.Blackboard
import leo.datastructures.impl.Signature

import leo.modules.Utility
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * @author Alexander Steen
 * @since 09.02.2015
 */
@RunWith(classOf[JUnitRunner])
class InputTestSuite extends FunSuite {
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
      println("##################################")
      println("########## Parsing Test ##########")
      println(s"##### ${p._2}")
      print(s"## Parsing ${p._1} ...")

      Utility.load(source + "/" +  p._1 + ".p")
      println("Success!")
      println(s"Parsed ${sig.allUserConstants.size} symbols into signature, ${Blackboard().getFormulas.size} formulae added to blackboard.")
      println()
      println("## Problem signature:")
      println("#####################")
      Utility.printSignature()
      println()
      println("## Formulae converted to internal representation:")
      println("#################################################")
      Utility.formulaContext()
      println()
    }
  }
}
