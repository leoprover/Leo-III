package leo.modules.seqpproc

import leo.Configuration
import leo.Out
import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.datastructures.{AnnotatedClause, Clause, ClauseAnnotation, Literal, Signature, Term, addProp, tptp}
import leo.modules.output._
import leo.modules.control.Control
import leo.modules.parsers.Input
import leo.modules.{SZSException, SZSOutput, Utility}

import scala.annotation.tailrec

/**
  * Sequential proof procedure. Its state is represented by [[leo.modules.seqpproc.State]].
  *
  * @since 28.10.15
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
object SeqPProc {
  type LocalState = State[AnnotatedClause]
  ////////////////////////////////////
  //// Loading and converting the problem
  ////////////////////////////////////
  /** Converts the input into clauses and filters the axioms if applicable. */
  final def effectiveInput(input: Seq[tptp.Commons.AnnotatedFormula], state: LocalState): Seq[AnnotatedClause] = {
    Out.info(s"Parsing finished. Scanning for conjecture ...")
    val (effectiveInput,conj) = effectiveInput0(input, state)
    if (state.negConjecture != null) {
      Out.info(s"Found a conjecture and ${effectiveInput.size} axioms. Running axiom selection ...")
      // Do relevance filtering: Filter hopefully unnecessary axioms
      val relevantAxioms = Control.getRelevantAxioms(effectiveInput, conj)(state.signature)
      Out.info(s"Axiom selection finished. Selected ${relevantAxioms.size} axioms " +
        s"(removed ${effectiveInput.size - relevantAxioms.size} axioms).")
      relevantAxioms.map(ax => processInput(ax, state))
    } else {
      Out.info(s"${effectiveInput.size} axioms and no conjecture found.")
      effectiveInput.map(ax => processInput(ax, state))
    }
  }
  /** Insert types, definitions and the conjecture to the signature resp. state. The rest
    * (axioms etc.) is left unchanged for relevance filtering. Throws an error if multiple
    * conjectures are present or unknown role occurs. */
  final private def effectiveInput0(input: Seq[tptp.Commons.AnnotatedFormula], state: LocalState): (Seq[tptp.Commons.AnnotatedFormula], tptp.Commons.AnnotatedFormula) = {
    import leo.modules.Utility.termToClause
    import leo.datastructures.{Role_Definition, Role_Type, Role_Conjecture, Role_NegConjecture, Role_Unknown}
    import leo.datastructures.ClauseAnnotation._
    var result: Seq[tptp.Commons.AnnotatedFormula] = Seq()
    var conj: tptp.Commons.AnnotatedFormula = null
    val inputIt = input.iterator
    while (inputIt.hasNext) {
      val formula = inputIt.next()
      formula.role match {
        case Role_Type.pretty => Input.processFormula(formula)(state.signature)
        case Role_Definition.pretty => Control.relevanceFilterAdd(formula)(state.signature)
          Input.processFormula(formula)(state.signature)
        case Role_Conjecture.pretty =>
          if (state.negConjecture == null) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input conjecture ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              // Convert and negate and add conjecture
              import leo.modules.calculus.CalculusRule
              Control.relevanceFilterAdd(formula)(state.signature)
              val translated = Input.processFormula(formula)(state.signature)
              val conjectureClause = AnnotatedClause(termToClause(translated._2), Role_Conjecture, FromFile(Configuration.PROBLEMFILE, translated._1), ClauseAnnotation.PropNoProp)
              state.setConjecture(conjectureClause)
              val negConjectureClause = AnnotatedClause(termToClause(translated._2, false), Role_NegConjecture, InferredFrom(new CalculusRule {
                final val name: String = "neg_conjecture"
                final val inferenceStatus = SZS_CounterTheorem
              }, conjectureClause), ClauseAnnotation.PropSOS)
              state.setNegConjecture(negConjectureClause)
              conj = formula
            }
          } else throw new SZSException(SZS_InputError, "At most one conjecture per input problem is permitted.")
        case Role_NegConjecture.pretty =>
          if (state.negConjecture == null) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input conjecture ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              Control.relevanceFilterAdd(formula)(state.signature)
              val translated = Input.processFormula(formula)(state.signature)
              val negConjectureClause = AnnotatedClause(termToClause(translated._2), Role_NegConjecture, FromFile(Configuration.PROBLEMFILE, translated._1), ClauseAnnotation.PropSOS)
              state.setNegConjecture(negConjectureClause)
              conj = formula
            }
          } else throw new SZSException(SZS_InputError, "At most one (negated) conjecture per input problem is permitted.")
        case Role_Unknown.pretty =>
          throw new SZSException(SZS_InputError, s"Formula ${formula.name} has role 'unknown' which is regarded an error.")
        case _ =>
          Control.relevanceFilterAdd(formula)(state.signature)
          result = formula +: result
      }
    }
    (result,conj)
  }
  final private def processInput(input: tptp.Commons.AnnotatedFormula, state: LocalState): AnnotatedClause = {
    import leo.modules.Utility.termToClause
    import leo.datastructures.ClauseAnnotation.FromFile
    val formula = Input.processFormula(input)(state.signature)
    AnnotatedClause(termToClause(formula._2), formula._3, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
  }
  final def typeCheck(input: Seq[AnnotatedClause], state: LocalState): Unit = {
    if (state.negConjecture != null) typeCheck0(state.negConjecture +: input)
    else typeCheck0(input)
  }
  @tailrec
  final private def typeCheck0(input: Seq[AnnotatedClause]): Unit = {
    if (input.nonEmpty) {
      val hd = input.head
      val term = hd.cl.lits.head.left
      import leo.modules.HOLSignature.o
      if (!Term.wellTyped(term) || term.ty != o) {
        leo.Out.severe(s"Input problem did not pass type check: ${hd.id} is ill-typed.")
        throw new SZSException(SZS_TypeError, s"Type error in formula ${hd.id}")
      } else {
        typeCheck0(input.tail)
      }
    }
  }

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

    // TODO: Interplay between choice and defined equalities?
    /*result = result.map {cl =>
      val choiceCandidate = Control.detectChoiceClause(cl)
      if (choiceCandidate.isDefined) {
        val choiceFun = choiceCandidate.get
        state.addChoiceFunction(choiceFun)
        leo.Out.debug(s"Choice: function detected ${choiceFun.pretty(sig)}")
        leo.Out.debug(s"Choice: clause removed ${cl.id}")
        import leo.modules.HOLSignature.LitTrue
        // replace formula by a trivial one: [[true]^t]
        AnnotatedClause(Clause(Literal.mkLit(LitTrue, true)), NoAnnotation)
      } else cl
    }*/
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
      Control.simp(result)
    }
    // Pre-unify new clauses or treat them extensionally and remove trivial ones
    result = Control.extPreprocessUnify(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }

  ////////////////////////////////////
  //// Main-loop operations
  ////////////////////////////////////

  /* Main function containing proof loop */
  final def apply(startTime: Long): Unit = {
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up, etc.
    /////////////////////////////////////////
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    try {
      // Check if external provers were defined
      if (Configuration.ATPS.nonEmpty) {
        import leo.modules.external.ExternalProver
        Configuration.ATPS.foreach { case(name, path) =>
          try {
            val p = ExternalProver.createProver(name,path)
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

      // Preprocessing Conjecture
      if (state.negConjecture != null) {
        // Expand conj, Initialize indexes
        // We expand here already since we are interested in all symbols (possibly contained within defined symbols)
        Out.debug("## Preprocess Neg.Conjecture BEGIN")
        Out.trace(s"Neg. conjecture: ${state.negConjecture.pretty(sig)}")
        val simpNegConj = Control.expandDefinitions(state.negConjecture)
        state.defConjSymbols(simpNegConj)
        state.initUnprocessed()
        Control.initIndexes(simpNegConj +: remainingInput)
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
        Control.initIndexes(remainingInput)
      }

      // Preprocessing
      Out.debug("## Preprocess BEGIN")
      val preprocessIt = remainingInput.iterator
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

      val preprocessTime = System.currentTimeMillis() - startTimeWOParsing
      var loop = true

      /////////////////////////////////////////
      // Main proof loop
      /////////////////////////////////////////
      Out.debug("## Reasoning loop BEGIN")
      while (loop && !prematureCancel(state.noProcessedCl)) {
        if (System.currentTimeMillis() - startTime > 1000 * Configuration.TIMEOUT) {
          loop = false
          state.setSZSStatus(SZS_Timeout)
        } else if (!state.unprocessedLeft) {
          loop = false
        } else {
          // No cancel, do reasoning step
          val extRes = Control.checkExternalResults(state)
          if (extRes.nonEmpty) {
            val extResAnwers = extRes.get
            // Other than THM or CSA are filter out by control
            assert(extResAnwers.szsStatus == SZS_Theorem || extResAnwers.szsStatus == SZS_CounterSatisfiable)
            loop = false
            if (extResAnwers.szsStatus == SZS_Theorem) {
              val emptyClause = AnnotatedClause(Clause.empty, extCallInference(extResAnwers.proverName, extResAnwers.problem))
              endplay(emptyClause, state)
            } else {
              assert(extResAnwers.szsStatus == SZS_CounterSatisfiable)
              state.setSZSStatus(SZS_CounterSatisfiable)
            }

          } else {
            var cur = state.nextUnprocessed
            // cur is the current AnnotatedClause
            Out.debug(s"Taken: ${cur.pretty(sig)}")
            Out.trace(s"Maximal: ${Literal.maxOf(cur.cl.lits).map(_.pretty(sig)).mkString("\n\t")}")

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
                val choiceCandidate = Control.detectChoiceClause(cur)
                if (choiceCandidate.isDefined) {
                  val choiceFun = choiceCandidate.get
                  state.addChoiceFunction(choiceFun)
                  leo.Out.debug(s"Choice function detected: ${choiceFun.pretty(sig)}")
                } else {
                  // Redundancy check: Check if cur is redundant wrt to the set of processed clauses
                  // e.g. by forward subsumption
                  if (!Control.redundant(cur, state.processed)) {
                    Control.submit(state.processed, state)
                    if(mainLoopInferences(cur, state)) loop = false
                  } else {
                    Out.debug(s"Clause ${cur.id} redundant, skipping.")
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
      // Main loop terminated, print result
      /////////////////////////////////////////

      val time = System.currentTimeMillis() - startTime
      val timeWOParsing = System.currentTimeMillis() - startTimeWOParsing

      Out.output("")
      Out.output(SZSOutput(state.szsStatus, Configuration.PROBLEMFILE, s"$time ms resp. $timeWOParsing ms w/o parsing"))

      /* Output additional information about the reasoning process. */
      Out.comment(s"Time passed: ${time}ms")
      Out.comment(s"Effective reasoning time: ${timeWOParsing}ms")
      Out.comment(s"Thereof preprocessing: ${preprocessTime}ms")
      val proof = if (state.derivationClause.isDefined) Utility.proofOf(state.derivationClause.get) else null
      if (proof != null)
        Out.comment(s"No. of axioms used: ${Utility.axiomsInProof(proof).size}")
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
      Out.finest(Utility.userDefinedSignatureAsString(sig))
      Out.finest("Clauses at the end of the loop:")
      Out.finest("\t" + state.processed.toSeq.sortBy(_.cl.lits.size).map(_.pretty(sig)).mkString("\n\t"))

      /* Print proof object if possible and requested. */
      if ((state.szsStatus == SZS_Theorem || state.szsStatus == SZS_Unsatisfiable) && Configuration.PROOF_OBJECT && proof != null) {
        Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
        Out.output(Utility.userSignatureToTPTP(Utility.symbolsInProof(proof))(sig))
        Out.output(Utility.proofToTPTP(proof))
        Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
      }
    } catch {
      case e:Throwable => Out.severe(s"Signature used:\n${Utility.signatureAsString(sig)}"); throw e
    } finally {
      if (state.externalProvers.nonEmpty)
        Control.killExternals()
    }
  }

  private final def mainLoopInferences(cur: AnnotatedClause, state: LocalState): Boolean = {
    implicit val sig: Signature = state.signature
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
      // Remove all direct descendants of clauses in `bachSubsumedClauses` from unprocessed
      val descendants = Control.descendants(backSubsumedClauses)
      state.incDescendantsDeleted(descendants.size)
      state.removeUnprocessed(descendants)
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
    val boolext_result = Control.boolext(cur)
    newclauses = newclauses union boolext_result

    /* paramodulation where at least one involved clause is `cur` */
    val paramod_result = Control.paramodSet(cur, state.processed)
    newclauses = newclauses union paramod_result

    /* Equality factoring of `cur` */
    val factor_result = Control.factor(cur)
    newclauses = newclauses union factor_result

    /* Prim subst */
    val primSubst_result = Control.primsubst(cur, Configuration.PRIMSUBST_LEVEL)
    newclauses = newclauses union primSubst_result

    /* Replace defined equalities */
    newclauses = newclauses union Control.convertDefinedEqualities(newclauses)
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(Control.liftEq)

    val choice_result = Control.instantiateChoice(cur, state.choiceFunctions)
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
    newclauses = Control.unifyNewClauses(newclauses)

    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(Control.cnf)
    /* Replace eq symbols on top-level by equational literals. */
    newclauses = newclauses.map(cw => Control.shallowSimp(Control.liftEq(cw)))
    /////////////////////////////////////////
    // Simplification of newly generated clauses END
    /////////////////////////////////////////
    Control.updateDescendants(cur, newclauses)
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

  @inline final private def endplay(emptyClause: AnnotatedClause, state: LocalState): Unit = {
    if (state.conjecture == null) state.setSZSStatus(SZS_Unsatisfiable)
    else state.setSZSStatus(SZS_Theorem)
    state.setDerivationClause(emptyClause)
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

  private def extCallInference(prover: String, source: Set[AnnotatedClause]): ClauseAnnotation = {
    InferredFrom(new leo.modules.calculus.CalculusRule {
      final val name: String = prover
      final val inferenceStatus = SZS_Theorem
    }, source.toSeq)
  }
}
