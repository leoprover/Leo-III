package leo

import leo.modules.Modes
import leo.modules.{SZSException, SZSResult, stackTraceAsString}
import leo.modules.output.{SZS_Error, SZS_Forced, SZS_MemoryOut, SZS_UsageError}
import leo.modules.input.{CLParameterParser, Input}

/**
  * Entry Point for Leo-III as an executable.
  *
  * @see [[leo.modules.Modes]] for different modes Leo-III can be run with.
  * @author Max Wisniewski, Alexander Steen
  * @since 7/8/14
 */
object Main {
  private[this] var hook: scala.sys.ShutdownHookThread = _
  final private[this] val MSG_LEO3_STOPPED: String = "Leo-III stopped externally."
  final private[this] val MSG_LEO3_GENERAL_ERROR: String = "OUT OF CHEESE ERROR +++ MELON MELON MELON +++ REDO FROM START"

  final def main(args: Array[String]): Unit = {
    try {
      val beginTime = System.currentTimeMillis()
      /** Hook is for returning an szs status if leo3 is killed forcefully. */
      hook = sys.addShutdownHook({
        Configuration.cleanup()
        Out.output(SZSResult(SZS_Forced, Configuration.PROBLEMFILE, MSG_LEO3_STOPPED))
      })

      /** Parameter stuff BEGIN */
      // TODO: Hacky parameter stuff, redo argument processing
      Configuration.init(new CLParameterParser(args))
      if (Configuration.PROBLEMFILE == "--caps") {
        println(Configuration.CAPS)
        return
      }
      if (Configuration.PROBLEMFILE == s"--${Configuration.PARAM_VERSION}") {
        println(s"Leo-III ${Configuration.VERSION}")
        return
      }
      if (Configuration.HELP ||
        Configuration.PROBLEMFILE == s"-${Configuration.PARAM_HELP}" ||
        Configuration.PROBLEMFILE == s"--${Configuration.PARAM_USAGE}") {
        Configuration.help()
        return
      }
      /** Parameter stuff END */

      /** Call concrete functionality BEGIN */
      import leo.modules.embeddings.{Library => LogicEmbeddingLibrary, getLogicSpecFromProblem, getLogicFromSpec}
      Out.info(s"Parsing problem ${Configuration.PROBLEMFILE} ...")
      val parseStartTime = System.currentTimeMillis()
      val problem0 = Input.parseProblemFile(Configuration.PROBLEMFILE)
      val parseEndTime = System.currentTimeMillis()
      Out.info(s"Parsing done (${parseEndTime - parseStartTime}ms).")
      // If it is a logic embedding, call the embedding tool, else just use the problem itself.
      // To Leo-III it will currently look like a standard HOL problem, regardless of its original object logic.
      val maybeLogicSpec = getLogicSpecFromProblem(problem0)
      val problem = maybeLogicSpec match {
        case Some(logicSpec) =>
          val logicName = getLogicFromSpec(logicSpec)
          val embeddingFunction = LogicEmbeddingLibrary.embeddingTable(logicName)
          Out.info("Input problem is non-classical. Running HOL transformation from semantics specification contained in the problem file ...")
          embeddingFunction.apply(problem0, Set.empty)
        case None => problem0
      }
      // Invoke concrete mode
      Modes.apply(beginTime, problem)
      /** Call concrete functionality END */
      
    } catch {
      /** Handle all top-level errors BEGIN */
      case e:Throwable =>
        Out.comment(MSG_LEO3_GENERAL_ERROR)
        e match {
          case e0: SZSException =>
            e0.status match {
              case SZS_UsageError =>
                // Happens only if no problem file was given. Syntax/semantics/input errors are
                // handled separately
                Out.output(SZSResult(e0.status, "<unknown>" ,e0.getMessage))
                println()
                Configuration.help()
              case s =>
                Out.output(SZSResult(s, Configuration.PROBLEMFILE,e0.getMessage))
                Out.debug(e0.debugMessage)
            }
          case e0: OutOfMemoryError =>
            Out.output(SZSResult(SZS_MemoryOut, Configuration.PROBLEMFILE, e0.toString))
          case _ => Out.output(SZSResult(SZS_Error, Configuration.PROBLEMFILE,e.toString))
        }
        Out.trace(stackTraceAsString(e))
        if (e.getCause != null) {
          Out.trace("Caused by: " + e.getCause.getMessage)
          Out.trace("at: " + e.getCause.getStackTrace.mkString("Array(", ", ", ")"))
        }
      /** Handle all top-level errors END */
    } finally {
      Configuration.cleanup()
      hook.remove() // When we reach this code we didn't face a SIGTERM etc. so remove the hook.
    }
  }
}
