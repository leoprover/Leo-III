package leo.modules.external

import java.util.concurrent.TimeUnit

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
    *
    * Waits for timeout[unit] time for the process to finish.
    * Returns true, if the process has finished, false otherwise.
    *
    * @param timout The amount of time to wait for the process to finish
    * @param unit The unit of the time waiting
    * @return true iff the process has successfully terminated
    */
  @throws[InterruptedException]
  def waitFor(timout : Long, unit : TimeUnit) : Boolean

  /**
    * Is the exitValue of the started Programm
 *
    * @return
    */
  @throws[InterruptedException]
  def exitValue : Int

  /**
    * Blocks until the outputs has been received.
    *
    * @return
    */
  @throws[InterruptedException]
  def out : Iterator[String]

  /**
    *
    * Blocks until the error stream has been received
    *
    * @return
    */
  @throws[InterruptedException]
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
    *
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