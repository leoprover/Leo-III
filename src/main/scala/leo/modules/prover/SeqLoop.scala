package leo.modules.prover

import leo.{Configuration, Out}
import leo.datastructures._
import leo.modules.SZSOutput
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
  protected[modules] final def preprocess(state: LocalState, cur: AnnotatedClause): Set[AnnotatedClause] = {
    implicit val sig: Signature = state.signature
    var result: Set[AnnotatedClause] = Set()

    // Fresh clause, that means its unit and nonequational
    assert(Clause.unit(cur.cl), "clause not unit")
    val lit = cur.cl.lits.head
    assert(!lit.equational, "initial literal equational")

    // Def expansion and simplification
    val expanded = Control.expandDefinitions(cur)
    val polarityswitchedAndExpanded = Control.switchPolarity(expanded)
    // We may instantiate here special symbols for universal variables
    // Its BEFORE miniscope because their are less quantifiers and maybe
    // some universal quantification may vanish after extensional instantiation
    // Run simp here again to eliminate connectives with true/false as operand due
    // to ext. instantiation.
    result = Control.specialInstances(polarityswitchedAndExpanded)

    result = result.flatMap { cl =>
      Control.cnf(Control.miniscope(cl))
    }

    result = result.map {cl =>
      leo.Out.trace(s"[Choice] Search for instance in ${cl.id}")
      val isChoiceSpec = Control.detectChoiceClause(cl)(state)
      if (isChoiceSpec) {
        // replace clause by a trivial one: [[true]^t]
        leo.Out.debug(s"[Choice] Removed ${cl.id}")
        import leo.modules.HOLSignature.LitTrue
        AnnotatedClause(leo.modules.termToClause(LitTrue), ClauseAnnotation.FromSystem("redundant"))
      } else cl
    }
    // Add detected equalities as primitive ones
    result = result union Control.convertDefinedEqualities(result)

    // To equation if possible and then apply func ext
    // AC Simp if enabled, then Simp.
    result = result.map { cl =>
      var result = cl
      result = Control.liftEq(result)
      result = Control.funcext(result) // Maybe comment out? why?
    val possiblyAC = Control.detectAC(result)
      if (possiblyAC.isDefined) {
        val symbol = possiblyAC.get._1
        val spec = possiblyAC.get._2
        val sig = state.signature
        val oldProp = sig(symbol).flag
        if (spec) {
          Out.trace(s"[AC] A/C specification detected: ${result.id} is an instance of commutativity")
          sig(symbol).updateProp(addProp(Signature.PropCommutative, oldProp))
        } else {
          Out.trace(s"[AC] A/C specification detected: ${result.id} is an instance of associativity")
          sig(symbol).updateProp(addProp(Signature.PropAssociative, oldProp))
        }
      }
      result = Control.acSimp(result)
      result = Control.simp(result)
      if (!state.isPolymorphic && result.cl.typeVars.nonEmpty) state.setPolymorphic()
      result
    }
    // Pre-unify new clauses or treat them extensionally and remove trivial ones
    result = Control.extPreprocessUnify(result)(state)
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
    state.setRunStrategy(Control.defaultStrategy(timeout))

    try {
      // Check if external provers were defined
      if (Configuration.ATPS.nonEmpty) {
        import leo.modules.external.ExternalProver
        Configuration.ATPS.foreach { case (name, path) =>
          try {
            val p = ExternalProver.createProver(name, path)
            state.addExternalProver(p)
            leo.Out.info(s"$name registered as external prover.")
            leo.Out.info(s"$name timeout set to:${Configuration.ATP_TIMEOUT(name)}.")
          } catch {
            case e: NoSuchElementException => leo.Out.warn(e.getMessage)
          }
        }
      }
      // Read problem from file
      val input2 = Input.parseProblem(Configuration.PROBLEMFILE)
      val startTimeWOParsing = System.currentTimeMillis()
      // Split input in conjecture/definitions/axioms etc.
      val remainingInput: Seq[AnnotatedClause] = effectiveInput(input2, state)
      // Typechecking: Throws and exception if not well-typed
      typeCheck(remainingInput, state)
      Out.info(s"Type checking passed. Searching for refutation ...")
      run(state, remainingInput, startTime)
      printResult(state, startTime, startTimeWOParsing)
    } catch {
      case e:Exception =>
        Out.severe(s"Signature used:\n${leo.modules.signatureAsString(sig)}")
        e.printStackTrace()
        throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  final def run(state: State[AnnotatedClause], input: Seq[AnnotatedClause],
               startTime: Long): Boolean = {
    try {
      implicit val sig: Signature = state.signature
      implicit val stateImplicit = state
      val timeout0 = state.runStrategy.timeout
      val timeout = if (timeout0 == 0) Float.PositiveInfinity else timeout0

      // Preprocessing Conjecture
      if (state.negConjecture != null) {
        // Expand conj, Initialize indexes
        // We expand here already since we are interested in all symbols (possibly contained within defined symbols)
        Out.debug("## Preprocess Neg.Conjecture BEGIN")
        Out.trace(s"Neg. conjecture: ${state.negConjecture.pretty(sig)}")
        val simpNegConj = Control.expandDefinitions(state.negConjecture)
        state.defConjSymbols(simpNegConj)
        state.initUnprocessed()
        Control.initIndexes(simpNegConj +: input)
        val result = preprocess(state, simpNegConj).filterNot(cw => Clause.trivial(cw.cl))
        Out.debug(s"# Result:\n\t${
          result.map {
            _.pretty(sig)
          }.mkString("\n\t")
        }")
        Out.trace("## Preprocess Neg.Conjecture END")
        state.addUnprocessed(result)
        // Save initial pre-processed set as auxiliary set for ATP calls (if existent)
        if (state.externalProvers.nonEmpty) {
          state.addInitial(result)
        }
      } else {
        // Initialize indexes
        state.initUnprocessed()
        Control.initIndexes(input)
      }

      // Preprocessing
      Out.debug("## Preprocess BEGIN")
      val preprocessIt = input.iterator
      while (preprocessIt.hasNext) {
        val cur = preprocessIt.next()
        Out.trace(s"# Process: ${cur.pretty(sig)}")
        val processed = preprocess(state, cur)
        Out.debug(s"# Result:\n\t${
          processed.map {
            _.pretty(sig)
          }.mkString("\n\t")
        }")
        val preprocessed = processed.filterNot(cw => Clause.trivial(cw.cl))
        state.addUnprocessed(preprocessed)
        if (state.externalProvers.nonEmpty) {
          state.addInitial(preprocessed)
        }
        if (preprocessIt.hasNext) Out.trace("--------------------")
      }
      Out.trace("## Preprocess END\n\n")
      assert(state.unprocessed.forall(cl => Clause.wellTyped(cl.cl)), s"Not well typed:\n\t${state.unprocessed.filterNot(cl => Clause.wellTyped(cl.cl)).map(_.pretty(sig)).mkString("\n\t")}")
      // Debug output
      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.finest(s"Clauses and maximal literals of them:")
        for (c <- state.unprocessed) {
          Out.finest(s"Clause ${c.pretty(sig)}")
          Out.finest(s"Maximal literal(s):")
          Out.finest(s"\t${Literal.maxOf(c.cl.lits).map(_.pretty(sig)).mkString("\n\t")}")
        }
      }
      Out.finest(s"################")

      /////////////////////////////////////////
      // Main proof loop
      /////////////////////////////////////////
      var loop = true
      Out.debug("## Reasoning loop BEGIN")
      while (loop && !prematureCancel(state.noProcessedCl)) {
        state.incProofLoopCount()
        if (System.currentTimeMillis() - startTime > 1000 * timeout) {
          loop = false
          state.setSZSStatus(SZS_Timeout)
        } else if (!state.unprocessedLeft) {
          loop = false
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
            Out.trace(s"[SeqLoop] Maximal: ${Literal.maxOf(cur.cl.lits).map(_.pretty(sig)).mkString("\n\t")}")

            cur = Control.rewriteSimp(cur, state.rewriteRules)
            /* Functional Extensionality */
            cur = Control.funcext(cur)
            /* To equality if possible */
            cur = Control.liftEq(cur)

            val curCNF = Control.cnf(cur)
            if (curCNF.size == 1 && curCNF.head == cur) {
              // No CNF step, do main loop inferences
              // Check if `cur` is an empty clause
              if (Clause.effectivelyEmpty(cur.cl)) {
                loop = false
                endplay(cur, state)
              } else {
                // Not an empty clause, detect choice definition or do reasoning step.
                val isChoiceSpec = Control.detectChoiceClause(cur)
                if (isChoiceSpec) {
                  leo.Out.debug(s"[SeqLoop] Removed ${cur.id} (Choice Spec)")
                } else {
                  // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
                  // e.g. by forward subsumption
                  if (!Control.redundant(cur, state.processed)) {
                    Control.submit(state.processed, state)
                    if(mainLoopInferences(cur, state)) loop = false
                  } else {
                    Out.debug(s"[SeqLoop] Clause ${cur.id} redundant, skipping.")
                    state.incForwardSubsumedCl()
                  }
                }
              }
            } else {
              state.addUnprocessed(curCNF)
            }
          }
        }
      }

      /////////////////////////////////////////
      // Main loop terminated, check if any prover result is pending
      /////////////////////////////////////////

      if (state.szsStatus == SZS_Unknown && System.currentTimeMillis() - startTime <= 1000 * timeout && Configuration.ATPS.nonEmpty) {
        if (!ExtProverControl.openCallsExist) {
          Control.submit(state.processed, state)
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

  private final def mainLoopInferences(cur: AnnotatedClause, state: LocalState): Boolean = {
    implicit val sig: Signature = state.signature
    implicit val stateImpl = state
    var newclauses: Set[AnnotatedClause] = Set()

    /////////////////////////////////////////
    // Backward simplification BEGIN
    /////////////////////////////////////////
    /* Subsumption */
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)
    if (backSubsumedClauses.nonEmpty) {
      Out.trace(s"[Redundancy] ${cur.id} subsumes processed clauses")
      state.incBackwardSubsumedCl(backSubsumedClauses.size)
      Out.finest(s"[Redundancy] Processed subsumed:" +
        s"\n\t${backSubsumedClauses.map(_.pretty(sig)).mkString("\n\t")}")
      // Remove from processed set, from indexes etc.
      state.removeProcessed(backSubsumedClauses)
      state.removeUnits(backSubsumedClauses)
      Control.removeFromIndex(backSubsumedClauses)
      //      // Remove all direct descendants of clauses in `bachSubsumedClauses` from unprocessed
      //      val descendants = Control.descendants(backSubsumedClauses)
      //      state.incDescendantsDeleted(descendants.size)
      //      state.removeUnprocessed(descendants)
    }
    assert(!cur.cl.lits.exists(leo.modules.calculus.FullCNF.canApply), s"\n\tcl ${cur.pretty(sig)} not in cnf")
    /** Add to processed and to indexes. */
    state.addProcessed(cur)
    Control.insertIndexed(cur)
    /* Add rewrite rules to set */
    if (Clause.unit(cur.cl)) {
      if (Clause.rewriteRule(cur.cl)) {
        Out.trace(s"Clause ${cur.id} added as rewrite rule.")
        state.addRewriteRule(cur)
      } else {
        Out.trace(s"Clause ${cur.id} added as (non-rewrite) unit.")
        state.addNonRewriteUnit(cur)
      }
    }
    /////////////////////////////////////////
    // Backward simplification END
    /////////////////////////////////////////
    /////////////////////////////////////////
    // Generating inferences BEGIN
    /////////////////////////////////////////
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
//    val funspec_result = Control.guessFuncSpec(Set(cur))(state)
//    newclauses = newclauses union funspec_result

    val choice_result = Control.instantiateChoice(cur)
    state.incChoiceInstantiations(choice_result.size)
    newclauses = newclauses union choice_result
    /////////////////////////////////////////
    // Generating inferences END
    /////////////////////////////////////////

    /////////////////////////////////////////
    // Simplification of newly generated clauses BEGIN
    /////////////////////////////////////////
    state.incGeneratedCl(newclauses.size)
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
    /////////////////////////////////////////
    // Simplification of newly generated clauses END
    /////////////////////////////////////////
    //    Control.updateDescendants(cur, newclauses)
    /////////////////////////////////////////
    // At the end, for each generated clause add to unprocessed,
    // eagly look for the empty clause
    // and return true if found.
    /////////////////////////////////////////
    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      val newCl = newIt.next()
      assert(Clause.wellTyped(newCl.cl), s"Clause [${newCl.id}] is not well-typed")
      if (Clause.effectivelyEmpty(newCl.cl)) {
        endplay(newCl, state)
        return true
      } else {
        if (!Clause.trivial(newCl.cl)) state.addUnprocessed(newCl)
        else Out.trace(s"Trivial, hence dropped: ${newCl.pretty(sig)}")
      }
    }
    false
  }

  final def printResult(state: LocalState, startTime: Long, startTimeWOParsing: Long): Unit = {
    implicit val sig: Signature = state.signature
    /////////////////////////////////////////
    // All finished, print result
    /////////////////////////////////////////
    import leo.modules.{proofOf, axiomsInProof, userSignatureToTPTP, symbolsInProof, compressedProofOf, proofToTPTP, userDefinedSignatureAsString}

    val time = System.currentTimeMillis() - startTime
    val timeWOParsing = System.currentTimeMillis() - startTimeWOParsing

    Out.output("")
    Out.output(SZSOutput(state.szsStatus, Configuration.PROBLEMFILE, s"$time ms resp. $timeWOParsing ms w/o parsing"))

    /* Output additional information about the reasoning process. */
    Out.comment(s"Time passed: ${time}ms")
    Out.comment(s"Effective reasoning time: ${timeWOParsing}ms")
    //      Out.comment(s"Thereof preprocessing: ${preprocessTime}ms")
    val proof = if (state.derivationClause.isDefined) proofOf(state.derivationClause.get) else null
    if (proof != null)
      Out.comment(s"No. of axioms used: ${axiomsInProof(proof).size}")
    Out.comment(s"No. of processed clauses: ${state.noProcessedCl}")
    Out.comment(s"No. of generated clauses: ${state.noGeneratedCl}")
    Out.comment(s"No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
    Out.comment(s"No. of backward subsumed clauses: ${state.noBackwardSubsumedCl}")
    Out.comment(s"No. of subsumed descendants deleted: ${state.noDescendantsDeleted}")
    Out.comment(s"No. of rewrite rules in store: ${state.rewriteRules.size}")
    Out.comment(s"No. of other units in store: ${state.nonRewriteUnits.size}")
    Out.comment(s"No. of choice functions detected: ${state.choiceFunctionCount}")
    Out.comment(s"No. of choice instantiations: ${state.choiceInstantiations}")
    Out.debug(s"literals processed: ${state.processed.flatMap(_.cl.lits).size}")
    Out.debug(s"-thereof maximal ones: ${state.processed.flatMap(c => Literal.maxOf(c.cl.lits)).size}")
    Out.debug(s"avg. literals per clause: ${state.processed.flatMap(_.cl.lits).size / state.processed.size.toDouble}")
    Out.debug(s"avg. max. literals per clause: ${state.processed.flatMap(c => Literal.maxOf(c.cl.lits)).size / state.processed.size.toDouble}")
    Out.debug(s"oriented processed: ${state.processed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"oriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(_.oriented)}")
    Out.debug(s"unoriented processed: ${state.processed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"unoriented unprocessed: ${state.unprocessed.flatMap(_.cl.lits).count(!_.oriented)}")
    Out.debug(s"subsumption tests: ${leo.modules.calculus.Subsumption.subsumptiontests}")

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

    /* Print proof object if possible and requested. */
    if ((state.szsStatus == SZS_Theorem || state.szsStatus == SZS_Unsatisfiable) && Configuration.PROOF_OBJECT && proof != null) {
      Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
      Out.output(userSignatureToTPTP(symbolsInProof(proof))(sig))
      if (Configuration.isSet("compressProof")) Out.output(proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(state.derivationClause.get)))
      else Out.output(proofToTPTP(proof))
      Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
    }
  }

  @inline final private def endplay(emptyClause: AnnotatedClause, state: LocalState): Unit = {
    if (state.conjecture == null) state.setSZSStatus(SZS_Unsatisfiable)
    else state.setSZSStatus(SZS_Theorem)
    state.setDerivationClause(emptyClause)
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
