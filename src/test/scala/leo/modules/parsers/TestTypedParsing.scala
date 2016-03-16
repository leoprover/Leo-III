package leo.modules.parsers

import leo.{Checked, LeoTestSuite}
import leo.modules.parsers.syntactical_new.termParser._
import leo.modules.parsers.syntactical_new.termParser.TermParser._

/**
  * Created by samuel on 08.03.16.
  */
class TestTypedParsing
  extends LeoTestSuite
{
  //import leo.modules.parsers.syntactical_new._

  def execParser(stream: Seq[Token]) =
    TermParser.parse(stream) match {
      case Left(err) => println("error: " + err)
      case Right((f, rest)) =>
        val tree = f
        println(
          s"res: ${ tree }, rest: ${ rest }"
        )
    }

  test("testUntypedParsing", Checked) {
    val tokStream = Seq(Integer(3))
    val tokStream2 = Seq(LowerWord("f"), LeftParenthesis, LowerWord("x"), RightParenthesis)
    val tokStream3 = Seq(LowerWord("f"), LeftParenthesis, LowerWord("x"), Comma, LowerWord("y"), RightParenthesis)
    val tokStream4 = Seq(LowerWord("f"), LeftParenthesis, LowerWord("g"), LeftParenthesis, LowerWord("x"), RightParenthesis, Comma, LowerWord("y"), RightParenthesis)
    execParser(tokStream)
    execParser(tokStream2)
    execParser(tokStream3)
    execParser(tokStream4)
  }
}
