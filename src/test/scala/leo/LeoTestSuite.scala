package leo

import org.scalatest.{BeforeAndAfter, FunSuite, BeforeAndAfterAll}

/**
 * Abstract template for test suites.
 *
 * @author Alexander Steen
 * @since 4.03.2015
 */
abstract class LeoTestSuite extends FunSuite with BeforeAndAfter with BeforeAndAfterAll with TestUtility {

  before {
    resetBlackBoard
    resetTermBank
    leo.Out.setLogLevel(java.util.logging.Level.FINER)
  }

  override def beforeAll: Unit = {
    leo.Out.setLogLevel(java.util.logging.Level.FINER)
  }

}
