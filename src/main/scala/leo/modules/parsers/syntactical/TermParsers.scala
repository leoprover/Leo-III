package leo.modules.parsers.syntactical

import scala.util.parsing.combinator.syntactical.TokenParsers
import scala.util.parsing.combinator.PackratParsers
import scala.util.parsing.input.Reader

import leo.modules.parsers.lexical.{TPTPLexical, TPTPTokens}
import leo.datastructures.internal.Term
import leo.datastructures.internal.Term.{mkAtom, mkBound}


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

  /////////////////////////////////////
  // General combinators
  /////////////////////////////////////

  type Output = (String, Term, String)

  //  Files
  def tptpFile: Parser[List[Output]] = rep(tptpInput) ^^ {_.flatten[List[Output]]}

  def tptpInput: Parser[List[Output]] = annotatedFormula ^^ {List(_)} | include

  // Formula records
  def annotatedFormula: Parser[Output] = tpiAnnotated | thfAnnotated | tffAnnotated | fofAnnotated | cnfAnnotated

  def tpiAnnotated: Parser[Output] =
    (elem(TPI) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ _ => (name,formula,role)
    }

  def thfAnnotated: Parser[Output] =
    (elem(THF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ thfFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ _ => (name,formula,role)
    }

  def tffAnnotated: Parser[Output] =
    (elem(TFF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ tffFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ _ => (name,formula,role)
    }

  def fofAnnotated: Parser[Output] =
    (elem(FOF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ fofFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ _ => (name,formula,role)
    }

  def cnfAnnotated: Parser[Output] =
    (elem(CNF) ~ elem(LeftParenthesis)) ~> name ~ (elem(Comma) ~> formulaRole <~ elem(Comma)) ~ cnfFormula ~ annotations <~ elem(RightParenthesis) ~ elem(Dot)  ^^ {
      case name ~ role ~ formula ~ _ => (name,formula,role)
    }

  // Additional infos
  def annotations: Parser[Nothing] =
    opt(elem(Comma) ~> source ~ optionalInfo) ^^^ { }

  def formulaRole: Parser[String] = elem("Lower word", _.isInstanceOf[LowerWord]) ^^ {_.chars}


  // Include directives
  def include: Parser[List[Output]] = (
    (elem(Include) ~ elem(LeftParenthesis)) ~> elem("Single quoted", _.isInstanceOf[SingleQuoted])
      ~ opt((elem(Comma) ~ elem(LeftBracket)) ~> repsep(name,elem(Comma)) <~ elem(RightBracket))
      <~ (elem(RightParenthesis) ~ elem(Dot)) ^^ {
      case SingleQuoted(data) ~ Some(names) => parseIncluded(data, names)
      case SingleQuoted(data) ~ _           => parseIncluded(data)
    }
    )

  def parseIncluded(pathToFile: String, selection: List[String]): List[Output] = parseIncluded(pathToFile) // TODO: Change to selective import
  def parseIncluded(pathToFile: String): List[Output] = parse()

  // General purpose
  def name: Parser[String] = (
        atomicWord
      | elem("integer", _.isInstanceOf[Integer]) ^^ {_.chars}
    )
  def atomicWord: Parser[String] = (
        elem("lower word", _.isInstanceOf[LowerWord])         ^^ {_.chars}
      | elem("single quoted", _.isInstanceOf[SingleQuoted])   ^^ {_.chars}
    )

  def atomicDefinedWord: Parser[String] = elem("Dollar word", _.isInstanceOf[DollarWord]) ^^ {_.chars}
  def atomicSystemWord: Parser[String] = elem("Dollar Dollar word", _.isInstanceOf[DollarDollarWord]) ^^ {_.chars}
  def number: Parser[Double] = (
    elem("Integer", _.isInstanceOf[Integer]) ^^ {_.asInstanceOf[Integer].value.toDouble}
      | elem("Real", _.isInstanceOf[Real]) ^^ {x => (x.asInstanceOf[Real].coeff.toString + "E" + x.asInstanceOf[Real].exp.toString).toDouble}
      | elem("Rational", _.isInstanceOf[Rational]) ^^ {x => (x.asInstanceOf[Rational]).p / (x.asInstanceOf[Rational]).q}
    )

  def fileName: Parser[String] = elem("single quoted", _.isInstanceOf[SingleQuoted]) ^^ {_.chars}
}


