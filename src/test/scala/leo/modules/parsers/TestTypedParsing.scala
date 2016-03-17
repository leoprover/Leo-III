package leo.modules.parsers

import leo.modules.parsers.utils.GenerateTerm
import leo.modules.parsers.syntactical_new.termParser.TermParser._
import leo.{Checked, LeoTestSuite}
//import leo.modules.parsers.syntactical_new.termParser._

//import leo.modules.parsers.lexical.TPTPLexical

/**
  * Created by samuel on 08.03.16.
  */
class TestTypedParsing
  extends LeoTestSuite
{
  /*
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
        /*
        println(
          s"res: ${ tree }, rest: ${ rest }"
        )
        */
    }
  }
  */

/*
  test("temporary", Checked) {
    val lexical = new TPTPLexical
    val lexical2 = new TPTPLexical

    assert( lexical.LowerWord("f") == lexical2.LowerWord("f") )
    assert( lexical.LeftParenthesis == lexical2.LeftParenthesis )
  }
*/
  /*
  test("testDiv", Checked) {
    import leo.modules.parsers.utils.GenerateTerm._
    println(divide(1,2))
    println(divide(2,1))
    println(divide(4,4))
  }
  */

  test("testTermGen", Checked) {
    //println(s"sq_char: ${sq_char}")
    //println(s"do_char: ${do_char}")
    for( length <- 10 to 100 ) {
      val term = utils.GenerateTerm(length)
      assert( length == term.length)
      //println( s"length: ${length}, real: ${term.length}; ${term}" )
    }
  }

  test("testUntypedParsing", Checked) {
    for{
      //_ <- 0 to 100
      length <- 10 to 50
    } {
      val term = GenerateTerm(length)
      println(s"testing term: ${term}")
      val parseRet = parse(term)
      assert( parseRet.isRight )
      parseRet.right map {
        case (syntaxTree, inputRest) =>
          assert( syntaxTree.toString == term )
      }
    }
  }
}
