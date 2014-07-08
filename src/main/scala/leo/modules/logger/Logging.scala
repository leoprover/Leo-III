//package leo.modules.logger
//
//import org.apache.logging.log4j._
///**
// * Created by lex on 08.07.14.
// */
//trait Logging {
//  private[this] val log = LogManager.getLogger(logName)
//
//  def logName: String = getClass.getName
//
//  import org.apache.logging.log4j.Level._
//
//  def trace(msg: => String): Unit = if (log.isEnabled(TRACE)) log.trace(logName + ": " + msg)
//  def debug(msg: => String): Unit = if (log.isEnabled(DEBUG)) log.trace(logName + ": " + msg)
//  def info(msg: => String): Unit  = if (log.isEnabled(INFO))  log.trace(logName + ": " + msg)
//  def warn(msg: => String): Unit  = if (log.isEnabled(WARN))  log.trace(logName + ": " + msg)
//  def error(msg: => String): Unit = if (log.isEnabled(ERROR)) log.trace(logName + ": " + msg)
//  def fatal(msg: => String): Unit = if (log.isEnabled(FATAL)) log.trace(logName + ": " + msg)
//}
