package parsers.combinators

import scala.util.parsing.combinator.{JavaTokenParsers, RegexParsers, PackratParsers}
import parsers.PExec

/**
 * Created by lex on 3/23/14.
 */
class Commons extends PExec with PackratParsers with JavaTokenParsers {
  override type Target = tptp.Commons.TPTPInput
  override def target = tptpFile

  // Defining lexical tokens
  // Character classes
  def doChar: Parser[String] = """[\040-\041\043-\0133\0135-\0176]|[\\]["\\]""".r
  def sqChar: Parser[String] = """[\040-\046\050-\0133\0135-\0176]|[\\]['\\]""".r
  def alphaNumeric: Parser[String] = """[a-zA-Z0-9\_]""".r

  // Other tokens
  def commentLine: Parser[String] = """%.*""".r

  def singleQuoted: Parser[String] = "'" ~> rep1(sqChar) <~ "'" ^^ {_.fold("")((a,b) => a++b)}
  def distinctObject: Parser[String] = "\"" ~> rep(doChar) <~ "\"" ^^ {_.fold("")((a,b) => a++b)}

  def lowerWord: Parser[String] = """[a-z][A-Za-z0-9_]*""".r
  def upperWord: Parser[String] = """[A-Z][A-Za-z0-9_]*""".r
  def dollarWord: Parser[String] ="""\$[a-z][A-Za-z0-9_]*""".r
  def dollarDollarWord: Parser[String] ="""\$\$[a-z][A-Za-z0-9_]*""".r

  def integer: Parser[Int] = """(-)?[1-9][0-9]*""".r ^^ {_.toInt}

  /*
   * Parsing rules
   */

  // Files
  def tptpFile: Parser[tptp.Commons.TPTPInput] = rep(tptpInput) ^^ {tptp.Commons.TPTPInput(_)}

  def tptpInput: Parser[Either[tptp.Commons.AnnotatedFormula, tptp.Commons.Include]] = (annotatedFormula ||| include) ^^ {
    case e1: tptp.Commons.AnnotatedFormula => Left(e1)
    case e2: tptp.Commons.Include  => Right(e2)
  }

  // Formula records
  def annotatedFormula: Parser[tptp.Commons.AnnotatedFormula] = failure("asd")


  // Include directives
  def include: Parser[tptp.Commons.Include] =
    "include(" ~> singleQuoted ~ opt(",[" ~> repsep(name,",") <~"]") <~ ")" ^^ {
      case name ~ Some(names) => (name, names)
      case name ~ _           => (name, List.empty)
    }
  // Non-logical data (GeneralTerm, General data)

  // General purpose
  def name: Parser[Either[String, Int]] = atomicWord ^^ {Left(_)} | integer ^^ {Right(_)}
  def atomicWord: Parser[String] = lowerWord | singleQuoted
  def atomicDefinedWord: Parser[String] = dollarWord
  def atomicSystemWord: Parser[String] = dollarDollarWord
  def fileName: Parser[String] = singleQuoted
  //def formula: Parser[tptp.Commons.AnnotatedFormula] = null
}
