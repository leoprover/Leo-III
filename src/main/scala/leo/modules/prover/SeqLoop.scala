package leo.modules.prover

import leo.{Configuration, Out}
import leo.datastructures._
import leo.datastructures.TPTP.AnnotatedFormula
import leo.modules.{SZSOutput, SZSResult, myAssert}
import leo.modules.control.Control
import leo.modules.input.ProblemStatistics
import leo.modules.output._

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
  protected[modules] final def preprocess(cur: AnnotatedClause)(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = {
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
      val simp = Control.cheapSimp(Control.liftEq(cl))
      Control.detectAC(simp)
      Control.detectDomainConstraint(simp)
      Control.detectInjectivity(simp)
      if (!state.isPolymorphic && simp.cl.typeVars.nonEmpty) state.setPolymorphic() // TODO: FIXME
      simp
    }
    // Pre-unify new clauses or treat them extensionally and remove trivial ones
//    result = Control.extPreprocessUnify(result)(state)
    //result = Control.cheapSimpSet(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }

  ////////////////////////////////////
  //// Main-loop operations
  ////////////////////////////////////

  /* Main function containing proof loop */
  final def apply(startTime: Long, timeout: Int, parsedProblem: scala.Seq[AnnotatedFormula], stats: ProblemStatistics): Unit = {
    val startTimeWOParsing = System.currentTimeMillis()
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up, etc.
    /////////////////////////////////////////
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    state.setProblemStatistics(stats)
    state.setTimeout(timeout)

    try {
      // Check if external provers were defined
      if (Configuration.ATPS.nonEmpty) Control.registerExtProver(Configuration.ATPS)(state)
      // Split input in conjecture/definitions/axioms etc.
      val remainingInput: Seq[AnnotatedClause] =
        if (Configuration.isSet("sine")) effectiveInputNew(parsedProblem, state) else effectiveInput(parsedProblem, state)
      // Typechecking: Throws and exception if not well-typed
      typeCheck(remainingInput, state)
      Out.info(s"Type checking passed.")
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
      Out.config(s"Using configuration: timeout($timeout) with ${state.runStrategy.pretty}.  Searching for refutation ...")
      run(remainingInput, startTime)(state)
      printResult(state, startTime, startTimeWOParsing)
    } catch {
      case e:Exception =>
        Out.trace(s"Signature used:\n${leo.modules.signatureAsString(sig)}")
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
      val timeout: Float = if (timeout0 == 0) Float.PositiveInfinity else timeout0.toFloat
      // Initialize indexes
      state.initUnprocessed()
      val toPreprocess = if (state.negConjecture.nonEmpty) {
        val expandedNegatedConjectures = state.negConjecture.map(c => Control.expandDefinitions(c))
        state.defConjSymbols(expandedNegatedConjectures)
        val input0 = expandedNegatedConjectures.toSeq ++ input
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
      /////////////////////////////////////////
      // Main loop start
      /////////////////////////////////////////
      mainLoop(timeout, startTime)(state)
      /////////////////////////////////////////
      // Main loop terminated, check if any prover result is pending
      /////////////////////////////////////////
      if (successSZS(state.szsStatus)) true
      else {
        if (state.szsStatus == SZS_Timeout) false
        else if (state.szsStatus == SZS_GaveUp) {
          // If terminated with GaveUp and there is time left, try less restrictive strategy
          leo.Out.info(s"GaveUp with current strategy, trying again with another strategy ...")
          state.setRunStrategy(RunStrategy.funcspec)
          Configuration.overrideOrdering = state.runStrategy.ordering
          val clauses = state.processed
          state.removeProcessed(clauses)
          val clauses0 = clauses.map(c => AnnotatedClause(Clause(c.cl.lits.map(l => Literal.mkLit(l.left, l.right, l.polarity))), c.role, c.annotation, c.properties))
          state.addUnprocessed(clauses0)
          mainLoop(timeout, startTime)(state)
          successSZS(state.szsStatus)
        } else {
          // Try something else, todo...
          Control.despairSubmit(startTime, timeout)(state)
          successSZS(state.szsStatus)
        }
      }
    } catch {
      case e:Exception =>
        Out.severe(s"Signature used:\n${leo.modules.signatureAsString(state.signature)}")
        throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  private[this] final def mainLoop(timeout: Float, startTime: Long)(implicit state: LocalState): Unit = {
    implicit val sig: Signature = state.signature
    var loop = true
    Out.debug("## Reasoning loop BEGIN")
    while (loop && !prematureCancel(state.noProofLoops)) {
      Interaction(state)

      if (!Configuration.GUIDED && System.currentTimeMillis() - startTime > 1000 * timeout) {
        loop = false
        state.setSZSStatus(SZS_Timeout)
      } else if (!state.unprocessedLeft) {
        loop = false
        if (isSatisfiable(state.processed)(state)) state.setSZSStatus(appropriateSatStatus(state))
        else state.setSZSStatus(SZS_GaveUp)
      } else {
        // No cancel, do reasoning step
        val extRes = Control.checkExternalResults(state)
        if (extRes.nonEmpty) {
          val extRes0 = extRes.filter(endgameResult)
          if (extRes0.nonEmpty) {
            loop = false
            val extResAnswer = extRes0.head
            if (extResAnswer.szsStatus == SZS_Unsatisfiable) {
              val emptyClause = AnnotatedClause(Clause.empty,
                extCallInference(extResAnswer.prover.name,
                  extResAnswer.problem))
              endplay(emptyClause, state)
            } else {
              endplay(null, state)
            }
            Out.comment(s"External prover '${extResAnswer.prover.name}' found a proof!")
          }
        } else {
          var cur = state.nextUnprocessed
          // cur is the current AnnotatedClause
          Out.debug(s"[SeqLoop] ------------ next iteration (#${state.noProofLoops+1}) ------------")
          Out.debug(s"[SeqLoop] Taken: ${cur.pretty(sig)}")
          Out.trace(s"[SeqLoop] Maximal: ${cur.cl.maxLits.map(_.pretty(sig)).mkString("\n\t")}")

          /* Full simp with rewriting and stuff */
          cur = Control.liftEq(Control.simp(cur))
          val curCNF = Control.cnf(cur)
          if (curCNF.size == 1 && curCNF.head == cur) {
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
          } else {
            Control.addUnprocessed(curCNF)
          }
        }
      }
    }
  }

  private[this] final def mainLoopInferences(cur: AnnotatedClause)(implicit state: LocalState): Boolean = {
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
    val (simplifiedProcessed,affected) = Control.rewritable(state.processed, cur)
    newclauses = newclauses union simplifiedProcessed
    Control.removeProcessed(affected)
    assert(!leo.modules.calculus.FullCNF.canApply(cur.cl), s"[SeqLoop] Not in CNF: ${cur.pretty(sig)}")
    /** Add to processed and to indexes. */
    state.addProcessed(cur)
    Control.insertIndexed(cur)
    /* Recognize rewrite rules or other units */
    Control.detectUnit(cur)
    /////////////////////////////////////////
    // Backward simplification END
    /////////////////////////////////////////
    /////////////////////////////////////////
    // Generating inferences BEGIN
    /////////////////////////////////////////
    val detUni_result = Control.detUniInferences(cur)(state)
    newclauses = newclauses union detUni_result
    val exhaustUni_result = Control.generalUnify(cur)(state)
    newclauses = newclauses union exhaustUni_result
    /* Functional Extensionality */
    val funcext_result = Control.funcExtNew(cur)(state)
    newclauses = newclauses union funcext_result
//    state.addToHotList(funcext_result)

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
    // Simplification/unification of newly generated clauses BEGIN
    /////////////////////////////////////////
    /* Remove those which are tautologies */
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /* Pre-unify new clauses */
    newclauses = Control.unifyNewClauses(newclauses)(state)

    /* exhaustively: */
    /* (1) CNF (2) replace eq symbols on top-level by equational literals and */
    /* (3) rewriting */
    newclauses = Control.exhaustiveCnfSimpSet(newclauses)

    /* Remove trivial clauses eagerly */
    newclauses = newclauses.filterNot(cw => Clause.trivial(cw.cl))
    /////////////////////////////////////////
    // Simplification of newly generated clauses END
    /////////////////////////////////////////
    //    Control.updateDescendants(cur, newclauses)
    /////////////////////////////////////////
    state.incGeneratedCl(newclauses.size)
    Out.debug(s"[SeqLoop] Generated: ${newclauses.map(_.id).mkString(",")}")
    Out.debug(s"[SeqLoop] \t${newclauses.map(_.pretty(sig)).mkString("\n\t\t\t\t\t\t")}")
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
        // after change of clause hashcode and improvement of clause equals
        // the exists check seems feasible again :)
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
    import leo.modules.proof_object.CompressProof

    val time = System.currentTimeMillis() - startTime
    val timeWOParsing = System.currentTimeMillis() - startTimeWOParsing

    /* Output additional information about the reasoning process. */
    Out.comment(s"Time passed: ${time}ms (effective reasoning time: ${timeWOParsing}ms)")
    if (state.szsStatus == SZS_Theorem) Out.comment(s"Solved by ${state.runStrategy.pretty}")
    val proof = state.proof
    if (proof != null) {
      try {
        import leo.datastructures.ClauseAnnotation.FromFile
        val proofAx = axiomsInProof(proof)
        Out.comment(s"Axioms used in derivation (${proofAx.size}): ${proofAx.map(_.annotation.asInstanceOf[FromFile].formulaName).mkString(", ")}")
        Out.comment(s"No. of inferences in proof: ${proof.size}")
      } catch {
        case _:Exception => ()
      }
    }
    if (Out.logLevelAtLeast(java.util.logging.Level.FINE)) {
      Out.comment(s"No. of processed clauses: ${state.processed.size}")
      Out.comment(s"No. of generated clauses: ${state.noGeneratedCl}")
      Out.comment(s"No. of forward subsumed clauses: ${state.noForwardSubsumedCl}")
      Out.comment(s"No. of backward subsumed clauses: ${state.noBackwardSubsumedCl}")
      Out.comment(s"No. of ground rewrite rules in store: ${state.groundRewriteRules.size}")
      Out.comment(s"No. of non-ground rewrite rules in store: ${state.nonGroundRewriteRules.size}")
      Out.comment(s"No. of positive (non-rewrite) units in store: ${state.posNonRewriteUnits.size}")
      Out.comment(s"No. of negative (non-rewrite) units in store: ${state.negNonRewriteUnits.size}")
      Out.comment(s"No. of choice functions detected: ${state.choiceFunctionCount}")
      Out.comment(s"No. of choice instantiations: ${state.choiceInstantiations}")
    }
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
    Out.finest("ground rewrite rules")
    import leo.modules.calculus.PatternUnification.isPattern
    Out.finest(state.groundRewriteRules.map(cl => s"(${isPattern(cl.cl.lits.head.left)}/${isPattern(cl.cl.lits.head.right)}): ${cl.pretty(sig)}").mkString("\n\t"))
    Out.finest("#########################")
    Out.finest("non-ground rewrite rules")
    import leo.modules.calculus.PatternUnification.isPattern
    Out.finest(state.nonGroundRewriteRules.map(cl => s"(${isPattern(cl.cl.lits.head.left)}/${isPattern(cl.cl.lits.head.right)}): ${cl.pretty(sig)}").mkString("\n\t"))
    Out.finest("#########################")
    Out.finest("positive other units")
    import leo.modules.calculus.PatternUnification.isPattern
    Out.finest(state.posNonRewriteUnits.map(entry => s"(${isPattern(entry._1.left)}/${isPattern(entry._1.right)}): ${entry._2.pretty(sig)}").mkString("\n\t"))
    Out.finest("negative other units")
    import leo.modules.calculus.PatternUnification.isPattern
    Out.finest(state.negNonRewriteUnits.map(entry => s"(${isPattern(entry._1.left)}/${isPattern(entry._1.right)}): ${entry._2.pretty(sig)}").mkString("\n\t"))
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
    if (Configuration.isSet("print-signature")) {
      println("Signature at termination:")
      println(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
      println(userDefinedSignatureAsString(sig))
    }
    if (Configuration.isSet("print-clauses")) {
      println("Processed clauses at termination:")
      println("\t" + state.processed.toSeq.sortBy(_.cl.lits.size).map(_.pretty(sig)).mkString("\n\t"))
    }

    Out.output(SZSResult(state.szsStatus, Configuration.PROBLEMFILE, s"$time ms resp. $timeWOParsing ms w/o parsing"))
    /* Print proof object if possible and requested. */
    if (Configuration.PROOF_OBJECT && proof != null) {
      try {
        val proofSignature = userSignatureToTPTP(symbolsInProof(proof))(sig)
        val proofString = if (Configuration.isSet("compressProof")) proofToTPTP(compressedProofOf(CompressProof.stdImportantInferences)(state.derivationClause.get))
        else proofToTPTP(proof)
        val proofOutput: String = if (proofSignature.isEmpty) proofString else proofSignature + "\n" + proofString
        Out.output(SZSOutput(SZS_Refutation, Configuration.PROBLEMFILE, proofOutput))
      } catch {
        case e: Exception => Out.comment("Translation of proof object failed. See error logs for details.")
          Out.warn(e.toString)
      }
    }
  }

  @inline final def prematureCancel(counter: Long): Boolean = {
    try {
      val limit: Long = Configuration.valueOf("ll").get.head.toLong
      counter >= limit
    } catch {
      case _: NumberFormatException => false
      case _: NoSuchElementException => false
    }
  }
}
