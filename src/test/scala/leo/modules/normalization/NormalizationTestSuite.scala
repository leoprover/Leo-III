package leo.modules.normalization

import org.scalatest.{Suites}

/**
 * Created by ryu on 6/17/14.
 */
class NormalizationTestSuite extends Suites(
  new SimplificationTestSuite,
  new NegationNormalTestSuite,
 // new SkolemizationTestSuite,
  new PrenexTestSuite

)
