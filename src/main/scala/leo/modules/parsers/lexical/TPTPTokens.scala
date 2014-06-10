package leo.modules.parsers.lexical

import scala.util.parsing.combinator.token.Tokens

/**
 * Tokens for the TPTP problem language.
 *
 * @author Alexander Steen
 * @since 10.04.2014
 */
trait TPTPTokens extends Tokens {
  case class SingleQuoted(data: String) extends Token {
    override def chars = "'" + data + "'"
  }
  case class DistinctObject(data: String) extends Token {
    override def chars = "\"" + data + "\""
  }
  case class DollarWord(data: String) extends Token {
    override def chars = data
  }
  case class DollarDollarWord(data: String) extends Token {
    override def chars = data
  }
  case class UpperWord(data: String) extends Token {
    override def chars = data
  }
  case class LowerWord(data: String) extends Token {
    override def chars = data
  }

  // %----Tokens used in syntax, and cannot be character classes
  case object VLine extends Token {
    override def chars = "|"
  }
  case object Star extends Token {
    override def chars = "*"
  }
  case object Plus extends Token {
    override def chars = "+"
  }
  case object Minus extends Token {
    override def chars = "-"
  }
  case object Arrow extends Token {
    override def chars = ">"
  }
  case object LessSign extends Token {
    override def chars = "<"
  }

  case object Application extends Token {
    override def chars = "@"
  }
  case object Lambda extends Token {
    override def chars = "^"
  }

  // Keywords
  case object FOF extends Token {
    override def chars = "fof"
  }
  case object CNF extends Token {
    override def chars = "cnf"
  }
  case object THF extends Token {
    override def chars = "thf"
  }
  case object TFF extends Token {
    override def chars = "tff"
  }
  case object TPI extends Token {
    override def chars = "tpi"
  }
  case object Include extends Token {
    override def chars = "include"
  }

  // Punctuation
  case object LeftParenthesis extends Token {
    override def chars = "("
  }
  case object RightParenthesis extends Token {
    override def chars = ")"
  }
  case object LeftBracket extends Token {
    override def chars = "["
  }
  case object RightBracket extends Token {
    override def chars = "]"
  }
  case object Comma extends Token {
    override def chars = ","
  }
  case object Dot extends Token {
    override def chars = "."
  }
  case object Colon extends Token {
    override def chars = ":"
  }

  // Operators
  case object Exclamationmark extends Token {
    override def chars = "!"
  }
  case object Questionmark extends Token {
    override def chars = "?"
  }
  case object Tilde extends Token {
    override def chars = "~"
  }
  case object Ampersand extends Token {
    override def chars = "&"
  }
  case object Leftrightarrow extends Token {
    override def chars = "<=>"
  }
  case object Rightarrow extends Token {
    override def chars = "=>"
  }
  case object Leftarrow extends Token {
    override def chars = "<="
  }
  case object Leftrighttildearrow extends Token {
    override def chars = "<~>"
  }
  case object TildePipe extends Token {
    override def chars = "~|"
  }
  case object TildeAmpersand extends Token {
    override def chars = "~&"
  }

  // Predicates
  case object Equals extends Token {
    override def chars = "="
  }
  case object NotEquals extends Token {
    override def chars = "!="
  }

  // %----Numbers. Signs are made part of the same token here.
  case class Real(coeff: Double, exp: Int) extends Token {
    override def chars = coeff.toString + "e" + exp.toString
  }
  case class Rational(p: Int, q: Int) extends Token {
    override def chars = p.toString + "/" + q.toString
  }
  case class Integer(value: Int) extends Token {
    override def chars = value.toString
  }
}