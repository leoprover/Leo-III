package leo

import org.scalatest.{BeforeAndAfter, FunSuite}

/**
 * Abstract template for test suites.
 *
 * @author Alexander Steen
 * @since 4.03.2015
 */
abstract class LeoTestSuite extends FunSuite with BeforeAndAfter with TestUtility {

  before {
    resetBlackBoard
    resetTermBank
  }

}
