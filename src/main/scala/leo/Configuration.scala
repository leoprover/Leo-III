package leo

import java.util.logging.Level

import leo.datastructures.{Literal}
import leo.modules.CLParameterParser
import leo.modules.output.Output

/**
 * Configuration access point where central parameter settings of Leo-III
 * can be accessed and read. The Configuration object needs to be initialized
 * using the `init` method and a command line argument parser, i.e. subclasses of
 * `CLParameterParser`,
 *
 * @author Alexander Steen
 * @since 13.11.2014
 */
object Configuration extends DefaultConfiguration {
  private var configMap: Map[String, Seq[String]] = null

  private val PARAM_THREADCOUNT = "n"
  private val PARAM_VERBOSITY = "v"
  private val PARAM_TIMEOUT = "t"
  private val PARAM_PROOFOBJECT = "p"

  def init(parameterParser: CLParameterParser): Unit = configMap match {
    case null => {
      configMap = Map()
      for(param <- parameterParser.getParameters) {
        configMap += (param)
      }
      // Force values of lazy vals s.t. potential errors are reported as early as possible
      PROBLEMFILE
      THREADCOUNT
      VERBOSITY
      TIMEOUT
      ()
    }
    case _ => ()
  }

  //////////////////////////
  // Predefined parameters
  //////////////////////////

  lazy val PROBLEMFILE: String = configMap.get(CLParameterParser.ARG0Name) match {
    case None => ""
    case Some(str :: Nil) => str
    case Some(_) => ???
  }

  lazy val THREADCOUNT: Int = uniqueIntFor(PARAM_THREADCOUNT, DEFAULT_THREADCOUNT)

  lazy val VERBOSITY: java.util.logging.Level = configMap.get(PARAM_VERBOSITY) match {
    case None => DEFAULT_VERBOSITY
    case Some(arg :: Nil) => processLevel(arg)
    case Some(arg :: _) => Out.warn(multiDefOutput(PARAM_VERBOSITY)); processLevel(arg)
  }

  lazy val TIMEOUT: Int = uniqueIntFor(PARAM_TIMEOUT, DEFAULT_TIMEOUT)

  lazy val PROOF_OBJECT : Boolean = isSet(PARAM_PROOFOBJECT)

  import leo.datastructures.{LitWeight_TermSize, CLWeight_LitWeightSum, SimpleOrdering, CLOrdering_Lex_Weight_Age_Origin}
  lazy val CLAUSE_WEIGHTING: ClauseWeight = CLWeight_LitWeightSum
  lazy val CLAUSE_ORDERING: ClauseOrdering = CLOrdering_Lex_Weight_Age_Origin

  lazy val LITERAL_WEIGHTING: LiteralWeight = LitWeight_TermSize
  lazy val LITERAL_ORDERING: LiteralOrdering = new SimpleOrdering[Literal](LitWeight_TermSize)

  // more to come ...

  ////////////
  // Utility
  ////////////
  protected def processLevel(actual: String): Level = safeStrToInt(actual) match {
    case None => DEFAULT_VERBOSITY
    case Some(0) => Level.OFF
    case Some(1) => Level.INFO
    case Some(2) => Level.CONFIG
    case Some(3) => Level.FINE
    case Some(4) => Level.FINER
    case Some(5) => Level.FINEST
    case _ => {
      Out.warn(s"Allowed verbosity levels for parameter $PARAM_VERBOSITY are integers from 0 (including) to 5 (including).");
      DEFAULT_VERBOSITY}
  }

  protected def uniqueIntFor(param: String, default: Int): Int = configMap.get(param) match {
    case None => default
    case Some(arg :: Nil) => processIntFor(param, arg, default)
    case Some(arg :: _) => {
      Out.warn(multiDefOutput(param))
      processIntFor(param, arg, default)
    }
  }
  protected def processIntFor(param: String, actual: String, default: Int): Int = {
    safeStrToInt(actual).getOrElse({
      Out.warn(intExpectedOutput(param, actual))
      default})
  }

  protected def multiDefOutput(paramName: String): Output = new Output {
    val output = s"Parameter $paramName was defined multiple times. First occurrence is used, the rest is ignored."
  }
  protected def intExpectedOutput(paramName: String, actual: String): Output = new Output {
    val output = s"Parameter $paramName expects an Integer value, but '$actual' was given. Default value is used."
  }
  protected def safeStrToInt(str: String): Option[Int] = try {
    Some(str.toInt)
  } catch {
    case _:Throwable => None
  }

  //////////////////////////////
  // General purpose accessors
  //////////////////////////////

  def isSet(param: String): Boolean = configMap.get(param).isDefined
  def valueOf(param: String): Option[Seq[String]] = configMap.get(param)
  def isSetTo(param: String, arg: String): Boolean =
    configMap.get(param).fold(false)(args => args.length == 1 && args(0) == arg)

}

trait DefaultConfiguration {
  val DEFAULT_THREADCOUNT = 4
  val DEFAULT_VERBOSITY = java.util.logging.Level.INFO
  val DEFAULT_TIMEOUT = 60
}
