package leo.modules

import leo.datastructures.TPTP.AnnotatedFormula
import leo.datastructures.{Role_Definition, Role_Type, Signature}
import leo.{Configuration, Out}
import leo.modules.output._
import leo.modules.input.{Input, ProblemStatistics, TPTPParser}

object Modes {
  final def apply(beginTime: Long, parsedProblem: Seq[AnnotatedFormula], stats: ProblemStatistics): Unit = {
    val timeout = Configuration.TIMEOUT
    if (Configuration.isSet("seq")) {
      seqLoop(beginTime, timeout, parsedProblem, stats)
    } else if (Configuration.isSet("scheduled")) {
      scheduledSeq(beginTime, timeout, parsedProblem)
    } else if (Configuration.isSet("processOnly")) {
      normalizationOnly(parsedProblem)
    } else if (Configuration.isSet("syntaxcheck")) {
      syntaxCheck(parsedProblem)
    } else if (Configuration.isSet("typecheck")) {
      typeCheck(parsedProblem)
    } else if (Configuration.isSet("toTHF")) {
      toTHF(parsedProblem)
    } else {
      seqLoop(beginTime, timeout, parsedProblem, stats)
    }
  }

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
    var illTypedFormulas: Seq[AnnotatedFormula] = Seq.empty
    parsedProblem.foreach { af =>
      val processed = Input.processFormula(af)
      if (!Term.wellTyped(processed._2))
        illTypedFormulas = illTypedFormulas :+ af
    }
    if (illTypedFormulas.isEmpty)
      Out.output(SZSResult(SZS_Success, Configuration.PROBLEMFILE, "Type check succeeded"))
    else {
      Out.output(SZSResult(SZS_TypeError, Configuration.PROBLEMFILE, "Problem is not well-typed"))
      val responseText = illTypedFormulas.map { af =>
        val name = af.name
        val origin = if (af.meta.isDefinedAt(TPTPParser.META_ORIGIN)) {
          val origin0 = af.meta(TPTPParser.META_ORIGIN).asInstanceOf[TPTPParser.META_ORIGIN_TYPE]
          s"${origin0._1}:${origin0._2}"
        } else "<unknown origin>"
        s"Illtyped formula $name in line $origin."
      }
      Out.output(SZSOutput(SZS_LogicalData, Configuration.PROBLEMFILE, responseText.mkString("\n")))
    }
  }

  final def normalizationOnly(parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in processOnly mode.")
    modes.Normalization(parsedProblem)
  }

  final def seqLoop(startTime: Long, timeout: Int, parsedProblem: Seq[AnnotatedFormula], stats: ProblemStatistics): Unit = {
    Out.info("Running in sequential loop mode.")
    prover.SeqLoop(startTime, timeout, parsedProblem, stats)
  }

  final def scheduledSeq(startTime: Long, timeout: Int, parsedProblem: Seq[AnnotatedFormula]): Unit = {
    Out.info("Running in scheduled sequential loop mode.")
    modes.ScheduledRun(startTime, timeout, parsedProblem)
  }

}
