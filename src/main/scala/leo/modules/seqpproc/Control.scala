package leo
package modules.seqpproc

import leo.modules.seqpproc.controlStructures._

/**
  * Facade object for various control methods of the seq. proof procedure.
  *
  * @see [[leo.modules.seqpproc.SeqPProc]]
  * @author Alexander Steen <a.steen@fu-berlin.de>
  */
object Control {
  // Generating inferences
  @inline final def paramodSet(cl: ClauseWrapper, withSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.ParamodControl.paramodSet(cl,withSet)
  @inline final def factor(cl: ClauseWrapper): Set[ClauseWrapper] = inferenceControl.FactorizationControl.factor(cl)
  @inline final def boolext(cl: ClauseWrapper): Set[ClauseWrapper] = inferenceControl.BoolExtControl.boolext(cl)
  @inline final def primsubst(cl: ClauseWrapper): Set[ClauseWrapper] = inferenceControl.PrimSubstControl.primSubst(cl)
  @inline final def preunifySet(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.PreUniControl.preunifySet(clSet)
  // simplification inferences
  @inline final def cnf(cl: ClauseWrapper): Set[ClauseWrapper] = inferenceControl.CNFControl.cnf(cl)
  @inline final def expandDefinitions(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.expandDefinitions(cl)
  @inline final def switchPolarity(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.switchPolarity(cl)
  @inline final def liftEq(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.liftEq(cl)
  @inline final def funcext(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.funcext(cl)
  @inline final def acSimp(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.acSimp(cl)
  @inline final def simp(cl: ClauseWrapper): ClauseWrapper = inferenceControl.SimplificationControl.simp(cl)
  @inline final def simpSet(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.SimplificationControl.simpSet(clSet)
  @inline final def convertDefinedEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.DefinedEqualityProcessing.convertDefinedEqualities(clSet)
  @inline final def convertLeibnizEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.DefinedEqualityProcessing.convertLeibnizEqualities(clSet)
  @inline final def convertAndrewsEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = inferenceControl.DefinedEqualityProcessing.convertAndrewsEqualities(clSet)
  // Redundancy
  @inline final def indexedForwardSubsumptionTest(cl: ClauseWrapper, processed: Set[ClauseWrapper]): Boolean = ???
  // Indexing
  final def fvIndexInit(initClauses: Set[ClauseWrapper]): Unit = indexingControl.FVIndexControl.init(initClauses)
  final def fvIndexInsert(cl: ClauseWrapper): Unit = indexingControl.FVIndexControl.insert(cl)
  // External prover call
  @inline final def callExternalLeoII(clauses: Set[ClauseWrapper]) = externalProverControl.ExternalLEOIIControl.call(clauses)
}

/** Package collcetion control objects for inference rules.
  *
  * @see [[leo.modules.calculus.CalculusRule]] */
package inferenceControl {
  import leo.datastructures._
  import Literal.Side

  package object inferenceControl {
    type LiteralIndex = Int
    type WithConfiguration = (LiteralIndex, Literal, Side)
  }


  protected[modules] object CNFControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def cnf(cl: ClauseWrapper): Set[ClauseWrapper] = {
      Out.trace(s"CNF of ${cl.id}")
      val cnfresult = CNF(leo.modules.calculus.freshVarGen(cl.cl), cl.cl).toSet
      val cnfsimp = cnfresult.map(Simp.shallowSimp)
      val result = cnfresult.map {c => ClauseWrapper(c, InferredFrom(CNF, Set(cl)), cl.properties)}
      Out.trace(s"CNF result:\n\t${result.map(_.pretty).mkString("\n\t")}")
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
    final def paramodSet(cl: ClauseWrapper, withset: Set[ClauseWrapper]): Set[ClauseWrapper] = {
      var results: Set[ClauseWrapper] = Set()
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
      results
    }

    final def allParamods(cl: ClauseWrapper, other: ClauseWrapper): Set[ClauseWrapper] = {
      // Do paramod with cl into other
      val res = allParamods0(cl, other)
      if (cl.id != other.id) {
        // do paramod with other into cl
        res ++ allParamods0(other, cl)
      } else res
    }

    final private def allParamods0(withWrapper: ClauseWrapper, intoWrapper: ClauseWrapper): Set[ClauseWrapper] = {
      import leo.datastructures.ClauseAnnotation.InferredFrom
      assert(!Configuration.SOS || leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, withWrapper.properties) ||
        leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, intoWrapper.properties))

      var results: Set[ClauseWrapper] = Set()

      val withClause = withWrapper.cl
      val intoClause = intoWrapper.cl

      val withConfigurationIt = new LiteralSideIterator(withClause, true, true, false)
      while (withConfigurationIt.hasNext) {
        val (withIndex, withLit, withSide) = withConfigurationIt.next()
        val withTerm = if (withSide) withLit.left else withLit.right

        assert(withClause.lits(withIndex) == withLit, s"$withIndex in ${withClause.pretty}\n lit = ${withLit.pretty}")
        assert(withClause.lits(withIndex).polarity)

        val intoConfigurationIt = intoConfigurationIterator(intoClause)
        while (intoConfigurationIt.hasNext) {
          val (intoIndex, intoLit, intoSide, intoPos, intoTerm) = intoConfigurationIt.next()

          if (!intoTerm.isVariable && leo.modules.calculus.mayUnify(withTerm, intoTerm)) {
            Out.trace(s"May unify: ${withTerm.pretty} with ${intoTerm.pretty} (subterm at ${intoPos.pretty})")
            val newCl = OrderedParamod(withClause, withIndex, withSide,
              intoClause, intoIndex, intoSide, intoPos, intoTerm)

            val newClWrapper = ClauseWrapper(newCl, InferredFrom(OrderedParamod, Set(withWrapper, intoWrapper)), ClauseAnnotation.PropSOS)
            Out.finest(s"Result: ${newClWrapper.pretty}")
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

    final private def intoConfigurationIterator(cl: Clause): Iterator[IntoConfiguration] = new Iterator[IntoConfiguration] {

      import Literal.{leftSide, rightSide, selectSide}

      val maxLits = cl.maxLits
      var litIndex = 0
      var lits = cl.lits
      var side = leftSide
      var curSubterms: Set[Term] = null
      var curPositions: Set[Position] = null

      def hasNext: Boolean = if (lits.isEmpty) false
      else {
        val hd = lits.head
        if (!maxLits.contains(hd)) {
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

    import leo.datastructures.Not
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def factor(cl: ClauseWrapper): Set[ClauseWrapper] = {
      Out.debug(s"Factor in ${cl.id}")
      var res: Set[ClauseWrapper] = Set()
      val clause = cl.cl
      val maxLitIt = new LiteralSideIterator(clause, true, true, true)

      while (maxLitIt.hasNext) {
        val (maxLitIndex, maxLit, maxLitSide) = maxLitIt.next()
        val otherLitIt = new LiteralSideIterator(clause, false, true, true)

        while (otherLitIt.hasNext) {
          val (otherLitIndex, otherLit, otherLitSide) = otherLitIt.next()

          if (maxLitIndex != otherLitIndex) {
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
                val resultprop = if (leo.datastructures.isPropSet(ClauseAnnotation.PropSOS,cl.properties))
                  ClauseAnnotation.PropSOS
                else ClauseAnnotation.PropNoProp

                val result = ClauseWrapper(factor, InferredFrom(OrderedEqFac, Set(cl)), resultprop)
                res = res + result
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
//                    val result = ClauseWrapper(factor, InferredFrom(OrderedEqFac, Set(cl)))
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
//                    val result = ClauseWrapper(factor, InferredFrom(OrderedEqFac, Set(cl)))
//                    res = res + result
//                  }
                }
              }
            }
          }
        }
      }

      Out.trace(s"Factor result:\n\t${res.map(_.pretty).mkString("\n\t")}")
      res
    }
  }

  protected[modules] object PreUniControl {
    import leo.modules.output.ToTPTP
    import leo.datastructures.ClauseAnnotation._

    final def preunifySet(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = {
      var resultSet: Set[ClauseWrapper] = clSet
      val (uniClauses, otherClauses):(Set[(ClauseWrapper, PreUni.UniLits, PreUni.OtherLits)], Set[ClauseWrapper]) = clSet.foldLeft((Set[(ClauseWrapper, PreUni.UniLits, PreUni.OtherLits)](), Set[ClauseWrapper]())) {case ((uni,ot),cw) => {
        val (cA, ul, ol) = PreUni.canApply(cw.cl)
        if (cA) {
          (uni + ((cw, ul, ol)),ot)
        } else {
          (uni, ot + cw)
        }
      }}
      if (uniClauses.nonEmpty) {
        Out.debug("Unification tasks found. Working on it...")
        resultSet = otherClauses
        uniClauses.foreach { case (cw, ul, ol) =>
          Out.debug(s"Unification task from clause ${cw.pretty}")
          val nc = PreUni(leo.modules.calculus.freshVarGen(cw.cl), cw.cl, ul, ol).map{case (cl,subst) => ClauseWrapper(cl, InferredFrom(PreUni, Set((cw, ToTPTP(subst)))), cw.properties | ClauseAnnotation.PropUnified)}
          Out.trace(s"Uni result:\n\t${nc.map(_.pretty).mkString("\n\t")}")
          resultSet = resultSet union nc
        }

      }
      resultSet
    }
  }


  protected[modules] object BoolExtControl {
    import leo.datastructures.ClauseAnnotation._

    final def boolext(cw: ClauseWrapper): Set[ClauseWrapper] = {
      var res: Set[ClauseWrapper] = Set()
      if (!Configuration.isSet("nbe")) {
        if (!leo.datastructures.isPropSet(PropBoolExt, cw.properties)) {
          val (cA_boolExt, bE, bE_other) = BoolExt.canApply(cw.cl)
          if (cA_boolExt) {
            Out.debug(s"Bool Ext on: ${cw.pretty}")
            val boolExt_cws = BoolExt.apply(bE, bE_other).map(ClauseWrapper(_, InferredFrom(BoolExt, Set(cw)),cw.properties | ClauseAnnotation.PropBoolExt))
            Out.trace(s"Bool Ext result:\n\t${boolExt_cws.map(_.pretty).mkString("\n\t")}")

            res = boolExt_cws.flatMap(cw => {
              Out.finest(s"#\ncnf of ${cw.pretty}:\n\t")
              CNF(leo.modules.calculus.freshVarGen(cw.cl), cw.cl)
            }.map(c => {
              Out.finest(s"${c.pretty}\n\t")
              ClauseWrapper(c, InferredFrom(CNF, Set(cw)), cw.properties)
            }))
          }
        }
      }
      res
    }
  }

  protected[modules] object PrimSubstControl {
    import leo.modules.output.ToTPTP
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def primSubst(cw: ClauseWrapper): Set[ClauseWrapper] = {
      // TODO: Read from configuration thorougness of prim subst.
      val (cA_ps, ps_vars) = StdPrimSubst.canApply(cw.cl)
      if (cA_ps) {
        Out.debug(s"Prim subst on: ${cw.id}")
        val new_ps_pre = StdPrimSubst(cw.cl, ps_vars)
        val new_ps = new_ps_pre.map{case (cl,subst) => ClauseWrapper(cl, InferredFrom(StdPrimSubst, Set((cw,ToTPTP(subst)))), cw.properties)}
        Out.trace(s"Prim subst result:\n\t${new_ps.map(_.pretty).mkString("\n\t")}")
        return new_ps
      }
      Set()
    }
  }

  protected[modules] object Choice {

    import leo.datastructures.Term.{TermApp, Bound}
    import leo.datastructures.Type.{->}

    final def detectChoice(cw: ClauseWrapper): Boolean = {
      val clause = cw.cl
      if (clause.lits.size == 2) {
        val lit1 = clause.lits(0)
        val lit2 = clause.lits(1)
        if (!lit1.equational && !lit2.equational) {
          val term1 = lit1.left
          val term2 = lit2.left

          term1 match {
            case TermApp(Bound(varTy, varIdx), Seq(Bound(ptherVarTy, otherVarIdx))) =>
            case _ =>
          }
          ???
        } else
          false
      } else
        false

    }

    private final def findCandidate(t: Term): (Boolean, Term, Term) = {
      t match {
        case TermApp(var1, Seq(TermApp(choiceSymb, Seq(var2)))) => {
          choiceSymb.ty match {
            case ((a -> b) -> c) => ???
            case _ => (false, null, null)
          }
        }
        case _ => ???
      }
    }
  }

  protected[modules] object SimplificationControl {
    import leo.datastructures.ClauseAnnotation.InferredFrom

    final def switchPolarity(cl: ClauseWrapper): ClauseWrapper = {
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
        val result = ClauseWrapper(Clause(newLits), InferredFrom(PolaritySwitch, Set(cl)), cl.properties)
        Out.trace(s"Switch polarity: ${result.pretty}")
        result
      } else
        cl

    }

    final def expandDefinitions(cl: ClauseWrapper): ClauseWrapper = {
      assert(Clause.unit(cl.cl))
      val lit = cl.cl.lits.head
      assert(!lit.equational)
      Out.trace(s"def exp from ${cl.pretty}")
      Out.trace(s"sos flag set in from: ${leo.datastructures.isPropSet(ClauseAnnotation.PropSOS, cl.properties)}")
      val newleft = DefExpSimp(lit.left)
      val result = ClauseWrapper(Clause(Literal(newleft, lit.polarity)), InferredFrom(DefExpSimp, Set(cl)), cl.properties)
      Out.trace(s"Def expansion: ${result.pretty}")
      result
    }

    final def liftEq(cl: ClauseWrapper): ClauseWrapper = {
      val (cA_lift, lift, lift_other) = LiftEq.canApply(cl.cl)
      if (cA_lift) {
        val result = ClauseWrapper(Clause(LiftEq(lift, lift_other)), InferredFrom(LiftEq, Set(cl)), cl.properties)
        Out.trace(s"to_eq: ${result.pretty}")
        result
      } else
        cl
    }

    final def funcext(cl: ClauseWrapper): ClauseWrapper = {
      val (cA_funcExt, fE, fE_other) = FuncExt.canApply(cl.cl)
      if (cA_funcExt) {
        Out.finest(s"Func Ext on: ${cl.pretty}")
        val result = ClauseWrapper(Clause(FuncExt(leo.modules.calculus.freshVarGen(cl.cl),fE) ++ fE_other), InferredFrom(FuncExt, Set(cl)), cl.properties)
        Out.finest(s"Func Ext result: ${result.pretty}")
        result
      } else
        cl
    }

    final def acSimp(cl: ClauseWrapper): ClauseWrapper = {
      import leo.datastructures.impl.Signature
      if (Configuration.isSet("acsimp")) {
        val acSymbols = Signature.get.acSymbols
        Out.trace(s"AC Simp on ${cl.pretty}")
        val pre_result = ACSimp.apply(cl.cl,acSymbols)
        val result = ClauseWrapper(pre_result, InferredFrom(ACSimp, Set(cl)), cl.properties)
        Out.finest(s"AC Result: ${result.pretty}")
        result
      } else
        cl
    }

    final def simp(cl: ClauseWrapper): ClauseWrapper = {
      Out.trace(s"Simp on ${cl.id}")
      val result = ClauseWrapper(Simp(cl.cl), InferredFrom(Simp, Set(cl)), cl.properties)
      Out.finest(s"Simp result: ${result.pretty}")
      result
    }
    final def simpSet(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = clSet.map(simp)
  }

  protected[modules] object DefinedEqualityProcessing {
    import leo.modules.output.ToTPTP
    import leo.datastructures.ClauseAnnotation._

    final def convertDefinedEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = {
      val replaceLeibniz = !Configuration.isSet("nleq")
      val replaceAndrews = !Configuration.isSet("naeq")
      if (replaceLeibniz || replaceAndrews) {
        clSet.map { c =>
          var cur_c = c
          Out.finest(s"Searching for defined equalities in ${c.id}")
          if (replaceLeibniz) {
            cur_c = convertLeibniz0(cur_c)
          }
          if (replaceAndrews) {
            cur_c = convertAndrews0(cur_c)
          }
          cur_c
        }
      }
      clSet
    }

    // Leibniz Equalities
    final def convertLeibnizEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = {
      if (Configuration.isSet("nleq")) clSet
      else
        clSet.map(convertLeibniz0)
    }

    final def convertLeibnizEqualities(cl: ClauseWrapper): ClauseWrapper = {
      if (Configuration.isSet("nleq")) cl
      else convertLeibniz0(cl)
    }
    @inline private final def convertLeibniz0(cl: ClauseWrapper): ClauseWrapper = {
      val (cA_leibniz, leibTermMap) = ReplaceLeibnizEq.canApply(cl.cl)
      if (cA_leibniz) {
        Out.trace(s"Replace Leibniz equalities in ${cl.id}")
        val (resCl, subst) = ReplaceLeibnizEq(cl.cl, leibTermMap)
        val res = ClauseWrapper(resCl, InferredFrom(ReplaceLeibnizEq, Set((cl, ToTPTP(subst)))), cl.properties)
        Out.finest(s"Result: ${res.pretty}")
        res
      } else
        cl
    }

    // Andrews Equalities
    final def convertAndrewsEqualities(clSet: Set[ClauseWrapper]): Set[ClauseWrapper] = {
      if (Configuration.isSet("naeq")) clSet
      else clSet.map(convertAndrews0)
    }

    final def convertAndrewsEqualities(cl: ClauseWrapper): ClauseWrapper = {
      if (Configuration.isSet("naeq")) cl
      else convertAndrews0(cl)
    }
    @inline private final def convertAndrews0(cl: ClauseWrapper): ClauseWrapper = {
      val (cA_Andrews, andrewsTermMap) = ReplaceAndrewsEq.canApply(cl.cl)
      if (cA_Andrews) {
        Out.trace(s"Replace Andrews equalities in ${cl.id}")
        val (resCl, subst) = ReplaceAndrewsEq(cl.cl, andrewsTermMap)
        val res = ClauseWrapper(resCl, InferredFrom(ReplaceAndrewsEq, Set((cl, ToTPTP(subst)))), cl.properties)
        Out.finest(s"Result: ${res.pretty}")
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
  protected final class LiteralSideIterator(cl: Clause, onlyMax: Boolean, onlyPositive: Boolean, alsoFlexheads: Boolean) extends Iterator[inferenceControl.WithConfiguration] {
    import Literal.{leftSide, rightSide}

    val maxLits = cl.maxLits
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
  object Subsumption {
    def testForwardSubsumption(cl: ClauseWrapper, withSet: Set[ClauseWrapper]): Boolean = ???
  }
}

package indexingControl {

  import leo.datastructures.impl.Signature

  object FVIndexControl {
    import leo.modules.indexing.CFF
    import leo.modules.indexing.FVIndex
    import leo.datastructures.Clause

    val maxFeatures: Int = 150
    var initialized = false
    var features: Seq[CFF] = Seq()

    final def init(initClauses: Set[ClauseWrapper]): Unit = {
      assert(!initialized)

      val symbs = Signature.get.allUserConstants.toVector
      val featureFunctions: Seq[CFF] = Vector(FVIndex.posLitsFeature(_), FVIndex.negLitsFeature(_)) ++
        symbs.flatMap {case symb => Seq(FVIndex.posLitsSymbolCountFeature(symb,_:Clause),
        FVIndex.posLitsSymbolDepthFeature(symb,_:Clause), FVIndex.negLitsSymbolCountFeature(symb,_:Clause), FVIndex.negLitsSymbolDepthFeature(symb,_:Clause))}

      var initFeatures: Seq[Set[Int]] = Seq()
      val featureFunctionIt = featureFunctions.iterator
      var i = 0
      while (featureFunctionIt.hasNext) {
        val cff = featureFunctionIt.next()
        val res = initClauses.map {case cw => {Out.finest(s"$i feature of ${cw.pretty} = ${cff(cw.cl)}");cff(cw.cl)}}
        initFeatures = res +: initFeatures
        i = i+1
      }
      Out.trace(s"init Features: ${initFeatures.toString()}")
      val sortedFeatures = initFeatures.zipWithIndex.sortBy(_._1.size).take(maxFeatures)
      Out.trace(s"sorted Features: ${sortedFeatures.toString()}")
      this.features = sortedFeatures.map {case (feat, idx) => featureFunctions(idx)}
      initialized = true

      val initIt = initClauses.iterator
      while (initIt.hasNext) {
        val initCl = initIt.next()
        insert(initCl)
      }
    }

    final def insert(cl: ClauseWrapper): Unit = {
      assert(initialized)
      val featureVector = features.map(_(cl.cl))
      FVIndex.add(cl.cl, featureVector)
    }
  }
}

package  externalProverControl {

  import leo.datastructures.ClauseAnnotation.NoAnnotation
  import leo.modules.output._
  import leo.datastructures._
  import leo.modules.external._
  import java.util.concurrent.TimeUnit

  object ExternalLEOIIControl {
    @inline final def call(cls: Set[ClauseWrapper]): StatusSZS = call(cls, 5)

    final def call(cls: Set[ClauseWrapper], sec: Long): StatusSZS = {
      val modifyClauses = cls.map { cl =>
        if (cl.role == Role_NegConjecture) {
          ClauseWrapper(cl.id, cl.cl, Role_Axiom, NoAnnotation, cl.properties)
        } else {
          ClauseWrapper(cl.id, cl.cl, Role_Axiom, NoAnnotation, cl.properties)
        }
      }
      val submitClauses: Set[ClauseWrapper] = modifyClauses + ClauseWrapper(Clause(Literal(LitFalse(), true)), Role_Conjecture, NoAnnotation, ClauseAnnotation.PropNoProp)
      val send = ToTPTP(submitClauses).map(_.output)
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

////////////////////////////////////////////////////////
// Utility structures
///////////////////////////////////////////////////////

/**
  * Package containing utility structures wrapping clauses etc. with additional
  * information to guide proof search etc.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 27.02.16 (Contents older)
  */
package controlStructures {
  import leo.datastructures.{Role_Plain, Role, Clause, ClauseAnnotation, ClauseProxy}

  protected[seqpproc] case class ClauseWrapper(id: String, cl: Clause, role: Role, annotation: ClauseAnnotation,
                                               var properties: ClauseAnnotation.ClauseProp) extends ClauseProxy with Ordered[ClauseWrapper] {
    override def equals(o: Any): Boolean = o match {
      case cw: ClauseProxy => cw.cl == cl // TODO: Does this make sense?
      case _ => false
    }
    override def compare(that: ClauseWrapper) = Configuration.CLAUSE_ORDERING.compare(this.cl, that.cl)
    override def hashCode(): Int = cl.hashCode()  // TODO: Does this make sense?
  }

  protected[seqpproc] object ClauseWrapper {
    private var counter: Int = 0

    def apply(cl: Clause, r: Role, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp): ClauseWrapper = {
      counter += 1
      ClauseWrapper(s"gen_formula_$counter", cl, r, annotation, propFlag)
    }

    def apply(cl: Clause, annotation: ClauseAnnotation, propFlag: ClauseAnnotation.ClauseProp = ClauseAnnotation.PropNoProp): ClauseWrapper =
      apply(cl, Role_Plain, annotation, propFlag)
  }
}
