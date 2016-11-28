package leo.datastructures

import leo.{Checked, LeoTestSuite}

/**
 * This test checks the lexicographical orderings defined on types an terms.
 *
 * @author Hans-Jörg Schurr
 * @since 28.11.2016
 */
class LexicographicalOrderingTest extends LeoTestSuite {
  import leo.datastructures.Type
  import leo.modules.HOLSignature._

  def gtType(a: Type, b:Type): Unit = {
    assert(Type.LexicographicalOrdering.compare(a,b) > 0)
    assert(Type.LexicographicalOrdering.compare(b,a) < 0)
  }

  test("Comparision on base types",Checked) {
    assert(Type.LexicographicalOrdering.compare(o, o) == 0)
    assert(Type.LexicographicalOrdering.compare(i, i) == 0)
    gtType(i, o)
  }

  test("Comparision on function types",Checked) {
    assert(Type.LexicographicalOrdering.compare(o ->: o, o ->: o) == 0)
    gtType(o, o ->: o)
    gtType(o ->: o, o ->: o ->: o)
    gtType(i ->: o, o ->: o)
    gtType(o ->: i, o ->: o)
  }
}
