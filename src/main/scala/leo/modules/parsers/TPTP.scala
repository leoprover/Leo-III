package leo.modules.parsers

import java.io.{BufferedReader, StringReader}

import org.antlr.v4.runtime._
import org.antlr.v4.runtime.atn.PredictionMode
import org.antlr.v4.runtime.misc.ParseCancellationException
import leo.datastructures.tptp.Commons._
import leo.datastructures.tptp.thf.{LogicFormula => THFFormula}
import leo.modules.SZSException
import leo.modules.output.SZS_SyntaxError
import leo.modules.parsers.antlr._

/**
  * Parsing interface for TPTP files and single (annotated) TPTP formulae.
  * The parser obeys the rules of the TPTP Syntax BNF found at
  * [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]] and
  * returns objects from [[leo.datastructures.tptp]] as internal representation
  * for the respective inputs. Depending on the method, this can be a
  * representation of a whole input file (see  [[leo.datastructures.tptp.Commons.TPTPInput]])
  * containing includes as well as formulae, annotated formulae including the
  * formula language specification, a name and role
  * (see [[leo.datastructures.tptp.Commons.AnnotatedFormula]]), or
  * simply a single formula (e.g. [[leo.datastructures.tptp.thf.LogicFormula]]
  * when parsing THF).
  *
  * @author Alexander Steen
  * @since 23.03.2014
  * @note Updated last on 2017/07: Adapted to new ANTLR4 version
  *       (replaced deprecated methods)
  * @see Definitions on internal TPTP representation at [[leo.datastructures.tptp]].
  */
object TPTP {
  /**
    * Parses a complete TPTP file yielding a [[leo.datastructures.tptp.Commons.TPTPInput]] value,
    * if succeeded.
    *
    * @param input A [[java.io.BufferedReader]] wrapping the TPTP input
    * @return A representation of the in file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
   */
  protected[parsers] final def parseFile(input: java.io.BufferedReader): TPTPInput = {
    val inputStream = CharStreams.fromReader(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.getInterpreter.setPredictionMode(PredictionMode.SLL)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = try {
        parser.tptp_file()
      } catch {
        case _:ParseCancellationException =>
          tokenStream.seek(0)
          parser.reset()
          parser.getInterpreter.setPredictionMode(PredictionMode.LL)
          parser.tptp_file()
      }
      TPTPASTConstructor.tptpFile(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Convenience method for parsing. Same as `parseFile(input: Reader[Char])`, just that
    * it takes a string instead of a [[java.io.BufferedReader]].
    *
    * @param input The String that is to be parsed
    * @return A representation of the input file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def parseFile(input: String): TPTPInput =
    parseFile(new BufferedReader(new StringReader(input)))

  /**
    * Parses a THF formula (without annotations etc).
    *
    * @param input A <thf_logic_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.thf.LogicFormula]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def apply(input: String): THFFormula = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.thf_logic_formula()
      TPTPASTConstructor.thfLogicFormula(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Parses any annotated TPTP formula (including language specification, name, etc).
    *
    * @param input A <annotated_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.Commons.AnnotatedFormula]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def annotatedFormula(input: String): AnnotatedFormula = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.annotated_formula()
      TPTPASTConstructor.annotatedFormula(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Parses a annotated FOF formula (including language specification, name, etc).
    *
    * @note If you do not know which kind of formula (fof, thf, ...) you want to parse,
    *       use [[leo.modules.parsers.TPTP#annotatedFormula]] instead.
    * @param input A <annotated_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.Commons.FOFAnnotated]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def fof(input: String): FOFAnnotated = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.fof_annotated()
      TPTPASTConstructor.fofAnnotated(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Parses a annotated THF formula (including language specification, name, etc).
    *
    * @note If you do not know which kind of formula (fof, thf, ...) you want to parse,
    *       use [[leo.modules.parsers.TPTP#annotatedFormula]] instead.
    * @param input A <annotated_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.Commons.THFAnnotated]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def thf(input: String): THFAnnotated = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.thf_annotated()
      TPTPASTConstructor.thfAnnotated(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Parses a annotated TFF formula (including language specification, name, etc).
    *
    * @note If you do not know which kind of formula (fof, thf, ...) you want to parse,
    *       use [[leo.modules.parsers.TPTP#annotatedFormula]] instead.
    * @param input A <annotated_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.Commons.TFFAnnotated]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def tff(input: String): TFFAnnotated = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.tff_annotated()
      TPTPASTConstructor.tffAnnotated(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  /**
    * Parses a annotated CNF formula (including language specification, name, etc).
    *
    * @note If you do not know which kind of formula (fof, thf, ...) you want to parse,
    *       use [[leo.modules.parsers.TPTP#annotatedFormula]] instead.
    * @param input A <annotated_formula> as string to be parsed
    * @return A [[leo.datastructures.tptp.Commons.CNFAnnotated]] representing `input`
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
    */
  protected[parsers] final def cnf(input: String): CNFAnnotated = {
    val inputStream = CharStreams.fromString(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val parseResult = parser.cnf_annotated()
      TPTPASTConstructor.cnfAnnotated(parseResult)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_SyntaxError, s"Unrecognized input: ${e.toString} ")
    }
  }

  import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}
  final private class ParserErrorListener extends BaseErrorListener {
    override def syntaxError(recognizer: Recognizer[_, _], o: scala.Any, line: Int, pos: Int, s: String, e: RecognitionException): Unit = {
      var sourceName = recognizer.getInputStream.getSourceName
      if (sourceName == "<unknown>") sourceName = s"$sourceName:$line:$pos"
      else if (leo.Configuration.isInit) sourceName = s"${leo.Configuration.PROBLEMFILE}:$line:$pos"
      if (e == null) throw new SZSException(SZS_SyntaxError, s"$s in $sourceName")
      else throw new SZSException(SZS_SyntaxError, s"$s in $sourceName", e.toString)
    }
  }
}

