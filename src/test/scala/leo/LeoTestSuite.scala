package leo


import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Abstract template for test suites.
 *
 * @author Alexander Steen
 * @since 4.03.2015
 */
@RunWith(classOf[JUnitRunner])
abstract class LeoTestSuite extends FunSuite with TestUtility
