package leo.modules.calculus

import leo.datastructures.{Signature, Term}
import leo.datastructures.Term._
import leo.modules.HOLSignature._
import leo.{Checked, LeoTestSuite}

/**
  * Created by lex on 09.01.17.
  */
class MatchingTest extends LeoTestSuite {

  private def preCheck(vargen: FreshVarGen, s: Term, t: Term)(implicit sig: Signature): Unit = {
    assert(Term.wellTyped(s), s"${s.pretty(sig)} not well-typed")
    assert(Term.wellTyped(t), s"${t.pretty(sig)} not well-typed")
  }
  private def shouldMatch(vargen: FreshVarGen, s: Term, t: Term)(implicit sig: Signature): Unit = {
    val result = HOMatching.matchTerms(vargen, s, t).iterator
    assert(result.nonEmpty, "Terms should have matched")
    result.foreach { case (termSubst, typeSubst) =>
        assert(s.substitute(termSubst, typeSubst).etaExpand == t.etaExpand, s"substitution ${termSubst.pretty} does not match")
    }
  }
  private def shouldNotMatch(vargen: FreshVarGen, s: Term, t: Term)(implicit sig: Signature): Unit = {
    val result = HOMatching.matchTerms(vargen, s, t).iterator
    assert(result.isEmpty, "Terms should not have matched")
  }

  test("f(x,x) = f(a,a)", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val t1 : Term = mkTermApp(f , Seq(x,x))
    val t2 : Term = mkTermApp(f , Seq(a,a))

    preCheck(vargen, t1, t2)
    shouldMatch(vargen, t1, t2)
  }

  test("x(a) = f(a,a)", Checked){
    implicit val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val t1 : Term = mkTermApp(vargen(i ->: i),a)
    val t2 : Term = mkTermApp(f , Seq(a,a))

    preCheck(vargen, t1, t2)
    shouldMatch(vargen, t1, t2)
  }

  test("f(x,g(y)) = f(a,g(f(a,g(a))))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(a, mkTermApp(g, a))))))

    preCheck(vargen, t1, t2)
    shouldMatch(vargen, t1, t2)
  }

  test("f(x,g(x)) = f(a,g(f(a,a)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, x)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(a,a)))))

    preCheck(vargen, t1, t2)
    shouldNotMatch(vargen, t1, t2)
  }

  test("f(x,g(y)) = f(a,g(f(z,z)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(z,z)))))

    preCheck(vargen, t1, t2)
    shouldMatch(vargen, t1, t2)
  }

  test("f(x,g(a)) = f(a,g(z))", Checked){
    implicit  val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, a)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, Seq(z))))

    preCheck(vargen, t1, t2)
    shouldNotMatch(vargen, t1, t2)
  }

  test("(f(a) = x) = (f(a) = g(a))", Checked){
    import leo.modules.HOLSignature.{=== => EQ}

    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)

    val t1 : Term = EQ(mkTermApp(f , a), x)
    val t2 : Term = EQ(mkTermApp(f , a), mkTermApp(g, a))

    preCheck(vargen, t1, t2)
    shouldMatch(vargen, t1, t2)
  }

  test("(p(a) = true) = (f(a) = g(a))", Checked){
    import leo.modules.HOLSignature.{=== => EQ}

    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))
    val p = mkAtom(s.addUninterpreted("p", i ->: o))
    val vargen = freshVarGenFromBlank

    val t1 : Term = EQ(mkTermApp(p , a), LitTrue)
    val t2 : Term = EQ(mkTermApp(f , a), mkTermApp(g, a))

    preCheck(vargen, t1, t2)
    shouldNotMatch(vargen, t1, t2)
  }
}
