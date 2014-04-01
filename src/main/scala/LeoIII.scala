/**
 *
 * This class starts an interactive shell for leoIII
 *
 * Created by Max Wisniewski on 3/28/14.
 */


import scala.tools.nsc.Settings
import scala.tools.nsc.interpreter.ILoop
import interpreter.{interpreter => MyInt}

object LeoIII extends App {
  val settings = new Settings
  settings.usejavacp.value = true
  settings.deprecation.value = true
  settings.Yreplsync.value = true

  // No JLineReader Added to Project at the moment
  // settings.Xnojline.value = true

  val console = new LeoILoop()
  console.process(settings)
}

class LeoILoop extends ILoop {
  override def prompt = "leoIII> "

  addThunk {
    intp.beQuietDuring {
      intp.addImports("java.lang.Math._")
      intp.addImports("interpreter._")

      // Shorten Names for representation
      intp.addImports("tptp.Commons._")
      intp.addImports("tptp.Commons.{AnnotatedFormula=>Formula}")
    }
  }

  /**
   * Printing our own welcome message
   */
  override def printWelcome {
    echo(MyInt.welcome)
  }

  /**
   * If something should be done in the start up phase
   */
  override def loop(){
    // Calling the interpreter loop
    intp.beQuietDuring {
      intp.interpret("def help = interpreter.help")
      intp.interpret("def info (ask : String) = interpreter.info(ask)")
      intp.interpret("def load (file : String) = interpreter.load(file)")
      intp.interpret("def add(f : String)= interpreter.add(f)")
      intp.interpret("def context = interpreter.display")
      intp.interpret("def clear = interpreter.clear")
      intp.interpret("def rm(i : Int) = interpreter.rm(i)")
    }
    super.loop()
  }
}
