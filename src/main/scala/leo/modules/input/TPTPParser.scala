package leo.modules.input

import java.util.NoSuchElementException
import scala.annotation.tailrec
import scala.io.Source

object TPTPParser {
  import leo.datastructures.TPTPAST.{Problem, AnnotatedFormula, THFAnnotated, TFFAnnotated,
    FOFAnnotated, CNFAnnotated, TPIAnnotated}
  import leo.datastructures.TPTPAST.THF.{Formula => THFFormula}
  import leo.datastructures.TPTPAST.TFF.{Formula => TFFFormula}
  import leo.datastructures.TPTPAST.FOF.{Formula => FOFFormula}
  import leo.datastructures.TPTPAST.CNF.{Formula => CNFFormula}

  class TPTPParseException(message: String, val line: Int, val offset: Int) extends RuntimeException(message)

  final def problem(input: Source): Problem = {
    val parser = new TPTPParser(new TPTPLexer(input))
    val result = parser.tptpFile()
    parser.EOF()
    result
  }
  @inline final def problem(input: String): Problem = problem(io.Source.fromString(input))

  final def annotated(annotatedFormula: String): AnnotatedFormula = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedFormula()
    parser.EOF()
    result
  }
  final def annotatedTHF(annotatedFormula: String): THFAnnotated = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedTHF()
    parser.EOF()
    result
  }
  final def annotatedTFF(annotatedFormula: String): TFFAnnotated = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedTFF()
    parser.EOF()
    result
  }
  final def annotatedFOF(annotatedFormula: String): FOFAnnotated = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedFOF()
    parser.EOF()
    result
  }
  final def annotatedCNF(annotatedFormula: String): CNFAnnotated = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedCNF()
    parser.EOF()
    result
  }
  final def annotatedTPI(annotatedFormula: String): TPIAnnotated = {
    val parser = parserFromString(annotatedFormula)
    val result = parser.annotatedTPI()
    parser.EOF()
    result
  }

  final def thf(formula: String): THFFormula = {
    val parser = parserFromString(formula)
    val result = parser.thfLogicFormula()
    parser.EOF()
    result
  }
  final def tff(formula: String): TFFFormula = {
    val parser = parserFromString(formula)
    val result = parser.tffLogicFormula()
    parser.EOF()
    result
  }
  final def fof(formula: String): FOFFormula = {
    val parser = parserFromString(formula)
    val result = parser.fofLogicFormula()
    parser.EOF()
    result
  }
  final def cnf(formula: String): CNFFormula = {
    val parser = parserFromString(formula)
    val result = parser.cnfLogicFormula()
    parser.EOF()
    result
  }

  @inline private[this] final def parserFromString(input: String): TPTPParser = new TPTPParser(new TPTPLexer(io.Source.fromString(input)))

  final class TPTPLexer(input: Source) extends collection.BufferedIterator[TPTPLexer.TPTPLexerToken] {
    private[this] final lazy val iter = input.buffered
    private[this] var curLine: Int = 1
    private[this] var curOffset: Int = 1

    private[this] var lookahead: Seq[TPTPLexer.TPTPLexerToken] = Vector.empty


    @inline private[this] def line(): Unit = { curLine += 1; curOffset = 1 }
    @inline private[this] def step(): Unit = { curOffset += 1 }
    @inline private[this] def consume(): Char = { val res = iter.next(); step(); res }
    @inline private[this] def isLowerAlpha(ch: Char): Boolean = ch.isLower && ch <= 'z' // only select ASCII
    @inline private[this] def isUpperAlpha(ch: Char): Boolean = ch.isUpper && ch <= 'Z' // only select ASCII
    @inline private[this] def isAlpha(ch: Char): Boolean = isLowerAlpha(ch) || isUpperAlpha(ch)
    @inline private[this] def isNumeric(ch: Char): Boolean = ch.isDigit && ch <= '9' // only select ASCII
    @inline private[this] def isNonZeroNumeric(ch: Char): Boolean = ch > '0' && ch <= '9' // only select ASCII
    @inline private[this] def isAlphaNumeric(ch: Char): Boolean = isAlpha(ch) || isNumeric(ch) || ch == '_'

    override def hasNext: Boolean = lookahead.nonEmpty || hasNext0
    @tailrec  private[this] def hasNext0: Boolean = iter.hasNext && {
      val ch = iter.head
      // ignore newlines
      if (ch == '\n') { consume(); line(); hasNext0 }
      else if (ch == '\r') {
        consume()
        if (iter.hasNext && iter.head == '\n') consume()
        line()
        hasNext0
      }
      // ignore whitespace characters (ch.isWhitespace also matches linebreaks; so careful when re-ordering lines)
      else if (ch.isWhitespace) { consume(); hasNext0 }
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
          hasNext0
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
        hasNext0
      }
      // everything else
      else true
    }

    override def next(): TPTPLexer.TPTPLexerToken = {
      if (lookahead.isEmpty) {
        getNextToken
      } else {
        val result = lookahead.head
        lookahead = lookahead.tail
        result
      }
    }

    override def head: TPTPLexer.TPTPLexerToken = peek()
    def peek(): TPTPLexer.TPTPLexerToken = peek(0)
    def peek(i: Int): TPTPLexer.TPTPLexerToken = {
      val res = safePeek(i)
      if (res == null) throw new NoSuchElementException("peek on not sufficiently large stream.")
      else res
    }
    def safePeek(i: Int): TPTPLexer.TPTPLexerToken = {
      val i0 = i+1
      if (lookahead.length >= i0) lookahead(i)
      else {
        if(safeExpandLookahead(i0 - lookahead.length)) lookahead(i)
        else null
      }
    }

    @tailrec
    private[this] def safeExpandLookahead(n: Int): Boolean = {
      if (n > 0) {
        if (hasNext) {
          val tok = getNextToken
          lookahead = lookahead :+ tok
          safeExpandLookahead(n-1)
        } else false
      } else true
    }

    private[this] def getNextToken: TPTPLexer.TPTPLexerToken = {
      import TPTPLexer.TPTPLexerTokenType._

      if (!hasNext0) throw new NoSuchElementException // also to remove ignored input such as comments etc.
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
              throw new TPTPParseException(s"Read token 'SUBTYPE' ('<<') that is not supported.", curLine, curOffset-2)
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
            } else if (iter.hasNext && iter.head == '=') {
              consume()
              tok(EQCOMB, 2)
            } else if (iter.hasNext && iter.head == '@') {
              consume()
              if (iter.hasNext && iter.head == '+') {
                consume()
                tok(CHOICECOMB, 3)
              } else if (iter.hasNext && iter.head == '-') {
                consume()
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
                throw new TPTPParseException(s"Read token 'SEQUENT ARROW' ('-->') that is not supported.", curLine, curOffset-3)
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
    type TPTPLexerToken = (TPTPLexerTokenType, String, LineNo, Offset) // Cast Any to whatever it should be
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
    import leo.datastructures.TPTPAST._
    type Token = TPTPLexer.TPTPLexerToken
    type TokenType = TPTPLexer.TPTPLexerTokenType.Value

    private[this] var lastTok: Token = _

    def EOF(): Unit = {
      if (tokens.hasNext) {
        val tok = peek()
        throw new TPTPParseException("Unconsumed input when EOF was expected.", tok._3, tok._4)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // TPTP file related stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    def tptpFile(): Problem = {
      if (!tokens.hasNext) {
        // OK, empty file is fine
        Problem(Vector.empty, Vector.empty)
      } else {
        var formulas: Seq[AnnotatedFormula] = Vector.empty
        var includes: Seq[(String, Seq[String])] = Vector.empty
        while (tokens.hasNext) {
          val t = peek()
          t._1 match {
            case LOWERWORD =>
              t._2 match {
                case "include" => includes = includes :+ include()
                case "thf" | "tff" | "fof" | "tcf" | "cnf" | "tpi" => formulas = formulas :+ annotatedFormula()
                case _ => error1(Seq("thf", "tff", "fof", "tcf", "cnf", "tpi", "include"), t)
              }
            case _ => error1(Seq("thf", "tff", "fof", "tcf", "cnf", "tpi", "include"), t)
          }
        }
        Problem(includes, formulas)
      }
    }

    def include(): (String, Seq[String]) = {
      m(a(LOWERWORD), "include")
      a(LPAREN)
      val filename = a(SINGLEQUOTED)._2
      var fs: Seq[String] = Seq.empty
      val fs0 = o(COMMA, null)
      if (fs0 != null) {
        a(LBRACKET)
        fs = fs :+ name()
        while (o(RBRACKET, null) == null) {
          a(COMMA)
          fs = fs :+ name()
        }
        // RBRACKET already consumed
      }
      a(RPAREN)
      a(DOT)
      (filename, fs)
    }

    def annotatedFormula(): AnnotatedFormula = {
      val t = peek()
      t._1 match {
        case LOWERWORD =>
          t._2 match {
            case "thf" => annotatedTHF()
            case "tff" => annotatedTFF()
            case "fof" => annotatedFOF()
            case "cnf" => annotatedCNF()
            case "tpi" => annotatedTPI()
            case _ => error1(Seq("thf", "tff", "fof", "cnf", "tpi"), t)
          }
        case _ => error1(Seq("thf", "tff", "fof", "cnf", "tpi"), t)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // THF formula stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // Formula level
    ////////////////////////////////////////////////////////////////////////
    def annotatedTHF(): THFAnnotated = {
      m(a(LOWERWORD), "thf")
      a(LPAREN)
      val n = name()
      a(COMMA)
      val r = a(LOWERWORD)._2
      a(COMMA)
      val f = thfFormula()
      var source: GeneralTerm = null
      var info: Seq[GeneralTerm] = null
      val an0 = o(COMMA, null)
      if (an0 != null) {
        source = generalTerm()
        val an1 = o(COMMA, null)
        if (an1 != null) {
          info = generalList()
        }
      }
      a(RPAREN)
      a(DOT)
      if (source == null) THFAnnotated(n, r, f, None)
      else THFAnnotated(n, r, f, Some((source, Option(info))))
    }

    def thfFormula(): THF.Statement = {
      val idx = peekUnder(LPAREN)
      val tok = peek(idx)
      tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARDOLLARWORD if peek(idx+1)._1 == COLON => // Typing
          thfAtomTyping()
        case _ =>
          THF.Logical(thfLogicFormula())
      }
    }

    private[this] def thfAtomTyping(): THF.Typing = {
      val lp = o(LPAREN, null)
      if (lp != null) {
        val res = thfAtomTyping()
        a(RPAREN)
        res
      } else {
        val constant = untypedAtom()
        a(COLON)
        val typ = thfTopLevelType()
        THF.Typing(constant, typ)
      }
    }

    def thfLogicFormula(): THF.Formula = {
      // We want to eliminate backtracking when parsing THF. So change the grammar interpretation as follows
      // Always read units first (thats everything except binary formulas). Then check for following
      // binary connectives and iteratively parse more units (one if non-assoc, as much as possible if assoc).
      // Only allow equality or inequality if the formula parsed first is not a quantification (i.e.
      // a <thf_unitary_term> but not a <thf_unitary_formula> as TPTP would put it).
      val tok = peek()
      val isUnitaryTerm = !isTHFQuantifier(tok._1) && !isUnaryTHFConnective(tok._1)
      val isUnitaryFormula = !isUnaryTHFConnective(tok._1)
      // if direct quantification, parse it (as unit f1) and remember
      // if not: parse as unit f1
      // then
      //  if = or !=, and no direct quantification before, parse unitary term f2. return f1 op f2.
      //  if binary connective non-assoc, parse unit f2. return f1 op f2.
      //  if binary connective assoc. parse unit f2, collect f1 op f2. repeat parse unit until not same op.
      //  if none, return f1
      val f1 = thfUnitFormula(acceptEqualityLike = false)
      if (tokens.hasNext) {
        val next = peek()
        next._1 match {
          case EQUALS | NOTEQUALS if isUnitaryTerm =>
            val op = tokenToTHFEqConnective(consume())
            val nextTok = peek()
            if (isTHFQuantifier(nextTok._1) || isUnaryTHFConnective(nextTok._1)) {
              // not allowed, since we are in <thf_unitary_term> here.
              error2(s"Expected <thf_unitary_term>, but found ${nextTok._1} first. Maybe parentheses are missing around the argument of ${next._1}?", nextTok)
            } else {
              val f2 = thfUnitFormula(acceptEqualityLike = false)//thfUnitaryTerm()
              THF.BinaryFormula(op, f1, f2)
            }
          case c if isBinaryTHFConnective(c) || isBinaryTHFTypeConstructor(c) =>
            if (isBinaryAssocTHFConnective(c)) {
              val opTok = consume()
              val op = tokenToTHFBinaryConnective(opTok)
              val f2 = thfUnitFormula(acceptEqualityLike = true)
              // collect all further formulas with same associative operator
              var fs: Seq[THF.Formula] = Vector(f1,f2)
              while (tokens.hasNext && peek()._1 == opTok._1) {
                consume()
                val f = thfUnitFormula(acceptEqualityLike = true)
                fs = fs :+ f
              }
              if (op == THF.App) fs.reduceLeft((x,y) => THF.BinaryFormula(op, x, y))
              else fs.reduceRight((x,y) => THF.BinaryFormula(op, x, y))
            } else if (isBinaryTHFTypeConstructor(c)) {
              val opTok = consume()
              val op = tokenToTHFBinaryTypeConstructor(opTok)
              if (isUnitaryFormula) {
                if (isUnaryTHFConnective(peek()._1)) {
                  error2("Unexpected binary type constructor before <thf_unary_formula>.", peek())
                } else {
                  val f2 = thfUnitFormula(acceptEqualityLike = false)
                  // collect all further formulas with same associative operator
                  var fs: Seq[THF.Formula] = Vector(f1,f2)
                  while (peek()._1 == opTok._1) {
                    consume()
                    if (!isUnaryTHFConnective(peek()._1)) {
                      val f = thfUnitFormula(acceptEqualityLike = false)
                      fs = fs :+ f
                    } else {
                      error2("Unexpected binary type constructor before <thf_unary_formula>.", peek())
                    }
                  }
                  if (op == THF.FunTyConstructor) fs.reduceRight((x,y) => THF.BinaryFormula(op, x, y))
                  else fs.reduceLeft((x,y) => THF.BinaryFormula(op, x, y))
                }
              } else {
                error2("Unexpected binary type constructor after <thf_unary_formula>.", opTok)
              }
            } else {
              // non-assoc; just parse one more unit and then done.
              val op = tokenToTHFBinaryConnective(consume())
              val f2 = thfUnitFormula(acceptEqualityLike = true)
              THF.BinaryFormula(op, f1, f2)
            }
          case _ => f1
        }
      } else f1
    }

    // Can use this as thfUnitaryFormula with false for call in pre_unit and also for thfUnitaryTerm if is made sure before
    // that there is no quantifier or unary connective in peek().
    // Also as thfUnitaryFormula in general with argument false, if we make sure there is no unary connective in front.
    private[this] def thfUnitFormula(acceptEqualityLike: Boolean): THF.Formula = {
      val tok = peek()
      var feasibleForEq = false
      val f1 = tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARDOLLARWORD => // counts as ATOM, hence + expect equality
          feasibleForEq = true
          val fn = consume()._2
          var args: Seq[THF.Formula] = Vector.empty
          val lp = o(LPAREN, null)
          if (lp != null) {
            args = args :+ thfLogicFormula()
            while (o(COMMA, null) != null) {
              args = args :+ thfLogicFormula()
            }
            a(RPAREN)
          }
          THF.FunctionTerm(fn, args)

        case UPPERWORD => // + expect equality
          feasibleForEq = true
          val variable = consume()
          THF.Variable(variable._2)

        case q if isTHFQuantifier(q) =>
          val quantifier = tokenToTHFQuantifier(consume())
          a(LBRACKET)
          var variables: Seq[THF.TypedVariable] = Vector(typedVariable())
          while(o(COMMA, null) != null) {
            variables = variables :+ typedVariable()
          }
          a(RBRACKET)
          a(COLON)
          val body = thfUnitFormula(acceptEqualityLike = true)
          THF.QuantifiedFormula(quantifier, variables, body)


        case q if isUnaryTHFConnective(q) =>
          val op = tokenToTHFUnaryConnective(consume())
          var listOfUnaries: Seq[THF.UnaryConnective] = Vector(op)
          while (isUnaryTHFConnective(peek()._1)) {
            listOfUnaries = listOfUnaries :+ tokenToTHFUnaryConnective(consume())
          }
          val body = thfUnitFormula(acceptEqualityLike = false)
          listOfUnaries.foldRight(body)((op, acc) => THF.UnaryFormula(op, acc))

        case LPAREN if isTHFConnective(peek(1)._1) && peek(2)._1 == RPAREN => // counts as ATOM, hence + expect equality
          feasibleForEq = true
          consume()
          val op = consume()
          val connective: THF.Connective = if (isUnaryTHFConnective(op._1)) tokenToTHFUnaryConnective(op)
          else if (isBinaryTHFConnective(op._1)) tokenToTHFBinaryConnective(op)
          else {
            assert(isEqualityLikeConnective(op._1))
            tokenToTHFEqConnective(op)
          }
          a(RPAREN)
          THF.ConnectiveTerm(connective)

        case LPAREN => // + expect equality
          feasibleForEq = true
          consume()
          val res = thfLogicFormula()
          a(RPAREN)
          res
        case DOLLARWORD => // counts as ATOM, hence + expect equality
          feasibleForEq = true
          val fn = consume()._2
          fn match {
            case "$let" =>
              consume()
              a(LPAREN)
              // types
              val tyMap: Map[String, THF.Type] = if (o(LBRACKET, null) == null) {
                val typing = thfAtomTyping()
                Map(typing.atom -> typing.typ)
              } else {
                var result: Map[String, THF.Type] = Map.empty
                val typing1 = thfAtomTyping()
                result = result + (typing1.atom -> typing1.typ)
                while (o(COMMA, null) != null) {
                  val typingN = thfAtomTyping()
                  result = result + (typingN.atom -> typingN.typ)
                }
                a(RBRACKET)
                result
              }
              a(COMMA)
              // bindings
              var definitions: Seq[(THF.Formula, THF.Formula)] = Seq.empty
              if (o(LBRACKET, null) == null) {
                val leftSide = thfLogicFormula()
                a(ASSIGNMENT)
                val rightSide = thfLogicFormula()
                definitions = definitions :+ (leftSide, rightSide)
              } else {
                val leftSide = thfLogicFormula()
                a(ASSIGNMENT)
                val rightSide = thfLogicFormula()
                definitions = definitions :+ (leftSide, rightSide)
                while (o(COMMA, null) != null) {
                  val leftSideN = thfLogicFormula()
                  a(ASSIGNMENT)
                  val rightSideN = thfLogicFormula()
                  definitions = definitions :+ (leftSideN, rightSideN)
                }
                a(RBRACKET)
              }
              a(COMMA)
              val body = thfLogicFormula()
              a(RPAREN)
              THF.LetTerm(tyMap, definitions, body)
            case "$ite" =>
              consume()
              a(LPAREN)
              val cond = thfLogicFormula()
              a(COMMA)
              val thn = thfLogicFormula()
              a(COMMA)
              val els = thfLogicFormula()
              a(RPAREN)
              THF.ConditionalTerm(cond, thn, els)
            case _ => // general fof-like function
              var args: Seq[THF.Formula] = Vector.empty
              val lp = o(LPAREN, null)
              if (lp != null) {
                args = args :+ thfLogicFormula()
                while (o(COMMA, null) != null) {
                  args = args :+ thfLogicFormula()
                }
                a(RPAREN)
              }
              THF.FunctionTerm(fn, args)
          }

        case DOUBLEQUOTED => // counts as ATOM, hence + expect equality
          feasibleForEq = true
          val distinctobject = consume()._2
          THF.DistinctObject(distinctobject)

        case INT | RATIONAL | REAL => // counts as ATOM, hence + expect equality
          feasibleForEq = true
          val n = number()
          THF.NumberTerm(n)

        case LBRACKET =>
          consume()
          val rb = o(RBRACKET, null)
          if (rb != null) THF.Tuple(Seq.empty)
          else {
            val f = thfLogicFormula()
            var fs: Seq[THF.Formula] = Vector(f)
            while (o(COMMA, null) != null) {
              fs = fs :+ thfLogicFormula()
            }
            a(RBRACKET)
            THF.Tuple(fs)
          }

        case _ => error2(s"Unrecognized thf formula input '${tok._1}'", tok)
      }
      // if expect equality: do double time.
      if (acceptEqualityLike && feasibleForEq && tokens.hasNext) {
        val tok2 = peek()
        if (isEqualityLikeConnective(tok2._1)) {
          val op = tokenToTHFEqConnective(consume())
          val tok3 = peek()
          if (isTHFQuantifier(tok3._1) || isUnaryTHFConnective(tok3._1)) {
            // not allowed, since we are in <thf_unitary_term> (simulated) here.
            error2(s"Expected <thf_unitary_term>, but found ${tok3._1} first. Maybe parentheses are missing around the argument of ${tok3._1}?", tok3)
          } else {
            val f2 = thfUnitFormula(acceptEqualityLike = false)
            THF.BinaryFormula(op, f1, f2)
          }
        } else f1
      } else f1
    }

    private[this] def typedVariable(): THF.TypedVariable = {
      val variableName = variable()
      a(COLON)
      val typ = thfTopLevelType()
      (variableName, typ)
    }

    ////////////////////////////////////////////////////////////////////////
    // Type level
    ////////////////////////////////////////////////////////////////////////
    private[this] def thfTopLevelType(): THF.Type = {
      val tok = peek()
      val f1 = tok._1 match {
        case c if isUnaryTHFConnective(c) => error2("Read unexpected unary connective when reading <thf_top_level_type>", tok)
        case _ => thfUnitFormula(acceptEqualityLike = false)
      }
      val next = peek()
      next._1 match {
        case RANGLE | APP =>
          val opTok = consume()
          val op = tokenToTHFBinaryTypeConstructor(opTok)
          if (isUnaryTHFConnective(peek()._1)) {
            error2("Unexpected binary type constructor before <thf_unary_formula>.", peek())
          } else {
            val f2 = thfUnitFormula(acceptEqualityLike = false)
            // collect all further formulas with same associative operator
            var fs: Seq[THF.Formula] = Vector(f1,f2)
            while (peek()._1 == opTok._1) {
              consume()
              if (!isUnaryTHFConnective(peek()._1)) {
                val f = thfUnitFormula(acceptEqualityLike = false)
                fs = fs :+ f
              } else {
                error2("Unexpected binary type constructor before <thf_unary_formula>.", peek())
              }
            }
            if (op == THF.FunTyConstructor) fs.reduceRight((x,y) => THF.BinaryFormula(op, x, y))
            else fs.reduceLeft((x,y) => THF.BinaryFormula(op, x, y))
          }
        case _ => f1
      }
    }

    ////////////////////////////////////////////////////////////////////////
    // Other THF stuff
    ////////////////////////////////////////////////////////////////////////

    // Only real connective: So no @ (application), as opposed to isBinaryTHFConnective
    @inline private[this] def isTHFConnective(tokenType: TokenType): Boolean =
      isUnaryTHFConnective(tokenType) || isBinaryConnective(tokenType) || isEqualityLikeConnective(tokenType)

    @inline private[this] def isUnaryTHFConnective(tokenType: TokenType): Boolean = isUnaryConnective(tokenType) || (tokenType match {
      case FORALLCOMB | EXISTSCOMB | DESCRIPTIONCOMB | CHOICECOMB | EQCOMB => true
      case _ => false
    })
    @inline private[this] def isBinaryTHFConnective(tokenType: TokenType): Boolean = isBinaryConnective(tokenType) || tokenType == APP
    @inline private[this] def isBinaryTHFTypeConstructor(tokenType: TokenType): Boolean = tokenType == STAR || tokenType == RANGLE || tokenType == PLUS
    @inline private[this] def isTHFQuantifier(tokenType: TokenType): Boolean = isQuantifier(tokenType) || (tokenType match {
      case LAMBDA | DESCRIPTION | CHOICE | TYFORALL | TYEXISTS => true
      case _ => false
    })
    @inline private[this] def isBinaryAssocTHFConnective(tokenType: TokenType): Boolean = isBinaryAssocConnective(tokenType) || tokenType == APP


    private[this] def tokenToTHFEqConnective(token: Token): THF.BinaryConnective = token._1 match {
      case EQUALS => THF.Eq
      case NOTEQUALS => THF.Neq
      case _ => error(Seq(EQUALS, NOTEQUALS), token)
    }
    private[this] def tokenToTHFBinaryConnective(token: Token): THF.BinaryConnective = token._1 match {
      case APP => THF.App
      case OR => THF.|
      case AND => THF.&
      case IFF => THF.<=>
      case IMPL => THF.Impl
      case IF => THF.<=
      case NOR => THF.~|
      case NAND => THF.~&
      case NIFF => THF.<~>
      case _ => error(Seq(APP, OR, AND, IFF, IMPL, IF, NOR, NAND, NIFF), token)
    }
    private[this] def tokenToTHFBinaryTypeConstructor(token: Token): THF.BinaryConnective = token._1 match {
      case PLUS => THF.SumTyConstructor
      case STAR => THF.ProductTyConstructor
      case RANGLE => THF.FunTyConstructor
      case APP => THF.App
      case _ => error(Seq(PLUS, STAR, RANGLE, APP), token)
    }
    private[this] def tokenToTHFUnaryConnective(token: Token): THF.UnaryConnective = token._1 match {
      case NOT => THF.~
      case FORALLCOMB => THF.!!
      case EXISTSCOMB => THF.??
      case CHOICECOMB => THF.@@+
      case DESCRIPTIONCOMB => THF.@@-
      case EQCOMB => THF.@=
      case _ => error(Seq(NOT, FORALLCOMB, EXISTSCOMB, CHOICECOMB, DESCRIPTIONCOMB, EQCOMB), token)
    }
    private[this] def tokenToTHFQuantifier(token: Token): THF.Quantifier = token._1 match {
      case FORALL => THF.!
      case EXISTS => THF.?
      case LAMBDA => THF.^
      case CHOICE => THF.@+
      case DESCRIPTION => THF.@-
      case TYFORALL => THF.!>
      case TYEXISTS => THF.?*
      case _ => error(Seq(FORALL, EXISTS, LAMBDA, CHOICE, DESCRIPTION, TYFORALL, TYEXISTS), token)
    }


    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // TFF formula stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////
    // Formula level
    ////////////////////////////////////////////////////////////////////////
    def annotatedTFF(): TFFAnnotated = {
      m(a(LOWERWORD), "tff")
      a(LPAREN)
      val n = name()
      a(COMMA)
      val r = a(LOWERWORD)._2
      a(COMMA)
      val f = tffFormula()
      var source: GeneralTerm = null
      var info: Seq[GeneralTerm] = null
      val an0 = o(COMMA, null)
      if (an0 != null) {
        source = generalTerm()
        val an1 = o(COMMA, null)
        if (an1 != null) {
          info = generalList()
        }
      }
      a(RPAREN)
      a(DOT)
      if (source == null) TFFAnnotated(n, r, f, None)
      else TFFAnnotated(n, r, f, Some((source, Option(info))))
    }

    def tffFormula(): TFF.Statement = {
      val idx = peekUnder(LPAREN)
      val tok = peek(idx)
      tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARDOLLARWORD if peek(idx+1)._1 == COLON => // Typing
          tffAtomTyping()
        case _ =>
          TFF.Logical(tffLogicFormula())
      }
    }

    private[this] def tffAtomTyping(): TFF.Typing = {
      val lp = o(LPAREN, null)
      if (lp != null) {
        val res = tffAtomTyping()
        a(RPAREN)
        res
      } else {
        val constant = untypedAtom()
        a(COLON)
        val typ = tffTopLevelType()
        TFF.Typing(constant, typ)
      }
    }


    def tffLogicFormula(): TFF.Formula = {
      val f1 = tffUnitFormula()
      val next = peek()
      next._1 match {
        case c if isBinaryConnective(c)  =>
          if (isBinaryAssocConnective(c)) {
            val opTok = consume()
            val op = tokenToTFFBinaryConnective(opTok)
            val f2 = tffUnitFormula()
            // collect all further formulas with same associative operator
            var fs: Seq[TFF.Formula] = Vector(f1,f2)
            while (peek()._1 == opTok._1) {
              consume()
              val f = tffUnitFormula()
              fs = fs :+ f
            }
            fs.reduceRight((x,y) => TFF.BinaryFormula(op, x, y))
          } else {
            // non-assoc; just parse one more unit and then done.
            val op = tokenToTFFBinaryConnective(consume())
            val f2 = tffUnitFormula()
            TFF.BinaryFormula(op, f1, f2)
          }
        case _ => f1
      }
    }

    private[this] def tffUnitFormula(): TFF.Formula = {
      val tok = peek()

      tok._1 match {
        case LPAREN =>
          consume()
          val f = tffLogicFormula()
          a(RPAREN)
          f
        case c if isUnaryConnective(c) =>
          val connective = tokenToTFFUnaryConnective(consume())
          val body = tffUnitFormula()
          TFF.UnaryFormula(connective, body)
        case q if isQuantifier(q) =>
          val quantifier = tokenToTFFQuantifier(consume())
          a(LBRACKET)
          val name = typedTFFVariable()
          var names: Seq[TFF.TypedVariable] = Vector(name)
          while (o(COMMA, null) != null) {
            names = names :+ typedTFFVariable()
          }
          a(RBRACKET)
          a(COLON)
          val body = tffUnitFormula()
          TFF.QuantifiedFormula(quantifier, names, body)
        case LOWERWORD | UPPERWORD | DOLLARWORD | DOLLARDOLLARWORD | SINGLEQUOTED | DOUBLEQUOTED | INT | RATIONAL | REAL =>
          val term1 = tffTerm()
          // expect = and != still
          val nextTok = peek()
          nextTok._1 match {
            case EQUALS =>
              consume()
              val right = tffTerm()
              TFF.Equality(term1,right)
            case NOTEQUALS =>
              consume()
              val right = tffTerm()
              TFF.Inequality(term1,right)
            case _ =>
              term1 match {
                case TFF.AtomicTerm(f, args) => TFF.AtomicFormula(f, args)
                case _ => error2("Parse error: Unexpected term at formula level", nextTok)
              }
          }
        case _ => error2(s"Unrecognized tff formula input '${tok._1}'", tok)
      }
    }

    private[this] def typedTFFVariable(): TFF.TypedVariable = {
      val variableName = variable()
      a(COLON)
      val typ = tffAtomicType()
      (variableName, typ)
    }

    def tffTerm(): TFF.Term = {
      val tok = peek()
      tok._1 match {
        case INT | RATIONAL | REAL =>
          TFF.NumberTerm(number())
        case DOUBLEQUOTED =>
          TFF.DistinctObject(consume()._2)
        case UPPERWORD =>
          TFF.Variable(consume()._2)
        case LOWERWORD | SINGLEQUOTED | DOLLARWORD | DOLLARDOLLARWORD =>
          val fn = consume()._2
          var args: Seq[TFF.Term] = Vector.empty
          if (o(LPAREN, null) != null) {
            args = args :+ tffTerm()
            while (o(COMMA, null) != null) {
              args = args :+ tffTerm()
            }
            a(RPAREN)
          }
          TFF.AtomicTerm(fn, args)
        case LBRACKET => // Tuple
          consume()
          if (o(RBRACKET, null) == null) {
            var entries: Seq[TFF.Term] = Vector(tffTerm())
            while (o(COMMA, null) != null) {
              entries = entries :+ tffTerm()
            }
            a(RBRACKET)
            TFF.Tuple(entries)
          } else {
            // empty tuple
            TFF.Tuple(Seq.empty)
          }
        case _ => error(Seq(INT, RATIONAL, REAL, DOUBLEQUOTED, UPPERWORD, LOWERWORD, SINGLEQUOTED, DOLLARWORD, DOLLARDOLLARWORD, LBRACKET), tok)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    // Type level
    ////////////////////////////////////////////////////////////////////////
    private[this] def tffTopLevelType(): TFF.Type = {
      val idx = peekUnder(LPAREN)
      val tok = peek(idx)
      tok._1 match {
        case TYFORALL => tffQuantifiedType()
        case _ => tffUnitaryType()
      }
    }

    private[this] def tffQuantifiedType(): TFF.Type = {
      val tok = peek()
      tok._1 match {
        case LPAREN =>
          consume()
          val result = tffQuantifiedType()
          a(RPAREN)
          result
        case TYFORALL =>
          consume()
          a(LBRACKET)
          var variables: Seq[TFF.TypedVariable] = Vector(typedTFFVariable())
          while (o(COMMA, null) != null) {
            variables = variables :+ typedTFFVariable()
          }
          a(RBRACKET)
          a(COLON)
          val next = peek()
          val body = next._1 match {
            case LPAREN => tffUnitaryType() // mapping type
            case TYFORALL => tffQuantifiedType()
            case _ => tffAtomicType()
          }
          TFF.QuantifiedType(variables, body)
        case _ => error(Seq(LPAREN, TYFORALL), tok)
      }
    }

    private[this] def tffUnitaryType(): TFF.Type = {
      var leftParenCount = 0
      var productTypeEntries: Seq[TFF.Type] = Seq.empty
      while (o(LPAREN, null) != null) {
        leftParenCount += 1
      }
      var doneWithLeftSideOrMapping = false
      while (!doneWithLeftSideOrMapping) {
        productTypeEntries = productTypeEntries :+ tffAtomicType()
        val tok = peek()
        tok._1 match {
          case RPAREN if leftParenCount > 0 =>
            consume()
            leftParenCount = leftParenCount - 1
            if (leftParenCount == 0 || peek()._1 == RANGLE) doneWithLeftSideOrMapping = true
          case STAR if leftParenCount > 0 => consume()
          case _ =>
            doneWithLeftSideOrMapping = true
        }
      }
      val next = peek()
      next._1 match {
        case RANGLE =>
          consume()
          val rightType = tffAtomicType()
          while (leftParenCount > 0) {
            a(RPAREN)
            leftParenCount = leftParenCount - 1
          }
          TFF.MappingType(productTypeEntries, rightType)
        case _ if productTypeEntries.size == 1 => productTypeEntries.head
        case _ => error2(s"Parse error: Naked product type on top-level; expected mapping type constructor '>' but found '${next._1}'", next)
      }
    }

    private[this] def tffAtomicType(): TFF.Type = {
      val tok = peek()
      tok._1 match {
        case LPAREN =>
          consume()
          val result = tffAtomicType()
          a(RPAREN)
          result
        case UPPERWORD => TFF.TypeVariable(consume()._2)
        case DOLLARWORD | LOWERWORD | SINGLEQUOTED =>
          val fn = consume()._2
          if (o(LPAREN, null) != null) {
            var arguments: Seq[TFF.Type] = Vector(tffAtomicType())
            while (o(COMMA, null) != null) {
              arguments = arguments :+ tffAtomicType()
            }
            a(RPAREN)
            TFF.AtomicType(fn, arguments)
          } else TFF.AtomicType(fn, Seq.empty)
        case LBRACKET =>
          consume()
          var entries: Seq[TFF.Type] = Vector(tffTopLevelType())
          while (o(COMMA, null) != null) {
            entries = entries :+ tffTopLevelType()
          }
          a(RBRACKET)
          TFF.TupleType(entries)
        case _ => error(Seq(LPAREN, UPPERWORD, DOLLARWORD, LOWERWORD, SINGLEQUOTED, LBRACKET), tok)
      }
    }

    // Utility
    private[this] def tokenToTFFBinaryConnective(token: Token): TFF.BinaryConnective = token._1 match {
      case OR => TFF.|
      case AND => TFF.&
      case IFF => TFF.<=>
      case IMPL => TFF.Impl
      case IF => TFF.<=
      case NOR => TFF.~|
      case NAND => TFF.~&
      case NIFF => TFF.<~>
      case _ => error(Seq(OR, AND, IFF, IMPL, IF, NOR, NAND, NIFF), token)
    }
    private[this] def tokenToTFFUnaryConnective(token: Token): TFF.UnaryConnective = token._1 match {
      case NOT => TFF.~
      case _ => error(Seq(NOT), token)
    }
    private[this] def tokenToTFFQuantifier(token: Token): TFF.Quantifier = token._1 match {
      case FORALL => TFF.!
      case EXISTS => TFF.?
      case _ => error(Seq(FORALL, EXISTS), token)
    }


    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // FOF formula stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    def annotatedFOF(): FOFAnnotated = {
      m(a(LOWERWORD), "fof")
      a(LPAREN)
      val n = name()
      a(COMMA)
      val r = a(LOWERWORD)._2
      a(COMMA)
      val f = fofFormula()
      var source: GeneralTerm = null
      var info: Seq[GeneralTerm] = null
      val an0 = o(COMMA, null)
      if (an0 != null) {
        source = generalTerm()
        val an1 = o(COMMA, null)
        if (an1 != null) {
          info = generalList()
        }
      }
      a(RPAREN)
      a(DOT)
      if (source == null) FOFAnnotated(n, r, f, None)
      else FOFAnnotated(n, r, f, Some((source, Option(info))))
    }

    // Currently, no other kind of statement supported
    @inline private[this] def fofFormula(): FOF.Statement = FOF.Logical(fofLogicFormula())

    def fofLogicFormula(): FOF.Formula = {
      val f1 = fofUnitFormula()
      val next = peek()
      next._1 match {
        case c if isBinaryConnective(c)  =>
          if (isBinaryAssocConnective(c)) {
            val opTok = consume()
            val op = tokenToFOFBinaryConnective(opTok)
            val f2 = fofUnitFormula()
            // collect all further formulas with same associative operator
            var fs: Seq[FOF.Formula] = Vector(f1,f2)
            while (peek()._1 == opTok._1) {
              consume()
              val f = fofUnitFormula()
              fs = fs :+ f
            }
            fs.reduceRight((x,y) => FOF.BinaryFormula(op, x, y))
          } else {
            // non-assoc; just parse one more unit and then done.
            val op = tokenToFOFBinaryConnective(consume())
            val f2 = fofUnitFormula()
            FOF.BinaryFormula(op, f1, f2)
          }
        case _ => f1
      }
    }

    def fofUnitFormula(): FOF.Formula = {
      val tok = peek()

      tok._1 match {
        case LPAREN =>
          consume()
          val f = fofLogicFormula()
          a(RPAREN)
          f
        case c if isUnaryConnective(c) =>
          val connective = tokenToFOFUnaryConnective(consume())
          val body = fofUnitFormula()
          FOF.UnaryFormula(connective, body)
        case q if isQuantifier(q) =>
          val quantifier = tokenToFOFQuantifier(consume())
          a(LBRACKET)
          val name = variable()
          var names: Seq[String] = Vector(name)
          while (o(COMMA, null) != null) {
            names = names :+ variable()
          }
          a(RBRACKET)
          a(COLON)
          val body = fofUnitFormula()
          FOF.QuantifiedFormula(quantifier, names, body)
        case LOWERWORD | UPPERWORD | DOLLARWORD | DOLLARDOLLARWORD | SINGLEQUOTED | DOUBLEQUOTED | INT | RATIONAL | REAL =>
          val term1 = fofTerm()
          // expect = and != still
          val nextTok = peek()
          nextTok._1 match {
            case EQUALS =>
              consume()
              val right = fofTerm()
              FOF.Equality(term1,right)
            case NOTEQUALS =>
              consume()
              val right = fofTerm()
              FOF.Inequality(term1,right)
            case _ =>
              term1 match {
                case FOF.AtomicTerm(f, args) => FOF.AtomicFormula(f, args)
                case _ => error2("Parse error: Unexpected term at formula level", nextTok)
              }
          }
        case _ => error2(s"Unrecognized fof formula input '${tok._1}'", tok)
      }
    }

    def fofTerm(): FOF.Term = {
      val tok = peek()
      tok._1 match {
        case INT | RATIONAL | REAL =>
          FOF.NumberTerm(number())
        case DOUBLEQUOTED =>
          FOF.DistinctObject(consume()._2)
        case UPPERWORD =>
          FOF.Variable(consume()._2)
        case LOWERWORD | SINGLEQUOTED | DOLLARWORD | DOLLARDOLLARWORD =>
          val fn = consume()._2
          var args: Seq[FOF.Term] = Vector()
          if (o(LPAREN, null) != null) {
            args = args :+ fofTerm()
            while (o(COMMA, null) != null) {
              args = args :+ fofTerm()
            }
            a(RPAREN)
          }
          FOF.AtomicTerm(fn, args)
        case _ => error(Seq(INT, RATIONAL, REAL, DOUBLEQUOTED, UPPERWORD, LOWERWORD, SINGLEQUOTED, DOLLARWORD, DOLLARDOLLARWORD), tok)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // CNF formula stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    def annotatedCNF(): CNFAnnotated = {
      m(a(LOWERWORD), "cnf")
      a(LPAREN)
      val n = name()
      a(COMMA)
      val r = a(LOWERWORD)._2
      a(COMMA)
      val f = cnfFormula()
      var source: GeneralTerm = null
      var info: Seq[GeneralTerm] = null
      val an0 = o(COMMA, null)
      if (an0 != null) {
        source = generalTerm()
        val an1 = o(COMMA, null)
        if (an1 != null) {
          info = generalList()
        }
      }
      a(RPAREN)
      a(DOT)
      if (source == null) CNFAnnotated(n, r, f, None)
      else CNFAnnotated(n, r, f, Some((source, Option(info))))
    }

    // Currently, no other kind of statement supported
    @inline private[this] def cnfFormula(): CNF.Statement = CNF.Logical(cnfLogicFormula())

    def cnfLogicFormula(): CNF.Formula = {
      val tok = peek()
      tok._1 match {
        case LPAREN =>
          consume()
          val res = cnfLogicFormula()
          a(RPAREN)
          res
        case _ =>
          val lit = cnfLiteral()
          var lits: Seq[CNF.Literal] = Vector(lit)
          while (o(OR, null) != null) {
            lits = lits :+ cnfLiteral()
          }
          lits
      }
    }

    private[this] def cnfLiteral(): CNF.Literal = {
      val tok = peek()

      tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARWORD | DOLLARDOLLARWORD | DOUBLEQUOTED | UPPERWORD =>
          // parse term. if = or != comes, take anotehr. else return (if not variable/double quoted)
          val term1 = cnfTerm()
          val tok2 = peek()
          tok2._1 match {
            case EQUALS =>
              consume()
              val term2 = cnfTerm()
              CNF.Equality(term1, term2)
            case NOTEQUALS =>
              consume()
              val term2 = cnfTerm()
              CNF.Inequality(term1, term2)
            case _ => term1 match {
              case CNF.AtomicTerm(f, args) => CNF.PositiveAtomic(CNF.AtomicFormula(f, args))
              case _ => error2(s"Parse error: Unexpected term (variable or distinct object) when formula was expected", lastTok)
            }
          }
        case NOT =>
          consume()
          val formula = cnfTerm()
          formula match {
            case CNF.AtomicTerm(f, args) => CNF.NegativeAtomic(CNF.AtomicFormula(f, args))
            case _ => error2(s"Parse error: Unexpected term (variable or distinct object) after negation", lastTok)
          }
        case _ => error(Seq(SINGLEQUOTED, LOWERWORD, DOLLARWORD, DOLLARDOLLARWORD, DOUBLEQUOTED, UPPERWORD, NOT), tok)
      }
    }

    private[this] def cnfTerm(): CNF.Term = {
      val tok = peek()
      tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARWORD | DOLLARDOLLARWORD =>
          val fn = consume()._2
          var args: Seq[CNF.Term] = Vector.empty
          val lp = o(LPAREN, null)
          if (lp != null) {
            args = args :+ cnfTerm()
            while (o(COMMA, null) != null) {
              args = args :+ cnfTerm()
            }
            a(RPAREN)
          }
          CNF.AtomicTerm(fn, args)
        case UPPERWORD =>
          CNF.Variable(consume()._2)
        case DOUBLEQUOTED =>
          CNF.DistinctObject(consume()._2)
        case _ => error(Seq(SINGLEQUOTED, LOWERWORD, DOLLARWORD, DOLLARDOLLARWORD, UPPERWORD, DOUBLEQUOTED), tok)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // TPI formula stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    def annotatedTPI(): TPIAnnotated = {
      m(a(LOWERWORD), "tpi")
      a(LPAREN)
      val n = name()
      a(COMMA)
      val r = a(LOWERWORD)._2
      a(COMMA)
      val f = fofFormula()
      var source: GeneralTerm = null
      var info: Seq[GeneralTerm] = null
      val an0 = o(COMMA, null)
      if (an0 != null) {
        source = generalTerm()
        val an1 = o(COMMA, null)
        if (an1 != null) {
          info = generalList()
        }
      }
      a(RPAREN)
      a(DOT)
      if (source == null) TPIAnnotated(n, r, f, None)
      else TPIAnnotated(n, r, f, Some((source, Option(info))))
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////
    // General TPTP language stuff
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    @inline private[this] def isUnaryConnective(tokenType: TokenType): Boolean = tokenType == NOT
    @inline private[this] def isBinaryConnective(tokenType: TokenType): Boolean = isBinaryAssocConnective(tokenType) || (tokenType match {
      case IFF | IMPL | IF | NOR | NAND | NIFF => true
      case _ => false
    })
    @inline private[this] def isBinaryAssocConnective(tokenType: TokenType): Boolean = tokenType == AND || tokenType == OR
    @inline private[this] def isQuantifier(tokenType: TokenType): Boolean = tokenType == FORALL || tokenType == EXISTS
    @inline private[this] def isEqualityLikeConnective(tokenType: TokenType): Boolean = tokenType == EQUALS || tokenType == NOTEQUALS

    private[this] def tokenToFOFBinaryConnective(token: Token): FOF.BinaryConnective = token._1 match {
      case OR => FOF.|
      case AND => FOF.&
      case IFF => FOF.<=>
      case IMPL => FOF.Impl
      case IF => FOF.<=
      case NOR => FOF.~|
      case NAND => FOF.~&
      case NIFF => FOF.<~>
      case _ => error(Seq(OR, AND, IFF, IMPL, IF, NOR, NAND, NIFF), token)
    }
    private[this] def tokenToFOFUnaryConnective(token: Token): FOF.UnaryConnective = token._1 match {
      case NOT => FOF.~
      case _ => error(Seq(NOT), token)
    }
    private[this] def tokenToFOFQuantifier(token: Token): FOF.Quantifier = token._1 match {
      case FORALL => FOF.!
      case EXISTS => FOF.?
      case _ => error(Seq(FORALL, EXISTS), token)
    }

    private[this] def untypedAtom(): String = {
      val tok = peek()
      tok._1 match {
        case SINGLEQUOTED | LOWERWORD | DOLLARDOLLARWORD => consume()._2
        case _ => error(Seq(SINGLEQUOTED, LOWERWORD, DOLLARDOLLARWORD), tok)
      }
    }

    private[this] def generalList(): Seq[GeneralTerm] = {
      var result: Seq[GeneralTerm] = Seq.empty
      a(LBRACKET)
      var endOfList = o(RBRACKET, null)
      while (endOfList == null) {
        val item = generalTerm()
        result = result :+ item
        endOfList = o(RBRACKET, null)
        if (endOfList == null) {
          a(COMMA)
        }
      }
      // right bracket consumed by o(RBRACKET, null)
      result
    }

    private[this] def generalTerm(): GeneralTerm = {
      // if [, then list
      // collect items
      // then ]. DONE
      // if not [, then generaldata(). Not necessarily done.
      val t = peek()
      t._1 match {
        case LBRACKET => // list
          GeneralTerm(Seq.empty, Some(generalList()))
        case _ => // not a list
          var generalDataList: Seq[GeneralData] = Seq.empty
          var generalTermList: Option[Seq[GeneralTerm]] = None
          generalDataList = generalDataList :+ generalData()
          // as long as ':' then repeat all of above
          // if no ':' anymore, DONE. or if list.
          var done = false
          while(!done) {
            if (o(COLON, null) != null) {
              if (peek()._1 == LBRACKET) {
                // end of list, with generalList coming now
                generalTermList = Some(generalList())
                done = true
              } else {
                // maybe further
                generalDataList = generalDataList :+ generalData()
              }
            } else {
              done = true
            }
          }
          GeneralTerm(generalDataList, generalTermList)
      }
    }

    private[this] def generalData(): GeneralData = {
      val t = peek()
      t._1 match {
        case LOWERWORD | SINGLEQUOTED =>
          val function = consume()
          val t1 = o(LPAREN, null)
          if (t1 != null) {
            var args: Seq[GeneralTerm] = Seq.empty
            args = args :+ generalTerm()
            while (o(COMMA, null) != null) {
              args = args :+ generalTerm()
            }
            a(RPAREN)
            MetaFunctionData(function._2, args)
          } else MetaFunctionData(function._2, Seq.empty)
        case UPPERWORD => MetaVariable(consume()._2)
        case DOUBLEQUOTED => DistinctObjectData(consume()._2)
        case INT | RATIONAL | REAL => NumberData(number())
        case DOLLARWORD =>
          t._2 match {
            case "$thf" =>
              consume()
              a(LPAREN)
              val f = thfFormula()
              a(RPAREN)
              GeneralFormulaData(THFData(f))
            case "$tff" =>
              consume()
              a(LPAREN)
              val f = tffFormula()
              a(RPAREN)
              GeneralFormulaData(TFFData(f))
            case "$fof" =>
              consume()
              a(LPAREN)
              val f = fofFormula()
              a(RPAREN)
              GeneralFormulaData(FOFData(f))
            case "$fot" =>
              consume()
              a(LPAREN)
              val f = fofTerm()
              a(RPAREN)
              GeneralFormulaData(FOTData(f))
            case "$cnf" => consume()
              a(LPAREN)
              val f = cnfFormula()
              a(RPAREN)
              GeneralFormulaData(CNFData(f))
            case _ => error1(Seq("$thf", "$tff", "$fof", "$fot", "$cnf"), t)
          }
        case _ => error(Seq(INT, RATIONAL, REAL, UPPERWORD, LOWERWORD, SINGLEQUOTED, DOLLARWORD, DOUBLEQUOTED), t)
      }
    }

    private[this] def number(): Number = {
      val t = peek()
      t._1 match {
        case INT => Integer(consume()._2.toInt)
        case RATIONAL =>
          val numberTok = consume()
          val split = numberTok._2.split('/')
          val numerator = split(0).toInt
          val denominator = split(1).toInt
          if (denominator <= 0) throw new TPTPParseException("Denominator in rational number expression zero or negative", numberTok._3, numberTok._4)
          else Rational(numerator, denominator)
        case REAL =>
          val number = consume()._2
          val split = number.split('.')
          val wholePart = split(0).toInt
          val anothersplit = split(1).split(Array('E', 'e'))
          val decimalPart = anothersplit(0).toInt
          val exponent = if (anothersplit.length > 1) anothersplit(1).toInt else 1
          Real(wholePart, decimalPart, exponent)
        case _ => error(Seq(INT, RATIONAL, REAL), t)
      }
    }

    private[this] def name(): String = {
      val t = peek()
      t._1 match {
        case INT | LOWERWORD | SINGLEQUOTED => consume()._2
        case _ => error(Seq(INT, LOWERWORD, SINGLEQUOTED), t)
      }
    }

    private[this] def variable(): String = {
      val t = peek()
      t._1 match {
        case UPPERWORD => consume()._2
        case _ => error(Seq(UPPERWORD), t)
      }
    }

    ////////////////////////////////////////////////////////////////////////
    /////////////////// /////////////////////////////////////////////////////
    // General purpose functions
    ////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////

    private[this] def peek(): Token = {
      if (tokens.hasNext) {
        tokens.peek()
      } else {
        if (lastTok == null) throw new TPTPParseException(s"Parse error: Empty input when some token was expected", -1, -1)
        else throw new TPTPParseException(s"Parse error: Unexpected end of input when some token was expected", lastTok._3, lastTok._4)
      }
    }
    private[this] def peek(i: Int): Token = {
      try {
        tokens.peek(i)
      } catch {
        case _: NoSuchElementException => if (lastTok == null) throw new TPTPParseException(s"Parse error: Empty peek(.) when some token was expected", -1, -1)
        else throw new TPTPParseException(s"Parse error: Unexpected end of input when some token was expected in peek(.)", lastTok._3, lastTok._4)
      }
    }
    @inline private[this] def consume(): Token = {
      val t = tokens.next()
      lastTok = t
      t
    }

    private[this] def peekUnder(tokenType: TokenType): Int = {
      var i: Int = 0
      while (peek(i)._1 == tokenType) { i += 1  }
      i
    }

    @inline private[this] def error[A](acceptedTokens: Seq[TokenType], actual: Token): A = {
      assert(acceptedTokens.nonEmpty)
      if (acceptedTokens.size == 1)  throw new TPTPParseException(s"Expected ${acceptedTokens.head} but read ${actual._1}", actual._3, actual._4)
      else throw new TPTPParseException(s"Expected one of ${acceptedTokens.mkString(",")} but read ${actual._1}", actual._3, actual._4)
    }

    @inline private[this] def error1[A](acceptedPayload: Seq[String], actual: Token): A = {
      assert(acceptedPayload.nonEmpty)
      if (acceptedPayload.size == 1) {
        if (actual._2 == null) throw new TPTPParseException(s"Expected '${acceptedPayload.head}' but read ${actual._1}", actual._3, actual._4)
        else throw new TPTPParseException(s"Expected '${acceptedPayload.head}' but read ${actual._1} '${actual._2}'", actual._3, actual._4)
      }
      else {
        if (actual._2 == null) throw new TPTPParseException(s"Expected one of ${acceptedPayload.map(s => s"'$s'").mkString(",")} but read ${actual._1}", actual._3, actual._4)
        else throw new TPTPParseException(s"Expected one of ${acceptedPayload.map(s => s"'$s'").mkString(",")} but read ${actual._1} '${actual._2}'", actual._3, actual._4)
      }
    }

    @inline private[this] def error2[A](message: String, tokenReference: Token): A = {
      throw new TPTPParseException(message, tokenReference._3, tokenReference._4)
    }

    private[this] def a(tokType: TokenType): Token = {
      if (tokens.hasNext) {
        val t = peek()
        if (t._1 == tokType) {
          consume()
        } else {
          if (t._2 == null) throw new TPTPParseException(s"Expected $tokType but read ${t._1}", t._3, t._4)
          else throw new TPTPParseException(s"Expected $tokType but read ${t._1} '${t._2}'", t._3, t._4)
        }
      } else {
        if (lastTok == null) throw new TPTPParseException(s"Parse error: Empty input when $tokType was expected", -1, -1)
        else throw new TPTPParseException(s"Parse error: Unexpected end of input when $tokType was expected", lastTok._3, lastTok._4)
      }
    }

    private[this] def o(tokType: TokenType, payload: String): Token = {
      if (tokens.hasNext) {
        val t = peek()
        if (t._1 == tokType && (payload == null || t._2 == payload)) consume() else null
      } else null
    }

    private[this] def m(tok: Token, payload: String): Token = {
      if (tok._2 == payload) tok
      else throw new TPTPParseException(s"Expected '$payload' but read ${tok._1} with value '${tok._2}'", tok._3, tok._4)
    }

  }
  object TPTPParser {

  }
}
