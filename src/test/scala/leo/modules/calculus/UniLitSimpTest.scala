package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures.{Literal, Term, Type}
import Term._
import leo.modules.HOLSignature.{LitTrue, Not, i, o}
/**
  * Created by lex on 1/12/17.
  */
class UniLitSimpTest extends LeoTestSuite {
  test("Test 1", Checked) {
    implicit val sig = getFreshSignature

    val p = mkAtom(sig.addUninterpreted("p", i ->: (i ->: o) ->: i))
    val c = mkAtom(sig.addUninterpreted("c", i))
    val d = mkAtom(sig.addUninterpreted("d", i))
    val q = mkAtom(sig.addUninterpreted("q", i ->: o))

    val left = mkTermApp(p, Seq(c, λ(i)(LitTrue)))
    val right = mkTermApp(p, Seq(d, λ(i)(Not(mkTermApp(q, mkBound(i, 1))))))

    val vargen = freshVarGenFromBlank

    assert(Term.wellTyped(left))
    assert(Term.wellTyped(right))
    println(left.pretty(sig))
    println(right.pretty(sig))
    val result = Simp.uniLitSimp(Literal.mkNeg(left, right))(sig)
    assert(result.size == 2)
    println(s"Result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
  }
}
