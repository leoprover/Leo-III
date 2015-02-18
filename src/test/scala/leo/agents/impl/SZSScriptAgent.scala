package leo.agents.impl

import leo.Configuration
import leo.modules.CLParameterParser
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

/**
 * Created by ryu on 2/18/15.
 */
@RunWith(classOf[JUnitRunner])
class SZSScriptAgentTest extends FunSuite {
  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))

  val a : SZSScriptAgent = new SZSScriptAgent("scripts/echo.sh")

  test("Scan line") {
    val erg1 = a.getSZS("% SZS status Theorem")
    val erg2 = a.getSZS("performance 0.01s")

    assert(erg1.isDefined, "The line 'SZS status Theorem' contained an SZS Status.")
    assert(erg2.isEmpty, "The line 'performance 0.01s' contained no SZS Status.")
  }


}
