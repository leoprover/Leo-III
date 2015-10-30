package leo.modules.seqpproc

import leo.Configuration
import leo.Out
import leo.datastructures.{Term, Clause, Literal, Role, Role_Conjecture, Role_NegConjecture, Role_Plain, Pretty, LitTrue, LitFalse}
import leo.modules.normalization._
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
      val cl = cur.cl
      // Fresh clause, that means its unit and nonequational
      assert(Clause.unit(cl), "clause not unit")
      val lit = cl.lits.head
      assert(!lit.equational, "initial literal equational")
      val left = lit.left

      Out.debug(s"Original: ${cur.cl.pretty}")
      // Def expansion and simplification
      val left2 = DefExpSimp(left)
      val cl2 = ClauseWrapper(Clause(Literal(left2, lit.polarity)), InferredFrom(DefExpSimp, Set(cur)))
      Out.debug(s"Def expansion: ${cl2.cl.pretty}")
      // NNF
      val left3 = NegationNormal.normalize(left2)
      val cl3 = ClauseWrapper(Clause(Literal(left3, lit.polarity)), InferredFrom(NegationNormal, Set(cl2)))
      Out.debug(s"NNF: ${cl3.cl.pretty}")
      // Skolem
      val left4 = Skolemization.normalize(left3)
      val cl4 = ClauseWrapper(Clause(Literal(left4, lit.polarity)), InferredFrom(Skolemization, Set(cl3)))
      Out.debug(s"Skolemize: ${cl4.cl.pretty}")
      // Prenex
      val left5 = PrenexNormal.normalize(left4)
      val cl5 = ClauseWrapper(Clause(Literal(left5, lit.polarity)), InferredFrom(PrenexNormal, Set(cl4)))
      Out.debug(s"Prenex: ${cl5.cl.pretty}")
      // Remove quantifiers
      val left6 = CNF_Forall(left5, lit.polarity)
      val cl6 = ClauseWrapper(Clause(Literal(left6, lit.polarity)), InferredFrom(CNF_Forall, Set(cl5)))
      Out.debug(s"CNF_Forall: ${cl6.cl.pretty}")
      // To equation if possible
      if (LiftEq.canApply(left6)) {
        assert(cl6.cl.lits.head.polarity, "Polarity not $true")
        val eqLit = LiftEq(left6, lit.polarity, true)
        val cl7 = ClauseWrapper(Clause(eqLit), InferredFrom(LiftEq, Set(cl6)))
        preprocessed = preprocessed + cl7
      } else {
        preprocessed = preprocessed + cl6
      }
      Out.debug("####################")
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

protected[seqpproc] object ClauseWrapper {
  private var counter: Int = 0
  def apply(cl: Clause, r: Role, annotation: WrapperAnnotation): ClauseWrapper = {
    counter += 1
    ClauseWrapper(s"gen_formula_$counter}", cl, r, annotation)
  }
  def apply(cl: Clause, r: Role): ClauseWrapper = apply(cl, r, NoAnnotation)
  def apply(cl: Clause): ClauseWrapper =apply(cl, Role_Plain, NoAnnotation)
  def apply(cl: Clause, annotation: WrapperAnnotation): ClauseWrapper = apply(cl, Role_Plain, annotation)
}
