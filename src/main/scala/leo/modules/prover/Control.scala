package leo.modules.control

import leo.datastructures.{AnnotatedClause, Signature, Term, Type}
import leo.modules.prover.{Interaction, RunStrategy, State}
import leo.modules.{FVState, GeneralState, myAssert}
import leo.{Configuration, Out}

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
  @inline final def boolext(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.BoolExtControl(cl)(state)
  @inline final def primsubst(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.PrimSubstControl.primSubst(cl)(state)
  @inline final def unifyNewClauses(clSet: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.UnificationControl.unifyNewClauses(clSet)(state)
  @deprecated("Usage of this method is deprecated due to completeness considerations, use funcExtNew instead.", "Leo-III 1.2")
  @inline final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.FuncExtControl(cl)(sig)
  @inline final def funcExtNew(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.FuncExtControl.applyNew(cl)(state)

  // simplification inferences / preprocessing
  @inline final def cnf(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.CNFControl.cnf(cl)(state)
  @inline final def cnfSet(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.CNFControl.cnfSet(cls)(state)
  @inline final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.expandDefinitions(cl)(sig)
  @inline final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.miniscope(cl)(sig)
  @inline final def switchPolarity(cl: AnnotatedClause): AnnotatedClause = inferenceControl.SimplificationControl.switchPolarity(cl)
  @inline final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.liftEq(cl)(sig)
  @inline final def extPreprocessUnify(clSet: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.SimplificationControl.extPreprocessUnify(clSet)(state)
  @deprecated("Usage is deprecated. It is unknown what this exactly does.", "Leo-III 1.2")
  @inline final def acSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.acSimp(cl)(sig)
  @inline final def cheapSimp(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = inferenceControl.SimplificationControl.cheapSimp(cl)(state)
  @inline final def cheapSimpSet(cls: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.SimplificationControl.cheapSimpSet(cls)(state)
  @inline final def simp(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = inferenceControl.SimplificationControl.simp(cl)(state)
  @inline final def simpSet(clSet: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.SimplificationControl.simpSet(clSet)(state)
  @deprecated("Usage is deprecated. There is no real benefit of using this kind of simp method. Use cheapSimp instead as it includes rewriting and destructive equality resolution.", "Leo-III 1.2")
  @inline final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.shallowSimp(cl)(sig)
  @deprecated("Usage is deprecated. There is no real benefit of using this kind of simp method. Use cheapSimp instead as it includes rewriting and destructive equality resolution.", "Leo-III 1.2")
  @inline final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.shallowSimpSet(clSet)(sig)
  @inline final def detectUnit(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): Unit = inferenceControl.SimplificationControl.detectUnit(cl)
  @deprecated("Usage is deprecated. There is no real benefit of using this kind of simp method. Use simp instead as it includes simplify-reflect.", "Leo-III 1.2")
  @inline final def rewriteSimp(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = inferenceControl.SimplificationControl.rewriteSimp(cl)(state)
  @inline final def convertDefinedEqualities(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertDefinedEqualities(clSet)(sig)
  @inline final def specialInstances(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.SpecialInstantiationControl.specialInstances(cl)(state)
  @inline final def detectAC(cl: AnnotatedClause)(implicit sig: Signature): Boolean = inferenceControl.SimplificationControl.detectAC(cl)(sig)

  // Choice
  @inline final def instantiateChoice(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.ChoiceControl.instantiateChoice(cl)(state)
  @inline final def detectChoiceClause(cl: AnnotatedClause)(implicit state: LocalState): Boolean = inferenceControl.ChoiceControl.detectChoiceClause(cl)(state)
  @inline final def guessFuncSpec(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = inferenceControl.ChoiceControl.guessFuncSpec(cls)(state)

  // Domain Constraints
  @inline final def detectDomainConstraint(cl: AnnotatedClause)(implicit state: LocalState): Boolean = inferenceControl.DomainConstraintInstanceControl.detectDomainConstraint(cl)
  @inline final def instantiateDomainConstraint(cl : AnnotatedClause)(implicit state : LocalState) : Set[AnnotatedClause] = inferenceControl.DomainConstraintInstanceControl.instanciateDomain(cl)
  @inline final def instantiateDomainConstraint(cl : Set[AnnotatedClause])(implicit state : LocalState) : Set[AnnotatedClause] = inferenceControl.DomainConstraintInstanceControl.instanciateDomain(cl)

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
  @inline final def registerExtProver(provers: Seq[(String, String)])(implicit state: State[AnnotatedClause]): Unit =  externalProverControl.ExtProverControl.registerExtProver(provers)(state)
  @inline final def checkExternalResults(state: State[AnnotatedClause]): Seq[leo.modules.external.TptpResult[AnnotatedClause]] =  externalProverControl.ExtProverControl.checkExternalResults(state)
  @inline final def submit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = externalProverControl.ExtProverControl.submit(clauses, state, force)
  @inline final def despairSubmit(startTime: Long, timeout: Float)(implicit state: State[AnnotatedClause]): Unit = externalProverControl.ExtProverControl.despairSubmit(startTime, timeout)(state)
  @inline final def killExternals(): Unit = externalProverControl.ExtProverControl.killExternals()

  // Limited resource scheduling
  type RunConfiguration = (RunStrategy, Int)
  type RunSchedule = Iterable[RunConfiguration]
  @inline final def defaultStrategy: RunStrategy = schedulingControl.StrategyControl.defaultStrategy
  @inline final def generateRunStrategies(globalTimeout: Int, extraTime: Int = 0): RunSchedule = schedulingControl.StrategyControl.generateRunStrategies(globalTimeout, extraTime)

  // Delegator etc.
  final def addUnprocessed(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): Unit = {
    Interaction.trackClause(cl)
    state.addUnprocessed(cl)
  }
  final def addUnprocessed(cls: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Unit = {
    Interaction.trackClause(cls)
    state.addUnprocessed(cls)
  }
  final def removeProcessed(cls: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Unit = {
    state.removeProcessed(cls)
    state.removeUnits(cls)
    removeFromIndex(cls)
    //      // Remove all direct descendants of clauses in `bachSubsumedClauses` from unprocessed
    //      val descendants = Control.descendants(backSubsumedClauses)
    //      state.incDescendantsDeleted(descendants.size)
    //      state.removeUnprocessed(descendants)
  }
}

/** Package collection control objects for inference rules.
  *
  * @see [[leo.modules.calculus.CalculusRule]] */
package inferenceControl {
  import leo.datastructures.ClauseAnnotation.InferredFrom
  import leo.datastructures.Literal.Side
  import leo.datastructures._
  import leo.modules.HOLSignature
  import leo.modules.calculus._
  import leo.modules.control.Control.LocalState
  import leo.modules.output.{SZS_Theorem, SuccessSZS}

  import scala.annotation.tailrec
  package object inferenceControl {
    type LiteralIndex = Int
    type WithConfiguration = (LiteralIndex, Literal, Side)
  }

  protected[modules] object CNFControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def cnf(cl : AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      if (state.runStrategy.renaming) cnf2(cl, state)
      else cnf1(cl, state.signature)
    }

    private final def cnf1(cl: AnnotatedClause, sig: Signature): Set[AnnotatedClause] = {
      Out.trace(s"Standard CNF of ${cl.pretty(sig)}")
      val cnfresult = FullCNF(leo.modules.calculus.freshVarGen(cl.cl), cl.cl)(sig).toSet
      if (cnfresult.size == 1 && cnfresult.head == cl.cl) {
        // no CNF step at all
        Out.trace(s"CNF result:\n\t${cl.pretty(sig)}")
        Set(cl)
      } else {
        val cnfsimp = cnfresult //.map(Simp.shallowSimp)
        val result = cnfsimp.map {c => AnnotatedClause(c, InferredFrom(FullCNF, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cl.properties))}
        Out.trace(s"CNF result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
        result
      }
    }

    private final def cnf2(cl: AnnotatedClause, s: GeneralState[AnnotatedClause]): Set[AnnotatedClause] = {
      Out.trace(s"Rename CNF of ${cl.pretty(s.signature)}")
      val cnfresult = RenameCNF(leo.modules.calculus.freshVarGen(cl.cl), s.renamingCash, cl.cl)(s.signature).toSet
      if (cnfresult.size == 1 && cnfresult.head == cl.cl) {
        // no CNF step at all
        Out.trace(s"CNF result:\n\t${cl.pretty(s.signature)}")
        Set(cl)
      } else {
        val cnfsimp = cnfresult //.map(Simp.shallowSimp)
        val result = cnfsimp.map {c => AnnotatedClause(c, InferredFrom(RenameCNF, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cl.properties))} // TODO Definitions other way into the CNF.
        Out.trace(s"CNF result:\n\t${result.map(_.pretty(s.signature)).mkString("\n\t")}")
        result
      }
    }

    final def cnfSet(cls: Set[AnnotatedClause])(implicit state: LocalState): Set[AnnotatedClause] = {
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
      assert(!state.runStrategy.sos || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) ||
        leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties))

      val sig = state.signature
      var results: Set[AnnotatedClause] = Set()

      val withClause = withWrapper.cl
      val intoClause = intoWrapper.cl

      val withConfigurationIt = new LiteralSideIterator(withClause, true, true, false)(sig)
      while (withConfigurationIt.hasNext) {
        val (withIndex, withLit, withSide) = withConfigurationIt.next()

        assert(withClause.lits(withIndex) == withLit, s"$withIndex in ${withClause.pretty(sig)}\n lit = ${withLit.pretty(sig)}")
        assert(withLit.polarity)

        val intoConfigurationIt = intoConfigurationIterator(intoClause)(sig)
        while (intoConfigurationIt.hasNext) {
          val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()
          assert(!intoLit.flexflex)

          val result = singleParamod(withWrapper, withIndex, withLit, withSide,
            intoWrapper, intoIndex, intoLit, intoSide, intoPos, intoTerm)(state)
          if (result != null) results = results + result
        }
      }
      results
    }

    final def singleParamod(withWrapper: AnnotatedClause,
                            withIndex: Int,
                            withLit: Literal,
                            withSide: Side,
                            intoWrapper: AnnotatedClause,
                            intoIndex: Int,
                            intoLit: Literal,
                            intoSide: Side,
                            intoPos: Position,
                            intoTerm: Term)(state: LocalState): AnnotatedClause = {
      val sig: Signature = state.signature
      if (intoPos == Position.root &&
        ((intoWrapper.id == withWrapper.id && intoIndex == withIndex) ||
          (!withLit.equational && !intoLit.equational && intoLit.polarity))) {
        /* skip, this generates a redundant clause */
        null
      } else {
        val withClause = withWrapper.cl; val intoClause = intoWrapper.cl
        val (withTerm,otherTerm) = Literal.getSidesOrdered(withLit, withSide)
        assert(withTerm.ty == otherTerm.ty)

        val shouldParamod0 = shouldParamod(withTerm, intoTerm)(state)
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
          // switch to this if there is no problem:
//          val shiftedIntoLit = shiftedIntoClause(intoIndex)
//          val (shiftedIntoTerm0, shiftedOtherSide) = Literal.getSidesOrdered(shiftedIntoLit, intoSide)
//          assert(shiftedIntoTerm0.ty == shiftedOtherSide.ty)
//          assert(shiftedIntoTerm0 == shiftedIntoTerm)

          singleParamod0(withWrapper, withClause, withIndex, withSide, withTerm, otherTerm,
            intoWrapper, shiftedIntoClause, intoIndex, intoSide, intoPos, shiftedIntoTerm)(sig)
        } else null
      }
    }

    private final def singleParamod0(withWrapper: AnnotatedClause,
                                     withClause: Clause,
                                     withIndex: Int,
//                                     withLit: Literal,
                                     withSide: Side,
                                     withTerm: Term,
                                     otherTerm: Term,
                                     intoWrapper: AnnotatedClause,
                                     shiftedIntoClause: Clause,
                                     intoIndex: Int,
//                                     intoLit: Literal,
                                     intoSide: Side,
                                     intoPos: Position,
                                     shiftedIntoTerm: Term)(implicit sig: Signature): AnnotatedClause = {
      import leo.modules.output.ToTPTP

      val result0 = OrderedParamod(withClause, withIndex, withSide,
        shiftedIntoClause, intoIndex, intoSide, intoPos, shiftedIntoTerm)(sig)

      val uniLit = result0.lits.last
      val (uniEqLeft,uniEqRight) = UnificationControl.getUniTaskFromLit(uniLit)
      val newProperties = if (isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) || isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties)) {
        ClauseAnnotation.PropNeedsUnification |  ClauseAnnotation.PropSOS
      } else ClauseAnnotation.PropNeedsUnification

      val tyUnifiedResult = if (uniEqLeft.ty == uniEqRight.ty) {
        // all good, no type unification needed
        Out.finest(s"[Paramod] No type unification needed.")
        AnnotatedClause(result0, InferredFrom(OrderedParamod, Seq(withWrapper, intoWrapper)), newProperties)
      } else {
        // type unify, then proceed to pattern unify check
        Out.finest(s"[Paramod] Type unification needed.")
        val maybeSubst = TypeUnification(uniEqLeft.ty, uniEqRight.ty)
        if (maybeSubst.isDefined) {
          val tySubst = maybeSubst.get
          Out.finest(s"[Paramod] Type unification succeeded: ${tySubst.pretty}")
          val result1 = result0.substituteOrdered(Subst.id, tySubst)(sig)
          // TODO: Include type unification
          AnnotatedClause(result1, InferredFrom(OrderedParamod, Seq(withWrapper, intoWrapper)), newProperties)
        } else {
          Out.finest(s"[Paramod] Type unification failed. Dropping clause.")
          null
        }
      }

      if (tyUnifiedResult != null) {
        val uniLit = tyUnifiedResult.cl.lits.last
        val otherLits = tyUnifiedResult.cl.lits.init
        val (uniEqLeft,uniEqRight) = UnificationControl.getUniTaskFromLit(uniLit)
        assert(uniEqLeft.ty == uniEqRight.ty)

        val unifiedResult = if (isPattern(uniEqLeft) && isPattern(uniEqRight)) {
          Out.finest(s"[Paramod] Unification constraint is pattern. Solving directly...")
          // solve directly
          val vargen = freshVarGen(tyUnifiedResult.cl)
          val result = PatternUni.apply(vargen, Vector((uniEqLeft, uniEqRight)), otherLits)(sig)
          if (result.isEmpty) {
            Out.finest(s"[Paramod] Not unifiable, dropping clause. ")
            val (simpsubst, asd) = Simp.uniLitSimp(uniEqLeft, uniEqRight)
            AnnotatedClause(Clause(otherLits.map(_.substituteOrdered(Subst.id, simpsubst)) ++ asd), InferredFrom(Simp, tyUnifiedResult))
          } else {
            import leo.Configuration.{TERM_ORDERING => ord}
            Out.finest(s"[Paramod] Unifiable! ")
            val (resultClause, (termSubst, typeSubst)) = result.get
            val withTermSubst = withTerm.substitute(termSubst, typeSubst)
            val otherTermSubst = otherTerm.substitute(termSubst, typeSubst)
            val cmpResult = ord.compare(otherTermSubst, withTermSubst)(sig)
            leo.Out.finest(s"Checking Ordering restrictions ...")
            leo.Out.finest(s"withTerm: ${withTerm.pretty(sig)}")
            leo.Out.finest(s"otherTerm: ${otherTerm.pretty(sig)}")
            leo.Out.finest(s"withTerm': ${withTermSubst.pretty(sig)}")
            leo.Out.finest(s"otherTerm': ${otherTermSubst.pretty(sig)}")
            leo.Out.finest(s"compare(otherTerm',withTerm') = ${Orderings.pretty(cmpResult)}")


            if (Configuration.isSet("noOrdCheck1") || cmpResult != CMP_GT) {
              val restrictedTermSubst = termSubst.restrict(i => shiftedIntoClause.implicitlyBound.exists(_._1 == i))
              val intoClauseSubst = shiftedIntoClause.substitute(restrictedTermSubst, typeSubst)
              val intoLitSubst = intoClauseSubst(intoIndex)
              leo.Out.finest(s"intoClause: ${shiftedIntoClause.pretty(sig)}")
              leo.Out.finest(s"maxLits = \n\t${shiftedIntoClause.maxLits(sig).map(_.pretty(sig)).mkString("\n\t")}")
              leo.Out.finest(s"intoClauseSubst: ${intoClauseSubst.pretty(sig)}")
              leo.Out.finest(s"intoLitSubst: ${intoLitSubst.pretty(sig)}")
              leo.Out.finest(s"maxLits = \n\t${intoClauseSubst.maxLits(sig).map(_.pretty(sig)).mkString("\n\t")}")
              myAssert(Clause.wellTyped(intoClauseSubst))
              myAssert(Literal.wellTyped(intoLitSubst))
              if (Configuration.isSet("noOrdCheck2") || !intoLitSubst.polarity || intoClauseSubst.maxLits(sig).contains(intoLitSubst)) { // FIXME: Approx. of selection strategy
                val restrictedTermSubst = termSubst.restrict(i => withWrapper.cl.implicitlyBound.exists(_._1 == i))
                val withClauseSubst = withWrapper.cl.substitute(restrictedTermSubst, typeSubst)
                leo.Out.finest(s"withClauseSubst: ${withClauseSubst.pretty(sig)}")
                val withLitSubst = withClauseSubst(withIndex)
                leo.Out.finest(s"withLitSubst: ${withLitSubst.pretty(sig)}")
                myAssert(Clause.wellTyped(withClauseSubst))
                myAssert(Literal.wellTyped(withLitSubst))
                if (Configuration.isSet("noOrdCheck3") || withClauseSubst.maxLits(sig).contains(withLitSubst)) {
//                  if (tyUnifiedResult.id == 153) System.exit(0)
                  AnnotatedClause(resultClause, InferredFrom(PatternUni, Seq((tyUnifiedResult, ToTPTP(termSubst, tyUnifiedResult.cl.implicitlyBound)(sig)))), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,tyUnifiedResult.properties | ClauseAnnotation.PropUnified))
                } else {
                  leo.Out.finest(s"[Paramod] Dropped due to ordering restrictions (#3).")
                  null
                }
              } else {
                leo.Out.finest(s"[Paramod] Dropped due to ordering restrictions (#2).")
//                if (j == 2) System.exit(0)
//                j += 1
                null
              }
            } else {
              leo.Out.finest(s"[Paramod] Dropped due to ordering restrictions (#1).")
//              System.exit(0)
              null
            }
          }
        } else {
          // postpone
          Out.finest(s"[Paramod] Unification constraint is non-pattern. Postponing.")
          tyUnifiedResult
        }
        if (unifiedResult != null) {
          Out.finest(s"Result: ${unifiedResult.pretty(sig)}")
          myAssert(Clause.wellTyped(unifiedResult.cl), "paramod not well-typed")
          myAssert(uniqueFVTypes(unifiedResult.cl), "not unique free var types")
          unifiedResult
        } else {
          null
        }

      } else null
    }
    var j = 0

    /** We should paramod if either the terms are unifiable or if at least one unification rule step can be executed. */
    private final def shouldParamod(withTerm: Term, intoTerm: Term)(state: LocalState): Boolean = {
      if (mayUnify(withTerm.ty, intoTerm.ty)) {
        if (state.runStrategy.restrictUniAttempts) {
          val withHd = withTerm.headSymbol
          val intoHd = intoTerm.headSymbol
          if (withHd == intoHd && withHd.isConstant) true
          else mayUnify(withTerm, intoTerm)
        } else true
      } else false
    }

    ////////////////////////////////////////////////////////
    // Utility for Paramod control
    ///////////////////////////////////////////////////////

    type Subterm = Term
    type IntoConfiguration = (inferenceControl.LiteralIndex, Literal, Side, Position, Subterm)

    final private def intoConfigurationIterator(cl: Clause)(implicit sig: Signature): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {
      import Literal.{leftSide, rightSide, selectSide}

      private val maxLits = cl.maxLits union cl.negLits //if (cl.negLits.nonEmpty) cl.negLits else cl.maxLits
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
                if (hd.oriented || side == rightSide) {
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
      implicit val sig: Signature = state.signature
      var res: Set[AnnotatedClause] = Set.empty
      val clause = cl.cl
      val maxLitsofClause = clause.maxLits
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
              val test1 = shouldFactor(maxLitMaxSide, otherLitMaxSide)(state)
              val test2 = shouldFactor(maxLitOtherSide, otherLitOtherSide)(state)
              Out.finest(s"Should factor ($test1): ${maxLitMaxSide.pretty(sig)} = ${otherLitMaxSide.pretty(sig)}")
              Out.finest(s"Should factor ($test2): ${maxLitOtherSide.pretty(sig)} = ${otherLitOtherSide.pretty(sig)}")
              if (test1 && test2) {
                val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, otherLitSide)
                val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cl.properties) | ClauseAnnotation.PropNeedsUnification)
                Out.finest(s"result: ${result.pretty(sig)}")
                res = res + result
              }
              // If equation is oriented, we still need to look at the side-switched version
              // of otherLit, since our iterator does not give us this test. It will give us this test
              // if otherLit is not oriented.
              if (otherLit.oriented) {
                val test1 = shouldFactor(maxLitMaxSide, otherLitOtherSide)(state)
                val test2 = shouldFactor(maxLitOtherSide, otherLitMaxSide)(state)
                Out.finest(s"Should factor ($test1): ${maxLitMaxSide.pretty(sig)} = ${otherLitOtherSide.pretty(sig)}")
                Out.finest(s"Should factor ($test2): ${maxLitOtherSide.pretty(sig)} = ${otherLitMaxSide.pretty(sig)}")
                if (test1 && test2) {
                  val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, !otherLitSide)
                  val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cl.properties) | ClauseAnnotation.PropNeedsUnification)
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
                val test = shouldFactor(flexTerm, Not(otherTerm))(state)
                Out.finest(s"Should factor ($test): ${flexTerm.pretty(sig)} = ${Not(otherTerm).pretty(sig)}")
                if (test) {
                  val adjustedClause = Clause(clause.lits.updated(otherLitIndex, Literal(Not(otherTerm), !otherLit.polarity)))
                  val factor = OrderedEqFac(adjustedClause, maxLitIndex, Literal.leftSide, otherLitIndex, Literal.leftSide)
                  val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cl.properties) | ClauseAnnotation.PropNeedsUnification)
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
    private final def shouldFactor(term: Term, otherTerm: Term)(state: LocalState): Boolean = {
      if (mayUnify(term.ty, otherTerm.ty)) {
        if (state.runStrategy.restrictUniAttempts) {
          val withHd = term.headSymbol
          val intoHd = otherTerm.headSymbol
          if (withHd == intoHd && withHd.isConstant) true
          else mayUnify(term, otherTerm)
        } else true
      } else false
    }
  }

  protected[modules] object UnificationControl {
    import leo.datastructures.ClauseAnnotation._
    import leo.modules.output.ToTPTP

    type UniLits = Seq[(Term, Term)]
    type OtherLits = Seq[Literal]
    type UniResult = (Clause, (Unification#TermSubst, Unification#TypeSubst))

    final def getUniTaskFromLit(lit: Literal): (Term, Term) = {
      import leo.modules.HOLSignature.LitFalse
      if (!lit.polarity) (lit.left, lit.right) /*standard case*/
      else {
        assert(!lit.equational)
        (lit.left, LitFalse()) /* in case a False was substituted in paramod */
      }
    }

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
      val sig = state.signature
      val cl = cl0.cl
      assert(cl.lits.nonEmpty)
      val uniLit = cl.lits.last

      val uniEq = getUniTaskFromLit(uniLit)
      val uniResult0 = doUnify0(cl0, freshVarGen, Vector(uniEq), cl.lits.init)(state)
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
            Set(AnnotatedClause(Clause(substPosLits ++ uniLitsSimp), InferredFrom(Simp, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cl.properties)))
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
        val result = doUnifyAllPattern(cl, freshVarGen, uniLits, otherLits)(sig)
        if (result == null) Set.empty
        else {
          leo.Out.finest(s"doUnify0 result: ${result.pretty(sig)}")
          Set(result)
        }
      } else {
        val uniResultIterator = PreUni(freshVarGen, uniLits, otherLits, state.runStrategy.uniDepth)(sig)
        val uniResult = uniResultIterator.take(state.runStrategy.unifierCount).toSet
        val result = uniResult.map(annotate(cl, _, PreUni)(sig))
        leo.Out.finest(s"doUnify0 result:\n${result.map(_.pretty(sig)).mkString("\n")}")
        result
      }
    }

    protected[control] final def doUnifyAllPattern(cl: AnnotatedClause, freshVarGen: FreshVarGen,
                                          uniLits: UniLits, otherLits: OtherLits)(sig: Signature):  AnnotatedClause = {
      val result = PatternUni.apply(freshVarGen, uniLits, otherLits)(sig)
      if (result.isEmpty) null
      else annotate(cl, result.get, PatternUni)(sig)
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
      AnnotatedClause(clause, InferredFrom(rule, Seq((origin, ToTPTP(subst._1, origin.cl.implicitlyBound)(sig)))), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification | ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,origin.properties | ClauseAnnotation.PropUnified))
    }


  }

  protected[modules] object BoolExtControl {
    import leo.datastructures.ClauseAnnotation._

    final def apply(cw: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      val sig = state.signature
      if (state.runStrategy.boolExt) {
        if (!leo.datastructures.isPropSet(PropBoolExt, cw.properties)) {
          val (cA_boolExt, bE, bE_other) = BoolExt.canApply(cw.cl)
          if (cA_boolExt) {
            Out.debug(s"Bool Ext on: ${cw.pretty(sig)}")
            val result = BoolExt.apply(bE, bE_other).map(AnnotatedClause(_, InferredFrom(BoolExt, cw), addProp(ClauseAnnotation.PropBoolExt, deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cw.properties))))
            Out.trace(s"Bool Ext result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
            result
          } else Set()
        } else Set()
      } else Set()
    }
  }

  protected[modules] object FuncExtControl {
    final def apply(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      val (cA_funcExt, fE, fE_other) = FuncExt.canApply(cl.cl)
      if (cA_funcExt) {
        Out.finest(s"Func Ext on: ${cl.pretty(sig)}")
        Out.finest(s"TyFV(${cl.id}): ${cl.cl.typeVars.toString()}")
        val result = AnnotatedClause(Clause(FuncExt(leo.modules.calculus.freshVarGen(cl.cl),fE) ++ fE_other), InferredFrom(FuncExt, cl), deleteProp(ClauseAnnotation.PropBoolExt | ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cl.properties))
        myAssert(Clause.wellTyped(result.cl), "func ext not well-typed")
        Out.finest(s"Func Ext result: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    /**
      * Returns a set of clauses where each clause is step-wise treated with (FuncExt):
      *   - Each positive literal is applied with fresh variables (step-wise, excluding the original input)
      *   - Each negative literal is exhaustively applied with fresh Skolem terms
      * @param cl The clause `cl` to be processed
      */
    final def applyNew(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      if (isPropSet(ClauseAnnotation.PropFuncExt, cl.properties)) Set.empty
      else {
        implicit val sig: Signature = state.signature
        val (cA_funcExt, funcExtLits, otherLits) = FuncExt.canApply(cl.cl)
        if (cA_funcExt) {
          var result: Set[AnnotatedClause] = Set.empty
          Out.trace(s"[FuncExtControl] On ${cl.pretty(sig)}")
          Out.finest(s"[FuncExtControl] FV(${cl.id}): ${cl.cl.implicitlyBound.toString}\ttyFV(${cl.id}): ${cl.cl.typeVars.toString}")
          val vargen = freshVarGen(cl.cl)
          val (posFuncExtLits, negFuncExtLits) = funcExtLits.partition(_.polarity)
          val appliedNegFuncExtLits = negFuncExtLits.map(lit => FuncExt.applyExhaust(lit, vargen)(sig))
          val steps = exhaustiveSteps(posFuncExtLits,vargen)(sig).iterator
          val newProp = addProp(ClauseAnnotation.PropFuncExt, deleteProp(ClauseAnnotation.PropBoolExt | ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified, cl.properties))
          while (steps.hasNext) {
            val posFuncExtStep = steps.next()
            val newClause = Clause(posFuncExtStep ++ appliedNegFuncExtLits ++ otherLits)
            result = result + AnnotatedClause(newClause, InferredFrom(FuncExt, cl), newProp)
          }
          Out.trace(s"[FuncExtControl] Result(s):\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
          myAssert(result.forall(r => Clause.wellTyped(r.cl)), "FuncExt results not well-typed")
          result
        } else
          Set.empty
      }
    }
    private final def exhaustiveSteps(posLits: Seq[Literal], vargen: FreshVarGen)(sig: Signature): Seq[Seq[Literal]] = {
      if (posLits.isEmpty) Seq(Seq.empty)
      else exhaustiveSteps0(posLits, vargen, Seq.empty, Seq.empty)(sig)
    }
    @tailrec private final def exhaustiveSteps0(posLits: Seq[Literal], vargen: FreshVarGen, done: Seq[Literal], acc: Seq[Seq[Literal]])(sig: Signature): Seq[Seq[Literal]] = {
      if (posLits.isEmpty) acc
      else {
        val appliedOneStepPosFuncExtLits = posLits.map(lit => FuncExt.applyNew(lit, vargen)(sig))
        val (_,todoLits,doneLits) = FuncExt.canApply(appliedOneStepPosFuncExtLits)
        exhaustiveSteps0(todoLits, vargen, done ++ doneLits, acc :+ (appliedOneStepPosFuncExtLits ++ done))(sig)
      }
    }
  }

  protected[modules] object PrimSubstControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom
    import leo.modules.HOLSignature.{!===, ===, LitFalse, LitTrue, Not, |||}
    import leo.modules.output.ToTPTP

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
              primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), sig.uninterpretedSymbols.filter(id => sig(id)._ty.funParamTypesWithResultType.last == HOLSignature.o).map(Term.mkAtom)))
//              primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), specialEqBindings(sig.uninterpretedSymbols.map(Term.mkAtom), h.ty.funParamTypes)))
              if (level > 3) {
                primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), eqBindings(h.ty.funParamTypes)))
                if (level > 4) {
                  primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, Set(h), specialEqBindings(cw.cl.implicitlyBound.map(a => Term.mkBound(a._2, a._1)).toSet, h.ty.funParamTypes)))
                }
              }
            }
          }
          val newCl = primsubstResult.map{case (cl,subst) => AnnotatedClause(cl, InferredFrom(PrimSubst, Seq((cw,ToTPTP(subst, cw.cl.implicitlyBound)))), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cw.properties))}
          Out.trace(s"Prim subst result:\n\t${newCl.map(_.pretty(sig)).mkString("\n\t")}")
          return newCl
        }
        Set()
      } else Set()
    }
  }

  protected[modules] object SpecialInstantiationControl {
    import leo.Configuration.{PRE_PRIMSUBST_MAX_DEPTH => MAXDEPTH}
    import leo.modules.calculus.Enumeration._

    final def specialInstances(cl: AnnotatedClause)(implicit state: LocalState): Set[AnnotatedClause] = {
      implicit val sig = state.signature
      val LEVEL = state.runStrategy.specialInstances
      if (LEVEL != NO_REPLACE) {
        leo.Out.trace("[Special Instances] Searching ...")
        val clause = cl.cl
        assert(Clause.unit(clause))
        val lit = clause.lits.head
        assert(!lit.equational)
        val term = lit.left
        val instancesResult = instantiateTerm(term, lit.polarity, 0)(state)
        val result = instancesResult.map (r =>
          if (r == term)
            cl
          else {
            val result = AnnotatedClause(Clause(Literal(r, lit.polarity)), InferredFrom(Enumeration, cl), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cl.properties))
            val simpResult = SimplificationControl.shallowSimp(result)(sig)
            simpResult
          }
        )
        leo.Out.trace(s"[Special Instances] Instances used:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
        result
      } else Set(cl)
    }

    final def instantiateTerm(t: Term, polarity: Boolean, depth: Int)(state: LocalState): Set[Term] = {
      import leo.datastructures.Term._
      import leo.modules.HOLSignature.{&, Exists, Forall, Impl, Not, |||}

      if (depth >= MAXDEPTH)
        Set(t)
      else {
        t match {
          case Not(body) =>
            val erg = instantiateTerm(body, !polarity, depth+1)(state)
            erg.map(e => Not(e))
          case &(l,r) =>
            val ergL = instantiateTerm(l, polarity, depth+1)(state)
            val ergR = instantiateTerm(r, polarity, depth+1)(state)
            var result: Set[Term] = Set()
            val ergLIt = ergL.iterator
            while (ergLIt.hasNext) {
              val eL = ergLIt.next()
              val ergRIt = ergR.iterator
              while (ergRIt.hasNext) {
                val eR = ergRIt.next()
                val and = &(eL, eR)
                result = result + and
              }
            }
            result
          case |||(l,r) =>
            val ergL = instantiateTerm(l, polarity, depth+1)(state)
            val ergR = instantiateTerm(r, polarity, depth+1)(state)
            var result: Set[Term] = Set()
            val ergLIt = ergL.iterator
            while (ergLIt.hasNext) {
              val eL = ergLIt.next()
              val ergRIt = ergR.iterator
              while (ergRIt.hasNext) {
                val eR = ergRIt.next()
                val or = |||(eL, eR)
                result = result + or
              }
            }
            result
          case Impl(l,r) =>
            val ergL = instantiateTerm(l, !polarity, depth+1)(state)
            val ergR = instantiateTerm(r, polarity, depth+1)(state)
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
          case Forall(all@(ty :::> _)) if polarity && shouldReplace(ty, state.runStrategy.specialInstances) =>
            val r = instantiateAbstractions(all, ty)(state)
            val r2 = r.flatMap(rr => instantiateTerm(rr, polarity, depth+1)(state))
            if (Enumeration.exhaustive(ty))
              r2
            else
              r2 + t
          case Exists(all@(ty :::> _)) if !polarity && shouldReplace(ty, state.runStrategy.specialInstances) =>
            val r = instantiateAbstractions(all, ty)(state)
            val r2 = r.flatMap(rr => instantiateTerm(rr, polarity, depth+1)(state))
            if (Enumeration.exhaustive(ty))
              r2
            else
              r2 + t
          case _ => Set(t)
        }
      }
    }

    private final def instantiateAbstractions(term: Term, ty: Type)(state: LocalState): Set[Term] = {
      implicit val sig = state.signature
      val LEVEL = state.runStrategy.specialInstances
      assert(term.ty.isFunType)
      leo.Out.finest(s"[Special Instances]: Apply for ${ty.pretty(sig)}?")
      leo.Out.finest(s"[Special Instances]: REPLACE_O: ${isPropSet(REPLACE_O,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_OO: ${isPropSet(REPLACE_OO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_OOO: ${isPropSet(REPLACE_OOO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_AO: ${isPropSet(REPLACE_AO,LEVEL)}")
      leo.Out.finest(s"[Special Instances]: REPLACE_AAO: ${isPropSet(REPLACE_AAO,LEVEL)}")
      if (shouldReplace(ty, LEVEL)) {
        leo.Out.finest(s"[Special Instances]: Should apply.")
        val instances = Enumeration.specialInstances(ty, LEVEL)(sig)
        if (instances.nonEmpty) {
          leo.Out.trace(s"[Special Instances]: Used (${instances.size}): ${instances.map(_.pretty(sig))}")
          instances.map(i => Term.mkTermApp(term, i).betaNormalize)
        } else Set()
      } else Set()
    }

    private final def shouldReplace(ty: Type, LEVEL: Int): Boolean = {
      import leo.modules.HOLSignature.o
      import leo.modules.calculus.Enumeration._

      val funTyArgs = ty.funParamTypesWithResultType
      if (funTyArgs.last == o) {
        if (funTyArgs.size == 1) isPropSet(REPLACE_O, LEVEL) // Booleans
        else {
          // funTyArgs.size > 1
          if (funTyArgs.size == 2 && funTyArgs.head == o) isPropSet(REPLACE_OO, LEVEL)
          else if (funTyArgs.size == 3 && funTyArgs.head == o && funTyArgs.tail.head == o) isPropSet(REPLACE_OOO, LEVEL)
          else {
            if (isPropSet(REPLACE_AO, LEVEL)) true
            else {
              if (funTyArgs.size == 3) {
                val ty1 = funTyArgs.head; val ty2 = funTyArgs.tail.head
                (ty1 == ty2) && isPropSet(REPLACE_AAO,LEVEL)
              } else false
            }
          }
        }
      } else if (isPropSet(REPLACE_SPECIAL, LEVEL)) {
        if (funTyArgs.size == 2) {
          val in = funTyArgs(0)
          val out = funTyArgs(1)
          if (in.isFunType) {
            val inTyArgs = in.funParamTypesWithResultType
            if (inTyArgs.size ==2) in.codomainType == o && in._funDomainType == out
            else if (inTyArgs.size == 3) {
              val in0 = inTyArgs(0)
              val in1 = inTyArgs(1)
              val outout = inTyArgs(2)
              outout == o && ((in0 == out) || (in1 == out))
            } else false
          } else false
        } else if (funTyArgs.size == 4) {
          funTyArgs(0) == o && funTyArgs(1) == funTyArgs(2) && funTyArgs(2) == funTyArgs(3)
        } else false
      } else false
    }
  }

  protected[modules] object DomainConstraintInstanceControl {
    import leo.modules.calculus.{DomainConstraintInstances => Constraint}

    private final def constraintLiteral(l : Literal)(implicit s : GeneralState[AnnotatedClause]) : Option[Term] = {
      val left = l.left
      val right = l.right
      if(leo.datastructures.isVariableModuloEta(left) && right.freeVars.isEmpty) Some(right)
      else if(leo.datastructures.isVariableModuloEta(right) && left.freeVars.isEmpty) Some(left)
      else None
    }

    final def detectDomainConstraint(cl: AnnotatedClause)(implicit state: GeneralState[AnnotatedClause]): Boolean = {
      implicit val sig: Signature = state.signature
      val findResult = findDomainConstraint(cl)
        if (findResult.isEmpty) false
        else {
          val (domainType, domainObjects) = findResult.get
          Out.info(s"[Domain constraints] Detected constraint on ${domainType.pretty(sig)}")
          if (state.domainConstr.contains(domainType)) {
            Out.debug(s"[Domain constraints] Duplicated constraint on ${domainType.pretty(sig)}")
            if (state.domainConstr(domainType).size > domainObjects.size) {
              state.addDomainConstr(domainType, domainObjects)
            }
          } else {
            state.addDomainConstr(domainType, domainObjects)
          }
          Out.info(s"[Domain constraints] dom(${domainType.pretty(sig)}) ⊆ {${state.domainConstr(domainType).map(_.pretty(sig)).mkString(",")}}")
          true
        }
    }

    final def findDomainConstraint(cl: AnnotatedClause)(implicit s: GeneralState[AnnotatedClause]): Option[(Type, Set[Term])] = {
      if(cl.cl.implicitlyBound.size != 1) return None
      val lits = cl.cl.lits.iterator
      val ty = cl.cl.implicitlyBound.head._2
      var constrs: Set[Term] = Set.empty
      while(lits.hasNext){
        constraintLiteral(lits.next()) match {
          case None => return None
          case Some(t) => constrs += t
        }
      }
      Some((ty,constrs))
    }

    final def instanciateDomain(c : AnnotatedClause)
                               (implicit s : GeneralState[AnnotatedClause]) : Set[AnnotatedClause] = {
      if(s.runStrategy.domConstr == 0) {
        return Set(c)
      }
      val instatiatedClauses = Constraint.apply(c.cl, s.domainConstr, s.runStrategy.domConstr)(s.signature)
      val result = instatiatedClauses.map{ic =>
        if(ic != c.cl) {
          val ac = AnnotatedClause(ic, InferredFrom(Constraint, c), c.properties)
          val simpResult = SimplificationControl.shallowSimp(ac)(s.signature)
          simpResult
        } else {
          c
        }
      }
      // TODO Flag for removing
      result + c
    }

    final def  instanciateDomain(cls : Set[AnnotatedClause])
                                (implicit s : GeneralState[AnnotatedClause]) : Set[AnnotatedClause] = {
      cls.flatMap(instanciateDomain(_))
    }
  }

  protected[modules] object ChoiceControl {
    import leo.datastructures.ClauseAnnotation.FromSystem
    import leo.modules.calculus.{Choice => ChoiceRule}
    /* This is for the proof output: Generate a clause with the axiom of choice
    * for some type as parent to the instantiateChoice rule. */
    private var acMap: Map[Type, AnnotatedClause] = Map()
    final def axiomOfChoice(ty: Type): AnnotatedClause = acMap.getOrElse(ty, newACInstance(ty))

    final def newACInstance(ty: Type): AnnotatedClause = {
      import leo.datastructures.Term.{mkBound, mkTermApp, λ}
      import leo.modules.HOLSignature._
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

    final def detectChoiceClause(cw: AnnotatedClause)(state: GeneralState[AnnotatedClause]): Boolean = {
      if (!state.runStrategy.choice) false
      else {
        leo.Out.trace(s"[Choice] Search for instance in ${cw.id}")
        val maybeChoiceFun = ChoiceRule.detectChoice(cw.cl)
        if (maybeChoiceFun.isDefined) {
          val choiceFun = maybeChoiceFun.get
          state.addChoiceFunction(choiceFun)
          leo.Out.debug(s"[Choice] Detected ${choiceFun.pretty(state.signature)}")
          true
        } else false
      }
    }




    private var choicePreds: Set[Term] = Set.empty

    final def instantiateChoice(cw: AnnotatedClause)(state: GeneralState[AnnotatedClause]): Set[AnnotatedClause] = {
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
            if (!choicePreds.contains(candPredicate)) {
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
              choicePreds += candPredicate
            }
          }
          Out.finest(s"[Choice] Instantiate choice for terms: ${candidates.map(_.pretty(sig)).mkString(",")}")

//          Out.trace(s"[Choice] Collected (${choicePreds.size}):\n\t${choicePreds.map(_.pretty(sig)).mkString("\t\n")}")
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
      if (!state.runStrategy.funcspec) Set.empty
      else cls.flatMap(guessFuncSpec(_)(state))
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
      Out.trace(s"FunSpec result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")

      result
    }
  }

  protected[modules] object SimplificationControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom
    import scala.collection.mutable

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

    final def extPreprocessUnify(cls: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = {
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
            val simpNewCl = Control.simp(newCl)
            result = result + cl + simpNewCl
          }
        } else {
          leo.Out.finest(s"Detecting Boolean extensionality literals, inserted expanded clauses...")
          val boolExtResult = BoolExt.apply(boolExtLits, nonBoolExtLits).map(AnnotatedClause(_, InferredFrom(BoolExt, cl),cl.properties | ClauseAnnotation.PropBoolExt))
          val cnf = CNFControl.cnfSet(boolExtResult)
          val lifted = cnf.map(Control.liftEq)
          val liftedIt = lifted.iterator
          while (liftedIt.hasNext) {
            val liftedCl = Control.simp(liftedIt.next())
            result = result + liftedCl
            val (liftedClUniLits, liftedClOtherLits) = liftedCl.cl.lits.partition(_.uni)
            val liftedUnified = doUnify0(cl, freshVarGen(liftedCl.cl), liftedClUniLits.map(l => (l.left, l.right)), liftedClOtherLits)(state)
            if (liftedUnified.isEmpty) {
              val (tySubst, res) = Simp.uniLitSimp(liftedClUniLits)(sig)
              if (res != liftedClUniLits) {
                val newCl = AnnotatedClause(Clause(res ++ liftedClOtherLits.map(_.substituteOrdered(Subst.id, tySubst))), InferredFrom(Simp, cl), cl.properties)
                val simpNewCl = Control.simp(newCl)
                result = result + simpNewCl
              }
            } else {
              result = result union liftedUnified
            }
          }
        }
      }
      result = Control.cnfSet(result)
      result = result.map(cl => Control.liftEq(Control.simp(cl)))
      leo.Out.finest(s"[ExtPreprocessUnify] Results:\n${result.map(_.pretty(sig)).mkString("\n")}")
      result
    }

    type ACSpec = Boolean
    final val ACSpec_Associativity: ACSpec = false
    final val ACSpec_Commutativity: ACSpec = true

    final def detectAC(cl: AnnotatedClause)(implicit sig: Signature): Boolean = {
      val findResult0 = findAC(cl)
      if (findResult0.nonEmpty) {
        val findResult = findResult0.get
        val key = findResult._1
        val acSpec = findResult._2
        val oldProp = sig(key).flag
        if (acSpec == ACSpec_Associativity) {
          Out.trace(s"[AC] Specification detected: ${cl.id} is an instance of A for ${sig(key).name}")
          sig(key).updateProp(addProp(Signature.PropAssociative, oldProp))
        } else {
          myAssert(acSpec == ACSpec_Commutativity)
          Out.trace(s"[AC] Specification detected: ${cl.id} is an instance of C for ${sig(key).name}")
          sig(key).updateProp(addProp(Signature.PropCommutative, oldProp))
        }
        true
      } else false
    }

    final def findAC(cl: AnnotatedClause): Option[(Signature.Key, Boolean)] = {
      if (Clause.demodulator(cl.cl)) {
        val lit = cl.cl.lits.head
        // Check if lit is an specification for commutativity
        if (lit.equational) {
          import leo.datastructures.Term.{Bound, Symbol, TermApp}
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
              if key == key2  && v1 != v2 && v1 != v3 && v2 != v3 => // A case 2
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


    final def cheapSimp(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = {
      implicit val sig: Signature = state.signature
      Out.trace(s"[Simp] Processing ${cl.pretty(sig)}")
//      if (isPropSet(ClauseAnnotation.PropShallowSimplified, cl.properties) || isPropSet(ClauseAnnotation.PropFullySimplified, cl.properties))
//        cl
//      else {
        val simpResult = Simp(cl.cl)
        val result0 = if (simpResult == cl.cl) cl
        else AnnotatedClause(simpResult, InferredFrom(Simp, cl), addProp(ClauseAnnotation.PropShallowSimplified,cl.properties))
        val result = rewriteClause(result0)(state)
        Out.finest(s"[Simp] Result: ${result.pretty(sig)}")
        result
//      }
    }
    final def cheapSimpSet(clSet: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = clSet.map(cheapSimp)

    final def simp(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = {
      implicit val sig: Signature = state.signature
//      if (isPropSet(ClauseAnnotation.PropFullySimplified, cl.properties)) cl
//      else if (isPropSet(ClauseAnnotation.PropShallowSimplified, cl.properties)) simplifyReflect(cl)(state)
//      else {
        val result0 = cheapSimp(cl)(state)
        simplifyReflect(result0)(state)
//      }
    }
    final def simpSet(clSet: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Set[AnnotatedClause] = clSet.map(simp)

    // This method sets the flag PropFullySimplified, since it is only called within simp.
    final private def simplifyReflect(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = {
      val sig: Signature = state.signature
      val posEqs = state.posNonRewriteUnits
      val negEqs = state.negNonRewriteUnits
      Out.trace(s"[SimplifyReflect] Processing ${cl.id}")
      val usedUnits: mutable.Set[AnnotatedClause] = mutable.Set.empty
      var newLits: Seq[Literal] = Vector.empty
      val lits = cl.cl.lits.iterator
      while (lits.hasNext) {
        val lit = lits.next()
        if (lit.polarity) {
          if (!negSimplifyReflect0(cl.cl, lit, negEqs, usedUnits)) newLits = newLits :+ lit
        } else {
          if (!posSimplifyReflect0(cl.cl, lit, posEqs, usedUnits)) newLits = newLits :+ lit
        }
      }
      val result = if (usedUnits.isEmpty) cl else AnnotatedClause(Clause(newLits), InferredFrom(SimplifyReflect, Seq(cl) ++ usedUnits.toSeq), addProp(ClauseAnnotation.PropFullySimplified, cl.properties))
      Out.finest(s"[SimplifyReflect] Result: ${result.pretty(sig)}")
      result
    }
    final private def posSimplifyReflect0(cl: Clause, lit: Literal, posUnits: Map[Literal, AnnotatedClause], usedUnits: mutable.Set[AnnotatedClause]): Boolean =  {
      assert(!lit.polarity)
      val posUnitsIt = posUnits.keysIterator
      while (posUnitsIt.hasNext) {
        val posUnit = posUnitsIt.next()
        assert(posUnit.polarity)
        if (SimplifyReflect.canApplyPos(cl, lit, posUnit)) {
          usedUnits += posUnits(posUnit)
          return true
        }
      }
      false
    }
    final private def negSimplifyReflect0(cl: Clause, lit: Literal, negUnits: Map[Literal, AnnotatedClause], usedUnits: mutable.Set[AnnotatedClause]): Boolean =  {
      assert(lit.polarity)
      val negUnitsIt = negUnits.keysIterator
      while (negUnitsIt.hasNext) {
        val negUnit = negUnitsIt.next()
        assert(!negUnit.polarity)
        if (SimplifyReflect.canApplyNeg(cl, lit, negUnit)) {
          usedUnits += negUnits(negUnit)
          return true
        }
      }
      false
    }

    final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"[Simp] Shallow processing ${cl.id}")
      if (isPropSet(ClauseAnnotation.PropFullySimplified, cl.properties) || isPropSet(ClauseAnnotation.PropShallowSimplified, cl.properties)) {
        Out.finest(s"[Simp] [${cl.id}] already simplified, skipping.")
        cl
      } else {
        val simpresult = Simp.shallowSimp(cl.cl)
        val result = if (simpresult != cl.cl)
          AnnotatedClause(simpresult, InferredFrom(Simp, cl), addProp(ClauseAnnotation.PropShallowSimplified,cl.properties))
        else cl
        Out.trace(s"[Simp] Shallow result: ${result.pretty(sig)}")
        result
      }
    }
    final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = clSet.map(shallowSimp)

    final def detectUnit(cl: AnnotatedClause)(implicit state: State[AnnotatedClause]): Unit = {
      if (Clause.unit(cl.cl)) {
        if (Clause.rewriteRule(cl.cl)) {
          if (cl.cl.implicitlyBound.isEmpty) {
            state.addGroundRewriteRule(cl)
            Out.trace(s"[SeqLoop] Clause ${cl.id} added as ground rewrite rule.")
          } else {
            state.addNonGroundRewriteRule(cl)
            Out.trace(s"[SeqLoop] Clause ${cl.id} added as non-ground rewrite rule.")
          }
        } else {
          if (cl.cl.lits.head.polarity) {
            state.addPosNonRewriteUnits(cl)
            Out.trace(s"[SeqLoop] Clause ${cl.id} added as positive (non-rewrite) unit.")
          } else {
            state.addNegNonRewriteUnits(cl)
            Out.trace(s"[SeqLoop] Clause ${cl.id} added as negative (non-rewrite) unit.")
          }
        }
      }
    }
    type RewriteTable = Map[Term, (Term, AnnotatedClause)]
    final def rewriteSimp(cw: AnnotatedClause)(implicit state: State[AnnotatedClause]): AnnotatedClause = {
      implicit val sig: Signature = state.signature
      Out.trace(s"[Rewriting] Processing ${cw.id}")
      Out.finest(s"[Rewriting] ${cw.pretty(sig)}")
      val plainSimp = simp(cw)
      Out.finest(s"[Rewriting] plain simp: ${plainSimp.pretty(sig)}")

      val rulesExist = state.groundRewriteRules.nonEmpty || state.nonGroundRewriteRules.nonEmpty
      Out.finest(s"[Rewriting] Rules existent? $rulesExist")
      if (!rulesExist) {
        Out.trace(s"[RewriteSimp] Result: ${plainSimp.pretty(sig)}")
        plainSimp
      } else {
        val groundRewriteTable: RewriteTable = state.groundRewriteRules.map(cl => (cl.cl.lits.head.left, (cl.cl.lits.head.right, cl))).toMap
        val maxImplicitVar = plainSimp.cl.maxImplicitlyBound
        val nonGroundRewriteTable: RewriteTable = state.nonGroundRewriteRules.map(cl => (cl.cl.lits.head.left.lift(maxImplicitVar), (cl.cl.lits.head.right.lift(maxImplicitVar), cl))).toMap
        val vargen = freshVarGen(plainSimp.cl)
        val rewriteRulesUsed: mutable.Set[AnnotatedClause] = mutable.Set.empty
        leo.Out.finest(s"vargen in rewriteSimp: ${vargen.existingVars.toString()}")
        val newLits = plainSimp.cl.lits.map(lit => rewriteLit(vargen, lit, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig))
        val newCl = Clause(newLits)
        val result = if (rewriteRulesUsed.isEmpty) plainSimp else {
          leo.Out.finest(s"Rewriting happend!")
          AnnotatedClause(newCl, InferredFrom(RewriteSimp, Seq(plainSimp) ++ rewriteRulesUsed.toSeq), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cw.properties))
        }
        val result2 = shallowSimp(result)
        Out.debug(s"[RewriteSimp] Result: ${result2.pretty(sig)}")
        result2
      }
    }
    private final def rewriteClause(cl: AnnotatedClause)(state: State[AnnotatedClause]): AnnotatedClause = {
      val sig: Signature = state.signature
      val rulesExist = state.groundRewriteRules.nonEmpty || state.nonGroundRewriteRules.nonEmpty
      Out.finest(s"[Rewriting] Rules existent? $rulesExist")
      if (!rulesExist) cl
      else {
        val groundRewriteTable: RewriteTable = state.groundRewriteRules.map(cl => (cl.cl.lits.head.left, (cl.cl.lits.head.right, cl))).toMap
        val maxImplicitVar = cl.cl.maxImplicitlyBound
        val nonGroundRewriteTable: RewriteTable = state.nonGroundRewriteRules.map(cl => (cl.cl.lits.head.left.lift(maxImplicitVar), (cl.cl.lits.head.right.lift(maxImplicitVar), cl))).toMap
        val vargen = freshVarGen(cl.cl)
        val rewriteRulesUsed: mutable.Set[AnnotatedClause] = mutable.Set.empty
        leo.Out.finest(s"vargen in rewriteSimp: ${vargen.existingVars.toString()}")
        val newLits = cl.cl.lits.map(lit => rewriteLit(vargen, lit, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig))
        val newCl = Clause(newLits)
        val result0 = if (rewriteRulesUsed.isEmpty) cl else {
          leo.Out.finest(s"Rewriting happend!")
          AnnotatedClause(newCl, InferredFrom(RewriteSimp, Seq(cl) ++ rewriteRulesUsed.toSeq), deleteProp(ClauseAnnotation.PropFullySimplified | ClauseAnnotation.PropShallowSimplified,cl.properties))
        }
        val simpResult = Simp.shallowSimp(result0.cl)(sig)
        val result = if (simpResult == result0.cl) result0
        else AnnotatedClause(simpResult, InferredFrom(Simp, Seq(result0)), result0.properties)
        Out.debug(s"[Rewriting] Result: ${result.pretty(sig)}")
        result
      }
    }
    private def rewriteLit(vargen: FreshVarGen, lit: Literal, groundRewriteTable: RewriteTable, nonGroundRewriteTable: RewriteTable, rewriteRulesUsed: mutable.Set[AnnotatedClause])(sig: Signature): Literal = {
      if (lit.equational) Literal.mkOrdered(rewriteTerm(vargen, lit.left, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig), rewriteTerm(vargen, lit.right, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig), lit.polarity)(sig)
      else Literal.apply(rewriteTerm(vargen, lit.left, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig), lit.polarity)
    }
    private def rewriteTerm(vargen: FreshVarGen, term: Term, groundRewriteTable: RewriteTable, nonGroundRewriteTable: RewriteTable, rewriteRulesUsed: mutable.Set[AnnotatedClause])(sig: Signature): Term = {
      import leo.datastructures.Term._
      import leo.datastructures.partitionArgs

      if (groundRewriteTable.contains(term)) {
        val (res, origin) = groundRewriteTable(term)
        leo.Out.finest(s"Yeah! replace ${term.pretty(sig)} by ${res.pretty(sig)}")
        rewriteRulesUsed += origin
        res
      } else {
        val toFind = nonGroundRewriteTable.keysIterator
        while (toFind.hasNext) {
          val template = toFind.next()
          val vargen0 = vargen.copy
          vargen0.addVars(template.fv.toSeq)
          val matchingResult = Matching(vargen0, template, term)
          if (matchingResult.nonEmpty) {
            val (termSubst, typeSubst) = matchingResult.head
            val (replaceBy, origin) = nonGroundRewriteTable(template)
            val result =  replaceBy.substitute(termSubst, typeSubst)
            leo.Out.finest(s"Yeah! replace ${term.pretty(sig)} by ${result.pretty(sig)}")
            leo.Out.finest(s"via lhs ${template.pretty(sig)}")
            leo.Out.finest(s"via rhs ${replaceBy.pretty(sig)}")
            leo.Out.finest(s"via subst ${termSubst.pretty}")
            rewriteRulesUsed += origin
            return result
          }
        }
        // only reachable if not rewritten so far
        term match {
          case Bound(_,_) | Symbol(_) => term
          case hd ∙ args =>
            val rewrittenHd = rewriteTerm(vargen, hd, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig)
            val (tyArgs, termArgs) = partitionArgs(args)

            val res0 = Term.mkTypeApp(rewrittenHd, tyArgs)
            Term.mkTermApp(res0, termArgs.map(t => rewriteTerm(vargen, t, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig)))
          case ty :::> body => Term.mkTermAbs(ty, rewriteTerm(vargen, body, groundRewriteTable, nonGroundRewriteTable, rewriteRulesUsed)(sig))
          case _ => term
        }
      }

    }

//    type Subterm = Term
//    type IntoConfiguration = (inferenceControl.LiteralIndex, Literal, Side, Position, Subterm)

//    /** into-Iterator for rewriting literals. Returns all literal-side-subterm configurations
//      * `(i, l_i, s, p, t)` where
//      *
//      *  - `i` is the literal's index in `cl.lits`
//      *  - `l_i` equals `cl.lits(i)`
//      *  - `s` is a side, either `true` (left) or `false` (right)
//      *  - `p` is a position in `cl.lits(i).s` (s = left/right)
//      *  - `t` is the subterm at position `p`
//      *
//      * The iterator gives all such configurations for which `l_i` is either
//      *
//      *  (i) non-maximal, or
//      *  (ii) maximal, but `s` is not a maximal side, or
//      *  (iii) maximal, `s`is a maximal side, but `p = Position.root`.
//      */
//    final private def intoConfigurationIterator(cl: Clause)(implicit sig: Signature): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {
//
//      import Literal.{leftSide, rightSide, selectSide}
//
//      val maxLits: Seq[Literal] = cl.maxLits
//      var litIndex = 0
//      var lits: Seq[Literal] = cl.lits
//      var side: Side = rightSide // minimal side
//      var curSubterms: Set[Term] = _
//      var curPositions: Set[Position] = _
//
//      def hasNext: Boolean = if (lits.isEmpty) false
//      else {
//        val hd = lits.head
//        if (curSubterms == null) {
//          if (side == rightSide && !hd.equational) {
//            side = leftSide
//          }
//          if (side == leftSide && maxLits.contains(hd)) {
//            curSubterms = Set(selectSide(hd, side))
//            curPositions = Set(Position.root)
//          } else {
//            curSubterms = selectSide(hd, side).feasibleOccurrences.keySet
//            curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
//          }
//          true
//        } else {
//          if (curPositions.isEmpty) {
//            curSubterms = curSubterms.tail
//            if (curSubterms.isEmpty) {
//              if (maxLits.contains(hd) && side == rightSide) {
//                // hd is maximal and right side is done,
//                // select left side at root position
//                side = leftSide
//                curSubterms = Set(selectSide(hd, side))
//                curPositions = Set(Position.root)
//                true
//              } else {
//                if (side == leftSide) {
//                  lits = lits.tail
//                  litIndex += 1
//                  side = rightSide
//                } else {
//                  side = leftSide
//                }
//                curSubterms = null
//                curPositions = null
//                hasNext
//              }
//            } else {
//              curPositions = selectSide(hd, side).feasibleOccurrences(curSubterms.head)
//              assert(hasNext)
//              true
//            }
//          } else {
//            true
//          }
//        }
//      }
//
//      def next(): IntoConfiguration = {
//        if (hasNext) {
//          val res = (litIndex, lits.head, side, curPositions.head, curSubterms.head)
//          curPositions = curPositions.tail
//          res
//        } else {
//          throw new NoSuchElementException
//        }
//      }
//    }
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

    private val maxLits = cl.maxLits
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

  object RedundancyControl {
    /** Returns true iff cl is redundant wrt to processed. */
    final def redundant(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit state: LocalFVState): Boolean = {
      import leo.datastructures.Clause.trivial
      if (trivial(cl.cl)) {
        Out.debug(s"[Redundancy] ${cl.id} is trivial.")
        true
      } else if (processed.exists(_.cl == cl.cl)) {
        Out.debug(s"[Redundancy] Already contained in processed set: ${cl.id}")
        true
      } else if (SubsumptionControl.isSubsumed(cl, processed)) true
      // TODO: Do e.g. AC tautology deletion? maybe restructure later.
      else false
    }
  }

  object SubsumptionControl {
    import leo.datastructures.FixedLengthTrie
    import leo.modules.calculus.Subsumption
    import leo.modules.indexing.{ClauseFeature, FVIndex, FeatureVector}

    /** Main function called for deciding if cl is subsumed by (any clause within) `by`.
      * This function simply check for subsumption (see
      * [[leo.modules.calculus.Subsumption]]) or might call indexing pre-filters and then check those results
      * for the subsumption relation. */
    final def isSubsumed(cl: AnnotatedClause, by: Set[AnnotatedClause])(implicit state : Control.LocalFVState): Boolean = {
      Out.trace(s"[Subsumption] Test [${cl.id}] for subsumption")
      // Current implementation checks feature-vector index for a pre-filter.
      // testFowardSubsumptionFVI also applies the "indeed subsumes"-relation check internally.
      val res = testForwardSubsumptionFVI(cl)
      if (res.nonEmpty)
        Out.debug(s"[Subsumption] [${cl.id}] subsumed by ${res.map(_.id).mkString(",")}")
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
    private final def testSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] = {
      withSet.filter {cw =>
        leo.Out.finest(s"[Subsumption] Test subsumes(${cw.id},${cl.id})")
        Configuration.SUBSUMPTION_METHOD.subsumes(cw.cl, cl.cl)}
    }

    /** Check for subsumption of any clause in `withSet` by `cl` by subsumption rule in [[leo.modules.calculus.Subsumption]]. */
    private final def testBackwardSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] =
    withSet.filter(cw => Subsumption.subsumes(cl.cl, cw.cl))
  }
}

package indexingControl {

  import leo.modules.control.Control.LocalFVState

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
      state.resetCash()
      leo.datastructures.Term.reset()
      leo.datastructures.Type.clear()
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
      state.fVIndex.features = sortedFeatures.map {case (_, idx) => featureFunctions(idx)}
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
        if (input.isEmpty) input
        else {
          val noAx = input.size
          if (noAx < 10) {
            // dont filter here
            input
          } else if (noAx < 20) {
            getRelevantAxioms0(input, conjecture,
              0.54, 2.35)(sig)
          } else if (noAx < 100) {
            getRelevantAxioms0(input, conjecture,
              0.56, 2.35)(sig)
          } else if (noAx < 200) {
            getRelevantAxioms0(input, conjecture,
              0.58, 2.35)(sig)
          } else if (noAx < 500) {
            getRelevantAxioms0(input, conjecture,
              0.6, 2.35)(sig)
          } else if (noAx < 1000) {
            getRelevantAxioms0(input, conjecture,
              0.64, 2.35)(sig)
          } else {
            getRelevantAxioms0(input, conjecture,
              0.66, 2.35)(sig)
          }
        }
      }
    }

    final def getRelevantAxioms0(input: Seq[AnnotatedFormula], conjecture: AnnotatedFormula,
                                 passmark: Double, aging: Double)(sig: Signature): Seq[AnnotatedFormula] = {
      var result: Seq[AnnotatedFormula] = Vector.empty
      var round : Int = 0

      leo.Out.finest(s"Conjecture: ${conjecture.toString}")
      val conjSymbols = PreFilterSet.useFormula(conjecture)
      leo.Out.finest(s"Symbols in conjecture: ${conjSymbols.mkString(",")}")
      val firstPossibleCandidates = PreFilterSet.getCommonFormulas(conjSymbols)
      var taken: Iterable[AnnotatedFormula] = firstPossibleCandidates.filter(f => RelevanceFilter(passmark)(aging)(round)(f))
      round += 1

      while (taken.nonEmpty) {
        // From SeqFilter:
        // Take all formulas (save the newly touched symbols
        val newsymbs : Iterable[String] = taken.flatMap(f => PreFilterSet.useFormula(f))
        taken.foreach(f => result = f +: result)
        // Obtain all formulas, that have a
        val possibleCandidates : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(newsymbs)
        // Take the new formulas
        taken = possibleCandidates.filter(f => RelevanceFilter(passmark)(aging)(round)(f))
        round += 1
      }
      result
    }

    final def relevanceFilterAdd(formula: AnnotatedFormula)(sig: Signature): Unit = {
      PreFilterSet.addNewFormula(formula)
    }
  }
}

