package leo.modules.normalization

import leo.modules.preprocessing.PrenexNormal
import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import Term._
import leo.modules.HOLSignature.{o, Forall, &}

/**
 * Created by ryu on 6/17/14.
 */
class PrenexTestSuite extends LeoTestSuite {

  implicit val s = getFreshSignature

  val skVar = mkAtom(s.freshSkolemConst(o))

  val toNorm : Map[Term,Term] = Map[Term, Term](
    (&(Forall(\(o)(mkBound(o,1))), skVar), Forall(\(o)(&(mkBound(o,1), skVar)))),
    (&(skVar,Forall(\(o)(mkBound(o,1)))), Forall(\(o)(&(skVar,mkBound(o,1))))),
    (&(Forall(\(o)(mkBound(o,1))),Forall(\(o)(mkBound(o,1)))), Forall(\(o)(Forall(\(o)(&(mkBound(o,1),mkBound(o,2)))))))
  )

//  println("\n----------------------\nPrenexNormalform Test.\n--------------------")
  for ((t,t1) <- toNorm){
//    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    test("Prenex Test:"+t.pretty, Checked) {
      val st = Clause.asTerm(PrenexNormal(termToClause(t)))
      println("Prenex: The Term '" + t.pretty(s) + "' was normalized to '" + st.pretty(s) + "'.")
      assert(st == t1, "\nThe negation normalized Term '" + t.pretty(s) + "' should be '" + t1.pretty(s) + "', but was '" + st.pretty(s) + "'.")
    }
  }

  def termToClause(t : Term) : Clause = Clause.mkClause(List(Literal(t, true)),Derived)
}
