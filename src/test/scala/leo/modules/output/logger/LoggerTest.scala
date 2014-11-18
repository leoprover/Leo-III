package leo.modules.output.logger

import leo.Configuration
import leo.modules.CLParameterParser

/**
 * Created by lex on 10.11.14.
 */
object LoggerTest {
  def main(args: Array[String]) {
    Configuration.init(new CLParameterParser(Array("arg0", "-v", "3")))
    Out.output("Test1 Test2 test3")
    Out.severe("help! fatal error!")
    Out.trace("does correctly not show up (log level too low)")
  }
}
