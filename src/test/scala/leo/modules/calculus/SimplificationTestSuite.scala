package leo.modules.calculus

import leo.datastructures.Term._
import leo.datastructures._
import leo.modules.HOLSignature._
import leo.{Checked, LeoTestSuite}
import leo.modules.input.Input.{readFormula => read}
import leo.modules.procedures.Simplification

/**
 * Created by Max Wisniewski on 6/10/14.
 */
class SimplificationTestSuite extends LeoTestSuite {
  implicit val s: Signature = getFreshSignature

  val p = mkAtom(s.addUninterpreted("p", o))
  val q = mkAtom(s.addUninterpreted("q", o))
  val r = mkAtom(s.addUninterpreted("r", o))

  val toSimpl : Map[Term,Term] = Map[Term, Term](
    (Not(LitTrue()),LitFalse()),
    (Not(LitFalse()), LitTrue()),
    (&(p,p),p),
    (&(p,Not(p)), LitFalse()),
    (<=>(p,p), LitTrue()),
    (&(p,LitTrue()), p),
    (&(p,LitFalse()), LitFalse()),
    (Impl(p,LitTrue()), LitTrue()),
    (Impl(p,LitFalse()), Not(p)),
    (<=>(p,LitTrue()), p),
    (<=>(p,LitFalse()), Not(p)),
    (|||(p,p), p),
    (|||(p,Not(p)), LitTrue()),
    (Impl(p,p), LitTrue()),
    (|||(p,LitTrue()), LitTrue()),
    (|||(p,LitFalse()), p),
    (Impl(LitTrue(), p), p),
    (Impl(LitFalse(), p), LitTrue()),
    (Forall(mkTermAbs(o,p)),p),
    (Exists(mkTermAbs(o, p)),p),
    (Forall(mkTermAbs(o, <=>(mkTermApp(p, mkBound(o,1)), mkTermApp(p, mkBound(o,1))))), LitTrue()),
    (read("! [X:$i]: (r = r)"), read("$true")),
    (read("! [X:$i]: (X = X)"), read("$true")),
    (read("! [X:$tType]: (r = r)"), read("$true"))
  )

//  println("\n-------------------\nSimplification Test.\n---------------------------")
  for ((t,t1) <- toSimpl){
    test("Simplification Test: "+t.pretty(s), Checked) {
      val st = Simplification.apply(t) //Simp.normalize(t)
      println("Simplicifcation: '" + t.pretty(s) + "' was simplified to '" + st.pretty(s))
      if (st != t1) {
        println("The simplified Term '" + t.pretty(s) + "' should be '" + t1.pretty(s) + "', but was '" + st.pretty(s) + "'.")
        fail()
      }
    }
  }

  def termToClause(t : Term) : Clause = Clause(List(Literal(t, true)),Derived)
}
