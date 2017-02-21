package leo.modules.parsers

import java.io.{BufferedReader, StringReader}

import leo.datastructures.tptp.Commons.{AnnotatedFormula, TPTPInput}
import leo.datastructures.tptp.thf.{LogicFormula => THFFormula}
import leo.modules.SZSException
import leo.modules.output.SZS_InputError

/**
 * Provides a parsing interface for TPTP files and single tptp formulae.
 * The parser obeys the rules of the TPTP Syntax BNF found at
 * [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html]].
 *
 * @author Alexander Steen
 * @since 23.03.2014
 * @note Updated last on 2017/02
 */
object TPTP {
  /**
   * Parses a complete TPTP file yielding a [[leo.datastructures.tptp.Commons.TPTPInput]] value if succeeded.
   *
   * @param input A [[java.io.BufferedReader]] wrapping the TPTP input
   * @return A representation of the in file in [[leo.datastructures.tptp.Commons.TPTPInput]] format
    * @throws leo.modules.SZSException If a parse error or AST construction error occured.
   */
  protected[parsers] final def parseFile(input: java.io.BufferedReader): TPTPInput = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.tptp_file()
      TPTPASTConstructor.tptpFile(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
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
  protected[parsers] final def parseFile(input: String): TPTPInput = parseFile(new BufferedReader(new StringReader(input)))

  protected[parsers] final def apply(input: String): THFFormula = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.thf_logic_formula()
      TPTPASTConstructor.thfLogicFormula(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
    }
  }
  protected[parsers] final def annotatedFormula(input: String): AnnotatedFormula = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.annotated_formula()
      TPTPASTConstructor.annotatedFormula(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
    }
  }
  protected[parsers] final def fof(input: String): AnnotatedFormula = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.fof_annotated()
      TPTPASTConstructor.fofAnnotated(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
    }
  }
  protected[parsers] final def thf(input: String): AnnotatedFormula = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.thf_annotated()
      TPTPASTConstructor.thfAnnotated(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
    }
  }
  protected[parsers] final def tff(input: String): AnnotatedFormula = {
    import leo.modules.parsers.antlr._
    import org.antlr.v4.runtime._
    val inputStream = new ANTLRInputStream(input)
    val lexer = new tptpLexer(inputStream)
    val tokenStream = new CommonTokenStream(lexer)
    val parser = new tptpParser(tokenStream)
    parser.removeErrorListeners()
    parser.addErrorListener(new ParserErrorListener)
    try {
      val x = parser.tff_annotated()
      TPTPASTConstructor.tffAnnotated(x)
    } catch {
      case e: IllegalArgumentException => throw new SZSException(SZS_InputError, s"Unrecognized input: ${e.toString} ")
    }
  }

  import org.antlr.v4.runtime.{BaseErrorListener, RecognitionException, Recognizer}
  final private class ParserErrorListener extends BaseErrorListener {
    override def syntaxError(recognizer: Recognizer[_, _], o: scala.Any, line: Int, pos: Int, s: String, e: RecognitionException): Unit = {
      var sourceName = recognizer.getInputStream.getSourceName
      if (sourceName != "<unknown>") sourceName = s"$sourceName:$line:$pos"
      else sourceName = s"${leo.Configuration.PROBLEMFILE}:$line:$pos"
      if (e == null) throw new SZSException(SZS_InputError, s"$s in $sourceName")
      else throw new SZSException(SZS_InputError, s"$s in $sourceName", e.toString)
    }
  }
}

