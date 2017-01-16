package leo.modules.control

import leo.{Configuration, Out}
import leo.datastructures.{AnnotatedClause, Signature}

/**
  * Facade object for various control methods of the seq. proof procedure.
  *
  * @see [[leo.modules.seqpproc.SeqPProc]]
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
object Control {
  // Generating inferences
  @inline final def paramodSet(cl: AnnotatedClause, withSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.ParamodControl.paramodSet(cl,withSet)(sig)
  @inline final def factor(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.FactorizationControl.factor(cl)(sig)
  @inline final def boolext(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.BoolExtControl.boolext(cl)(sig)
  @inline final def primsubst(cl: AnnotatedClause, level: Int)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.PrimSubstControl.primSubst(cl, level)(sig)
  @inline final def unifyNewClauses(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.UnificationControl.unifyNewClauses(clSet)(sig)
  // simplification inferences / preprocessing
  @inline final def cnf(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnf(cl)(sig)
  @inline final def cnfSet(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnfSet(cls)(sig)
  @inline final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.expandDefinitions(cl)(sig)
  @inline final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.miniscope(cl)(sig)
  @inline final def switchPolarity(cl: AnnotatedClause): AnnotatedClause = inferenceControl.SimplificationControl.switchPolarity(cl)
  @inline final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.liftEq(cl)(sig)
  @inline final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.funcext(cl)(sig)
  @inline final def extPreprocessUnify(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.extPreprocessUnify(clSet)(sig)
  @inline final def acSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.acSimp(cl)(sig)
  @inline final def simp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.simp(cl)(sig)
  @inline final def simpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.simpSet(clSet)(sig)
  @inline final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.shallowSimp(cl)(sig)
  @inline final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.shallowSimpSet(clSet)(sig)
  @inline final def rewriteSimp(cl: AnnotatedClause, rewriteRules: Set[AnnotatedClause])(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.rewriteSimp(cl, rewriteRules)(sig)
  @inline final def convertDefinedEqualities(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertDefinedEqualities(clSet)(sig)
  @inline final def specialInstances(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SpecialInstantiationControl.specialInstances(cl)(sig)
//  @inline final def convertLeibnizEqualities(clSet: Set[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertLeibnizEqualities(clSet)
//  @inline final def convertAndrewsEqualities(clSet: Set[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertAndrewsEqualities(clSet)
  // AC detection
  @inline final def detectAC(cl: AnnotatedClause): Option[(Signature#Key, Boolean)] = inferenceControl.SimplificationControl.detectAC(cl)
  // Choice
  import leo.datastructures.{Term, Type}
  @inline final def instantiateChoice(cl: AnnotatedClause, choiceFuns: Map[Type, Set[Term]])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.Choice.instantiateChoice(cl, choiceFuns)(sig)
  @inline final def detectChoiceClause(cl: AnnotatedClause): Option[leo.datastructures.Term] = inferenceControl.Choice.detectChoiceClause(cl)
  // Redundancy
  @inline final def redundant(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit sig: Signature): Boolean = redundancyControl.RedundancyControl.redundant(cl, processed)
  @deprecated("forwardSubsumption test should not be used anymore from this position. Use redundant() or SubsumptionControl object directly.")
  @inline final def forwardSubsumptionTest(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = redundancyControl.SubsumptionControl.testForwardSubsumptionFVI(cl)
  @inline final def backwardSubsumptionTest(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = redundancyControl.SubsumptionControl.testBackwardSubsumptionFVI(cl)
  // Indexing
  @inline final def initIndexes(initClauses: Seq[AnnotatedClause])(implicit sig: Signature): Unit = indexingControl.IndexingControl.initIndexes(initClauses.toSet)(sig)
  @inline final def insertIndexed(cl: AnnotatedClause)(implicit sig: Signature): Unit = indexingControl.IndexingControl.insertIndexed(cl)
  @inline final def insertIndexed(cls: Set[AnnotatedClause])(implicit sig: Signature): Unit = cls.foreach(insertIndexed)
  @inline final def removeFromIndex(cl: AnnotatedClause)(implicit sig: Signature): Unit = indexingControl.IndexingControl.removeFromIndex(cl)
  @inline final def removeFromIndex(cls: Set[AnnotatedClause])(implicit sig: Signature): Unit = cls.foreach(removeFromIndex)
  // TODO: Clean-up all those indexing methods below:
  @deprecated @inline final def fvIndexInit(initClauses: Seq[AnnotatedClause])(implicit sig: Signature): Unit = indexingControl.FVIndexControl.init(initClauses.toSet)(sig)
  @deprecated @inline final def fvIndexInsert(cl: AnnotatedClause): Unit = indexingControl.FVIndexControl.insert(cl)
  @deprecated @inline final def fvIndexInsert(cls: Set[AnnotatedClause]): Unit = indexingControl.FVIndexControl.insert(cls)
  @deprecated @inline final def fvIndexRemove(cl: AnnotatedClause): Unit = indexingControl.FVIndexControl.remove(cl)
  @deprecated @inline final def fvIndexRemove(cls: Set[AnnotatedClause]): Unit = indexingControl.FVIndexControl.remove(cls)
  @deprecated @inline final def foIndexInit(): Unit = indexingControl.FOIndexControl.foIndexInit()
  @deprecated @inline final def foIndex: leo.modules.indexing.FOIndex = indexingControl.FOIndexControl.index
  // Relevance filtering
  @inline final def getRelevantAxioms(input: Seq[leo.datastructures.tptp.Commons.AnnotatedFormula], conjecture: leo.datastructures.tptp.Commons.AnnotatedFormula)(implicit sig: Signature): Seq[leo.datastructures.tptp.Commons.AnnotatedFormula] = indexingControl.RelevanceFilterControl.getRelevantAxioms(input, conjecture)(sig)
  @inline final def relevanceFilterAdd(formula: leo.datastructures.tptp.Commons.AnnotatedFormula)(implicit sig: Signature): Unit = indexingControl.RelevanceFilterControl.relevanceFilterAdd(formula)(sig)
  // External prover call
  @inline final def callExternalLeoII(clauses: Set[AnnotatedClause])(implicit sig: Signature) = externalProverControl.ExternalLEOIIControl.call(clauses)(sig)
}

/** Package collcetion control objects for inference rules.
  *
  * @see [[leo.modules.calculus.CalculusRule]] */
