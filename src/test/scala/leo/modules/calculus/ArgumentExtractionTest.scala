package leo.modules.calculus

import leo.datastructures.Term
import leo.modules.HOLSignature.{&, i, o, |||}
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/5/16.
  */
class ArgumentExtractionTest extends LeoTestSuite {


  /* Extract
    f( g ( a /\ b) , a \/ b , a )
   */
  test("Extraction Test 1 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", i ->: o ->: o ->: o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", o ->: i)
    val g = Term.mkAtom(kg)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, &(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    f( g ( a /\ b) , a \/ b , a )
   */
  test("Extraction Test 2 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", i ->: o ->: o ->: o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", o ->: i)
    val g = Term.mkAtom(kg)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, |||(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) => x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    f( g ( X /\ Y) , X \/ Y , X )
   */
  test("Extraction Test 3 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", i ->: o ->: o ->: o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", o ->: i)
    val g = Term.mkAtom(kg)
    val a = Term.mkBound(o, 1)
    val b = Term.mkBound(o, 2)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, &(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    \Y . p ( \X . X /\ Y )
   */
  test("Extraction Test 4 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.clearUnitStore()
    val kp = s.addUninterpreted("p", (o ->: o) ->: i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(o, 1)
    val y = Term.mkBound(o, 2)

    val t = Term.mkTermAbs(o, Term.mkTermApp(p, Term.mkTermAbs(o, |||(x,y))))
    println(Term.wellTyped(t))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

}
