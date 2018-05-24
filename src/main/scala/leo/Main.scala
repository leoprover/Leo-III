package leo

import leo.modules._
import leo.modules.output.{SZS_Error, SZS_Forced, SZS_MemoryOut}
import leo.modules.parsers.{CLParameterParser, Input}

/**
  * Entry Point for Leo-III as an executable.
  *
  * @see [[leo.modules.Modes]] for different modes Leo-III can be run with.
  * @author Max Wisniewski, Alexander Steen
  * @since 7/8/14
 */
object Main {
  private[this] var hook: scala.sys.ShutdownHookThread = _

  def main(args : Array[String]){
    try {
      val beginTime = System.currentTimeMillis()
      /** Hook is for returning an szs status if leo3 is killed forcefully. */
      hook = sys.addShutdownHook({
        Configuration.cleanup()
        Out.output(SZSResult(SZS_Forced, Configuration.PROBLEMFILE, "Leo-III stopped externally."))
      })

      /** Parameter stuff BEGIN */
      try {
        Configuration.init(new CLParameterParser(args))
      } catch {
        case e: IllegalArgumentException =>
          Out.severe(e.getMessage)
          Configuration.help()
          return
      }
      if (Configuration.PROBLEMFILE == "--caps") { // FIXME: Hacky, redo argument reading
        println(Configuration.CAPS)
        return
      }
      if (Configuration.PROBLEMFILE == s"--${Configuration.PARAM_VERSION}") { // FIXME: Hacky, redo argument reading
        println(s"Leo-III ${Configuration.VERSION}")
        return
      }
      if (Configuration.HELP ||
        Configuration.PROBLEMFILE == s"-${Configuration.PARAM_HELP}" ||
        Configuration.PROBLEMFILE == s"--${Configuration.PARAM_USAGE}") { // FIXME: Hacky, redo argument reading
        Configuration.help()
        return
      }
      /** Parameter stuff END */

      /** Call concrete functionality BEGIN */
      if (false) {
        // Functionality that does not need to parse the input file
        // none yet
      } else {
        // Functionality that need to parse the input file, do it now
        import leo.modules.parsers.{ModalPreprocessor => Modal, DDLPreprocessor => DDL}
        val problem0 = if (Configuration.isSet("ddl")) DDL.apply(Configuration.PROBLEMFILE)
        else Input.parseProblemFile(Configuration.PROBLEMFILE)
        // If it is a logic embedding, call the embedding tool, else just use the problem itself
        val problem = if (Modal.canApply(problem0)) Modal.apply(problem0)
                      else problem0
        // Functionality calls
        if (Configuration.isSet("seq")) {
          Modes.seqLoop(beginTime, Configuration.TIMEOUT, problem)
        } else if (Configuration.isSet("scheduled")) {
          Modes.scheduledSeq(beginTime, Configuration.TIMEOUT, problem)
        } else if (Configuration.isSet("pure-ext")) {
          Modes.runExternalProver(problem)
        } else if (Configuration.isSet("rules")) {
          Modes.agentRuleRun(beginTime, problem)
        } else if (Configuration.isSet("par")) {
          Modes.runParallel(beginTime, problem)
        } else if (Configuration.isSet("scheduled-par")) {
          Modes.runMultiSearch(beginTime, problem)
        } else if (Configuration.isSet("processOnly")) {
          Modes.normalizationOnly(problem)
        } else if (Configuration.isSet("syntaxcheck")) {
          Modes.syntaxCheck(problem)
        } else if (Configuration.isSet("typecheck")) {
          Modes.typeCheck(problem)
        } else if (Configuration.isSet("toTHF")) {
          Modes.toTHF(problem)
        } else {
          Modes.seqLoop(beginTime, Configuration.TIMEOUT, problem)
        }
      }
      /** Call concrete functionality END */
      
    } catch {
      /** Handle all top-level errors BEGIN */
      case e:Throwable =>
        Out.comment("OUT OF CHEESE ERROR +++ MELON MELON MELON +++ REDO FROM START")
        e match {
          case e0: SZSException =>
            Out.output(SZSResult(e0.status, Configuration.PROBLEMFILE,e0.getMessage))
            Out.debug(e0.debugMessage)
          case e0: OutOfMemoryError =>
            Out.output(SZSResult(SZS_MemoryOut, Configuration.PROBLEMFILE, e0.toString))
          case _ => Out.output(SZSResult(SZS_Error, Configuration.PROBLEMFILE,e.toString))
        }
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
      /** Handle all top-level errors END */
    } finally {
      Configuration.cleanup()
      hook.remove() // When we reach this code we didn't face a SIGTERM etc. so remove the hook.
    }
  }
}
