package leo.modules.parsers

import leo.modules.parsers.utils.GenerateTerm
import leo.modules.parsers.syntactical.{TPTPParsers => TermParser0}
import leo.modules.parsers.syntactical_new.termParser_functional.{TermParser => TermParserFunctional}
import leo.modules.parsers.syntactical_new.termParser2.TermParser2
import leo.{Checked, Ignored, LeoTestSuite}
import leo.datastructures.tptp.Commons._
import leo.modules.output.logger.Out

import scala.util.parsing.input.Position

/**
  * Created by samuel on 08.03.16.
  */
class ParseTermTest
  extends LeoTestSuite
{

  test("testTermGen", Ignored) {
    //println(s"sq_char: ${sq_char}")
    //println(s"do_char: ${do_char}")
    for( length <- 10 to 100 ) {
      val term = utils.GenerateTerm(length)
      assert( length == term.length)
      //println( s"length: ${length}, real: ${term.length}; ${term}" )
    }
  }

  def testParser(parser: ParserInterface[Term]): Unit = {

    def tokensToString(tokStream: Seq[parser.Token]): String = {
      tokStream map {
        case parser.lexical.SingleQuoted(x) => x
        case x => x.chars
      } reduce (_+_)
    }

    for{
      length <- 10 to 500 by 10
      //_ <- 0 to 100
    } {

      val term = GenerateTerm(length)
      Out.output(s"length: ${length}, parsing term: ${term}")
      val tokStream = parser.tokenize(term)
      //println(s"tokens: ${tokStream}")

      val parseRet = parser.parse(tokStream)
      parseRet match {
        case Left(err)
        => fail(s"parser failed! error message: ${err}")
        case Right((syntaxTree, inputRest))
        =>
          //println(s"parser returned: ${syntaxTree}, rest: ${inputRest}")
          assert( syntaxTree.toString == tokensToString(tokStream) )
          assert( inputRest == Nil )
      }
    }
  }

  object TermParser0Wrapper
    extends ParserInterface[Term]
  {
    override val lexical = TermParser0.lexical
    type Token = TermParser0.lexical.Token

    def tokenize(input: String): TokenStream[Token] = {
      new Iterator[Token]{
        var scanner = new TermParser0.lexical.Scanner(input)
        def hasNext = !scanner.atEnd
        def next(): Token = {
          val ret = scanner.first
          scanner = scanner.rest
          ret
        }
      }.toStream
    }

    def tokenStreamFromSource(src: io.Source): TokenStream[Token] = {
      import util.parsing.input._
      tokenizeFromScanner(
        new TermParser0.lexical.Scanner(
          new CharArrayReader(src.toArray)  // <- quick and dirty solution, to be improved!
          /*new Reader[Char]{
            def atEnd: Boolean = !src.hasNext
            def first: Char = src.next()
            def pos = new Position{
              def column: Int = 1
              def line: Int = 1
              def lineContents: String = ""
            }
            def rest: Reader[Char] =
              this
          }
          */
        )
      )
    }

    def parse(input: Seq[Token]): Either[String,(Term, Seq[Token])] = {
      import util.parsing.input.Reader
      class TokenReader(data: Seq[Token])
        extends Reader[Token]
      {
        override def first: Token =
          data.head
        override def atEnd: Boolean =
          data.isEmpty
        override def pos: Position =
          new Position{ def lineContents = "<>"; def line = 0; def column = 0 }
        override def rest: Reader[Token] =
          new TokenReader(data.tail)
      }
      TermParser0.term(new TokenReader(input)) match {
        case TermParser0.Success(x,rest) => Right((x, Seq()))
        case _ => Left("parser failed!")
      }
    }

    private def tokenizeFromScanner(scanner: TermParser0.lexical.Scanner): TokenStream[Token] = {
      new Iterator[Token]{
        var scannerCopy = scanner
        def hasNext = !scannerCopy.atEnd
        def next(): Token = {
          val ret = scannerCopy.first
          scannerCopy = scannerCopy.rest
          ret
        }
      }.toStream
    }

  }

  test("oldTermParser", Ignored) {
    Out.output("testing OLD parser on random generated terms")
    testParser(TermParser0Wrapper)
  }
  test("newFunctionalTermParser", Ignored) {
    Out.output("testing COMBINATORS BASED parser on random generated terms")
    testParser(TermParserFunctional)
  }
  test("newTermParser", Ignored) {
    Out.output("testing NEW parser on random generated terms")
    testParser(TermParser2)
  }

}