package  externalProverControl {
  import leo.datastructures.Clause
  import leo.modules.external.Capabilities.Language
  import leo.modules.output.SuccessSZS
  import leo.modules.prover.State.LastCallStat

  object ExtProverControl {
    import leo.modules.external._
    import leo.modules.output.{SZS_Error, SZS_GaveUp, SZS_Unknown, SZS_Unsatisfiable}

    type S = State[AnnotatedClause]
    private final val prefix: String = "[ExtProver]"

    private var openCalls: Set[S] = Set.empty // keep track of states with open ext. prover calls
    private var callFacade : AsyncTranslation = new SequentialTranslationImpl

    final def registerAsyncTranslation(translation : AsyncTranslation) : Unit = {
      callFacade = translation
    }

    final def registerExtProver(provers: Seq[(String, String)])(implicit state: S): Unit = {
      import leo.modules.external.ExternalProver
      Configuration.ATPS.foreach { case (name, path) =>
        try {
          val p = ExternalProver.createProver(name, path)
          state.addExternalProver(p)
          leo.Out.info(s"$name registered as external prover.")
        } catch {
          case e: NoSuchMethodException => leo.Out.warn(e.getMessage)
        }
      }

      if(Configuration.CONCURRENT_TRANSLATE) {
        val maxTrans = Configuration.ATP_MAX_JOBS
        val asyncTrans = new PrivateThreadPoolTranslationImpl(maxTrans)
        registerAsyncTranslation(asyncTrans)
      }

      state.setLastCallStat(new MixedInfoLastCallStat)
    }

    final def openCallsExistGlobally: Boolean = openCalls.nonEmpty  // TODO check open translations?
    final def openCallsExist(implicit state: S): Boolean = state.openExtCalls.nonEmpty || state.getTranslations > 0

    final def submit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
      callFacade.call(clauses, state, force)
    }

    final def despairSubmit(startTime: Long, timeout: Float)(implicit state: S): Unit = {
      import leo.modules.prover.{endplay, extCallInference}
      if ((state.szsStatus == SZS_GaveUp || state.szsStatus == SZS_Unknown) && System.currentTimeMillis() - startTime <= 1000 * timeout && Configuration.ATPS.nonEmpty) {
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
    }

    final def checkExternalResults(state: State[AnnotatedClause]): Seq[TptpResult[AnnotatedClause]] = {
      if (state.externalProvers.isEmpty) Seq.empty
      else {
        leo.Out.debug(s"[ExtProver]: Checking for finished jobs ...")
        var results: Seq[TptpResult[AnnotatedClause]] = Vector.empty

        val proversIt = synchronized(state.openExtCalls.iterator)
        while (proversIt.hasNext) {
          val (prover, openCalls0) = proversIt.next()
          var finished: Set[Future[TptpResult[AnnotatedClause]]] = Set.empty
          val openCallsIt = openCalls0.iterator
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
              }
            }
          }
          synchronized {
            state.removeOpenExtCalls(prover, finished)

            var curJobs = if (state.openExtCalls.isDefinedAt(prover)) state.openExtCalls(prover).size else 0
            while (curJobs < Configuration.ATP_MAX_JOBS && state.queuedCallExists(prover)) {
              val problem = state.nextQueuedCall(prover)
              submit1(prover, problem, state)
              curJobs = curJobs +1
            }

            if (state.openExtCalls.isEmpty) openCalls = openCalls - state
          }
        }
        results
      }
    }


    final def checkExternalResults(): Map[S, Seq[TptpResult[AnnotatedClause]]] =
      openCalls.map(state => (state, checkExternalResults(state))).toMap


    final def sequentialSubmit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
      if (state.externalProvers.nonEmpty) {
        if (shouldRun(realProblem(clauses)(state), state) || force) {
          leo.Out.debug(s"[ExtProver]: Starting jobs ...")
          state.lastCall.calledNow(realProblem(clauses)(state))(state)
          val openCallState = state.openExtCalls
          state.externalProvers.foreach(prover =>
            if (openCallState.isDefinedAt(prover)) {
              if (openCallState(prover).size < Configuration.ATP_MAX_JOBS) {
                submit1(prover, clauses, state)
              }  else {
                state.enqueueCall(prover, clauses)
              }
            } else {
              submit1(prover, clauses, state)
            }
          )
        }
      }
    }

    final def uncheckedSequentialSubmit(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force : Boolean = false): Unit = {
      if (state.externalProvers.nonEmpty) {
        leo.Out.debug(s"[ExtProver]: Starting jobs ...")
        state.lastCall.calledNow(realProblem(clauses)(state))(state)
        val openCallState = state.openExtCalls
        state.externalProvers.foreach(prover =>
          if (openCallState.isDefinedAt(prover)) {
            if (openCallState(prover).size < Configuration.ATP_MAX_JOBS) {
              submit1(prover, clauses, state)
            }  else {
              state.enqueueCall(prover, clauses)
            }
          } else {
            submit1(prover, clauses, state)
          }
        )
      }
    }


    final def submitSingleProver(prover : TptpProver[AnnotatedClause],
                                 clauses: Set[AnnotatedClause],
                                 state: State[AnnotatedClause]) : Unit = {
      leo.Out.debug(s"[ExtProver]: Starting job ${prover.name}")
      state.lastCall.calledNow(realProblem(clauses)(state))(state)
      submit0(prover, clauses, state)
    }

    private def submit0(prover: TptpProver[AnnotatedClause],
                        clauses: Set[AnnotatedClause], state: S): Unit = {
      val openCallState = state.openExtCalls
      if (openCallState.isDefinedAt(prover)) {
        if (openCallState(prover).size < Configuration.ATP_MAX_JOBS) {
          submit1(prover, clauses, state)
        }  else {
          state.enqueueCall(prover, clauses)
        }
      } else {
        submit1(prover, clauses, state)
      }
    }

    private def submit0All(clauses: Set[AnnotatedClause], state: S): Unit = {
      val openCallState = state.openExtCalls
      state.externalProvers.foreach(prover =>
        if (openCallState.isDefinedAt(prover)) {
          if (openCallState(prover).size < Configuration.ATP_MAX_JOBS) {
            submit1(prover, clauses, state)
          } else {
            state.enqueueCall(prover, clauses)
          }
        } else {
          submit1(prover, clauses, state)
        }
      )
    }

    private def submit1(prover: TptpProver[AnnotatedClause],
                        clauses: Set[AnnotatedClause], state: S): Unit = {
      val problem = realProblem(clauses)(state)
      val futureResult = callProver(prover,problem, Configuration.ATP_TIMEOUT(prover.name), state, state.signature)
      if (futureResult != null) {
        state.addOpenExtCall(prover, futureResult)
        openCalls = openCalls + state
      }
      leo.Out.debug(s"[ExtProver]: ${prover.name} started.")
    }

    @inline private def realProblem(problem: Set[AnnotatedClause])(state: S): Set[AnnotatedClause] = {
      state.initialProblem union problem
    }

    final def callProver(prover: TptpProver[AnnotatedClause],
                                 problem: Set[AnnotatedClause], timeout : Int,
                                 state: State[AnnotatedClause], sig: Signature): Future[TptpResult[AnnotatedClause]] = {
      import leo.modules.encoding._
      import leo.modules.external.Capabilities._
      // Check what the provers speaks, translate only to first-order if necessary
      val proverCaps = prover.capabilities
      val extraArgs0 = Configuration.ATP_ARGS(prover.name)
      val extraArgs = if (extraArgs0 == "") Seq.empty else extraArgs0.split(" ").toSeq
      if (proverCaps.contains(THF)) {
        val preparedProblem = prepareProblem(problem, THF)(sig)
        callProver0(prover, problem, preparedProblem.map(_.cl), sig, THF, timeout, extraArgs)
      } else if (proverCaps.contains(TFF)) {
        Out.finest(s"Translating problem ...")
        val preparedProblem = prepareProblem(problem, TFF)(sig)
        try {
          val (translatedProblem, auxDefs, translatedSig) =
            if (supportsFeature(proverCaps, TFF)(Polymorphism))
              Encoding(preparedProblem.map(_.cl), EP_None, LambdaElimStrategy_SKI,  PolyNative)(sig)
            else
              Encoding(preparedProblem.map(_.cl), EP_None, LambdaElimStrategy_SKI,  MonoNative)(sig)
          callProver0(prover, problem, translatedProblem union auxDefs, translatedSig, TFF, timeout, extraArgs)
        } catch {
          case e: Exception =>
            Out.warn(s"Translation of external proof obligation failed for some reason.")
            Out.debug(e.toString)
            null
        }
      } else if (proverCaps.contains(FOF)) {
        Out.warn(s"$prefix Untyped first-order cooperation currently not supported.")
        null
      } else {
        Out.warn(s"$prefix Prover ${prover.name} input syntax not supported.")
        null
      }
    }

    private def callProver0(prover: TptpProver[AnnotatedClause],
                            referenceProblem: Set[AnnotatedClause], problem: Set[Clause],
                            sig: Signature, language: Capabilities.Language, timeout: Int,
                            extraArgs: Seq[String]): Future[TptpResult[AnnotatedClause]] = {
      try {
        prover.call(referenceProblem, problem, sig, language, timeout, extraArgs)
      } catch {
        case e: Exception => Out.warn(e.toString); null
      }
    }

    /** Prepare a problem that is given as a set of clauses (i.e. clauses from the
      * processed set or so) and rework them into a set of clauses suitable for
      * giving to an external prover. This may include
      * (1) deletion of clauses that seem irrelevant
      * (2) addition of clauses whose information is represented elsewhere inside Leo
      * (3) (satisfiability-preserving) modification of clauses if reasonable.
      *
      * Concretely, this method enriches the problem with axioms
      * about some signature constants (choice ...).
      * if goal language first-order. */
    final def prepareProblem(problem: Set[AnnotatedClause], goalLanguage: Language)(implicit sig: Signature): Set[AnnotatedClause] = {
      import leo.datastructures.ClauseAnnotation
      import ClauseAnnotation.{NoAnnotation, PropNoProp}
      import leo.datastructures.Role_Axiom
      val extraAxioms = leo.modules.external.generateSpecialAxioms(sig)
      extraAxioms.map(AnnotatedClause(_, Role_Axiom, NoAnnotation, PropNoProp)) union problem
    }

    final def killExternals(): Unit = {
      callFacade.killAll()
    }

    final def sequentialKillExternals(): Unit = {
      Out.info(s"Killing All external provers ...")
      openCalls.foreach {state => sequentialKillExternals(state) }
    }

    final def sequentialKillExternals(state : State[AnnotatedClause]) : Unit = {
      state.openExtCalls.foreach { case (_, futures) =>
        futures.foreach(_.kill())
      }
    }

    @inline final def shouldRun(problem: Set[AnnotatedClause], state: State[AnnotatedClause]): Boolean = state.lastCall.shouldCall(problem)(state)

    class MixedInfoLastCallStat extends State.LastCallStat[AnnotatedClause] {
      override def shouldCall(problem: Set[AnnotatedClause])(implicit state: State[AnnotatedClause]): Boolean = {
        if (state.openExtCalls.isEmpty && lastLoopCount < state.noProofLoops && problem != lastProblem) {
          true
        } else {
          if (state.noProofLoops - lastLoopCount >= Configuration.ATP_CALL_INTERVAL && problem != lastProblem) {
            true
          }
          else {
            if (System.currentTimeMillis() - lastTime > Configuration.DEFAULT_ATP_TIMEOUT*1000 && problem != lastProblem) {
              true
            }
            else false
          }
        }
      }

      override def fresh: LastCallStat[AnnotatedClause] = new MixedInfoLastCallStat
    }

    final private def helpfulAnswer(result: TptpResult[AnnotatedClause]): Boolean = {
      result.szsStatus match {
        case _:SuccessSZS => true
        case _ => false
      }
    }
  }
}

