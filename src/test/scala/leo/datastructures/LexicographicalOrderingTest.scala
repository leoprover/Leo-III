package leo.datastructures

import leo.{Checked, LeoTestSuite}
import Term._
import leo.modules.calculus.freshVarGenFromBlank
import leo.modules.HOLSignature._

/**
 * This test checks the lexicographical orderings defined on types an terms.
 *
 * @author Hans-Jörg Schurr
 * @since 28.11.2016
 */
class LexicographicalOrderingTest extends LeoTestSuite {

  def gtType(a: Type, b: Type): Unit = {
    assert(Type.LexicographicalOrdering.compare(a,b) > 0)
    assert(Type.LexicographicalOrdering.compare(b,a) < 0)
  }

  def gtTerm(a: Term, b: Term): Unit = {
    assert(Term.LexicographicalOrdering.compare(a,b) > 0)
    assert(Term.LexicographicalOrdering.compare(b,a) < 0)
  }

  test("Comparision on base types",Checked) {
    assert(Type.LexicographicalOrdering.compare(o, o) == 0)
    assert(Type.LexicographicalOrdering.compare(i, i) == 0)
    gtType(i, o)
  }

  test("Comparision on function types",Checked) {
    assert(Type.LexicographicalOrdering.compare(o ->: o, o ->: o) == 0)
    gtType(o, o ->: o)
    gtType(o ->: o, o ->: o ->: o)
    gtType(i ->: o, o ->: o)
    gtType(o ->: i, o ->: o)
  }

  test("Comparison on terms", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)
    val G = vargen(i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i))

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 2)))
    val r = λ(i,i)(mkTermApp(c, mkTermApp(G.lift(2), Seq(mkBound(i, 1),mkBound(i, 2)))))
    gtTerm(l, r)
  }
}
