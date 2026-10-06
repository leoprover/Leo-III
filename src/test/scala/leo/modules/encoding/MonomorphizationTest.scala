package leo.modules.encoding

import leo.LeoTestSuite
import leo.datastructures.{Role_Axiom, Term}
import leo.modules.{signatureAsString,termToClause}
import leo.modules.output.ToTHF
import leo.modules.input.Input

/**
  * Created by lex on 3/22/17.
  */
class MonomorphizationTest extends LeoTestSuite {
  test("Test 1") {
    implicit val sig = getFreshSignature
    val input =
      """
        |thf(sur_cantor, conjecture, ~( ! [T: $tType]: (~ ( ? [F: T > (T > $o)] : (
        |                                   ! [Y: T > $o] :
        |                                    ? [X: T] : (
        |                                      (F @ X) = Y
        |                                    )
        |                                 ) )))).
      """.stripMargin

    val (_, f1, _) = Input.readAnnotated(input)
    Out.finest(f1.pretty(sig))
    Term.wellTyped(f1)

    val problem = leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1)).toSet
    Out.finest(problem.map(_.pretty(sig)).mkString("\n"))
    val (monoProb, monoSig) = Monomorphization(problem)

    Out.finest(signatureAsString(monoSig))

    println(ToTHF(monoSig))
    var i_prob = 0
    monoProb.foreach { prob =>
      println(ToTHF.toTPTP(s"prob_$i_prob", prob, Role_Axiom)(monoSig))
      i_prob += 1
    }
  }
}
