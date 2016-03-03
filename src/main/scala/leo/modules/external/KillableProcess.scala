package leo.modules.external

import java.io.{OutputStream, InputStream}
import java.lang.reflect.Field

/**
  * Companionobject to the KillableProcess.
  * Allows sigkill to be send to the currently executing process.
  */
object KillableProcess {

  private[external] val isUNIXSystem : Boolean= {
    try {
      val process = Runtime.getRuntime.exec("echo a")
      process.getClass().getName().equals("java.lang.UNIXProcess")    // If it allows unix like processes
    } catch{
      case _ : Exception => false
    }
  }

  /**
    * Creates a killable process.
    *
    * @param cmd the command to be executed
    * @return A Processhandle to the killable process
    */
  def apply(cmd : String) : KillableProcess = Command(cmd).exec()
}

/**
  * Killable Process. Calls for SigKill.
  */
trait KillableProcess {
  /**
    * Waits for the exit Value of the spawnt process.
    *
    * @return The exit Value of the process
    */
  def exitValue : Int

  /**
    *
    * Returns the output of the spawned process in a ReadOnly Stream
    *
    * @return stdout of the process
    */
  def output : InputStream

  /**
    * Allows to write to the process in a WriteOnly Stream
    *
    * @return stdin of the process
    */
  def input : OutputStream

  /**
    * Returns the error of the spawned process in a ReadOnly Stream
    *
    * @return stderr of the process
    */
  def error : InputStream

  /**
    * Sends a sigkill to the process
    */
  def kill : Unit
}

case class Command(cmd : String) {
  def exec() : KillableProcess = {
    val cmd1 : Array[String] = if(KillableProcess.isUNIXSystem) Array("/bin/sh","-c",cmd) else {Array(cmd)}   // If it is a Unix like system, we allow chaining
    val process = Runtime.getRuntime.exec(cmd1)
    new KillableProcessImpl(process)
  }
}


private class KillableProcessImpl(process : Process) extends KillableProcess {
  override def exitValue: Int = process.waitFor()

  override def output: InputStream = process.getInputStream
  override def input: OutputStream = process.getOutputStream
  override def error: InputStream = process.getErrorStream

  override def kill: Unit = {
    pid match {
      case Some(pid1) => Runtime.getRuntime.exec("kill -9 "+pid1)   // TODO If implemented for windows, extend to different kill
      case None => process.destroy()
    }
  }

  private def pid : Option[Int] = {
    if(process.getClass().getName().equals("java.lang.UNIXProcess")) {
      /* get the PID on unix/linux systems */
      try {
        val f : Field = process.getClass().getDeclaredField("pid")
        f.setAccessible(true)
        return Some(f.getInt(process))
      } catch {
        case _ : Throwable => return None
      }
    } else {
      return None   // TODO implement windows handles
    }
  }
}
