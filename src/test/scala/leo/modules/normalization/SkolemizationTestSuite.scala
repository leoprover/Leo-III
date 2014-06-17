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
class SkolemizationTestSuite extends FunSuite{
  val s = Signature.get

  val p = mkAtom(s.addUninterpreted("p", s.o))

  def eqa(t : Term, s : Term) = mkTermApp(mkTermApp(mkAtom(10),t),s)
  def vari(i : Int) : Term= mkBound(s.o, i)

  var toNorm : Map[Term,(Term,Term)] = Map[Term, (Term,Term)]()

  def addTest(what : Term, calc : Term, exp : Term) {
    toNorm += ((what, (calc, exp)))
  }

  val test1 = Exists(\(s.o)(Forall(\(s.o)(Exists(\(s.o)(eqa(p,&(vari(3),&(vari(2),vari(1))))))))))
  val test1Sk = Skolemization(Exists(\(s.o)(Forall(\(s.o)(Exists(\(s.o)(eqa(p,&(vari(3),&(vari(2),vari(1)))))))))))
  val erg1 = Forall(\(s.o)(eqa(p,&(mkAtom(s("sk2").key),&(vari(1),mkTermApp(mkAtom(s("sk1").key),List(mkAtom(s("sk2").key),vari(1))))))))
  addTest(test1, test1Sk, erg1)

  for ((t,(t1,t2)) <- toNorm){
    //    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    //Signature.get.allConstants foreach {println(_)}

    val st = t1
    println("The Term '"+t.pretty+"' was skolemized to '"+st.pretty+"'.")
    assert(st==t2, "\nThe skolemized Term '"+t.pretty+"' should be '"+t2.pretty+"', but was '"+st.pretty+"'.")
  }
}
