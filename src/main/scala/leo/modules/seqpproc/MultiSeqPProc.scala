package leo.modules.seqpproc

import java.util.concurrent.atomic.AtomicInteger

import leo.agents.ProofProcedure
import leo.{Configuration, Out}
import leo.datastructures.ClauseAnnotation._
import leo.datastructures.impl.Signature
import leo.datastructures._
import leo.datastructures.context.Context
import leo.modules.calculus.Subsumption
import leo.modules.output._
import leo.modules.Utility

/**
  *
  * Implementation of the sequential loop for an agent
  * to be executed in parallel
  *
  * @author Max Wisniewski, Alexander Steen
  * @since 5/24/16
  */
object MultiSeqPProc extends ProofProcedure {

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
//    result = result union Control.convertDefinedEqualities2(result)

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
    result = result union Control.preunifySet(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }


  ///////////////////////////////////////////////////////////
  private var counter : AtomicInteger = new AtomicInteger()

  /* Main function containing proof loop */
  /**
    * Executes a sequential proof procedure.
    * Upon return updates the blackboard with
    * <ol>
    * <li>The SZS status</li>
    * <li>The remaining proof obligations.<br />The obligation should contain the empty clause, if a proof was found</li>
    * </ol>
    *
    * @param cs The set of formulas we want to proof a contradiction.
    * @return The SZS status and optinally the remaing proof obligations. In the case of a sucessfull proof the empty
    *         clause should be returned (containing the proof).
    */
  override def execute(cs: Iterable[AnnotatedClause], c: Context): (StatusSZS, Option[Seq[AnnotatedClause]]) = {
    val proc = counter.incrementAndGet()
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up
    // SOS set-sup
    /////////////////////////////////////////
    var test = false
    val conjecture : Iterable[AnnotatedClause] = cs.filter(x => x.role == Role_NegConjecture || x.role == Role_Conjecture)
    assert(conjecture.size == 1)
    val negatedConjecture : AnnotatedClause = conjecture.head  // TODO no conjecture?
    val effectiveInputWithoutConjecture : Iterable[AnnotatedClause] = cs.filter(_.role != Role_NegConjecture)
    // Read problem
    // Proprocess terms with standard normalization techniques for terms (non-equational)
    // transform into equational literals if possible
    val state: State[AnnotatedClause] = State.fresh(Signature.get)
    Out.debug(s"## ($proc) Preprocess Neg.Conjecture BEGIN")
    val conjecture_preprocessed = preprocess(negatedConjecture).filterNot(cw => Clause.trivial(cw.cl))
    Out.debug(s"# ($proc) Result:\n\t${conjecture_preprocessed.map{_.pretty}.mkString("\n\t")}")
    Out.debug(s"## ($proc)Preprocess Neg.Conjecture END")

    Out.debug(s"## ($proc) Preprocess BEGIN")
    val inputIt = effectiveInputWithoutConjecture.iterator
    while (inputIt.hasNext) {
      val cur = inputIt.next()
      Out.debug(s"# ($proc) Process: ${cur.pretty}")
      val processed = preprocess(cur)
      Out.debug(s"# ($proc) Result:\n\t${processed.map{_.pretty}.mkString("\n\t")}")
      var preprocessed = processed.filterNot(cw => Clause.trivial(cw.cl))
      state.addUnprocessed(preprocessed)
      if (inputIt.hasNext) Out.trace("--------------------")
    }
    Out.debug(s"## ($proc)Preprocess END\n\n")

    Out.finest(s" ($proc) Clauses and maximal literals of them:")
    for (c <- state.unprocessed union conjecture_preprocessed)  {
      Out.finest(s"($proc) Clause ${c.pretty}")
      Out.finest(s"($proc) Maximal literal(s):")
      Out.finest(s"\t${c.cl.maxLits.map(_.pretty).mkString("\n\t")}")
    }
    Out.finest(s"################")
//    val preprocessTime = System.currentTimeMillis() - startTimeWOParsing
    Control.fvIndexInit(state.unprocessed.toSet union conjecture_preprocessed)
    var loop = true

    // Init loop for conjecture-derived clauses
    val conjectureProcessedIt = conjecture_preprocessed.toSeq.sorted.iterator
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
        // Subsumption
        if (!state.processed.exists(cw => Subsumption.subsumes(cw.cl, cur.cl))) {
          mainLoopInferences(cur, state)
        } else {
          Out.debug(s"($proc) clause subsumbed, skipping.")
          state.incForwardSubsumedCl()
          Out.trace(s"($proc) Subsumed by:\n\t${state.processed.filter(cw => Subsumption.subsumes(cw.cl, cur.cl)).map(_.pretty).mkString("\n\t")}")
        }
      }
    }
    Out.debug(s"## ($proc) Pre-reasoning loop END")



    /////////////////////////////////////////
    // Main proof loop
    /////////////////////////////////////////
    Out.debug(s"## ($proc) Reasoning loop BEGIN")
    while (loop && !prematureCancel(state.noProcessedCl)) {
      if (state.unprocessed.isEmpty) {
        loop = false
      } else {
        // No cancel, do reasoning step
        if (Configuration.isSet("ec") && state.noProcessedCl % 20 == 0 && !test) {
          test = true
          Out.debug(s"($proc) CALL LEO-II")
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
            // Subsumption
            if (!state.processed.exists(cw => Subsumption.subsumes(cw.cl, cur.cl))) {
              mainLoopInferences(cur, state)
            } else {
              Out.debug(s"($proc)clause subsumbed, skipping.")
              state.incForwardSubsumedCl()
              Out.trace(s"($proc) Subsumed by:\n\t${state.processed.filter(cw => Subsumption.subsumes(cw.cl, cur.cl)).map(_.pretty).mkString("\n\t")}")
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
    Out.comment(s"($proc) No. of units in store: ${state.rewriteRules.size}")
    Out.debug(s"($proc) literals processed: ${state.processed.flatMap(_.cl.lits).size}")
    Out.debug(s"($proc) -thereof maximal ones: ${state.processed.flatMap(_.cl.maxLits).size}")
    Out.debug(s"($proc) avg. literals per clause: ${state.processed.flatMap(_.cl.lits).size/state.processed.size.toDouble}")
    Out.debug(s"($proc) avg. max. literals per clause: ${state.processed.flatMap(_.cl.maxLits).size/state.processed.size.toDouble}")
    Out.debug(s"($proc) unoriented processed: ${state.processed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"($proc) oriented processed: ${state.processed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"($proc) unoriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"($proc) oriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(_.oriented)}")

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
    newclauses = Control.preunifySet(newclauses)

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

  override def name: String = "MultiSeqProc"
}
