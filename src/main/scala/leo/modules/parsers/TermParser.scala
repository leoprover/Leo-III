//package leo.modules.parsers
//
//import scala.util.parsing.input.Reader
//
//import syntactical.TermParsers._
//import java.io.FileReader
//
///**
// * Created by lex on 16.06.14.
// */
//object TermParser {
//
//  /** must be absolute path */
//  def parseFromFile(file: String) = extract(parse(new FileReader(file), tptpFile))
//
//  def parseTPTPFile(input: Reader[Char]) = extract(parse(input, tptpFile))
//  def parseTPTPFile(input: String) = extract(parse(input, tptpFile))
//
//  def parseFormula(input: Reader[Char]) = extract(parse(input, annotatedFormula))
//  def parseFormula(input: String) = extract(parse(input, annotatedFormula))
//
//
//
//  private def extract[T](res: ParseResult[T]): Either[String, T] = {
//    res match {
//      case Success(x, _) => Right(x)
//      case noSu: NoSuccess => Left(noSu.msg)
//    }
//  }
//
//
//}
//
