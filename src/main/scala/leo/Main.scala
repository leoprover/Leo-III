package leo

import leo.modules._
import leo.modules.output._
import leo.modules.parsers.CLParameterParser

/**
 * Entry Point for Leo-III as an executable to
 * proof a TPTP File
 *
 * @author Max Wisniewski, Alexander Steen
 * @since 7/8/14
 */
object Main {
  private var hook: scala.sys.ShutdownHookThread = _

  def main(args : Array[String]){
    try {
      val beginTime = System.currentTimeMillis()
      /** Hook is for returning an szs status if leo3 is killed forcefully. */
      hook = sys.addShutdownHook({
        Out.output(SZSOutput(SZS_Forced, Configuration.PROBLEMFILE, "Leo-III stopped externally."))
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
      if (Configuration.PROBLEMFILE == "--caps") {
        println(Configuration.CAPS)
        return
      }
      if (Configuration.HELP || Configuration.PROBLEMFILE == "-h") { // FIXME: Hacky, redo argument reading
        Configuration.help()
        return
      }
      /** Parameter stuff END */

      val config = {
        val sb = new StringBuilder()
        sb.append(s"problem(${Configuration.PROBLEMFILE}),")
        sb.append(s"time(${Configuration.TIMEOUT}),")
        sb.append(s"proofObject(${Configuration.PROOF_OBJECT}),")
        sb.append(s"sos(${Configuration.SOS}),")
        sb.append(s"primSubst(level=${Configuration.PRIMSUBST_LEVEL}),")
        sb.append(s"uniDepth(${Configuration.UNIFICATION_DEPTH}),")
        sb.append(s"unifierCount(${Configuration.UNIFIER_COUNT}),")
        // TBA ...
        sb.init.toString()
      }
      Out.config(s"Configuration: $config")

      if (Configuration.isSet("seq")) {
        Out.info("Running in sequential loop mode.")
        prover.SeqLoop(beginTime, Configuration.TIMEOUT)
      } else if (Configuration.isSet("scheduled-seq")) {
        Out.info("Running in scheduled sequential loop mode.")
        prover.ScheduledRun(beginTime, Configuration.TIMEOUT)
      } else if (Configuration.isSet("pure-ext")) {
        Out.info("Running in purely external mode.")
        RunExternalProver.runExternal()
      } else if (Configuration.isSet("rules")) {
        Out.info("Running in rules mode.")
        ParallelMain.agentRuleRun(beginTime)
      } else if (Configuration.isSet("par")) {
        Out.info("Running in parallel mode.")
        ParallelMain.runParallel(beginTime)
      } else if (Configuration.isSet("scheduled-par")) {
        Out.info("Running in scheduled parallel mode.")
        ParallelMain.runMultiSearch(beginTime)
      } else if (Configuration.isSet("processOnly")) {
        Out.info("Running in processOnly mode.")
        Normalization()
      } else {
        Out.info("No mode given, using sequential loop mode as default.")
        prover.SeqLoop(beginTime, Configuration.TIMEOUT)
      }
      
    } catch {
      case e:Throwable =>
        Out.comment("OUT OF CHEESE ERROR +++ MELON MELON MELON +++ REDO FROM START")
        e match {
          case e0: SZSException =>
            Out.output(SZSOutput(e0.status, Configuration.PROBLEMFILE,e0.toString))
            Out.debug(e0.debugMessage)
          case e0: OutOfMemoryError =>
            Out.output(SZSOutput(SZS_MemoryOut, Configuration.PROBLEMFILE, e0.toString))
          case _ => Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE,e.toString))
        }
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
    } finally {
      Configuration.cleanup()
      hook.remove() // When we reach this code we didnt face a SIGTERM etc. so remove the hook.
    }
  }
}
