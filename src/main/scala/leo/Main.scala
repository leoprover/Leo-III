package leo

import leo.datastructures.{Role_Definition, Role_Type}
import leo.modules._
import leo.modules.output._
import leo.modules.parsers.{CLParameterParser, Input}
import leo.modules.prover.RunExternalProver


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
          val result = ModalProcessing.convertModalToString(java.nio.file.Paths.get(Configuration.PROBLEMFILE))
          Input.parseProblem(result)
        } else {
          problem0
        }
        // Functionality calls
        if (Configuration.isSet("seq")) {
          Out.info("Running in sequential loop mode.")
          prover.SeqLoop(beginTime, Configuration.TIMEOUT, problem)
        } else if (Configuration.isSet("scheduled")) {
          Out.info("Running in scheduled sequential loop mode.")
          prover.ScheduledRun(beginTime, Configuration.TIMEOUT, problem)
        } else if (Configuration.isSet("pure-ext")) {
          Out.info("Running in purely external mode.")
          RunExternalProver.apply(problem)
        } else if (Configuration.isSet("rules")) {
          Out.info("Running in rules mode.")
          ParallelMain.agentRuleRun(beginTime) // TODO
        } else if (Configuration.isSet("par")) {
          Out.info("Running in parallel mode.")
          ParallelMain.runParallel(beginTime) // TODO
        } else if (Configuration.isSet("scheduled-par")) {
          Out.info("Running in scheduled parallel mode.")
          ParallelMain.runMultiSearch(beginTime) // TODO
        } else if (Configuration.isSet("processOnly")) {
          Out.info("Running in processOnly mode.")
          Normalization(problem)
        } else if (Configuration.isSet("syntaxcheck")) {
          // if it fails the catch below will print it
          Out.output(SZSOutput(SZS_Success, Configuration.PROBLEMFILE,
            s"Syntax check succeeded"))
        } else if (Configuration.isSet("typecheck")) { // TODO: Refactor
          import leo.datastructures.{Term, Signature, Role}
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
        } else if (Configuration.isSet("toTHF")) { // TODO: Refactor
          import leo.datastructures.Signature
          import leo.modules.output.ToTPTP
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
          prover.SeqLoop(beginTime, Configuration.TIMEOUT, problem)
        }
      }
      /** Call concrete functionality END */
      
    } catch {
      case e:Throwable =>
        Out.comment("OUT OF CHEESE ERROR +++ MELON MELON MELON +++ REDO FROM START")
        e match {
          case e0: SZSException =>
            Out.output(SZSOutput(e0.status, Configuration.PROBLEMFILE,e0.getMessage))
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
