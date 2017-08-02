package leo.datastructures.context

import leo.LeoTestSuite
import leo.datastructures.Term
import leo.datastructures.Orderings
import leo.datastructures.impl.orderings.{TO_CPO_Naive => ord}


class TermOrderingTest extends LeoTestSuite {
  test("asd") {
    implicit val sig = getFreshSignature
    import leo.modules.HOLSignature.i

    val c0 = Term.mkAtom(sig.addUninterpreted("c0", i))
    val x = Term.mkBound(i, 1)
    val y = Term.mkBound(i, 2)

    val c0x = ord.compare(c0,x)
    println(s"c0x: ${Orderings.pretty(c0x)}")
    val c0y = ord.compare(c0,y)
    println(s"c0y: ${Orderings.pretty(c0y)}")

  }
}
