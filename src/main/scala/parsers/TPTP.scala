package parsers

import util.parsing.combinator._
import tptp._

/**
 * Created by lex on 3/23/14.
 */

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
  def parseFile(input: String): Option[Commons.TPTPInput] =
    combinators.Commons.exec(input)

  def parseFormula(input: String): Option[Commons.AnnotatedFormula] =
    combinators.Commons.exec(input, combinators.Commons.formula)

  def parseFOF(input: String): Option[FOF] = combinators.FOF.exec(input)
  def parseTHF(input: String): Option[THF] = combinators.THF.exec(input)
  def parseTFF(input: String): Option[TFF] = combinators.TFF.exec(input)
  def parseCNF(input: String): Option[CNF] = combinators.CNF.exec(input)
  def parseTFA(input: String): Option[TFA] = combinators.TFA.exec(input)
}
