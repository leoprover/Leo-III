package leo.modules.preprocessing

import leo.datastructures.Term
import leo.datastructures.impl.SignatureImpl
import leo.modules.HOLSignature.{o, <=>}
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/14/16.
  */
class DefExpansionTest extends LeoTestSuite{
  implicit val s = getFreshSignature
  test("DefExpansion Equivalenz", Checked) {
    val a = Term.mkAtom(s.addUninterpreted("a",o))
    val b = Term.mkAtom(s.addUninterpreted("b",o))

    val t = <=>(a,b)

    val dt = DefExpSimp(t)

    println(dt.pretty)
  }
}
