package leo.modules.calculus

import leo.datastructures.{Signature, Term}
import leo.modules.HOLSignature.{<=>, o}
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/14/16.
  */
class DefExpansionTest extends LeoTestSuite{
  implicit val s: Signature = getFreshSignature
  test("DefExpansion Equivalenz", Checked) {
    val a = Term.mkAtom(s.addUninterpreted("a",o))
    val b = Term.mkAtom(s.addUninterpreted("b",o))

    val t = <=>(a,b)

    val dt = DefExpSimp(t)

    println(dt.pretty)
  }
}
