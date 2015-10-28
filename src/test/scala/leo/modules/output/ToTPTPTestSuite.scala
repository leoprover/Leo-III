package leo.modules.output

import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.{Ignored, LeoTestSuite}
import leo.datastructures.blackboard.Blackboard
import leo.modules.Utility
import leo.modules.parsers.TPTP
import leo.modules.parsers.InputProcessing

/**
* This suite tests the backward translation of internal formulae.
* The formulae are translated back to TPTP representation and then
* re-parsed and checked for syntactic equality.
*
* @author Alexander Steen <a.steen@fu-berlin.de>
* @since 09.03.2015
*/
class ToTPTPTestSuite extends LeoTestSuite {
  val source = getClass.getResource("/problems").getPath
  val problem_suffix = ".p"

  val problems = Seq( //"SYN000-1" -> "TPTP CNF basic syntax features",
//    "SYN000+1" -> "TPTP FOF basic syntax features"
//    "SYN000_1" -> "TPTP TF0 basic syntax features",
    "SYN000^1" -> "TPTP THF basic syntax features"
//    "SYN000^2" -> "TPTP THF advanced syntax features",
//    "SYN000+2" -> "TPTP FOF advanced syntax features"
//    "SYN000_2" -> "TPTP TF0 advanced syntax features",
//    "SYN000=2" -> "TPTP TFA with arithmetic advanced syntax features"
  )

  for (p <- problems) {
    test(p._2, Ignored){
      val sig = getFreshSignature
      Blackboard().clear()

      printHeading(s"Forward/Backward translation test for ${p._2}")
      print(s"## Parsing and processing ${p._1} ...")
      Utility.load(source + "/" +  p._1 + ".p")
      println("Success!")
      Utility.printUserDefinedSignature()
      for (fs <- FormulaDataStore.getFormulas) {
        val toTPTP = ToTPTP(fs)
        println("## Back translation ... success!")
        println(toTPTP.output)
        println("## Reparsing of backward translation ...")

        val parseRes = TPTP.parseTHF(toTPTP.output)
        if (parseRes.isLeft) {
          println("Failed!")
          fail(parseRes.left.get)
        } else {
          println("Success!")
          val parsed = parseRes.right.get
          print("Processing of parsed formulae ...")
          val processed = InputProcessing.process(sig)(parsed)

            println("Success!")
            val (name, form, role) = processed
            println(s"Equivalent names: ${name == fs.name}")
            println(s"Equivalent roles: ${role == fs.role}")

            val (oldFormula, newFormula) = (fs.clause.lits.head.term, form)
            println(s"Equivalent formula: ${oldFormula == newFormula}")

            if ((name != fs.name) || (role != fs.role) || (oldFormula != newFormula)) {
              println(s"Old Name: ${fs.name}, new name: $name")
              println(s"Old Role: ${fs.role}, new role: $role")
              println(s"Old formula: ${oldFormula.pretty}")
              println(s"New formula: ${newFormula.pretty}")

              fail("toTPTP translation defective or backward translation buggy.")
            }

        }
      }
    }
  }
}
