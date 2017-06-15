package leo.modules.control

import leo.{Configuration, Out}
import leo.datastructures.{AnnotatedClause, Signature}
import leo.modules.{FVState, GeneralState, myAssert}
import leo.modules.prover.{RunStrategy, State}

/**
  * Facade object for various control methods of the seq. proof procedure.
  *
  * @see [[leo.modules.prover.SeqLoop]]
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
object Control {
  type LocalState = GeneralState[AnnotatedClause]
  type LocalFVState = FVState[AnnotatedClause]

  // Generating inferences
  @inline final def paramodSet(cl: AnnotatedClause, withSet: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.ParamodControl.paramodSet(cl,withSet)(state)
  @inline final def factor(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.FactorizationControl.factor(cl)(state)
  @inline final def boolext(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.BoolExtControl.boolext(cl)(state)
  @inline final def primsubst(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.PrimSubstControl.primSubst(cl)(state)
  @inline final def unifyNewClauses(clSet: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.UnificationControl.unifyNewClauses(clSet)(state)
  // simplification inferences / preprocessing
  @inline final def cnf(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnf(cl)(sig)
  @inline final def cnfSet(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnfSet(cls)(sig)
  @inline final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.expandDefinitions(cl)(sig)
  @inline final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.miniscope(cl)(sig)
  @inline final def switchPolarity(cl: AnnotatedClause): AnnotatedClause = inferenceControl.SimplificationControl.switchPolarity(cl)
  @inline final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.liftEq(cl)(sig)
  @inline final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.funcext(cl)(sig)
  @inline final def extPreprocessUnify(clSet: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.SimplificationControl.extPreprocessUnify(clSet)(state)
  @inline final def acSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.acSimp(cl)(sig)
  @inline final def simp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.simp(cl)(sig)
  @inline final def simpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.simpSet(clSet)(sig)
  @inline final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.shallowSimp(cl)(sig)
  @inline final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.shallowSimpSet(clSet)(sig)
  @inline final def rewriteSimp(cl: AnnotatedClause, rewriteRules: Set[AnnotatedClause])(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.rewriteSimp(cl, rewriteRules)(sig)
  @inline final def convertDefinedEqualities(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertDefinedEqualities(clSet)(sig)
  @inline final def specialInstances(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SpecialInstantiationControl.specialInstances(cl)(sig)
  // AC detection
  @inline final def detectAC(cl: AnnotatedClause): Option[(Signature.Key, Boolean)] = inferenceControl.SimplificationControl.detectAC(cl)
  // Choice
  @inline final def instantiateChoice(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.ChoiceControl.instantiateChoice(cl)(state)
  @inline final def detectChoiceClause(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): Boolean = inferenceControl.ChoiceControl.detectChoiceClause(cl)(state)
  @inline final def guessFuncSpec(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.ChoiceControl.guessFuncSpec(cls)(state)
  // Redundancy
  @inline final def redundant(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit state: LocalFVState): Boolean = redundancyControl.RedundancyControl.redundant(cl, processed)
  @inline final def backwardSubsumptionTest(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit state: LocalFVState): Set[AnnotatedClause] = redundancyControl.SubsumptionControl.testBackwardSubsumptionFVI(cl)
  // Indexing
  @inline final def initIndexes(initClauses: Seq[AnnotatedClause])(implicit state: LocalFVState): Unit = indexingControl.IndexingControl.initIndexes(initClauses.toSet)(state)
  @inline final def insertIndexed(cl: AnnotatedClause)(implicit state: LocalFVState): Unit = indexingControl.IndexingControl.insertIndexed(cl)
  @inline final def insertIndexed(cls: Set[AnnotatedClause])(implicit state: LocalFVState): Unit = cls.foreach(insertIndexed)
  @inline final def removeFromIndex(cl: AnnotatedClause)(implicit state: LocalFVState): Unit = indexingControl.IndexingControl.removeFromIndex(cl)
  @inline final def removeFromIndex(cls: Set[AnnotatedClause])(implicit state: LocalFVState): Unit = cls.foreach(removeFromIndex)
  @inline final def updateDescendants(taken: AnnotatedClause, generated: Set[AnnotatedClause]): Unit = indexingControl.IndexingControl.updateDescendants(taken, generated)
  @inline final def descendants(cls: Set[AnnotatedClause]): Set[AnnotatedClause] = indexingControl.IndexingControl.descendants(cls)
  @inline final def resetIndexes(implicit state: State[AnnotatedClause]): Unit = indexingControl.IndexingControl.resetIndexes(state)
  // Relevance filtering
  @inline final def getRelevantAxioms(input: Seq[leo.datastructures.tptp.Commons.AnnotatedFormula], conjecture: leo.datastructures.tptp.Commons.AnnotatedFormula)(implicit sig: Signature): Seq[leo.datastructures.tptp.Commons.AnnotatedFormula] = indexingControl.RelevanceFilterControl.getRelevantAxioms(input, conjecture)(sig)
  @inline final def relevanceFilterAdd(formula: leo.datastructures.tptp.Commons.AnnotatedFormula)(implicit sig: Signature): Unit = indexingControl.RelevanceFilterControl.relevanceFilterAdd(formula)(sig)
  // External prover call
  @inline final def checkExternalResults(state: State[AnnotatedClause]): Seq[leo.modules.external.TptpResult[AnnotatedClause]] =  externalProverControl.ExtProverControl.checkExternalResults(state)
  @inline final def submit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = externalProverControl.ExtProverControl.submit(clauses, state)
  @inline final def killExternals(): Unit = externalProverControl.ExtProverControl.killExternals()
  // Limited resource scheduling
  @inline final def defaultStrategy(timeout: Int): RunStrategy = schedulingControl.StrategyControl.defaultStrategy(timeout)
  @inline final def generateRunStrategies: Iterator[RunStrategy] = schedulingControl.StrategyControl.generateRunStrategies
}

/** Package collection control objects for inference rules.
  *
  * @see [[leo.modules.calculus.CalculusRule]] */
