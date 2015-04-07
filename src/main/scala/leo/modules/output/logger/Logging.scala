package leo.modules.output.logger

import java.util.logging.Logger
import java.util.logging.Level

import leo.Configuration
import leo.modules.output.Output

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
  final def finest(msg: => String): Unit = if (log.isLoggable(FINEST)) log.finest(msg)
  /** Log ALL details for debug tracing, including verbose intermediate information. */
  final def finest(msg: Output): Unit = if (log.isLoggable(FINEST)) log.finest(msg.output)
  /** Log fine-grained debug trace information, i.e. small step messages with extensive information output. */
  final def trace(msg: => String): Unit = if (log.isLoggable(FINER)) log.finer(msg)
  /** Log fine-grained debug trace information, i.e. small step messages with extensive information output. */
  final def trace(msg: Output): Unit = if (log.isLoggable(FINER)) log.finer(msg.output)
  /** Log coarse-grained debug messages that help tracing program flow. */
  final def debug(msg: => String): Unit = if (log.isLoggable(FINE)) log.fine(msg)
  /** Log coarse-grained debug message Outputs that help tracing program flow. */
  final def debug(msg: Output): Unit = if (log.isLoggable(FINE)) log.fine(msg.output)
  /** Log important (static or run-time) configuration parameters, e.g. settings from command line. */
  final def config(msg: => String): Unit = if (log.isLoggable(CONFIG)) log.config(msg)
  /** Log important (static or run-time) configuration parameters, e.g. settings from command line. */
  final def config(msg: Output): Unit = if (log.isLoggable(CONFIG)) log.config(msg.output)
  /** Log additional (run-time) information. */
  final def info(msg: => String): Unit  = if (log.isLoggable(INFO))  log.info(msg)
  /** Log additional (run-time) information. */
  final def info(msg: Output): Unit  = if (log.isLoggable(INFO))  log.info(msg.output)
  /** Log a warning. These events describe errors/circumstances that might lead
    * to counter intuitive system behaviour or should simply not occur (even if not leading to a severe error). */
  final def warn(msg: => String): Unit  = if (log.isLoggable(WARNING))  log.warning(msg)
  /** Log a warning Output. These events describe errors/circumstances that might lead
    * to counter intuitive system behaviour or should simply not occur (even if not leading to a severe error). */
  final def warn(msg: Output): Unit  = if (log.isLoggable(WARNING))  log.warning(msg.output)
  /** Log a message as `severe` error. These errors are likely to break the system/certain functionality. */
  final def severe(msg: => String): Unit = if (log.isLoggable(SEVERE)) log.severe(msg)
  /** Log an Output as `severe` error. These errors are likely to break the system/certain functionality. */
  final def severe(msg: Output): Unit = if (log.isLoggable(SEVERE)) log.severe(msg.output)

  import java.util.logging.Handler

  final def addLogHandler(h: Handler): Unit = log.addHandler(h)
  final def removeLogHandler(h: Handler): Unit = log.removeHandler(h)


  def setLogLevel(level: Level): Unit = {
    log.setLevel(level)
    log.getHandlers.toSeq.foreach(_.setLevel(level))
  }
}
