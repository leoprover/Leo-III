package leo.modules.external

import java.io.{PrintWriter, File}
import java.util.concurrent.TimeUnit

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

/**
  *
  * Simple external command with a sequence of
  * Strings that will be added to a file and passed to
  * the command.
  */
class ExternalCommandOnFile (cmd : String, fileArgs : Seq[String]) extends ExternalCall{
  override def execute(): ExternalResult = {
    val process = if(fileArgs.nonEmpty) {
      val file = File.createTempFile("remoteInvoke", ".p")
      file.deleteOnExit()
      val writer = new PrintWriter(file)
      try {
        fileArgs foreach { out =>
          writer.println(out)
        }
      } finally writer.close()
      // FIX ME : If a better solution for obtaining the processID is found
      val res = Seq(cmd, file.getAbsolutePath)

      new AExternalResult(res)
    } else {
      new AExternalResult(Seq(cmd))
    }
    process.go()  // Forking of the process, but not blocking

    process
  }
}

class ScriptOnFile (script : String, fileArgs : Seq[String]) extends ExternalCall {
  override def execute(): ExternalResult = {
    val file = File.createTempFile("remoteInvoke", ".p")
    file.deleteOnExit()
    val writer = new PrintWriter(file)
    try {
      fileArgs foreach { out =>
        writer.println(out)
      }
    } finally writer.close()
    // FIX ME : If a better solution for obtaining the processID is found
    val f = new File(script)
    if(!f.canExecute) throw new IllegalArgumentException(s"Script $script is not executable.")
    val res = Seq(f.getAbsolutePath, file.getAbsolutePath)

   val process =  new AExternalResult(res)
    process.go()  // Forking of the process, but not blocking

    process
  }
}









protected class AExternalResult(exec : Seq[String]) extends ExternalResult{

  val str: mutable.ListBuffer[String] = new ListBuffer[String]
  val errstr: mutable.ListBuffer[String] = new ListBuffer[String]
  var pid = -1
  var process : KillableProcess = null
  private var eV = 0
  private var fetch = false
  private var finished = false
  private var res : Iterator[String] = null
  private var err : Iterator[String] = null
  private var shouldTerm = false

  private[external] def go() : Unit = {
    process = KillableProcess(exec.mkString(" "))
  }

  private def waitForTerm() : Unit = synchronized{if(!shouldTerm && !fetch) {
    process.waitFor()
    eV = process.exitValue
    res = scala.io.Source.fromInputStream(process.output).getLines()
    err = scala.io.Source.fromInputStream(process.error).getLines()
    fetch = true
    finished = true
  }}

  /**
    * Is the exitValue of the started Programm
    *
    * @return
    */
  override def exitValue: Int = {
    if(!fetch && !shouldTerm) waitForTerm()
    eV
  }

  /**
    * The Value of the computation.
    *
    * @return
    */
  override def out: Iterator[String] = {
    if(!finished && shouldTerm)
      Seq()
    if(!fetch) waitForTerm()
    res
  }

  override def error : Iterator[String] = {
    if(!finished && shouldTerm)
      Seq()
    if(!fetch) waitForTerm()
    res
  }

  /**
    * Terminates the computation, if it is not yet killed.
    */
  override def kill(): Unit = {
    if(!fetch){ // The process is not yet started
      shouldTerm = true
      return
    }
    if(!finished){
      process.kill
    }
  }

  /**
    *
    * Waits for timeout[unit] time for the process to finish.
    * Returns true, if the process has finished, false otherwise.
    *
    * @param timeout The amount of time to wait for the process to finish
    * @param unit   The unit of the time waiting
    * @return true iff the process has successfully terminated
    */
  @throws[InterruptedException]
  override def waitFor(timeout: Long, unit: TimeUnit): Boolean = process.waitFor(timeout, unit)
}
