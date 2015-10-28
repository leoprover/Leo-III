package leo.modules.seqpproc

import leo.Configuration
import leo.datastructures.{Term, Clause, Literal, Role, Role_Conjecture, Role_NegConjecture, Pretty}
import leo.modules.output.{SZS_CounterSatisfiable, SZS_InputError}
import leo.modules.{SZSException, Parsing}
import leo.modules.calculus.CalculusRule
/**
 * Created by lex on 10/28/15.
 */
object SeqPProc extends Function0[Unit]{

  private final def termToClause(t: Term): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, true)))
  }

  final def apply(): Unit = {

    // Read problem
    val input = Parsing.parseProblem(Configuration.PROBLEMFILE)
    // Negate conjecture
    val conjecture = input.filter {case (id, term, role) => role == Role_Conjecture}
    if (conjecture.size > 1) throw new SZSException(SZS_InputError, "Only one conjecture per input problem permitted.")
    assert(conjecture.size == 1)

    val conj = conjecture.head
    val conjWrapper = ClauseWrapper(conj._1, Clause.mkClause(Seq(Literal.mkLit(conj._2, true))), conj._3, NoAnnotation)
    val rest = input.filterNot(_._1 == conjecture.head._1)
    val effectiveInput: Seq[ClauseWrapper] = {
      rest.map { case (id, term, role) => ClauseWrapper(id, termToClause(term), role, FromFile(Configuration.PROBLEMFILE, id)) } :+ ClauseWrapper(conj._1 + "_neg", Clause.mkClause(Seq(Literal.mkLit(conj._2, false))), Role_NegConjecture, InferredFrom(new CalculusRule {
        override def name: String = "neg_conjecture"
        override val inferenceStatus = Some(SZS_CounterSatisfiable)
      }, Set(conjWrapper)))
    }
    // Proprocess terms with standard normalization techniques for terms (non-equational)
    // transform into equational literals if possible
    var preprocessed: Set[ClauseWrapper] = Set()
    val inputIt = effectiveInput.iterator
    while (inputIt.hasNext) {
      val cur = inputIt.next()
      // Def expansion
      // Simplification
      // NNF
      // Skolem
      // Prenex
      // Remove quantifiers
      // To equation if possible
      ???
    }
    // initialize sets
    var unprocessed: Set[ClauseWrapper] = preprocessed
    var processed: Set[ClauseWrapper] = Set()


    // proof loop
    while (true) {
      ???
    }
  }


}

protected[seqpproc] abstract sealed class WrapperAnnotation extends Pretty
case class InferredFrom(rule: leo.modules.calculus.CalculusRule, cws: Set[ClauseWrapper]) extends WrapperAnnotation {
  def pretty: String = s"inference(${rule.name},[${rule.inferenceStatus.fold("")("status("+_.pretty.toLowerCase+")")}],[${cws.map(_.id).mkString(",")}])"
}
case object NoAnnotation extends WrapperAnnotation {val pretty: String = ""}
case class FromFile(fileName: String, formulaName: String) extends WrapperAnnotation { def pretty = s"file('$fileName',$formulaName)" }

protected[seqpproc] case class ClauseWrapper(id: String, cl: Clause, role: Role, annotation: WrapperAnnotation) {
  override def equals(o: Any): Boolean = o match {
    case cw: ClauseWrapper => cw.cl == cl && cw.role == role
    case _ => false
  }
}
