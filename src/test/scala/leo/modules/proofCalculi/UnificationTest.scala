package leo.modules.proofCalculi

import leo.{Checked, LeoTestSuite}
import leo.datastructures._
import leo.datastructures.impl.Signature
import leo.datastructures.term.Term
import scala.collection.immutable.HashMap
import Term._

/**
 * Created by Max Wisniewski on 6/10/14.
 */
class UnificationTestSuite extends LeoTestSuite {
  test("First test", Checked){
  val s = getFreshSignature

  val a = mkAtom(s.addUninterpreted("a",s.i))
  val f = mkAtom(s.addUninterpreted("f", s.i ->: s.i))


    val t1 : Term = mkTermApp(mkBound(s.i ->: s.i, 1),a)
    val t2 : Term = mkTermApp(f , List(a,a))

    val result : Iterator[Subst] = HuetsPreUnification.unify(t1,t2,1).iterator

    val res1 : Term = \(s.i)(mkTermApp(f,List(mkBound(s.i,1), mkBound(s.i,1))))

    // should have 4 unifiers, we need to check they are different from each other
    for( a <- 1 to 4) {
      val sb: Subst = result.next
      println(sb.pretty)
      assert (t1.closure(sb).betaNormalize.equals (t2))
    }
  }
}