package inferenceControl {
  import leo.datastructures.ClauseAnnotation.InferredFrom
  import leo.datastructures.Literal.Side
  import leo.datastructures._
  import leo.modules.calculus._
  import Control.LocalState
  package object inferenceControl {
    type LiteralIndex = Int
    type WithConfiguration = (LiteralIndex, Literal, Side)
  }


  protected[modules] object CNFControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    private lazy val internalCNF: (AnnotatedClause, Signature) => Set[AnnotatedClause] = if (Configuration.RENAMING_SET) cnf2 else cnf1
    private lazy val threshhold : Int = Configuration.RENAMING_THRESHHOLD

    final def cnf(cl : AnnotatedClause)(implicit sig : Signature) : Set[AnnotatedClause] = internalCNF(cl, sig)

    private final def cnf1(cl: AnnotatedClause, sig: Signature): Set[AnnotatedClause] = {
      Out.trace(s"Standard CNF of ${cl.pretty(sig)}")
      val cnfresult = FullCNF(leo.modules.calculus.freshVarGen(cl.cl), cl.cl)(sig).toSet
      if (cnfresult.size == 1 && cnfresult.head == cl.cl) {
        // no CNF step at all
        Out.trace(s"CNF result:\n\t${cl.pretty(sig)}")
        Set(cl)
      } else {
        val cnfsimp = cnfresult //.map(Simp.shallowSimp)
        val result = cnfsimp.map {c => AnnotatedClause(c, InferredFrom(FullCNF, cl), cl.properties)}
        Out.trace(s"CNF result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
        result
      }
    }

    private final def cnf2(cl: AnnotatedClause, sig: Signature): Set[AnnotatedClause] = {
      Out.trace(s"Rename CNF of ${cl.pretty(sig)}")
      val cnfresult = RenameCNF(leo.modules.calculus.freshVarGen(cl.cl), cl.cl)(sig).toSet
      if (cnfresult.size == 1 && cnfresult.head == cl.cl) {
        // no CNF step at all
        Out.trace(s"CNF result:\n\t${cl.pretty(sig)}")
        Set(cl)
      } else {
        val cnfsimp = cnfresult //.map(Simp.shallowSimp)
        val result = cnfsimp.map {c => AnnotatedClause(c, InferredFrom(RenameCNF, cl), cl.properties)} // TODO Definitions other way into the CNF.
        Out.trace(s"CNF result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
        result
      }
    }

    final def cnfSet(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      var result: Set[AnnotatedClause] = Set()
      val clsIt = cls.iterator
      while(clsIt.hasNext) {
        val cl = clsIt.next()
        result = result union cnf(cl)
      }
      result
    }
  }


  /**
    * Object that offers methods that filter/control how paramodulation steps between a claues
    * and a set of clauses (or between two individual clauses) will be executed.
    *
    * @author Alexander Steen <a.steen@fu-berlin.de>
    * @since 22.02.16
    */
  protected[modules] object ParamodControl {
    final def paramodSet(cl: AnnotatedClause, withset: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = {
      val sos = state.runStrategy.sos
      var results: Set[AnnotatedClause] = Set()
      val withsetIt = withset.iterator
      Out.debug(s"Paramod on ${cl.id} (SOS: ${leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, cl.properties)}) and processed set")
      while (withsetIt.hasNext) {
        val other = withsetIt.next()
        if (!sos || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, other.properties) ||
          leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, cl.properties))  {
          Out.finest(s"Paramod on ${cl.id} and ${other.id}")
          results = results ++ allParamods(cl, other)(state)
        }
      }
      if (results.nonEmpty) Out.trace(s"Paramod result: ${results.map(_.id).mkString(",")}")
      results
    }

    final def allParamods(cl: AnnotatedClause, other: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      // Do paramod with cl into other
      val res = allParamods0(cl, other)(state)
      if (cl.id != other.id) {
        // do paramod with other into cl
        res ++ allParamods0(other, cl)(state)
      } else res
    }

    final private def allParamods0(withWrapper: AnnotatedClause, intoWrapper: AnnotatedClause)(state: LocalState): Set[AnnotatedClause] = {
      import leo.datastructures.ClauseAnnotation.InferredFrom
      assert(!state.runStrategy.sos || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) ||
        leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties))

      val sig = state.signature
      var results: Set[AnnotatedClause] = Set()

      val withClause = withWrapper.cl
      val intoClause = intoWrapper.cl

      val withConfigurationIt = new LiteralSideIterator(withClause, true, true, false)(sig)
      while (withConfigurationIt.hasNext) {
        val (withIndex, withLit, withSide) = withConfigurationIt.next()
        val withTerm = Literal.selectSide(withLit, withSide)

        assert(withClause.lits(withIndex) == withLit, s"$withIndex in ${withClause.pretty(sig)}\n lit = ${withLit.pretty(sig)}")
        assert(withLit.polarity)

        val intoConfigurationIt = intoConfigurationIterator(intoClause)(sig)
        while (intoConfigurationIt.hasNext) {
          val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()
          assert(!intoLit.flexflex)
          if (intoPos == Position.root &&
            ((intoWrapper.id == withWrapper.id && intoIndex == withIndex) ||
              (!withLit.equational && !intoLit.equational && intoLit.polarity))) {
            /* skip, this generates a redundant clause */
          } else {
            val shouldParamod0 = shouldParamod(withTerm, intoTerm)
            leo.Out.finest(s"shouldParamod: $shouldParamod0\n\twith ${withTerm.pretty(sig)}\n\tinto: ${intoTerm.pretty(sig)}")
            if (!intoTerm.isVariable && shouldParamod0) {
              leo.Out.finest(s"ordered: ${withLit.oriented} // ${intoLit.oriented}")
              Out.trace(s"May unify: ${withTerm.pretty(sig)} with ${intoTerm.pretty(sig)} (subterm at ${intoPos.pretty})")
              Out.finest(s"with: ${withClause.pretty(sig)}")
              Out.finest(s"withside: ${withSide.toString}")
              Out.finest(s"into: ${intoClause.pretty(sig)}")
              Out.finest(s"intoside: ${intoSide.toString}")
              // We shift all lits from intoClause to make the universally quantified variables distinct from those of withClause.
              // We cannot use _.substitute on literal since this will forget the ordering
              val termShift = Subst.shift(withClause.maxImplicitlyBound)
              val typeShift = Subst.shift(withClause.maxTypeVar)
              val shiftedIntoClause: Clause = Clause(
                intoClause.lits.map {l =>
                  if (l.equational) {
                    // since we only lift variables, the orientation property isnt affected
                    // we may safely assume that the new lits are also orientable if the original ones were orientable.
                    Literal.mkLit(l.left.substitute(termShift, typeShift), l.right.substitute(termShift, typeShift), l.polarity, l.oriented)
                  } else {
                    Literal.mkLit(l.left.substitute(termShift, typeShift), l.polarity)
                  }
                }
              )
              val shiftedIntoTerm: Term = intoTerm.substitute(Subst.shift(withClause.maxImplicitlyBound-intoPos.abstractionCount), typeShift)
              Out.finest(s"shifted into: ${shiftedIntoClause.pretty(sig)}")
              Out.finest(s"shiftedIntoSubterm: ${shiftedIntoTerm.pretty(sig)}")

              val newCl = if (withTerm.ty == shiftedIntoTerm.ty) {
                // No type unification needed at this point
                OrderedParamod(withClause, withIndex, withSide,
                  shiftedIntoClause, intoIndex, intoSide, intoPos, shiftedIntoTerm)(sig)
              } else {
                import leo.modules.calculus.{TypeUnification => Unification}
                leo.Out.finest(s"Nonequal type, calculating type unification ...")
                leo.Out.finest(s"withTerm.ty: ${withTerm.ty.pretty(sig)}")
                leo.Out.finest(s"intoTerm.ty: ${shiftedIntoTerm.ty.pretty(sig)}")
                val tyUniResult = Unification(withTerm.ty, shiftedIntoTerm.ty)
                if (tyUniResult.isDefined) {
                  leo.Out.finest(s"Type unification succeeded.")
                  val tySubst = tyUniResult.get
                  leo.Out.finest(s"Type subst: ${tySubst.pretty}.")
                  // FIXME the clauses below are not ordered anymore. This needs to be fixed.
                  val substWithClause = withClause.substitute(Subst.id, tySubst)
                  val substIntoClause = shiftedIntoClause.substitute(Subst.id, tySubst)
                  val substIntoTerm = shiftedIntoTerm.substitute(Subst.id, tySubst)
                  OrderedParamod(substWithClause, withIndex, withSide,
                    substIntoClause, intoIndex, intoSide, intoPos, substIntoTerm)(sig)
                } else {
                  leo.Out.finest(s"Type unification failed.")
                  null
                }
              }
              if (newCl != null) { // May be null if type unification was not successful
                val newProperties = if (isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) || isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties))
                  ClauseAnnotation.PropNeedsUnification |  ClauseAnnotation.PropSOS
                else ClauseAnnotation.PropNeedsUnification
                val newClWrapper = AnnotatedClause(newCl, InferredFrom(OrderedParamod, Seq(withWrapper, intoWrapper)), newProperties)
                Out.finest(s"Result: ${newClWrapper.pretty(sig)}")
                myAssert(Clause.wellTyped(newCl), "paramod not well-typed")
                myAssert(uniqueFVTypes(newCl), "not unique free var types")
                results = results + newClWrapper
              }
            }
          }
        }
      }
      results
    }
    /** We should paramod if either the terms are unifiable or if at least one unification rule step can be executed. */
    private final def shouldParamod(withTerm: Term, intoTerm: Term): Boolean = {
      val withHd = withTerm.headSymbol
      val intoHd = intoTerm.headSymbol
      if (withHd == intoHd && withHd.isConstant && mayUnify(withTerm.ty, intoTerm.ty)) true
      else leo.modules.calculus.mayUnify(withTerm, intoTerm)
    }

    ////////////////////////////////////////////////////////
    // Utility for Paramod control
    ///////////////////////////////////////////////////////

    type Subterm = Term
    type IntoConfiguration = (inferenceControl.LiteralIndex, Literal, Side, Position, Subterm)

    final private def intoConfigurationIterator(cl: Clause)(implicit sig: Signature): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {
      import Literal.{leftSide, rightSide, selectSide}

      private val maxLits = Literal.maxOf(cl.lits)
      private var litIndex = 0
      private var lits = cl.lits
      private var side = leftSide
      private var curSubterms: Set[Term] = _
      private var curPositions: Set[Position] = _

      def hasNext: Boolean = if (lits.isEmpty) false
      else {
        val hd = lits.head
        if (!maxLits.contains(hd) || hd.flexflex) {
          lits = lits.tail
          litIndex += 1
          hasNext
        } else {
          if (curSubterms == null) {
            curSubterms = selectSide(hd, side).feasibleOccurrences.keySet
            curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
            true
          } else {
            if (curPositions.isEmpty) {
              curSubterms = curSubterms.tail
              if (curSubterms.isEmpty) {
                if (/*hd.oriented ||*/ side == rightSide) {
                  lits = lits.tail
                  litIndex += 1
                  side = leftSide
                } else {
                  side = rightSide
                }
                curSubterms = null
                curPositions = null
                hasNext
              } else {
                curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
                assert(hasNext)
                true
              }
            } else {
              true
            }
          }
        }

      }

      def next(): IntoConfiguration = {
        if (hasNext) {
          val res = (litIndex, lits.head, side, curPositions.head, curSubterms.head)
          curPositions = curPositions.tail
          res
        } else {
          throw new NoSuchElementException
        }
      }
    }
  }

  protected[modules] object FactorizationControl {

    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def factor(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      Out.debug(s"Factor in ${cl.id}")
      implicit val sig = state.signature
      var res: Set[AnnotatedClause] = Set()
      val clause = cl.cl
      val maxLitsofClause = Literal.maxOf(clause.lits)
      val maxLitIt = new LiteralSideIterator(clause, true, false, true)

      while (maxLitIt.hasNext) {
        val (maxLitIndex, maxLit, maxLitSide) = maxLitIt.next()
        Out.trace(s"maxLit chosen: ${maxLit.pretty(sig)}")
        val otherLitIt = new LiteralSideIterator(clause, false, false, true)

        while (otherLitIt.hasNext) {
          val (otherLitIndex, otherLit, otherLitSide) = otherLitIt.next()
          Out.trace(s"otherLit chosen: ${otherLit.pretty(sig)}")
          if (maxLitIndex <= otherLitIndex && maxLitsofClause.contains(otherLit) ) {
            Out.finest(s"skipped maxLit ${maxLit.pretty(sig)} with ${otherLit.pretty(sig)}")
            /* skipped since already tested */
          } else {
            if (maxLit.polarity == otherLit.polarity) {
              // same polarity, standard
              val (maxLitMaxSide, maxLitOtherSide) = Literal.getSidesOrdered(maxLit, maxLitSide)
              val (otherLitMaxSide, otherLitOtherSide) = Literal.getSidesOrdered(otherLit, otherLitSide)
              val test1 = shouldFactor(maxLitMaxSide, otherLitMaxSide)
              val test2 = shouldFactor(maxLitOtherSide, otherLitOtherSide)
              Out.finest(s"Should factor ($test1): ${maxLitMaxSide.pretty(sig)} = ${otherLitMaxSide.pretty(sig)}")
              Out.finest(s"Should factor ($test2): ${maxLitOtherSide.pretty(sig)} = ${otherLitOtherSide.pretty(sig)}")
              if (test1 && test2) {
                val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, otherLitSide)
                val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), cl.properties | ClauseAnnotation.PropNeedsUnification)
                res = res + result
              }
              // If equation is oriented, we still need to look at the side-switched version
              // of otherLit, since our iterator does not give us this test. It will give us this test
              // if otherLit is not oriented.
              if (otherLit.oriented) {
                val test1 = shouldFactor(maxLitMaxSide, otherLitOtherSide)
                val test2 = shouldFactor(maxLitOtherSide, otherLitMaxSide)
                Out.finest(s"Should factor ($test1): ${maxLitMaxSide.pretty(sig)} = ${otherLitOtherSide.pretty(sig)}")
                Out.finest(s"Should factor ($test2): ${maxLitOtherSide.pretty(sig)} = ${otherLitMaxSide.pretty(sig)}")
                if (test1 && test2) {
                  val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, !otherLitSide)
                  val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), cl.properties | ClauseAnnotation.PropNeedsUnification)
                  res = res + result
                }
              }
            } else {
              // Different polarity, this can only work out if at least one of the literals
              // is a flexhead, i.e. a literal `l` with `l = [s = $true]^alpha` where head(s) is a variable.
              // The other literal l` must then be non-equational.
              // This is not traversed again since bot literals are oriented.
              if (maxLit.flexHead && !otherLit.equational) {
                assert(maxLit.polarity != otherLit.polarity)
                import leo.modules.HOLSignature.Not
                val flexTerm = maxLit.left
                val otherTerm = otherLit.left
                val test = shouldFactor(flexTerm, Not(otherTerm))
                Out.finest(s"Should factor ($test): ${flexTerm.pretty(sig)} = ${Not(otherTerm).pretty(sig)}")
                if (test) {
                  val adjustedClause = Clause(clause.lits.updated(otherLitIndex, Literal(Not(otherTerm), !otherLit.polarity)))
                  val factor = OrderedEqFac(adjustedClause, maxLitIndex, Literal.leftSide, otherLitIndex, Literal.leftSide)
                  val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), cl.properties | ClauseAnnotation.PropNeedsUnification)
                  res = res + result
                }
              }
              // Not clear if we also want the other way around: Since maxlit would be removed by EqFac
            }
          }
        }
      }

      Out.trace(s"Factor result:\n\t${res.map(_.pretty(sig)).mkString("\n\t")}")
      res
    }

    /** We should paramod if either the terms are unifiable or if at least one unification rule step can be executed. */
    private final def shouldFactor(term: Term, otherTerm: Term): Boolean = {
      val withHd = term.headSymbol
      val intoHd = otherTerm.headSymbol
      if (withHd == intoHd && withHd.isConstant && mayUnify(term.ty, otherTerm.ty)) true
      else leo.modules.calculus.mayUnify(term, otherTerm)
    }
  }

  protected[modules] object UnificationControl {
    import leo.datastructures.ClauseAnnotation._
    import leo.modules.output.ToTPTP

    type UniLits = Seq[(Term, Term)]
    type OtherLits = Seq[Literal]
    type UniResult = (Clause, (Unification#TermSubst, Unification#TypeSubst))

    // TODO: Flags, check for types in pattern unification
    final def unifyNewClauses(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = {
      val sig = state.signature
      var resultSet: Set[AnnotatedClause] = Set()
      val clsIt = cls.iterator

      while(clsIt.hasNext) {
        val cl = clsIt.next()

        if (leo.datastructures.isPropSet(ClauseAnnotation.PropNeedsUnification, cl.properties)) {
          Out.trace(s"Clause ${cl.id} needs unification. Working on it ...")
          Out.trace(s"Clause ${cl.pretty(sig)} needs unification. Working on it ...")
          Out.trace(s"FV(${cl.id}) = ${cl.cl.implicitlyBound.toString()}")
          val vargen = leo.modules.calculus.freshVarGen(cl.cl)

          val results = if (cl.annotation.fromRule == null) {
            defaultUnify(vargen, cl)(state)
          } else {
            val fromRule = cl.annotation.fromRule
            if (fromRule == OrderedParamod) {
              paramodUnify(vargen, cl)(state)
            } else if (fromRule == OrderedEqFac) {
              factorUnify(vargen, cl)(state)
            } else {
              defaultUnify(vargen, cl)(state)
            }
          }
          Out.trace(s"Uni result:\n\t${results.map(_.pretty(sig)).mkString("\n\t")}")
          results.foreach(cl =>
            Out.trace(s"FV(${cl.id}) = ${cl.cl.implicitlyBound.toString()}")
          )
          resultSet = resultSet union results
        } else resultSet = resultSet + cl
      }
      resultSet
    }

    private final def paramodUnify(freshVarGen: FreshVarGen, cl0: AnnotatedClause)(state: LocalState): Set[AnnotatedClause] = {
      import leo.modules.HOLSignature.LitFalse
      val sig = state.signature
      val cl = cl0.cl
      assert(cl.lits.nonEmpty)
      val uniLit = cl.lits.last

      val uniEq = if (!uniLit.polarity) Vector((uniLit.left, uniLit.right)) /*standard case*/
      else {
        assert(!uniLit.equational)
        Seq((uniLit.left, LitFalse.apply())) /* in case a False was substituted in paramod */
      }
      val uniResult0 = doUnify0(cl0, freshVarGen, uniEq, cl.lits.init)(state)
      // 1 if not unifiable, check if uni constraints can be simplified
      // if it can be simplified, return simplified constraints
      // if it cannot be simplied, drop clause
      // 2 if unifiable, reunify again with all literals (simplified)
      if (uniResult0.isEmpty) {
        Out.finest(s"Unification failed, but looking for uni simp.")
        if (!uniLit.polarity) {
          val (simpSubst, simpResult) = Simp.uniLitSimp(uniLit)(sig)
          Out.finest(s"Unification simp: ${simpResult.map(_.pretty)}")
          if (simpResult.size == 1 && simpResult.head == uniLit) Set()
          else {
            val substitutedRemainingLits = if (simpSubst == Subst.id) cl.lits.init
            else cl.lits.init.map(_.substituteOrdered(Subst.id, simpSubst)(sig))
            val resultClause = Clause(substitutedRemainingLits ++ simpResult)
            val res = AnnotatedClause(resultClause, InferredFrom(Simp, cl0), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,cl0.properties | ClauseAnnotation.PropUnified))
            Out.finest(s"No unification, but Uni Simp result: ${res.pretty(sig)}")
            myAssert(Clause.wellTyped(res.cl), "uniSimp not well-typed")
            Set(res)
          }
        } else Set()
      } else {
        var uniResult: Set[AnnotatedClause] = Set()
        val uniResultIt = uniResult0.iterator
        while (uniResultIt.hasNext) {
          val uniRes = uniResultIt.next()
          uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(state)
        }
        uniResult
      }
    }

    private final def factorUnify(freshVarGen: FreshVarGen, cl0: AnnotatedClause)(state: LocalState): Set[AnnotatedClause] = {
      import leo.modules.HOLSignature.LitFalse
      val sig = state.signature
      val cl = cl0.cl
      assert(cl.lits.size >= 2)
      val uniLit1 = cl.lits.last
      val uniLit2 = cl.lits.init.last

      val uniEq1 = if (!uniLit1.polarity) (uniLit1.left, uniLit1.right) /*standard case*/
      else {
        assert(!uniLit1.equational)
        (uniLit1.left, LitFalse()) /* in case a False was substituted in factor */
      }
      val uniEq2 = if (!uniLit2.polarity) (uniLit2.left, uniLit2.right) /*standard case*/
      else {
        assert(!uniLit2.equational)
        (uniLit2.left, LitFalse()) /* in case a False was substituted in factor */
      }
      val uniResult0 = doUnify0(cl0, freshVarGen, Vector(uniEq1, uniEq2), cl.lits.init.init)(state)
      // 1 if not unifiable, check if uni constraints can be simplified
      // if it can be simplified, return simplified constraints
      // if it cannot be simplied, drop clause
      // 2 if unifiable, reunify again with all literals (simplified)
      if (uniResult0.isEmpty) {
        var wasSimplified = false
        val (simpSubst1, uniLit1Simp) = if (!uniLit1.polarity) {
          val (simpSubst1, simpResult1) = Simp.uniLitSimp(uniLit1)(sig)
          if (simpResult1.size == 1 && simpResult1.head == uniLit1) (Subst.id, Seq(uniLit1))
          else { wasSimplified = true; (simpSubst1,simpResult1) }
        } else (Subst.id, Seq(uniLit1))
        val (simpSubst2, uniLit2Simp) = if (!uniLit2.polarity) {
          val (simpSubst2, simpResult2) = Simp.uniLitSimp(uniLit2.substitute(Subst.id, simpSubst1))(sig)
          if (simpResult2.size == 1 && simpResult2.head == uniLit2) (Subst.id, Seq(uniLit2))
          else { wasSimplified = true; (simpSubst2, simpResult2) }
        } else (Subst.id,Seq(uniLit2.substituteOrdered(Subst.id, simpSubst1)(sig)))
        if (wasSimplified) {
          val substitutedRemainingLits = cl.lits.init.init.map(_.substituteOrdered(Subst.id, simpSubst1.comp(simpSubst2))(sig))
          val resultClause = Clause(substitutedRemainingLits ++ uniLit1Simp ++ uniLit2Simp)
          val res = AnnotatedClause(resultClause, InferredFrom(Simp, cl0), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,cl0.properties | ClauseAnnotation.PropUnified))
          Out.finest(s"Uni Simp result: ${res.pretty(sig)}")
          Set(res)
        } else Set()
      } else {
        var uniResult: Set[AnnotatedClause] = Set()
        val uniResultIt = uniResult0.iterator
        while (uniResultIt.hasNext) {
          val uniRes = uniResultIt.next()
          uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(state)
        }
        uniResult
      }
    }

    private final def defaultUnify(freshVarGen: FreshVarGen, cl: AnnotatedClause)(state: LocalState): Set[AnnotatedClause] = {
      val sig = state.signature
      val litIt = cl.cl.lits.iterator
      var uniLits: UniLits = Vector()
      var otherLits:OtherLits = Vector()
      while(litIt.hasNext) {
        val lit = litIt.next()
        if (lit.equational && !lit.polarity) {
          uniLits = (lit.left,lit.right) +: uniLits
        } else {
          otherLits = lit +: otherLits
        }
      }
      if (uniLits.nonEmpty) {
        val uniResult = doUnify0(cl, freshVarGen, uniLits, otherLits)(state)
        // all negative literals are taken as unification constraints
        // if no unifier is found, the original clause is unisimp'd and returned
        // else the unified clause is unisimp*d and returned
        if (uniResult.isEmpty) {
          val uniLits = cl.cl.negLits
          val (simpSubst, uniLitsSimp) = Simp.uniLitSimp(uniLits)(sig)
          if (uniLits == uniLitsSimp) Set(cl)
          else {
            val substPosLits = cl.cl.posLits.map(_.substituteOrdered(Subst.id, simpSubst)(sig))
            Set(AnnotatedClause(Clause(substPosLits ++ uniLitsSimp), InferredFrom(Simp, cl), cl.properties))
          }
        } else {
          val resultClausesIt = uniResult.iterator
          var resultClausesSimp: Set[AnnotatedClause] = Set()
          while (resultClausesIt.hasNext) {
            val resultClause = resultClausesIt.next()
            val uniLits = resultClause.cl.negLits
            val (simpSubst, uniLitsSimp) = Simp.uniLitSimp(uniLits)(sig)
            if (uniLits == uniLitsSimp)  resultClausesSimp = resultClausesSimp +  resultClause
            else {
              val substPosLits = resultClause.cl.posLits.map(_.substituteOrdered(Subst.id, simpSubst)(sig))
              resultClausesSimp = resultClausesSimp + AnnotatedClause(Clause(substPosLits ++ uniLitsSimp), InferredFrom(Simp, resultClause), resultClause.properties)
            }
          }
          resultClausesSimp
        }
      } else Set(cl)
    }


    protected[control] final def doUnify0(cl: AnnotatedClause, freshVarGen: FreshVarGen,
                               uniLits: UniLits, otherLits: OtherLits)(state: LocalState):  Set[AnnotatedClause] = {
      val sig = state.signature
      if (isAllPattern(uniLits)) {
        val result = PatternUni.apply(freshVarGen, uniLits, otherLits)(sig)
        if (result.isEmpty) Set()
        else Set(annotate(cl, result.get, PatternUni)(sig))
      } else {
        val uniResultIterator = PreUni(freshVarGen, uniLits, otherLits, state.runStrategy.uniDepth)(sig)
        val uniResult = uniResultIterator.take(state.runStrategy.unifierCount).toSet
        uniResult.map(annotate(cl, _, PreUni)(sig))
      }
    }

    private final def isAllPattern(uniLits: UniLits): Boolean = {
      val uniLitIt = uniLits.iterator
      while (uniLitIt.hasNext) {
        val uniLit = uniLitIt.next()
        if (!PatternUnification.isPattern(uniLit._1)) return false
        if (!PatternUnification.isPattern(uniLit._2)) return false
      }
      true
    }

    private final def annotate(origin: AnnotatedClause,
                               uniResult: UniResult,
                               rule: CalculusRule)(sig: Signature): AnnotatedClause = {
      val (clause, subst) = uniResult
      AnnotatedClause(clause, InferredFrom(rule, Seq((origin, ToTPTP(subst._1, origin.cl.implicitlyBound)(sig)))), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,origin.properties | ClauseAnnotation.PropUnified))
    }


  }


  protected[modules] object BoolExtControl {
    import leo.datastructures.ClauseAnnotation._

    final def boolext(cw: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      val sig = state.signature
      if (state.runStrategy.boolExt) {
        if (!leo.datastructures.isPropSet(PropBoolExt, cw.properties)) {
          val (cA_boolExt, bE, bE_other) = BoolExt.canApply(cw.cl)
          if (cA_boolExt) {
            Out.debug(s"Bool Ext on: ${cw.pretty(sig)}")
            val result = BoolExt.apply(bE, bE_other).map(AnnotatedClause(_, InferredFrom(BoolExt, cw),cw.properties | ClauseAnnotation.PropBoolExt))
            Out.trace(s"Bool Ext result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
            result
          } else Set()
        } else Set()
      } else Set()
    }
  }

  protected[modules] object PrimSubstControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom
    import leo.modules.output.ToTPTP
    import leo.modules.HOLSignature.{Not, LitFalse, LitTrue, |||, ===, !===}

    val standardbindings: Set[Term] = Set(Not, LitFalse(), LitTrue(), |||)
    final def eqBindings(tys: Seq[Type]): Set[Term] = {
      leo.Out.trace(s"eqBindings on type: ${tys.map(_.pretty)}")
      if (tys.size == 2) {
        leo.Out.trace(s"eqBindings two arguments")
        val (ty1, ty2) = (tys.head, tys.tail.head)
        if (ty1 == ty2) {
          leo.Out.trace(s"same type")
          Set(  // lambda abstraction intentionally removed: they are added by partialBinding call in primSubst(.)
            /*Term.λ(ty1, ty1)*/Term.mkTermApp(Term.mkTypeApp(===, ty1), Seq(Term.mkBound(ty1, 2),Term.mkBound(ty1, 1))),
            /*Term.λ(ty1, ty1)*/Term.mkTermApp(Term.mkTypeApp(!===, ty1), Seq(Term.mkBound(ty1, 2),Term.mkBound(ty1, 1)))
          )
        } else Set()
      } else Set()
    }
    final def specialEqBindings(terms: Set[Term], typs: Seq[Type]): Set[Term] = {
      if (typs.size == 1) {
        val typ = typs.head
        val compatibleTerms = terms.filter(_.ty == typ)
        // lambda abstraction intentionally removed: they are added by partialBinding call in primSubst(.)
        compatibleTerms.map(t => Term.mkTermApp(Term.mkTypeApp(===, typ), Seq(t.lift(1), Term.mkBound(typ, 1))))
      } else Set()
    }

    final def primSubst(cw: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      implicit val sig = state.signature
      val level = state.runStrategy.primSubst
      if (level > 0) {
        val (cA_ps, ps_vars) = PrimSubst.canApply(cw.cl)
        if (cA_ps) {
          // Every variable in ps_vars has type a_1 -> ... -> a_n -> o (n >= 0)
          Out.debug(s"[Prim subst] On ${cw.id}")
          var primsubstResult = PrimSubst(cw.cl, ps_vars, standardbindings)
          if (level > 1) {
            primsubstResult = primsubstResult union ps_vars.flatMap{h =>
              val (ty,idx) = Term.Bound.unapply(h).get
              val eligibleConstants = sig.uninterpretedSymbolsOfType(ty).map(Term.mkAtom)
              eligibleConstants.map{c =>
                val subst = Subst.singleton(idx, c)
                (cw.cl.substituteOrdered(subst),subst)}
            }
            if (level > 2) {
              primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), eqBindings(h.ty.funParamTypes)))
              if (level > 3) {
                primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), specialEqBindings(cw.cl.implicitlyBound.map(a => Term.mkBound(a._2, a._1)).toSet, h.ty.funParamTypes)))
                if (level > 4) {
                  primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), specialEqBindings(sig.uninterpretedSymbols.map(Term.mkAtom), h.ty.funParamTypes)))
                }
              }
            }
          }
          val newCl = primsubstResult.map{case (cl,subst) => AnnotatedClause(cl, InferredFrom(PrimSubst, Seq((cw,ToTPTP(subst, cw.cl.implicitlyBound)))), cw.properties)}
          Out.trace(s"Prim subst result:\n\t${newCl.map(_.pretty(sig)).mkString("\n\t")}")
          return newCl
        }
        Set()
      } else Set()
    }
  }

  protected[modules] object SpecialInstantiationControl {
    import leo.modules.calculus.Enumeration._
    import leo.Configuration.{PRE_PRIMSUBST_LEVEL => LEVEL, PRE_PRIMSUBST_MAX_DEPTH => MAXDEPTH}

    final def specialInstances(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
      if (LEVEL != NO_REPLACE) {
        leo.Out.trace("[Special Instances] Searching ...")
        val clause = cl.cl
        assert(Clause.unit(clause))
        val lit = clause.lits.head
        assert(!lit.equational)
        val term = lit.left
        val instancesResult = instantiateTerm(term, lit.polarity, 0)(sig)
        val result = instancesResult.map (r =>
          if (r == term)
            cl
          else {
            val result = AnnotatedClause(Clause(Literal(r, lit.polarity)), InferredFrom(Enumeration, cl), cl.properties)
            val simpResult = SimplificationControl.shallowSimp(result)(sig)
            simpResult
          }
        )
        leo.Out.trace(s"[Special Instances] Instances used:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
        result
      } else Set(cl)
    }

    final def instantiateTerm(t: Term, polarity: Boolean, depth: Int)(sig: Signature): Set[Term] = {
      import leo.datastructures.Term._
      import leo.modules.HOLSignature.{Forall, Exists, Not, Impl}

      if (depth >= MAXDEPTH)
        Set(t)
      else {
        t match {
          case Not(body) =>
            val erg = instantiateTerm(body, !polarity, depth+1)(sig)
            erg.map(e => Not(e))
          case Impl(l,r) =>
            val ergL = instantiateTerm(l, !polarity, depth+1)(sig)
            val ergR = instantiateTerm(r, polarity, depth+1)(sig)
            var result: Set[Term] = Set()
            val ergLIt = ergL.iterator
            while (ergLIt.hasNext) {
              val eL = ergLIt.next()
              val ergRIt = ergR.iterator
              while (ergRIt.hasNext) {
                val eR = ergRIt.next()
                val impl = Impl(eL, eR)
                result = result + impl
              }
            }
            result
          case Forall(all@(ty :::> _)) if polarity && shouldReplace(ty) =>
            val r = instantiateAbstractions(all, ty)(sig)
            val r2 = r.flatMap(rr => instantiateTerm(rr, polarity, depth+1)(sig))
            if (Enumeration.exhaustive(ty))
              r2
            else
              r2 + t
          case Exists(all@(ty :::> _)) if !polarity && shouldReplace(ty) =>
            val r = instantiateAbstractions(all, ty)(sig)
            val r2 = r.flatMap(rr => instantiateTerm(rr, polarity, depth+1)(sig))
            if (Enumeration.exhaustive(ty))
              r2
            else
              r2 + t
          case hd ∙ args =>
            val argsIt = args.iterator
            var newArgs: Set[Seq[Either[Term, Type]]] = Set(Vector())
            while (argsIt.hasNext) {
              val arg = argsIt.next()
              if (arg.isRight) {
                newArgs = newArgs.map(seq => seq :+ arg)
              } else {
                val termArg = arg.left.get
                val erg = instantiateTerm(termArg, polarity, depth+1)(sig)
                newArgs = newArgs.flatMap(seq => erg.map(e => seq :+ Left(e)))
              }
            }
            newArgs.map(erg => Term.mkApp(hd, erg))
          case ty :::> body =>
            val erg = instantiateTerm(body, polarity, depth+1)(sig)
            erg.map(e => Term.mkTermAbs(ty, e))
          case TypeLambda(body) =>
            val erg = instantiateTerm(body, polarity, depth+1)(sig)
            erg.map(e => Term.mkTypeAbs(e))
          case _ => Set(t)
        }
      }
    }

    private final def instantiateAbstractions(term: Term, ty: Type)(sig: Signature): Set[Term] = {
      assert(term.ty.isFunType)
      leo.Out.finest(s"[Special Instances]: Apply for ${ty.pretty(sig)}?")
      leo.Out.finest(s"[Special Instances]: REPLACE_O: ${isPropSet(REPLACE_O,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_OO: ${isPropSet(REPLACE_OO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_OOO: ${isPropSet(REPLACE_OOO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_AO: ${isPropSet(REPLACE_AO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_AAO: ${isPropSet(REPLACE_AAO,LEVEL)}")
      if (shouldReplace(ty)) {
        leo.Out.finest(s"[Special Instances]: Should apply.")
        val instances = Enumeration.specialInstances(ty, LEVEL)(sig)
        if (instances.nonEmpty) {
          leo.Out.trace(s"[Special Instances]: Used (${instances.size}): ${instances.map(_.pretty(sig))}")
          instances.map(i => Term.mkTermApp(term, i).betaNormalize)
        } else Set()
      } else Set()
    }

    private final def shouldReplace(ty: Type): Boolean = {
      import leo.modules.HOLSignature.o
      import leo.modules.calculus.Enumeration._

      val funTyArgs = ty.funParamTypesWithResultType
      if (funTyArgs.last == o) {
        if (funTyArgs.size == 1) isPropSet(REPLACE_O, Configuration.PRE_PRIMSUBST_LEVEL) // Booleans
        else {
          // funTyArgs.size > 1
          if (funTyArgs.size == 2 && funTyArgs.head == o) isPropSet(REPLACE_OO, Configuration.PRE_PRIMSUBST_LEVEL)
          else if (funTyArgs.size == 3 && funTyArgs.head == o && funTyArgs.tail.head == o) isPropSet(REPLACE_OOO, Configuration.PRE_PRIMSUBST_LEVEL)
          else {
            if (isPropSet(REPLACE_AO, Configuration.PRE_PRIMSUBST_LEVEL)) true
            else {
              if (funTyArgs.size == 3) {
                val ty1 = funTyArgs.head; val ty2 = funTyArgs.tail.head
                (ty1 == ty2) && isPropSet(REPLACE_AAO,Configuration.PRE_PRIMSUBST_LEVEL)
              } else false
            }
          }
        }
      } else false
    }
  }

  protected[modules] object ChoiceControl {
    import leo.modules.calculus.{Choice => ChoiceRule}
    import leo.datastructures.ClauseAnnotation.FromSystem
    /* This is for the proof output: Generate a clause with the axiom of choice
    * for some type as parent to the instantiateChoice rule. */
    private var acMap: Map[Type, AnnotatedClause] = Map()
    final def axiomOfChoice(ty: Type): AnnotatedClause = acMap.getOrElse(ty, newACInstance(ty))

    final def newACInstance(ty: Type): AnnotatedClause = {
      import leo.modules.HOLSignature._
      import leo.datastructures.Term.{mkBound,λ, mkTermApp}
      val lit = Literal.mkLit(Exists(λ((ty ->: o) ->: ty)(
        Forall(λ(ty ->: o)(
          Impl(
            Exists(λ(ty)(
                mkTermApp(mkBound(ty ->: o, 2), mkBound(ty, 1))
            )),
            mkTermApp(
              mkBound(ty ->: o, 1),
              mkTermApp(
                mkBound((ty ->: o) ->: ty, 2),
                mkBound(ty ->: o, 1)
              )
            )
          )
        ))
      )), true)
      val res = AnnotatedClause(Clause(lit), Role_Axiom, FromSystem("axiom_of_choice"), ClauseAnnotation.PropNoProp)
      acMap = acMap + ((ty, res))
      res
    }
    /** Proof output end **/

    final def detectChoiceClause(cw: AnnotatedClause)(state: State[AnnotatedClause]): Boolean = {
      if (!state.runStrategy.choice) false
      else {
        val maybeChoiceFun = ChoiceRule.detectChoice(cw.cl)
        if (maybeChoiceFun.isDefined) {
          val choiceFun = maybeChoiceFun.get
          state.addChoiceFunction(choiceFun)
          leo.Out.debug(s"[Choice] Detected ${choiceFun.pretty(state.signature)}")
          true
        } else false
      }
    }

    final def instantiateChoice(cw: AnnotatedClause)(state: State[AnnotatedClause]): Set[AnnotatedClause] = {
      if (!state.runStrategy.choice) Set()
      else {
        val cl = cw.cl
        val choiceFuns = state.choiceFunctions
        val sig = state.signature
        Out.trace(s"[Choice] Searching for possible choice terms...")
        val candidates = ChoiceRule.canApply(cl, choiceFuns)(sig)
        if (candidates.nonEmpty) {
          Out.finest(s"[Choice] Found possible choice term.")
          var results: Set[AnnotatedClause] = Set()
          val candidateIt = candidates.iterator
          while(candidateIt.hasNext) {
            val candPredicate = candidateIt.next()
            // type is (alpha -> o), alpha is choice type
            val choiceType: Type = candPredicate.ty._funDomainType

            if (choiceFuns.contains(choiceType)) {
              // Instantiate with all registered choice functions
              val choiceFunsForChoiceType = choiceFuns(choiceType)
              val choiceFunIt = choiceFunsForChoiceType.iterator
              while (choiceFunIt.hasNext) {
                val choiceFun = choiceFunIt.next()
                val result0 = ChoiceRule(candPredicate, choiceFun)
                val result = AnnotatedClause(result0, InferredFrom(ChoiceRule, axiomOfChoice(choiceType)))
                results = results + result
              }
            } else {
              // No choice function registered, introduce one now
              val choiceFun = registerNewChoiceFunction(choiceType)
              val result0 = ChoiceRule(candPredicate, choiceFun)
              val result = AnnotatedClause(result0, InferredFrom(ChoiceRule, axiomOfChoice(choiceType)))
              results = results + result
            }
          }
          Out.finest(s"[Choice] Instantiate choice for terms: ${candidates.map(_.pretty(sig)).mkString(",")}")
          Out.trace(s"[Choice] Results: ${results.map(_.pretty(sig)).mkString(",")}")
          results
        } else Set()
      }
    }

    final def registerNewChoiceFunction(ty: Type): Term = {
      import leo.modules.HOLSignature.Choice
      Term.mkTypeApp(Choice, ty)
    }

    final def guessFuncSpec(cls: Set[AnnotatedClause])(state: LocalState): Set[AnnotatedClause] = {
      cls.flatMap(guessFuncSpec(_)(state))
    }

    final def guessFuncSpec(cw: AnnotatedClause)(state: LocalState): Set[AnnotatedClause] = {
      import leo.datastructures.Term.TermApp
      implicit val sig = state.signature
      leo.Out.finest(s"call guesFuncSpec on ${cw.id}")
      val cl = cw.cl
      val uniLits = cl.negLits.filter(_.uni)
      leo.Out.finest(s"call guesFuncSpec on ${uniLits.map(_.pretty(sig)).mkString("\n")}")
      var collectedSpecs: Map[Term, Seq[(Seq[Term], Term)]] = Map.empty.withDefaultValue(Seq.empty)
      val uniLitsIt = uniLits.iterator
      while (uniLitsIt.hasNext) {
        val uniLit = uniLitsIt.next()
        leo.Out.finest(s"check: ${uniLit.pretty(sig)}")
        val (l,r) = Literal.getSidesOrdered(uniLit, Literal.leftSide)
        val (flexSide, otherSide) = if (l.flexHead && l.isApp) (l,r) else (r,l)
        leo.Out.finest(s"flexSide: ${flexSide.pretty(sig)}")
        leo.Out.finest(s"otherSide: ${otherSide.pretty(sig)}")
        if (flexSide.flexHead && flexSide.isApp) {
          val maybeArgs = TermApp.unapply(flexSide)
          if (maybeArgs.isDefined) {
            val (hd, args) = maybeArgs.get
            assert(hd.isVariable)
            val alreadyCollected = collectedSpecs(hd)
            val alreadyCollected0 = alreadyCollected :+ (args, otherSide)
            collectedSpecs = collectedSpecs + (hd -> alreadyCollected0)
          }
        }
      }
      Out.finest(s"Collected specs:\n" +
        collectedSpecs.map {case (hd, spec) => hd.pretty + ":\n" + spec.map(s => s._1.map(_.pretty(sig)).mkString(",") + " = " + s._2.pretty(sig)).mkString("\t\n")}.mkString("\n\n"))
      var result: Set[AnnotatedClause] = Set.empty
      collectedSpecs.foreach {case (hd, specs) =>
        val a = SolveFuncSpec.apply(hd.ty, specs)(sig)
        val hdIdx = Term.Bound.unapply(hd).get._2
          result = result + AnnotatedClause(cl.substituteOrdered(Subst.singleton(hdIdx, a))(sig), FromSystem("choice instance"), cw.properties)
      }
      Out.finest(s"FunSpec result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")

      result
    }
  }

  protected[modules] object SimplificationControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def switchPolarity(cl: AnnotatedClause): AnnotatedClause = {
      val litsIt = cl.cl.lits.iterator
      var newLits: Seq[Literal] = Seq()
      var wasApplied = false
      while(litsIt.hasNext) {
        val lit = litsIt.next()
        if (PolaritySwitch.canApply(lit)) {
          wasApplied = true
          newLits = newLits :+ PolaritySwitch(lit)
        } else {
          newLits = newLits :+ lit
        }
      }
      if (wasApplied) {
        val result = AnnotatedClause(Clause(newLits), InferredFrom(PolaritySwitch, cl), cl.properties)
        Out.trace(s"Switch polarity: ${result.pretty}")
        result
      } else
        cl

    }

    /** Pre: Is only called on initial clauses, i.e. clauses are not equaltional and unit. */
    final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      import leo.modules.calculus.Miniscope
      if (Clause.empty(cl.cl)) return cl

      assert(Clause.unit(cl.cl))
      assert(!cl.cl.lits.head.equational)

      val lit = cl.cl.lits.head
      val term = lit.left
      val resultterm = Miniscope.apply(term, lit.polarity)
      val result = if (term != resultterm)
          AnnotatedClause(Clause(Literal(resultterm, lit.polarity)), InferredFrom(Miniscope, cl), cl.properties)
        else
          cl
      Out.trace(s"Miniscope Result: ${result.pretty(sig)}")
      result
    }


    final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      if (cl.annotation.fromRule != null && cl.annotation.fromRule == DefExpSimp) cl
      else {
        assert(Clause.unit(cl.cl))
        val lit = cl.cl.lits.head
        assert(!lit.equational)
        val newleft = DefExpSimp(lit.left)(sig)
        val result = AnnotatedClause(Clause(Literal(newleft, lit.polarity)), InferredFrom(DefExpSimp, cl), cl.properties)
        Out.trace(s"Def expansion: ${result.pretty(sig)}")
        result
      }
    }

    final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      val (cA_lift, posLift, negLift, lift_other) = LiftEq.canApply(cl.cl)
      if (cA_lift) {
        val result = AnnotatedClause(Clause(LiftEq(posLift, negLift, lift_other)(sig)), InferredFrom(LiftEq, cl), deleteProp(ClauseAnnotation.PropBoolExt,cl.properties))
        Out.trace(s"to_eq: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      val (cA_funcExt, fE, fE_other) = FuncExt.canApply(cl.cl)
      if (cA_funcExt) {
        Out.finest(s"Func Ext on: ${cl.pretty(sig)}")
        Out.finest(s"TyFV(${cl.id}): ${cl.cl.typeVars.toString()}")
        val result = AnnotatedClause(Clause(FuncExt(leo.modules.calculus.freshVarGen(cl.cl),fE) ++ fE_other), InferredFrom(FuncExt, cl), deleteProp(ClauseAnnotation.PropBoolExt,cl.properties))
        myAssert(Clause.wellTyped(result.cl), "func ext not well-typed")
        Out.finest(s"Func Ext result: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def extPreprocessUnify(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = {
      import UnificationControl.doUnify0
      implicit val sig = state.signature
      var result: Set[AnnotatedClause] = Set()
      val clIt = cls.iterator

      while(clIt.hasNext) {
        val cl = clIt.next

        leo.Out.finest(s"[ExtPreprocessUnify] On ${cl.id}")
        leo.Out.finest(s"${cl.pretty(sig)}")
        var uniLits: Seq[Literal] = Vector()
        var nonUniLits: Seq[Literal] = Vector()
        var boolExtLits: Seq[Literal] = Vector()
        var nonBoolExtLits: Seq[Literal] = Vector()

        val litIt = cl.cl.lits.iterator

        while(litIt.hasNext) {
          val lit = litIt.next()
          if (!lit.polarity && lit.equational) uniLits = lit +: uniLits
          else nonUniLits = lit +: nonUniLits
          if (BoolExt.canApply(lit)) boolExtLits = lit +: boolExtLits
          else nonBoolExtLits = lit +: nonBoolExtLits
        }

        // (A) if unification literal is present, try to unify the set of unification literals as a whole
        // and add it to the solutions
        // (B) if also boolean extensionality literals present, add (BE/cnf) treated clause to result set, else
        // insert the original clause.
        if (uniLits.nonEmpty) result = result union doUnify0(cl, freshVarGen(cl.cl), uniLits.map(l => (l.left, l.right)), nonUniLits)(state)

        if (boolExtLits.isEmpty) {
          val (tySubst, res) = Simp.uniLitSimp(uniLits)(sig)
          if (res == uniLits) result = result + cl
          else {
            val newCl = AnnotatedClause(Clause(res ++ nonUniLits.map(_.substituteOrdered(Subst.id, tySubst))), InferredFrom(Simp, cl), cl.properties)
            val simpNewCl = Control.simp(newCl)(sig)
            result = result + cl + simpNewCl
          }
        } else {
          leo.Out.finest(s"Detecting Boolean extensionality literals, inserted expanded clauses...")
          val boolExtResult = BoolExt.apply(boolExtLits, nonBoolExtLits).map(AnnotatedClause(_, InferredFrom(BoolExt, cl),cl.properties | ClauseAnnotation.PropBoolExt))
          val cnf = CNFControl.cnfSet(boolExtResult)
          val lifted = cnf.map(Control.liftEq)
          val liftedIt = lifted.iterator
          while (liftedIt.hasNext) {
            val liftedCl = Control.shallowSimp(liftedIt.next())
            result = result + liftedCl
            val (liftedClUniLits, liftedClOtherLits) = liftedCl.cl.lits.partition(_.uni)
            val liftedUnified = doUnify0(cl, freshVarGen(liftedCl.cl), liftedClUniLits.map(l => (l.left, l.right)), liftedClOtherLits)(state)
            if (liftedUnified.isEmpty) {
              val (tySubst, res) = Simp.uniLitSimp(liftedClUniLits)(sig)
              if (res != liftedClUniLits) {
                val newCl = AnnotatedClause(Clause(res ++ liftedClOtherLits.map(_.substituteOrdered(Subst.id, tySubst))), InferredFrom(Simp, cl), cl.properties)
                val simpNewCl = Control.simp(newCl)(sig)
                result = result + simpNewCl
              }
            } else {
              result = result union liftedUnified
            }
          }
        }
      }
      result
    }

    type ACSpec = Boolean
    final val ACSpec_Associativity: ACSpec = false
    final val ACSpec_Commutativity: ACSpec = true

    final def detectAC(cl: AnnotatedClause): Option[(Signature.Key, Boolean)] = {
      if (Clause.demodulator(cl.cl)) {
        val lit = cl.cl.lits.head
        // Check if lit is an specification for commutativity
        if (lit.equational) {
          import leo.datastructures.Term.{TermApp, Symbol, Bound}
          val left = lit.left
          val right = lit.right
          left match {
            case TermApp(f@Symbol(key), Seq(v1@Bound(_, _), v2@Bound(_, _))) if v1 != v2 => // C case
              right match {
                case TermApp(`f`, Seq(`v2`, `v1`)) => Some((key, ACSpec_Commutativity))
                case _ => None
              }
            case TermApp(f@Symbol(key), Seq(TermApp(Symbol(key2), Seq(v1@Bound(_, _),v2@Bound(_, _))), v3@Bound(_, _)))
              if key == key2  && v1 != v2 && v1 != v3 && v2 != v3 => // A case 1
              right match {
                case TermApp(`f`, Seq(`v1`,TermApp(`f`, Seq(`v2`,`v3`)))) =>
                  Some((key, ACSpec_Associativity))
                case _ => None
              }
            case TermApp(f@Symbol(key), Seq(v1@Bound(_, _), TermApp(Symbol(key2), Seq(v2@Bound(_, _),v3@Bound(_, _)))))
              if key == key2  && v1 != v2 && v1 != v3 && v2 != v3 => // A case 1
              right match {
                case TermApp(`f`, Seq(TermApp(`f`, Seq(`v1`,`v2`)), `v3`)) =>
                  Some((key, ACSpec_Associativity))
                case _ => None
              }
            case _ => None
          }
        } else None
      } else None
    }

    final def acSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      if (Configuration.isSet("acsimp")) {
        val acSymbols = sig.acSymbols
        Out.trace(s"[AC] Simp on ${cl.pretty(sig)}")
        val pre_result = ACSimp.apply(cl.cl,acSymbols)(sig)
        val result = if (pre_result == cl.cl) cl
        else AnnotatedClause(pre_result, InferredFrom(ACSimp, cl), cl.properties)
        Out.finest(s"[AC] Result: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def simp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"[Simp] Processing ${cl.id}")
      val simpresult = Simp(cl.cl)
      val result = if (simpresult != cl.cl)
        AnnotatedClause(simpresult, InferredFrom(Simp, cl), cl.properties)
      else
        cl
      Out.finest(s"[Simp] Result: ${result.pretty(sig)}")
      result
    }
    final def simpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = clSet.map(simp)

    final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"[Simp] Shallow processing ${cl.id}")
      val simpresult = Simp.shallowSimp(cl.cl)
      val result = if (simpresult != cl.cl)
        AnnotatedClause(simpresult, InferredFrom(Simp, cl), cl.properties)
      else
        cl
      Out.trace(s"[Simp] Shallow result: ${result.pretty(sig)}")
      result
    }
    final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = clSet.map(shallowSimp)

    final def rewriteSimp(cw: AnnotatedClause, rules0: Set[AnnotatedClause])(implicit sig: Signature): AnnotatedClause = {
      val plainSimp = simp(cw)
      Out.trace(s"[Rewriting] Processing ${cw.id}")
      Out.finest(s"[Rewriting] Rules existent? ${rules0.nonEmpty}")
      if (rules0.isEmpty) plainSimp
      else {
        // get all rewrite rules as literals
        val rules: Set[Literal] = rules0.map(_.cl.lits.head)
        myAssert(rules.forall(_.oriented))

        // search in all literals of cw for instances of a rule's left side
        val intoConfigurationIt = intoConfigurationIterator(plainSimp.cl)(sig)
        while (intoConfigurationIt.hasNext) {
          val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()
          val rewriteRulesIt = rules.iterator
          while (rewriteRulesIt.hasNext) {
            val rewriteRule = rewriteRulesIt.next()
            val withTerm = rewriteRule.left
            val replaceBy = rewriteRule.right
            leo.Out.finest(s"[Rewriting] check with ${withTerm.pretty(sig)}, into: ${intoTerm.pretty(sig)}: ${leo.modules.calculus.mayMatch(withTerm, intoTerm)}")
            // TODO What to do with multiple rewrites on same (sub)position?
          }
        }
        val rewriteSimp = plainSimp.cl// RewriteSimp(plainSimp, ???)
        if (rewriteSimp != plainSimp.cl) AnnotatedClause(rewriteSimp, InferredFrom(RewriteSimp, cw), cw.properties)
        else plainSimp
      }
    }

    type Subterm = Term
    type IntoConfiguration = (inferenceControl.LiteralIndex, Literal, Side, Position, Subterm)

    /** into-Iterator for rewriting literals. Returns all literal-side-subterm configurations
      * `(i, l_i, s, p, t)` where
      *
      *  - `i` is the literal's index in `cl.lits`
      *  - `l_i` equals `cl.lits(i)`
      *  - `s` is a side, either `true` (left) or `false` (right)
      *  - `p` is a position in `cl.lits(i).s` (s = left/right)
      *  - `t` is the subterm at position `p`
      *
      * The iterator gives all such configurations for which `l_i` is either
      *
      *  (i) non-maximal, or
      *  (ii) maximal, but `s` is not a maximal side, or
      *  (iii) maximal, `s`is a maximal side, but `p = Position.root`.
      */
    final private def intoConfigurationIterator(cl: Clause)(implicit sig: Signature): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {

      import Literal.{leftSide, rightSide, selectSide}

      val maxLits: Seq[Literal] = Literal.maxOf(cl.lits)
      var litIndex = 0
      var lits: Seq[Literal] = cl.lits
      var side: Side = rightSide // minimal side
      var curSubterms: Set[Term] = _
      var curPositions: Set[Position] = _

      def hasNext: Boolean = if (lits.isEmpty) false
      else {
        val hd = lits.head
        if (curSubterms == null) {
          if (side == rightSide && !hd.equational) {
            side = leftSide
          }
          if (side == leftSide && maxLits.contains(hd)) {
            curSubterms = Set(selectSide(hd, side))
            curPositions = Set(Position.root)
          } else {
            curSubterms = selectSide(hd, side).feasibleOccurrences.keySet
            curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
          }
          true
        } else {
          if (curPositions.isEmpty) {
            curSubterms = curSubterms.tail
            if (curSubterms.isEmpty) {
              if (maxLits.contains(hd) && side == rightSide) {
                // hd is maximal and right side is done,
                // select left side at root position
                side = leftSide
                curSubterms = Set(selectSide(hd, side))
                curPositions = Set(Position.root)
                true
              } else {
                if (side == leftSide) {
                  lits = lits.tail
                  litIndex += 1
                  side = rightSide
                } else {
                  side = leftSide
                }
                curSubterms = null
                curPositions = null
                hasNext
              }
            } else {
              curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
              assert(hasNext)
              true
            }
          } else {
            true
          }
        }
      }

      def next(): IntoConfiguration = {
        if (hasNext) {
          val res = (litIndex, lits.head, side, curPositions.head, curSubterms.head)
          curPositions = curPositions.tail
          res
        } else {
          throw new NoSuchElementException
        }
      }
    }
  }

  protected[modules] object DefinedEqualityProcessing {
    import leo.datastructures.ClauseAnnotation._
    import leo.modules.output.ToTPTP

    final def convertDefinedEqualities(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      val replaceLeibniz = !Configuration.isSet("nleq")
      val replaceAndrews = !Configuration.isSet("naeq")
      if (replaceLeibniz || replaceAndrews) {
        var newClauses: Set[AnnotatedClause] = Set()
        val clSetIt = clSet.iterator
        while (clSetIt.hasNext) {
          val cl = clSetIt.next()
          var cur_c = cl
          if (replaceLeibniz) {
            cur_c = convertLeibniz0(cur_c)(sig)
          }
          if (replaceAndrews) {
            cur_c = convertAndrews0(cur_c)(sig)
          }
          if (cur_c.cl != cl.cl) {
            newClauses = newClauses + cur_c
          }
        }
        newClauses
      } else clSet
    }

    // Leibniz Equalities
    final def convertLeibnizEqualities(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      if (Configuration.isSet("nleq")) cl
      else convertLeibniz0(cl)(sig)
    }
    @inline private final def convertLeibniz0(cl: AnnotatedClause)(sig: Signature): AnnotatedClause = {
      val (cA_leibniz, leibTermMap) = ReplaceLeibnizEq.canApply(cl.cl)(sig)
      if (cA_leibniz) {
        Out.trace(s"Replace Leibniz equalities in ${cl.id}")
        val (resCl, subst) = ReplaceLeibnizEq(cl.cl, leibTermMap)(sig)
        val res = AnnotatedClause(resCl, InferredFrom(ReplaceLeibnizEq, Seq((cl, ToTPTP(subst, cl.cl.implicitlyBound)(sig)))), cl.properties | ClauseAnnotation.PropNeedsUnification)
        Out.finest(s"Result: ${res.pretty(sig)}")
        res
      } else
        cl
    }

    // Andrews Equalities
    final def convertAndrewsEqualities(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      if (Configuration.isSet("naeq")) cl
      else convertAndrews0(cl)(sig)
    }
    @inline private final def convertAndrews0(cl: AnnotatedClause)(sig: Signature): AnnotatedClause = {
      val (cA_Andrews, andrewsTermMap) = ReplaceAndrewsEq.canApply(cl.cl)
      if (cA_Andrews) {
        Out.trace(s"Replace Andrews equalities in ${cl.id}")
        val (resCl, subst) = ReplaceAndrewsEq(cl.cl, andrewsTermMap)(sig)
        val res = AnnotatedClause(resCl, InferredFrom(ReplaceAndrewsEq, Seq((cl, ToTPTP(subst, cl.cl.implicitlyBound)(sig)))), cl.properties | ClauseAnnotation.PropNeedsUnification)
        Out.finest(s"Result: ${res.pretty(sig)}")
        res
      } else
        cl
    }
  }


  ////////////////////////////////////////////////////////
  // Utility for inferenceControl
  ///////////////////////////////////////////////////////

  /**
    * Creates an iterator over the clause `cl` which iterates over the maximal sides (or both sides if not orientable)
    * of each literal inside `cl`.
    *
    * @param cl The clause which literals are iterated.
    * @param onlyMax If `onlyMax` is true, only maximal literals are considered.
    * @param onlyPositive If `onlyPositive` is true, only positive literals are considered..
    * @param alsoFlexheads If `alsoFlexHeads` is true, not only positive literals but also literals with a flexible head
    *                      are considered during iteration. `alsoFlexHeads` has no effect if `onlyPositive` is `false`.
    */
  protected final class LiteralSideIterator(cl: Clause, onlyMax: Boolean, onlyPositive: Boolean, alsoFlexheads: Boolean)(implicit sig: Signature) extends Iterator[inferenceControl.WithConfiguration] {
    import Literal.{leftSide, rightSide}

    private val maxLits = Literal.maxOf(cl.lits)
    private var litIndex = 0
    private var lits = cl.lits
    private var side = leftSide

    def hasNext: Boolean = {
      if (lits.isEmpty) false
      else {
        val hd = lits.head
        if ((!onlyPositive || hd.polarity || (alsoFlexheads && hd.flexHead)) &&
          (!onlyMax || maxLits.contains(hd))) true
        else {
          litIndex = litIndex + 1
          lits = lits.tail
          hasNext
        }
      }
    }

    def next(): inferenceControl.WithConfiguration = {
      if (hasNext) {
        assert(!onlyPositive || lits.head.polarity || (alsoFlexheads && lits.head.flexHead))
        assert(!onlyMax || maxLits.contains(lits.head))
        val res = (litIndex, lits.head, side)
        if (lits.head.oriented || side == rightSide) { // Flexheads are always oriented since they are not equational
          litIndex += 1
          lits = lits.tail
          side = leftSide
        } else {
          side = rightSide
        }
        res
      } else {
        throw new NoSuchElementException
      }
    }
  }

}

