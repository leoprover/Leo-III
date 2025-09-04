package leo.modules.external

import leo.datastructures.Clause.effectivelyEmpty
import leo.datastructures.{AnnotatedClause, Clause, ClauseAnnotation, Pretty, Signature, TPTP}
import leo.modules.termToClause
import leo.modules.input.Input
import leo.modules.output.{SZS_Success, SuccessSZS, ToTHF, unescapeTPTPName}


final case class ExternalProof(derivation: Seq[AnnotatedClause], sig: Signature) extends Pretty {
  private[this] lazy val bottomClause: Option[AnnotatedClause] = derivation.find { cl =>
    effectivelyEmpty(cl.cl) || Clause.asTerm(cl.cl) == leo.modules.HOLSignature.LitFalse()
  }
  private[this] lazy val leafClauses: Seq[AnnotatedClause] = {
    derivation.filter { cl =>
      cl.annotation match {
        case ClauseAnnotation.FromFile(_, _) => true
        case _ => false
      }
    }
  }

  def leaves: Seq[AnnotatedClause] = leafClauses
  def bottom: Option[AnnotatedClause] = bottomClause

  override def pretty: String = derivation.map(ToTHF.withAnnotation(_)(sig)).mkString("\n")
}
object ExternalProof {
  final def fromTSTPLines(lines: Seq[String]): ExternalProof = {
    println(s"lines: ${lines.mkString("\n")}")
    var nameToClause: Map[String, AnnotatedClause] = Map.empty
    val sig: Signature = Signature.freshWithHOL()
    val linesAsTPTP = Input.parseProblem(lines.mkString(""))(stats = None)
    val annotatedClauses = linesAsTPTP.map { annotated =>
      val (name, term, role) = Input.processFormula(annotated)(sig)
//            println(s"name=$name,term=${term.pretty(sig)},role=$role,annotation=${annotated.annotations}")
      val tptpAnnotation: ClauseAnnotation = convertTPTPAnnotation(annotated.annotations, nameToClause)
      val r = AnnotatedClause.apply(termToClause(term), role, tptpAnnotation, ClauseAnnotation.PropNoProp)
      nameToClause = nameToClause + (name -> r)
      println(ToTHF.withAnnotation(r)(sig))
      r
    }

    new ExternalProof(annotatedClauses, sig)
  }
  private[this] final def convertTPTPAnnotation(annotation: TPTP.Annotations, clauseMap: Map[String, AnnotatedClause]): ClauseAnnotation = {
    annotation match {
      case Some((step, _)) => step.data match {
        case Seq(TPTP.MetaFunctionData("file",
          Seq(
            TPTP.GeneralTerm(Seq(TPTP.MetaFunctionData(filename, Seq())), _),
            TPTP.GeneralTerm(Seq(TPTP.MetaFunctionData(axName, Seq())), _))
          )) =>
          ClauseAnnotation.FromFile(unescapeTPTPName(filename), axName)
        case Seq(TPTP.MetaFunctionData("inference",
              Seq(
              TPTP.GeneralTerm(Seq(TPTP.MetaFunctionData(inferenceRule, Seq())), None),
              TPTP.GeneralTerm(Seq(), Some(Seq(TPTP.GeneralTerm(Seq(TPTP.MetaFunctionData("status", Seq(TPTP.GeneralTerm(Seq(TPTP.MetaFunctionData(status, Seq())), None)))), None)))),
              TPTP.GeneralTerm(Seq(), Some(parents))
              ))) =>
          val szsStatus = SuccessSZS.apply(status) match {
            case Some(value) => value
            case None => SZS_Success // As default
          }
          val convertedParents = parents.flatMap { p =>
            p.data match {
              case Seq(TPTP.MetaFunctionData(parentName, Seq())) if clauseMap.contains(parentName) =>
                Some(clauseMap(parentName))
              case _ => None
            }
          }
          if (convertedParents.size != parents.size) {
            leo.Out.warn(s"[ExternalProof] Backward translation of annotation '$annotation' external proof step failed; results may be incomplete.")
          }
          ClauseAnnotation.InferredFrom.apply(extInference(inferenceRule, szsStatus), convertedParents)

        case _ => ClauseAnnotation.NoAnnotation
      }
      case None => ClauseAnnotation.NoAnnotation
    }
  }
  final def extInference(name: String, inferenceStatus: SuccessSZS): leo.modules.calculus.CalculusRule = new ExternalInference(name, inferenceStatus)
  private final class ExternalInference(inferenceName: String, status: SuccessSZS) extends leo.modules.calculus.CalculusRule {
    override def name: String = s"external($inferenceName)"
    override def inferenceStatus: SuccessSZS = status
  }
}

