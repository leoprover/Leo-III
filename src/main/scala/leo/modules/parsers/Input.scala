package leo.modules.parsers

import java.io.{BufferedReader, InputStreamReader}
import java.nio.file.{Files, Path, Paths}

import leo.datastructures.tptp.Commons
import leo.datastructures.tptp.thf.{LogicFormula => THFFormula}
import leo.datastructures.{Role, Signature, Term}
import leo.modules.SZSException
import leo.modules.output.SZS_InputError

/**
  * This facade object publishes various methods for parsing/processing/reading
  * of TPTP inputs. The method names are as follows by convention:
  *
  * - parseX: Raw input (e.g. string) -> TPTP AST representation
  * - processX: TPTP AST representation -> Term
  * - readX: Raw input (e.g. string) -> Term
  *
  * The most usual methods may be [[leo.modules.parsers.Input#readProblem]] and
  * [[leo.modules.parsers.Input#readFormula]] for reading a whole
  * TPTP problem and readling a single formula, respectively.
  *
  * @example {{{implicit val s: Signature = ...
  * val t: Term = readFormula("! [X:$i]: (p @ X)")
  * println(t.pretty(s))}}}
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 29.04.2015
  * @note Updated February 2017: Overhaul
  * @see [[leo.datastructures.tptp]]
  * @see [[leo.datastructures.Term]]
 */
object Input {

