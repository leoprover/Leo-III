package leo.modules.seqpproc

import leo.Configuration
import leo.Out
import leo.datastructures._
import ClauseAnnotation._
import leo.modules.calculus.PatternUnification
import leo.modules.output._
import leo.modules.control.Control
import leo.modules.{Parsing, SZSException, SZSOutput, Utility}


/**
  * Created by lex on 10/28/15.
  */
object SeqPProc extends Function1[Long, Unit]{

  @inline private final def termToClause(t: Term): Clause = {
    Clause.mkClause(Seq(Literal.mkLit(t, true)))
  }

  final def preprocess(state: State[AnnotatedClause], cur: AnnotatedClause): Set[AnnotatedClause] = {
    implicit val sig: Signature = state.signature
    var result: Set[AnnotatedClause] = Set()
    // Fresh clause, that means its unit and nonequational
    assert(Clause.unit(cur.cl), "clause not unit")
    val lit = cur.cl.lits.head
    assert(!lit.equational, "initial literal equational")

    // Def expansion and simplification
    var cw = cur
    cw = Control.expandDefinitions(cw)
    cw = Control.nnf(cw)
    cw = Control.switchPolarity(cw)
    cw = Control.skolemize(cw)

    // Exhaustively CNF
    result = Control.cnf(cw)
    // Remove defined equalities as far as possible
    result = result union Control.convertDefinedEqualities(result)

    // To equation if possible and then apply func ext
    // AC Simp if enabled, then Simp.
    result = result.map { cl =>
      var result = cl
      result = Control.liftEq(result)
      result = Control.funcext(result) // Maybe comment out? why?
      result = Control.acSimp(result)
      Control.simp(result)
    }
    // Pre-unify new clauses
    result = result union Control.preunifySet(result)
    result = result.filterNot(cw => Clause.trivial(cw.cl))
    result
  }


  final def typeCheck(input: Seq[(Parsing.FormulaId, Term, Role)]): Set[(Parsing.FormulaId, Term, Role)] = {
    var returnSet:Set[(Parsing.FormulaId, Term, Role)] = Set()
    val inputIt = input.iterator
    while(inputIt.hasNext) {
      val i = inputIt.next()
      if (!Term.wellTyped(i._2)) returnSet = returnSet + i
    }
    returnSet
  }

  ///////////////////////////////////////////////////////////

