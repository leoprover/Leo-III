package leo

import java.util.logging.Level

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
  private var configMap: Map[String, Seq[String]] = _

  private val PARAM_THREADCOUNT = "n"
  private val PARAM_VERBOSITY = "v"
  private val PARAM_TIMEOUT = "t"
  private val PARAM_PROOFOBJECT = "p"
  private val PARAM_HELP = "h"
  private val PARAM_COUNTERSAT = "c"
  private val PARAM_SOS_SHORT = "s"
  private val PARAM_SOS_LONG = "sos"
  private val PARAM_UNIFICATIONDEPTH = "ud"

  // Collect standard options for nice output: short-option -> (long option, argname, description)
  private val optionsMap : Map[Char, (String, String, String)] = {
    Map(
      'h' -> ("", "", "Display this help message"),
      'n' -> ("", "N", "Maximum number of threads"),
      'p' -> ("", "", "Display proof output"),
      't' -> ("", "N", "Timeout in seconds"),
      'v' -> ("", "Lvl", "Set verbosity: From 0 (No Logging output) to 6 (very fine-grained debug output)"),
      'c' -> ("", "Csat", "Sets the proof mode to counter satisfiable (Through remote proof"),
      's' -> ("sos", "", "Use SOS heuristic search strategy"),
      'a' -> ("atp", "name=call", "Addition of external provers")
    )
  }

  /////////////////////////

  def init(parameterParser: CLParameterParser): Unit = configMap match {
    case null => {
      configMap = Map()
      for(param <- parameterParser.getParameters) {
        configMap += param
      }
      // Force computation of lazy values for early error output
      PROBLEMFILE
      THREADCOUNT
      TIMEOUT
      PROOF_OBJECT
      VERBOSITY
      COUNTER_SAT
      SOS
      ATPS
      HELP
      ()
    }
    case _ => ()
  }

  //////////////////////////
  // Predefined parameters
  //////////////////////////

  lazy val HELP: Boolean = isSet(PARAM_HELP)

  lazy val PROBLEMFILE: String = configMap.get(CLParameterParser.ARG0Name) match {
    case None => throw new IllegalArgumentException("No problem file given. Aborting.")
    case Some(str :: Nil) => str
    case Some(_) => throw new IllegalArgumentException("This should not happen. Please call support hotline.")
  }

  lazy val THREADCOUNT: Int = uniqueIntFor(PARAM_THREADCOUNT, DEFAULT_THREADCOUNT)

  lazy val VERBOSITY: java.util.logging.Level = {
    val v = configMap.get(PARAM_VERBOSITY) match {
      case None => DEFAULT_VERBOSITY
      case Some(arg :: Nil) => processLevel(arg)
      case Some(arg :: _) => Out.warn(multiDefOutput(PARAM_VERBOSITY))
                             processLevel(arg)
      case Some(_) => Out.warn(intExpectedOutput(PARAM_VERBOSITY,"None"))
                      DEFAULT_VERBOSITY
    }
    Out.setLogLevel(v)
    v
  }

  lazy val TIMEOUT: Int = {
    if (configMap.get(PARAM_TIMEOUT).isEmpty) Out.info(s"No timeout was given, using default timeout -t $DEFAULT_TIMEOUT")
    uniqueIntFor(PARAM_TIMEOUT, DEFAULT_TIMEOUT)
  }

  lazy val PROOF_OBJECT : Boolean = isSet(PARAM_PROOFOBJECT)
  lazy val UNIFICATION_DEPTH: Int = uniqueIntFor(PARAM_UNIFICATIONDEPTH, DEFAULT_UNIFICATIONDEPTH)
  lazy val SOS: Boolean = isSet(PARAM_SOS_LONG) || isSet(PARAM_SOS_SHORT)

  lazy val COUNTER_SAT : Boolean = isSet(PARAM_COUNTERSAT)
  import leo.datastructures.{Precedence,ClauseProxyWeights,LiteralWeights}

  lazy val CLAUSEPROXY_WEIGHTING: ClauseProxyWeight = ClauseProxyWeights.litCount

  lazy val LITERAL_WEIGHTING: LiteralWeight = LiteralWeights.termsize

  lazy val TERM_ORDERING: TermOrdering = leo.datastructures.impl.orderings.TO_CPO_Naive

  lazy val PRECEDENCE: Precedence = Precedence.arityInvOrder

  lazy val ATPS : Seq[(String, String)] = {
    val a = valueOf("a")
    if(a.nonEmpty) {
      val atps = a.get
      atps.filter(_.contains("=")).map{(s : String)  =>
        val eses = s.split("=",2)
        (eses(0), eses(1))
      }
    }
    else {
      val b = valueOf("atp")
      if(b.nonEmpty) {
        val atps = b.get
        atps.filter(_.contains("=")).map{(s : String)  =>
          val eses = s.split("=",2)
          (eses(0), eses(1))
        }
      }
      else Seq()
    }
  }

  // more to come ...

  ///////////////
  // Help output
  ///////////////
  lazy val helptext = {
    val sb = StringBuilder.newBuilder
    sb.append("Leo III -- A Higher-Order Theorem Prover.\n")
    sb.append("Christoph Benzmüller, Alexander Steen, Max Wisniewski and others.\n\n")
    sb.append("Usage: ... PROBLEM_FILE [OPTIONS]\n")
    sb.append("Options:\n")
    val it = optionsMap.iterator
    while (it.hasNext) {
      val entry = it.next()
      sb.append(s"-${entry._1}")
      if (!entry._2._2.isEmpty) {
        sb.append(s" ${entry._2._2}")
      }
      if (!entry._2._1.isEmpty) {
        sb.append(s", --${entry._2._1} ${entry._2._2}")
      }
      sb.append(s"\t\t${entry._2._3}\n")
    }
    sb.append("\n")
    sb.toString
  }
  def help(): Unit = Out.output(helptext)

  ////////////
  // Utility
  ////////////
  protected def processLevel(actual: String): Level = safeStrToInt(actual) match {
    case None => DEFAULT_VERBOSITY
    case Some(0) => Level.OFF
    case Some(1) => Level.WARNING
    case Some(2) => Level.INFO
    case Some(3) => Level.CONFIG
    case Some(4) => Level.FINE
    case Some(5) => Level.FINER
    case Some(6) => Level.FINEST
    case _ =>
      Out.warn(s"Allowed verbosity levels for parameter $PARAM_VERBOSITY are integers from 0 (including) to 6 (including).");
      DEFAULT_VERBOSITY
  }

  protected def uniqueIntFor(param: String, default: Int): Int = if (configMap == null) default
    else configMap.get(param) match {
    case None => default
    case Some(arg :: Nil) => processIntFor(param, arg, default)
    case Some(arg :: _) =>
      Out.warn(multiDefOutput(param))
      processIntFor(param, arg, default)
    case Some(_) => Out.warn(intExpectedOutput(param, "None"))
      default
  }
  protected def processIntFor(param: String, actual: String, default: Int): Int = {
    safeStrToInt(actual).getOrElse({
      Out.warn(intExpectedOutput(param, actual))
      default})
  }

  protected def multiDefOutput(paramName: String): Output = new Output {
    val apply = s"Parameter $paramName was defined multiple times. First occurrence is used, the rest is ignored."
  }
  protected def intExpectedOutput(paramName: String, actual: String): Output = new Output {
    val apply = s"Parameter $paramName expects an Integer value, but '$actual' was given. Default value is used."
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
  def valueOf(param: String): Option[Seq[String]] = configMap.get(param) match {
    case None => None
    case Some(Seq()) => None
    case rest => rest
  }
  def isSetTo(param: String, arg: String): Boolean =
    configMap.get(param).fold(false)(args => args.length == 1 && args(0) == arg)

}

trait DefaultConfiguration {
  val DEFAULT_THREADCOUNT = 4
  val DEFAULT_VERBOSITY = java.util.logging.Level.INFO
  val DEFAULT_TIMEOUT = 60
  val DEFAULT_UNIFICATIONDEPTH = 8
}
