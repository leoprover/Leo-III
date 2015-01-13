package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import leo.datastructures.internal._
import scala.collection.immutable.HashMap
import Term._
import org.scalatest.FunSuite

/**
 * Created by ryu on 6/12/14.
 */
@RunWith(classOf[JUnitRunner])
class NegationNormalTestSuite extends FunSuite {
  val s = Signature.get

  val p = mkAtom(s.addUninterpreted("r", s.o))
  val q = mkAtom(s.addUninterpreted("s", s.o))

  val toNorm : Map[Term,Term] = Map[Term, Term](
    (Not(Not(p)), p),
    (Impl(p,q), |||(Not(p), q)),
    (Not(<=>(p,q)),&(|||(Not(p),Not(q)), |||(p,q))),
    (<=>(p,q), &(|||(Not(p),q),|||(Not(q),p))),
    (Not(&(p,q)), |||(Not(p),Not(q))),
    (Not(|||(p,q)), &(Not(p),Not(q))),
    (Not(Forall(mkTermAbs(s.o, p))), Exists(mkTermAbs(s.o, Not(p)))),
    (Not(Exists(mkTermAbs(s.o, p))), Forall(mkTermAbs(s.o, Not(p))))
  )

//  println("\n------------------\nNegation Normalform Test.\n---------------------")
  for ((t,t1) <- toNorm){
//    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    test("Negation Test:"+t.pretty) {
      val st = NegationNormal.normalize(termToClause(t)).lits.head.term
      println("Negation: '" + t.pretty + "' was normalized to '" + st.pretty + "'.")
      assert(st == t1, "\nThe negation normalized Term '" + t.pretty + "' should be '" + t1.pretty + "', but was '" + st.pretty + "'.")
    }
  }

  def termToClause(t : Term) : Clause = Clause.mkClause(List(Literal(t, true)),Derived)
}
