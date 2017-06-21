package leo

import leo.modules.parsers.CLParameterParser
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuite}

/**
 * Abstract template for test suites.
 *
 * @author Alexander Steen
 * @since 4.03.2015
 */
abstract class LeoTestSuite extends FunSuite with BeforeAndAfter with BeforeAndAfterAll with TestUtility {

  before {
    resetTermBank()
    leo.Out.setLogLevel(java.util.logging.Level.FINER)
  }

  override def beforeAll: Unit = {
    leo.Out.setLogLevel(java.util.logging.Level.FINER)
    Configuration.init(new CLParameterParser(Array("ARG0")))
  }

}
