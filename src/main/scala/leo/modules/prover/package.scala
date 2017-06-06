package leo.modules

import leo.datastructures.ClauseAnnotation.InferredFrom
import leo.{Configuration, Out}
import leo.datastructures.{AnnotatedClause, ClauseAnnotation, Term, tptp}
import leo.modules.calculus.NegateConjecture
import leo.modules.control.Control
import leo.modules.output.{SZS_InputError, SZS_Theorem, SZS_TypeError}
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
  final private def effectiveInput0(input: Seq[tptp.Commons.AnnotatedFormula], state: LocalGeneralState): (Seq[tptp.Commons.AnnotatedFormula], tptp.Commons.AnnotatedFormula) = {
    import leo.datastructures.{Role_Definition, Role_Type, Role_Conjecture, Role_NegConjecture, Role_Unknown}
    import leo.datastructures.ClauseAnnotation._
    var result: Seq[tptp.Commons.AnnotatedFormula] = Vector()
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
              Control.relevanceFilterAdd(formula)(state.signature)
              val translated = Input.processFormula(formula)(state.signature)
              val conjectureClause = AnnotatedClause(termToClause(translated._2), Role_Conjecture, FromFile(Configuration.PROBLEMFILE, translated._1), ClauseAnnotation.PropNoProp)
              state.setConjecture(conjectureClause)
              val negConjectureClause = AnnotatedClause(termToClause(translated._2, false), Role_NegConjecture, InferredFrom(NegateConjecture, conjectureClause), ClauseAnnotation.PropSOS)
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

  final private def processInput(input: tptp.Commons.AnnotatedFormula, state: LocalGeneralState): AnnotatedClause = {
    import leo.datastructures.ClauseAnnotation.FromFile
    val formula = Input.processFormula(input)(state.signature)
    AnnotatedClause(termToClause(formula._2), formula._3, FromFile(Configuration.PROBLEMFILE, formula._1), ClauseAnnotation.PropNoProp)
  }

  ////////////////////////////////////
  //// Further Utility
  ////////////////////////////////////
  final def typeCheck(input: Seq[AnnotatedClause], state: LocalGeneralState): Unit = {
    if (state.negConjecture != null) typeCheck0(state.negConjecture +: input)
    else typeCheck0(input)
  }
  @tailrec final private def typeCheck0(input: Seq[AnnotatedClause]): Unit = {
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

  def extCallInference(prover: String, source: Set[AnnotatedClause]): ClauseAnnotation = {
    InferredFrom(new leo.modules.calculus.CalculusRule {
      final val name: String = prover
      final val inferenceStatus = SZS_Theorem
    }, source.toSeq)
  }
}
