package leo.modules.parsers

import java.io.{BufferedReader, StringReader}

import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.thf.{LogicFormula => THFFormula}
import leo.datastructures.tptp.tff.{LogicFormula => TFFFormula}
import leo.datastructures.tptp.fof.{LogicFormula => FOFFormula}
import leo.datastructures.tptp.cnf.{Formula => CNFFormula}

import scala.annotation.tailrec
import scala.io.Source

object TPTPKloeppelParser {

  def problem(input: Source): TPTPInput = ???
  def problem(input: String): TPTPInput = problem(io.Source.fromString(input))

  def annotated(annotatedFormula: String): AnnotatedFormula = ???
  def annotatedTHF(annotatedFormula: String): THFAnnotated = ???
  def annotatedTFF(annotatedFormula: String): TFFAnnotated = ???
  def annotatedFOF(annotatedFormula: String): FOFAnnotated = ???
  def annotatedCNF(annotatedFormula: String): CNFAnnotated = ???

  def thf(formula: String): THFFormula = ???
  def tff(formula: String): TFFFormula = ???
  def fof(formula: String): FOFFormula = ???
  def cnf(formula: String): CNFFormula = ???

  class TPTPParseException(message: String, val line: Int, val offset: Int) extends RuntimeException(message)

  final class TPTPLexer(input: Source) extends Iterator[TPTPLexer.TPTPLexerToken] {
    private[this] final lazy val iter = input.buffered
    private[this] var curLine: Int = 1
    private[this] var curOffset: Int = 1

    @inline private[this] def line(): Unit = { curLine += 1; curOffset = 1 }
    @inline private[this] def step(): Unit = { curOffset += 1 }
    @inline private[this] def consume(): Unit = { iter.next(); step() }

    @tailrec
    override def hasNext: Boolean = iter.hasNext && {
      val ch = iter.head
      // ignore newlines
      if (ch == '\n') { consume(); line(); hasNext }
      else if (ch == '\r') {
        consume()
        if (iter.hasNext && iter.head == '\n') consume()
        line()
        hasNext
      }
      // ignore whitespace characters (ch.isWhitespace also matches linebreaks; so careful when re-ordering lines)
      else if (ch.isWhitespace) { consume(); hasNext }
      // ignore block comments: consume everything until end of comment block
      else if (ch == '/') {
        consume()
        if (iter.hasNext && iter.head == '*') {
          consume()
          // it is a block comment. consume everything until end of block
          var done = false
          while (!done) {
            while (iter.hasNext && iter.head != '*') {
              if (iter.head == '\n') { consume(); line() }
              else if (iter.head == '\r') {
                consume()
                if (iter.hasNext && iter.head == '\n') { consume() }
                line()
              } else { consume() }
            }
            if (iter.hasNext) {
              // iter.head equals '*', consume first
              consume()
              if (iter.hasNext) {
                if (iter.head == '/') {
                  done = true
                  consume()
                }
              } else {
                // Unclosed comment is a parsing error
                throw new TPTPParseException(s"Unclosed block comment", curLine, curOffset)
              }
            } else {
              // Unclosed comment is a parsing error
              throw new TPTPParseException(s"Unclosed block comment", curLine, curOffset)
            }
          }
          hasNext
        } else {
          // There cannot be a token starting with '/'
          throw new TPTPParseException(s"Unrecognized token '/${iter.head}'", curLine, curOffset-1)
        }
      }
      // ignore line comments: consume percentage sign and everything else until newline
      else if (ch == '%') {
        consume()
        while (iter.hasNext && (iter.head != '\n' || iter.head != '\r')) { consume() }
        // dont need to check rest, just pass to recursive call
        hasNext
      }
      // everything else
      else true
    }

    override def next(): TPTPLexer.TPTPLexerToken = {
      import TPTPLexer.TPTPLexerTokenType._

      if (!hasNext) throw new NoSuchElementException // also to remove ignored input such as comments etc.
      else {
        val ch = iter.head
        // BIG switch case over all different possibilities.
        ch match {
          // most frequent tokens
          case '(' => LPAREN
          case ')' => RPAREN
          case '[' => LBRACKET
          case ']' => RBRACKET
          case _ if ch.isLower && ch <= 'z' => ??? // lower word
          case _ if ch.isUpper && ch <= 'Z' => ??? // upper word
          case ',' => COMMA
          case '$' => ??? // doller word or doller doller word
          case ':' => ??? // COLON or Assignment
          // connectives
          case '|' => OR
          case '&' => AND
          case '^' => LAMBDA
          case '<' => ??? // IFF, NIFF, IF, but also subtype
          case '=' => ??? // IMPL or EQUALS
          case '~' => ??? // NOT, NAND, or NOR
          case '!' => ??? // FORALL, FORALLCOMB, TYFORAL, or NOTEQUALS
          case '?' => ??? // EXISTS, TYEXISTS, EXISTSCOMB
          case '@' => ??? // CHOICE, DESC, COMBS of that and EQ, and APP  
          // remaining tokens
          case '*' => STAR
          case '+' => PLUS // TODO: Plus can also start a number
          case '>' => RANGLE
          case '.' => DOT
          case '\'' => ??? // single quoted
          case '"' => ??? // double quoted
          case '-' => ??? // Can start a number, or a sequent arrow
          case '{' => LBRACES
          case '}' => RBRACES
        }
        ???
      }
    }

  }
  object TPTPLexer {
    type TPTPLexerToken = (TPTPLexerTokenType, Any, LineNo, Offset) // Cast Any to whatever it should be
    type TPTPLexerTokenType = TPTPLexerTokenType.TPTPLexerTokenType
    type LineNo = Int
    type Offset = Int

    object TPTPLexerTokenType extends Enumeration {
      type TPTPLexerTokenType = Value
      val REAL, RATIONAL, INT,
          DOLLARWORD, DOLLARDOLLARWORD, UPPERWORD, LOWERWORD,
          SINGLEQUOTED, DOUBLEQUOTED,
          OR, AND, IFF, IMPL, IF,
          NOR, NAND, NIFF, NOT,
          FORALL, EXISTS, FORALLCOMB, EXISTSCOMB,
          EQUALS, NOTEQUALS, EQCOMB, LAMBDA, APP,
          CHOICE, DESCRIPTION, CHOICECOMB, DESCRIPTIONCOMB,
          TYFORALL, TYEXISTS, ASSIGNMENT,
          SUBTYPE,
          LPAREN, RPAREN, LBRACKET, RBRACKET, LBRACES, RBRACES,
          COMMA, DOT, COLON,
          RANGLE, STAR, PLUS,
          SEQUENTARROW = Value
    }
  }


}
