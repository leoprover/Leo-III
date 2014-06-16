package leo.modules.normalization

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import leo.datastructures.internal._
import scala.collection.immutable.HashMap
import leo.datastructures.internal.Term._
import org.scalatest.FunSuite

/**
 * Created by ryu on 6/12/14.
 */
@RunWith(classOf[JUnitRunner])
class NegationNormalTestSuite extends FunSuite {
  val s = Signature.get

  val p = mkAtom(s.addUninterpreted("p", s.o))
  val q = mkAtom(s.addUninterpreted("q", s.o))

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

  for ((t,t1) <- toNorm){
//    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    val st = NegationNormal(t)
    assert(st==t1, "\nThe negation normalized Term '"+t.pretty+"' should be '"+t1.pretty+"', but was '"+st.pretty+"'.")
  }
}