  /* Main function containing proof loop */
  final def apply(startTime: Long): Unit = {
    /////////////////////////////////////////
    // Main loop preparations:
    // Read Problem, preprocessing, state set-up, etc.
    /////////////////////////////////////////
    var test = false
    implicit val sig: Signature = Signature.freshWithHOL()
    val state: State[AnnotatedClause] = State.fresh(sig)
    try {
      // Read problem
      val input = Parsing.parseProblem(Configuration.PROBLEMFILE)
      val startTimeWOParsing = System.currentTimeMillis()
      // Do type check in input
      val tyCheckSet = typeCheck(input)
      if (tyCheckSet.nonEmpty) {
        leo.Out.severe(s"Input problem did not pass type check.")
        throw new SZSException(SZS_TypeError, s"Type error in formulas: ${tyCheckSet.map(_._1).mkString(",")}")
      }
      Out.info(s"Parsing finished. Searching refutation ...")
      // Iterate over all inputs (which are not type declarations and definitions)
      // and collect all non-conjectures and set conjecture in state
      val inputIt = input.iterator
      var remainingInput: Set[AnnotatedClause] = Set()

      while (inputIt.hasNext) {
        val formula = inputIt.next()
        if (formula._3 == Role_Definition || formula._3 == Role_Type) {
          /* skip */
        } else if (formula._3 == Role_Conjecture) {
          if (state.conjecture == null) {
            import leo.modules.calculus.CalculusRule

            val conjectureClause = AnnotatedClause(termToClause(formula._2), Role_Conjecture, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
            state.setConjecture(conjectureClause)
            val negConjectureClause = AnnotatedClause(Clause.mkClause(Seq(Literal.mkLit(formula._2, false))), Role_NegConjecture, InferredFrom(new CalculusRule {
              override def name: String = "neg_conjecture"

              override val inferenceStatus = Some(SZS_CounterTheorem)
            }, Set(conjectureClause)), ClauseAnnotation.PropSOS)
            state.setNegConjecture(negConjectureClause)
          } else throw new SZSException(SZS_InputError, "At most one conjecture per input problem is permitted.")
        } else if (formula._3 == Role_NegConjecture) {
          throw new SZSException(SZS_InputError, "The system does not support input of already negated conjectures, sorry.")
        } else if (formula._3 == Role_Plain) {
          leo.Out.warn(s"Formula ${formula._1} has role 'plain' and is now treated as 'axiom'. Please check if that is what you intended.")
          val formulaClause = AnnotatedClause(termToClause(formula._2), Role_Axiom, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
          remainingInput = remainingInput + formulaClause
        } else if (formula._3 == Role_Unknown) {
          throw new SZSException(SZS_InputError, "One input formula has role 'unknown' which is treated as an error.")
        } else {
          val formulaClause = AnnotatedClause(termToClause(formula._2), formula._3, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
          remainingInput = remainingInput + formulaClause
        }
      }
      // Initialize indexes
      if (state.negConjecture != null) Control.fvIndexInit(remainingInput + state.negConjecture)
      else Control.fvIndexInit(remainingInput)
      Control.foIndexInit()

      // Preprocessing
      val conjecture_preprocessed = if (state.negConjecture != null) {
        Out.debug("## Preprocess Neg.Conjecture BEGIN")
        Out.trace(s"Neg. conjecture: ${state.negConjecture.pretty(sig)}")
        val result = preprocess(state, state.negConjecture).filterNot(cw => Clause.trivial(cw.cl))
        Out.debug(s"# Result:\n\t${
          result.map {
            _.pretty(sig)
          }.mkString("\n\t")
        }")
        Out.trace("## Preprocess Neg.Conjecture END")
        result
      } else Set[AnnotatedClause]()

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
        if (preprocessIt.hasNext) Out.trace("--------------------")
      }
      Out.trace("## Preprocess END\n\n")

      // Debug output
      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.finest(s"Clauses and maximal literals of them:")
        for (c <- state.unprocessed union conjecture_preprocessed) {
          Out.finest(s"Clause ${c.pretty(sig)}")
          Out.finest(s"Maximal literal(s):")
          Out.finest(s"\t${Literal.maxOf(c.cl.lits).map(_.pretty(sig)).mkString("\n\t")}")
        }
      }
      Out.finest(s"################")

      val preprocessTime = System.currentTimeMillis() - startTimeWOParsing
      var loop = true

      /////////////////////////////////////////
      // Init loop for conjecture-derived clauses
      /////////////////////////////////////////
      val conjectureProcessedIt = conjecture_preprocessed.toSeq.iterator
      Out.debug("## Pre-reasoning loop BEGIN")
      while (conjectureProcessedIt.hasNext && loop && !prematureCancel(state.noProcessedCl)) {
        if (System.currentTimeMillis() - startTime > 1000 * Configuration.TIMEOUT) {
          loop = false
          state.setSZSStatus(SZS_Timeout)
        } else {
          var cur = conjectureProcessedIt.next()
          Out.debug(s"Taken: ${cur.pretty(sig)}")
          cur = Control.rewriteSimp(cur, state.rewriteRules)
          if (Clause.effectivelyEmpty(cur.cl)) {
            loop = false
            if (state.conjecture == null) {
              state.setSZSStatus(SZS_Unsatisfiable)
            } else {
              state.setSZSStatus(SZS_Theorem)
            }
            state.setDerivationClause(cur)
          } else {
            // Subsumption
            val subsumed = Control.forwardSubsumptionTest(cur, state.processed)
            if (subsumed.isEmpty) {
              mainLoopInferences(cur, state)
            } else {
              Out.debug("clause subsumbed, skipping.")
              state.incForwardSubsumedCl()
              Out.trace(s"Subsumed by:\n\t${subsumed.map(_.pretty(sig)).mkString("\n\t")}")
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
        if (System.currentTimeMillis() - startTime > 1000 * Configuration.TIMEOUT) {
          loop = false
          state.setSZSStatus(SZS_Timeout)
        } else if (!state.unprocessedLeft) {
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
            Out.debug(s"Taken: ${cur.pretty(sig)}")

            cur = Control.rewriteSimp(cur, state.rewriteRules)
            if (Clause.effectivelyEmpty(cur.cl)) {
              loop = false
              if (state.conjecture == null) {
                state.setSZSStatus(SZS_Unsatisfiable)
              } else {
                state.setSZSStatus(SZS_Theorem)
              }
              state.setDerivationClause(cur)
            } else {
              // Subsumption
              val subsumed = Control.forwardSubsumptionTest(cur, state.processed)
              if (subsumed.isEmpty) {
                mainLoopInferences(cur, state)
              } else {
                Out.debug("clause subsumbed, skipping.")
                state.incForwardSubsumedCl()
                Out.trace(s"Subsumed by:\n\t${subsumed.map(_.pretty(sig)).mkString("\n\t")}")
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
      Out.comment(s"No. of units in store: ${state.rewriteRules.size}")
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
      Out.finest(state.rewriteRules.map(cl => s"(${PatternUnification.isPattern(cl.cl.lits.head.left)}/${PatternUnification.isPattern(cl.cl.lits.head.right)}): ${cl.pretty(sig)}").mkString("\n\t"))
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


      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.comment("Signature extension used:")
        Out.comment(s"Name\t|\tId\t|\tType/Kind\t|\tDef.\t|\tProperties")
        Out.comment(Utility.userDefinedSignatureAsString(sig)) // TODO: Adjust for state
      }

      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.comment("Clauses at the end of the loop:")
        Out.comment("\t" + state.processed.toSeq.sortBy(_.cl.lits.size).map(_.pretty(sig)).mkString("\n\t"))
      }
      if (Out.logLevelAtLeast(java.util.logging.Level.FINEST)) {
        Out.finest("TFF clauses at the end:")
        Out.finest("\t" + Control.foIndex.iterator.toSeq.map(leo.modules.output.ToTFF.apply).mkString("\n\t"))
      }


      /* Print proof object if possible and requested. */
      if (state.szsStatus == SZS_Theorem && Configuration.PROOF_OBJECT && proof != null) {
        Out.comment(s"SZS output start CNFRefutation for ${Configuration.PROBLEMFILE}")
        Out.output(Utility.userSignatureToTPTP(Utility.symbolsInProof(proof))(sig))
        Out.output(Utility.proofToTPTP(proof))
        Out.comment(s"SZS output end CNFRefutation for ${Configuration.PROBLEMFILE}")
      }
    } catch {
      case e:Throwable => throw new SZSException(SZS_Error, e.toString, Utility.signatureAsString(sig))
    }
  }

  private final def mainLoopInferences(cl: AnnotatedClause, state: State[AnnotatedClause]): Unit = {
    implicit val sig: Signature = state.signature

    var cur: AnnotatedClause = cl
    var newclauses: Set[AnnotatedClause] = Set()

    /////////////////////////////////////////
    // Simplifying (mofifying inferences and backward subsumption) BEGIN
    // TODO: à la E: direct descendant criterion, etc.
    /////////////////////////////////////////
    /* Subsumption */
    val backSubsumedClauses = Control.backwardSubsumptionTest(cur, state.processed)

    if (backSubsumedClauses.nonEmpty) {
      Out.trace(s"#### backward subsumed")
      state.incBackwardSubsumedCl(backSubsumedClauses.size)
      Out.trace(s"backward subsumes\n\t${backSubsumedClauses.map(_.pretty(sig)).mkString("\n\t")}")
      state.setProcessed(state.processed -- backSubsumedClauses)
      Control.fvIndexRemove(backSubsumedClauses)
    }

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
    leo.Out.finest(s"XX ${cur.pretty(sig)}")
    leo.Out.finest(s"XX ${leo.modules.indexing.FOIndex.typedFirstOrder(cur)}")
    Control.foIndex.insert(cur)
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

    /* Pre-unify new clauses */
    newclauses = Control.preunifyNewClauses(newclauses)

    /* exhaustively CNF new clauses */
    newclauses = newclauses.flatMap(cw => Control.cnf(cw))
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
      assert(Clause.wellTyped(newCl.cl), s"clause ${newCl.id} is not well-typed")
      // Simplify again, including rewriting etc.
      newCl = Control.rewriteSimp(newCl, state.rewriteRules)

      if (!Clause.trivial(newCl.cl)) {
        state.addUnprocessed(newCl)
      } else {
        Out.trace(s"Trivial, hence dropped: ${newCl.pretty(sig)}")
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
}
