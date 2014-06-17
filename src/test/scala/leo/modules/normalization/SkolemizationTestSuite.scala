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
  val sko1 = mkAtom(17)     // If they are not the Keys, updated xD
  val sko2 = mkAtom(18)

  val toNorm : Map[Term,Term] = Map[Term, Term](
    (Exists(\(s.o)(Forall(\(s.o)(Exists(\(s.o)(eqa(p,&(vari(3),&(vari(2),vari(1)))))))))),
     Forall(\(s.o)(eqa(p,&(sko2,&(vari(1),mkTermApp(sko1,List(sko2,vari(1)))))))))
     )

  for ((t,t1) <- toNorm){
    //    println("('"+t.pretty+"' , '"+t1.pretty+"')")
    //Signature.get.allConstants foreach {println(_)}

    val st = Skolemization(t)
    println("The Term '"+t.pretty+"' was skolemized to '"+st.pretty+"'.")
    assert(st==t1, "\nThe skolemized Term '"+t.pretty+"' should be '"+t1.pretty+"', but was '"+st.pretty+"'.")
  }
}
