package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import Term._
import leo.modules.HOLSignature.{i,o, LitTrue}

/**
  * Created by lex on 6/6/16.
  */
class MatchingTestSuite extends LeoTestSuite {
  type UEq = Seq[(Term, Term)]

  private final val MATCHES = true
  private final val NOT_MATCHES = false

  private final def verify(vargen: FreshVarGen, s: Term, t: Term, expected: Boolean)(implicit sig: Signature): Unit = {
    assert(Term.wellTyped(s), s"${s.pretty(sig)} not well-typed")
    assert(Term.wellTyped(t), s"${t.pretty(sig)} not well-typed")
    val result0 = Matching(vargen, s, t).iterator
    assertResult(expected)(result0.nonEmpty)
  }

  private final def verify(vargen: FreshVarGen, s: Term, t: Term, expected: TermSubst)(implicit sig: Signature): Unit = {
    assert(Term.wellTyped(s), s"${s.pretty(sig)} not well-typed")
    assert(Term.wellTyped(t), s"${t.pretty(sig)} not well-typed")
    val result0 = Matching(vargen.copy, s, t).iterator
    assertResult(true)(result0.nonEmpty)
    val subst = result0.next()
    println(vargen.existingVars.toString())
    val termSubst = subst._1.restrict(i => vargen.existingVars.exists(_._1 == i)).normalize
    println(s"result subst: ${termSubst.pretty}")
    println(s"expected subst: ${expected.pretty}")
    assertResult(expected)(termSubst)
    assertResult(t)(s.substitute(subst._1, subst._2))
  }

  test("f(x,x) = f(a,a)", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val t1 : Term = mkTermApp(f , Seq(x,x))
    val t2 : Term = mkTermApp(f , Seq(a,a))

    verify(vargen, t1, t2, MATCHES)
  }

  test("f(x,x) = f(a,a) unifier", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val t1 : Term = mkTermApp(f , Seq(x,x))
    val t2 : Term = mkTermApp(f , Seq(a,a))

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a))
    verify(vargen, t1, t2, expectedSubst)
  }

  test("x(a) = f(a,a)", Checked){
    implicit val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val t1 : Term = mkTermApp(vargen(i ->: i),a)
    val t2 : Term = mkTermApp(f , Seq(a,a))

    verify(vargen, t1, t2, MATCHES)
  }

  test("f(x,g(y)) = f(a,g(f(a,a)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(a,a)))))

    verify(vargen, t1, t2, MATCHES)
  }

  test("f(x,g(y)) = f(a,g(f(a,a))) unifier", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(a,a)))))

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a, 2 -> mkTermApp(f, Seq(a,a))))
    verify(vargen, t1, t2, expectedSubst)
  }

  test("f(x,g(x)) = f(a,g(f(a,a)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, x)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, Seq(a,a)))))

    verify(vargen, t1, t2, NOT_MATCHES)
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
assert(isPattern(t1))
    assert(isPattern(t2))
    verify(vargen, t1, t2, MATCHES)
  }

  test("f(x,g(y)) = f(a,g(f(z))) unifier", Checked){
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

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a, 2 -> mkTermApp(f, Seq(z,z))))
    verify(vargen, t1, t2, expectedSubst)
  }

  test("f(x,g(a)) = f(a,g(z))", Checked){
    implicit  val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, a)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, z)))

    verify(vargen, t1, t2, NOT_MATCHES)
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

    verify(vargen, t1, t2, MATCHES)
  }

  test("(p(a) = true) = (f(a) = g(a))", Checked){
    import leo.modules.HOLSignature.{=== => EQ}

    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))
    val p = mkAtom(s.addUninterpreted("p", i ->: o))

    val t1 : Term = EQ(mkTermApp(p , a), LitTrue)
    val t2 : Term = EQ(mkTermApp(f , a), mkTermApp(g, a))
    val vargen = freshVarGenFromBlank

    verify(vargen, t1, t2, NOT_MATCHES)
  }
}