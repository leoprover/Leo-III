package leo.modules

import leo.datastructures.{Term, Role}
import leo.datastructures.tptp.Commons

/**
 * This facade object publishes some convenience methods
 * for parsing related utility including parsing of strings and problem files,
 *
 * @author Alexander Steen <a.steen@fu-berlin.de>
 * @since 29.04.2015
 */
object Parsing {

  implicit val noneRead: Seq[String] = Seq[String]()

  // Functions that go from file/string to unprocessed internal TPTP-syntax
  // "String -> TPTP"
  /**
   * Reads the file located at `file` and parses it recursively using the `TPTP` parser.
   * Note that the return value is a sequence of [[Commons.AnnotatedFormula]] since
   * all includes are automatically parsed exhaustively.
   * If `file` is a relative path, it is assumed to be equivalent to the path
   * [[Utility.wd]]/file where `Utility.wd` is the working directory `user.dir`.
   *
   * @param file  The absolute or relative path to the problem file.
   * @param assumeRead Implicitly assume that the problem files in this parameter
   *                   have already been read. Hence, recursive parsing will skip this
   *                   includes.
   * @return The sequence of annotated TPTP formulae.
   */
  def readProblem(file: String)(implicit assumeRead: Seq[String]): Seq[Commons.AnnotatedFormula] = ???

  /**
   * Parses the single TPTP syntax formula given by `formula` into internal
   * tptp syntax representation.
   *
   * @param formula The formula to be parsed
   * @return The input formula in internal TPTP syntax representation
   */
  def readFormula(formula: String): Commons.AnnotatedFormula = ???

  /**
   * Reads the file located at `file`  and shallowly parses it using the `TPTP` parser.
   * Note that include statements are NOT recursively parsed but returned in internal TPTP
   * syntax instead. For recursive parsing of include statements, use [[readProblem()]].
   * If `file` is a relative path, it is assumed to be equivalent to the path
   * [[Utility.wd]]/file where `Utility.wd` is the working directory `user.dir`.
   *
   * @param file The absolute or relative path to the problem file.
   * @return The TPTP problem file in internal [[Commons.TPTPInput]] representation.
   */
  def shallowReadProblem(file: String): Commons.TPTPInput = ???

  // Functions that go from internal TPTP syntax to processed internal representation (Term)
  // "TPTP -> Term"
  type FormulaId = String

  /**
   * Convert the problem given by the formulae in `problem` to internal term representation.
   * SIDE-EFFECTS: Type declarations and definitions within `problem` are added to the signature.
   *
   * Note that the parameter type is `Commons.AnnotatedFormula`, hence
   * all include statements need to be read externally before calling this function.
   *
   * @param problem The input problem in internal TPTP representation.
   * @return A triple `(Id, Term, Role)` for each `AnnotatedFormula` within
   *         `problem`, such that `Id` is the identifier of the formula, `Term` is the respective
   *         formula in internal representation, and `Role` is the role of the formula as defined
   *         by the TPTP input.
   *         Note that formulae with role `definition` or `type` are returned as triples
   *         `(Id, $true, Role)` with their respective identifier and role.
   */
  def processProblem(problem: Seq[Commons.AnnotatedFormula]): Seq[(FormulaId, Term, Role)] = ???
  /**
   * Convert the `formula` to internal term representation.
   * SIDE-EFFECTS: If `formula` is either a type declaration or a definition,
   * the respective declarations are added to the signature.
   *
   * @param formula The input formula in internal TPTP representation.
   * @return A triple `(Id, Term, Role)`,
   *         such that `Id` is the identifier of the formula, `Term` is the respective
   *         formula in internal representation, and `Role` is the role of the formula as defined
   *         by the TPTP input.
   *         Note that a formula with role `definition` or `type` is returned as a triple
   *         `(Id, $true, Role)` with its respective identifier and role.
   */
  def processFormula(formula: Commons.AnnotatedFormula): (FormulaId, Term, Role) = ???


  // Functions that go from file/string to processed internal representation (Term)
  // "String -> Term"

  /**
   * Reads and recursively parses the file located at `file` and converts its
   * formulae to internal term presentation.
   * Note that the return value is a sequence of `(FormulaId, Term, Role)` since
   * all includes are automatically parsed and converted exhaustively.
   * If `file` is a relative path, it is assumed to be equivalent to the path
   * [[Utility.wd]]/file where `Utility.wd` is the working directory `user.dir`.
   *
   * SIDE-EFFECTS: Type declarations and definitions within `problem` are added to the signature.
   *
   * @param file  The absolute or relative path to the problem file.
   * @param assumeProcessed Implicitly assume that the problem files in this parameter
   *                   have already been read and processed.
   *                   Hence, recursive parsing will skip this includes.
   * @return A triple `(Id, Term, Role)` for each formula within
   *         the problem, such that `Id` is the identifier of the formula, `Term` is the respective
   *         formula in internal representation, and `Role` is the role of the formula as defined
   *         by the TPTP input.
   *         Note that formulae with role `definition` or `type` are returned as triples
   *         `(Id, $true, Role)` with their respective identifier and role.
   */
  def parseProblem(file: String)(implicit assumeProcessed: Seq[String]): Seq[(FormulaId, Term, Role)] = ???

  /**
   * Reads and parses `formula` and converts it to internal term representation.
   *
   * SIDE-EFFECTS: Type declarations and definitions within `problem` are added to the signature.
   *
   * @param formula The formula to be parsed and converted.
   * @return A triple `(Id, Term, Role)`,
   *         such that `Id` is the identifier of the formula, `Term` is the respective
   *         formula in internal representation, and `Role` is the role of the formula as defined
   *         by the TPTP input.
   *         Note that a formula with role `definition` or `type` is returned as a triple
   *         `(Id, $true, Role)` with its respective identifier and role.
   */
  def parseFormula(formula: String): (FormulaId, Term, Role) = ???


}
