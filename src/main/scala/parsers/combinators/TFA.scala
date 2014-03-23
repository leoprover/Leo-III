package parsers.combinators

import parsers.PExec
import scala.util.parsing.combinator.PackratParsers

/**
 * Created by lex on 3/23/14.
 */
object TFA extends PExec with PackratParsers {
  override type Target = tptp.TFA
  override def target = null
}