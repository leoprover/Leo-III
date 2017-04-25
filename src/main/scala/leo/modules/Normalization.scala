package leo.modules

import leo.Configuration
import leo.datastructures.ClauseAnnotation.{FromFile, NoAnnotation}
import leo.datastructures._
import leo.modules.Utility.termToClause
import leo.modules.output.SZS_UsageError
import leo.modules.parsers.Input

/**
  * Created by lex on 4/25/17.
  */
object Normalization {
  final def apply(): Unit = {
    implicit val sig: Signature = Signature.freshWithHOL()
    val input0 = Input.parseProblem(Configuration.PROBLEMFILE)
    val (input,conjecture) = effectiveInput(input0)
    val inputIt = input.iterator

    var resultClauses: Set[AnnotatedClause] = Set.empty

    while (inputIt.hasNext) {
      val formula = inputIt.next()
      resultClauses = resultClauses union process(formula)(sig)
    }
    val newConjecture: AnnotatedClause = if (conjecture != null) {
      val localResult = process(conjecture)(sig)
      assert(localResult.count(_.role == Role_Conjecture) == 1)
      val (conjClause, otherClauses) = localResult.partition(_.role == Role_Conjecture)
      println(otherClauses)
      println(conjClause)
      assert(conjClause.size == 1)
      resultClauses = resultClauses union otherClauses
      conjClause.head
    } else null

    val newProb = if (newConjecture == null) leo.modules.external.createTHFProblem(resultClauses.map(_.cl))
    else leo.modules.external.createTHFProblem(resultClauses.map(_.cl), newConjecture.cl)

    println(newProb)
  }

  private final def process(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
    // Miniscope

    // Argument extraction

    // formula renaming

    // CNF

    // Defined equality removing
    Set(cl)
  }

  private final def effectiveInput(input: Seq[tptp.Commons.AnnotatedFormula])
                                  (implicit sig: Signature): (Seq[AnnotatedClause], AnnotatedClause) = {
    var result: Seq[AnnotatedClause] = Vector.empty
    var conj: AnnotatedClause = null
    val inputIt = input.iterator
    while (inputIt.hasNext) {
      val formula = inputIt.next()
      formula.role match {
        case Role_Type.pretty => Input.processFormula(formula)(sig)
        case Role_Definition.pretty => Input.processFormula(formula)(sig)
        case Role_Conjecture.pretty =>
          if (conj == null) {
            val erg = Input.processFormula(formula)(sig)
            val cl = AnnotatedClause(termToClause(erg._2), erg._3, NoAnnotation, ClauseAnnotation.PropNoProp)
            conj = cl
          } else throw new SZSException(SZS_UsageError, "Only one conjecture allowed per problem.")
        case _ =>
          val erg = Input.processFormula(formula)(sig)
          val cl = AnnotatedClause(termToClause(erg._2), erg._3, NoAnnotation, ClauseAnnotation.PropNoProp)
          result = result :+ cl
      }
    }
    (result, conj)
  }
}
