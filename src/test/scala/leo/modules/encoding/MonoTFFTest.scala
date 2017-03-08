package leo.modules.encoding

import leo.LeoTestSuite
import leo.datastructures._
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


  test("Test Cantor") {
    implicit val sig: Signature = getFreshSignature


    // create formulae, taken from the proof
    Input.readAnnotated("thf(sk1_type, type, sk1: ($i > ($i > $o))).")
    Input.readAnnotated("thf(sk2_type, type, sk2: (($i > $o) > $i)).")
    val (_,f1,_) = Input.readAnnotated("thf(62,plain,(! [A:$i,B:($i > $o)]: ((B @ A) | (sk1 @ (sk2 @ (^ [C:$i]: ~ (B @ C))) @ A))),inference(simp,[status(thm)],[26])).")
    val (_,f2,_) = Input.readAnnotated("thf(35,plain,(! [A:$i,B:($i > $o)]: ((~ (B @ A)) | (~ (sk1 @ (sk2 @ (^ [C:$i]: ~ (B @ C))) @ A)))),inference(simp,[status(thm)],[14])).")

    println(f1.pretty(sig))
    assert(Term.wellTyped(f1))
    println(f2.pretty(sig))
    assert(Term.wellTyped(f2))
    leo.modules.Utility.printSignature(sig)
    val cnf = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1)) union leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f2))).toSet

    println(cnf.map(_.pretty(sig)).mkString("\n"))


    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(cnf, LambdaElimStrategy_SKI)
    leo.modules.Utility.printSignature(encodingSig)
    println("########")
    println(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    println("---")
    printSignature(encodingSig)
    assert(encodedProblem.forall(Clause.wellTyped))
    println("########")

    println(leo.modules.output.ToTFF(encodingSig))
    println(encodedProblem.map(leo.modules.output.ToTFF(_, Role_Axiom, "a")(encodingSig)).mkString("\n"))

    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem)(encodingSig)

    println("########")
    println(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    println("---")
    printSignature(monoSig)
    assert(monoProblem.forall(Clause.wellTyped))
    println("########")

    println(leo.modules.output.ToTFF(monoSig))
    println(monoProblem.map(leo.modules.output.ToTFF(_, Role_Axiom, "a")(monoSig)).mkString("\n"))

  }
}
