package leo.modules.normalization

import org.junit.runner.RunWith
import org.scalatest.{Suites, FunSuite}
import org.scalatest.junit.JUnitRunner

/**
 * Created by ryu on 6/17/14.
 */
@RunWith(classOf[JUnitRunner])
class NormalizationTestSuite extends Suites(
  new SimplificationTestSuite,
  new NegationNormalTestSuite,
  new SkolemizationTestSuite,
  new PrenexTestSuite

)
