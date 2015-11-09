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

  final def preprocess(cur: ClauseWrapper): Set[ClauseWrapper] = {
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
    // Exhaustively CNF
    val left2 = CNF(leo.modules.calculus.freshVarGen(cw.cl), cw.cl).map(Simp.shallowSimp).map(ClauseWrapper(_, InferredFrom(CNF, Set(cw)))).toSet
    Out.debug(s"CNF:\n\t${left2.map(_.cl.pretty).mkString("\n\t")}")
    left2.map { c =>
      // To equation if possible
      val (cA_lift, lift, lift_other) = LiftEq.canApply(c.cl)
      if (cA_lift) {
        Out.debug(s"to_eq: ${c.cl.pretty}")
        val curr = Clause(LiftEq(lift, lift_other))
        ClauseWrapper(curr, InferredFrom(LiftEq, Set(c)))

      } else c
    }
    //TODO: Replace leibniz/andrew equalities
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
      val processed = preprocess(cur)
      preprocessed = preprocessed ++ processed.filterNot(cw => Clause.trivial(cw.cl))
      Out.debug("####################")
    }
    Out.debug("####################")
    Out.debug("####################")
    Out.debug("####################")
    // initialize sets
    var unprocessed: SortedSet[ClauseWrapper] = preprocessed
    var processed: Set[ClauseWrapper] = Set()
    var returnSZS: StatusSZS = SZS_Unknown
    var loop = true
    // proof loop
    while (loop) {
      if (unprocessed.isEmpty) {
        loop = false
        returnSZS = SZS_CounterSatisfiable
      } else {
        val cur = unprocessed.head
        unprocessed = unprocessed.tail
        Out.debug(s"Taken: ${cur.pretty}")
        // TODO: simpl to be simplification by rewriting à la E
        val simpl = cur.cl
//        val simpl = simplify(cur.cl)
        if (Clause.empty(simpl)) {
          loop = false
          returnSZS = SZS_Theorem
        } else {
          // Subsumption
          if (!processed.exists(cw => Subsumption.subsumes(cw.cl, simpl))) {
            var curr = simpl
            var curr_cw = ClauseWrapper(cur.id, curr, cur.role, cur.annotation) // Simpl annotation?


            var newclauses: Set[ClauseWrapper] = Set()

            // Extensionality
            val (cA_funcExt, fE, fE_other) = FuncExt.canApply(curr)
            if (cA_funcExt) {
              Out.debug(s"Func Ext on: ${curr_cw.pretty}")
              val funcExt_cw = ClauseWrapper(Clause(FuncExt(leo.modules.calculus.freshVarGen(simpl),fE) ++ fE_other), InferredFrom(FuncExt, Set(curr_cw)))
              Out.trace(s"Func Ext result: ${funcExt_cw.pretty}")
              newclauses = newclauses + funcExt_cw
              // Break here
            } else {
              val (cA_boolExt, bE, bE_other) = BoolExt.canApply(curr)
              if (cA_boolExt) {
                Out.debug(s"Bool Ext on: ${curr_cw.pretty}")
                val boolExt_cws = BoolExt.apply(bE, bE_other).map(ClauseWrapper(_, InferredFrom(BoolExt, Set(curr_cw))))
                Out.trace(s"Bool Ext result:\n\t${boolExt_cws.map(_.pretty).mkString("\n\t")}")
                newclauses = newclauses union boolExt_cws
                // Break here
              } else {
                processed = processed.filterNot(cw => Subsumption.subsumes(curr, cw.cl)) + curr_cw
                // To equality if possible
                val (cA_lift, lift, lift_other) = LiftEq.canApply(curr)
                if (cA_lift) {
                  curr = Clause(LiftEq(lift, lift_other))
                  curr_cw = ClauseWrapper(curr, InferredFrom(LiftEq, Set(curr_cw)))
                  // No break here
                }
                /* create new claues from curr and processed from here */
                // All paramodulations
                val procIt = processed.iterator
                while (procIt.hasNext) {
                  val procCl = procIt.next()
                  Out.debug(s"Paramod on ${curr_cw.id} and ${procCl.id}")
                  val paramodres = if (curr_cw.id == procCl.id)
                    OrderedParamod(curr, procCl.cl).map(cl => ClauseWrapper(cl, InferredFrom(OrderedParamod, Set(curr_cw, procCl))))
                  else {
                    OrderedParamod(curr, procCl.cl).map(cl => ClauseWrapper(cl, InferredFrom(OrderedParamod, Set(curr_cw, procCl)))) ++
                    OrderedParamod(procCl.cl, curr).map(cl => ClauseWrapper(cl, InferredFrom(OrderedParamod, Set(curr_cw, procCl))))}

                  newclauses = newclauses ++ paramodres
                  Out.debug(s"Paramod result:\n\t${paramodres.map(_.pretty).mkString("\n\t")}")
                }
                // Equality factoring
                // ....
                // Prim subst
                val (cA_ps, ps_vars) = StdPrimSubst.canApply(curr)
                if (cA_ps) {
                  Out.debug(s"Prim subst on: ${curr_cw.pretty}")
                  val new_ps_pre = StdPrimSubst(curr, ps_vars)
                  val new_ps = new_ps_pre.map{case (cl,subst) => ClauseWrapper(cl, InferredFrom(StdPrimSubst, Set((curr_cw,ToTPTP(subst)))))}
                  // FIXME: Additional binding information does not get updates when FVs are beeing renamed
                  Out.trace(s"Prim subst result:\n\t${new_ps.map(_.pretty).mkString("\n\t")}")
                  newclauses = newclauses union new_ps
                }
                /* work on new claues from here */
                // Simplify new clauses
                newclauses = newclauses.map(cw => ClauseWrapper(Simp(cw.cl), InferredFrom(Simp, Set(cw))))
                // Remove those which are tautologies
                newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
                // CNF new clauses
                newclauses = newclauses.flatMap(cw => {Out.finest(s"#####################\ncnf of ${cw.pretty}:\n\t");CNF(leo.modules.calculus.freshVarGen(cw.cl),cw.cl)}.map(c => {Out.finest(s"${c.pretty}\n\t");ClauseWrapper(c, InferredFrom(CNF, Set(cw)))}))
                // Pre-unify new clauses
                val (uniClauses, otherClauses):(Set[(ClauseWrapper, PreUni.UniLits, PreUni.OtherLits)], Set[ClauseWrapper]) = newclauses.foldLeft((Set[(ClauseWrapper, PreUni.UniLits, PreUni.OtherLits)](), Set[ClauseWrapper]())) {case ((uni,ot),cw) => {
                  val (cA, ul, ol) = PreUni.canApply(cw.cl)
                  if (cA) {
                    (uni + ((cw, ul, ol)),ot)
                  } else {
                    (uni, ot + cw)
                  }
                }}
                if (uniClauses.nonEmpty) {
                  Out.debug("Unification tasks found. Working on it...")
                  newclauses = otherClauses
                  uniClauses.foreach { case (cw, ul, ol) =>
                    val nc = PreUni(leo.modules.calculus.freshVarGen(cw.cl), ul, ol).map{case (cl,subst) => ClauseWrapper(cl, InferredFrom(PreUni, Set((cw, ToTPTP(subst)))))}
                    Out.trace(s"Uni result:\n\t${nc.map(_.pretty).mkString("\n\t")}")
                    newclauses = newclauses union nc
                  }

                }

              }
            }


            // At the end, for each generated clause apply simplification etc.
            val newIt = newclauses.iterator
            while (newIt.hasNext) {
              var newCl = newIt.next()
              // Simplify again
              newCl = ClauseWrapper(Simp(newCl.cl), InferredFrom(Simp, Set(newCl)))
              // TODO: Cheap rewriting à la E
              // ...

              if (!Clause.trivial(newCl.cl)) {
                unprocessed = unprocessed + newCl
              }
            }

          } else {
            Out.debug("clause subsumbed, skipping.")
            Out.trace(s"Subsumed by:\n\t${processed.filter(cw => Subsumption.subsumes(cw.cl, simpl)).map(_.pretty).mkString("\n\t")}")
          }

        }
      }

    }

    Out.output(SZSOutput(returnSZS, Configuration.PROBLEMFILE))
  }


}

