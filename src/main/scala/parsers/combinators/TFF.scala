package parsers.combinators

import parsers.PExec
import scala.util.parsing.combinator.PackratParsers

/**
 * Created by lex on 3/23/14.
 */
object TFF extends Commons with PackratParsers {
  override type Target = tptp.TFF
  override def target = null

  def tffFormula: Parser[tptp.TFF] = ???
}
