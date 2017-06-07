package leo

import java.nio.file.Files

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
      hook = sys.addShutdownHook({
        Out.output(SZSOutput(SZS_Forced, Configuration.PROBLEMFILE, "Leo-III stopped externally."))
      })
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
      val leodir = Configuration.LEODIR
      if (!Files.exists(leodir)) Files.createDirectory(leodir)
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
        prover.SeqLoop(beginTime, Configuration.TIMEOUT)
      } else if (Configuration.isSet("scheduled-seq")) {
        prover.ScheduledRun(beginTime, Configuration.TIMEOUT)
      } else if (Configuration.isSet("pure-ext")) {
        RunExternalProver.runExternal()
      } else if (Configuration.isSet("rules")) {
        ParallelMain.agentRuleRun(beginTime)
      } else if (Configuration.isSet("par")) {
        ParallelMain.runParallel(beginTime)
      } else if (Configuration.isSet("scheduled-par")) {
        ParallelMain.runMultiSearch(beginTime)
      } else if (Configuration.isSet("processOnly")) {
        Normalization()
      } else {
        throw new SZSException(SZS_UsageError, "standard mode not included right now, use --seq")
      }
      
    } catch {
      case e:Throwable =>
        Out.comment("OUT OF CHEESE ERROR +++ MELON MELON MELON +++ REDO FROM START")
        if (e.isInstanceOf[SZSException]) {
          val e0 = e.asInstanceOf[SZSException]
          Out.output(SZSOutput(e0.status, Configuration.PROBLEMFILE,e.toString))
          Out.debug(e0.debugMessage)
        } else Out.output(SZSOutput(SZS_Error, Configuration.PROBLEMFILE,e.toString))
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.toString)
        }
    } finally {
      hook.remove() // When we reach this code we didnt face a SIGTERM etc. so remove the hook.
    }
  }
}
