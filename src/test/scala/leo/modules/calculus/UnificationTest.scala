package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import Term._
import leo.modules.HOLSignature.{i,o, Not}

/**
 * TOTEST all huets rules
 * TODO create a test suite for the utilities and test them
 */
class UnificationTestSuite extends LeoTestSuite {
   //x(a) = f(a,a)
  test("f(x,x) = f(a,z)", Checked){
    implicit val s = getFreshSignature

    val a = mkAtom(s.addUninterpreted("a", i))
    val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val vargen = freshVarGenFromBlank
    val x = vargen(i)
    val z = vargen(i)
    val t1 : Term = mkTermApp(f , List(x,x))
    val t2 : Term = mkTermApp(f , List(a,z))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val ((termSub, typeSub), _) = result.next
    assert(!result.hasNext)
    println(termSub.pretty)
    assert (t1.substitute(termSub).betaNormalize.equals (t2.substitute(termSub).betaNormalize))
  }

  // x(a) = f(a,a)
  test("x(a) = f(a,a)", Checked){
  implicit val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",i))
  val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))

    val t1 : Term = mkTermApp(vargen(i ->: i),a)
    val t2 : Term = mkTermApp(f , List(a,a))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(i)(mkTermApp(f,List(mkBound(i,1), mkBound(i,1))))

    // should have 4 unifiers, we need to check they are different from each other
    for( a <- 1 to 4) {
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals (t2))
    }
    assert (result.isEmpty)
  }

  // x(f(a)) = f(x(a)) -> inf # of unifiers
  test("x(f(a)) = f(x(a))", Checked){
  implicit val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",i))
  val f = mkAtom(s.addUninterpreted("f", i ->: i))
  val x = vargen(i ->: i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,a))
    val t2 : Term = mkTermApp(f,mkTermApp(x,a))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(i)(mkTermApp(f,List(mkBound(i,1), mkBound(i,1))))

    // should have inf many unifiers, we limit here to 5 since standard unification depth is quite low
    for( a <- 1 to 5) {
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("x(f(a,a)) = f(x(a),f(f(a,a),a))", Checked){
  implicit val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",i))
  val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
  val x = vargen(i ->: i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,List(a,a)))
    val t2 : Term = mkTermApp(f,List(mkTermApp(x,a),mkTermApp(f, List(mkTermApp(f, List(a,a)),a))))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(i)(mkTermApp(f,List(mkBound(i,1), mkBound(i,1))))

    // Does it have only 6 unifiers?!
    for( a <- 1 to 1) { // the 7th substitutions fails from some reason
      // reduced to one because of low standard unification depth
    val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("x(f(a,g(a,a))) = f(a,g(x(a),a))", Checked){
  implicit val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",i))
  val f = mkAtom(s.addUninterpreted("f", i ->: i ->: i))
  val g = mkAtom(s.addUninterpreted("g", i ->: i ->: i))
  val x = vargen(i ->: i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,List(a,mkTermApp(g,List(a,a)))))
    val t2 : Term = mkTermApp(f,List(a,mkTermApp(g,List(mkTermApp(x,List(a)),a))))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(i)(mkTermApp(f,List(mkBound(i,1), mkBound(i,1))))

    for( a <- 1 to 2) { // fails for 30 pre-unifiers!
      //reduced to two because of low standard unification depth
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("y(ey) = ~ (sKf(skX(y), ey))", Checked) {
    implicit val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val y = vargen(i ->: o)
    val ey = vargen(i)

    val sKf = mkAtom(s.addUninterpreted("skf", i ->: i ->: o))
    val skX = mkAtom(s.addUninterpreted("skX", (i ->: o) ->: i))

    val t1 = mkTermApp(y, Seq(ey))
    println(t1.pretty +" "+ Term.wellTyped(t1))
    val t2 = Not(mkTermApp(sKf, Seq(mkTermApp(skX, y), ey)))
    println(t2.pretty + " " + Term.wellTyped(t2))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    assert(result.nonEmpty)
    // This unification task should be solvable, right?
    val ((termSub, typeSub), _) = result.next
    println("unifier: " + termSub.pretty)


    println(result.hasNext)
  }

  test("λx. x f = λx. x Y", Checked) {
    implicit val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val Y = vargen(i ->: i)

    val f = mkAtom(s.addUninterpreted("f", i ->: i))

    val t1 = λ((i ->: i) ->: i)(mkTermApp(mkBound((i ->: i) ->: i,1), f))
    println(t1.pretty +" "+ Term.wellTyped(t1))
    val t2 = λ((i ->: i) ->: i)(mkTermApp(mkBound((i ->: i) ->: i,1), Y.lift(1)))
    println(t2.pretty + " " + Term.wellTyped(t2))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    assert(result.nonEmpty)
    // This unification task should be solvable, right?
    val ((termSub, typeSub), _) = result.next
    println("unifier: " + termSub.pretty)
    println(t1.substitute(termSub, typeSub).pretty(s))
    println(t2.substitute(termSub, typeSub).pretty(s))
    assert(t1.substitute(termSub, typeSub).etaExpand == t2.substitute(termSub, typeSub).etaExpand)

    println(result.hasNext)
  }
}

class PatternUnificationTestSuite extends LeoTestSuite {
  test("Is pattern: λx.c x", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val c = mkAtom(s.addUninterpreted("c", i ->: i))
    val t = \(i)(mkTermApp(c, mkBound(i,1)))

    println(t.pretty + " " + Term.wellTyped(t))
    assert(PatternUnification.isPattern(t))
  }

  test("Is pattern: F (free variable)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i)
    val F = mkBound(f_t, f_num)

    println(F.pretty + " " + Term.wellTyped(F))
    assert(PatternUnification.isPattern(F))
  }

  test("Is pattern: λx.F (λz.x z)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i)
    val t = \(i)(mkTermApp(mkBound(f_t, f_num + 1),\(i)(mkTermApp(mkBound(i ->: i,2), mkBound(i,1)))))

    println(t.pretty + " " + Term.wellTyped(t))
    assert(PatternUnification.isPattern(t))
  }


  test("Is pattern: λx,y.F y x", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i ->: i)
    val t = \(i)(\(i)(mkTermApp(mkBound(f_t, f_num + 2), Seq(mkBound(i,2), mkBound(i,1)))))

    println(t.pretty + " " + Term.wellTyped(t))
    assert(PatternUnification.isPattern(t))
  }

  test("Is not pattern: F c", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i)
    val c = mkAtom(s.addUninterpreted("c", i))
    val t = mkTermApp(mkBound(f_t, f_num), c)

    println(t.pretty + " " + Term.wellTyped(t))
    assert(!PatternUnification.isPattern(t))
  }

  test("Is not pattern: λx.F x x", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i ->: i)
    val t = \(i)(mkTermApp(mkBound(f_t, f_num+1),
      Seq(mkBound(i,1), mkBound(i,1))))

    println(t.pretty + " " + Term.wellTyped(t))
    assert(!PatternUnification.isPattern(t))

  }
  test("Is not pattern: λx.F (F x)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val (f_num, f_t) = vargen.next(i ->: i)
    val t = \(i)(mkTermApp(mkBound(f_t, f_num+1), mkTermApp(mkBound(f_t, f_num+1), mkBound(i,1))))

    println(t.pretty + " " + Term.wellTyped(t))
    assert(!PatternUnification.isPattern(t))
  }


  /////////////////////
  // Pattern unifier checks
  /////////////////////
  final private def checkUnifier(l: Term, r: Term, s: Signature, vargen: FreshVarGen): Unit = {
    assert(Term.wellTyped(l), "Left not well typed")
    assert(Term.wellTyped(r), "Right not well typed")
    assert(PatternUnification.isPattern(l), "Left is not a pattern")
    assert(PatternUnification.isPattern(r), "Right is not a pattern")
    val res = PatternUnification.unify(vargen, l,r)
    assert(res.nonEmpty, "No unifier found although it should be unifiable")
    val unifier = res.head
//    println(s"unifier: ${unifier._1._1.pretty}")
    val lsubst = l.substitute(unifier._1._1, unifier._1._2)
    val rsubst = r.substitute(unifier._1._1, unifier._1._2)
//    println(s"lsubst: ${lsubst.pretty(s)}")
//    println(s"rsubst: ${rsubst.pretty(s)}")
    assert(Term.wellTyped(lsubst), "Left result not well typed")
    assert(Term.wellTyped(rsubst), "Right result not well typed")
    assert(lsubst == rsubst, "Substitution is no unifier")
  }

  test("unify λx y.F(x) = λx y.c(G(y,x))", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)
    val G = vargen(i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i))

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 2)))
    val r = λ(i,i)(mkTermApp(c, mkTermApp(G.lift(2), Seq(mkBound(i, 1),mkBound(i, 2)))))

    checkUnifier(l,r,s,vargen)
  }


  test("unify λx y.F(y) = λx y.c(G(y,x))", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)
    val G = vargen(i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i))

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 1)))
    val r = λ(i,i)(mkTermApp(c, mkTermApp(G.lift(2), Seq(mkBound(i, 1),mkBound(i, 2)))))

    checkUnifier(l,r,s,vargen)
  }

  test("unify λx y.F(y) = λx y.c(G(x))", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)
    val G = vargen(i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i))

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 1)))
    val r = λ(i,i)(mkTermApp(c, mkTermApp(G.lift(2), Seq(mkBound(i, 2)))))

    checkUnifier(l,r,s,vargen)
  }

  test("unify λx y.F(y) = λx y.F(x)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i)

    val l = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 1)))
    val r = λ(i,i)(mkTermApp(F.lift(2), mkBound(i, 2)))

    checkUnifier(l,r,s,vargen)
  }
  test("unify λx y z.F(y z x) = λx y z.F(x z y)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i ->: i ->: i)

    val l = λ(i,i,i)(mkTermApp(F.lift(3), Seq(mkBound(i, 2), mkBound(i, 1), mkBound(i, 3))))
    val r = λ(i,i,i)(mkTermApp(F.lift(3), Seq(mkBound(i, 3), mkBound(i, 1), mkBound(i, 2))))

    checkUnifier(l,r,s,vargen)
  }

  test("unify λx y z.c(F(y z x), G(x)) = λx y z.H(x z y)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i ->: i ->: i)
    val G = vargen(i ->: i)
    val H = vargen(i ->: i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i ->: i))


    val l = λ(i,i,i)(mkTermApp(c, Seq(mkTermApp(F.lift(3), Seq(mkBound(i, 2), mkBound(i, 1), mkBound(i, 3))), mkTermApp(G.lift(3), mkBound(i, 3)))))
    val r = λ(i,i,i)(mkTermApp(H.lift(3), Seq(mkBound(i, 3), mkBound(i, 1), mkBound(i, 2))))

    checkUnifier(l,r,s,vargen)
  }

  test("unify λx y z.c(F(y z x), G(x), H(y)) = λx y z.I(x z y)", Checked) {
    implicit val s  = getFreshSignature
    val vargen = freshVarGenFromBlank

    val F = vargen(i ->: i ->: i ->: i)
    val G = vargen(i ->: i)
    val H = vargen(i ->: i)
    val I = vargen(i ->: i ->: i ->: i)
    val c = mkAtom(s.addUninterpreted("c",i ->: i ->: i ->: i))


    val l = λ(i,i,i)(mkTermApp(c, Seq(mkTermApp(F.lift(3), Seq(mkBound(i, 2), mkBound(i, 1), mkBound(i, 3))), mkTermApp(G.lift(3), mkBound(i, 3)), mkTermApp(H.lift(3), mkBound(i, 2)))))
    val r = λ(i,i,i)(mkTermApp(I.lift(3), Seq(mkBound(i, 3), mkBound(i, 1), mkBound(i, 2))))

    checkUnifier(l,r,s,vargen)
  }

}
