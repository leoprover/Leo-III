package parsers.combinators

import scala.util.parsing.combinator.RegexParsers
import scala.util.parsing.combinator.PackratParsers
import parsers.PExec

/**
 * Created by lex on 3/23/14.
 */
object Commons extends PExec with PackratParsers {
  override type Target = tptp.Commons.TPTPInput
  override def target = input

  def input: Parser[tptp.Commons.TPTPInput] = null

  def formula: Parser[tptp.Commons.AnnotatedFormula] = null
}
