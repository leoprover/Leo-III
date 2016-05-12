package leo.modules.seqpproc

import leo.Configuration
import leo.Out
import leo.datastructures.impl.Signature
import leo.datastructures.{Clause, ClauseAnnotation, AnnotatedClause, ClauseProxy, LitFalse, LitTrue, Literal, Pretty, Role, Role_Axiom, Role_Conjecture, Role_Definition, Role_NegConjecture, Role_Plain, Role_Type, Term}
import ClauseAnnotation._
import leo.modules.output._
import leo.modules.{Parsing, SZSException, SZSOutput, Utility}
import leo.modules.calculus.{CalculusRule, Subsumption}


import scala.collection.SortedSet

/**
  * Created by lex on 10/28/15.
  */
object SeqPProc extends Function1[Long, Unit]{

  @inline private final def termToClause(t: Term): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, true)))
  }

  final def preprocess(cur: AnnotatedClause): Set[AnnotatedClause] = {
    var result: Set[AnnotatedClause] = Set()
    // Fresh clause, that means its unit and nonequational
    assert(Clause.unit(cur.cl), "clause not unit")
    val lit = cur.cl.lits.head
    assert(!lit.equational, "initial literal equational")

    // Def expansion and simplification
    var cw = cur
    cw = Control.expandDefinitions(cw)
    cw = Control.switchPolarity(cw)

    // Exhaustively CNF
    result = Control.cnf(cw)
    // Remove defined equalities as far as possible
    result = Control.convertDefinedEqualities(result)

    // To equation if possible and then apply func ext
    // AC Simp if enabled, then Simp.
    result = result.map { cl =>
      var result = cl
      result = Control.liftEq(result)
      result = Control.funcext(result)
      result = Control.acSimp(result)
      Control.simp(result)
    }
    // Pre-unify new clauses
    result = Control.preunifySet(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }


  ///////////////////////////////////////////////////////////

  /* Main function containing proof loop */
  final def apply(startTime: Long): Unit = {
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up
    // SOS set-sup
    /////////////////////////////////////////
    var test = false
    // Read problem
    val input = Parsing.parseProblem(Configuration.PROBLEMFILE)
    val startTimeWOParsing = System.currentTimeMillis()
    // Filter out inputs that were produced by definitions and type declarations
    val filteredInput = input.filterNot(i => i._3 == Role_Definition || i._3 == Role_Type)
    // Negate conjecture
    val conjecture = filteredInput.filter {case (id, term, role) => role == Role_Conjecture}
    if (conjecture.size > 1) throw new SZSException(SZS_InputError, "At most one conjecture per input problem permitted.")
    val conj = conjecture.head
    val conjWrapper = AnnotatedClause(Clause.mkClause(Seq(Literal.mkLit(conj._2, true))), conj._3, FromFile(Configuration.PROBLEMFILE, conj._1), ClauseAnnotation.PropNoProp)
    val negatedConjecture = AnnotatedClause(Clause.mkClause(Seq(Literal.mkLit(conj._2, false))), Role_NegConjecture, InferredFrom(new CalculusRule {
      override def name: String = "neg_conjecture"
      override val inferenceStatus = Some(SZS_CounterSatisfiable)
    }, Set(conjWrapper)),ClauseAnnotation.PropSOS)

    // Input to proving process (axioms plus negated conjecture, if existent)
    val effectiveInputWithoutConjecture: Seq[AnnotatedClause] = if (conjecture.isEmpty) {
      filteredInput.map { case (id, term, role) => AnnotatedClause(termToClause(term), role, FromFile(Configuration.PROBLEMFILE, id), ClauseAnnotation.PropSOS) }
    } else {
      assert(conjecture.size == 1)
      val rest = filteredInput.filterNot(_._1 == conjecture.head._1)
      rest.map { case (id, term, role) => AnnotatedClause(termToClause(term), role, FromFile(Configuration.PROBLEMFILE, id), ClauseAnnotation.PropNoProp) }
    }

    // Proprocess terms with standard normalization techniques for terms (non-equational)
    // transform into equational literals if possible
    val state: State[AnnotatedClause] = State.fresh(Signature.get)
    Out.debug("## Preprocess Neg.Conjecture BEGIN")
    val conjecture_preprocessed = preprocess(negatedConjecture).filterNot(cw => Clause.trivial(cw.cl))
    Out.debug(s"# Result:\n\t${conjecture_preprocessed.map{_.pretty}.mkString("\n\t")}")
    Out.debug("## Preprocess Neg.Conjecture END")

    Out.debug("## Preprocess BEGIN")
    val inputIt = effectiveInputWithoutConjecture.iterator
    while (inputIt.hasNext) {
      val cur = inputIt.next()
      Out.debug(s"# Process: ${cur.pretty}")
      val processed = preprocess(cur)
      Out.debug(s"# Result:\n\t${processed.map{_.pretty}.mkString("\n\t")}")
      var preprocessed = processed.filterNot(cw => Clause.trivial(cw.cl))
      state.addUnprocessed(preprocessed)
      if (inputIt.hasNext) Out.trace("--------------------")
    }
    Out.debug("## Preprocess END\n\n")

    val preprocessTime = System.currentTimeMillis() - startTimeWOParsing
    Control.fvIndexInit(state.unprocessed.toSet union conjecture_preprocessed)
    var loop = true

    // Init loop for conjecture-derived clauses
    val conjectureProcessedIt = conjecture_preprocessed.iterator
    Out.debug("## Pre-reasoning loop BEGIN")
    while(conjectureProcessedIt.hasNext && loop && !prematureCancel(state.noProcessedCl)) {
      if (System.currentTimeMillis() - startTime > 1000*Configuration.TIMEOUT) {
        loop = false
        state.setSZSStatus(SZS_Timeout)
      } else {
        var cur = conjectureProcessedIt.next()
        Out.debug(s"Taken: ${cur.pretty}")
        cur =  Control.rewriteSimp(cur, state.rewriteRules)
        if (Clause.effectivelyEmpty(cur.cl)) { // TODO: Instantiate flex-flex to get real proof
          loop = false
          if (conjecture.isEmpty) {
            state.setSZSStatus(SZS_ContradictoryAxioms)
          } else {
            state.setSZSStatus(SZS_Theorem)
          }
          state.setDerivationClause(cur)
        } else {
          // Subsumption
          if (!state.processed.exists(cw => Subsumption.subsumes(cw.cl, cur.cl))) {
            mainLoopInferences(cur, state)
          } else {
            Out.debug("clause subsumbed, skipping.")
            state.incForwardSubsumedCl()
            Out.trace(s"Subsumed by:\n\t${state.processed.filter(cw => Subsumption.subsumes(cw.cl, cur.cl)).map(_.pretty).mkString("\n\t")}")
          }
        }
      }
    }
    Out.debug("## Pre-reasoning loop END")

    /////////////////////////////////////////
    // Main proof loop
    /////////////////////////////////////////
    Out.debug("## Reasoning loop BEGIN")
    while (loop && !prematureCancel(state.noProcessedCl)) {
      if (System.currentTimeMillis() - startTime > 1000*Configuration.TIMEOUT) {
        loop = false
        state.setSZSStatus(SZS_Timeout)
      } else if (state.unprocessed.isEmpty) {
        loop = false
      } else {
        // No cancel, do reasoning step
        if (Configuration.isSet("ec") && state.noProcessedCl % 20 == 0 && !test) {
          test = true
          Out.debug(s"CALL LEO-II")
          val returnszs = Control.callExternalLeoII(state.processed)
          Out.debug(s"${returnszs.pretty}")

          if (returnszs == SZS_Theorem) {
            loop = false
            state.setSZSStatus(SZS_Theorem)
            val resultcl = AnnotatedClause(Clause(Seq()), InferredFrom(CallLeo, state.processed))
            state.setDerivationClause(resultcl)
          }

        } else {
          test = false
          var cur = state.nextUnprocessed
          // cur is the current AnnotatedClause
          Out.debug(s"Taken: ${cur.pretty}")

          cur = Control.rewriteSimp(cur, state.rewriteRules)
          if (Clause.effectivelyEmpty(cur.cl)) {
            // TODO: Instantiate flex-flex to get real proof
            loop = false
            if (conjecture.isEmpty) {
              state.setSZSStatus(SZS_ContradictoryAxioms)
            } else {
              state.setSZSStatus(SZS_Theorem)
            }
            state.setDerivationClause(cur)
          } else {
            // Subsumption
            if (!state.processed.exists(cw => Subsumption.subsumes(cw.cl, cur.cl))) {
              mainLoopInferences(cur, state)
            } else {
              Out.debug("clause subsumbed, skipping.")
              state.incForwardSubsumedCl()
              Out.trace(s"Subsumed by:\n\t${state.processed.filter(cw => Subsumption.subsumes(cw.cl, cur.cl)).map(_.pretty).mkString("\n\t")}")
            }
          }
        }
      }

    }

    /////////////////////////////////////////
    // Main loop terminated, print result
    /////////////////////////////////////////

    val time = System.currentTimeMillis() - startTime
    val timeWOParsing = System.currentTimeMillis() - startTimeWOParsing

    Out.output("")
    Out.output(SZSOutput(state.szsStatus, Configuration.PROBLEMFILE, s"${time} ms resp. ${timeWOParsing} ms w/o parsing"))

    /* Output additional information about the reasoning process. */
    Out.comment(s"Time passed: ${time}ms")
    Out.comment(s"Effective reasoning time: ${timeWOParsing}ms")
    Out.comment(s"Thereof preprocessing: ${preprocessTime}ms")
    Out.comment(s"No. of processed clauses: ${state.noProcessedCl}")
    Out.comment(s"No. of generated clauses: ${state.noGeneratedCl}")
    Out.comment(s"No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
    Out.comment(s"No. of units in store: ${state.rewriteRules.size}")
    if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
      Out.comment("Signature extension used:")
      Out.comment(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
      Out.comment(Utility.userDefinedSignatureAsString) // TODO: Adjust for state
    }
    // FIXME: Count axioms used in proof:
    //    if (derivationClause != null)
    //      Out.output(s" No. of axioms used: ${axiomsUsed(derivationClause)}")

    /* Print proof object if possible and requested. */
    if (state.szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && state.derivationClause.isDefined) {
      Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
      //      Out.output(makeDerivation(derivationClause).drop(1).toString)
      Utility.printProof(state.derivationClause.get)
      Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
    }
  }

  @inline private final def mainLoopInferences(cl: AnnotatedClause, state: State[AnnotatedClause]): Unit = {
    var cur: AnnotatedClause = cl
    var newclauses: Set[AnnotatedClause] = Set()

    /////////////////////////////////////////
    // Simplifying (mofifying inferences and backward subsumption) BEGIN
    // TODO: à la E: direct descendant criterion, etc.
    /////////////////////////////////////////
    /* Subsumption */
    state.setProcessed(state.processed.filterNot(cw => Subsumption.subsumes(cur.cl, cw.cl)))
    state.addProcessed(cur)
    Control.fvIndexInsert(cur)
    /* Add rewrite rules to set */
    if (Clause.rewriteRule(cur.cl)) {
      state.addRewriteRule(cur)
    }
    /* Functional Extensionality */
    cur = Control.funcext(cur)
    /* To equality if possible */
    cur = Control.liftEq(cur)
    /////////////////////////////////////////
    // Simplifying (mofifying inferences) END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // Generating inferences BEGIN
    /////////////////////////////////////////
    /* Boolean Extensionality */
    val boolext_result = Control.boolext(cur)
    newclauses = newclauses union boolext_result

    /* paramodulation where at least one involved clause is `cur` */
    val paramod_result = Control.paramodSet(cur, state.processed)
    newclauses = newclauses union paramod_result

    /* Equality factoring of `cur` */
    val factor_result = Control.factor(cur)
    newclauses = newclauses union factor_result

    /* Prim subst */
    val primSubst_result = Control.primsubst(cur)
    newclauses = newclauses union primSubst_result

    /* TODO: Choice */
    /////////////////////////////////////////
    // Generating inferences END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // Simplification of newly generated clauses BEGIN
    /////////////////////////////////////////
    state.incGeneratedCl(newclauses.size)
    /* Simplify new clauses */
    newclauses = Control.simpSet(newclauses)
    /* Remove those which are tautologies */
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(cw => Control.cnf(cw))
    /* Pre-unify new clauses */
    newclauses = Control.preunifySet(newclauses)
    /* Replace defined equalities */
    newclauses = Control.convertDefinedEqualities(newclauses)
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(Control.liftEq)
    /////////////////////////////////////////
    // Simplification of newly generated clauses END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // At the end, for each generated clause apply simplification etc.
    // and add to unprocessed
    /////////////////////////////////////////

    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      var newCl = newIt.next()
      // Simplify again, including rewriting etc.
      newCl = Control.rewriteSimp(newCl, state.rewriteRules)

      if (!Clause.trivial(newCl.cl)) {
        state.addUnprocessed(newCl)
      } else {
        Out.trace(s"Trivial, hence dropped: ${newCl.pretty}")
      }
    }
  }

  @inline final def prematureCancel(counter: Int): Boolean = {
    try {
      val limit: Int = Configuration.valueOf("ll").get.head.toInt
      counter >= limit
    } catch {
      case e: NumberFormatException => false
      case e: NoSuchElementException => false
    }
  }
  object CallLeo extends leo.modules.calculus.CalculusRule {
    val name = "call_leo2"
    override val inferenceStatus = Some(SZS_Theorem)
  }
  final def makeDerivation(cw: ClauseProxy, sb: StringBuilder = new StringBuilder(), indent: Int = 0): StringBuilder = cw.annotation match {
    case NoAnnotation => sb.append("\n"); sb.append(" ` "*indent); sb.append(s"thf(${cw.id}, ${cw.role}, ${cw.cl.pretty}).")
    case a@FromFile(_, _) => sb.append("\n"); sb.append(" ` "*indent); sb.append(s"thf(${cw.id}, ${cw.role}, ${cw.cl.pretty}, ${a.pretty}).")
    case a@InferredFrom(_, parents) => {
      sb.append("\n");
      sb.append(" | "*indent);
      sb.append(s"thf(${cw.id}, ${cw.role}, ${cw.cl.pretty}, ${a.pretty}).")
      if (parents.size == 1) {
        makeDerivation(parents.head._1,sb,indent)
      } else parents.foreach {case (parent, _) => makeDerivation(parent,sb,indent+1)}
      sb
    }
  }
}
