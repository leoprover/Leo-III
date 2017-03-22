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

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))

    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(Set(termToClause(f1)), LambdaElimStrategy_SKI)

    Out.finest("########")
    Out.finest(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    Out.finest("---")
    printSignature(encodingSig)
    assert(encodedProblem.forall(Clause.wellTyped))
    assert(auxDefs.forall(Clause.wellTyped))
    Out.finest("########")
    Out.finest(s"aufDefs size: ${auxDefs.size}")

    Out.finest(leo.modules.output.ToTFF(encodingSig))
    var i_def = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"ax_$i_def")(encodingSig))
      i_def += 1
    }
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodingSig))
      i_prob += 1
    }

    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem union auxDefs)(encodingSig)

    Out.finest("########")
    Out.finest(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    Out.finest("---")
    printSignature(monoSig)
    monoProblem.foreach { cl =>
      assert(Clause.wellTyped(cl), s"${cl.pretty(monoSig)} not well typed")
    }
    assert(monoProblem.forall(Clause.wellTyped))
    Out.finest("########")

    println(leo.modules.output.ToTFF(monoSig))
    println(monoProblem.map(leo.modules.output.ToTFF(_, Role_Plain, "a")(monoSig)).mkString("\n"))

  }


  test("Test Cantor") {
    implicit val sig: Signature = getFreshSignature


    // create formulae, taken from the proof
//    Input.readAnnotated("thf(sk1_type, type, sk1: ($i > ($i > $o))).")
//    Input.readAnnotated("thf(sk2_type, type, sk2: (($i > $o) > $i)).")
    Input.readAnnotated("thf(sk5_type, type, sk5: ($i > ($i > $o))).")
    Input.readAnnotated("thf(sk6_type, type, sk6: (($i > $o) > $i)).")
//    val (_,f1,_) = Input.readAnnotated("thf(62,plain,(! [A:$i,B:($i > $o)]: ((B @ A) | (sk1 @ (sk2 @ (^ [C:$i]: ~ (B @ C))) @ A))),inference(simp,[status(thm)],[26])).")
//    val (_,f2,_) = Input.readAnnotated("thf(35,plain,(! [A:$i,B:($i > $o)]: ((~ (B @ A)) | (~ (sk1 @ (sk2 @ (^ [C:$i]: ~ (B @ C))) @ A)))),inference(simp,[status(thm)],[14])).")
    val (_,f1,_) = Input.readAnnotated("thf(72,plain,(! [A:$i,B:($i > $o)]: ((B @ A) | (sk5 @ (sk6 @ (^ [C:$i]: ~ (B @ C))) @ A))),inference(simp,[status(thm)],[34])).")
    val (_,f2,_) = Input.readAnnotated("thf(98,plain,((~ (sk5 @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A))) @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A)))))),inference(pattern_uni,[status(thm)],[94:[bind(A, $thf(sk6 @ (^ [C:$i]: ~ (sk5 @ C @ C)))),bind(B, $thf(^ [C:$i]: (sk5 @ C @ C)))]])).")

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))
    Out.finest(f2.pretty(sig))
    assert(Term.wellTyped(f2))

    val cnf = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1)) union leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f2))).toSet

    Out.finest(cnf.map(_.pretty(sig)).mkString("\n"))


    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(cnf, LambdaElimStrategy_SKI)
    leo.modules.Utility.printSignature(encodingSig)
    Out.finest("########")
    Out.finest(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    assert(encodedProblem.forall(Clause.wellTyped))
    assert(auxDefs.forall(Clause.wellTyped))
    Out.finest("########")

    Out.finest(leo.modules.output.ToTFF(encodingSig))
    var i_def = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"ax_$i_def")(encodingSig))
      i_def += 1
    }
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodingSig))
      i_prob += 1
    }

    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem union auxDefs)(encodingSig)

    Out.finest("########")
    Out.finest(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    Out.finest("---")
    printSignature(monoSig)
    monoProblem.foreach(cl =>
      if (Clause.unit(cl)) {
        val lit = cl.lits.head
        if (!lit.equational) {
          assert(Term.wellTyped(lit.left), s"Non-equational Not well typed: ${lit.left.pretty(monoSig)}")
        } else {
          assert(Term.wellTyped(lit.left), s"equational Not well typed: ${lit.left.pretty(monoSig)}")
          assert(Term.wellTyped(lit.right), s"equational Not well typed: ${lit.right.pretty(monoSig)}")
          assert(lit.left.ty == lit.right.ty)
        }
      }
      else
        assert(Clause.wellTyped(cl), s"Not well typed: ${cl.pretty(monoSig)}"))
    Out.finest("########")

    println(leo.modules.output.ToTFF(monoSig))

    monoProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"ax_$i_prob")(monoSig))
      i_prob += 1
    }

  }

  test("Test b_eta") {
    implicit val sig: Signature = getFreshSignature

    val (_,f1,_) = Input.readAnnotated("thf(conj, conjecture, ((^ [X: $o, Y: $o]: (X | Y)) != (^ [X: $o, Y: $o]: (Y | X)) )).")

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))

    val cnf = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1))).toSet

    Out.finest(cnf.map(_.pretty(sig)).mkString("\n"))


    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(cnf, LambdaElimStrategy_SKI)
    leo.modules.Utility.printSignature(encodingSig)
    Out.finest("########")
    Out.finest(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    assert(encodedProblem.forall(Clause.wellTyped))
    assert(auxDefs.forall(Clause.wellTyped))
    Out.finest("########")

    Out.finest(leo.modules.output.ToTFF(encodingSig))
    var i_def = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"ax_$i_def")(encodingSig))
      i_def += 1
    }
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodingSig))
      i_prob += 1
    }

    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem union auxDefs)(encodingSig)

    Out.finest("########")
    Out.finest(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    Out.finest("---")
    printSignature(monoSig)
    monoProblem.foreach(cl =>
      if (Clause.unit(cl)) {
        val lit = cl.lits.head
        if (!lit.equational) {
          assert(Term.wellTyped(lit.left), s"Non-equational Not well typed: ${lit.left.pretty(monoSig)}")
        } else {
          assert(Term.wellTyped(lit.left), s"equational Not well typed: ${lit.left.pretty(monoSig)}")
          assert(Term.wellTyped(lit.right), s"equational Not well typed: ${lit.right.pretty(monoSig)}")
          assert(lit.left.ty == lit.right.ty)
        }
      }
      else
        assert(Clause.wellTyped(cl), s"Not well typed: ${cl.pretty(monoSig)}"))
    Out.finest("########")

    println(leo.modules.output.ToTFF(monoSig))

    monoProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"ax_$i_prob")(monoSig))
      i_prob += 1
    }

  }

  test("Test Cantor 2") {
    implicit val sig: Signature = getFreshSignature

    val (_,f1,_) = Input.readAnnotated("thf(sur_cantor, conjecture, (( ? [F: $i > ($i > $o)] : (\n                                   ! [Y: $i > $o] :\n                                    ? [X: $i] : (\n                                      (F @ X) = Y\n                                    )\n                                 ) ))).")

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))

    val cnf = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1))(sig)).toSet

    Out.finest(cnf.map(_.pretty(sig)).mkString("\n"))


    val (encodedProblem, auxDefs, encodingSig) = TypedFOLEncoding.apply(cnf, LambdaElimStrategy_SKI)
    leo.modules.Utility.printSignature(encodingSig)
    Out.finest("########")
    Out.finest(encodedProblem.map(_.pretty(encodingSig)).mkString("\n"))
    assert(encodedProblem.forall(Clause.wellTyped))
    assert(auxDefs.forall(Clause.wellTyped))
    Out.finest("########")

    Out.finest(leo.modules.output.ToTFF(encodingSig))
    var i_def = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"ax_$i_def")(encodingSig))
      i_def += 1
    }
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodingSig))
      i_prob += 1
    }

    val (monoProblem, monoSig) = Monomorphization.apply(encodedProblem union auxDefs)(encodingSig)

    Out.finest("########")
    Out.finest(monoProblem.map(_.pretty(monoSig)).mkString("\n"))
    Out.finest("---")
    printSignature(monoSig)
    monoProblem.foreach(cl =>
      if (Clause.unit(cl)) {
        val lit = cl.lits.head
        if (!lit.equational) {
          assert(Term.wellTyped(lit.left), s"Non-equational Not well typed: ${lit.left.pretty(monoSig)}")
        } else {
          assert(Term.wellTyped(lit.left), s"equational Not well typed: ${lit.left.pretty(monoSig)}")
          assert(Term.wellTyped(lit.right), s"equational Not well typed: ${lit.right.pretty(monoSig)}")
          assert(lit.left.ty == lit.right.ty)
        }
      }
      else
        assert(Clause.wellTyped(cl), s"Not well typed: ${cl.pretty(monoSig)}"))
    Out.finest("########")

    println(leo.modules.output.ToTFF(monoSig))

    monoProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"ax_$i_prob")(monoSig))
      i_prob += 1
    }

  }
}