package redundancyControl {
  import leo.modules.control.Control.LocalFVState
  import leo.modules.control.indexingControl.FVIndexControl

  object RedundancyControl {
    /** Returns true iff cl is redundant wrt to processed. */
    final def redundant(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit state: LocalFVState): Boolean = {
      if (SubsumptionControl.isSubsumed(cl, processed)) true
      // TODO: Do e.g. AC tautology deletion? maybe restructure later.
      else false
    }
  }

  object SubsumptionControl {
    import leo.modules.calculus.Subsumption
    import leo.modules.indexing.{ClauseFeature, FVIndex, FeatureVector}
    import leo.datastructures.FixedLengthTrie

    /** Main function called for deciding if cl is subsumed by (any clause within) `by`.
      * This function simply check for subsumption (see
      * [[leo.modules.calculus.Subsumption]]) or might call indexing pre-filters and then check those results
      * for the subsumption relation. */
    final def isSubsumed(cl: AnnotatedClause, by: Set[AnnotatedClause])(implicit state : Control.LocalFVState): Boolean = {
      // Current implementation checks feature-vector index for a pre-filter.
      // testFowardSubsumptionFVI also applies the "indeed subsumes"-relation check internally.
      val res = testForwardSubsumptionFVI(cl)
      if (res.nonEmpty)
        Out.trace(s"[Subsumption]: [${cl.id}] subsumed by ${res.map(_.id).mkString(",")}")
      res.nonEmpty
    }

    /** Test for subsumption using the feature vector index as a prefilter, then run
      * "trivial" subsumption check using [[leo.modules.calculus.Subsumption]]. */
    final def testForwardSubsumptionFVI(cl: AnnotatedClause)(implicit state : Control.LocalFVState): Set[AnnotatedClause] = {
      val index = state.fVIndex.index
      val clFV = FVIndex.featureVector(state.fVIndex.clauseFeatures, cl)
      testForwardSubsumptionFVI0(index, clFV, 0, cl)
    }
    final private def testForwardSubsumptionFVI0(index: FixedLengthTrie[ClauseFeature, AnnotatedClause],
                                                 clauseFeatures: FeatureVector,
                                                 featureIndex: Int,
                                                 cl: AnnotatedClause): Set[AnnotatedClause] = {
      if (index.isLeaf) {
        testSubsumption(cl, index.valueSet)
      } else {
        var curFeatureValue = 0
        val clFeatureValue = clauseFeatures(featureIndex)
        while (curFeatureValue <= clFeatureValue) {
          val subtrie = index.subTrie(Seq(curFeatureValue))
          if (subtrie.isDefined) {
            val subtrie0 = subtrie.get.asInstanceOf[FixedLengthTrie[ClauseFeature, AnnotatedClause]]
            val result = testForwardSubsumptionFVI0(subtrie0, clauseFeatures, featureIndex+1, cl)
            if (result.nonEmpty)
              return result
          }
          curFeatureValue += 1
        }
        Set()
      }
    }

    /** Test for subsumption using the feature vector index as a prefilter, then run
      * "trivial" subsumption check using [[leo.modules.calculus.Subsumption]]. */
    final def testBackwardSubsumptionFVI(cl: AnnotatedClause)(implicit state : Control.LocalFVState): Set[AnnotatedClause] = {
      val index = state.fVIndex.index
      val clFV = FVIndex.featureVector(state.fVIndex.clauseFeatures, cl)
      testBackwardSubsumptionFVI0(index, clFV, 0, cl)
    }
    final private def testBackwardSubsumptionFVI0(index: FixedLengthTrie[ClauseFeature, AnnotatedClause],
                                                  clauseFeatures: FeatureVector,
                                                  featureIndex: Int,
                                                  cl: AnnotatedClause): Set[AnnotatedClause] = {
      if (index.isLeaf) {
        testBackwardSubsumption(cl, index.valueSet)
      } else {
        var result: Set[AnnotatedClause] = Set()
        var curFeatureValue = clauseFeatures(featureIndex)
        val maxFeatureValue = index.keySet.max
        while (curFeatureValue <= maxFeatureValue) {
          val subtrie = index.subTrie(Seq(curFeatureValue))
          if (subtrie.isDefined) {
            val subtrie0 = subtrie.get.asInstanceOf[FixedLengthTrie[ClauseFeature, AnnotatedClause]]
            val localresult = testBackwardSubsumptionFVI0(subtrie0, clauseFeatures, featureIndex+1, cl)
            result = result union localresult
          }
          curFeatureValue += 1
        }
        result
      }
    }

    /** Check for subsumption of cl by any clause in `withSet` by subsumption rule in [[leo.modules.calculus.Subsumption]]. */
    private final def testSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] =
    withSet.filter(cw => Subsumption.subsumes(cw.cl, cl.cl))
    /** Check for subsumption of any clause in `withSet` by `cl` by subsumption rule in [[leo.modules.calculus.Subsumption]]. */
    private final def testBackwardSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] =
    withSet.filter(cw => Subsumption.subsumes(cl.cl, cw.cl))
  }
}

