package leo.modules

import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.{Configuration, Out}
import leo.datastructures.{AnnotatedClause, ClauseAnnotation, Term, tptp}
import leo.modules.calculus.NegateConjecture
import leo.modules.control.Control
import leo.modules.external.TptpResult
import leo.modules.output._
import leo.modules.parsers.Input

import scala.annotation.tailrec

/**
  * Created by lex on 26.05.17.
  */
package object prover {
  type LocalGeneralState = GeneralState[AnnotatedClause]
  type LocalState = State[AnnotatedClause]

  ////////////////////////////////////
  //// Loading and converting the problem
  ////////////////////////////////////

  /** Converts the input into clauses and filters the axioms if applicable. */
  final def effectiveInput(input: Seq[tptp.Commons.AnnotatedFormula], state: LocalGeneralState): Seq[AnnotatedClause] = {
    import leo.datastructures.Clause
    Out.info(s"Parsing finished. Scanning for conjecture ...")
    val (effectiveInput,conjs) = effectiveInput0(input, state) // Split input

    if (state.negConjecture.nonEmpty) {
      Out.info(s"Found a conjecture and ${effectiveInput.size} axioms. Running axiom selection ...")
      // Do relevance filtering: Filter hopefully unnecessary axioms
      val relevantAxioms = if (state.negConjecture.exists(cl => Clause.asTerm(cl.cl).symbols.distinct.intersect(state.signature.allUserConstants).isEmpty)) {
        leo.Out.finest(s"trivial conjecture, lets take all axioms.")
        effectiveInput
      } else Control.getRelevantAxioms(effectiveInput, conjs)(state.signature)
      state.setFilteredAxioms(effectiveInput.diff(relevantAxioms))
      Out.info(s"Axiom selection finished. Selected ${relevantAxioms.size} axioms " +
        s"(removed ${state.filteredAxioms.size} axioms).")
      relevantAxioms.map(ax => processInput(ax, state))
    } else {
      Out.info(s"${effectiveInput.size} axioms and no conjecture found.")
      effectiveInput.map(ax => processInput(ax, state))
    }
  }

  /** Split the problem input (list of TPTP AnnotatedFormulas) into
    * (1) axioms, and
    * (2) (negated) conjecture(s).
    * Multiple conjecture are not allowed, multiple negated conjecture are allowed.
    * The axioms of the problem statement are left unchanged, a possibly existing
    * conjecture is negated.
    *
    * Side effects:
    * - Types and definitions are inserted into the signature.
    * - The conjecture/negated conjecture(s) are updated in the state.
    * - Each axiom is registered at the axiom filter
    *
    * @throws leo.modules.SZSException of type [[leo.modules.output.SZS_InputError]]
    * if multiple conjecture are contained in the problem input. */
  final private def effectiveInput0(input: Seq[tptp.Commons.AnnotatedFormula], state: LocalGeneralState): (Seq[tptp.Commons.AnnotatedFormula], Seq[tptp.Commons.AnnotatedFormula]) = {
    import leo.datastructures.{Role_Definition, Role_Type, Role_Conjecture, Role_NegConjecture, Role_Unknown}
    import leo.datastructures.ClauseAnnotation._
    var result: Seq[tptp.Commons.AnnotatedFormula] = Vector.empty
    var conj: Seq[tptp.Commons.AnnotatedFormula] = Vector.empty
    val inputIt = input.iterator
    while (inputIt.hasNext) {
      val formula = inputIt.next()
      formula.role match {
        case Role_Type.pretty => Input.processFormula(formula)(state.signature)
        case Role_Definition.pretty => Control.relevanceFilterAdd(formula)(state.signature)
          Input.processFormula(formula)(state.signature)
        case Role_Conjecture.pretty =>
          if (state.conjecture == null && state.negConjecture.isEmpty) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input conjecture ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              // Convert and negate and add conjecture
              Control.relevanceFilterAdd(formula)(state.signature)
              val translated = Input.processFormula(formula)(state.signature)
              val conjectureClause = AnnotatedClause(termToClause(translated._2), Role_Conjecture, FromFile(Configuration.PROBLEMFILE, translated._1), ClauseAnnotation.PropNoProp)
              state.setConjecture(conjectureClause)
              val negConjectureClause = AnnotatedClause(termToClause(translated._2, polarity = false), Role_NegConjecture, InferredFrom(NegateConjecture, conjectureClause), ClauseAnnotation.PropSOS)
              state.addNegConjecture(negConjectureClause)
              conj = Vector(formula)
            }
          } else throw new SZSException(SZS_InputError, "Problem contains either multiple conjectures or both conjecture and negated_conjecture formulas. This is not allowed.")
        case Role_NegConjecture.pretty =>
          if (state.conjecture == null) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input conjecture ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              Control.relevanceFilterAdd(formula)(state.signature)
              val translated = Input.processFormula(formula)(state.signature)
              val negConjectureClause = AnnotatedClause(termToClause(translated._2), Role_NegConjecture, FromFile(Configuration.PROBLEMFILE, translated._1), ClauseAnnotation.PropSOS)
              state.addNegConjecture(negConjectureClause)
              conj = conj :+ formula
            }
          } else throw new SZSException(SZS_InputError, "Problem contains both conjecture and negated_conjecture formulas. This is not allowed.")
        case Role_Unknown.pretty =>
          throw new SZSException(SZS_InputError, s"Formula ${formula.name} has role 'unknown' which is regarded an error.")
        case _ =>
          Control.relevanceFilterAdd(formula)(state.signature)
          result = formula +: result
      }
    }
    (result,conj)
  }

  final private def processInput(input: tptp.Commons.AnnotatedFormula, state: LocalGeneralState): AnnotatedClause = {
    import leo.datastructures.ClauseAnnotation.FromFile
    val formula = Input.processFormula(input)(state.signature)
    AnnotatedClause(termToClause(formula._2), formula._3, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
  }

  ////////////////////////////////////
  //// Further Utility
  ////////////////////////////////////
  final def typeCheck(input: Seq[AnnotatedClause], state: LocalGeneralState): Unit = {
    typeCheck0(state.negConjecture.toSeq ++ input)
  }
  @tailrec final private def typeCheck0(input: Seq[AnnotatedClause]): Unit = {
    import leo.datastructures.ClauseAnnotation.FromFile
    import leo.modules.HOLSignature.o
    if (input.nonEmpty) {
      val hd = input.head
      val term = hd.cl.lits.head.left
      val annotation = if (hd.annotation.isInstanceOf[FromFile]) hd.annotation.asInstanceOf[FromFile]
      else hd.annotation.parents.head.annotation.asInstanceOf[FromFile]

      if (!Term.wellTyped(term)) {
        leo.Out.severe(s"Input problem did not pass type check: formula '${annotation.formulaName}' is ill-typed.")
        throw new SZSException(SZS_TypeError, s"Type error in formula '${annotation.formulaName}' from file '${annotation.fileName}'.")
      } else if (term.ty != o) {
        leo.Out.severe(s"Input problem did not pass type check: '${annotation.formulaName}' is not Boolean typed.")
        throw new SZSException(SZS_TypeError, s"Term of non-Boolean type at top-level in formula '${annotation.formulaName}' from file '${annotation.fileName}'.")
      } else {
        typeCheck0(input.tail)
      }
    }
  }

  final def successSZS(szs: StatusSZS): Boolean = {
    import leo.modules.output.SuccessSZS
    szs.isInstanceOf[SuccessSZS]
  }

  final def isSatisfiable(processed: Set[AnnotatedClause])(state: LocalState): Boolean = {
//    if (state.filteredAxioms.isEmpty) {
//      if (state.runStrategy.choice && state.runStrategy.boolExt && !state.runStrategy.sos && state.runStrategy.primSubst > 0) {
//        val processedIt = state.processed.iterator
//        while (processedIt.hasNext) {
//          val processed = processedIt.next()
//          if (processed.cl.implicitlyBound.map(_._2).exists(_.isFunType)) return false
//        }
//        true
//      } else false
//    } else false
    false
  }
  final def appropriateSatStatus(state: LocalState): StatusSZS = {
    if (state.negConjecture.isEmpty) SZS_Satisfiable
    else SZS_CounterSatisfiable
  }
  final def appropriateThmStatus(state: LocalState, proof: Proof): StatusSZS = {
    if (state.negConjecture.isEmpty) SZS_Unsatisfiable
    else {
      if (conjInProof(proof)) SZS_Theorem
      else SZS_ContradictoryAxioms
    }
  }

  final def endplay(emptyClause: AnnotatedClause, state: LocalState): Unit = {
    if (emptyClause == null) state.setSZSStatus(appropriateSatStatus(state))
    else {
      state.setDerivationClause(emptyClause)
      val proof = proofOf(emptyClause)
      state.setProof(proof)
      state.setSZSStatus(appropriateThmStatus(state, proof))
    }
  }

  final def endgameResult(result: TptpResult[_]): Boolean = {
    import leo.modules.external.Capabilities
    if (result.szsStatus == SZS_Unsatisfiable)
      true
    else if (result.szsStatus == SZS_Satisfiable && result.prover.capabilities.contains(Capabilities.THF))
      true
    else false
  }

  final def extCallInference(prover: String, source: Set[AnnotatedClause]): ClauseAnnotation = {
    InferredFrom(new leo.modules.calculus.CalculusRule {
      final val name: String = prover
      final val inferenceStatus = SZS_Theorem
    }, source.toSeq)
  }


  @tailrec final def exhaustive[T](f: Set[T] => Set[T])(set: Set[T]): Set[T] = {
    val result = f(set)
    if (result == set) set
    else exhaustive(f)(result)
  }
}
