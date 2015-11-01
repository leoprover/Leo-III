package leo.modules.seqpproc

import leo.Configuration
import leo.Out
import leo.datastructures.{Term, Clause, Literal, Role, Role_Conjecture, Role_NegConjecture, Role_Plain, Pretty, LitTrue, LitFalse}
import leo.modules.normalization._
import leo.modules.output._
import leo.modules.{SZSOutput, SZSException, Parsing}
import leo.modules.calculus.{Subsumption, CalculusRule}

import scala.collection.SortedSet

/**
 * Created by lex on 10/28/15.
 */
object SeqPProc extends Function0[Unit]{

  private final def termToClause(t: Term): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, true)))
  }

  final def preprocess(cur: ClauseWrapper): ClauseWrapper = {
    val cl = cur.cl
    // Fresh clause, that means its unit and nonequational
    assert(Clause.unit(cl), "clause not unit")
    val lit = cl.lits.head
    var pol = lit.polarity
    assert(!lit.equational, "initial literal equational")
    var cw = cur
    var left = lit.left

    Out.debug(s"Original: ${cur.cl.pretty}")

    if (PolaritySwitch.canApply(left)) {
      left = PolaritySwitch(left)
      pol = !pol
      cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(PolaritySwitch, Set(cw)))
      Out.debug(s"Pol. switch: ${cw.cl.pretty}")
    }
    // Def expansion and simplification
    left = DefExpSimp(left)
    cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(DefExpSimp, Set(cw)))
    Out.debug(s"Def expansion: ${cw.cl.pretty}")
    // NNF
    left = NegationNormal.normalize(left)
    cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(NegationNormal, Set(cw)))
    // Skolem
    left = Skolemization.normalize(left)
    cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(Skolemization, Set(cw)))
    Out.debug(s"Skolemize: ${cw.cl.pretty}")
    // Prenex
    left = PrenexNormal.normalize(left)
    cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(PrenexNormal, Set(cw)))
    Out.debug(s"Prenex: ${cw.cl.pretty}")
    // Remove quantifiers
    left = CNF_Forall(left, pol)
    cw = ClauseWrapper(Clause(Literal(left, pol)), InferredFrom(CNF_Forall, Set(cw)))
    Out.debug(s"CNF_Forall: ${cw.cl.pretty}")
    // To equation if possible
    if (LiftEq.canApply(left)) {
      cw = ClauseWrapper(Clause(LiftEq(left, pol)), InferredFrom(LiftEq, Set(cw)))
      Out.debug(s"to_eq: ${cw.cl.pretty}")
      Out.debug(s"orientable? ${cw.cl.lits.head.oriented}")
    }
    //TODO: Replace leibniz/andrew equalities
    Out.debug("####################")
    cw

    /*val left2 = DefExpSimp(left)
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
    Out.debug("####################")*/
  }


  final def simplify(cl: Clause): Clause = {
    Simplification.normalize(cl)
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
    var preprocessed: SortedSet[ClauseWrapper] = SortedSet()
    val inputIt = effectiveInput.iterator
    while (inputIt.hasNext) {
      val cur = inputIt.next()
      preprocessed = preprocessed + preprocess(cur)
    }
    // initialize sets
    var unprocessed: SortedSet[ClauseWrapper] = preprocessed
    var processed: Set[ClauseWrapper] = Set()
    var returnSZS: StatusSZS = SZS_Unknown
    var loop = true
    // proof loop
    while (loop) {
      if (unprocessed.isEmpty) {
        loop = false
        returnSZS = SZS_Satisfiable
      } else {
        val cur = unprocessed.head
        unprocessed = unprocessed.tail
        Out.output(s"Taken: ${cur.pretty}")
        val simpl = simplify(cur.cl)
        if (Clause.empty(simpl)) {
          loop = false
          returnSZS = SZS_Theorem
        } else {
          // Subsumption
          if (!processed.exists(cw => Subsumption.subsumes(cw.cl, simpl))) {
            val nu = ClauseWrapper(cur.id, simpl, cur.role, cur.annotation) // Simpl annotation?
            processed = processed.filterNot(cw => Subsumption.subsumes(simpl, cw.cl)) + nu

            // Extensionality
            val (cA_funcExt, fE, fE_other) = FuncExt.canApply(simpl)
            if (cA_funcExt) {
              ClauseWrapper(Clause(FuncExt(fE,???) ++ fE_other), InferredFrom(FuncExt, Set(nu)))
            }

          } else {
            Out.debug("clause subsumbed, skipping.")
          }

        }
      }

    }

    Out.output(SZSOutput(returnSZS, Configuration.PROBLEMFILE))
  }


}

protected[seqpproc] abstract sealed class WrapperAnnotation extends Pretty
case class InferredFrom(rule: leo.modules.calculus.CalculusRule, cws: Set[ClauseWrapper]) extends WrapperAnnotation {
  def pretty: String = s"inference(${rule.name},[${rule.inferenceStatus.fold("")("status("+_.pretty.toLowerCase+")")}],[${cws.map(_.id).mkString(",")}])"
}
case object NoAnnotation extends WrapperAnnotation {val pretty: String = ""}
case class FromFile(fileName: String, formulaName: String) extends WrapperAnnotation { def pretty = s"file('$fileName',$formulaName)" }

protected[seqpproc] case class ClauseWrapper(id: String, cl: Clause, role: Role, annotation: WrapperAnnotation) extends Ordered[ClauseWrapper] with Pretty {
  override def equals(o: Any): Boolean = o match {
    case cw: ClauseWrapper => cw.cl == cl && cw.role == role
    case _ => false
  }
  override def hashCode(): Int = cl.hashCode() ^ role.hashCode()

  def compare(that: ClauseWrapper) = Configuration.CLAUSE_ORDERING.compare(this.cl, that.cl) // FIXME mustmatch withequals and hash

  def pretty: String = s"[$id] ${role.pretty}:\t${cl.pretty}"
}

protected[seqpproc] object ClauseWrapper {
  private var counter: Int = 0
  def apply(cl: Clause, r: Role, annotation: WrapperAnnotation): ClauseWrapper = {
    counter += 1
    ClauseWrapper(s"gen_formula_$counter", cl, r, annotation)
  }
  def apply(cl: Clause, r: Role): ClauseWrapper = apply(cl, r, NoAnnotation)
  def apply(cl: Clause): ClauseWrapper =apply(cl, Role_Plain, NoAnnotation)
  def apply(cl: Clause, annotation: WrapperAnnotation): ClauseWrapper = apply(cl, Role_Plain, annotation)
}

