package leo.datastructures

import leo.LeoTestSuite
import leo.datastructures.impl.orderings.{TO_CPO_Naive => ord}
import leo.modules.calculus.freshVarGenFromBlank
import org.scalatest.Matchers._
import leo.datastructures.Term.local._


class TermOrderingTest extends LeoTestSuite {
  test("Constants vs. variable") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val c0 = mkAtom(sig.addUninterpreted("c0", i))
    val x = vargen(i)
    val y = vargen(i)
    val expect = CMP_NC

    validate(c0,x,expect)(sig)
    validate(c0,y,expect)(sig)
  }

  test("f(X) > X") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val X = vargen(i)

    val s = f(X)
    val t = X
    val expect = CMP_GT

    validate(s,t,expect)(sig)
  }

  test("f(X,Y) > X / f(X,Y) >  Y") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val f = mkAtom(sig.addUninterpreted("f", i ->: i ->: i))
    val X = vargen(i)
    val Y = vargen(i)

    val s = f(X,Y)
    val t = X
    val t2 = Y
    val expect = CMP_GT

    validate(s,t,expect)(sig)
    validate(s,t2,expect)(sig)
  }

  test("f(X) < g(X)") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val g = mkAtom(sig.addUninterpreted("g", i ->: i))
    val X = vargen(i)

    val s = f(X)
    val t = g(X)
    val expect = CMP_LT

    validate(s,t,expect)(sig)
  }

  test("f(f(X)) > f(X)") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val X = vargen(i)

    val s = f(f(X))
    val t = f(X)
    val expect = CMP_GT

    validate(s,t,expect)(sig)
  }

  test("f(X) < g(f(X))") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val g = mkAtom(sig.addUninterpreted("g", i ->: i))
    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val X = vargen(i)

    val s = f(X)
    val t = g(f(X))
    val expect = CMP_LT

    validate(s,t,expect)(sig)
  }


//  % [FINEST] 	 withTerm': f ⋅ (sk4 ⋅ (f ⋅ (sk4 ⋅ (f ⋅ (sk4 ⋅ (8:$i;⊥);⊥);⊥);⊥);⊥);⊥)
//  % [FINEST] 	 otherTerm': f ⋅ (sk4 ⋅ (f ⋅ (sk4 ⋅ (f ⋅ (sk4 ⋅ (f ⋅ (sk4 ⋅ (8:$i;⊥);⊥);⊥);⊥);⊥);⊥);⊥);⊥)
  test("f(sk4(f(sk4(f(sk4(X)))))) < f(sk4(f(sk4(f(sk4(f(sk4(X))))))))") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.{i, o}

    val vargen = freshVarGenFromBlank
    val f = mkAtom(sig.addUninterpreted("f", o ->: i))
    val sk4 = mkAtom(sig.addUninterpreted("sk4", i ->: o))
    val X = vargen(i)

    val s = f(sk4(f(sk4(f(sk4(X))))))
    val t = f(sk4(f(sk4(f(sk4(f(sk4(X))))))))
    val expect = CMP_LT

    validate(s,t,expect)(sig)
  }

  test("p(X) > F / p(X) > T") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.{i, o, LitTrue, LitFalse}

    val vargen = freshVarGenFromBlank
    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val X = vargen(i)

    val s = p(X)
    val t = LitFalse
    val t2 = LitTrue
    val expect = CMP_GT

    validate(s,t,expect)(sig)
    validate(s,t2,expect)(sig)
  }

  test("f <> λXλY.f Y X") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val f = mkAtom(sig.addUninterpreted("f", i ->: i ->: i))

    val s = f
    val t = λ(i,i)(f(mkBound(i,1), mkBound(i,2)))
    val expect = CMP_NC

    validate(s,t,expect)(sig)
  }

  test("e(c(x)) > x(e)") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val vargen = freshVarGenFromBlank
    val e = mkAtom(sig.addUninterpreted("e", i ->: i))
    val c = mkAtom(sig.addUninterpreted("c", ((i ->: i) ->: i) ->: i))
    val X = vargen((i ->: i) ->: i)

    val s = e(c(X))
    val t = X(e)
    val expect = CMP_GT

    validate(s,t,expect)(sig)
  }

  test("totality 1") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val g = mkAtom(sig.addUninterpreted("g", i ->: i ->: i))
    val c = mkAtom(sig.addUninterpreted("c", i))
    val d = mkAtom(sig.addUninterpreted("d", i))

    val s = f(g(c,f(d)))
    val t = g(f(c), f(f(d)))
    val u = f(g(f(d),c))

    validate(s,t,CMP_GT)(sig)
    validate(s,u,CMP_GT)(sig)
  }

  test("totality 2") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val f = mkAtom(sig.addUninterpreted("f", i ->: i))
    val g = mkAtom(sig.addUninterpreted("g", i ->: i ->: i))

    val d = mkAtom(sig.addUninterpreted("d", i))
    val c = mkAtom(sig.addUninterpreted("c", i))

    val s = f(g(c,f(d)))
    val t = g(c,c)

    validate(d,c,CMP_LT)(sig)
    validate(g,c,CMP_GT)(sig)
    validate(f,c,CMP_GT)(sig)
    validate(c,c,CMP_EQ)(sig)
    validate(f(d),c,CMP_GT)(sig)

    validate(s,t,CMP_GT)(sig)
  }

  test("l^f > l^t") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.{i,o}

    val vargen = freshVarGenFromBlank
    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val X = vargen(i)

    val s = Literal.mkLit(p(X), false)
    val t = Literal.mkLit(p(X), true)
    val expect = CMP_GT

    validate(s,t,expect)(sig)
  }

  private final def validate(s: Term, t:Term, expect: CMP_Result)(implicit sig: Signature): Unit = {
    assert(Term.wellTyped(s))
    assert(Term.wellTyped(t))
    println(s"s: ${s.pretty(sig)}")
    println(s"t: ${t.pretty(sig)}")
    val result = ord.compare(s,t)
    println(s"compare(${s.pretty(sig)},${t.pretty(sig)}): ${Orderings.pretty(result)}")
    result shouldBe expect
  }

  private final def validate(s: Literal, t:Literal, expect: CMP_Result)(implicit sig: Signature): Unit = {
    println(s"s: ${s.pretty(sig)}")
    println(s"t: ${t.pretty(sig)}")
    assert(Literal.wellTyped(s))
    assert(Literal.wellTyped(t))
    val result = Literal.compare(s,t)
    println(s"Compare(${s.pretty(sig)},${t.pretty(sig)}): ${Orderings.pretty(result)}")
    result shouldBe expect
  }


}
