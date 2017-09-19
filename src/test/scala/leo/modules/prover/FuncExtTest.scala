package leo.modules.prover

import leo.LeoTestSuite
import leo.datastructures.ClauseAnnotation.NoAnnotation
import leo.datastructures._
import leo.modules.HOLSignature.i
import leo.modules.control.Control

class FuncExtTest extends LeoTestSuite {
  test("FuncExt Test 1") {
    implicit val sig = getFreshSignature
    val state = State.fresh[AnnotatedClause](sig)

    val a = Term.mkAtom(sig.addUninterpreted("a", i ->: i))
    val b = Term.mkAtom(sig.addUninterpreted("b", i ->: i ->: i))
    val c = Term.mkAtom(sig.addUninterpreted("c", i))

    val l1 = Literal.mkNeg(a, a)
    val l2 = Literal.mkNeg(b, b)
    val l3 = Literal.mkNeg(c, c)

    val cl = Clause(Seq(l1,l2,l3))
    val ac: AnnotatedClause = AnnotatedClause(cl, NoAnnotation)
    val results = Control.funcExtNew(ac)(state)

    println(results.map(_.pretty(sig)).mkString("\n\t"))
  }

  test("FuncExt Test 2") {
    implicit val sig = getFreshSignature
    val state = State.fresh[AnnotatedClause](sig)

    val a = Term.mkAtom(sig.addUninterpreted("a", i ->: i))
    val b = Term.mkAtom(sig.addUninterpreted("b", i ->: i ->: i))
    val c = Term.mkAtom(sig.addUninterpreted("c", i))

    val l1 = Literal.mkNeg(a, a)
    val l2 = Literal.mkPos(b, b)
    val l3 = Literal.mkNeg(c, c)

    val cl = Clause(Seq(l1,l2,l3))
    val ac: AnnotatedClause = AnnotatedClause(cl, NoAnnotation)
    val results = Control.funcExtNew(ac)(state)

    println(results.map(_.pretty(sig)).mkString("\n\t"))
  }

  test("FuncExt Test 3") {
    implicit val sig = getFreshSignature
    val state = State.fresh[AnnotatedClause](sig)

    val a = Term.mkAtom(sig.addUninterpreted("a", i ->: i))
    val b = Term.mkAtom(sig.addUninterpreted("b", i ->: i ->: i))
    val c = Term.mkAtom(sig.addUninterpreted("c", i))

    val l1 = Literal.mkPos(a, a)
    val l2 = Literal.mkPos(b, b)
    val l3 = Literal.mkNeg(c, c)

    val cl = Clause(Seq(l1,l2,l3))
    val ac: AnnotatedClause = AnnotatedClause(cl, NoAnnotation)
    val results = Control.funcExtNew(ac)(state)

    println(results.map(_.pretty(sig)).mkString("\n\t"))
  }
}
