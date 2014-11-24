package leo.datastructures.internal

import leo.datastructures.HOLSignature
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
/**
 * This test checks if the number of predefined symbols is coherent with the
 * description in [[HOLSignature]].
 *
 * @author Alexander Steen
 * @since 05.05.2014
 */
@RunWith(classOf[JUnitRunner])
class HOLSignatureTest extends FunSuite with HOLSignature {
  test("cardinality of fixed symbols") {
    assert(fixedConsts.length == 14)
  }
  test("cardinality of defined symbols") {
    assert(definedConsts.length == 9)
  }
  test("cardinality of type symbols") {
    assert(types.length == 6)
  }
}
