package leo.modules.normalization

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import leo.datastructures.internal.Signature

import leo.datastructures.internal._
import leo.datastructures.internal.Term._

/**
 * Created by ryu on 6/17/14.
 */
@RunWith(classOf[JUnitRunner])
class PrenexTestSuite extends FunSuite {

  val s = Signature.get

  val skVar = mkAtom(s.freshSkolemVar(s.o))

  val toNorm : Map[Term,Term] = Map[Term, Term](
    (&(Forall(\(s.o)(mkBound(s.o,1))), skVar), Forall(\(s.o)(&(mkBound(s.o,1), skVar)))),
    (&(skVar,Forall(\(s.o)(mkBound(s.o,1)))), Forall(\(s.o)(&(skVar,mkBound(s.o,1))))),
    (&(Forall(\(s.o)(mkBound(s.o,1))),Forall(\(s.o)(mkBound(s.o,1)))), Forall(\(s.o)(Forall(\(s.o)(&(mkBound(s.o,2),mkBound(s.o,1)))))))
  )

  println("\n----------------------\nPrenexNormalform Test.\n--------------------")
  for ((t,t1) <- toNorm){
//    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    val st = PrenexNormal(t)
    println("The Term '"+t.pretty+"' was normalized to '"+st.pretty+"'.")
    assert(st==t1, "\nThe negation normalized Term '"+t.pretty+"' should be '"+t1.pretty+"', but was '"+st.pretty+"'.")
  }

}
