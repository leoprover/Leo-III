package parsers

import tptp._
import syntactical.TPTPParsers

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
  //
    def parseFormula(input: String): Option[Commons.AnnotatedFormula] = ??? //  parser.exec(input, parser.annotatedFormula)

  //  def parseFOF(input: String): Option[Commons.FOFAnnotated] = parser.exec(input, parser.fofAnnotated)
  //  def parseTHF(input: String): Option[Commons.THFAnnotated] = parser.exec(input, parser.thfAnnotated)
  //  def parseTFF(input: String): Option[Commons.TFFAnnotated] = parser.exec(input, parser.tffAnnotated)
  //  def parseCNF(input: String): Option[Commons.CNFAnnotated] = parser.exec(input, parser.cnfAnnotated)
  //  def parseTPI(input: String): Option[Commons.TPIAnnotated] = parser.exec(input, parser.tpiAnnotated)
  //def parseTFA(input: String): Option[TFA] = parser.exec(input, parser.tfaFormula)

  protected def extract[T](res: parser.ParseResult[T]): Either[String, T] = {
    res match {
      case parser.Success(x, _) => Right(x)
      case parser.Failure(msg,_) => Left(msg)
      case parser.Error(msg,_) => Left(msg)
    }
  }

  val parser = new TPTPParsers
}