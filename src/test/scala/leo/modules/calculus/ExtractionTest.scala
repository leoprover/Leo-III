package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures.Term
import leo.modules.HOLSignature._

/**
  * Created by mwisnie on 1/25/17.
  */
class ExtractionTest extends LeoTestSuite{

  /* Extract
    f( g ( a /\ b) , a \/ b , a )
   */
  test("Simple Extraction 1", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.resetCash()
    val kf = s.addUninterpreted("f", i ->: o ->: o ->: o)
    val f = Term.mkAtom(kf)
    val kg = s.addUninterpreted("g", o ->: i)
    val g = Term.mkAtom(kg)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Term.mkTermApp(f, Seq(Term.mkTermApp(g, &(a,b)), |||(a,b), a))
    val (t1, units) = ArgumentExtraction(t, false, ArgumentExtraction.BooleanType)

    println(s"Extract ${t.pretty(s)}:\n  =>${t1.pretty(s)}\n     ${units.map{_.pretty(s)}.mkString("\n     ")}")
  }

  /* Extract
    p ( \X . X /\ Y )
   */
  test("Extraction Test 2 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.resetCash()
    val kp = s.addUninterpreted("p", (o ->: o) ->: i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(o, 1)
    val y = Term.mkBound(o, 2)

    val t = Term.mkTermApp(p, Term.mkTermAbs(o, |||(x,y)))
    assert(Term.wellTyped(t), s"Not well typed ${t.pretty(s)}")
    val (t1, units) = ArgumentExtraction(t, false, ArgumentExtraction.BooleanType)

    assert(units.isEmpty, "BooleanType should not extract predicates.")

    val (t2, units2) = ArgumentExtraction(t, false, ArgumentExtraction.PredicateType)

    println(s"Extract ${t.pretty(s)}:\n  =>${t2.pretty(s)}\n     ${units2.map{_.pretty(s)}.mkString("\n     ")}")
  }

  /* Extract
  \Y . p ( \X . X /\ Y)
 */
  test("Extraction Test 3 (Term Level)", Checked){
    implicit val s = getFreshSignature
    ArgumentExtraction.resetCash()
    val kp = s.addUninterpreted("p", (o ->: o) ->: i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(o, 1)
    val y = Term.mkBound(o, 2)

    val t = Term.mkTermAbs(o, Term.mkTermApp(p, Term.mkTermAbs(o, |||(x,y))))
    assert(Term.wellTyped(t), s"Not well typed ${t.pretty(s)}")
    val (t2, units2) = ArgumentExtraction(t, false, ArgumentExtraction.PredicateType)

    assert(units2.isEmpty, s"The clause ${t.pretty(s)} cannot extract arguments")
    println(s"Extract ${t.pretty(s)}:\n  =>${t2.pretty(s)}\n     ${units2.map{_.pretty(s)}.mkString("\n     ")}")
  }

  /* Extract
  \Y . p ( \X . X /\ Y , \X . X /\ Z)
 */
  test("Extraction Test 4 (Term level)", Checked) {
    implicit val s = getFreshSignature
    ArgumentExtraction.resetCash()
    val kp = s.addUninterpreted("p", (o ->: o) ->: (o ->: o) ->: i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(o, 1)
    val y = Term.mkBound(o, 2)
    val z = Term.mkBound(o, 3)

    val t = Term.mkTermAbs(o, Term.mkTermApp(p, Seq(Term.mkTermAbs(o, |||(x,y)), Term.mkTermAbs(o, |||(x,z)))))
    assert(Term.wellTyped(t), s"Not well typed ${t.pretty(s)}")
    val (t2, units2) = ArgumentExtraction(t, false, ArgumentExtraction.PredicateType)
    assert(units2.size == 1, s"The clause ${t.pretty(s)} canonly extract the second argument.")
    println(s"Extract ${t.pretty(s)}:\n  =>${t2.pretty(s)}\n     ${units2.map{_.pretty(s)}.mkString("\n     ")}")

  }

  /* Extract
 \Y . p ( \X . X /\ Y , \X . X /\ Y)
*/
  test("Extract Twice Test 1", Checked) {
    implicit val s = getFreshSignature
    ArgumentExtraction.resetCash()
    val kp = s.addUninterpreted("p", (o ->: o) ->: (o ->: o) ->: i)
    val p = Term.mkAtom(kp)
    val x = Term.mkBound(o, 1)
    val y = Term.mkBound(o, 2)

    val t = Term.mkTermAbs(o, Term.mkTermApp(p, Seq(Term.mkTermAbs(o, |||(x,y)), Term.mkTermAbs(o, |||(x,y)))))
    assert(Term.wellTyped(t), s"Not well typed ${t.pretty(s)}")
    val (t2, units2) = ArgumentExtraction(t, true, ArgumentExtraction.PredicateType)
    assert(units2.size == 1, s"The clause ${t.pretty(s)} canonly extract both.")
    println(s"Extract ${t.pretty(s)}:\n  =>${t2.pretty(s)}\n     ${units2.map{_.pretty(s)}.mkString("\n     ")}")
  }

}
