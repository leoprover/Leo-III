package leo

import leo.modules.input.CLParameterParser
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll}

/**
 * Abstract template for test suites.
 *
 * @author Alexander Steen
 * @since 4.03.2015
 */
abstract class LeoTestSuite extends AnyFunSuite with BeforeAndAfter with BeforeAndAfterAll with TestUtility {

  before {
    resetTermBank()
    leo.Out.setLogLevel(java.util.logging.Level.FINEST)
  }

  override def beforeAll(): Unit = {
    leo.Out.setLogLevel(java.util.logging.Level.FINEST)
    Configuration.init(new CLParameterParser(Array("ARG0")))
  }

}
