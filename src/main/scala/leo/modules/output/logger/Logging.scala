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

  protected def loggerName: String = getClass.getName
  protected def defaultLogLevel: Level = Configuration.DEFAULT_VERBOSITY
  protected def useParentLoggers: Boolean = true

  import java.util.logging.Level._

  final def finest(msg: => String): Unit = if (log.isLoggable(FINEST)) log.finest(msg)
  final def finest(msg: Output): Unit = if (log.isLoggable(FINEST)) log.finest(msg.output)
  final def trace(msg: => String): Unit = if (log.isLoggable(FINER)) log.finer(msg)
  final def trace(msg: Output): Unit = if (log.isLoggable(FINER)) log.finer(msg.output)
  final def debug(msg: => String): Unit = if (log.isLoggable(FINE)) log.fine(msg)
  final def debug(msg: Output): Unit = if (log.isLoggable(FINE)) log.fine(msg.output)
  final def config(msg: => String): Unit = if (log.isLoggable(CONFIG)) log.config(msg)
  final def config(msg: Output): Unit = if (log.isLoggable(CONFIG)) log.config(msg.output)
  final def info(msg: => String): Unit  = if (log.isLoggable(INFO))  log.info(msg)
  final def info(msg: Output): Unit  = if (log.isLoggable(INFO))  log.info(msg.output)
  final def warn(msg: => String): Unit  = if (log.isLoggable(WARNING))  log.warning(msg)
  final def warn(msg: Output): Unit  = if (log.isLoggable(WARNING))  log.warning(msg.output)
  final def severe(msg: => String): Unit = if (log.isLoggable(SEVERE)) log.severe(msg)
  final def severe(msg: Output): Unit = if (log.isLoggable(SEVERE)) log.severe(msg.output)

  import java.util.logging.Handler

  final def addLogHandler(h: Handler): Unit = log.addHandler(h)
  final def removeLogHandler(h: Handler): Unit = log.removeHandler(h)


  def setLogLevel(level: Level): Unit = {
    log.setLevel(level)
    log.getHandlers.toSeq.foreach(_.setLevel(level))
  }
}
