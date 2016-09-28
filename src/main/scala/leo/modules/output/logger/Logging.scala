package leo.modules.output.logger

import java.util.logging.Logger
import java.util.logging.Level

import leo.Configuration
import leo.modules.output.Output

import scala.annotation.elidable

/**
 *
 * @author Alexander Steen
 * @since 10.11.2014
 * @note  Replaces obsolete trait formerly located at leo.modules.logger.Logging.
 */
trait Logging {
  protected lazy val log = {val l = Logger.getLogger(loggerName)
    l.setLevel(defaultLogLevel)
    l.setUseParentHandlers(useParentLoggers); l}

  /** The logger's name. */
  protected def loggerName: String = getClass.getName
  protected def defaultLogLevel: Level = Configuration.DEFAULT_VERBOSITY
  /** Whether to bubble-up the logging events to all parent logger handler. */
  protected def useParentLoggers: Boolean = true

  import java.util.logging.Level._

  /** Log ALL details for debug tracing, including verbose intermediate information. */
  @elidable(elidable.FINEST)
  final def finest(msg: => String): Unit = if (log.isLoggable(FINEST)) log.finest(msg)
  /** Log ALL details for debug tracing, including verbose intermediate information. */
  @elidable(elidable.FINEST)
  final def finest(msg: Output): Unit = if (log.isLoggable(FINEST)) log.finest(msg.apply)
  /** Log fine-grained debug trace information, i.e. small step messages with extensive information output. */
  @elidable(elidable.FINER)
  final def trace(msg: => String): Unit = if (log.isLoggable(FINER)) log.finer(msg)
  /** Log fine-grained debug trace information, i.e. small step messages with extensive information output. */
  @elidable(elidable.FINER)
  final def trace(msg: Output): Unit = if (log.isLoggable(FINER)) log.finer(msg.apply)
  /** Log coarse-grained debug messages that help tracing program flow. */
  @elidable(elidable.FINE)
  final def debug(msg: => String): Unit = if (log.isLoggable(FINE)) log.fine(msg)
  /** Log coarse-grained debug message Outputs that help tracing program flow. */
  @elidable(elidable.FINE)
  final def debug(msg: Output): Unit = if (log.isLoggable(FINE)) log.fine(msg.apply)
  /** Log important (static or run-time) configuration parameters, e.g. settings from command line. */
  @elidable(elidable.CONFIG)
  final def config(msg: => String): Unit = if (log.isLoggable(CONFIG)) log.config(msg)
  /** Log important (static or run-time) configuration parameters, e.g. settings from command line. */
  @elidable(elidable.CONFIG)
  final def config(msg: Output): Unit = if (log.isLoggable(CONFIG)) log.config(msg.apply)
  /** Log additional (run-time) information. */
  final def info(msg: => String): Unit  = if (log.isLoggable(INFO))  log.info(msg)
  /** Log additional (run-time) information. */
  final def info(msg: Output): Unit  = if (log.isLoggable(INFO))  log.info(msg.apply)
  /** Log a warning. These events describe errors/circumstances that might lead
    * to counter intuitive system behaviour or should simply not occur (even if not leading to a severe error). */
  final def warn(msg: => String): Unit  = if (log.isLoggable(WARNING))  log.warning(msg)
  /** Log a warning Output. These events describe errors/circumstances that might lead
    * to counter intuitive system behaviour or should simply not occur (even if not leading to a severe error). */
  final def warn(msg: Output): Unit  = if (log.isLoggable(WARNING))  log.warning(msg.apply)
  /** Log a message as `severe` error. These errors are likely to break the system/certain functionality. */
  final def severe(msg: => String): Unit = if (log.isLoggable(SEVERE)) log.severe(msg)
  /** Log an Output as `severe` error. These errors are likely to break the system/certain functionality. */
  final def severe(msg: Output): Unit = if (log.isLoggable(SEVERE)) log.severe(msg.apply)

  import java.util.logging.Handler

  final def addLogHandler(h: Handler): Unit = log.addHandler(h)
  final def removeLogHandler(h: Handler): Unit = log.removeHandler(h)


  def setLogLevel(level: Level): Unit = {
    log.setLevel(level)
    log.getHandlers.toSeq.foreach(_.setLevel(level))
  }

  def logLevel: Level = log.getLevel
  def logLevelAbove(level: Level): Boolean = {
    val thisLogLevel = log.getLevel
    level.intValue() > thisLogLevel.intValue()
  }
  def logLevelAtLeast(level: Level): Boolean = {
    val thisLogLevel = log.getLevel
    level.intValue() >= thisLogLevel.intValue()
  }
}
