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

  def gt[T](a: T, b: T)(implicit o: Ordering[T]): Unit = {
    assert(o.equiv(a,a))
    assert(o.equiv(b,b))
    assert(o.gt(a,b))
    assert(o.lt(b,a))
  }

  def gtTerm(a: Term, b: Term): Unit = {
    assert(Term.LexicographicalOrdering.equiv(a,a))
    assert(Term.LexicographicalOrdering.equiv(b,b))
    assert(Term.LexicographicalOrdering.gt(a,b))
    assert(Term.LexicographicalOrdering.lt(b,a))
  }

  test("Comparision on base types",Checked) {
    implicit  val ord = Type.LexicographicalOrdering
    assert(Type.LexicographicalOrdering.compare(o, o) == 0)
    assert(Type.LexicographicalOrdering.compare(i, i) == 0)
    gt(i, o)
  }

  test("Comparision on function types",Checked) {
    implicit  val ord = Type.LexicographicalOrdering

    assert(Type.LexicographicalOrdering.compare(o ->: o, o ->: o) == 0)

    gt(o, o ->: o)
    gt(o ->: o, o ->: o ->: o)
    gt(i ->: o, o ->: o)
    gt(o ->: i, o ->: o)
  }

  test("Comparison on terms: λx y.F(x) > λx y.c(G(y,x)) ", Checked) {
    implicit val s  = getFreshSignature
    implicit val ord = Term.LexicographicalOrdering
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)
    val G = vargen(i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i))

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 2)))
    val r = λ(i,i)(mkTermApp(c, mkTermApp(G.lift(2), Seq(mkBound(i, 1),mkBound(i, 2)))))
    gt(l, r)
  }

  test("Comparison on terms:  λx y z.c(F(y z x), G(x)) < λx y z.H(x z y)", Checked) {
    implicit val s  = getFreshSignature
    implicit val ord = Term.LexicographicalOrdering
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i ->: i ->: i)
    val G = vargen(i ->: i)
    val H = vargen(i ->: i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i ->: i))


    val l = λ(i,i,i)(mkTermApp(c, Seq(mkTermApp(F.lift(3), Seq(mkBound(i, 2), mkBound(i, 1), mkBound(i, 3))), mkTermApp(G.lift(3), mkBound(i, 3)))))
    val r = λ(i,i,i)(mkTermApp(H.lift(3), Seq(mkBound(i, 3), mkBound(i, 1), mkBound(i, 2))))

    gt(r, l)
  }

  test("Comparison on terms: λx y.F(x) < λx.F(x)", Checked) {

    implicit val s  = getFreshSignature
    implicit val ord = Term.LexicographicalOrdering
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i ->: i ->: i)

    val r = λ(i,i,i)(mkTermApp(F.lift(2), Seq(mkBound(i, 1))))
    val l = λ(i,i)(mkTermApp(F.lift(1), Seq(mkBound(i, 1))))

    gt(l, r)
  }

}