protected[seqpproc] abstract sealed class WrapperAnnotation extends Pretty
case class InferredFrom(rule: leo.modules.calculus.CalculusRule, cws: Set[(ClauseWrapper, Output)]) extends WrapperAnnotation {
  def pretty: String = s"inference(${rule.name},[${rule.inferenceStatus.fold("")("status("+_.pretty.toLowerCase+")")}],[${cws.map{case (cw,add) => if (add == null) {cw.id} else {cw.id + ":[" + add.output + "]"} }.mkString(",")}])"
}
case object NoAnnotation extends WrapperAnnotation {val pretty: String = ""}
case class FromFile(fileName: String, formulaName: String) extends WrapperAnnotation { def pretty = s"file('$fileName',$formulaName)" }

object InferredFrom {
  def apply(rule: leo.modules.calculus.CalculusRule, cws: Set[ClauseWrapper]): WrapperAnnotation = {
    new InferredFrom(rule, cws.map((_,null)))
  }
}

protected[seqpproc] case class ClauseWrapper(id: String, cl: Clause, role: Role, annotation: WrapperAnnotation) extends Ordered[ClauseWrapper] with Pretty {
  override def equals(o: Any): Boolean = o match {
    case cw: ClauseWrapper => cw.cl == cl && cw.role == role
    case _ => false
  }
  override def hashCode(): Int = cl.hashCode() ^ role.hashCode()

  def compare(that: ClauseWrapper) = Configuration.CLAUSE_ORDERING.compare(this.cl, that.cl) // FIXME mustmatch withequals and hash

//  def pretty: String = s"[$id]:\t${cl.pretty}\t(${annotation match {case InferredFrom(_,cws) => cws.map(_._1.id).mkString(","); case _ => ""}})"
  def pretty: String = s"[$id]:\t${cl.pretty}\t(${annotation.pretty})"
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