package indexingControl {

  import leo.modules.control.Control.{LocalState, LocalFVState}

  object IndexingControl {
    /** Initiate all index structures. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.
      * @note method may change in future (maybe more arguments will be needed). */
    final def initIndexes(initClauses: Set[AnnotatedClause])(implicit state: Control.LocalFVState): Unit = {
      FVIndexControl.init(initClauses)(state)
//      FOIndexControl.foIndexInit()
    }
    /** Insert cl to all relevant indexes used. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.*/
    final def insertIndexed(cl: AnnotatedClause)(implicit state: LocalFVState): Unit = {
      FVIndexControl.insert(cl)
//      FOIndexControl.index.insert(cl) // FIXME There seems to be some error in recognizing real TFF clauses, i.e. some are falsely added
      // TODO: more indexes ...
    }
    /** Remove cl from all relevant indexes used. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.*/
    final def removeFromIndex(cl: AnnotatedClause)(implicit state: LocalFVState): Unit = {
      FVIndexControl.remove(cl)
//      FOIndexControl.index.remove(cl)
      // TODO: more indexes ...
    }

    final def resetIndexes(state: State[AnnotatedClause]): Unit = {
      state.fVIndex.reset()
      leo.datastructures.Term.reset()
    }


    private var decendantMap: Map[Long, Set[AnnotatedClause]] = Map.empty
    final def descendants(cls: Set[AnnotatedClause]): Set[AnnotatedClause] = {
      var result: Set[AnnotatedClause] = Set.empty
      val clsIt = cls.iterator
      while (clsIt.hasNext) {
        val cl = clsIt.next()
        result = result union decendantMap(cl.id)
      }
      result
    }

    final def updateDescendants(taken: AnnotatedClause, generated: Set[AnnotatedClause]): Unit = {
      decendantMap = decendantMap + (taken.id -> generated)
      val generatedIt = generated.iterator
      while (generatedIt.hasNext) {
        val cl = generatedIt.next()
        var parents = cl.annotation.parents
        var found = false
        assert(parents.nonEmpty)
        while (!found) {
          if (parents.size == 1) {
            if (parents.head == taken) found = true
            else parents = parents.head.annotation.parents
          } else if (parents.size == 2) {
            val p1 = parents.head; val p2 = parents.tail.head
            assert(p1.id == taken.id || p2.id == taken.id)
            if (p1.id == taken.id) {
              // cl is descendant of p2
              assert(decendantMap.isDefinedAt(p2.id))
              decendantMap = decendantMap + (p2.id -> (decendantMap(p2.id) + cl))
            } else {
              // p2 == taken
              // cl is descendant of p1
              assert(decendantMap.isDefinedAt(p1.id))
              decendantMap = decendantMap + (p1.id -> (decendantMap(p1.id) + cl))
            }
            found = true
          } else found = true
        }
      }
    }
  }

  object FVIndexControl {
    import leo.datastructures.Clause
    import leo.modules.indexing.{CFF, FVIndex}


    final def init(initClauses: Set[AnnotatedClause])(implicit state: LocalFVState): Unit = {
      implicit val sig = state.signature
      assert(!state.fVIndex.initialized)

      val symbs = sig.allUserConstants.toVector
      val featureFunctions: Seq[CFF] = Vector(FVIndex.posLitsFeature(_), FVIndex.negLitsFeature(_)) ++
        symbs.flatMap {symb => Seq(FVIndex.posLitsSymbolCountFeature(symb,_:Clause),
          FVIndex.posLitsSymbolDepthFeature(symb,_:Clause), FVIndex.negLitsSymbolCountFeature(symb,_:Clause), FVIndex.negLitsSymbolDepthFeature(symb,_:Clause))}

      var initFeatures: Seq[Set[Int]] = Vector.empty
      val featureFunctionIt = featureFunctions.iterator
      var i = 0
      while (featureFunctionIt.hasNext) {
        val cff = featureFunctionIt.next()
        val res = initClauses.map {cw => {cff(cw.cl)}}
        initFeatures = res +: initFeatures
        i = i+1
      }
      Out.trace(s"init Features: ${initFeatures.toString()}")
      val sortedFeatures = initFeatures.zipWithIndex.sortBy(_._1.size).take(state.fVIndex.maxFeatures)
      Out.trace(s"sorted Features: ${sortedFeatures.toString()}")
      state.fVIndex.features = sortedFeatures.map {case (feat, idx) => featureFunctions(idx)}
      state.fVIndex.initialized = true
    }

    final def insert(cl: AnnotatedClause)(implicit state : LocalFVState): Unit = {
      assert(state.fVIndex.initialized)
      val featureVector = FVIndex.featureVector(state.fVIndex.features, cl)
      state.fVIndex.index.insert(featureVector, cl)
    }

    final def insert(cls: Set[AnnotatedClause])(implicit state : LocalFVState): Unit = {
      assert(state.fVIndex.initialized)
      val clIt = cls.iterator
      while(clIt.hasNext) {
        val cl = clIt.next()
        insert(cl)
      }
    }

    final def remove(cl: AnnotatedClause)(implicit state : LocalFVState): Unit = {
      assert(state.fVIndex.initialized)
      val featureVector = FVIndex.featureVector(state.fVIndex.features, cl)
      state.fVIndex.index.remove(featureVector, cl)
    }

    final def remove(cls: Set[AnnotatedClause])(implicit state : LocalFVState): Unit = {
      assert(state.fVIndex.initialized)
      val clIt = cls.iterator
      while(clIt.hasNext) {
        val cl = clIt.next()
        remove(cl)
      }
    }
  }

  object FOIndexControl {
    import leo.modules.indexing.FOIndex
    private var foIndex: FOIndex = _

    final def foIndexInit(): Unit  = {
      if (foIndex == null) foIndex = FOIndex()
    }

    final def index: FOIndex = foIndex
  }

  object RelevanceFilterControl {
    import leo.datastructures.tptp.Commons.AnnotatedFormula
    import leo.modules.relevance_filter._

    final def getRelevantAxioms(input: Seq[AnnotatedFormula], conjecture: AnnotatedFormula)(sig: Signature): Seq[AnnotatedFormula] = {
      if (Configuration.NO_AXIOM_SELECTION) input
      else {
        var result: Seq[AnnotatedFormula] = Vector.empty
        var round : Int = 0

        leo.Out.finest(s"Conjecture: ${conjecture.toString}")
        val conjSymbols = PreFilterSet.useFormula(conjecture)
        leo.Out.finest(s"Symbols in conjecture: ${conjSymbols.mkString(",")}")
        val firstPossibleCandidates = PreFilterSet.getCommonFormulas(conjSymbols)
        var taken: Iterable[AnnotatedFormula] = firstPossibleCandidates.filter(f => RelevanceFilter(round)(f))
        round += 1

        while (taken.nonEmpty) {
          // From SeqFilter:
          // Take all formulas (save the newly touched symbols
          val newsymbs : Iterable[String] = taken.flatMap(f => PreFilterSet.useFormula(f))
          taken.foreach(f => result = f +: result)
          // Obtain all formulas, that have a
          val possibleCandidates : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(newsymbs)
          // Take the new formulas
          taken = possibleCandidates.filter(f => RelevanceFilter(round)(f))
          round += 1
        }
        result
      }
    }

    final def relevanceFilterAdd(formula: AnnotatedFormula)(sig: Signature): Unit = {
      PreFilterSet.addNewFormula(formula)
    }
  }
}

