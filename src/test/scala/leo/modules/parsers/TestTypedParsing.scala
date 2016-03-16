package leo.modules.parsers

import leo.{Checked, LeoTestSuite}
import leo.modules.parsers.syntactical_new.termParser._
import leo.modules.parsers.syntactical_new.termParser.TermParser._

import leo.modules.parsers.lexical.TPTPLexical

/**
  * Created by samuel on 08.03.16.
  */
class TestTypedParsing
  extends LeoTestSuite
{
  def tokenize(input: String): Seq[Token] = {
    //var scanner = new lexical.Scanner(input)
    var scanner = new TermParser.Scanner(input)
    var tokStream: Seq[Token] = List[Token]()
    while(!scanner.atEnd) {
      tokStream = tokStream :+ scanner.first
      //tokStream = tokStream :+ (scanner.first.asInstanceOf[Token])
      scanner = scanner.rest
    }
    tokStream
  }

  def testParser(input: String) = {
    TermParser.parse(input) match {
      case Left(err) => println("error: " + err)
      case Right((f, rest)) =>
        val tree = f
        println(
          s"res: ${ tree }, rest: ${ rest }"
        )
    }
  }

  def testParser(stream: Seq[Token]) = {
    //println(s"tokens: ${ stream }")
    TermParser.parse(stream) match {
      case Left(err) => println("error: " + err)
      case Right((f, rest)) =>
        val tree = f
        println(
          s"res: ${ tree }, rest: ${ rest }"
        )
    }
  }

/*
  test("temporary", Checked) {
    val lexical = new TPTPLexical
    val lexical2 = new TPTPLexical

    assert( lexical.LowerWord("f") == lexical2.LowerWord("f") )
    assert( lexical.LeftParenthesis == lexical2.LeftParenthesis )
  }
*/

  test("testUntypedParsing", Checked) {
    val f = tokenize("f")
    val fx = tokenize("f(x)")

    testParser(f)
    testParser(fx)
    testParser("f(g(x),3)")
  }
}
