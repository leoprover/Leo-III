package leo.modules.normalization

import leo.modules.preprocessing.NegationNormal
import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import leo.modules.HOLSignature.{o, Not, Impl, <=>, &, |||, Forall, Exists}
import Term._

/**
 * Created by ryu on 6/12/14.
 */
class NegationNormalTestSuite extends LeoTestSuite {
  implicit val s = getFreshSignature

  val p = mkAtom(s.addUninterpreted("r", o))
  val q = mkAtom(s.addUninterpreted("s", o))

  val toNorm : Map[Term,Term] = Map[Term, Term](
    (Not(Not(p)), p),
    (Impl(p,q), |||(Not(p), q)),
    (Not(<=>(p,q)),&(|||(Not(p),Not(q)), |||(p,q))),
    (<=>(p,q), &(|||(Not(p),q),|||(Not(q),p))),
    (Not(&(p,q)), |||(Not(p),Not(q))),
    (Not(|||(p,q)), &(Not(p),Not(q))),
    (Not(Forall(mkTermAbs(o, p))), Exists(mkTermAbs(o, Not(p)))),
    (Not(Exists(mkTermAbs(o, p))), Forall(mkTermAbs(o, Not(p))))
  )

//  println("\n------------------\nNegation Normalform Test.\n---------------------")
  for ((t,t1) <- toNorm){
//    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    test("Negation Test:"+t.pretty, Checked) {
      val st = Literal.asTerm(NegationNormal(termToClause(t)).lits.head)
      println("Negation: '" + t.pretty + "' was normalized to '" + st.pretty + "'.")
      assert(st == t1, "\nThe negation normalized Term '" + t.pretty + "' should be '" + t1.pretty + "', but was '" + st.pretty + "'.")
    }
  }

  def termToClause(t : Term) : Clause = Clause.mkClause(List(Literal(t, true)),Derived)
}
