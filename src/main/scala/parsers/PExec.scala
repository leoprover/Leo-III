package parsers

import scala.util.parsing.combinator.RegexParsers

/**
 * Created by lex on 3/23/14.
 */
trait PExec extends RegexParsers {
  def exec[Target](input: String, target: Parser[Target]) = parseAll(target, input) match {
    case Success(x, in)  =>  Some(x)
    case f => None
  }

  type Target
  def target : Parser[Target]

  def exec(input: String): Option[Target] = exec(input, target)

}
