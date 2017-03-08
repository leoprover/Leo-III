package leo.modules.encoding

import leo.LeoTestSuite
import leo.datastructures.{Clause, Role_Plain, Signature, Term}
import leo.modules.HOLSignature._
import leo.modules.parsers.Input
import leo.modules.Utility.{printSignature, termToClause}

/**
  * Created by lex on 07.03.17.
  */
class MonoTFFTest extends LeoTestSuite {
  test("Test 1") {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", (i ->: i) ->: o)
    val p2 = sig.addUninterpreted("p2", i ->: i ->: i)
    val a = sig.addUninterpreted("a", i ->: i)

    // create formulae
    val f1 = Input.readFormula("p @ (^[X: $i]: (p2 @ X @ (a @ X)))")

    println(f1.pretty(sig))
    assert(Term.wellTyped(f1))

    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(Set(termToClause(f1)), LambdaElimStrategy_SKI)

    println("########")
    println(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    println("---")
    printSignature(encodingSig)
    assert(encodedProblem.forall(Clause.wellTyped))
    println("########")

    println(leo.modules.output.ToTFF(encodingSig))
    println(encodedProblem.map(leo.modules.output.ToTFF(_, Role_Plain, "a")(encodingSig)).mkString("\n"))
    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem)(encodingSig)

    println("########")
    println(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    println("---")
    printSignature(monoSig)
    assert(monoProblem.forall(Clause.wellTyped))
    println("########")

    println(leo.modules.output.ToTFF(monoSig))
    println(monoProblem.map(leo.modules.output.ToTFF(_, Role_Plain, "a")(monoSig)).mkString("\n"))

  }
}