package schedulingControl {
  import leo.modules.agent.multisearch.EquiScheduleImpl
  import leo.modules.control.Control.{RunConfiguration, RunSchedule}

  object StrategyControl {
    import leo.modules.prover.RunStrategy._
    val MINTIME = 80
    val STRATEGIES: Seq[RunStrategy] = Seq( s1, s1a ) //, s3bb, s2, s1b, s6, s3b )

    final def strategyList: Seq[RunStrategy] = {
      if (Configuration.isSet("strategies")) {
        val inputString0 = Configuration.valueOf("strategies")
        if (inputString0.isDefined) {
          val inputString = inputString0.get
          val input = inputString.head
          val inputAsList = input.split(",").iterator
          var result: Seq[RunStrategy] = Seq.empty
          while (inputAsList.hasNext) {
            val sName = inputAsList.next()
            val s0 = RunStrategy.byName(sName)
            result = result :+ s0
          }
          result
        } else STRATEGIES
      } else STRATEGIES
    }

    /**
      * Given a time `globalTimeout`, return a [[RunSchedule]]
      * in which for each [[RunStrategy]] `r` it holds that
      * {{{timeout  of r = MINTIME * share + extraTime}}}
      *
      * @see [[leo.modules.control.schedulingControl.StrategyControl.MINTIME]]
      */
    final def generateRunStrategies(globalTimeout: Int, extraTime: Int = 0): RunSchedule = {
      val to = Configuration.TIMEOUT
      if (to == 0) {
        // unlimited resources, dont schedule...i guess?
        Iterable((defaultStrategy,0))
      } else {
        val strategyIt = strategyList.iterator
        var remainingTime = globalTimeout
        var result: Seq[RunConfiguration] = Vector.empty
        var shareSum: Float = 0
        while (strategyIt.hasNext) {
          val strategy = strategyIt.next()
          val proportionalTimeOfStrategy = (strategy.share * MINTIME).toInt + extraTime

          if (proportionalTimeOfStrategy <= remainingTime) {
            result = result :+ (strategy, proportionalTimeOfStrategy)
            remainingTime = remainingTime - proportionalTimeOfStrategy
            shareSum = shareSum + strategy.share
          } else {
	    if (result.isEmpty) {
	       return Iterable((strategy, remainingTime))
	    } else {
              // distribute remaining time
              val remainingTime0 = remainingTime
              result = result.map {case (s,time) =>
              	  val extraTime = (remainingTime0 * (s.share / shareSum)).floor.toInt
              	  (s, time+extraTime)
              }
	    }
          }
        }
        Iterable(result:_*)
      }
    }

    final def defaultStrategy: RunStrategy = {
      // currently: ignore meta-knowledge from state and just return standard strategy
      RunStrategy.defaultStrategy
    }

    final def calculateExtraTime(noAxioms: Int): Int = {
      0
//      if (noAxioms < 200) 0
//      else if (noAxioms < 500) 5
//      else if (noAxioms < 1000) 20
//      else 30
    }
  }

  object ParStrategyControl {
    import leo.modules.agent.multisearch.Schedule
    //TODO  Mintime is set in Schedule!!! Move here
    val STRATEGIES: Seq[RunStrategy] = StrategyControl.STRATEGIES // TODO Own strategies? Reorder?


    final def generateRunStrategies(): Schedule = {
      val to = Configuration.TIMEOUT
      if (to == 0) {
        // unlimited resources, dont schedule...i guess?
        new EquiScheduleImpl(Seq(defaultStrategy))
      } else {
        new EquiScheduleImpl(STRATEGIES)
      }
    }


    final def defaultStrategy: RunStrategy = {
      RunStrategy.defaultStrategy
    }
  }
}
