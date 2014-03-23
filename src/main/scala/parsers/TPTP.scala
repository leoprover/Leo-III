package parsers

import util.parsing.combinator._
import tptp._

/**
 * Created by lex on 3/23/14.
 */
object TPTP {
  def parseFile(input: String): Option[Commons.TPTPInput] =
    fileParser.exec(input)

  def parseFormula(input: String): Option[Commons.Formula] =
    formulaParser.exec(input)
  def parseFOF(input: String): Option[FOF] = formulaParser.parseFOF(input)
  def parseTHF(input: String): Option[THF] = formulaParser.parseTHF(input)
  def parseTFF(input: String): Option[TFF] = formulaParser.parseTFF(input)
  def parseCNF(input: String): Option[CNF] = formulaParser.parseCNF(input)
  def parseTFA(input: String): Option[TFA] = formulaParser.parseTFA(input)

  val formulaParser = new FormulaParser
  val fileParser = new FileParser

}

class FormulaParser extends PExec with PackratParsers {
  override type Target = Commons.Formula
  def target = formula

  def formula : Parser[Commons.Formula] = null
  // case match on type, then build formula type

  def parseFOF(input: String): Option[FOF] = combinators.FOF.exec(input)
  def parseTHF(input: String): Option[THF] = combinators.THF.exec(input)
  def parseTFF(input: String): Option[TFF] = combinators.TFF.exec(input)
  def parseCNF(input: String): Option[CNF] = combinators.CNF.exec(input)
  def parseTFA(input: String): Option[TFA] = combinators.TFA.exec(input)
}

class FileParser extends PExec with PackratParsers {
  override type Target = Commons.TPTPInput
  def target = input

  def input: Parser[Commons.TPTPInput] = null

}
