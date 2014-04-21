package parsers

import tptp._
import syntactical.TPTPParsers
import scala.util.parsing.input.Reader

/**
 * Provides a parsing interface for TPTP files and single formulae.
 * The parser obeys the rules of the TPTP Syntax BNF.
 */
object TPTP {
  // Delegate object, so that no instance of parsers needs to created manually

  /**
   * Parses a complete tptp file yielding a [[Commons.TPTPInput]] value if succeeded.
   *
   * @param input A string containing a tptp file
   * @return A representation of the in file in [[Commons.TPTPInput]] format
   */
    def parseFile(input: String)= extract(parser.parse(input, parser.tptpFile))
    def parseFile(input: Reader[Char])= extract(parser.parse(input, parser.tptpFile))

    def parseFormula(input: String) = extract(parser.parse(input, parser.annotatedFormula))

    def parseFOF(input: String) = extract(parser.parse(input, parser.fofAnnotated))
    def parseTHF(input: String) = extract(parser.parse(input, parser.thfAnnotated))
    def parseTFF(input: String) = extract(parser.parse(input, parser.tffAnnotated))
    def parseCNF(input: String) = extract(parser.parse(input, parser.cnfAnnotated))
    def parseTPI(input: String) = extract(parser.parse(input, parser.tpiAnnotated))
//    def parseTFA(input: String) = parser.exec(input, parser.tfaFormula)

  protected def extract[T](res: parser.ParseResult[T]): Either[String, T] = {
    res match {
      case parser.Success(x, _) => Right(x)
      case parser.Failure(msg,_) => Left(msg)
      case parser.Error(msg,_) => Left(msg)
    }
  }

  val parser = new TPTPParsers
}