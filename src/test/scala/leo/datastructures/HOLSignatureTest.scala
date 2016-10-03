package leo.datastructures

import leo.{Checked, LeoTestSuite}
/**
 * This test checks if the number of predefined symbols is coherent with the
 * description in [[HOLSignature]].
 *
 * @author Alexander Steen
 * @since 05.05.2014
 */
class HOLSignatureTest extends LeoTestSuite {
  import leo.datastructures.HOLSignature._
  test("Cardinality of fixed symbols",Checked) {
    assertResult(36)(fixedConsts.length)
  }
  test("cardinality of defined symbols",Checked) {
    assertResult(9)(definedConsts.length)
  }
  test("cardinality of type symbols",Checked) {
    assertResult(6)(types.length)
  }
}
