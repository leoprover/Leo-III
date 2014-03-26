package parsers.combinators

import scala.util.parsing.combinator.{JavaTokenParsers, RegexParsers, PackratParsers}
import parsers.PExec

/**
 * Created by lex on 3/23/14.
 */
class Commons extends PExec with PackratParsers with JavaTokenParsers {
  override type Target = tptp.Commons.TPTPInput
  override def target = tptpFile

  // Defining lexical tokens
  // Character classes
  def doChar: Parser[String] = """[\040-\041\043-\0133\0135-\0176]|[\\]["\\]""".r
  def sqChar: Parser[String] = """[\040-\046\050-\0133\0135-\0176]|[\\]['\\]""".r
  def alphaNumeric: Parser[String] = """[a-zA-Z0-9\_]""".r

  // Other tokens
  def commentLine: Parser[String] = """%.*""".r

  def singleQuoted: Parser[String] = "'" ~> rep1(sqChar) <~ "'" ^^ {_.fold("")((a,b) => a++b)}
  def distinctObject: Parser[String] = "\"" ~> rep(doChar) <~ "\"" ^^ {_.fold("")((a,b) => a++b)}

  def lowerWord: Parser[String] = """[a-z][A-Za-z0-9_]*""".r
  def upperWord: Parser[String] = """[A-Z][A-Za-z0-9_]*""".r
  def dollarWord: Parser[String] ="""\$[a-z][A-Za-z0-9_]*""".r
  def dollarDollarWord: Parser[String] ="""\$\$[a-z][A-Za-z0-9_]*""".r

  def integer: Parser[Int] = wholeNumber ^^ {_.toInt}
  def real: Parser[Double] = floatingPointNumber ^^ {_.toDouble}

  /*
   * Parsing rules
   */

  // Files
  def tptpFile: Parser[tptp.Commons.TPTPInput] = rep(tptpInput) ^^ {tptp.Commons.TPTPInput(_)}

  def tptpInput: Parser[Either[tptp.Commons.AnnotatedFormula, tptp.Commons.Include]] = (annotatedFormula ||| include) ^^ {
    case e1: tptp.Commons.AnnotatedFormula => Left(e1)
    case e2: tptp.Commons.Include  => Right(e2)
  }

  // Formula records
  def annotatedFormula: Parser[tptp.Commons.AnnotatedFormula] = failure("asd")

  def tpiAnnotated: Parser[tptp.FOF] = ???
  def thfAnnotated: Parser[tptp.THF] = ???
  def tffAnnotated: Parser[tptp.TFF] = ???
  def fofAnnotated: Parser[tptp.FOF] = ???
  def cnfAnnotated: Parser[tptp.CNF] = ???

  def annotations: Parser[tptp.Commons.Annotations] =
    "^$".r ^^ {_ => None} | ("," ~> source ~ optionalInfo) ^^ {
      case src ~ opt => Some((src,opt))
      }
  def formulaRole: Parser[String] = lowerWord


  def term: Parser[tptp.Commons.Term] = ???
  def variable: Parser[String] = upperWord

  // Formula sources and infos
  def source: Parser[tptp.Commons.GeneralTerm] = generalTerm
  def optionalInfo: Parser[List[tptp.Commons.GeneralTerm]] =
    "^$".r ^^ {_ => List.empty} | "," ~> usefulInfo

  def usefulInfo: Parser[List[tptp.Commons.GeneralTerm]] = generalList

  // Include directives
  def include: Parser[tptp.Commons.Include] =
    "include(" ~> singleQuoted ~ opt(",[" ~> repsep(name,",") <~"]") <~ ")" ^^ {
      case name ~ Some(names) => (name, names)
      case name ~ _           => (name, List.empty)
    }
  // Non-logical data (GeneralTerm, General data)
  def generalTerm: Parser[tptp.Commons.GeneralTerm] = ???

  def generalData: Parser[tptp.Commons.GeneralData] =
    atomicWord ^^ {tptp.Commons.GWord(_)} |
      generalFunction |
      variable ^^ {tptp.Commons.GVar(_)} |
      number ^^ {tptp.Commons.GNumber(_)} |
      distinctObject ^^ {tptp.Commons.GDistinct(_)} |
      formulaData ^^ {tptp.Commons.GFormulaData(_)}

  def generalFunction: Parser[tptp.Commons.GFunc] = atomicWord ~ "(" ~ generalTerms ~ ")" ^^ {
    case name ~ "(" ~ args ~ ")"  => tptp.Commons.GFunc(name,args)
  }

  def formulaData: Parser[tptp.Commons.FormulaData] =
    "$thf(" ~> THF.thfFormula <~ ")" ^^ {tptp.Commons.THFData(_)} |
      "$thf(" ~> TFF.tffFormula <~ ")" ^^ {tptp.Commons.TFFData(_)} |
      "$fof(" ~> FOF.fofFormula <~ ")" ^^ {tptp.Commons.FOFData(_)} |
      "$cnf(" ~> CNF.cnfFormula <~ ")" ^^ {tptp.Commons.CNFData(_)} |
      "$fot(" ~> term <~ ")" ^^ {tptp.Commons.FOTData(_)}

  def generalList: Parser[List[tptp.Commons.GeneralTerm]] =
    "[" ~> opt(generalTerms) <~ "]" ^^ {
      case Some(gt)   => gt
      case _       => List.empty
    }
  def generalTerms: Parser[List[tptp.Commons.GeneralTerm]] = rep1sep(generalTerm, ",")

  // General purpose
  def name: Parser[Either[String, Int]] = atomicWord ^^ {Left(_)} | integer ^^ {Right(_)}
  def atomicWord: Parser[String] = lowerWord | singleQuoted
  def atomicDefinedWord: Parser[String] = dollarWord
  def atomicSystemWord: Parser[String] = dollarDollarWord
  def number: Parser[Double] = integer ^^ {_.toDouble} | real

  def fileName: Parser[String] = singleQuoted
  //def formula: Parser[tptp.Commons.AnnotatedFormula] = null
}
