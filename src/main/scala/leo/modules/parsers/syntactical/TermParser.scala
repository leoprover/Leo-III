package leo.modules.parsers.syntactical

import scala.util.parsing.combinator.syntactical.TokenParsers
import scala.util.parsing.combinator.PackratParsers
import leo.modules.parsers.lexical.{TPTPLexical, TPTPTokens}
import scala.util.parsing.input.Reader


/**
 * A parser that reads input in TPTP format (as described by [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]])
 * and yields a representation of the input as [[leo.datastructures.internal.Term]].
 *
 * @author Alexander Steen
 * @since 11.06.2014
 */
object TermParser extends TokenParsers with PackratParsers {
  type Tokens = TPTPTokens
  val lexical = new TPTPLexical

  /** Methods for parsing and tokenizing whole input streams */

  def parse[Target](input: String, parser: Parser[Target]) = {
    val tokens = new lexical.Scanner(input)
    phrase(parser)(tokens)
  }

  def parse[Target](input: Reader[Char], parser: Parser[Target]) = {
    val tokens = new lexical.Scanner(input)
    phrase(parser)(tokens)
  }

  def tokens(input: String) = {
    new lexical.Scanner(input)
  }

  def tokens(input: Reader[Char]) = {
    new lexical.Scanner(input)
  }

  // From here on the combinators are implemented according to the syntax bnf declaration
  // from http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html
  // (Almost) each single bnf rule is reflected by a combinator declaration, where the name is adjusted
  // to camelCase format. Where possible, several rules are contracted to a single rule.

  // A Packratparser is used when the bnf is left-recursive, such as in `thfOrFormula` and many other.

  import lexical._

  // ....
}
