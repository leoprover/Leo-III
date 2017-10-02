package leo.modules.prover

import leo.{Configuration, Out}
import leo.datastructures._
import leo.modules.{SZSOutput, myAssert}
import leo.modules.control.Control
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.output._
import leo.modules.parsers.Input
import leo.modules.proof_object.CompressProof

/**
  * Sequential proof procedure.
  * Its state is represented by [[leo.modules.prover.State]].
  *
  * @since 28.10.15
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
object SeqLoop {
  ////////////////////////////////////
  //// Preprocessing
  ////////////////////////////////////
  protected[modules] final def preprocess(cur: AnnotatedClause)(implicit state: LocalGeneralState): Set[AnnotatedClause] = {
    implicit val sig: Signature = state.signature
    var result: Set[AnnotatedClause] = Set.empty

    // Fresh clause, that means its unit and non-equational
    myAssert(Clause.unit(cur.cl), s"[Preprocess] clause not unit: ${cur.cl.pretty(sig)}")
    val lit = cur.cl.lits.head
    myAssert(!lit.equational, s"[Preprocess] initial literal equational: ${cur.cl.pretty(sig)}")

    // Def expansion and simplification
    val expanded = Control.expandDefinitions(cur)
    if (state.externalProvers.nonEmpty) state.addInitial(Set(expanded))
    val polaritySwitchedAndExpanded = Control.switchPolarity(expanded)
    // We may instantiate here special symbols for universal variables
    // Its BEFORE miniscope because their are less quantifiers and maybe
    // some universal quantification may vanish after extensional instantiation
    // Run simp here again to eliminate connectives with true/false as operand due
    // to ext. instantiation.
    result = Control.specialInstances(polaritySwitchedAndExpanded)(state)
    // Exhaustively calculate CNF of input (do miniscoping before)
    result = result.flatMap { cl => Control.cnf(Control.miniscope(cl))(state) }
    // Remove instances of axiom of choice (AoC)
    result = result.filterNot { cl => Control.detectChoiceClause(cl)(state) }
    // Add detected equalities as primitive ones
    result = result union Control.convertDefinedEqualities(result)
    // Do cheap simplification, transform equality symbols on top-level to proper equality literals
    // Also, search for specifications of AC (Associativity/Commutativity)
    result = result.map { cl =>
      val simp = Control.shallowSimp(Control.liftEq(cl))
      Control.detectAC(simp)
      Control.detectDomainConstraint(simp)
      if (!state.isPolymorphic && simp.cl.typeVars.nonEmpty) state.setPolymorphic() // TODO: FIXME
      simp
    }
    // Pre-unify new clauses or treat them extensionally and remove trivial ones
    result = Control.extPreprocessUnify(result)(state)
    result = Control.simpSet(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }

  ////////////////////////////////////
  //// Main-loop operations
  ////////////////////////////////////

  /* Main function containing proof loop */
  final def apply(startTime: Long, timeout: Int): Unit = {
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up, etc.
    /////////////////////////////////////////
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    val strategy: RunStrategy = if (Configuration.isSet("strategy")) {
      val strategyParam0 = Configuration.valueOf("strategy")
      if (strategyParam0.isDefined) {
        val strategyParam = strategyParam0.get.head
        RunStrategy.byName(strategyParam)
      } else Control.defaultStrategy
    } else {
      Control.defaultStrategy
    }
    state.setRunStrategy(strategy)
    state.setTimeout(timeout)
    Out.config(s"Using configuration: timeout($timeout) with ${state.runStrategy.pretty}")

    try {
      // Check if external provers were defined
      if (Configuration.ATPS.nonEmpty) Control.registerExtProver(Configuration.ATPS)(state)

      // Read problem from file
      val input2 = Input.parseProblem(Configuration.PROBLEMFILE)
      val startTimeWOParsing = System.currentTimeMillis()
      // Split input in conjecture/definitions/axioms etc.
      val remainingInput: Seq[AnnotatedClause] = effectiveInput(input2, state)
      // Typechecking: Throws and exception if not well-typed
      typeCheck(remainingInput, state)
      Out.info(s"Type checking passed. Searching for refutation ...")
      run(remainingInput, startTime)(state)
      printResult(state, startTime, startTimeWOParsing)
    } catch {
      case e:Exception =>
        Out.trace(s"Signature used:\n${leo.modules.signatureAsString(sig)}")
        e.printStackTrace()
        throw e
      case e:Error =>
        Out.trace(s"Signature used:\n${leo.modules.signatureAsString(sig)}")
        throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  final def run(input: Seq[AnnotatedClause], startTime: Long)(implicit state: State[AnnotatedClause]): Boolean = {
    try {
      implicit val sig: Signature = state.signature
      val timeout0 = state.timeout
      val timeout = if (timeout0 == 0) Float.PositiveInfinity else timeout0
      // Initialize indexes
      state.initUnprocessed()
      val toPreprocess = if (state.negConjecture != null) {
        val simpNegConj = Control.expandDefinitions(state.negConjecture)
        state.defConjSymbols(simpNegConj)
        val input0 = simpNegConj +: input
        Control.initIndexes(input0)
        input0
      } else {
        Control.initIndexes(input)
        input
      }
      // Pre-processing
      val toPreprocessIt = toPreprocess.iterator
      Out.trace("## Preprocess BEGIN")
      while (toPreprocessIt.hasNext) {
        val todo = toPreprocessIt.next()
        Out.trace(s"# Process: ${todo.pretty(sig)}")
        val result0 = preprocess(todo)(state)
        Out.trace(s"# Result:\n\t${result0.map {_.pretty(sig)}.mkString("\n\t")}")
        val result = result0.filterNot(cw => Clause.trivial(cw.cl))
        myAssert(result.forall(cl => Clause.wellTyped(cl.cl)),
          s"[SeqLoop] Not well-typed: ${result.filterNot(cl => Clause.wellTyped(cl.cl)).map(_.id).mkString(",")}")
        Control.addUnprocessed(result)
        if (toPreprocessIt.hasNext) Out.trace("--------------------")
      }
      Out.trace("## Preprocess END")
      // Debug output
      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.finest(s"Clauses and maximal literals of them:")
        for (c <- state.unprocessed) {
          Out.finest(s"Clause ${c.pretty(sig)}")
          Out.finest(s"Maximal literal(s):")
          Out.finest(s"\t${c.cl.maxLits.map(_.pretty(sig)).mkString("\n\t")}")
        }
        Out.finest(s"################")
      }

      /////////////////////////////////////////
      // Main proof loop
      /////////////////////////////////////////
      var loop = true
      Out.debug("## Reasoning loop BEGIN")
      while (loop && !prematureCancel(state.noProcessedCl)) {
        Interaction(state)

        if (!Configuration.GUIDED && System.currentTimeMillis() - startTime > 1000 * timeout) {
          loop = false
          state.setSZSStatus(SZS_Timeout)
        } else if (!state.unprocessedLeft) {
          loop = false
          state.setSZSStatus(SZS_GaveUp)
        } else {
          // No cancel, do reasoning step
          val extRes = Control.checkExternalResults(state)
          if (extRes.nonEmpty) {
            val extRes0 = extRes.filter(res => endgameAnswer(res.szsStatus))
            if (extRes0.nonEmpty) {
              val extResAnswers = extRes0.head
              assert(extResAnswers.szsStatus == SZS_Unsatisfiable, "other than UNS was trapped")
              // CounterSat cannot happen since we do not send a conjecture
              // Theorem ditto
              loop = false
              val emptyClause = AnnotatedClause(Clause.empty, extCallInference(extResAnswers.proverName, extResAnswers.problem))
              endplay(emptyClause, state)
            }
          } else {
            var cur = state.nextUnprocessed
            // cur is the current AnnotatedClause
            Out.debug(s"[SeqLoop] Taken: ${cur.pretty(sig)}")
            Out.trace(s"[SeqLoop] Maximal: ${cur.cl.maxLits.map(_.pretty(sig)).mkString("\n\t")}")

            cur = Control.rewriteSimp(cur, state.rewriteRules)
            /* To equality if possible */
            cur = Control.liftEq(cur)

            // Check if `cur` is an empty clause
            if (Clause.effectivelyEmpty(cur.cl)) {
              loop = false
              endplay(cur, state)
            } else {
              // Not an empty clause, detect choice definition or do reasoning step.
              val isChoiceSpec = Control.detectChoiceClause(cur)
              if (isChoiceSpec) {
                leo.Out.debug(s"[SeqLoop] Removed Choice: ${cur.id}")
              } else {
                // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
                // e.g. by forward subsumption
                if (!Control.redundant(cur, state.processed)) {
                  Control.submit(state.processed, state)
                  if(mainLoopInferences(cur)(state)) loop = false
                  state.incProofLoopCount()
                } else {
                  Out.debug(s"[SeqLoop] Redundant: ${cur.id}")
                  state.incForwardSubsumedCl()
                }
              }
            }
          }
        }
      }

      /////////////////////////////////////////
      // Main loop terminated, check if any prover result is pending
      /////////////////////////////////////////

      if (state.szsStatus == SZS_Unknown && System.currentTimeMillis() - startTime <= 1000 * timeout && Configuration.ATPS.nonEmpty) {
        if (!ExtProverControl.openCallsExist) {
          Control.submit(state.processed, state, force = true)
          Out.info(s"[ExtProver] We still have time left, try a final call to external provers...")
        } else Out.info(s"[ExtProver] External provers still running, waiting for termination within timeout...")
        var wait = true
        while (wait && System.currentTimeMillis() - startTime <= 1000 * timeout && ExtProverControl.openCallsExist) {
          Out.finest(s"[ExtProver] Check for answer")
          val extRes = Control.checkExternalResults(state)
          if (extRes.nonEmpty) {
            Out.debug(s"[ExtProver] Got answer(s)! ${extRes.map(_.szsStatus.pretty).mkString(",")}")
            val unSatAnswers = extRes.filter(_.szsStatus == SZS_Unsatisfiable)
            if (unSatAnswers.nonEmpty) {
              val extRes0 = unSatAnswers.head
              wait = false
              val emptyClause = AnnotatedClause(Clause.empty, extCallInference(extRes0.proverName, extRes0.problem))
              endplay(emptyClause, state)
            } else if (System.currentTimeMillis() - startTime <= 1000 * timeout && ExtProverControl.openCallsExist) {
              Out.info(s"[ExtProver] Still waiting ...")
              Thread.sleep(5000)
            }
          } else {
            if (System.currentTimeMillis() - startTime <= 1000 * timeout && ExtProverControl.openCallsExist) {
              Out.info(s"[ExtProver] Still waiting ...")
              Thread.sleep(5000)
            }
          }

        }
        if (wait) Out.info(s"No helpful answer from external systems within timeout. Terminating ...")
        else Out.info(s"Helpful answer from external systems within timeout. Terminating ...")
      }

      if (endgameAnswer(state.szsStatus)) true
      else false
    } catch {
      case e:Exception =>
        Out.severe(s"Signature used:\n${leo.modules.signatureAsString(state.signature)}")
        throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  private final def mainLoopInferences(cur: AnnotatedClause)(implicit state: LocalState): Boolean = {
    implicit val sig: Signature = state.signature
    var newclauses: Set[AnnotatedClause] = Set.empty

    /////////////////////////////////////////
    // Backward simplification BEGIN
    /////////////////////////////////////////
    /* Subsumption */
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)
    if (backSubsumedClauses.nonEmpty) {
      Out.trace(s"[SeqLoop] ${cur.id} subsumes processed clauses")
      state.incBackwardSubsumedCl(backSubsumedClauses.size)
      Out.finest(s"[SeqLoop] Processed subsumed:" +
        s"\n\t${backSubsumedClauses.map(_.pretty(sig)).mkString("\n\t")}")
      Control.removeProcessed(backSubsumedClauses)
    }
    myAssert(!leo.modules.calculus.FullCNF.canApply(cur.cl), s"[SeqLoop] Not in CNF: ${cur.pretty(sig)}")
    /** Add to processed and to indexes. */
    state.addProcessed(cur)
    Control.insertIndexed(cur)
    /* Add rewrite rules to set */
    if (Clause.unit(cur.cl)) {
      if (Clause.rewriteRule(cur.cl)) {
        Out.trace(s"[SeqLoop] Clause ${cur.id} added as rewrite rule.")
        state.addRewriteRule(cur)
      } else {
        Out.trace(s"[SeqLoop] Clause ${cur.id} added as (non-rewrite) unit.")
        state.addNonRewriteUnit(cur)
      }
    }
    /////////////////////////////////////////
    // Backward simplification END
    /////////////////////////////////////////
    /////////////////////////////////////////
    // Generating inferences BEGIN
    /////////////////////////////////////////
    /* Functional Extensionality */
    val funcext_result = Control.funcExtNew(cur)(state)
    newclauses = newclauses union funcext_result
    state.addToHotList(funcext_result)

    /* Boolean Extensionality */
    val boolext_result = Control.boolext(cur)(state)
    newclauses = newclauses union boolext_result

    /* paramodulation where at least one involved clause is `cur` */
    val paramod_result = Control.paramodSet(cur, state.processed)(state)
    newclauses = newclauses union paramod_result

    /* Equality factoring of `cur` */
    val factor_result = Control.factor(cur)(state)
    newclauses = newclauses union factor_result

    /* Prim subst */
    val primSubst_result = Control.primsubst(cur)(state)
    newclauses = newclauses union primSubst_result

    /* Replace defined equalities */
    newclauses = newclauses union Control.convertDefinedEqualities(newclauses)
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(Control.liftEq)

    /* guess functions for those not solved by unification */
    val funspec_result = Control.guessFuncSpec(Set(cur))(state)
    newclauses = newclauses union funspec_result

    val choice_result = Control.instantiateChoice(cur)
    state.incChoiceInstantiations(choice_result.size)
    newclauses = newclauses union choice_result
    /////////////////////////////////////////
    // Generating inferences END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // Simplification of newly generated clauses BEGIN
    /////////////////////////////////////////
    /* Simplify new clauses */
    //    newclauses = Control.shallowSimpSet(newclauses)
    /* Remove those which are tautologies */
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /* Pre-unify new clauses */
    newclauses = Control.unifyNewClauses(newclauses)(state)

    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(Control.cnf)
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(cw => Control.shallowSimp(Control.liftEq(cw)))
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /////////////////////////////////////////
    // Simplification of newly generated clauses END
    /////////////////////////////////////////
    //    Control.updateDescendants(cur, newclauses)
    /////////////////////////////////////////
    state.incGeneratedCl(newclauses.size)
    Out.debug(s"[SeqLoop] Generated: ${newclauses.map(_.id).mkString(",")}")
    // At the end, for each generated clause add to unprocessed,
    // eagly look for the empty clause
    // and return true if found.
    /////////////////////////////////////////
    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      val newCl = newIt.next()
      assert(Clause.wellTyped(newCl.cl), s"[SeqLoop] Clause [${newCl.id}] is not well-typed")
      if (Clause.effectivelyEmpty(newCl.cl)) {
        endplay(newCl, state)
        return true
      } else {
        if (!Clause.trivial(newCl.cl) && !state.processed.exists(_.cl == newCl.cl)) Control.addUnprocessed(newCl)
        else Out.debug(s"[SeqLoop] Trivial, hence dropped: ${newCl.pretty(sig)}")
      }
    }
    false
  }

  final def printResult(state: LocalState, startTime: Long, startTimeWOParsing: Long): Unit = {
    implicit val sig: Signature = state.signature
    /////////////////////////////////////////
    // All finished, print result
    /////////////////////////////////////////
    import leo.modules.{axiomsInProof, userSignatureToTPTP, symbolsInProof, compressedProofOf, proofToTPTP, userDefinedSignatureAsString}

    val time = System.currentTimeMillis() - startTime
    val timeWOParsing = System.currentTimeMillis() - startTimeWOParsing

    /* Output additional information about the reasoning process. */
    Out.comment(s"Time passed: ${time}ms")
    Out.comment(s"Effective reasoning time: ${timeWOParsing}ms")
    if (state.szsStatus == SZS_Theorem) Out.comment(s"Solved by ${state.runStrategy.pretty}")
    val proof = state.proof
    if (proof != null) {
      try {
        import leo.datastructures.ClauseAnnotation.FromFile
        val proofAx = axiomsInProof(proof)
        Out.comment(s"Axioms used in derivation (${proofAx.size}): ${proofAx.map(_.annotation.asInstanceOf[FromFile].formulaName).mkString(", ")}")
      } catch {
        case e:Exception => ()
      }
    }
    Out.comment(s"No. of processed clauses: ${state.processed.size}")
    Out.comment(s"No. of generated clauses: ${state.noGeneratedCl}")
    Out.comment(s"No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
    Out.comment(s"No. of backward subsumed clauses: ${state.noBackwardSubsumedCl}")
    Out.comment(s"No. of rewrite rules in store: ${state.rewriteRules.size}")
    Out.comment(s"No. of other units in store: ${state.nonRewriteUnits.size}")
    Out.comment(s"No. of choice functions detected: ${state.choiceFunctionCount}")
    Out.comment(s"No. of choice instantiations: ${state.choiceInstantiations}")
    Out.debug(s"literals processed: ${state.processed.flatMap(_.cl.lits).size}")
    Out.debug(s"-thereof maximal ones: ${state.processed.flatMap(c => c.cl.maxLits).size}")
    Out.debug(s"avg. literals per clause: ${state.processed.flatMap(_.cl.lits).size / state.processed.size.toDouble}")
    Out.debug(s"avg. max. literals per clause: ${state.processed.flatMap(c => c.cl.maxLits).size / state.processed.size.toDouble}")
    Out.debug(s"oriented processed: ${state.processed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"oriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"unoriented processed: ${state.processed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"unoriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"subsumption tests: ${leo.modules.calculus.Subsumption.subsumptiontests}")
    Out.debug(s"No. of subsumed descendants deleted: ${state.noDescendantsDeleted}")

    Out.finest("#########################")
    Out.finest("units")
    import leo.modules.calculus.PatternUnification.isPattern
    Out.finest(state.rewriteRules.map(cl => s"(${isPattern(cl.cl.lits.head.left)}/${isPattern(cl.cl.lits.head.right)}): ${cl.pretty(sig)}").mkString("\n\t"))
    Out.finest("#########################")
    Out.finest("#########################")
    Out.finest("#########################")
    Out.finest("Processed unoriented")
    Out.finest("#########################")
    Out.finest(state.processed.flatMap(_.cl.lits).filter(!_.oriented).map(_.pretty(sig)).mkString("\n\t"))
    Out.finest("#########################")
    Out.finest("#########################")
    Out.finest("#########################")
    Out.finest("Unprocessed unoriented")
    Out.finest(state.unprocessed.flatMap(_.cl.lits).filter(!_.oriented).map(_.pretty(sig)).mkString("\n\t"))
    Out.finest("#########################")

    Out.finest("Signature extension used:")
    Out.finest(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
    Out.finest(userDefinedSignatureAsString(sig))
    Out.finest("Clauses at the end of the loop:")
    Out.finest("\t" + state.processed.toSeq.sortBy(_.cl.lits.size).map(_.pretty(sig)).mkString("\n\t"))

    Out.output(SZSOutput(state.szsStatus, Configuration.PROBLEMFILE, s"$time ms resp. $timeWOParsing ms w/o parsing"))
    /* Print proof object if possible and requested. */
    if (Configuration.PROOF_OBJECT && proof != null) {
      try {
        Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
        Out.output(userSignatureToTPTP(symbolsInProof(proof))(sig))
        if (Configuration.isSet("compressProof")) Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(state.derivationClause.get)))
        else Out.output(proofToTPTP(proof))
        Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
      } catch {
        case e: Exception => Out.comment("Translation of proof object failed. See error logs for details.")
          Out.warn(e.toString)
      }
    }
  }

  @inline final private def endplay(emptyClause: AnnotatedClause, state: LocalState): Unit = {
    import leo.modules.{proofOf, conjInProof}
    state.setDerivationClause(emptyClause)
    val proof = proofOf(emptyClause)
    state.setProof(proof)

    if (state.conjecture == null) state.setSZSStatus(SZS_Unsatisfiable)
    else {
      if (conjInProof(proof)) state.setSZSStatus(SZS_Theorem)
      else state.setSZSStatus(SZS_ContradictoryAxioms)
    }
  }

  final private def endgameAnswer(result: StatusSZS): Boolean = {
    result match {
      case SZS_CounterSatisfiable | SZS_Theorem | SZS_Unsatisfiable => true
      case _ => false
    }
  }

  @inline final def prematureCancel(counter: Int): Boolean = {
    try {
      val limit: Int = Configuration.valueOf("ll").get.head.toInt
      counter >= limit
    } catch {
      case _: NumberFormatException => false
      case _: NoSuchElementException => false
    }
  }
}
