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
          import java.util.logging
          val spec = maybeLogicSpecification.get
          assert(spec.function_symbols.contains("$modal"), "Non-classical logics other than modal logic not supported yet.")
          Out.info("Input problem is modal. Running modal-to-HOL transformation from semantics specification contained in the problem file ...")
          logging.Logger.getLogger("default").setLevel(logging.Level.WARNING)
          val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE))
          Input.parseProblem(result)
        } else {
          val symbolsInProblem = problem0.flatMap(_.function_symbols).toSet
          val boxSymbol = "$box"; val diamondSymbol = "$dia"
          if (symbolsInProblem.contains(boxSymbol) || symbolsInProblem.contains(diamondSymbol)) {
            import transformation.{Wrappers => ModalProcessing, SemanticsGenerator => ModalSemantics}
            import java.util.logging
            Out.info("Input problem is modal. Running modal-to-HOL transformation from externally provided semantics specification ...")
            if (!Configuration.isSet(Configuration.PARAM_MODAL_SYSTEM)) Out.info(s"No modal system specified. Using default: ${Configuration.DEFAULT_MODALSYSTEM}.")
            if (!Configuration.isSet(Configuration.PARAM_MODAL_DOMAIN)) Out.info(s"No modal system specified. Using default: ${Configuration.DEFAULT_MODALDOMAIN}.")
            if (!Configuration.isSet(Configuration.PARAM_MODAL_RIGIDITY)) Out.info(s"No modal system specified. Using default: ${Configuration.DEFAULT_MODALRIGIDITY}.")
            if (!Configuration.isSet(Configuration.PARAM_MODAL_CONSEQUENCE)) Out.info(s"No modal system specified. Using default: ${Configuration.DEFAULT_MODALCONSEQUENCE}.")
            logging.Logger.getLogger("default").setLevel(logging.Level.WARNING)
            val modalSystem = ModalSemantics.systemCommonNameToInt(Configuration.MODAL_SYSTEM)
            val modalDomain = ModalSemantics.domainCommonNameToInt(Configuration.MODAL_DOMAIN)
            val modalRigidity = ModalSemantics.rigidityCommonNameToInt(Configuration.MODAL_RIGIDITY)
            val modalConsequence = ModalSemantics.consequenceCommonNameToInt(Configuration.MODAL_CONSEQUENCE)
            val semanticsSpecification = ModalSemantics.semanticsToTPTPSpecification(modalSystem, modalDomain, modalRigidity, modalConsequence)
            val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE),
              null, null, null, semanticsSpecification)
            Input.parseProblem(result)
          } else problem0
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
