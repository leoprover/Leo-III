package leo.modules.normalization

import leo.{Checked, LeoTestSuite}
import leo.datastructures._

import leo.modules.preprocessing.Simplification
import Term._
import leo.modules.HOLSignature.{o, Not, LitFalse, LitTrue, &, |||, <=>, Forall, Exists, Impl}

/**
 * Created by Max Wisniewski on 6/10/14.
 */
class SimplificationTestSuite extends LeoTestSuite {
  implicit val s = getFreshSignature

  val p = mkAtom(s.addUninterpreted("p", o))
  val q = mkAtom(s.addUninterpreted("q", o))

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
    (Forall(mkTermAbs(o, <=>(mkTermApp(p, mkBound(o,1)), mkTermApp(p, mkBound(o,1))))), LitTrue())
  )

//  println("\n-------------------\nSimplification Test.\n---------------------------")
  for ((t,t1) <- toSimpl){
    test("Simplification Test: "+t.pretty, Checked) {
      val st = Literal.asTerm(Simplification(termToClause(t)).lits.head)
      println("Simplicifcation: '" + t.pretty + "' was simplified to '" + st.pretty)
      assert(st == t1, "\nThe simplified Term '" + t.pretty + "' should be '" + t1.pretty + "', but was '" + st.pretty + "'.")
    }
  }

  def termToClause(t : Term) : Clause = Clause.mkClause(List(Literal(t, true)),Derived)
}
