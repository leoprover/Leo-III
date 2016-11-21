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
  @inline final def preunifySet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.UnificationControl.preunifySet(clSet)(sig)
  // simplification inferences
  @inline final def cnf(cl: AnnotatedClause)(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnf(cl)(sig)
  @inline final def cnfSet(cls: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.CNFControl.cnfSet(cls)(sig)
  @inline final def expandDefinitions(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.expandDefinitions(cl)(sig)
  @inline final def miniscope(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.miniscope(cl)(sig)
  @inline final def switchPolarity(cl: AnnotatedClause): AnnotatedClause = inferenceControl.SimplificationControl.switchPolarity(cl)
  @inline final def liftEq(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.liftEq(cl)(sig)
  @inline final def funcext(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.funcext(cl)(sig)
  @inline final def acSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.acSimp(cl)(sig)
  @inline final def simp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.simp(cl)(sig)
  @inline final def simpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.simpSet(clSet)(sig)
  @inline final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.shallowSimp(cl)(sig)
  @inline final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.SimplificationControl.shallowSimpSet(clSet)(sig)
  @inline final def rewriteSimp(cl: AnnotatedClause, rewriteRules: Set[AnnotatedClause])(implicit sig: Signature): AnnotatedClause = inferenceControl.SimplificationControl.rewriteSimp(cl, rewriteRules)(sig)
  @inline final def convertDefinedEqualities(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertDefinedEqualities(clSet)(sig)
//  @inline final def convertLeibnizEqualities(clSet: Set[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertLeibnizEqualities(clSet)
//  @inline final def convertAndrewsEqualities(clSet: Set[AnnotatedClause]): Set[AnnotatedClause] = inferenceControl.DefinedEqualityProcessing.convertAndrewsEqualities(clSet)
  // Choice
  import leo.datastructures.{Term, Type}
  @inline final def instantiateChoice(cl: AnnotatedClause, choiceFuns: Map[Type, Set[Term]])(implicit sig: Signature): Set[AnnotatedClause] = inferenceControl.Choice.instantiateChoice(cl, choiceFuns)(sig)
  @inline final def detectChoiceClause(cl: AnnotatedClause): Option[leo.datastructures.Term] = inferenceControl.Choice.detectChoiceClause(cl)
  // Redundancy
  @inline final def forwardSubsumptionTest(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = redundancyControl.SubsumptionControl.testForwardSubsumptionFVI(cl)
  @inline final def backwardSubsumptionTest(cl: AnnotatedClause, processed: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = redundancyControl.SubsumptionControl.testBackwardSubsumptionFVI(cl)
  // Indexing
  @inline final def fvIndexInit(initClauses: Set[AnnotatedClause])(implicit sig: Signature): Unit = indexingControl.FVIndexControl.init(initClauses)(sig)
  @inline final def fvIndexInsert(cl: AnnotatedClause): Unit = indexingControl.FVIndexControl.insert(cl)
  @inline final def fvIndexInsert(cls: Set[AnnotatedClause]): Unit = indexingControl.FVIndexControl.insert(cls)
  @inline final def fvIndexRemove(cl: AnnotatedClause): Unit = indexingControl.FVIndexControl.remove(cl)
  @inline final def fvIndexRemove(cls: Set[AnnotatedClause]): Unit = indexingControl.FVIndexControl.remove(cls)
  @inline final def foIndexInit(): Unit = indexingControl.FOIndexControl.foIndexInit()
  @inline final def foIndex: leo.modules.indexing.FOIndex = indexingControl.FOIndexControl.index
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
          } else if (!intoTerm.isVariable && leo.modules.calculus.mayUnify(withTerm, intoTerm)) {
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
            curSubterms = selectSide(hd, side).feasibleOccurences.keySet
            curPositions = selectSide(hd, side).feasibleOccurences(curSubterms.head)
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
                curPositions = selectSide(hd, side).feasibleOccurences(curSubterms.head)
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

    final def preunifySet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = {
      var resultSet: Set[AnnotatedClause] = clSet
      val (uniClauses, otherClauses):(Set[(AnnotatedClause, PreUni.UniLits, PreUni.OtherLits)], Set[AnnotatedClause]) = clSet.foldLeft((Set[(AnnotatedClause, PreUni.UniLits, PreUni.OtherLits)](), Set[AnnotatedClause]())) {case ((uni,ot),cw) =>
        val (cA, ul, ol) = PreUni.canApply(cw.cl)
        if (cA) {
          (uni + ((cw, ul, ol)),ot)
        } else {
          (uni, ot + cw)
        }
      }
      if (uniClauses.nonEmpty) {
        Out.debug("Unification tasks found. Working on it...")
        resultSet = otherClauses
        uniClauses.foreach { case (cw, ul, ol) =>
          Out.debug(s"Unification task from clause ${cw.pretty(sig)}")
          Out.debug(s"Free vars: ${cw.cl.implicitlyBound.toString}")
          val uniResultIterator = PreUni(leo.modules.calculus.freshVarGen(cw.cl), ul, ol)
          val uniResult = uniResultIterator.take(Configuration.UNIFIER_COUNT).toSet
          val result = uniResult.map {case (cl,subst) =>
            AnnotatedClause(cl, InferredFrom(PreUni, Set((cw, ToTPTP(subst._1, cw.cl.implicitlyBound)(sig)))), cw.properties | ClauseAnnotation.PropUnified)}
          Out.trace(s"Uni result:\n\t${result.map(_.pretty(sig)).mkString("\n\t")}")
          resultSet = resultSet union result
        }

      }
      resultSet
    }

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
      val cl = cl0.cl
      assert(cl.lits.nonEmpty)
      val uniLit = cl.lits.last
      assert(!uniLit.polarity)
      val uniResult0 = doUnify0(cl0, freshVarGen, Seq((uniLit.left, uniLit.right)), cl.lits.init)(sig)
      // TODO: Try again to unify all possibly new unification constraints, is that useful?
      var uniResult: Set[AnnotatedClause] = Set()
      val uniResultIt = uniResult0.iterator
      while (uniResultIt.hasNext) {
        val uniRes = uniResultIt.next()
        uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(sig)
      }
      uniResult
    }

    private final def factorUnify(freshVarGen: FreshVarGen, cl0: AnnotatedClause)(sig: Signature): Set[AnnotatedClause] = {
      val cl = cl0.cl
      assert(cl.lits.size >= 2)
      val uniLit1 = cl.lits.last
      val uniLit2 = cl.lits.init.last
      assert(!uniLit1.polarity && !uniLit2.polarity)
      val uniResult0 = doUnify0(cl0, freshVarGen, Seq((uniLit1.left, uniLit1.right), (uniLit2.left, uniLit2.right)), cl.lits.init.init)(sig)
      // Try again to unify all possibly new unification constraints
      var uniResult: Set[AnnotatedClause] = Set()
      val uniResultIt = uniResult0.iterator
      while (uniResultIt.hasNext) {
        val uniRes = uniResultIt.next()
        uniResult = uniResult union defaultUnify(freshVarGen, uniRes)(sig)
      }
      uniResult
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
        // TODO: Discard those with no unifier here?
        // i.e. either one of the two options below
        // at the moment, i prefer (2)
        // (1)
        //      uniResult
        // (2)
        if (uniResult.isEmpty) Set(cl)
        else uniResult
      } else Set(cl)
    }


    private final def doUnify0(cl: AnnotatedClause, freshVarGen: FreshVarGen,
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
//      val resultterm = Skolemization.apply(term, s)
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
      Out.trace(s"Simp on ${cl.id}")
      val simpresult = Simp(cl.cl)
      val result = if (simpresult != cl.cl)
        AnnotatedClause(simpresult, InferredFrom(Simp, Set(cl)), cl.properties)
      else
        cl
      Out.finest(s"Simp result: ${result.pretty(sig)}")
      result
    }
    final def simpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = clSet.map(simp)

    final def shallowSimp(cl: AnnotatedClause)(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"Shallow Simp on ${cl.id}")
      val simpresult = Simp.shallowSimp(cl.cl)
      val result = if (simpresult != cl.cl)
        AnnotatedClause(simpresult, InferredFrom(Simp, Set(cl)), cl.properties)
      else
        cl
      Out.trace(s"Shallow Simp result: ${result.pretty(sig)}")
      result
    }
    final def shallowSimpSet(clSet: Set[AnnotatedClause])(implicit sig: Signature): Set[AnnotatedClause] = clSet.map(shallowSimp)

    final def rewriteSimp(cw: AnnotatedClause, rules: Set[AnnotatedClause])(implicit sig: Signature): AnnotatedClause = {
      Out.trace(s"Rewrite simp on ${cw.id}")
      val sim = simp(cw)
      val rewriteSimp = sim.cl //RewriteSimp.apply(rules.map(_.cl), sim.cl)
      // TODO: simpl to be simplification by rewriting à la E etc
      if (rewriteSimp != sim.cl) AnnotatedClause(rewriteSimp, InferredFrom(RewriteSimp, Set(cw)), cw.properties)
      else sim
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

  object SubsumptionControl {
    import leo.modules.calculus.Subsumption
    import leo.modules.indexing.{ClauseFeature, FVIndex, FeatureVector}
    import leo.datastructures.FixedLengthTrie

    final def testForwardSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] = withSet.filter(cw => Subsumption.subsumes(cw.cl, cl.cl))

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
        testForwardSubsumption(cl, index.valueSet)
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


    final def testBackwardSubsumption(cl: AnnotatedClause, withSet: Set[AnnotatedClause]): Set[AnnotatedClause] =
      withSet.filter(cw => Subsumption.subsumes(cl.cl, cw.cl))


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

  }
}

package indexingControl {

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