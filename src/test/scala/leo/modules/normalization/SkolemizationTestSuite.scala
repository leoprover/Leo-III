package leo.modules.normalization

import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import leo.datastructures.internal._
import leo.datastructures.internal.HOLSignature
import leo.datastructures.internal.Term._

/**
 * Created by ryu on 6/16/14.
 */
@RunWith(classOf[JUnitRunner])
class SkolemizationTestSuite extends FunSuite {
  val s = Signature.get

  val p = mkAtom(s.addUninterpreted("t", s.o ->: s.o ->: s.o ->: s.o))
  val r = mkAtom(s.addUninterpreted("u", s.i ->: s.o))
  val t = mkAtom(s.addUninterpreted("v", s.i ->: s.i ->: s.o))

  def eqa(t : Term, s : Term) = mkTermApp(mkTermApp(mkAtom(10),t),s)
  def vari(i : Int) : Term= mkBound(s.o, i)

  var toNorm : Map[Term,(Term,Term)] = Map[Term, (Term,Term)]()

  def addTest(what : Term, calc : Term, exp : Term) {
    toNorm += ((what, (calc, exp)))
  }

  println("\n------------------\nSkolemization Test.\n---------------------")

  // Test 1
  val test1 = Exists(\(s.o)(Forall(\(s.o)(Exists(\(s.o)(mkTermApp(p,List(vari(1),vari(2),vari(3)))))))))
  val test1Sk = Skolemization(test1)
  val erg1 = Forall(\(s.o)(mkTermApp(p,List(mkTermApp(mkAtom(s("SK1").key), List(vari(1),mkAtom(s("SK2").key))),vari(1),mkAtom(s("SK2").key)))))
  addTest(test1, test1Sk, erg1)


  // Test 2
  val test2 = Forall(\(s.i)(Exists(\(s.i)(|||((mkTermApp(r,mkBound(s.i, 1))), (mkTermApp(t,List(mkBound(s.i,1), mkBound(s.i,2)))))))))
  val test2Sk = Skolemization(test2)
  val erg2 = ||| (mkTermApp(r, mkAtom(s("SK3").key)), Forall(\(s.i)(mkTermApp(t, List(mkTermApp(mkAtom(s("SK4").key), mkBound(s.i,1)), mkBound(s.i,1))))))
  addTest(test2, test2Sk, erg2)


  for ((t,(t1,t2)) <- toNorm){
    //    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    //Signature.get.allConstants foreach {println(_)}

    val st = t1
    println("The Term '"+t.pretty+"' was skolemized to '"+st.pretty+"'.")
    assert(st==t2, "\nThe skolemized Term '"+t.pretty+"' should be '"+t2.pretty+"', but was '"+st.pretty+"'.")
  }
}
