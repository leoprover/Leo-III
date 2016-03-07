package leo.modules.preprocessing

import leo.datastructures.{<=>, Term}
import leo.datastructures.impl.Signature
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/14/16.
  */
class DefExpansionTest extends LeoTestSuite{
  val s = Signature.get
  test("DefExpansion Equivalenz", Checked) {
    val a = Term.mkAtom(s.addUninterpreted("a",s.o))
    val b = Term.mkAtom(s.addUninterpreted("b",s.o))

    val t = <=>(a,b)

    val dt = DefExpansion(t)

    println(dt.pretty)
  }
}
