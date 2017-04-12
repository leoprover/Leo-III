package leo.modules.encoding

import leo.LeoTestSuite
import leo.datastructures.{Role_Axiom, Signature, Term, Clause}
import leo.modules.Utility
import leo.modules.Utility.termToClause
import leo.modules.parsers.Input

/**
  * Created by lex on 3/22/17.
  */
class EncodingTest extends LeoTestSuite {
  test("Cantor PolyNative") {
    implicit val sig: Signature = getFreshSignature
    Input.readAnnotated("thf(sk5_type, type, sk5: ($i > ($i > $o))).")
    Input.readAnnotated("thf(sk6_type, type, sk6: (($i > $o) > $i)).")
    val (_,f1,_) = Input.readAnnotated("thf(72,plain,(! [A:$i,B:($i > $o)]: ((B @ A) | (sk5 @ (sk6 @ (^ [C:$i]: ~ (B @ C))) @ A))),inference(simp,[status(thm)],[34])).")
    val (_,f2,_) = Input.readAnnotated("thf(98,plain,((~ (sk5 @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A))) @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A)))))),inference(pattern_uni,[status(thm)],[94:[bind(A, $thf(sk6 @ (^ [C:$i]: ~ (sk5 @ C @ C)))),bind(B, $thf(^ [C:$i]: (sk5 @ C @ C)))]])).")

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))
    Out.finest(f2.pretty(sig))
    assert(Term.wellTyped(f2))

    val problem: Problem = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1)) union leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f2))).toSet
    val (encodedProblem, auxDefs, encodedSig) = Encoding(problem, EP_None,
      LambdaElimStrategy_SKI, PolyNative)(sig)

    Out.finest(Utility.signatureAsString(encodedSig))

    println(leo.modules.output.ToTFF(encodedSig))
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodedSig))
      i_prob += 1
    }
    var i_aux = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"aux_$i_aux")(encodedSig))
      i_aux += 1
    }
    encodedProblem.foreach { prob => Clause.wellTyped(prob)}
    auxDefs.foreach { prob => Clause.wellTyped(prob)}
  }

  test("Cantor MonoNative") {
    implicit val sig: Signature = getFreshSignature
    Input.readAnnotated("thf(sk5_type, type, sk5: ($i > ($i > $o))).")
    Input.readAnnotated("thf(sk6_type, type, sk6: (($i > $o) > $i)).")
    val (_,f1,_) = Input.readAnnotated("thf(72,plain,(! [A:$i,B:($i > $o)]: ((B @ A) | (sk5 @ (sk6 @ (^ [C:$i]: ~ (B @ C))) @ A))),inference(simp,[status(thm)],[34])).")
    val (_,f2,_) = Input.readAnnotated("thf(98,plain,((~ (sk5 @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A))) @ (sk6 @ (^ [A:$i]: ~ (sk5 @ A @ A)))))),inference(pattern_uni,[status(thm)],[94:[bind(A, $thf(sk6 @ (^ [C:$i]: ~ (sk5 @ C @ C)))),bind(B, $thf(^ [C:$i]: (sk5 @ C @ C)))]])).")

    Out.finest(f1.pretty(sig))
    assert(Term.wellTyped(f1))
    Out.finest(f2.pretty(sig))
    assert(Term.wellTyped(f2))

    val problem: Problem = (leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1)) union leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f2))).toSet
    val (encodedProblem, auxDefs, encodedSig) = Encoding(problem, EP_None,
      LambdaElimStrategy_SKI, MonoNative)(sig)

    Out.finest(Utility.signatureAsString(encodedSig))
    println(leo.modules.output.ToTFF(encodedSig))
    var i_prob = 0
    encodedProblem.foreach { prob =>
      println(leo.modules.output.ToTFF(prob, Role_Axiom, s"prob_$i_prob")(encodedSig))
      i_prob += 1
    }
    var i_aux = 0
    auxDefs.foreach { defi =>
      println(leo.modules.output.ToTFF(defi, Role_Axiom, s"aux_$i_aux")(encodedSig))
      i_aux += 1
    }
    encodedProblem.foreach { prob => Clause.wellTyped(prob)}
    auxDefs.foreach { prob => Clause.wellTyped(prob)}
  }
}