package  externalProverControl {
  import leo.modules.output.SuccessSZS

  object ExtProverControl {
    import leo.modules.external._
    import leo.modules.output.SZS_Error
    private final val prefix: String = "[ExtProver]"
    private var openCalls: Map[TptpProver[AnnotatedClause], Set[Future[TptpResult[AnnotatedClause]]]] = Map()
    private var lastCheck: Long = Long.MinValue
    private var lastCall: Long = 0

    final def openCallsExist: Boolean = openCalls.nonEmpty

    final private def helpfulAnswer(result: TptpResult[AnnotatedClause]): Boolean = {
      result.szsStatus match {
        case _:SuccessSZS => true
        case _ => false
      }
    }

    final def checkExternalResults(state: State[AnnotatedClause]): Seq[TptpResult[AnnotatedClause]] = {
      if (state.externalProvers.isEmpty) Seq.empty
      else {
        val curTime = System.currentTimeMillis()
        if (curTime >= lastCheck + Configuration.ATP_CHECK_INTERVAL * 1000) {
          leo.Out.debug(s"[ExtProver]: Checking for finished jobs.")
          var results: Seq[TptpResult[AnnotatedClause]] = Vector.empty
          lastCheck = curTime
          val proversIt = openCalls.keys.iterator
          while (proversIt.hasNext) {
            val prover = proversIt.next()
            var finished: Set[Future[TptpResult[AnnotatedClause]]] = Set.empty
            val openCallsIt = openCalls(prover).iterator
            while (openCallsIt.hasNext) {
              val openCall = openCallsIt.next()
              if (openCall.isCompleted) {
                leo.Out.debug(s"[ExtProver]: Job finished (${prover.name}).")
                finished = finished + openCall
                val result = openCall.value.get
                val resultSZS = result.szsStatus
                leo.Out.debug(s"[ExtProver]: Result ${resultSZS.pretty}")
                if (resultSZS == SZS_Error) leo.Out.warn(result.error.mkString("\n"))
                if (helpfulAnswer(result)) {
                  results = results :+ result
//                  val oldOpenCalls = openCalls(prover)
//                  val newOpenCalls = oldOpenCalls diff finished
//                  if (newOpenCalls.isEmpty) openCalls = openCalls - prover
//                  else openCalls = openCalls.updated(prover, newOpenCalls)
//                  return Some(result)
                }
              }
            }
            val oldOpenCalls = openCalls(prover)
            val newOpenCalls = oldOpenCalls diff finished
            if (newOpenCalls.isEmpty) openCalls = openCalls - prover
            else openCalls = openCalls.updated(prover, newOpenCalls)
          }
          results
        } else Seq.empty
      }
    }
    final def shouldRun(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Boolean = {
      state.noProofLoops >= lastCall + Configuration.ATP_CALL_INTERVAL
    }
    final def submit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = {
      if (state.externalProvers.nonEmpty) {
        if (shouldRun(clauses, state)) {
          leo.Out.debug(s"[ExtProver]: Staring jobs ...")
          lastCall = state.noProofLoops
          state.externalProvers.foreach(prover =>
            if (openCalls.isDefinedAt(prover)) {
              if (openCalls(prover).size < Configuration.ATP_MAX_JOBS) {
                val futureResult = callProver(prover,state.initialProblem union clauses, Configuration.ATP_TIMEOUT(prover.name), state, state.signature)
                if (futureResult != null) openCalls = openCalls + (prover -> (openCalls(prover) + futureResult))
                leo.Out.debug(s"[ExtProver]: ${prover.name} started.")
              }
            } else {
              val futureResult = callProver(prover,state.initialProblem union clauses, Configuration.ATP_TIMEOUT(prover.name), state, state.signature)
              if (futureResult != null) openCalls = openCalls + (prover -> Set(futureResult))
              leo.Out.debug(s"[ExtProver]: ${prover.name} started.")
            }
          )
        }
      }
    }

    final def submitSingleProver(prover : TptpProver[AnnotatedClause],
                                 clauses: Set[AnnotatedClause],
                                 state: State[AnnotatedClause]) : Unit = {
      leo.Out.debug(s"[ExtProver]: Staring job ${prover.name}")
      lastCall = state.noProofLoops
      if (openCalls.isDefinedAt(prover)) {
        if (openCalls(prover).size < Configuration.ATP_MAX_JOBS) {
          val futureResult = callProver(prover,state.initialProblem union clauses, Configuration.ATP_TIMEOUT(prover.name), state, state.signature)
          if (futureResult != null) openCalls = openCalls + (prover -> (openCalls(prover) + futureResult))
          leo.Out.debug(s"[ExtProver]: ${prover.name} started.")
        }
      } else {
        val futureResult = callProver(prover,state.initialProblem union clauses, Configuration.ATP_TIMEOUT(prover.name), state, state.signature)
        if (futureResult != null) openCalls = openCalls + (prover -> Set(futureResult))
        leo.Out.debug(s"[ExtProver]: ${prover.name} started.")
      }
    }
    final def callProver(prover: TptpProver[AnnotatedClause],
                                 problem: Set[AnnotatedClause], timeout : Int,
                                 state: State[AnnotatedClause], sig: Signature): Future[TptpResult[AnnotatedClause]] = {
      import leo.modules.encoding._
      import leo.modules.external.Capabilities._
      // Check what the provers speaks, translate only to first-order if necessary
      val proverCaps = prover.capabilities
      val extraArgs = Seq(Configuration.ATP_ARGS(prover.name))
      if (proverCaps.contains(THF)) {
        prover.call(problem, problem.map(_.cl), sig, THF, timeout, extraArgs)
      } else if (proverCaps.contains(TFF)) {
        Out.finest(s"Translating problem ...")
        val (translatedProblem, auxDefs, translatedSig) =
          if (supportsFeature(proverCaps, TFF)(Polymorphism))
            Encoding(problem.map(_.cl), EP_None, LambdaElimStrategy_SKI,  PolyNative)(sig)
          else
            Encoding(problem.map(_.cl), EP_None, LambdaElimStrategy_SKI,  MonoNative)(sig)
          prover.call(problem, translatedProblem union auxDefs, translatedSig, TFF, timeout, extraArgs)
      } else if (proverCaps.contains(FOF)) {
        Out.warn(s"$prefix Untyped first-order cooperation currently not supported.")
        null
      } else {
        Out.warn(s"$prefix Prover ${prover.name} input syntax not supported.")
        null
      }





//      if (state.isPolymorphic) { // FIXME: Hack, implement with capabilities
//        // monomorphize the problem
//        val monoResult = leo.modules.encoding.Encoding.mono(problem.map(_.cl))(sig)
//        val asAnnotated = monoResult._1.map(cl =>
//          AnnotatedClause(cl, Role_Axiom, NoAnnotation, ClauseAnnotation.PropNoProp))
//        prover.call(asAnnotated, timeout)(monoResult._3)
//      } else prover.call(problem, timeout)(state.signature)
    }

    final def killExternals(): Unit = {
      Out.info(s"Killing external provers ...")
      openCalls.keys.foreach(prover =>
        openCalls(prover).foreach(future =>
          future.kill()
        )
      )
    }
  }
}

package schedulingControl {
  object StrategyControl {

    val MINTIME = 20
    val STRATEGY_TEMPLATES: Seq[RunStrategy] = Seq(
      RunStrategy(
        timeout = -1,
        primSubst = Configuration.DEFAULT_PRIMSUBST,
        sos = Configuration.DEFAULT_SOS,
        unifierCount = Configuration.DEFAULT_UNIFIERCOUNT,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true),

      RunStrategy(
        timeout = -1,
        primSubst = Configuration.DEFAULT_PRIMSUBST,
        sos = true,
        unifierCount = Configuration.DEFAULT_UNIFIERCOUNT,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true),

      RunStrategy(
        timeout = -1,
        primSubst = Configuration.DEFAULT_PRIMSUBST,
        sos = false,
        unifierCount = 3,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true),

      RunStrategy(
        timeout = -1,
        primSubst = Configuration.DEFAULT_PRIMSUBST,
        sos = true,
        unifierCount = 3,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true),

      RunStrategy(
        timeout = -1,
        primSubst = 2,
        sos = false,
        unifierCount = 3,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true),

      RunStrategy(
        timeout = -1,
        primSubst = 2,
        sos = true,
        unifierCount = 3,
        uniDepth = Configuration.DEFAULT_UNIFICATIONDEPTH,
        boolExt = true,
        choice = true)
    )


    final def generateRunStrategies: Iterator[RunStrategy] = {
      val to = Configuration.TIMEOUT
      if (to == 0) {
        // unlimited resources, dont schedule...i guess?
        Iterator(defaultStrategy(0))
      } else {
        // limited resources, divide time for different strategies
        // each strategy should take at least MINTIME seconds
        val nominalStrategyCount = Math.floorDiv(to, MINTIME)
        val remainingTime = Math.floorMod(to, MINTIME)
        val realStrategyCount = Math.min(STRATEGY_TEMPLATES.size, nominalStrategyCount)
        val exceedTime = (nominalStrategyCount-realStrategyCount)*MINTIME+remainingTime
        val extraTimePerStrategy = Math.floorDiv(exceedTime, realStrategyCount)
        val timePerStrategy = MINTIME + extraTimePerStrategy
        val overheadTime = Math.floorMod(exceedTime, realStrategyCount)

        val defStrategy = defaultStrategy(timePerStrategy + overheadTime)
        Iterator(
          defStrategy
            +: STRATEGY_TEMPLATES.filterNot(_ == defStrategy).take(realStrategyCount-1).map(t =>
            RunStrategy(timePerStrategy, t.primSubst, t.sos,
              t.unifierCount, t.uniDepth, t.boolExt, t.choice)):_*
        )
      }
    }

    def defaultStrategy(timeout: Int): RunStrategy = {
      RunStrategy(timeout,
        Configuration.PRIMSUBST_LEVEL,
        Configuration.SOS,
        Configuration.UNIFIER_COUNT,
        Configuration.UNIFICATION_DEPTH,
        Configuration.DEFAULT_BOOLEXT,
        Configuration.DEFAULT_CHOICE)
    }
  }
}
