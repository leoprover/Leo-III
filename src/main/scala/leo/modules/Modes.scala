package leo.modules

import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.datastructures.{Role_Definition, Role_Type, Signature}
import leo.{Configuration, Out}
import leo.modules.output._
import leo.modules.parsers.Input

object Modes {

  final def toTHF(parsedProblem: scala.Seq[AnnotatedFormula]): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val sb: StringBuilder = new StringBuilder
    val erg = Input.processProblem(parsedProblem)
    sb.append(ToTPTP(sig))
    sb.append(ToTPTP.printDefinitions(sig))
    erg.foreach {case (id, t, role) =>
      if (role != Role_Definition && role != Role_Type) {
        sb.append(ToTPTP.toTPTP(id, termToClause(t), role)(sig))
        sb.append("\n")
      }
    }
    Out.output(SZSResult(SZS_Success, Configuration.PROBLEMFILE, s"Translation finished."))
    Out.output(SZSOutput(SZS_ListOfTHF, Configuration.PROBLEMFILE, sb.toString()))
  }

  final def syntaxCheck(parsedProblem: scala.Seq[AnnotatedFormula]): Unit = {
    // if it fails the catch in Main will print the error
    Out.output(SZSResult(SZS_Success, Configuration.PROBLEMFILE, s"Syntax check succeeded"))
  }

  final def typeCheck(parsedProblem: scala.Seq[AnnotatedFormula]): Unit = {
    import leo.datastructures.{Term, Role}
    implicit val sig: Signature = Signature.freshWithHOL()
    val erg = Input.processProblem(parsedProblem)
    var notWellTyped: Seq[(Input.FormulaId, Term, Role)] = Seq.empty
    erg.foreach {case (id, t, role) =>
      val wellTyped = Term.wellTyped(t)
      if (!wellTyped) notWellTyped = notWellTyped :+ (id, t, role)
    }
    if (notWellTyped.isEmpty)
      Out.output(SZSResult(SZS_Success, Configuration.PROBLEMFILE, "Type check succeeded"))
    else {
      Out.output(SZSResult(SZS_TypeError, Configuration.PROBLEMFILE, "Problem is not well-typed"))
      Out.output(SZSOutput(SZS_LogicalData, Configuration.PROBLEMFILE, s"Formulae ${notWellTyped.map(_._1).mkString(", ")} are not well-typed."))
    }
  }

  final def runExternalProver(parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in purely external mode.")
    modes.RunExternalProver(parsedProblem)
  }

  final def normalizationOnly(parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in processOnly mode.")
    modes.Normalization(parsedProblem)
  }

  final def seqLoop(startTime: Long, timeout: Int, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in sequential loop mode.")
    prover.SeqLoop(startTime, timeout, parsedProblem)
  }

  final def scheduledSeq(startTime: Long, timeout: Int, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in scheduled sequential loop mode.")
    modes.ScheduledRun(startTime, timeout, parsedProblem)
  }

  final def agentRuleRun(startTime: Long, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in rules mode.")
    modes.Parallel.agentRuleRun(startTime, parsedProblem)
  }

  final def runParallel(startTime: Long, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in parallel mode.")
    modes.Parallel.runParallel(startTime, parsedProblem)
  }

  final def runMultiSearch(startTime: Long, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in scheduled parallel mode.")
    modes.Parallel.runMultiSearch(startTime, parsedProblem)
  }

}
