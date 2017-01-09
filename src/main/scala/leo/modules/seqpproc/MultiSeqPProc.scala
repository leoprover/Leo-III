package leo.modules.seqpproc

import java.util.concurrent.atomic.AtomicInteger

import leo.agents.ProofProcedure
import leo.agents.impl.SZSScriptAgent
import leo.{Configuration, Out}
import leo.datastructures.impl.SignatureImpl
import leo.datastructures._
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules.control.Control
import leo.modules.output._

object MultiSeqPProc {
  private[seqpproc] var counter : AtomicInteger = new AtomicInteger()
}

/**
  *
  * Implementation of the sequential loop for an agent
  * to be executed in parallel
  *
  * @author Max Wisniewski, Alexander Steen
  * @since 5/24/16
  */
class MultiSeqPProc(externalCallIteration : Int, addPreprocessing : Set[AnnotatedClause] => Set[AnnotatedClause]) extends ProofProcedure {


  ///////////////////////////////////////////////////////////
  /* Main function containing proof loop */
  /**
    * Executes a sequential proof procedure.
    * Upon return updates the blackboard with
    * <ol>
    * <li>The SZS status</li>
    * <li>The remaining proof obligations.<br />The obligation should contain the empty clause, if a proof was found</li>
    * </ol>
    *
    * @param cs1 The set of formulas we want to proof a contradiction.
    * @return The SZS status and optinally the remaing proof obligations. In the case of a sucessfull proof the empty
    *         clause should be returned (containing the proof).
    */
  override def execute(cs1: Iterable[AnnotatedClause]): (StatusSZS, Option[Seq[AnnotatedClause]]) = {
    import leo.modules.seqpproc.SeqPProc.preprocess
    val proc = MultiSeqPProc.counter.incrementAndGet()
    val cs = addPreprocessing(cs1.toSet)
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up
    // SOS set-sup
    /////////////////////////////////////////
    var test = false
    val conjecture : Iterable[AnnotatedClause] = cs.filter(x => x.role == Role_NegConjecture || x.role == Role_Conjecture)
    assert(conjecture.size == 1)
    val negatedConjecture : AnnotatedClause = conjecture.head  // TODO no conjecture?
    val effectiveInputWithoutConjecture : Set[AnnotatedClause] = cs.filter(_.role != Role_NegConjecture)

    // Read problem
    // Proprocess terms with standard normalization techniques for terms (non-equational)
    // transform into equational literals if possible
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    Control.fvIndexInit((effectiveInputWithoutConjecture + negatedConjecture).toSeq)
    Out.debug(s"## ($proc) Preprocess Neg.Conjecture BEGIN")
    val conjecture_preprocessed = preprocess(state, negatedConjecture).filterNot(cw => Clause.trivial(cw.cl))
    Out.debug(s"# ($proc) Result:\n\t${conjecture_preprocessed.map{_.pretty}.mkString("\n\t")}")
//    Control.fvIndexInsert(conjecture_preprocessed)
    Out.debug(s"## ($proc)Preprocess Neg.Conjecture END")

    Out.debug(s"## ($proc) Preprocess BEGIN")
    val inputIt = effectiveInputWithoutConjecture.iterator
    while (inputIt.hasNext) {
      val cur = inputIt.next()
      Out.debug(s"# ($proc) Process: ${cur.pretty}")
      val processed = preprocess(state, cur)
      Out.debug(s"# ($proc) Result:\n\t${processed.map{_.pretty}.mkString("\n\t")}")
      val preprocessed = processed.filterNot(cw => Clause.trivial(cw.cl))
      state.addUnprocessed(preprocessed)
//      Control.fvIndexInsert(preprocessed)
      if (inputIt.hasNext) Out.trace("--------------------")
    }
    Out.debug(s"## ($proc)Preprocess END\n\n")

    Out.finest(s" ($proc) Clauses and maximal literals of them:")
    for (c <- state.unprocessed union conjecture_preprocessed)  {
      Out.finest(s"($proc) Clause ${c.pretty}")
      Out.finest(s"($proc) Maximal literal(s):")
      Out.finest(s"\t${Literal.maxOf(c.cl.lits).map(_.pretty).mkString("\n\t")}")
    }
    Out.finest(s"################")
//    val preprocessTime = System.currentTimeMillis() - startTimeWOParsing
    var loop = true

    // Init loop for conjecture-derived clauses
    val conjectureProcessedIt = conjecture_preprocessed.toSeq.iterator
    Out.debug(s"## ($proc)Pre-reasoning loop BEGIN")
    while(conjectureProcessedIt.hasNext && loop && !prematureCancel(state.noProcessedCl)) {
      var cur = conjectureProcessedIt.next()
      Out.debug(s"($proc) Taken: ${cur.pretty}")
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
        // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
        // e.g. by forward subsumption
        if (!Control.redundant(cur, state.processed)) {
          mainLoopInferences(cur, state)
        } else {
          Out.debug(s"Clause ${cur.id} redundant, skipping.")
          state.incForwardSubsumedCl()
        }
      }
    }
    Out.debug(s"## ($proc) Pre-reasoning loop END")



