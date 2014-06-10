package leo.modules.parsers

import leo.datastructures.tptp._
import syntactical.TPTPParsers._
import scala.util.parsing.input.Reader

/**
 * Provides a parsing interface for TPTP files and single tptp formulae.
 * The parser obeys the rules of the TPTP Syntax BNF found at
 * [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]].
 *
 * @author Alexander Steen
 * @since 23.03.2014
 * @note Updated last on 22.04.2014
 */
object TPTP {
  /**
   * Parses a complete TPTP file yielding a [[leo.datastructures.tptp.Commons.TPTPInput]] value if succeeded.
   * On success, the result is wrapped in an instance of [[scala.util.Right]]; on failure
   * a [[scala.util.Left]] containing an error message is returned.
   *
   * @param input A [[scala.util.parsing.input.Reader]] wrapping the TPTP input
   * @return A representation of the in file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
   */
  def parseFile(input: Reader[Char])= extract(parse(input, tptpFile))

  /**
   * Convenience method for parsing. Same as `parseFile(input: Reader[Char])`, just that
   * it takes a string instead of a [[scala.util.parsing.input.Reader]].
   *
   * @param input The String that is to be parsed
   * @return A representation of the input file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
   */
  def parseFile(input: String)= extract(parse(input, tptpFile))

  def parseFormula(input: String) = extract(parse(input, annotatedFormula))
  def parseFOF(input: String) = extract(parse(input, fofAnnotated))
  def parseTHF(input: String) = extract(parse(input, thfAnnotated))
  def parseTFF(input: String) = extract(parse(input, tffAnnotated))
  def parseCNF(input: String) = extract(parse(input, cnfAnnotated))
  def parseTPI(input: String) = extract(parse(input, tpiAnnotated))
//    def parseTFA(input: String) = parser.exec(input, tfaFormula)

  // give simplified parsing result representations to the outside
  private def extract[T](res: ParseResult[T]): Either[String, T] = {
    res match {
      case Success(x, _) => Right(x)
      case noSu: NoSuccess => Left(noSu.msg)
    }
  }
}