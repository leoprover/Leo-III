package leo.modules.parsers

import leo.modules.output.logger.Out

/**
 * Command-line parser class that takes the standard string-array input from the main function
 * and yields an `CLParameterParser` object that offers various
 * methods for accessing the processed parameters.
 *
 * Command line arguments are treated the following:
 *
 * So-called `short-options` consist of a single character and start with an hyphen, e.g. "-a".
 * `Long-options` consist of many characters and start with a double-hyphen, e.g. "--useMegaCoolPreprocessing".
 * Any option may be given (at most) one argument or it can used as a switch, e.g. "-a" vs. "-a 2" for value `2` for parameter `a`.
 * Argument are written behind the parameter with at least one space in between.
 * Short-options that do not expect a value can be combined, e.g. "-afJ". All parameters
 * are case-sensitive.
 * All trailing parameters without switches and modifiers are parsed as one argument of a special name
 * that can be changed, default special name is "ARG". Long-options must be strictly longer than one character.
 *
 * @author Alexander Steen
 * @since 13.11.2014
 *
 * @param args
 */
class CLParameterParser(protected val args: Array[String]) {
  type ParameterMap = Map[String, Seq[String]]
  val paraMap : ParameterMap = parse(args.toList)

  def isSet(param: String): Boolean = paraMap.get(param).isDefined
  def getParameters: Iterator[(String, Seq[String])] = paraMap.iterator
  def getParameter(param: String): Option[Seq[String]] = paraMap.get(param)


  protected def parse(argList: List[String]): ParameterMap = argList match {
    case Nil => {
      Map.empty
    }
    case arg0 :: tail => parse0(tail, Map((CLParameterParser.ARG0Name, Seq(arg0))))
  }
  protected def parse0(argList: List[String], map: ParameterMap): ParameterMap = {
    argList match {
      case Nil => map
      case arg :: value :: tail if isLong(arg) && isArg(value) => val cArg = arg.drop(2); map.get(cArg) match {
        case None => parse0(tail, map + ((cArg, Seq(value))))
        case Some("" :: Nil) => {
          Out.warn(s"Command-line argument '$cArg' occurred with and without parameter. First argument occurrence is used.")
          parse0(tail, map)
        }
        case Some(list) => parse0(tail, map + ((cArg, list :+ value)))
      }
      case arg :: tail if isLong(arg) => val cArg = arg.drop(2); map.get(cArg) match {
        case None => parse0(tail, map + ((cArg, Seq())))
        case Some("" :: Nil) => {
          Out.trace(s"Reuse of command-line argument '$cArg'. Skipped.")
          parse0(tail, map)
        }
        case Some(_) => {
          Out.warn(s"Command-line argument '$cArg' occurred with and without parameter. Using first argument occurrence(s) with values.")
          parse0(tail, map)
        }
      }
      case arg :: value :: tail if isShort(arg) && isArg(value) => val cArg = arg.drop(1); map.get(cArg) match {
        case None => parse0(tail, map + ((cArg, Seq(value))))
        case Some("" :: Nil) => {
          Out.warn(s"Command-line argument '$cArg' occurred with and without parameter. First argument occurrence is used.")
          parse0(tail, map)
        }
        case Some(list) => parse0(tail, map + ((cArg, list :+ value)))
      }
      case arg :: tail if isShort(arg) && arg.length == 2 => val cArg = arg.drop(1); map.get(cArg) match {
        case None => parse0(tail, map + ((cArg, Seq())))
        case Some("" :: Nil) => {
          Out.trace(s"Reuse of command-line argument '$cArg'. Skipped.")
          parse0(tail, map)
        }
        case Some(_) => {
          Out.warn(s"Command-line argument '$cArg' occurred with and without parameter. Using first argument occurrence(s) with values.")
          parse0(tail, map)
        }
      }
      case arg :: tail if isShort(arg) => {
        parse0(splitShort(arg.drop(1)) ++ tail, map)
      }
      case arg :: tail => {
        Out.warn(s"Runaway argument $arg (skipped).")
        parse0(tail, map)
      }
    }
  }

  protected def isShort(arg: String): Boolean = arg.length match {
    case n if n >= 2 => arg(0) == '-' && arg(1) != '-'
    case _ => false
  }

  protected def isLong(arg: String): Boolean = arg.length match {
    case n if n >= 3 => arg(0) == '-' && arg(1) == '-'
    case _ => false
  }

  protected def isArg(arg: String): Boolean = arg(0) != '-'

  protected def splitShort(arg: String): List[String] = {
    arg.toCharArray.toList.map(a => s"-$a")
  }
}

object CLParameterParser {
  def ARG0Name: String = "ARG"
}
