package leo.modules.parsers

import leo.modules.parsers.utils.GenerateTerm
import leo.modules.parsers.syntactical.{TPTPParsers => TermParser0}
import leo.modules.parsers.syntactical_new.termParser_functional.{TermParser => TermParserFunctional}
import leo.modules.parsers.syntactical_new.termParser2.{TermParser2 => TermParser2}
import leo.datastructures.tptp.Commons._
import leo.{Checked, Ignored, LeoTestSuite}
import leo.modules.output.logger.Out

//import leo.modules.parsers.lexical.TPTPTokens


/**
  * Created by samuel on 17.03.16.
  */
class ParseTermBenchmark
  extends LeoTestSuite
{
  object Parser0Wrapper {
    def parse(str: String): Either[String,(Term, Seq[TermParser0.lexical.Token])] = {
      TermParser0.parse[Term](str,TermParser0.term) match {
        case TermParser0.Success(res, rest) => Right((res, Seq()))
        case _ => Left("parser0 failed!")
      }
    }
  }
  def testParser[Token](parser: {
    def parse(input: String): Either[String,(Term, Seq[Token])]
  }): Unit = {
    val numRuns = 1000
    for( length <- 100 to 1000 by 100 )
    {
      var dt: Long = 0
      for(_ <- 0 to numRuns)
      {
        val term = GenerateTerm(length)
        //println(s"testing term: ${term}")

        val t0 = System.nanoTime
        val parseRet = parser.parse(term)
        val t1 = System.nanoTime
        dt = (t1- t0)
        parseRet
      }
      println(s"length: ${length}, time: ${(dt/numRuns)/1e6}ms")
    }
  }
  test("oldTermParser", Checked) {
    Out.output("benchmarking OLD parser on random generated terms")
    testParser(Parser0Wrapper)
  }
  test("newFunctionalTermParser", Ignored) {
    Out.output("benchmarking COMBINATORS BASED parser on random generated terms")
    testParser(TermParserFunctional)
  }
  test("newTermParser", Checked) {
    Out.output("benchmarking NEW parser on random generated terms")
    testParser(TermParser2)
  }
  /*
  test("benchmark all parsers", Checked) {
    println("parser0")
    testParser(Parser0Wrapper)
    println("parser1")
    testParser(TermParser1)
    println("parser2")
    testParser(TermParser2)
  }
  */
}