  /** Reads the `TPTP` environment variable, e.g. used
    * for parsing objects under TPTP Home. */
  lazy val tptpHome: Path = {
    try {
      val result = canonicalPath(System.getenv("TPTP"))
      result
    } catch {
      case _: Exception =>
        leo.Out.warn("TPTP environment variable not set. Some includes may not be found.")
        null
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Functions that go from file/string to unprocessed internal TPTP-syntax
  // "String -> TPTP"
  ///////////////////////////////////////////////////////////////////////////

  /**
    * Reads the file located at `file` and parses it recursively using the `TPTP` parser,
    * hence the file needs to be in valid TPTP format (e.g. FOF, THF, ...).
    * Note that the return value is a sequence of [[leo.datastructures.tptp.Commons.AnnotatedFormula]] since
    * all includes are automatically parsed exhaustively.
    * If `file` is a relative path, it is assumed to be equivalent to the path
    * `user.dir`/file.
    *
    * @note This method has no side effects.
    *
    * @param file  The absolute or relative path to the problem file.
    * @param assumeRead Implicitly assume that the problem files in this parameter
    *                   have already been read. Hence, recursive parsing will skip this
    *                   includes.
    * @return The sequence of annotated TPTP formulae.
    */
  def parseProblemFile(file: String, assumeRead: Set[Path] = Set()): Seq[Commons.AnnotatedFormula] = {
    val canonicalFile = canonicalPath(file)
    if (!assumeRead.contains(canonicalFile)) {
      val p: Commons.TPTPInput = parseProblemFileShallow(file)
      val includes = p.getIncludes

      // TODO Assume Read should be a shared between the calls (Dependencies between siblings not detected)

      val pIncludes = includes.map{case (inc, _) =>
        try {
          val next = canonicalFile.getParent.resolve(inc)
          parseProblemFile(next.toString, assumeRead + canonicalFile)
        } catch {
          case e : Exception =>
            try {
              if (tptpHome != null) {
                val tnext = tptpHome.resolve(inc)
                parseProblemFile(tnext.toString, assumeRead + canonicalFile)
              } else throw e
            } catch {
              case _ : Exception => throw new SZSException(SZS_InputError, s"The file $inc does not exist.")
            }
        }
      }
      pIncludes.flatten ++ p.getFormulae
    } else {
      Seq()
    }
  }

  /**
    * Parses the problem represented by `problem` recursively using the `TPTP` parser,
    * hence the string needs to be in valid TPTP format (e.g. FOF, THF, ...).
    * Note that the return value is a sequence of [[leo.datastructures.tptp.Commons.AnnotatedFormula]] since
    * all includes are automatically parsed exhaustively.
    * If the problem contains relative includes they are assumed
    * to be equivalent to includes of `user.dir/...`.
    *
    * @note This method has no side effects.
    *
    * @param problem  The problem as string.
    * @param assumeRead Implicitly assume that the problem files in this parameter
    *                   have already been read. Hence, recursive parsing will skip this
    *                   includes.
    * @return The sequence of annotated TPTP formulae.
    */
  def parseProblem(problem: String, assumeRead: Set[Path] = Set()): Seq[Commons.AnnotatedFormula] = {
    val p: Commons.TPTPInput = TPTP.parseFile(problem)
    val includes = p.getIncludes

    // TODO Assume Read should be a shared between the calls (Dependencies between siblings not detected)
    val pIncludes = includes.map{case (inc, _) =>
      try {
        val next = java.nio.file.Paths.get(System.getProperty("user.dir")).resolve(inc)
        parseProblemFile(next.toString, assumeRead)
      } catch {
        case e : Exception =>
          try {
            if (tptpHome != null) {
              val tnext = tptpHome.resolve(inc)
              parseProblemFile(tnext.toString, assumeRead)
            } else throw e
          } catch {
            case _ : Exception => throw new SZSException(SZS_InputError, s"The file $inc does not exist.")
          }
      }
    }
    pIncludes.flatten ++ p.getFormulae
  }

  /**
    * Reads the file located at `file`  and shallowly parses it using the `TPTP` parser, hence
    * the file needs to be in valid tptp format (regardless if FOF, TFF, ...).
    * Note that include statements are *NOT* recursively parsed but returned as TPTP
    * AST instead. For recursive parsing of include statements, use [[leo.modules.parsers.Input#parseProblem]].
    * If `file` is a relative path, it is assumed to be equivalent to the path
    * `user.dir`/file.
    *
    * @note This method has no side effects.
    *
    * @param file The absolute or relative path to the problem file.
    * @return The TPTP problem file in [[leo.datastructures.tptp.Commons.TPTPInput]] representation.
    */
  def parseProblemFileShallow(file: String): Commons.TPTPInput = {
    TPTP.parseFile(read0(canonicalPath(file)))
  }

  /**
    * Parses the single TPTP annotated formula given by `formula` into internal
    * TPTP AST representation.
    *
    * @note This method has no side effects.
    *
    * @param formula The formula to be parsed
    * @return The input formula in [[leo.datastructures.tptp.Commons.TPTPInput]] representation.
    */
  def parseAnnotated(formula: String): Commons.AnnotatedFormula = {
    TPTP.annotatedFormula(formula)
  }

  /**
    * Parses the single THF logic formula (i.e. without annotations)
    * given by `formula` into internal TPTP AST representation.
    *
    * @note This method has no side effects.
    *
    * @param formula The formula to be parsed without annotations, i.e. a <thf_logic_formula> as given by
    *                THF BNF.
    * @return The input formula in internal [[leo.datastructures.tptp.thf.LogicFormula]] representation
    * @see [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html#thf_logic_formula]] for TPTP THF BNF.
    */
  def parseFormula(formula: String): THFFormula = {
    TPTP(formula)
  }

  ///////////////////////////////////////////////////////////////////////////
  // Functions that go from internal TPTP syntax to processed internal representation (Term)
  // "TPTP -> Term"
  ///////////////////////////////////////////////////////////////////////////
  type FormulaId = String

  /**
    * Convert the problem given by the formulae in `problem` to internal term representation.
    * Note that the parameter type is `Commons.AnnotatedFormula`, hence
    * all include statements need to be read externally before calling this function.
    *
    * @note  Side effects: Type declarations and definitions within `problem` are added to the signature.
    * @param problem The input problem in [[leo.datastructures.tptp.Commons.AnnotatedFormula]] representation.
    * @return A triple `(id, term, role)` for each `formula` within
    *         `problem`, such that `id` is the identifier of `formula`, `term` is the respective
    *         [[leo.datastructures.Term]] representation of `formula`, and `role` is the role of `formula`
    *         as defined by the TPTP input.
    *         Note that formulae with role [[leo.datastructures.Role_Definition]] or
    *         [[leo.datastructures.Role_Type]] are returned as triples
    *         `(id, LitTrue, role)` with their respective identifier and role.
    */
  def processProblem(problem: Seq[Commons.AnnotatedFormula])(implicit sig: Signature): Seq[(FormulaId, Term, Role)] = {
    InputProcessing.processAll(sig)(problem)
  }
  /**
    * Convert the `formula` to internal term representation.
    *
    * @note Side effects: If `formula` is a type declaration or a definition,
    * @param formula The input formula in [[leo.datastructures.tptp.Commons.AnnotatedFormula]] representation.
    * @return A triple `(id, term, role)` such that `id` is the identifier of `formula`, `term` is the respective
    *         [[leo.datastructures.Term]] representation of `formula`, and `role` is the role of `formula`
    *         as defined by the TPTP input.
    *         Note that formulae with role [[leo.datastructures.Role_Definition]] or
    *         [[leo.datastructures.Role_Type]] are returned as triples
    *         `(id, LitTrue, role)` with their respective identifier and role.
    */
  def processFormula(formula: Commons.AnnotatedFormula)(implicit sig: Signature): (FormulaId, Term, Role) = {
    InputProcessing.process(sig)(formula)
  }

  ///////////////////////////////////////////////////////////////////////////
  // Functions that go from file/string to processed internal representation (Term)
  // "String -> Term"
  ///////////////////////////////////////////////////////////////////////////
  /**
    * Reads and recursively parses the file located at `file` and converts its
    * formulae to internal term representation, hence `file` needs to be in valid
    * TPTP syntax.
    * Note that the return value is a sequence of `(formulaId, term, role)` since
    * all includes are automatically parsed and converted exhaustively.
    * If `file` is a relative path, it is assumed to be equivalent to the path
    * `user.dir`/file.
    *
    * @note Side effect: Type declarations and definitions within `problem` are added to the signature.
    *
    * @param file  The absolute or relative path to the problem file.
    * @param assumeProcessed Implicitly assume that the problem files in this parameter
    *                   have already been read and processed.
    *                   Hence, recursive parsing will skip this includes.
    * @return A triple `(id, term, role)` for each `formula` within
    *         `file`, such that `id` is the identifier of `formula`, `term` is the respective
    *         [[leo.datastructures.Term]] representation of `formula`, and `role` is the role of `formula`
    *         as defined by the TPTP input.
    *         Note that formulae with role [[leo.datastructures.Role_Definition]] or
    *         [[leo.datastructures.Role_Type]] are returned as triples
    *         `(id, LitTrue, role)` with their respective identifier and role.
    */
  def readProblem(file: String, assumeProcessed: Set[Path] = Set())(implicit sig: Signature): Seq[(FormulaId, Term, Role)] = {
    processProblem(parseProblemFile(file,assumeProcessed))(sig)
  }

  /**
    * Reads and parses `formula` and converts it to internal term representation.
    *
    * @note Side effects: Type declarations and definitions within `problem` are added to the signature.
    *
    * @param formula The annotated formula to be parsed and converted.
    * @return A triple `(Id, Term, Role)`,
    *         such that `Id` is the identifier of the formula, `Clause` is the respective
    *         singleton clause in internal representation, and `Role` is the role of the formula as defined
    *         by the TPTP input.
    *         Note that a formula with role `definition` or `type` is returned as a triple
    *         `(Id, Clause($true), Role)` with its respective identifier and role.
    */
  def readAnnotated(formula: String)(implicit sig: Signature): (FormulaId, Term, Role) = {
    processFormula(parseAnnotated(formula))(sig)
  }

  /**
    * Parses and converts a single THF logic formula (i.e. without annotations)
    * given by `formula` into internal term representation.
    *
    * @note This method has no side effects.
    *
    * @param formula The formula to be parsed without annotations, i.e. a <thf_logic_formula> as given by
    *                THF BNF.
    * @return The input formula in internal [[leo.datastructures.Term]] representation
    * @see [[http://www.cs.miami.edu/~tptp/TPTP/SyntaxBNF.html#thf_logic_formula]] for TPTP THF BNF.
    */
  def readFormula(formula: String)(implicit sig: Signature): Term = {
    val parsed = TPTP(formula)
    val result = InputProcessing.processTHF(sig)(parsed)
    if (result.isLeft) result.left.get
    else throw new IllegalArgumentException
  }
  /** Synonym for [[leo.modules.parsers.Input#readFormula]]. */
  def apply(formula: String)(implicit sig: Signature): Term = readFormula(formula)

  final private val urlStartRegex:String  = "(\\w+?:\\/\\/)(.*)"

  /** Converts the input path to a canonical path representation */
  def canonicalPath(path: String): Path = {
    if (path.matches(urlStartRegex)) {
      if (path.startsWith("file://")) {
        Paths.get(path.drop("file://".length)).toAbsolutePath.normalize()
      } else {
        Paths.get(path)
      }
    } else {
      Paths.get(path).toAbsolutePath.normalize()
    }
  }

  final private val urlStartRegex0:String  = "(\\w+?:\\/)(.*)" // removed one slash because it gets removed by Paths.get(.)
  private def read0(absolutePath: Path): BufferedReader = {
    if (absolutePath.toString.matches(urlStartRegex0)) {
      // URL
      import java.net.URL
      val url = new URL(absolutePath.toString.replaceFirst(":/","://"))
      new BufferedReader(new InputStreamReader(url.openStream()))
    } else {
      if (!Files.exists(absolutePath)) { // It either does not exist or we cant access it
        throw new SZSException(SZS_InputError, s"The file ${absolutePath.toString} does not exist or cannot be read.")
      } else {
        Files.newBufferedReader(absolutePath)
      }
    }
  }
}
