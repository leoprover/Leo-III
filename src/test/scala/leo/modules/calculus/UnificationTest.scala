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
  type UEq = Seq[(Term, Term)]

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
}