package inferenceControl {
  import leo.datastructures.ClauseAnnotation.{InferredFrom, NoAnnotation}
  import leo.datastructures.Literal.Side
  import leo.datastructures._
  import leo.modules.calculus._

  package object inferenceControl {
    type LiteralIndex = Int
    type WithConfiguration = (LiteralIndex, Literal, Side)
  }


  protected[modules] object CNFControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def cnf(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
      Out.trace(s"CNF of ${cl.id}")
      val cnfresult = FullCNF(leo.modules.calculus.freshVarGen(cl.cl), cl.cl)(sig).toSet
      if (cnfresult.size == 1 && cnfresult.head == cl.cl) {
        // no CNF step at all
        Out.trace(s"CNF result:\n\t${cl.pretty(sig)}")
        Set(cl)
      } else {
        val cnfsimp = cnfresult //.map(Simp.shallowSimp)
        val result = cnfsimp.map {c => AnnotatedClause(c, InferredFrom(FullCNF, Set(cl)), cl.properties)}
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
    final def paramodSet(cl: AnnotatedClause, withset: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      var results: Set[AnnotatedClause] = Set()
      val withsetIt = withset.iterator
      Out.debug(s"Paramod on ${cl.id} (SOS: ${leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, cl.properties)}) and processed set")
      while (withsetIt.hasNext) {
        val other = withsetIt.next()
        if (!Configuration.SOS || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, other.properties) ||
          leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, cl.properties))  {
          Out.trace(s"Paramod on ${cl.id} and ${other.id}")
          results = results ++ allParamods(cl, other)
        }
      }
      if (results.nonEmpty) Out.trace(s"Paramod result: ${results.map(_.id).mkString(",")}")
      results
    }

    final def allParamods(cl: AnnotatedClause, other: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
      // Do paramod with cl into other
      val res = allParamods0(cl, other)(sig)
      if (cl.id != other.id) {
        // do paramod with other into cl
        res ++ allParamods0(other, cl)(sig)
      } else res
    }

    final private def allParamods0(withWrapper: AnnotatedClause, intoWrapper: AnnotatedClause)(sig: Signature): Set[AnnotatedClause] = {
      import leo.datastructures.ClauseAnnotation.InferredFrom
      assert(!Configuration.SOS || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) ||
        leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties))

      var results: Set[AnnotatedClause] = Set()

      val withClause = withWrapper.cl
      val intoClause = intoWrapper.cl

      val withConfigurationIt = new LiteralSideIterator(withClause, true, true, false)(sig)
      while (withConfigurationIt.hasNext) {
        val (withIndex, withLit, withSide) = withConfigurationIt.next()
        val withTerm = if (withSide) withLit.left else withLit.right

        assert(withClause.lits(withIndex) == withLit, s"$withIndex in ${withClause.pretty(sig)}\n lit = ${withLit.pretty(sig)}")
        assert(withLit.polarity)

        val intoConfigurationIt = intoConfigurationIterator(intoClause)(sig)
        while (intoConfigurationIt.hasNext) {
          val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()
          leo.Out.finest(s"check with ${withTerm.pretty(sig)}, into: ${intoTerm.pretty(sig)}: ${leo.modules.calculus.mayUnify(withTerm, intoTerm)}")
          assert(!intoLit.flexflex)
          if (intoPos == Position.root &&
            ((intoWrapper.id == withWrapper.id && intoIndex == withIndex) ||
              (!withLit.equational && !intoLit.equational && intoLit.polarity))) {
            /* skip, this generates a redundant clause */
          } else {
            if (leo.modules.calculus.mayUnify(withTerm, intoTerm)) {
              if (!intoTerm.isVariable) {
                Out.trace(s"May unify: ${withTerm.pretty(sig)} with ${intoTerm.pretty(sig)} (subterm at ${intoPos.pretty})")
                Out.finest(s"with: ${withClause.pretty}")
                Out.finest(s"withside: ${withSide.toString}")
                Out.finest(s"into: ${intoClause.pretty}")
                Out.finest(s"intoside: ${intoSide.toString}")
                val newCl = OrderedParamod(withClause, withIndex, withSide,
                  intoClause, intoIndex, intoSide, intoPos, intoTerm)(sig)

                val newClWrapper = AnnotatedClause(newCl, InferredFrom(OrderedParamod, Set(withWrapper, intoWrapper)), ClauseAnnotation.PropSOS | ClauseAnnotation.PropNeedsUnification)
                Out.finest(s"Result: ${newClWrapper.pretty(sig)}")
                results = results + newClWrapper
              }
            } else {
              if (!withLit.equational && !intoLit.equational && !intoLit.polarity && intoPos == Position.root) {
                // implicitly: intoWrapper.id != withWrapper.id since withLit.polarity == true
                if (withTerm.headSymbol == intoTerm.headSymbol) {
                  // this means we can do at least a one-step simplication (Decomp) during unification
                  Out.trace(s"Simulated Resolution step")
                  import leo.modules.HOLSignature.LitTrue
                  val newCl = OrderedParamod(withClause, withIndex, !withSide,
                    intoClause, intoIndex, !intoSide, intoPos, LitTrue(), true)(sig)

                  val newClWrapper = AnnotatedClause(newCl, InferredFrom(OrderedParamod, Set(withWrapper, intoWrapper)), ClauseAnnotation.PropSOS | ClauseAnnotation.PropNeedsUnification)
                  Out.finest(s"Result: ${newClWrapper.pretty(sig)}")
                  results = results + newClWrapper
                }
              }
            }
          }



//          if (!withLit.equational && !intoLit.equational && !intoLit.polarity && intoPos == Position.root) {
//            // implicitly: intoWrapper.id != withWrapper.id since withLit.polarity == true
//            Out.trace(s"Resolution step")
//          } else if (!intoTerm.isVariable && leo.modules.calculus.mayUnify(withTerm, intoTerm)) {
//            Out.trace(s"May unify: ${withTerm.pretty(sig)} with ${intoTerm.pretty(sig)} (subterm at ${intoPos.pretty})")
//            Out.finest(s"with: ${withClause.pretty}")
//            Out.finest(s"withside: ${withSide.toString}")
//            Out.finest(s"into: ${intoClause.pretty}")
//            Out.finest(s"intoside: ${intoSide.toString}")
//            val newCl = OrderedParamod(withClause, withIndex, withSide,
//              intoClause, intoIndex, intoSide, intoPos, intoTerm)(sig)
//
//            val newClWrapper = AnnotatedClause(newCl, InferredFrom(OrderedParamod, Set(withWrapper, intoWrapper)), ClauseAnnotation.PropSOS | ClauseAnnotation.PropNeedsUnification)
//            Out.finest(s"Result: ${newClWrapper.pretty(sig)}")
//            results = results + newClWrapper
//          }

        }
      }

      results
    }

    ////////////////////////////////////////////////////////
    // Utility for Paramod control
    ///////////////////////////////////////////////////////

    type Subterm = Term
    type IntoConfiguration = (inferenceControl.LiteralIndex, Literal, Side, Position, Subterm)

    final private def intoConfigurationIterator(cl: Clause)(implicit sig: Signature): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {

      import Literal.{leftSide, rightSide, selectSide}

      val maxLits = Literal.maxOf(cl.lits)
      var litIndex = 0
      var lits = cl.lits
      var side = leftSide
      var curSubterms: Set[Term] = _
      var curPositions: Set[Position] = _

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

    final def factor(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
      Out.debug(s"Factor in ${cl.id}")
      var res: Set[AnnotatedClause] = Set()
      val clause = cl.cl
      val maxLitsofClause = Literal.maxOf(clause.lits)
      val maxLitIt = new LiteralSideIterator(clause, true, true, true)

      while (maxLitIt.hasNext) {
        val (maxLitIndex, maxLit, maxLitSide) = maxLitIt.next()
        Out.trace(s"maxLit chosen: ${maxLit.pretty}")
        val otherLitIt = new LiteralSideIterator(clause, false, true, true)

        while (otherLitIt.hasNext) {
          val (otherLitIndex, otherLit, otherLitSide) = otherLitIt.next()
          Out.trace(s"otherLit chosen: ${otherLit.pretty}")
          if (maxLitIndex <= otherLitIndex && maxLitsofClause.contains(otherLit) ) {
            Out.finest(s"skipped maxLit ${maxLit.pretty} with ${otherLit.pretty}")
            /* skipped since already tested */
          } else {
            if (maxLit.polarity == otherLit.polarity) {
              // same polarity, standard
              val (maxLitMaxSide, maxLitOtherSide) = Literal.getSidesOrdered(maxLit, maxLitSide)
              val (otherLitMaxSide, otherLitOtherSide) = Literal.getSidesOrdered(otherLit, otherLitSide)
              val test1 = leo.modules.calculus.mayUnify(maxLitMaxSide, otherLitMaxSide)
              val test2 = leo.modules.calculus.mayUnify(maxLitOtherSide, otherLitOtherSide)
              Out.finest(s"Test unify ($test1): ${maxLitMaxSide.pretty} = ${otherLitMaxSide.pretty}")
              Out.finest(s"Test unify ($test2): ${maxLitOtherSide.pretty} = ${otherLitOtherSide.pretty}")
              if (test1 && test2) {
                val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, otherLitSide)
//                val resultprop = if (leo.datastructures.isPropSet(ClauseAnnotation.PropSOS,cl.properties))
//                  ClauseAnnotation.PropSOS
//                else ClauseAnnotation.PropNoProp

                val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, Set(cl)), cl.properties | ClauseAnnotation.PropNeedsUnification)
                res = res + result
              }
              // If equation is oriented, we still need to look at the side-switched version
              // of otherLit, since our iterator does not give us this test. It will give us this test
              // if otherLit is not oriented.
              if (otherLit.oriented) {
                val (otherLitMaxSide, otherLitOtherSide) = Literal.getSidesOrdered(otherLit, !otherLitSide)
                val test1 = leo.modules.calculus.mayUnify(maxLitMaxSide, otherLitMaxSide)
                val test2 = leo.modules.calculus.mayUnify(maxLitOtherSide, otherLitOtherSide)
                Out.finest(s"Test unify ($test1): ${maxLitMaxSide.pretty} = ${otherLitMaxSide.pretty}")
                Out.finest(s"Test unify ($test2): ${maxLitOtherSide.pretty} = ${otherLitOtherSide.pretty}")
                if (test1 && test2) {
                  val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, !otherLitSide)
                  //                val resultprop = if (leo.datastructures.isPropSet(ClauseAnnotation.PropSOS,cl.properties))
                  //                  ClauseAnnotation.PropSOS
                  //                else ClauseAnnotation.PropNoProp

                  val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, Set(cl)), cl.properties | ClauseAnnotation.PropNeedsUnification)
                  res = res + result
                }
              }
            } else {
              // Exactly one is flexhead and negative
              // attach not to not-flex literal
              if (maxLit.flexHead) {
                // TODO
                // assert(!otherLit.flexHead) NOT TRUE, right?
                // otherlit is not flexhead
                //following only makes sense if otherlit is equality between booleans
                if (otherLit.left.ty == maxLit.left.ty) {
                  //                  val adjustedOtherLit = Literal.mkLit(Not(otherLit.left), otherLit.right, !otherLit.polarity)
                  //
                  //                  val (maxLitMaxSide, maxLitOtherSide) = Literal.getSidesOrdered(maxLit, maxLitSide)
                  //                  val (otherLitMaxSide, otherLitOtherSide) = Literal.getSidesOrdered(adjustedOtherLit, otherLitSide)
                  //                  val test1 = leo.modules.calculus.mayUnify(maxLitMaxSide, otherLitMaxSide)
                  //                  val test2 = leo.modules.calculus.mayUnify(maxLitOtherSide, otherLitOtherSide)
                  //                  Out.finest(s"Test unify ($test1): ${maxLitMaxSide.pretty} = ${otherLitMaxSide.pretty}")
                  //                  Out.finest(s"Test unify ($test2): ${maxLitOtherSide.pretty} = ${otherLitOtherSide.pretty}")
                  //                  if (test1 && test2) {
                  //                    val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, otherLitSide)
                  //                    val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, Set(cl)))
                  //                    res = res + result
                  //                  }
                }
              } else {
                // assert(otherLit.flexHead)
                // assert(!maxLit.flexHead)

                //following only makes sense if maxLit is equality between booleans
                if (maxLit.left.ty == otherLit.left.ty) {
                  //                  val adjustedMaxLit = Literal.mkLit(Not(maxLit.left), maxLit.right, !maxLit.polarity)
                  //
                  //                  val (maxLitMaxSide, maxLitOtherSide) = Literal.getSidesOrdered(adjustedMaxLit, maxLitSide)
                  //                  val (otherLitMaxSide, otherLitOtherSide) = Literal.getSidesOrdered(otherLit, otherLitSide)
                  //                  val test1 = leo.modules.calculus.mayUnify(maxLitMaxSide, otherLitMaxSide)
                  //                  val test2 = leo.modules.calculus.mayUnify(maxLitOtherSide, otherLitOtherSide)
                  //                  Out.finest(s"Test unify ($test1): ${maxLitMaxSide.pretty} = ${otherLitMaxSide.pretty}")
                  //                  Out.finest(s"Test unify ($test2): ${maxLitOtherSide.pretty} = ${otherLitOtherSide.pretty}")
                  //                  if (test1 && test2) {
                  //                    val factor = OrderedEqFac(clause, maxLitIndex, maxLitSide, otherLitIndex, otherLitSide)
                  //                    val result = AnnotatedClause(factor, InferredFrom(OrderedEqFac, Set(cl)))
                  //                    res = res + result
                  //                  }
                }
              }
            }
          }
        }
      }

      Out.trace(s"Factor result:\n\t${res.map(_.pretty(sig)).mkString("\n\t")}")
      res
    }
  }

  protected[modules] object UnificationControl {
    import leo.datastructures.ClauseAnnotation._
    import leo.modules.output.ToTPTP

    type UniLits = Seq[(Term, Term)]
    type OtherLits = Seq[Literal]
    type UniResult = (Clause, (Unification#TermSubst, Unification#TypeSubst))

    // TODO: Flags, check for types in pattern unification
    final def unifyNewClauses(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      var resultSet: Set[AnnotatedClause] = Set()
      val clsIt = cls.iterator

      while(clsIt.hasNext) {
        val cl = clsIt.next()

        if (leo.datastructures.isPropSet(ClauseAnnotation.PropNeedsUnification, cl.properties)) {
          Out.debug(s"Clause ${cl.id} needs unification. Working on it ...")
          Out.debug(s"Clause ${cl.pretty(sig)} needs unification. Working on it ...")
          val vargen = leo.modules.calculus.freshVarGen(cl.cl)

          val results = if (cl.annotation.fromRule.isEmpty) {
            defaultUnify(vargen, cl)(sig)
          } else {
            val fromRule = cl.annotation.fromRule.get
            if (fromRule == OrderedParamod) {
              paramodUnify(vargen, cl)(sig)
            } else if (fromRule == OrderedEqFac) {
              factorUnify(vargen, cl)(sig)
            } else {
              defaultUnify(vargen, cl)(sig)
            }
          }
          Out.trace(s"Uni result:\n\t${results.map(_.pretty(sig)).mkString("\n\t")}")
          resultSet = resultSet union results
        } else resultSet = resultSet + cl
      }
      resultSet
    }

    private final def paramodUnify(freshVarGen: FreshVarGen, cl0: AnnotatedClause)(sig: Signature): Set[AnnotatedClause] = {
      import leo.modules.HOLSignature.LitFalse
      val cl = cl0.cl
      assert(cl.lits.nonEmpty)
      val uniLit = cl.lits.last

      val uniEq = if (!uniLit.polarity) Seq((uniLit.left, uniLit.right)) /*standard case*/
      else {
        assert(!uniLit.equational)
        Seq((uniLit.left, LitFalse.apply())) /* in case a False was substituted in paramod */
      }
      val uniResult0 = doUnify0(cl0, freshVarGen, uniEq, cl.lits.init)(sig)
      // 1 if not unifiable, check if uni constraints can be simplified
      // if it can be simplified, return simplified constraints
      // if it cannot be simplied, drop clause
      // 2 if unifiable, reunify again with all literals (simplified)
      if (uniResult0.isEmpty) {
        if (!uniLit.polarity) {
          val simpResult = Simp.uniLitSimp(uniLit)(sig)
          if (simpResult.size == 1 && simpResult.head == uniLit) Set()
          else {
            val resultClause = Clause(cl.lits.init ++ simpResult)
            val res = AnnotatedClause(resultClause, InferredFrom(Simp, Set(cl0)), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,cl0.properties | ClauseAnnotation.PropUnified))
            Out.finest(s"No unification, but Uni Simp result: ${res.pretty(sig)}")
            Set(res)
          }
        } else Set()
      } else {
        var uniResult: Set[AnnotatedClause] = Set()
        val uniResultIt = uniResult0.iterator
        while (uniResultIt.hasNext) {
          val uniRes = uniResultIt.next()
          uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(sig)
        }
        uniResult
      }
    }

    private final def factorUnify(freshVarGen: FreshVarGen, cl0: AnnotatedClause)(sig: Signature): Set[AnnotatedClause] = {
      import leo.modules.HOLSignature.LitFalse
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
      val uniResult0 = doUnify0(cl0, freshVarGen, Seq(uniEq1, uniEq2), cl.lits.init.init)(sig)
      // 1 if not unifiable, check if uni constraints can be simplified
      // if it can be simplified, return simplified constraints
      // if it cannot be simplied, drop clause
      // 2 if unifiable, reunify again with all literals (simplified)
      if (uniResult0.isEmpty) {
        var wasSimplified = false
        val uniLit1Simp = if (!uniLit1.polarity) {
          val simpResult1 = Simp.uniLitSimp(uniLit1)(sig)
          if (simpResult1.size == 1 && simpResult1.head == uniLit1) Seq(uniLit1)
          else { wasSimplified = true; simpResult1 }
        } else Seq(uniLit1)
        val uniLit2Simp = if (!uniLit2.polarity) {
          val simpResult2 = Simp.uniLitSimp(uniLit2)(sig)
          if (simpResult2.size == 1 && simpResult2.head == uniLit2) Seq(uniLit2)
          else { wasSimplified = true; simpResult2 }
        } else Seq(uniLit2)
        if (wasSimplified) {
          val resultClause = Clause(cl.lits.init.init ++ uniLit1Simp ++ uniLit2Simp)
          val res = AnnotatedClause(resultClause, InferredFrom(Simp, Set(cl0)), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,cl0.properties | ClauseAnnotation.PropUnified))
          Out.finest(s"Uni Simp result: ${res.pretty(sig)}")
          Set(res)
        } else Set()
      } else {
        var uniResult: Set[AnnotatedClause] = Set()
        val uniResultIt = uniResult0.iterator
        while (uniResultIt.hasNext) {
          val uniRes = uniResultIt.next()
          uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(sig)
        }
        uniResult
      }
    }

    private final def defaultUnify(freshVarGen: FreshVarGen, cl: AnnotatedClause)(sig: Signature): Set[AnnotatedClause] = {
      val litIt = cl.cl.lits.iterator
      var uniLits: UniLits = Seq()
      var otherLits:OtherLits = Seq()
      while(litIt.hasNext) {
        val lit = litIt.next()
        if (lit.equational && !lit.polarity) {
          uniLits = (lit.left,lit.right) +: uniLits
        } else {
          otherLits = lit +: otherLits
        }
      }
      if (uniLits.nonEmpty) {
        val uniResult = doUnify0(cl, freshVarGen, uniLits, otherLits)(sig)
        // all negative literals are taken as unification constraints
        // if no unifier is found, the original clause is unisimp'd and returned
        // else the unified clause is unisimp*d and returned
        if (uniResult.isEmpty) {
          val uniLits = cl.cl.negLits
          val uniLitsSimp = Simp.uniLitSimp(uniLits)(sig)
          if (uniLits == uniLitsSimp) Set(cl)
          else {
            Set(AnnotatedClause(Clause(cl.cl.posLits ++ uniLitsSimp), InferredFrom(Simp, Set(cl)), cl.properties))
          }
        } else {
          val resultClausesIt = uniResult.iterator
          var resultClausesSimp: Set[AnnotatedClause] = Set()
          while (resultClausesIt.hasNext) {
            val resultClause = resultClausesIt.next()
            val uniLits = resultClause.cl.negLits
            val uniLitsSimp = Simp.uniLitSimp(uniLits)(sig)
            if (uniLits == uniLitsSimp)  resultClausesSimp = resultClausesSimp +  resultClause
            else {
              resultClausesSimp = resultClausesSimp + AnnotatedClause(Clause(resultClause.cl.posLits ++ uniLitsSimp), InferredFrom(Simp, Set(resultClause)), resultClause.properties)
            }
          }
          resultClausesSimp
        }
      } else Set(cl)
    }


    protected[control] final def doUnify0(cl: AnnotatedClause, freshVarGen: FreshVarGen,
                               uniLits: UniLits, otherLits: OtherLits)(sig: Signature):  Set[AnnotatedClause] = {
      if (isAllPattern(uniLits)) {
        val result = PatternUni.apply(freshVarGen, uniLits, otherLits)(sig)
        if (result.isEmpty) Set()
        else Set(annotate(cl, result.get, PatternUni)(sig))
      } else {
        val uniResultIterator = PreUni(freshVarGen, uniLits, otherLits)(sig)
        val uniResult = uniResultIterator.take(Configuration.UNIFIER_COUNT).toSet
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
      AnnotatedClause(clause, InferredFrom(rule, Set((origin, ToTPTP(subst._1, origin.cl.implicitlyBound)(sig)))), leo.datastructures.deleteProp(ClauseAnnotation.PropNeedsUnification,origin.properties | ClauseAnnotation.PropUnified))
    }


  }


  protected[modules] object BoolExtControl {
    import leo.datastructures.ClauseAnnotation._

    final def boolext(cw: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
      if (!Configuration.isSet("nbe")) {
        if (!leo.datastructures.isPropSet(PropBoolExt, cw.properties)) {
          val (cA_boolExt, bE, bE_other) = BoolExt.canApply(cw.cl)
          if (cA_boolExt) {
            Out.debug(s"Bool Ext on: ${cw.pretty(sig)}")
            val result = BoolExt.apply(bE, bE_other).map(AnnotatedClause(_, InferredFrom(BoolExt, Set(cw)),cw.properties | ClauseAnnotation.PropBoolExt))
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

    val standardbindings: Set[Term] = Set(Not, LitFalse, LitTrue, |||)//, Term.mkTypeApp(Forall, Signature.get.i))
    def eqBindings(tys: Seq[Type]): Set[Term] = {
      if (tys.size == 2) {
        val (ty1, ty2) = (tys.head, tys.tail.head)
        if (ty1 == ty2) {
          Set(
            Term.λ(ty1, ty1)(Term.mkTermApp(Term.mkTypeApp(===, ty1), Seq(Term.mkBound(ty1, 2),Term.mkBound(ty1, 1)))),
            Term.λ(ty1, ty1)(Term.mkTermApp(Term.mkTypeApp(!===, ty1), Seq(Term.mkBound(ty1, 2),Term.mkBound(ty1, 1))))
          )
        } else Set()
      } else Set()
    }
    def specialEqBindings(terms: Set[Term], typs: Seq[Type]): Set[Term] = {
      if (typs.size == 1) {
        val typ = typs.head
        val compatibleTerms = terms.filter(_.ty == typ)
        compatibleTerms.map(t => Term.λ(typ)(Term.mkTermApp(Term.mkTypeApp(===, typ), Seq(t.substitute(Subst.shift(1)), Term.mkBound(typ, 1)))))
      } else Set()
    }

    final def primSubst(cw: AnnotatedClause, level: Int)(implicit sig: Signature): Set[AnnotatedClause] = {
      if (level > 0) {
        val (cA_ps, ps_vars) = PrimSubst.canApply(cw.cl)
        if (cA_ps) {
          Out.debug(s"Prim subst on: ${cw.id}")
          var primsubstResult = PrimSubst(cw.cl, ps_vars, standardbindings)
          if (level > 1) {
            primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, ps_vars, eqBindings(h.ty.funParamTypes)))
            if (level > 2) {
              primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, ps_vars, specialEqBindings(cw.cl.implicitlyBound.map(a => Term.mkBound(a._2, a._1)).toSet, h.ty.funParamTypes)))
              if (level > 3) {
                primsubstResult = primsubstResult union ps_vars.flatMap(h => PrimSubst(cw.cl, ps_vars, specialEqBindings(sig.uninterpretedSymbols.map(Term.mkAtom), h.ty.funParamTypes)))
              }
            }
          }
          val newCl = primsubstResult.map{case (cl,subst) => AnnotatedClause(cl, InferredFrom(PrimSubst, Set((cw,ToTPTP(subst, cw.cl.implicitlyBound)))), cw.properties)}
          Out.trace(s"Prim subst result:\n\t${newCl.map(_.pretty(sig)).mkString("\n\t")}")
          return newCl
        }
        Set()
      } else Set()
    }
  }

  protected[modules] object SpecialInstantiationControl {
    final def specialInstances(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = {
//      if (Configuration.PRE_PRIMSUBST_LEVEL > 0) {
//
//      }
// TODO: shallow simp at end.
      Set(cl)
    }

  }

  protected[modules] object Choice {
    import leo.modules.calculus.{Choice => ChoiceRule}
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
      val res = AnnotatedClause(Clause(lit), Role_Axiom, NoAnnotation, ClauseAnnotation.PropNoProp)
      acMap = acMap + ((ty, res))
      res
    }


    final def detectChoiceClause(cw: AnnotatedClause): Option[Term] = {
      ChoiceRule.detectChoice(cw.cl)
    }

    final def instantiateChoice(cw: AnnotatedClause, choiceFuns: Map[Type, Set[Term]])(sig: Signature): Set[AnnotatedClause] = {
      if (Configuration.NO_CHOICE) Set()
      else {
        val cl = cw.cl
        Out.trace(s"[Choice] Searching for possible choice terms...")
        val candidates = ChoiceRule.canApply(cl, choiceFuns)(sig)
        if (candidates.nonEmpty) {
          Out.trace(s"[Choice] Found possible choice term.")
          var results: Set[AnnotatedClause] = Set()
          val candidateIt = candidates.iterator
          while(candidateIt.hasNext) {
            val candPredicate = candidateIt.next()
            // type is (alpha -> o), alpha is choice type
            val choiceType: Type = candPredicate.ty._funDomainType

            if (choiceFuns.contains(choiceType)) {
              // Instantiate will all choice functions
              val choiceFunsForChoiceType = choiceFuns(choiceType)
              val choiceFunIt = choiceFunsForChoiceType.iterator
              while (choiceFunIt.hasNext) {
                val choiceFun = choiceFunIt.next()
                val result0 = ChoiceRule(candPredicate, choiceFun)
                val result = AnnotatedClause(result0, InferredFrom(ChoiceRule, Set(axiomOfChoice(choiceType))))
                results = results + result
              }
            } else {
              // No choice function registered, introduce one now
              val choiceFun = registerNewChoiceFunction(choiceType)(sig)
              val result0 = ChoiceRule(candPredicate, choiceFun)
              val result = AnnotatedClause(result0, InferredFrom(ChoiceRule, Set(axiomOfChoice(choiceType))))
              results = results + result
            }
          }
          Out.trace(s"[Choice] Instantiate choice for terms: ${candidates.map(_.pretty(sig)).mkString(",")}")
          Out.trace(s"[Choice] Results: ${results.map(_.pretty(sig)).mkString(",")}")
          results
        } else Set()
      }
    }

    final def registerNewChoiceFunction(ty: Type)(sig: Signature): Term = {
      import leo.modules.HOLSignature.o
      val newSymb = sig.freshSkolemConst((ty ->: o) ->: ty, Signature.PropChoice)
      Term.mkAtom(newSymb)(sig)
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
        val result = AnnotatedClause(Clause(newLits), InferredFrom(PolaritySwitch, Set(cl)), cl.properties)
        Out.trace(s"Switch polarity: ${result.pretty}")
        result
      } else
        cl

    }

    /** Pre: Is only called on initial clauses, i.e. clauses are not equaltional and unit. */
    final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      import leo.modules.calculus.Miniscope

      assert(Clause.unit(cl.cl))
      assert(!cl.cl.lits.head.equational)

      val lit = cl.cl.lits.head
      val term = lit.left
      val resultterm = Miniscope.apply(term, lit.polarity)
      val result = if (term != resultterm)
          AnnotatedClause(Clause(Literal(resultterm, lit.polarity)), InferredFrom(Miniscope, Set(cl)), cl.properties)
        else
          cl
      Out.trace(s"Miniscope Result: ${result.pretty(sig)}")
      result
    }


    final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      assert(Clause.unit(cl.cl))
      val lit = cl.cl.lits.head
      assert(!lit.equational)
      val newleft = DefExpSimp(lit.left)(sig)
      val result = AnnotatedClause(Clause(Literal(newleft, lit.polarity)), InferredFrom(DefExpSimp, Set(cl)), cl.properties)
      Out.trace(s"Def expansion: ${result.pretty(sig)}")
      result
    }

    final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      val (cA_lift, lift, lift_other) = LiftEq.canApply(cl.cl)
      if (cA_lift) {
        val result = AnnotatedClause(Clause(LiftEq(lift, lift_other)(sig)), InferredFrom(LiftEq, Set(cl)), cl.properties)
        Out.trace(s"to_eq: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      val (cA_funcExt, fE, fE_other) = FuncExt.canApply(cl.cl)
      if (cA_funcExt) {
        Out.finest(s"Func Ext on: ${cl.pretty(sig)}")
        val result = AnnotatedClause(Clause(FuncExt(leo.modules.calculus.freshVarGen(cl.cl),fE) ++ fE_other), InferredFrom(FuncExt, Set(cl)), cl.properties)
        Out.finest(s"Func Ext result: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def extPreprocessUnify(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      import UnificationControl.doUnify0
      var result: Set[AnnotatedClause] = Set()
      val clIt = cls.iterator

      while(clIt.hasNext) {
        val cl = clIt.next

        var uniLits: Seq[(Term, Term)] = Seq()
        var nonUniLits: Seq[Literal] = Seq()
        var boolExtLits: Seq[Literal] = Seq()
        var nonBoolExtLits: Seq[Literal] = Seq()

        val litIt = cl.cl.lits.iterator

        while(litIt.hasNext) {
          val lit = litIt.next()
          if (!lit.polarity && lit.equational) uniLits = (lit.left, lit.right) +: uniLits
          else nonUniLits = lit +: nonUniLits
          if (BoolExt.canApply(lit)) boolExtLits = lit +: boolExtLits
          else nonBoolExtLits = lit +: nonBoolExtLits
        }

        // (A) if unification literal is present, try to unify the set of unification literals as a whole
        // and add it to the solutions
        // (B) if also boolean extensionality literals present, add (BE/cnf) treated clause to result set, else
        // insert the original clause.
        if (uniLits.nonEmpty) result = result union doUnify0(cl, freshVarGen(cl.cl), uniLits, nonUniLits)(sig)

        if (boolExtLits.isEmpty) {
          result = result + cl
        } else {
          leo.Out.finest(s"Detecting Boolean extensionality literals, inserted expanded clauses...")
          val boolExtResult = BoolExt.apply(boolExtLits, nonBoolExtLits).map(AnnotatedClause(_, InferredFrom(BoolExt, Set(cl)),cl.properties | ClauseAnnotation.PropBoolExt))
          val cnf = CNFControl.cnfSet(boolExtResult)
          result = result union cnf
        }
      }
      result
    }

    type ACSpec = Boolean
    final val ACSpec_Associativity: ACSpec = false
    final val ACSpec_Commutativity: ACSpec = true

    final def detectAC(cl: AnnotatedClause): Option[(Signature#Key, Boolean)] = {
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
        Out.trace(s"AC Simp on ${cl.pretty(sig)}")
        val pre_result = ACSimp.apply(cl.cl,acSymbols)
        val result = AnnotatedClause(pre_result, InferredFrom(ACSimp, Set(cl)), cl.properties)
        Out.finest(s"AC Result: ${result.pretty(sig)}")
        result
      } else
        cl
    }

    final def simp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"[Simp] Processing ${cl.id}")
      val simpresult = Simp(cl.cl)
      val result = if (simpresult != cl.cl)
        AnnotatedClause(simpresult, InferredFrom(Simp, Set(cl)), cl.properties)
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
        AnnotatedClause(simpresult, InferredFrom(Simp, Set(cl)), cl.properties)
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
        assert(rules.forall(_.oriented))

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
        if (rewriteSimp != plainSimp.cl) AnnotatedClause(rewriteSimp, InferredFrom(RewriteSimp, Set(cw)), cw.properties)
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
          if (cur_c != cl) {
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
        val res = AnnotatedClause(resCl, InferredFrom(ReplaceLeibnizEq, Set((cl, ToTPTP(subst, cl.cl.implicitlyBound)(sig)))), cl.properties | ClauseAnnotation.PropNeedsUnification)
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
        val res = AnnotatedClause(resCl, InferredFrom(ReplaceAndrewsEq, Set((cl, ToTPTP(subst, cl.cl.implicitlyBound)(sig)))), cl.properties | ClauseAnnotation.PropNeedsUnification)
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

    val maxLits = Literal.maxOf(cl.lits)
    var litIndex = 0
    var lits = cl.lits
    var side = leftSide

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
  import leo.modules.control.indexingControl.FVIndexControl

  object RedundancyControl {
    /** Returns true iff cl is redundant wrt to processed. */
    final def redundant(cl: AnnotatedClause, processed: Set[AnnotatedClause]): Boolean = {
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
    final def isSubsumed(cl: AnnotatedClause, by: Set[AnnotatedClause]): Boolean = {
      // Current implementation checks feature-vector index for a pre-filter.
      // testFowardSubsumptionFVI also applies the "indeed subsumes"-relation check internally.
      val res = testForwardSubsumptionFVI(cl)
      if (res.nonEmpty)
        Out.trace(s"[Subsumption]: [${cl.id}] subsumed by ${res.map(_.id).mkString(",")}")
      res.nonEmpty
    }

    /** Test for subsumption using the feature vector index as a prefilter, then run
      * "trivial" subsumption check using [[leo.modules.calculus.Subsumption]]. */
    final def testForwardSubsumptionFVI(cl: AnnotatedClause): Set[AnnotatedClause] = {
      val index = FVIndexControl.index
      val clFV = FVIndex.featureVector(FVIndexControl.clauseFeatures, cl)
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
    final def testBackwardSubsumptionFVI(cl: AnnotatedClause): Set[AnnotatedClause] = {
      val index = FVIndexControl.index
      val clFV = FVIndex.featureVector(FVIndexControl.clauseFeatures, cl)
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

  object IndexingControl {
    /** Initiate all index structures. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.
      * @note method may change in future (maybe more arguments will be needed). */
    final def initIndexes(initClauses: Set[AnnotatedClause])(implicit sig: Signature): Unit = {
      FVIndexControl.init(initClauses.toSet)(sig)
      FOIndexControl.foIndexInit()
    }
    /** Insert cl to all relevant indexes used. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.*/
    final def insertIndexed(cl: AnnotatedClause)(implicit sig: Signature): Unit = {
      FVIndexControl.insert(cl)
      FOIndexControl.index.insert(cl)
      // TODO: more indexes ...
    }
    /** Remove cl from all relevant indexes used. This is
      * merely a delegator/distributor to all known indexes such
      * as feature vector index, subsumption index etc.*/
    final def removeFromIndex(cl: AnnotatedClause)(implicit sig: Signature): Unit = {
      FVIndexControl.remove(cl)
      FOIndexControl.index.remove(cl)
      // TODO: more indexes ...
    }
  }

  object FVIndexControl {
    import leo.datastructures.Clause
    import leo.modules.indexing.{CFF, FVIndex}

    private val maxFeatures: Int = 100
    private var initialized = false
    private var features: Seq[CFF] = Seq()
    final protected[modules] val index = FVIndex()
    def clauseFeatures: Seq[CFF] = features

    final def init(initClauses: Set[AnnotatedClause])(implicit sig: Signature): Unit = {
      assert(!initialized)

      val symbs = sig.allUserConstants.toVector
      val featureFunctions: Seq[CFF] = Vector(FVIndex.posLitsFeature(_), FVIndex.negLitsFeature(_)) ++
        symbs.flatMap {symb => Seq(FVIndex.posLitsSymbolCountFeature(symb,_:Clause),
          FVIndex.posLitsSymbolDepthFeature(symb,_:Clause), FVIndex.negLitsSymbolCountFeature(symb,_:Clause), FVIndex.negLitsSymbolDepthFeature(symb,_:Clause))}

      var initFeatures: Seq[Set[Int]] = Seq()
      val featureFunctionIt = featureFunctions.iterator
      var i = 0
      while (featureFunctionIt.hasNext) {
        val cff = featureFunctionIt.next()
        val res = initClauses.map {cw => {cff(cw.cl)}}
        initFeatures = res +: initFeatures
        i = i+1
      }
      Out.trace(s"init Features: ${initFeatures.toString()}")
      val sortedFeatures = initFeatures.zipWithIndex.sortBy(_._1.size).take(maxFeatures)
      Out.trace(s"sorted Features: ${sortedFeatures.toString()}")
      this.features = sortedFeatures.map {case (feat, idx) => featureFunctions(idx)}
      initialized = true

//      val initIt = initClauses.iterator
//      while (initIt.hasNext) {
//        val initCl = initIt.next()
//        insert(initCl)
//      }
    }

    final def insert(cl: AnnotatedClause): Unit = {
      assert(initialized)
      val featureVector = FVIndex.featureVector(features, cl)
      index.insert(featureVector, cl)
    }

    final def insert(cls: Set[AnnotatedClause]): Unit = {
      assert(initialized)
      val clIt = cls.iterator
      while(clIt.hasNext) {
        val cl = clIt.next()
        insert(cl)
      }
    }

    final def remove(cl: AnnotatedClause): Unit = {
      assert(initialized)
      val featureVector = FVIndex.featureVector(features, cl)
      index.remove(featureVector, cl)
    }

    final def remove(cls: Set[AnnotatedClause]): Unit = {
      assert(initialized)
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
        var result: Seq[AnnotatedFormula] = Seq()
        var round : Int = 1

        val conjSymbols = PreFilterSet.useFormula(conjecture)
        val firstPossibleCandidates = PreFilterSet.getCommonFormulas(conjSymbols)
        leo.Out.finest(s"${firstPossibleCandidates.map(_.name)}")
        var taken: Iterable[AnnotatedFormula] = firstPossibleCandidates.filter(f => RelevanceFilter(round)(f))

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

  import java.util.concurrent.TimeUnit

  import leo.datastructures.ClauseAnnotation.NoAnnotation
  import leo.datastructures._
  import leo.modules.HOLSignature.LitFalse
  import leo.modules.external._
  import leo.modules.output._

  object ExternalLEOIIControl {
    @inline final def call(cls: Set[AnnotatedClause])(implicit sig: Signature): StatusSZS = call(cls, 5)(sig)

    final def call(cls: Set[AnnotatedClause], sec: Long)(sig: Signature): StatusSZS = {
      val modifyClauses = cls.map { cl =>
        if (cl.role == Role_NegConjecture) {
          AnnotatedClause(cl.id, cl.cl, Role_Axiom, NoAnnotation, cl.properties)
        } else {
          AnnotatedClause(cl.id, cl.cl, Role_Axiom, NoAnnotation, cl.properties)
        }
      }
      val submitClauses: Set[AnnotatedClause] = modifyClauses + AnnotatedClause(Clause(Literal(LitFalse(), true)), Role_Conjecture, NoAnnotation, ClauseAnnotation.PropNoProp)
      val send = ToTPTP(submitClauses)(sig).map(_.apply)
      Out.finest(s"LEO input:")
      Out.finest(s"${send.mkString("\n")}")
      Out.finest("LEO INPUT END")
      val result = ExternalCall.exec("/home/lex/bin/leo2/bin/leo ", send)
      val resres = result.waitFor(sec, TimeUnit.SECONDS)
      if (resres) {
        Out.finest(s"leo did know: ${exitCodeToSZS(result.exitValue)}")
        exitCodeToSZS(result.exitValue)
      } else {
        Out.finest("leo didnt know")
        SZS_Unknown
      }
    }

    private final def exitCodeToSZS(exitcode: Int): StatusSZS = exitcode match {
      case 0 => SZS_Theorem
      case 1 => SZS_Unsatisfiable
      case _ => SZS_Unknown
    }
  }
}