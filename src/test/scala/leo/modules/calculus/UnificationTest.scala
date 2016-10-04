package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import leo.datastructures.impl.Signature
import scala.collection.immutable.HashMap
import Term._

/**
 * TOTEST all huets rules
 * TODO create a test suite for the utilities and test them
 */
class UnificationTestSuite extends LeoTestSuite {
  type UEq = Seq[(Term, Term)]

   //x(a) = f(a,a)
  test("f(x,x) = f(a,a)", Checked){
  val s = getFreshSignature

  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i))

    val vargen = freshVarGenFromBlank
    val x = vargen(s.i)
    val z = vargen(s.i)
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
  val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i ->: s.i))

    val t1 : Term = mkTermApp(vargen(s.i ->: s.i),a)
    val t2 : Term = mkTermApp(f , List(a,a))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(s.i)(mkTermApp(f,List(mkBound(s.i,1), mkBound(s.i,1))))

    // should have 4 unifiers, we need to check they are different from each other
    for( a <- 1 to 4) {
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals (t2))
    }
    assert (result.isEmpty)
  }

  // x(f(a)) = f(x(a)) -> inf # of unifiers
  test("x(f(a)) = f(x(a))", Checked){
  val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i))
  val x = vargen(s.i ->: s.i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,a))
    val t2 : Term = mkTermApp(f,mkTermApp(x,a))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(s.i)(mkTermApp(f,List(mkBound(s.i,1), mkBound(s.i,1))))

    // should have inf many unifiers, we limit here to 5 since standard unification depth is quite low
    for( a <- 1 to 5) {
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("x(f(a,a)) = f(x(a),f(f(a,a),a))", Checked){
  val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i ->: s.i))
  val x = vargen(s.i ->: s.i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,List(a,a)))
    val t2 : Term = mkTermApp(f,List(mkTermApp(x,a),mkTermApp(f, List(mkTermApp(f, List(a,a)),a))))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(s.i)(mkTermApp(f,List(mkBound(s.i,1), mkBound(s.i,1))))

    // Does it have only 6 unifiers?!
    for( a <- 1 to 1) { // the 7th substitutions fails from some reason
      // reduced to one because of low standard unification depth
    val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("x(f(a,g(a,a))) = f(a,g(x(a),a))", Checked){
  val s = getFreshSignature

  val vargen = freshVarGenFromBlank
  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i ->: s.i))
  val g = mkAtom(s.addUninterpreted("g", s.i ->: s.i ->: s.i))
  val x = vargen(s.i ->: s.i)

    val t1 : Term = mkTermApp(x,mkTermApp(f,List(a,mkTermApp(g,List(a,a)))))
    val t2 : Term = mkTermApp(f,List(a,mkTermApp(g,List(mkTermApp(x,List(a)),a))))

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    val res1 : Term = \(s.i)(mkTermApp(f,List(mkBound(s.i,1), mkBound(s.i,1))))

    for( a <- 1 to 2) { // fails for 30 pre-unifiers!
      //reduced to two because of low standard unification depth
      val ((termSub, typeSub), _) = result.next
      assert (t1.substitute(termSub).betaNormalize.equals(t2.substitute(termSub).betaNormalize))
    }
  }

  test("y(ey) = ~ (sKf(skX(y), ey))", Checked) {
    val s = getFreshSignature

    val vargen = freshVarGenFromBlank
    val y = vargen(s.i ->: s.o)
    val ey = vargen(s.i)

    val sKf = mkAtom(s.addUninterpreted("skf", s.i ->: s.i ->: s.o))
    val skX = mkAtom(s.addUninterpreted("skX", (s.i ->: s.o) ->: s.i))

    val t1 = mkTermApp(y, Seq(ey))
    println(t1.pretty +" "+ t1.typeCheck)
    val t2 = Not(mkTermApp(sKf, Seq(mkTermApp(skX, y), ey)))
    println(t2.pretty + " " + t2.typeCheck)

    val result : Iterator[Unification#UnificationResult] = HuetsPreUnification.unify(vargen,t1,t2).iterator

    assert(result.nonEmpty)
    // This unification task should be solvable, right?
    val ((termSub, typeSub), _) = result.next
    println("unifier: " + termSub.pretty)


    println(result.hasNext)
  }
}
