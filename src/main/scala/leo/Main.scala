package leo

import leo.modules._
import leo.modules.output.{SZS_Forced, SZS_Error, SZS_MemoryOut}
import leo.modules.parsers.{CLParameterParser, Input}

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
      if (Configuration.PROBLEMFILE == "--caps") {
        println(Configuration.CAPS)
        return
      }
      if (Configuration.HELP || Configuration.PROBLEMFILE == "-h") { // FIXME: Hacky, redo argument reading
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
        val problem0 = Input.parseProblemFile(Configuration.PROBLEMFILE)
        // If it is a logic embedding, call the embedding tool
        val maybeLogicSpecification = problem0.find(_.role == "logic")
        val problem = if (maybeLogicSpecification.isDefined) {
          import transformation.{Wrappers => ModalProcessing}
          val spec = maybeLogicSpecification.get
          assert(spec.function_symbols.contains("$modal"), "Non-classical logics other than modal logic not supported yet.")
          Out.info("Input problem is modal. Running modal-to-HOL transformation ...")
          val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE))
          Input.parseProblem(result)
        } else {
          problem0
        }
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
    } finally {
      Configuration.cleanup()
      hook.remove() // When we reach this code we didnt face a SIGTERM etc. so remove the hook.
    }
  }
}