    /////////////////////////////////////////
    // Main proof loop
    /////////////////////////////////////////
    var sinceLastExternal : Int = 0
    Out.debug(s"## ($proc) Reasoning loop BEGIN")
    while (loop && !prematureCancel(state.noProcessedCl) && !Scheduler().isTerminated) {
      if (state.unprocessed.isEmpty) {
        SZSScriptAgent.execute(state.processed)
        loop = false
      } else {
        // Should an external Call be made?
        sinceLastExternal += 1
        if(Configuration.ATPS.nonEmpty && sinceLastExternal > externalCallIteration){
          sinceLastExternal = 0
          SZSScriptAgent.execute(state.processed)
        } else {
          var cur = state.nextUnprocessed
          // cur is the current AnnotatedClause
          Out.debug(s"($proc) Taken: ${cur.pretty}")

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
            // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
            // e.g. by forward subsumption
            if (!Control.redundant(cur, state.processed)) {
              mainLoopInferences(cur, state)
            } else {
              Out.debug(s"Clause ${cur.id} redundant, skipping.")
              state.incForwardSubsumedCl()
            }
          }
        }
      }

    }

    /////////////////////////////////////////
    // Main loop terminated, print result
    /////////////////////////////////////////
    Out.output("")
//    Out.output(SZSOutput(state.szsStatus, Configuration.PROBLEMFILE, s"${time} ms resp. ${timeWOParsing} ms w/o parsing"))

    /* Output additional information about the reasoning process. */
    Out.comment(s"($proc) No. of processed clauses: ${state.noProcessedCl}")
    Out.comment(s"($proc) No. of generated clauses: ${state.noGeneratedCl}")
    Out.comment(s"($proc) No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
    Out.comment(s"($proc) No. of backward subsumed clauses: ${state.noBackwardSubsumedCl}")
    Out.comment(s"($proc) No. of units in store: ${state.rewriteRules.size}")
    Out.debug(s"($proc) literals processed: ${state.processed.flatMap(_.cl.lits).size}")
    Out.debug(s"($proc) -thereof maximal ones: ${state.processed.flatMap(c => Literal.maxOf(c.cl.lits)).size}")
    Out.debug(s"($proc) avg. literals per clause: ${state.processed.flatMap(_.cl.lits).size/state.processed.size.toDouble}")
    Out.debug(s"($proc) avg. max. literals per clause: ${state.processed.flatMap(c => Literal.maxOf(c.cl.lits)).size/state.processed.size.toDouble}")
    Out.debug(s"($proc) unoriented processed: ${state.processed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"($proc) oriented processed: ${state.processed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"($proc) unoriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"($proc) oriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"subsumption tests: ${leo.modules.calculus.Subsumption.subsumptiontests}")

    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) units")
    Out.finest(state.rewriteRules.map(_.pretty).mkString("\n\t"))
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) Processed unoriented")
    Out.finest(s"($proc) #########################")
    Out.finest(state.processed.flatMap(_.cl.lits).filter(!_.oriented).map(_.pretty).mkString("\n\t"))
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) #########################")
    Out.finest(s"($proc) Unprocessed oriented")
    Out.finest(state.unprocessed.flatMap(_.cl.lits).filter(!_.oriented).map(_.pretty).mkString("\n\t"))
    Out.finest(s"($proc) #########################")


//    if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
//      Out.comment(s"($proc) Signature extension used:")
//      Out.comment(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
//      Out.comment(Utility.userDefinedSignatureAsString) // TODO: Adjust for state
//    }
    // FIXME: Count axioms used in proof:
    //    if (derivationClause != null)
    //      Out.output(s" No. of axioms used: ${axiomsUsed(derivationClause)}")

    return (state.szsStatus, state.derivationClause.map(Seq(_)))
  }

  @inline private final def mainLoopInferences(cl: AnnotatedClause, state: State[AnnotatedClause]): Unit = {
    implicit val sig: Signature = state.signature
    var cur: AnnotatedClause = cl
    var newclauses: Set[AnnotatedClause] = Set()

    /////////////////////////////////////////
    // Simplifying (mofifying inferences and backward subsumption) BEGIN
    // TODO: à la E: direct descendant criterion, etc.
    /////////////////////////////////////////
    /* Subsumption */
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)
    state.incBackwardSubsumedCl(backSubsumedClauses.size)
    state.setProcessed(state.processed -- backSubsumedClauses)
    Control.fvIndexRemove(backSubsumedClauses)
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
    val primSubst_result = Control.primsubst(cur, 1)
    newclauses = newclauses union primSubst_result

    /* Replace defined equalities */
    newclauses = newclauses union Control.convertDefinedEqualities(newclauses)

    /* TODO: Choice */
    /////////////////////////////////////////
    // Generating inferences END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // Simplification of newly generated clauses BEGIN
    /////////////////////////////////////////
    state.incGeneratedCl(newclauses.size)
    /* Simplify new clauses */
//    newclauses = Control.simpSet(newclauses)
    /* Remove those which are tautologies */
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(cw => Control.cnf(cw))
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(Control.liftEq)
    /* Pre-unify new clauses */
    newclauses = Control.unifyNewClauses(newclauses)

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
//        Control.fvIndexInsert(newCl)
      } else
        Out.trace(s"Trivial, hence dropped: ${newCl.pretty}")
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


  override val name: String = s"MultiSeqProc($externalCallIteration)"
}
