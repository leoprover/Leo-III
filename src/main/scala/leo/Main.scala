package leo

import leo.datastructures.{Role_Definition, Role_Type}
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
        Configuration.cleanup()
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
      } else if (Configuration.isSet("syntaxcheck")) {
        import leo.modules.parsers.Input
        Input.parseProblem(Configuration.PROBLEMFILE)
        Out.output(SZSOutput(SZS_Success, Configuration.PROBLEMFILE, s"Syntax check succeeded"))
        // if it fails the catch below will print it
      } else if (Configuration.isSet("typecheck")) {
        import leo.modules.parsers.Input
        import leo.datastructures.{Term, Signature, Role}
        val problem =  Input.parseProblem(Configuration.PROBLEMFILE)
        implicit val sig = Signature.freshWithHOL()
        val erg = Input.processProblem(problem)
        var notWellTyped: Seq[(Input.FormulaId, Term, Role)] = Seq.empty
        erg.foreach {case (id, t, role) =>
            val wellTyped = Term.wellTyped(t)
            if (!wellTyped) notWellTyped = notWellTyped :+ (id, t, role)
        }
        if (notWellTyped.isEmpty)
          Out.output(SZSOutput(SZS_Success, Configuration.PROBLEMFILE, s"Type check succeeded"))
        else
          Out.output(SZSOutput(SZS_TypeError, Configuration.PROBLEMFILE, s"${notWellTyped.map(_._1).mkString(",")} are not well-typed."))
      } else if (Configuration.isSet("toTHF")) {
        import leo.modules.parsers.Input
        import leo.datastructures.Signature
        import leo.modules.output.ToTPTP
        val problem =  Input.parseProblem(Configuration.PROBLEMFILE)
        implicit val sig = Signature.freshWithHOL()
        val sb: StringBuilder = new StringBuilder
        val erg = Input.processProblem(problem)
        sb.append(ToTPTP(sig))
        sb.append(ToTPTP.printDefinitions(sig))
        erg.foreach {case (id, t, role) =>
          if (role != Role_Definition && role != Role_Type) {
            sb.append(ToTPTP.toTPTP(id, termToClause(t), role)(sig))
            sb.append("\n")
          }
        }
        Out.output(SZSOutput(SZS_Success, Configuration.PROBLEMFILE, s"Translation finished."))
        Out.output(sb.toString())
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
