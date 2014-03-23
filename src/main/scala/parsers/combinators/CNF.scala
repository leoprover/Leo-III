package parsers.combinators


import parsers.PExec
import scala.util.parsing.combinator.PackratParsers

/**
 * Created by lex on 3/23/14.
 */
object CNF extends PExec with PackratParsers {
  override type Target = tptp.CNF
  override def target = null
}