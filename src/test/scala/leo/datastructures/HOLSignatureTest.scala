package leo.datastructures

import leo.LeoTestSuite
/**
 * This test checks if the number of predefined symbols is coherent with the
 * description in [[HOLSignature]].
 *
 * @author Alexander Steen
 * @since 05.05.2014
 */
class HOLSignatureTest extends LeoTestSuite with HOLSignature {
  test("Cardinality of fixed symbols") {
    assertResult(36)(fixedConsts.length)
  }
  test("cardinality of defined symbols") {
    assertResult(9)(definedConsts.length)
  }
  test("cardinality of type symbols") {
    assertResult(6)(types.length)
  }
}
