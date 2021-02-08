package leo.modules

import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.{Configuration, Out}
import leo.datastructures.{AnnotatedClause, ClauseAnnotation, LanguageLevel, TPTP, Term}
import leo.modules.calculus.NegateConjecture
import leo.modules.control.Control
import leo.modules.external.TptpResult
import leo.modules.output._
import leo.modules.input.Input

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
  final def effectiveInput(input: Seq[TPTP.AnnotatedFormula], state: LocalGeneralState): Seq[AnnotatedClause] = {
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
      val result = relevantAxioms.map(ax => processInput(ax, state))
      Out.info(s"Problem is ${state.languageLevel.pretty}.")
      result
    } else {
      Out.info(s"${effectiveInput.size} axioms and no conjecture found.")
      val result = effectiveInput.map(ax => processInput(ax, state))
      Out.info(s"Problem is ${state.languageLevel.pretty}.")
      result
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
  final private def effectiveInput0(input: Seq[TPTP.AnnotatedFormula], state: LocalGeneralState): (Seq[TPTP.AnnotatedFormula], Seq[TPTP.AnnotatedFormula]) = {
    import leo.datastructures.{Role_Definition, Role_Type, Role_Conjecture, Role_NegConjecture, Role_Unknown}
    import leo.datastructures.ClauseAnnotation._
    import leo.datastructures.{Lang_Unknown, Lang_Mixed}

    var result: Seq[TPTP.AnnotatedFormula] = Vector.empty
    var conj: Seq[TPTP.AnnotatedFormula] = Vector.empty
    val inputIt = input.iterator
    while (inputIt.hasNext) {
      val formula = inputIt.next()
      val langLevelFromFormula = LanguageLevel.fromFormulaType(formula.formulaType)
      if (state.languageLevel == Lang_Unknown) state.setLanguageLevel(langLevelFromFormula)
      else {
        val cmp = langLevelFromFormula.compare(state.languageLevel)
        if (cmp > 0) state.setLanguageLevel(Lang_Mixed(langLevelFromFormula))
        else if (cmp < 0) state.setLanguageLevel(Lang_Mixed(state.languageLevel.flatten))
      }
      formula.role match {
        case Role_Type.pretty => Input.processFormula(formula)(state.signature)
        case Role_Definition.pretty =>
          Control.relevanceFilterAdd(formula)(state.signature)
          val res = Input.processFormula(formula)(state.signature)
          if (res._3 != Role_Definition) {
            // If it is not a definition, then it was not recognized to be one.
            // So we are treating it like an axiom and add it to the result.
            result = formula +: result
          }
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
          result = result :+ formula
      }
    }
    (result,conj)
  }

  final def effectiveInputNew(input: Seq[TPTP.AnnotatedFormula], state: LocalGeneralState): Seq[AnnotatedClause] = {
    Out.info(s"Parsing finished. Scanning for conjecture ...")
    val (axioms, definitions, conjectures) = splitInput(input)(state)

    // Set axiom selection filter, after processing of input is done in 'effectiveInput'
    state.setAxiomFilterConfig(Control.getBestFilterConfig(state))

    val selectedAxioms = if (state.negConjecture.nonEmpty) {
      Out.info(s"Found a conjecture and ${axioms.size} axioms. Running axiom selection (using ${state.getAxiomFilterConfig.toString}) ...")
      // Do relevance filtering: Filter hopefully unnecessary axioms
      val (relevantAxioms, removedAxioms) = Control.getRelevantAxiomsNew(axioms, definitions, conjectures)(state)
      state.setFilteredAxioms(removedAxioms)
      Out.info(s"Axiom selection finished. Selected ${relevantAxioms.size} axioms " +
        s"(removed ${removedAxioms.size} axioms).")
      relevantAxioms
    } else {
      Out.info(s"${axioms.size} axioms and no conjecture found.")
      axioms
    }
    val result = selectedAxioms.map(ax => processInput(ax, state))
    Out.info(s"Problem is ${state.languageLevel.pretty}.")
    result
  }

  type Conjecture = TPTP.AnnotatedFormula
  type Axiom = TPTP.AnnotatedFormula
  type Definition = (String, TPTP.AnnotatedFormula)
  final def splitInput(input: Seq[TPTP.AnnotatedFormula])(state: LocalGeneralState): (Seq[Axiom], Seq[Definition], Seq[Conjecture]) = {
    import leo.datastructures.{Lang_Unknown, Lang_Mixed}
    import scala.collection.mutable

    val axioms: mutable.ListBuffer[Axiom] = mutable.ListBuffer.empty
    val defs: mutable.ListBuffer[Definition] = mutable.ListBuffer.empty
    val conjs: mutable.ListBuffer[Conjecture] = mutable.ListBuffer.empty

    val dist = state.symbolDistribution

    input.foreach { annotatedFormula =>
      val langLevelFromFormula = LanguageLevel.fromFormulaType(annotatedFormula.formulaType)
      if (state.languageLevel == Lang_Unknown) state.setLanguageLevel(langLevelFromFormula)
      else {
        val cmp = langLevelFromFormula.compare(state.languageLevel)
        if (cmp > 0) state.setLanguageLevel(Lang_Mixed(langLevelFromFormula))
        else if (cmp < 0) state.setLanguageLevel(Lang_Mixed(state.languageLevel.flatten))
      }
      annotatedFormula.role match {
        case "type" => Input.processFormula(annotatedFormula)(state.signature)
        case "definition" =>
          import leo.datastructures.Role_Definition
          val res = Input.processFormula(annotatedFormula)(state.signature)
          if (res._3 != Role_Definition) {
            // If it is not a definition, then it was not recognized to be one.
            // So we are treating it like an axiom and add it to the result.
            dist.add(annotatedFormula)
            axioms.append(annotatedFormula)
          } else {
            val nameSymbols = definitionNameAndSymbols(annotatedFormula)
            nameSymbols match {
              case Some((name, symbols)) =>
                defs.append((name, annotatedFormula))
                dist.incrementAll(symbols)
              case None =>
            }
          }
        case "axiom" | "hypothesis" =>
          dist.add(annotatedFormula)
          axioms.append(annotatedFormula)
        case "conjecture" =>
          if (state.conjecture == null && state.negConjecture.isEmpty) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input conjecture ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              // Convert and negate and add conjecture
              dist.add(annotatedFormula)
              val translated = Input.processFormula(annotatedFormula)(state.signature)
              val conjectureClause = AnnotatedClause(
                termToClause(translated._2),
                leo.datastructures.Role_Conjecture,
                leo.datastructures.ClauseAnnotation.FromFile(Configuration.PROBLEMFILE, translated._1),
                ClauseAnnotation.PropNoProp
              )
              state.setConjecture(conjectureClause)
              val negConjectureClause = AnnotatedClause(
                termToClause(translated._2, polarity = false),
                leo.datastructures.Role_NegConjecture,
                InferredFrom(NegateConjecture, conjectureClause),
                ClauseAnnotation.PropSOS
              )
              state.addNegConjecture(negConjectureClause)
              conjs.append(annotatedFormula)
            }
          } else throw new SZSException(SZS_InputError, "Problem contains either multiple conjectures or both conjecture and negated_conjecture formulas. This is not allowed.")
        case "negated_conjecture" =>
          if (state.conjecture == null) {
            if (Configuration.CONSISTENCY_CHECK) {
              Out.info(s"Input (negated) conjecture(s) ignored since 'consistency-only' is set.")
              /* skip */
            } else {
              dist.add(annotatedFormula)
              val translated = Input.processFormula(annotatedFormula)(state.signature)
              val negConjectureClause = AnnotatedClause(
                termToClause(translated._2),
                leo.datastructures.Role_NegConjecture,
                leo.datastructures.ClauseAnnotation.FromFile(Configuration.PROBLEMFILE, translated._1),
                ClauseAnnotation.PropSOS
              )
              state.addNegConjecture(negConjectureClause)
              conjs.append(annotatedFormula)
            }
          } else throw new SZSException(SZS_InputError, "Problem contains both conjecture and negated_conjecture formulas. This is not allowed.")
        case "unknown" => throw new SZSException(SZS_InputError, s"Formula '${annotatedFormula.name}' has role 'unknown' which is regarded an error.")
        case role => throw new SZSException(SZS_InputError, s"Formula '${annotatedFormula.name}' has unexpected role '$role' and it's not clear how to proceed from here.")
      }
    }
    state.setAxiomCount(axioms.size)
    (axioms.toVector, defs.toVector, conjs.toVector)
  }

  private final def definitionNameAndSymbols(formula: TPTP.AnnotatedFormula): Option[(String, Set[String])] = {
    import leo.datastructures.TPTP.THFAnnotated
    import leo.datastructures.TPTP.THF
    formula match {
      case THFAnnotated(_, _, THF.Logical(THF.BinaryFormula(THF.Eq, THF.FunctionTerm(name, Seq()), definition)), _) =>
        Some(name, definition.symbols)
      case _ => None
    }
  }

  final private def processInput(input: TPTP.AnnotatedFormula, state: LocalGeneralState): AnnotatedClause = {
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


//  @tailrec final def exhaustive[T](f: Set[T] => Set[T])(set: Set[T]): Set[T] = {
//    val result = f(set)
//    if (result == set) set
//    else exhaustive(f)(result)
//  }
}
