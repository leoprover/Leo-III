package leo.modules.calculus

import leo.{Configuration, LeoTestSuite}
import leo.datastructures.{Signature, Term, Type}
import Term._
import leo.modules.CLParameterParser
import leo.modules.HOLSignature.{i, o}

/**
  * Created by lex on 27.12.16.
  */
class AntiUnificationTest extends LeoTestSuite {
  test("AntiUnify Test 1") {
    Configuration.init(new CLParameterParser(Array("arg0", "-v", "6")))
    implicit val sig = getFreshSignature

    val alpha = Type.mkType(sig.addBaseType("ɑ"))
    val beta = Type.mkType(sig.addBaseType("β"))
    val gamma = Type.mkType(sig.addBaseType("ɣ"))
    val delta = Type.mkType(sig.addBaseType("δ"))

    val f = Term.mkAtom(sig.addUninterpreted("f", alpha ->: alpha ->: beta))
    val g = Term.mkAtom(sig.addUninterpreted("g", gamma ->: delta))
    val h = Term.mkAtom(sig.addUninterpreted("h", gamma ->: delta ->: alpha))

    val vargen = freshVarGenFromBlank
    val U = vargen(delta ->: gamma ->: alpha)

    val s = λ(gamma,gamma)(
      mkTermApp(f,Seq(
        mkTermApp(h, Seq(mkBound(gamma,1), mkTermApp(g, mkBound(gamma,2)))),
        mkTermApp(h, Seq(mkBound(gamma,2), mkTermApp(g, mkBound(gamma,1)))))
      )
    )
    val t = λ(gamma,gamma)(
      mkTermApp(f,Seq(
        mkTermApp(U.lift(2), Seq(mkTermApp(g, mkBound(gamma,2)),mkBound(gamma,1))),
        mkTermApp(U.lift(2), Seq(mkTermApp(g, mkBound(gamma,1)),mkBound(gamma,2))))
      )
    )
    assert(wellTyped(t))
    assert(wellTyped(s))
    val result = PatternAntiUnification.antiUnify(vargen, t,s)
    assert(result.nonEmpty)
    val (generalization, leftSub, rightSub) = result.head

    println(s"Generalization: ${generalization.pretty(sig)}")
    println(s"LeftSub: ${leftSub._1.pretty}")
    println(s"RightSub: ${rightSub._1.pretty}")
    assert(generalization.substitute(leftSub._1, leftSub._2) == t)
    assert(generalization.substitute(rightSub._1, rightSub._2) == s)
  }
}
