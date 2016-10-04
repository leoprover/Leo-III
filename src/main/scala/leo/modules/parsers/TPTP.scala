package leo.modules.parsers

import java.io.CharArrayReader

import leo.datastructures.tptp.Commons.{AnnotatedFormula, TPTPInput}
import leo.datastructures.tptp._
import leo.modules.parsers.syntactical_new.TPTPParser2
import syntactical.TPTPParsers._

//import syntactical_new.{StatementParser2, TPTPParser2}

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
   * @param src A [[scala.util.parsing.input.Reader]] wrapping the TPTP input
   * @return A representation of the in file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
   */
  def parseFile(src: io.Source) =
    TPTPParser2.parseSource(src).right map (_._1)
//
//  /**
//   * Convenience method for parsing. Same as `parseFile(input: Reader[Char])`, just that
//   * it takes a string instead of a [[scala.util.parsing.input.Reader]].
//   *
//   * @param input The String that is to be parsed
//   * @return A representation of the input file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
//   */
//  def parseFile(input: String): Either[String, TPTPInput] =
//    TPTPParser2.parse(input).right map (_._1)
//
//  def parseFormula(input: String): Either[String, AnnotatedFormula] =
//    parseTHF(input)
//  //def parseFOF(input: String) =
//  def parseTHF(input: String) =
//    StatementParser2.parse(input).right map (x => x._1 match {
//      case Left( f ) => f
//      case Right( include ) => throw new Exception("annotated formula expected, but include statement found")
//    })
  //def parseTFF(input: String) =
  //def parseCNF(input: String) =
  //def parseTPI(input: String) =


//  def parseFile(src: io.Source) = {
//    val input = src.getLines mkString "\n"
////    val input = new CharArrayReader(src.toArray)
//    extract(parse(input, tptpFile))
//  }
//
//  def parseFile(input: Reader[Char])=
//    extract(parse(input, tptpFile))


  /**
   * Convenience method for parsing. Same as `parseFile(input: Reader[Char])`, just that
   * it takes a string instead of a [[scala.util.parsing.input.Reader]].
   *
   * @param input The String that is to be parsed
   * @return A representation of the input file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
   */
//  def parseFile(input: String)= extract(parse(input, tptpFile))

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

