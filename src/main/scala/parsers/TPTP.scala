package parsers

import datastructures.tptp._
import syntactical.TPTPParsers
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
   * Parses a complete TPTP file yielding a [[datastructures.tptp.Commons.TPTPInput]] value if succeeded.
   * On success, the result is wrapped in an instance of [[scala.util.Right]]; on failure
   * a [[scala.util.Left]] containing an error message is returned.
   *
   * @param input A [[scala.util.parsing.input.Reader]] wrapping the TPTP input
   * @return A representation of the in file in [[datastructures.tptp.Commons.TPTPInput]] format
   */
  def parseFile(input: Reader[Char])= extract(parser.parse(input, parser.tptpFile))

  /**
   * Convenience method for parsing. Same as `parseFile(input: Reader[Char])`, just that
   * it takes a string instead of a [[scala.util.parsing.input.Reader]].
   *
   * @param input The String that is to be parsed
   * @return A representation of the input file in [[datastructures.tptp.Commons.TPTPInput]] format
   */
  def parseFile(input: String)= extract(parser.parse(input, parser.tptpFile))

  def parseFormula(input: String) = extract(parser.parse(input, parser.annotatedFormula))
  def parseFOF(input: String) = extract(parser.parse(input, parser.fofAnnotated))
  def parseTHF(input: String) = extract(parser.parse(input, parser.thfAnnotated))
  def parseTFF(input: String) = extract(parser.parse(input, parser.tffAnnotated))
  def parseCNF(input: String) = extract(parser.parse(input, parser.cnfAnnotated))
  def parseTPI(input: String) = extract(parser.parse(input, parser.tpiAnnotated))
//    def parseTFA(input: String) = parser.exec(input, parser.tfaFormula)

  // give simplified parsing result representations to the outside
  protected def extract[T](res: parser.ParseResult[T]): Either[String, T] = {
    res match {
      case parser.Success(x, _) => Right(x)
      case parser.Failure(msg,_) => Left(msg)
      case parser.Error(msg,_) => Left(msg)
    }
  }

  val parser = new TPTPParsers
}