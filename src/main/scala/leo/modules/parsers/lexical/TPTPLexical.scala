package leo.modules.parsers.lexical

import scala.util.parsing.combinator.lexical.Lexical
import scala.util.parsing.input.CharArrayReader._
import scala.Some

class TPTPLexical extends Lexical with TPTPTokens {
  def token = (
    singleQuoted
      | distinctObject
      | dollarWord
      | dollarDollarWord
      | real
      | rational
      | integer
      | upperWord
      | acceptSeq("include".toList)         ^^^ Include
      | acceptSeq("fof".toList)             ^^^ FOF
      | acceptSeq("cnf".toList)             ^^^ CNF
      | acceptSeq("thf".toList)             ^^^ THF
      | acceptSeq("tff".toList)             ^^^ TFF
      | acceptSeq("tpi".toList)             ^^^ TPI
      | lowerWord
      | '*'                                 ^^^ Star
      | '+'                                 ^^^ Plus
      | '-'                                 ^^^ Minus
      | '@'                                 ^^^ Application
      | '^'                                 ^^^ Lambda
      | '('                                 ^^^ LeftParenthesis
      | ')'                                 ^^^ RightParenthesis
      | '['                                 ^^^ LeftBracket
      | ']'                                 ^^^ RightBracket
      | ','                                 ^^^ Comma
      | '.'                                 ^^^ Dot
      | ':'                                 ^^^ Colon
      | '?'                                 ^^^ Questionmark
      // BEGIN The Order of these tokens should not be altered
      | '<' ~ '=' ~ '>'                     ^^^ Leftrightarrow
      | '<' ~ '='                           ^^^ Leftarrow
      | '=' ~ '>'                           ^^^ Rightarrow
      | '<' ~ '~' ~ '>'                     ^^^ Leftrighttildearrow
      | '<'                                 ^^^ LessSign
      | '~' ~ '|'                           ^^^ TildePipe
      | '~' ~ '&'                           ^^^ TildeAmpersand
      | '~'                                 ^^^ Tilde
      | '|'                                 ^^^ VLine
      | '&'                                 ^^^ Ampersand
      | '>'                                 ^^^ Arrow
      | '!' ~ '='                           ^^^ NotEquals
      | '!'                                 ^^^ Exclamationmark
      | '='                                 ^^^ Equals
      // END
      | EofCh                               ^^^ EOF
      | failure ("unexcepted character")
    )

  def whitespace: Parser[Any] = rep(
    whitespaceChar
      | '/' ~ '*' ~ commentBlock
      | '%' ~ rep( chrExcept(EofCh, '\n'))
      | '/' ~ '*' ~ failure("Lexer error: Unclosed comment block")
  )

  protected def commentBlock: Parser[Any] = (
    '*' ~ '/' ^^ { case _ =>' ' }
      | chrExcept(EofCh) ~ commentBlock
    )

  def singleQuoted: Parser[SingleQuoted] =
    '\'' ~ rep1(sqChar) ~ '\'' ^^ {case '\'' ~ content ~ '\'' => SingleQuoted(content.mkString(""))}

  protected def sqChar = (
      chrExcept('\'', '\\', '\n', EofCh)
    | '\\' ~ '\'' ^^ { case '\\' ~ '\'' => "'"}
    | '\\' ~ '\\' ^^ { case '\\' ~ '\\' => "\\"}
  )

  def distinctObject: Parser[DistinctObject] =
    '\"' ~ rep(doChar) ~ '\"' ^^ {case '\"' ~ content ~ '\"' => DistinctObject(content.mkString(""))}

  protected def doChar = (
      chrExcept('\"', '\\', '\n', EofCh)
    | '\\' ~ '\"' ^^ { case '\\' ~ '\"' => "\""}
    | '\\' ~ '\\' ^^ { case '\\' ~ '\\' => "\\"}
  )

  def dollarWord: Parser[DollarWord] = '$' ~ lowerWord ^^ {case '$' ~ word => DollarWord('$' + word.data)}
  def dollarDollarWord: Parser[DollarDollarWord] = '$' ~ '$' ~ lowerWord ^^ {case '$' ~ '$' ~ word => DollarDollarWord("$$" + word.data)}


  def lowerWord: Parser[LowerWord] = elem("lowerword", _.isLower) ~ rep(letter | digit | elem('_')) ^^ {
    case start ~ rest => LowerWord(start :: rest mkString "")
  }
  def upperWord: Parser[UpperWord] = elem("upperword", _.isUpper) ~ rep(letter | digit | elem('_')) ^^ {
    case start ~ rest => UpperWord(start :: rest mkString "")
  }

  def integer: Parser[Integer] =
    opt(elem('+') | elem('-')) ~ unsignedInteger ^^ {
      case Some('-') ~ i => Integer(-i)
      case _         ~ i => Integer(i)
    }

  def unsignedInteger: Parser[Int] = digit ~ rep(digit) ^^ {
    case d0 ~ ds => (d0 :: ds).mkString("").toInt
  }

  def rational: Parser[Rational] = opt(elem('+') | elem('-')) ~ unsignedInteger ~ '/' ~ unsignedInteger ^^ {
    //case _         ~ _ ~ '/' ~ 0 => failure ("Division by zero not allowed")
    case Some('-') ~ p ~ '/' ~ q => Rational(-p,q)
    case _         ~ p ~ '/' ~ q => Rational(p,q)
  }

  def real: Parser[Real] = opt(elem('+') | elem('-')) ~ (exponent ||| fraction) ^^ {
    case Some('-') ~ v => Real(-v.coeff, v.exp)
    case _ ~ v => v
  }

  protected def fraction: Parser[Real] = unsignedInteger ~ '.' ~ digit ~ rep(digit) ^^ {
    case i ~ '.' ~ d0 ~ ds => Real((i.toString ++ "." ++ (d0 :: ds).mkString("")).toDouble, 1)
  }

  protected def exponent: Parser[Real] = (unsignedInteger ||| fraction) ~ (elem('E') | elem('e')) ~ integer ^^ {
    case (i: Int) ~ _ ~ exp => Real(i, exp.value)
    case (d: Real) ~ _ ~ exp => Real(d.coeff, exp.value)
  }
}
