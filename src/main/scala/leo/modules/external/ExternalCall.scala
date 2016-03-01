package leo.modules.external

import scala.sys.process.ProcessIO

/**
  *
  * Capsules a call to an external call to a
  * program, script or command run on a shell.
  *
  */
trait ExternalCall {
  /**
    *
    * Executes the call asynchronously
    * and returns a result object, that can be
    * used to access the result or terminate the process.
    *
    * @return
    */
  def execute() : ExternalResult
}

/**
  *
  * Kapsules the Computation of an External Call.
  *
  */
trait ExternalResult {
  /**
    * Is the exitValue of the started Programm
 *
    * @return
    */
  def exitValue : Int

  /**
    * Blocks until the outputs has been received.
    * @return
    */
  def out : Iterator[String]

  /**
    *
    * Blocks until the error stream has been received
    * @return
    */
  def error : Iterator[String]

  /**
    * Terminates the computation, if it is not yet killed.
    */
  def kill() : Unit
}

/**
  * Object to call to external commands or scripts.
  */
object ExternalCall {
  /**
    * Runs a command (with parameters) and a newly created file,
    * @param cmd  The command to be executed
    * @param fileArgs Lines of a file to be passed to the command
    * @return An asynchronous Wrapper to the Result
    */
  def exec(cmd : String, fileArgs : Seq[String]) : ExternalResult = (new ExternalCommandOnFile(cmd, fileArgs)).execute()

  /**
    *
    * Runs a command.
    *
    * @param cmd The command
    * @return An asynchronous Wrapper to the Result
    */
  def exec(cmd : String) : ExternalResult = (new ExternalCommandOnFile(cmd, Seq())).execute()

  /**
    * Runs a script on a newly created file
    *
    * @param script Path to the script to be executed
    * @param fileArgs Lines of a file to be passed to the command
    * @return An asynchronous Wrapper to the Result
    */
  def run(script : String, fileArgs : Seq[String]) : ExternalResult = (new ScriptOnFile(script, fileArgs)).execute()
}