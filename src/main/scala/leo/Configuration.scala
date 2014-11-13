package leo

import leo.modules.CLParameterParser

/**
 * Created by lex on 13.11.14.
 */
object Configuration {
  private var configMap: Map[String, Seq[String]] = null
  private var parser: CLParameterParser = null

  val THREADCOUNTPARAM = "n"
  val THREADCOUNTDEFAULT = 4

  def init(parameterParser: CLParameterParser): Unit = configMap match {
    case null => {
      configMap = Map()
      for(param <- parameterParser.getParameters) {
        configMap += (param)
      }
      parser = parameterParser
    }
    case _ => ()
  }

  //////////////////////////
  // Predefined parameters
  //////////////////////////

  val PROBLEMFILE: String = configMap.get(parser.ARG0Name) match {
    case None => ""
    case Some(str) => str(0)
  }

  val THREADCOUNT: Int = configMap.get(THREADCOUNTPARAM) match {
    case None => THREADCOUNTDEFAULT
    case Some(str) => try {
      str(0).toInt
    } catch {
      case _:Throwable => THREADCOUNTDEFAULT
    }
  }
  val VERBOSITY: java.util.logging.Level = ???
  val TIMEOUT: Int = ???

  // more to come ...


  //////////////////////////////
  // General purpose accessors
  //////////////////////////////

  def isSet(param: String): Boolean = ???
  def valueOf(param: String): Option[String] = ???
  def isSetTo(param: String, arg: String): Boolean = ???

}
