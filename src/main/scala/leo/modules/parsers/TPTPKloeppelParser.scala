package leo.modules.parsers


import java.util.NoSuchElementException

import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.thf.{LogicFormula => THFFormula}
import leo.datastructures.tptp.tff.{LogicFormula => TFFFormula}
import leo.datastructures.tptp.fof.{LogicFormula => FOFFormula}
import leo.datastructures.tptp.cnf.{Formula => CNFFormula}

import scala.annotation.{switch, tailrec}
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

    private[this] var curItem: TPTPLexer.TPTPLexerToken = _

    @inline private[this] def line(): Unit = { curLine += 1; curOffset = 1 }
    @inline private[this] def step(): Unit = { curOffset += 1 }
    @inline private[this] def consume(): Char = { val res = iter.next(); step(); res }
    @inline private[this] def isLowerAlpha(ch: Char): Boolean = ch.isLower && ch <= 'z' // only select ASCII
    @inline private[this] def isUpperAlpha(ch: Char): Boolean = ch.isUpper && ch <= 'Z' // only select ASCII
    @inline private[this] def isAlpha(ch: Char): Boolean = isLowerAlpha(ch) || isUpperAlpha(ch)
    @inline private[this] def isNumeric(ch: Char): Boolean = ch.isDigit && ch <= '9' // only select ASCII
    @inline private[this] def isNonZeroNumeric(ch: Char): Boolean = ch > '0' && ch <= '9' // only select ASCII
    @inline private[this] def isAlphaNumeric(ch: Char): Boolean = isAlpha(ch) || isNumeric(ch) || ch == '_'

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
        while (iter.hasNext && (iter.head != '\n' && iter.head != '\r')) { consume() }
        // dont need to check rest, just pass to recursive call
        hasNext
      }
      // everything else
      else true
    }

    override def next(): TPTPLexer.TPTPLexerToken = {
      if (curItem == null) {
        getNextToken
      } else {
        val result = curItem
        curItem = null
        result
      }
    }

    def peek(): TPTPLexer.TPTPLexerToken = {
      if (curItem == null) {
        curItem = getNextToken
        curItem
      } else {
        curItem
      }
    }

    private[this] def getNextToken: TPTPLexer.TPTPLexerToken = {
      import TPTPLexer.TPTPLexerTokenType._

      if (!hasNext) throw new NoSuchElementException // also to remove ignored input such as comments etc.
      else {
        val ch = consume()
        // BIG switch case over all different possibilities.
        ch match {
          // most frequent tokens
          case '(' => tok(LPAREN, 1)
          case ')' => tok(RPAREN, 1)
          case '[' => tok(LBRACKET, 1)
          case ']' => tok(RBRACKET, 1)
          case _ if isLowerAlpha(ch) => // lower word
            val offset = curOffset-1
            val payload = collectAlphaNums(ch)
            (LOWERWORD, payload, curLine, offset)
          case _ if isUpperAlpha(ch) && ch <= 'Z' => // upper word
            val offset = curOffset-1
            val payload = collectAlphaNums(ch)
            (UPPERWORD, payload, curLine, offset)
          case ',' => tok(COMMA, 1)
          case '$' =>  // doller word or doller doller word
            val offset = curOffset-1
            if (iter.hasNext) {
              if (iter.head == '$') { // DollarDollarWord
                consume()
                if (iter.hasNext && isAlphaNumeric(iter.head)) {
                  val payload = collectAlphaNums(ch)
                  (DOLLARDOLLARWORD, "$" ++ payload, curLine, offset)
                } else {
                  throw new TPTPParseException(s"Unrecognized token: Invalid or empty DollarDollarWord)", curLine, offset)
                }
              } else if (isAlphaNumeric(iter.head)) {
                val payload = collectAlphaNums(ch)
                (DOLLARWORD, payload, curLine, offset)
              } else
                throw new TPTPParseException(s"Unrecognized token '$$${iter.head}' (invalid dollar word)", curLine, offset)
            } else {
              throw new TPTPParseException("Unrecognized token '$' (empty dollar word)", curLine, offset)
            }
          case ':' => // COLON or Assignment
            if (iter.hasNext && iter.head == '=') {
              consume()
              tok(ASSIGNMENT, 2)
            } else
              tok(COLON, 1)
          // connectives
          case '|' => tok(OR, 1)
          case '&' => tok(AND, 1)
          case '^' => tok(LAMBDA, 1)
          case '<' => // IFF, NIFF, IF, but also subtype
            if (iter.hasNext && iter.head == '<') {
              consume()
              tok(SUBTYPE, 2)
            } else if (iter.hasNext && iter.head == '=') {
              consume()
              if (iter.hasNext && iter.head == '>') {
                consume()
                tok(IFF, 3)
              } else {
                tok(IF, 2)
              }
            } else if (iter.hasNext && iter.head == '~') {
              consume()
              if (iter.hasNext && iter.head == '>') {
               consume()
                tok(NIFF, 3)
              } else {
                throw new TPTPParseException("Unrecognized token '<~'", curLine, curOffset-2)
              }
            } else
              throw new TPTPParseException("Unrecognized token '<'", curLine, curOffset-1)
          case '=' => // IMPL or EQUALS
            if (iter.hasNext && iter.head == '>') {
              consume()
              tok(IMPL, 2)
            } else
              tok(EQUALS, 1)
          case '~' => // NOT, NAND, or NOR
            if (iter.hasNext && iter.head == '&') {
              consume()
              tok(NAND, 2)
            } else if (iter.hasNext && iter.head == '|') {
              consume()
              tok(NOR, 2)
            } else
              tok(NOT, 1)
          case '!' => // FORALL, FORALLCOMB, TYFORAL, or NOTEQUALS
            if (iter.hasNext && iter.head == '!') {
              consume()
              tok(FORALLCOMB, 2)
            } else if (iter.hasNext && iter.head == '=') {
              consume()
              tok(NOTEQUALS, 2)
            } else if (iter.hasNext && iter.head == '>') {
              consume()
              tok(TYFORALL, 2)
            } else
              tok(FORALL, 1)
          case '?' => // EXISTS, TYEXISTS, EXISTSCOMB
            if (iter.hasNext && iter.head == '?') {
              consume()
              tok(EXISTSCOMB, 2)
            } else if (iter.hasNext && iter.head == '*') {
              consume()
              tok(TYEXISTS, 2)
            } else
              tok(EXISTS, 1)
          case '@' => // CHOICE, DESC, COMBS of that and EQ, and APP
            if (iter.hasNext && iter.head == '+') {
              consume()
              tok(CHOICE, 2)
            } else if (iter.hasNext && iter.head == '-') {
              consume()
              tok(DESCRIPTION, 2)
            } else if (iter.hasNext && iter.head == '@') {
              consume()
              if (iter.hasNext && iter.head == '+') {
                tok(CHOICECOMB, 3)
              } else if (iter.hasNext && iter.head == '-') {
                tok(DESCRIPTIONCOMB, 3)
              } else {
                throw new TPTPParseException("Unrecognized token '@@'", curLine, curOffset-2)
              }
            } else
              tok(APP, 1)
          // remaining tokens
          case _ if isNumeric(ch) => // numbers
            generateNumberToken(ch)
          case '*' => tok(STAR, 1)
          case '+' => // PLUS or number
            if (iter.hasNext && isNumeric(iter.head)) {
              generateNumberToken(ch)
            } else tok(PLUS, 1)
          case '>' => tok(RANGLE, 1)
          case '.' => tok(DOT, 1)
          case '\'' => // single quoted
            val payload = collectSQChars()
            (SINGLEQUOTED, payload, curLine, curOffset-payload.length)
          case '"' => // double quoted
            val payload = collectDQChars()
            (DOUBLEQUOTED, payload, curLine, curOffset-payload.length)
          case '-' => // Can start a number, or a sequent arrow
            if (iter.hasNext && isNumeric(iter.head)) {
              generateNumberToken(ch)
            } else if (iter.hasNext && iter.head == '-') {
              consume()
              if (iter.hasNext && iter.head == '>') {
                consume()
                tok(SEQUENTARROW, 3)
              } else {
                throw new TPTPParseException(s"Unrecognized token '--'", curLine, curOffset-2)
              }
            } else {
              throw new TPTPParseException(s"Unrecognized token '-'", curLine, curOffset-1)
            }
          case '{' => tok(LBRACES, 1)
          case '}' => tok(RBRACES, 1)
          case _ => throw new TPTPParseException(s"Unrecognized token '$ch'", curLine, curOffset-1)
        }
      }
    }
    @inline private[this] def tok(tokType: TPTPLexer.TPTPLexerTokenType, length: Int): TPTPLexer.TPTPLexerToken =
      (tokType, null, curLine, curOffset-length)

    @inline private[this] def generateNumberToken(signOrFirstDigit: Char): TPTPLexer.TPTPLexerToken = {
      import TPTPLexer.TPTPLexerTokenType._
      val sb: StringBuilder = new StringBuilder
      sb.append(signOrFirstDigit)
      // iter.head is a digit if signOrFirstDigit is + or -
      val firstNumber = collectNumbers()
      sb.append(firstNumber)
      if (iter.hasNext && iter.head == '/') {
        sb.append(consume())
        if (iter.hasNext && isNonZeroNumeric(iter.head)) {
          val secondNumber = collectNumbers()
          sb.append(secondNumber)
          (RATIONAL, sb.toString(), curLine, curOffset-sb.length())
        } else throw new TPTPParseException(s"Unexpected end of rational token '${sb.toString()}'", curLine, curOffset-sb.length())
      } else {
        var isReal = false
        if (iter.hasNext && iter.head == '.') {
          isReal = true
          sb.append(consume())
          if (iter.hasNext && isNumeric(iter.head)) {
            val secondNumber = collectNumbers()
            sb.append(secondNumber)
          } else throw new TPTPParseException(s"Unexpected end of real number token '${sb.toString()}'", curLine, curOffset-sb.length())
        }
        if (iter.hasNext && (iter.head == 'E' || iter.head == 'e')) {
          isReal = true
          sb.append(consume())
          if (iter.hasNext && (iter.head == '+' || iter.head == '-')) {
            sb.append(consume())
          }
          if (iter.hasNext && isNumeric(iter.head)) {
            val exponent = collectNumbers()
            sb.append(exponent)
          } else throw new TPTPParseException(s"Unexpected end of real number token '${sb.toString()}'", curLine, curOffset-sb.length())
        }
        if (isReal) (REAL, sb.toString(), curLine, curOffset - sb.length())
        else  (INT, sb.toString(), curLine, curOffset-sb.length())
      }
    }

    @inline private[this] def collectNumbers(): StringBuilder = {
      val sb: StringBuilder = new StringBuilder
      while (iter.hasNext && isNumeric(iter.head)) {
        sb.append(consume())
      }
      sb
    }
    @inline private[this] def collectAlphaNums(startChar: Char): String = {
      val sb: StringBuilder = new StringBuilder()
      sb.append(startChar)
      while (iter.hasNext && isAlphaNumeric(iter.head)) {
        sb.append(consume())
      }
      sb.toString()
    }
    @inline private[this] def isSQChar(char: Char): Boolean = !char.isControl && char <= '~' && char != '\\' && char != '\''
    @inline private[this] def isDQChar(char: Char): Boolean = !char.isControl && char <= '~' && char != '\\' && char != '"'
    @inline private[this] def collectSQChars(): String = {
      val sb: StringBuilder = new StringBuilder()
      // omit starting '
      var done = false
      while (!done) {
        while (iter.hasNext && isSQChar(iter.head)) {
          sb.append(consume())
        }
        if (iter.hasNext) {
          if (iter.head == '\'') { // end of single quoted string
            consume()
            done = true
          } else if (iter.head == '\\') {
            consume()
            if (iter.hasNext && (iter.head == '\\' || iter.head == '\'')) {
              sb.append(consume())
            } else throw new TPTPParseException(s"Unexpected escape character '\' within single quoted string", curLine, curOffset-sb.length()-2)
          } else throw new TPTPParseException(s"Unexpected token within single quoted string", curLine, curOffset-sb.length()-1)
        } else throw new TPTPParseException(s"Unclosed single quoted string", curLine, curOffset-sb.length()-1)
      }
      sb.toString()
    }
    @inline private[this] def collectDQChars(): String = {
      val sb: StringBuilder = new StringBuilder()
      sb.append('"')
      var done = false
      while (!done) {
        while (iter.hasNext && isDQChar(iter.head)) {
          sb.append(consume())
        }
        if (iter.hasNext) {
          if (iter.head == '"') { // end of double quoted string
            sb.append(consume())
            done = true
          } else if (iter.head == '\\') {
            consume()
            if (iter.hasNext && (iter.head == '\\' || iter.head == '"')) {
              sb.append(consume())
            } else throw new TPTPParseException(s"Unexpected escape character '\' within double quoted string", curLine, curOffset-sb.length()-2)
          } else throw new TPTPParseException(s"Unexpected token within double quoted string", curLine, curOffset-sb.length()-1)
        } else throw new TPTPParseException(s"Unclosed double quoted string", curLine, curOffset-sb.length()-1)
      }
      sb.toString()
    }
  }
  object TPTPLexer {
    type TPTPLexerToken = (TPTPLexerTokenType, Any, LineNo, Offset) // Cast Any to whatever it should be
    type TPTPLexerTokenType = TPTPLexerTokenType.TPTPLexerTokenType
    type LineNo = Int
    type Offset = Int

    final object TPTPLexerTokenType extends Enumeration {
      type TPTPLexerTokenType = Value
      final val REAL, RATIONAL, INT,
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

  final class TPTPParser(tokens: TPTPLexer) {
    import TPTPLexer.TPTPLexerTokenType._
    private[this] var lastTok: TPTPLexer.TPTPLexerToken = _

    def annotatedFormula: Any = ???

    def annotatedTHF(): Any = {
      try {
//        m(LOWERWORD)._2.asInstanceOf[String] == "thf" // ??
//        m(LPAREN)
//        val fn = name()
//        m(COMMA)
//        val fr = role()
//        m(COMMA)
//        val f = thfFormula()
//        if(om(COMMA)) {
//          annotation()
//        }
//        m(RPAREN)
//        m(DOT)
      } catch {
        case e:NoSuchElementException => if (lastTok == null) throw new TPTPParseException("Parse error: Empty input", -1, -1)
        else throw new TPTPParseException("Parse error: Unexpected end of input", lastTok._3, lastTok._4)
      }
    }

    private[this] def m(tokType: TPTPLexer.TPTPLexerTokenType.Value): TPTPLexer.TPTPLexerToken = {
      if (tokens.peek()._1 == tokType) {
        tokens.next()
      } else throw new TPTPParseException("asd", -1, -1)
    }

  }
  object TPTPParser {

  }

}
