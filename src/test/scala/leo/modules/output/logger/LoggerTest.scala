package leo.modules.output.logger

/**
 * Created by lex on 10.11.14.
 */
object LoggerTest {
  def main(args: Array[String]) {
    Console.output("Test1 Test2 test3")
    Console.severe("help! fatal error!")
    Console.trace("does correctly not show up (log level too low)")
  }
}
