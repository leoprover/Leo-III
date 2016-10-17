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

  import leo.modules.calculus.matching.FOMatching

  test("f(x,x) = f(a,a)", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val t1 : Term = mkTermApp(f , Seq(x,x))
    val t2 : Term = mkTermApp(f , Seq(a,a))

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(true)(result)
  }

  test("f(x,x) = f(a,a) unifier", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val t1 : Term = mkTermApp(f , Seq(x,x))
    val t2 : Term = mkTermApp(f , Seq(a,a))

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a))
    val result = FOMatching.matches(t1, t2)

    assertResult(true)(result.isDefined)
    assertResult(expectedSubst)(result.get)
  }

  test("x(a) = f(a,a)", Checked){
    implicit val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val t1 : Term = mkTermApp(vargen(i ->: i),a)
    val t2 : Term = mkTermApp(f , Seq(a,a))

    val result = FOMatching.decideMatch(t1, t2)
    assertResult(false)(result)
  }

  test("f(x,g(y)) = f(a,g(f(a)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, a))))

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(true)(result)
  }

  test("f(x,g(y)) = f(a,g(f(a))) unifier", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, a))))

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a, 2 -> mkTermApp(f, a)))
    val result = FOMatching.matches(t1, t2)

    assertResult(true)(result.isDefined)
    assertResult(expectedSubst)(result.get)
  }

  test("f(x,g(x)) = f(a,g(f(a)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, x)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, a))))

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(false)(result)
  }

  test("f(x,g(y)) = f(a,g(f(z)))", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, z))))

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(true)(result)
  }

  test("f(x,g(y)) = f(a,g(f(z))) unifier", Checked){
    implicit val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, y)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, mkTermApp(f, z))))

    val expectedSubst: Subst = Subst.fromMap(Map(1 -> a, 2 -> mkTermApp(f, z)))
    val result = FOMatching.matches(t1, t2)

    assertResult(true)(result.isDefined)
    assertResult(expectedSubst)(result.get)
  }

  test("f(x,g(a)) = f(a,g(z))", Checked){
    implicit  val s = getFreshSignature
    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i))
    val g = mkAtom(s.addUninterpreted("g", i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val y = vargen(i)
    val z = vargen(i)

    val t1 : Term = mkTermApp(f , Seq(x, mkTermApp(g, a)))
    val t2 : Term = mkTermApp(f , Seq(a, mkTermApp(g, z)))

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(false)(result)
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

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(true)(result)
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

    val result = FOMatching.decideMatch(t1, t2)

    assertResult(false)(result)
  }
}