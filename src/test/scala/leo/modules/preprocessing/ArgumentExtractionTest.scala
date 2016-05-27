package leo.modules.preprocessing

import leo.datastructures.{&, |||, Term}
import leo.modules.output.ToTPTP
import leo.{Checked, LeoTestSuite}
import leo.datastructures.impl.Signature

/**
  * Created by mwisnie on 1/5/16.
  */
class ArgumentExtractionTest extends LeoTestSuite {
  val s = Signature.get

  /* Extract
    f( g ( a /\ b) , a \/ b , a )
   */
  test("Extraction Test 1 (Term Level)", Checked){
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", s.i ->: s.o ->: s.o ->: s.o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", s.o ->: s.i)
    val g = Term.mkAtom(kg)
    val ka = s.addUninterpreted("a", s.o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", s.o)
    val b = Term.mkAtom(kb)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, &(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    f( g ( a /\ b) , a \/ b , a )
   */
  test("Extraction Test 2 (Term Level)", Checked){
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", s.i ->: s.o ->: s.o ->: s.o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", s.o ->: s.i)
    val g = Term.mkAtom(kg)
    val ka = s.addUninterpreted("a", s.o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", s.o)
    val b = Term.mkAtom(kb)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, |||(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) => x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    f( g ( X /\ Y) , X \/ Y , X )
   */
  test("Extraction Test 3 (Term Level)", Checked){
    ArgumentExtraction.clearUnitStore()
    val kf = s.addUninterpreted("f", s.i ->: s.o ->: s.o ->: s.o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", s.o ->: s.i)
    val g = Term.mkAtom(kg)
    val a = Term.mkBound(s.o, 1)
    val b = Term.mkBound(s.o, 2)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, &(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

  /* Extract
    \Y . p ( \X . X /\ Y )
   */
  test("Extraction Test 4 (Term Level)", Checked){
    ArgumentExtraction.clearUnitStore()
    val kp = s.addUninterpreted("p", (s.o ->: s.o) ->: s.i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(s.o, 1)
    val y = Term.mkBound(s.o, 2)

    val t = Term.mkTermAbs(s.o, Term.mkTermApp(p, Term.mkTermAbs(s.o, |||(x,y))))
    println(t.typeCheck)
    val (t1, units) = ArgumentExtraction(t)

    println(s"Extract ${t.pretty}:\n  =>${t1.pretty}\n     ${units.map{case (x,y) =>x.pretty +"==" + y.pretty}.mkString("\n     ")}")
  }

}
