package leo.modules.calculus

import leo.datastructures.Type.BoundType
import leo.datastructures.{Subst, Term, Type}


trait Unification extends CalculusRule {
  import leo.modules.output.SZS_EquiSatisfiable

  val name = "pre_uni_full"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  /** A `UEq` is an unsolved equation. */
  type UEq = (Term, Term)

  type TermSubst = Subst
  type TypeSubst = Subst
  type ResultSubst = (TermSubst, TypeSubst)

  type UnificationResult = (ResultSubst, Seq[UEq])

  /**
    * Generates a stream of `UnificationResult`s (tuples of substitutions and unsolved equations)
    * where each result solves the unification constraint `t = s`. The unsolved equations on the `UnificationResult`
    * are hereby all flex-flex unification constraints that are postponed. The result stream
    * is empty, if the equation `t = s` is not unifiable.
    */
  def unify(vargen: FreshVarGen, t : Term, s : Term): Iterable[UnificationResult]

  /**
    * Generates a stream of `UnificationResult`s (tuples of substitutions and unsolved equations)
    * where each result solves all unification constraints `t_i = s_i` in `constraints`.
    * The unsolved equations on the `UnificationResult`
    * are hereby all flex-flex unification constraints that are postponed. The result stream
    * is empty, if the equation `t = s` is not unifiable.
    */
  def unifyAll(vargen: FreshVarGen, constraints: Seq[(Term, Term)]): Iterable[UnificationResult]
}

/**
 * Tests solely for equality
 */
object IdComparison extends Unification{
  override def unify(vargen: FreshVarGen, t: Term, s: Term) : Iterable[UnificationResult] =
    if (s == t) Stream(((Subst.id, Subst.id), Seq())) else Stream.empty

  override def unifyAll(vargen: FreshVarGen, constraints: Seq[UEq]): Iterable[UnificationResult] =
    if (constraints.forall(eq => eq._1 == eq._2)) Stream(((Subst.id, Subst.id), Seq()))
    else Stream.empty
}


/**
  * First-order unification
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since June 2016
  */
object FOUnification extends Unification {

  @inline override final def unify(varGen: FreshVarGen, t: Term, s: Term): Iterable[UnificationResult] =
    unify0(varGen, Seq((t,s)))

  @inline override final def unifyAll(varGen: FreshVarGen, constraints: Seq[UEq]): Iterable[UnificationResult] =
    unify0(varGen, constraints)

  //FIXME: Does not respect type substitution/equations.
  private final def unify0(vargen: FreshVarGen, constraints: Seq[UEq]): Iterable[UnificationResult] = {
    val (unsolved, solved, _, _) = HuetsPreUnification.detExhaust(vargen, constraints, Seq(), Seq(), Seq())
    if (  unsolved.isEmpty) {
      Stream(((HuetsPreUnification.computeSubst(solved), Subst.id), Seq()))
    } else Stream.empty
  }

}

/**
  * Implementation of Huets unification procedure. Flex-flex pairs are returned (not instantiated)
  * together with a substitution (unifier).
  *
  * @author Tomer Libal <shaolintl@gmail.com>
  *           Alexander Steen <a.steen@fu-berlin.de>
  * @since 4/15/15
  * @note Alex: Overhaul of the previous implementation (August '16) to use multiple lists
  *       (for prevent sorting) and correctly handle bound/free variables in terms.
  */
object HuetsPreUnification2 extends Unification {
  import scala.annotation.tailrec
  import leo.datastructures.{SearchConfiguration, NDStream, BFSAlgorithm}

  /** The Depth is the number of lambda abstractions under which a term is nested.*/
  type Depth = Int


  /** `UEq0` extends UEq with an depth indicator. */
  type UEq0 = (Term, Term, Depth)
  /** A `SEq` is a solved equation. */
  type SEq = (Term, Term)

  /** `UTEq` is an unsolved type equation. */
  type UTEq = (Type, Type)
  /** `STEq` is a solved type equation. */
  type STEq = UTEq


  /** Maximal unification search depth (i.e. number of flex-rigid rules on search path). */
  final lazy val MAX_DEPTH = leo.Configuration.UNIFICATION_DEPTH


  /////////////////////////////////////
  // the state of the search space
  /////////////////////////////////////
  protected case class MyConfiguration(unprocessed: Seq[UEq],
                                       flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
                                       solved: TermSubst, solvedTy: TypeSubst,
                                       result: Option[UnificationResult], searchDepth: Int)
    extends SearchConfiguration[UnificationResult] {
    def this(result: UnificationResult) = this(null, null, null, null, null, Some(result), Int.MaxValue) // for success
    def this(unprocessed: Seq[UEq],
             flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
             solved: TermSubst, solvedTy: TypeSubst,
             searchDepth: Int) = this(unprocessed, flexRigid, flexFlex, solved, solvedTy, None, searchDepth) // for in node

    override final def isTerminal: Boolean = searchDepth >= MAX_DEPTH
    override def toString  = s"{${unprocessed.map(x => s"<${x._1},${x._2}>").mkString}}"
  }


  /////////////////////////////////////
  // Unifier search starts with these methods
  /////////////////////////////////////
  final def unify (vargen: FreshVarGen, t1 : Term, s1 : Term) : Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val t_ty = t1.ty
    val s_ty = t1.ty
    val initialTypeSubst = tyDetExhaust(Seq((t_ty, s_ty)), Subst.id)
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val t = t1.substitute(Subst.id, initialTypeSubst0).etaExpand
      val s = s1.substitute(Subst.id, initialTypeSubst0).etaExpand
      new NDStream[UnificationResult](new MyConfiguration(Seq((t,s)), Seq(), Seq(), Subst.id, Subst.id, 0), new EnumUnifier(vargen, initialTypeSubst0)) with BFSAlgorithm
    }
  }

  final def unifyAll(vargen: FreshVarGen, constraints: Seq[(Term, Term)]): Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val initialTypeSubst = tyDetExhaust(constraints.map(e => (e._1.ty, e._2.ty)), Subst.id)
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val constraints0 = constraints.map(eq => (eq._1.substitute(Subst.id, initialTypeSubst0).etaExpand, eq._2.substitute(Subst.id, initialTypeSubst0).etaExpand))
      new NDStream[UnificationResult](new MyConfiguration(constraints0, Seq(), Seq(), Subst.id, Subst.id, 0), new EnumUnifier(vargen, initialTypeSubst0)) with BFSAlgorithm
    }

  }


  /////////////////////////////////////
  // Internal search functions
  /////////////////////////////////////
  /** the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch) */
  protected class EnumUnifier(vargen: FreshVarGen, initialTypeSubst: TypeSubst) extends Function1[SearchConfiguration[UnificationResult], Seq[SearchConfiguration[UnificationResult]]] {

    // Huets procedure is defined here
    override def apply(conf2: SearchConfiguration[UnificationResult]): Seq[SearchConfiguration[UnificationResult]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (fail, flexRigid, flexFlex, partialUnifier, partialTyUnifier) = detExhaust(conf.unprocessed,
                                                                                          conf.flexRigid, conf.flexFlex,
                                                                                          conf.solved, Seq(), conf.solvedTy)
      leo.Out.finest(s"Finished detExhaust")
      // if uTyProblems is non-empty fail
      if (fail) {
        leo.Out.debug(s"Unification failed.")
        Seq()
      } else {
        // if there is no unsolved equation (other than flex-flex), then succeed
        if (flexRigid.isEmpty) {
          leo.Out.debug(s"Unification finished")
          leo.Out.debug(s"\tTerm substitution ${partialUnifier.normalize.pretty}")
          leo.Out.debug(s"\tType substitution ${partialTyUnifier.normalize.pretty}")
          Seq(new MyConfiguration(((partialUnifier.normalize, initialTypeSubst.comp(partialTyUnifier).normalize), flexFlex)))
        }
        // else do flex-rigid cases
        else {
          assert(flexRigid.nonEmpty)
          leo.Out.finest(s"flex-rigid at depth ${conf.searchDepth}")
          val head = flexRigid.head
          import  scala.collection.mutable.ListBuffer
          val lb = new ListBuffer[MyConfiguration]
          // compute the imitate partial binding and add the new configuration
          if (ImitateRule.canApply(head)) lb.append(new MyConfiguration(Seq(ImitateRule(vargen, head)), flexRigid, flexFlex,
            partialUnifier, partialTyUnifier, conf.searchDepth+1))

          // compute all the project partial bindings and add them to the return list
          ProjectRule(vargen, head).foreach (e => lb.append(new MyConfiguration(Seq(e), flexRigid, flexFlex,
            partialUnifier, partialTyUnifier, conf.searchDepth+1)))

          lb.toList
        }
      }
    }
  }

  @tailrec
  private def tyDetExhaust(uTyProblems: Seq[UTEq], unifier: TypeSubst): Option[TypeSubst] = {
    if (uTyProblems.nonEmpty) {
      val head = uTyProblems.head

      if (TyDeleteRule.canApply(head))
        tyDetExhaust(uTyProblems.tail, unifier)
      else if (TyDecompRule.canApply(head))
        tyDetExhaust(TyDecompRule.apply(head) ++ uTyProblems.tail, unifier)
      else {
        val tyFunDecompRuleCanApplyHint = TyFunDecompRule.canApply(head)
        if (tyFunDecompRuleCanApplyHint != TyFunDecompRule.CANNOT_APPLY) {
          tyDetExhaust(TyFunDecompRule.apply(head, tyFunDecompRuleCanApplyHint) ++ uTyProblems.tail,unifier)
        } else if (TyBindRule.canApply(head))
          tyDetExhaust(uTyProblems.tail, unifier.comp(TyBindRule.apply(head)))
        else
          None
      }
    } else Some(unifier)
  }

  /** Exhaustively apply delete, comp and bind on the set  of unprocessed equations. */
  @tailrec
  private def detExhaust(unprocessed: Seq[UEq],
                         flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
                         solved: TermSubst,
                         uTyProblems: Seq[UTEq], solvedTy: TypeSubst):
                        (Boolean, Seq[UEq0], Seq[UEq], TermSubst, TypeSubst) = {
    //                  (fail, flexRigid, flexflex, solved, solvedTy)
    leo.Out.trace(s"Unsolved (term eqs): ${unprocessed.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    leo.Out.trace(s"Unsolved (type eqs): ${uTyProblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    if (uTyProblems.nonEmpty) {
      val head = uTyProblems.head

      // Try all type rules
      if (TyDeleteRule.canApply(head))
        detExhaust(unprocessed, flexRigid, flexFlex, solved, uTyProblems.tail, solvedTy)
      else if (TyDecompRule.canApply(head))
        detExhaust(unprocessed, flexRigid, flexFlex, solved, TyDecompRule.apply(head) ++ uTyProblems.tail, solvedTy)
      else {
        val tyFunDecompRuleCanApplyHint = TyFunDecompRule.canApply(head)
        if (tyFunDecompRuleCanApplyHint != TyFunDecompRule.CANNOT_APPLY) {
          detExhaust(unprocessed, flexRigid, flexFlex, solved,
            TyFunDecompRule.apply(head, tyFunDecompRuleCanApplyHint) ++ uTyProblems.tail, solvedTy)
        } else if (TyBindRule.canApply(head)) {
          val subst = TyBindRule.apply(head)
          leo.Out.finest(s"Ty Bind: ${subst.pretty}")
          detExhaust(applySubstToList(Subst.id, subst, unprocessed),
            applyTySubstToList(subst, flexRigid), applySubstToList(Subst.id, subst, flexFlex),
            solved.applyTypeSubst(subst), uTyProblems.tail, solvedTy.comp(subst))
        }
        else // No type rule applicable for head, so it's a fail, just return a fail state
          (true, flexRigid, flexFlex, solved, solvedTy)
      }
    } else {
      // check unprocessed
      if (unprocessed.nonEmpty) {
        val head0 = unprocessed.head
        leo.Out.finest(s"detExhaust on: ${head0._1.pretty} = ${head0._2.pretty}")
        // Try all term rules
        if (DeleteRule.canApply(head0)) {
          leo.Out.finest("Apply delete")
          detExhaust(unprocessed.tail, flexRigid, flexFlex, solved, uTyProblems, solvedTy)
        } else {
          val left = head0._1
          val right = head0._2

          val (leftBody, leftAbstractions) = collectLambdas(left)
          val (rightBody, rightAbstractions) = collectLambdas(right)
          assert(leftAbstractions == rightAbstractions)
          val abstractionCount = leftAbstractions.size

          if (DecompRule.canApply((leftBody, rightBody), abstractionCount)) {
            leo.Out.finest("Apply decomp")
            val (newUnsolvedTermEqs, newUnsolvedTypeEqs) = DecompRule.apply((leftBody, rightBody), leftAbstractions)
            detExhaust(newUnsolvedTermEqs ++ unprocessed.tail, flexRigid, flexFlex,
              solved, newUnsolvedTypeEqs ++ uTyProblems, solvedTy)
          } else {
            val bindHint = BindRule.canApply(head0,
              leftBody, leftAbstractions, rightBody, rightAbstractions, abstractionCount)
            if (bindHint != BindRule.CANNOT_APPLY) {
              val subst = BindRule.apply(head0, abstractionCount, bindHint)
              leo.Out.finest(s"Bind: ${subst.pretty}")
              detExhaust(
                applySubstToList(subst, Subst.id, flexRigid.map(e => (e._1, e._2)) ++ flexFlex ++ unprocessed.tail),
                Seq(), Seq(),
                solved.comp(subst), uTyProblems, solvedTy)
            } else {
              // ... move to according list if nothing applies
              if (flexflex(head0, abstractionCount))
                detExhaust(unprocessed.tail, flexRigid, head0 +: flexFlex,
                  solved, uTyProblems, solvedTy)
              else if (rigidrigid(head0, abstractionCount))
                (true, flexRigid, flexFlex, solved, solvedTy) // fail
              else {
                assert(flexrigid(head0, abstractionCount))
                detExhaust(unprocessed.tail, (left, right, abstractionCount) +: flexRigid, flexFlex,
                  solved, uTyProblems, solvedTy)
              }
            }
          }
        }
      } else {
        // no unprocessed left, return sets
        (false, flexRigid, flexFlex, solved, solvedTy)
      }
    }
  }


  /////////////////////////////////////
  // Huets rules
  /////////////////////////////////////
  /**
    * Delete rule for types
    * canApply(s,t) iff the equation (s = t) can be deleted
    */
  object TyDeleteRule {
    final def canApply(e: UTEq): Boolean = e._1 == e._2
  }

  object TyDecompRule {
    import leo.datastructures.Type.ComposedType
    final def apply(e: UTEq): Seq[UTEq] = {
      val args1 = ComposedType.unapply(e._1).get._2
      val args2 = ComposedType.unapply(e._2).get._2
      args1.zip(args2)
    }

    final def canApply(e: UTEq): Boolean = e match {
      case (ComposedType(head1, arg1), ComposedType(head2, args2)) => head1 == head2 // Heads cannot be flexible,
        // since in TH1 only small types/proper types can be quantified, not type operators
      case _ => false
    }
  }

  object TyFunDecompRule {
    final val CANNOT_APPLY = -1
    final val EQUAL_LENGTH = 0
    final val FIRST_LONGER = 1
    final val SECOND_LONGER = 2

    final def apply(e: UTEq, hint: Int): Seq[UTEq] = {
      assert(hint != CANNOT_APPLY)
      if (hint == EQUAL_LENGTH) {
        e._1.funParamTypesWithResultType.zip(e._2.funParamTypesWithResultType)
      } else {
        val shorterTyList = if (hint == FIRST_LONGER) e._2.funParamTypesWithResultType
        else e._1.funParamTypesWithResultType
        val longerTy = if (hint == FIRST_LONGER) e._1 else e._2
        val splittedLongerTy = longerTy.splitFunParamTypesAt(shorterTyList.size-1)
        (shorterTyList.last, splittedLongerTy._2) +: shorterTyList.init.zip(splittedLongerTy._1)
      }
    }

    final def canApply(e: UTEq): Int = {
      if (!e._1.isFunType || !e._2.isFunType) CANNOT_APPLY
      else {
        val tys1 = e._1.funParamTypesWithResultType
        val tys2 = e._2.funParamTypesWithResultType
        if (tys1.size == tys2.size) EQUAL_LENGTH
        else {
          val tys1Longer = tys1.size > tys2.size
          val shorterTyList = if (tys1Longer) tys2 else tys1
          if (shorterTyList.last.isBoundTypeVar) // Only possible if last one is variable
            if (tys1Longer) FIRST_LONGER
            else SECOND_LONGER
          else CANNOT_APPLY
        }
      }
    }
  }

  /**
    * Bind rule for type equations.
    * canApply(s,t) iff either s or t is a type variable and not a subtype of the other one.
    */
  object TyBindRule {
    final def apply(e: UTEq): Subst = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      val tyVar = if (leftIsTypeVar) BoundType.unapply(e._1).get else BoundType.unapply(e._2).get
      val otherTy = if (leftIsTypeVar) e._2 else e._1

      Subst.singleton(tyVar, otherTy)
    }

    final def canApply(e: UTEq): Boolean = {
      val leftIsTypeVar = e._1.isBoundTypeVar
      val rightIsTypeVar = e._2.isBoundTypeVar

      if (!leftIsTypeVar && !rightIsTypeVar) false
      else {
        val tyVar = if (leftIsTypeVar) BoundType.unapply(e._1).get else BoundType.unapply(e._2).get
        val otherTy = if (leftIsTypeVar) e._2 else e._1
        !otherTy.typeVars.contains(tyVar)
      }
    }
  }

  /**
    * 1
    * returns true if the equation can be deleted
    */
  object DeleteRule {
    final def canApply(e: UEq) = e._1 == e._2
  }

  /**
    * 2
    * returns the list of equations if the head symbols are the same function symbol.
    */
  object DecompRule {
    import leo.datastructures.Term.∙
    final def apply(e: UEq, abstractions: Seq[Type]): (Seq[UEq], Seq[UTEq]) = e match {
      case (_ ∙ sq1, _ ∙ sq2) => zipArgumentsWithAbstractions(sq1, sq2, abstractions)
      case _ => throw new IllegalArgumentException("impossible")
    }
    final def canApply(e: UEq, depth: Depth) = e match {
      case (hd1 ∙ _, hd2 ∙ _) if hd1 == hd2 => !isFlexible(hd1, depth)
      case _ => false
    }
  }

  /**
    * 3
    * BindRule tells if Bind is applicable
    * equation is not oriented
    * return an equation (x,s) substitution is computed from this equation later
    */
  object BindRule {
    final val CANNOT_APPLY = -1
    final val LEFT_IS_VAR = 0
    final val RIGHT_IS_VAR = 1

    import leo.datastructures.Term.Bound
    final def apply(e: UEq, depth: Int, hint: Int): Subst = {
      assert(hint != CANNOT_APPLY)
      val variable = if (hint == LEFT_IS_VAR) Bound.unapply(e._1.headSymbol).get
        else Bound.unapply(e._2.headSymbol).get
      val otherTerm = if (hint == LEFT_IS_VAR) e._2
        else e._1
      // getting flexible head
      Subst.singleton(variable._2 - depth, otherTerm)
    }

    final def canApply(e: UEq, leftBody: Term, leftAbstractions: Seq[Type],
                       rightBody: Term, rightAbstractions: Seq[Type], depth: Int): Int = {
      // orienting the equation
      val leftIsVar = isVariable(leftBody, leftAbstractions)
      if (leftIsVar) {
        val (_, scope) = Bound.unapply(e._1.headSymbol).get
        if (!e._2.looseBounds.contains(scope - depth)) LEFT_IS_VAR else CANNOT_APPLY
      } else {
        val rightIsVar = isVariable(rightBody, rightAbstractions)
        if (rightIsVar) {
          val (_, scope) = Bound.unapply(e._2.headSymbol).get
          if (!e._1.looseBounds.contains(scope - depth)) RIGHT_IS_VAR else CANNOT_APPLY
        }
        else CANNOT_APPLY
      }
    }
  }

  /**
    * 4a
    * equation is not oriented
    * not to forget that the approximations must be in eta-long-form
    */
  object ImitateRule {
    import leo.datastructures.Term.{∙, :::>}

    private final def takePrefixTypeArguments(t: Term): Seq[Type] = {
      t match {
        case _ ∙ args => args.takeWhile(_.isRight).map(_.right.get)
        case _ :::> body  => takePrefixTypeArguments(body)
        case _ => Seq()
      }
    }

    final def apply(vargen: FreshVarGen, e: UEq0): UEq = {
      import leo.datastructures.Term.Bound
      leo.Out.finest(s"Apply Imitate")
      leo.Out.finest(s"on ${e._1.pretty} = ${e._2.pretty}")
      val depth : Int = e._3
      // orienting the equation
      val (t,s) = if (isFlexible(e._1, depth)) (e._1,e._2) else (e._2, e._1)
      val s0 = if (s.headSymbol.ty.isPolyType) {
        leo.Out.finest(s"head symbol is polymorphic")
        Term.mkTypeApp(s.headSymbol, takePrefixTypeArguments(s))}
      else
        s.headSymbol
      leo.Out.finest(s"chose head symbol to be ${s0.pretty}, type: ${s0.ty.pretty}")
      val variable = Bound.unapply(t.headSymbol).get
      val liftedVar = Term.mkBound(variable._1, variable._2 - depth).etaExpand
      val res = (liftedVar, partialBinding(vargen, t.headSymbol.ty,  s0))
      leo.Out.finest(s"Result of Imitate: ${res._1.pretty} = ${res._2.pretty}")
      res
    }

    // must make sure s (rigid-part) doesnt have as head a bound variable
    final def canApply(e: UEq0): Boolean = {
      import leo.datastructures.Term.Bound
      // orienting the equation
      val (t,s) = if (isFlexible(e._1, e._3)) (e._1,e._2) else (e._2, e._1)
      s.headSymbol match {
        // cannot be flexible and fail on bound variable
        case Bound(_,scope) => scope > e._3
        case _ => true
      }
    }
  }

  /**
    * 4b
    * equation is not oriented
    * Always applicable on flex-rigid equations not under application of Bind
    * Alex: I filtered out all of those bound vars that have non-compatible type. Is that correct?
    */
  object ProjectRule {
    final def apply(vargen: FreshVarGen, e: UEq0): Seq[UEq] = {
      import leo.datastructures.Term.Bound

      leo.Out.finest(s"Apply Project")
      val depth = e._3
      // orienting the equation
      val (t,s) = if (isFlexible(e._1,depth)) (e._1,e._2) else (e._2, e._1)
      val bvars = t.headSymbol.ty.funParamTypes.zip(List.range(1,t.headSymbol.ty.arity+1).reverse).map(p => Term.mkBound(p._1,p._2)) // TODO
      leo.Out.finest(s"BVars in Projectrule: ${bvars.map(_.pretty).mkString(",")}")
      //Take only those bound vars that are itself a type with result type == type of general binding
      val funBVars = bvars.filter(bvar => t.headSymbol.ty.funParamTypesWithResultType.endsWith(bvar.ty.funParamTypesWithResultType))
      leo.Out.finest(s"compatible type BVars in Projectrule: ${funBVars.map(_.pretty).mkString(",")}")
      val variable = Bound.unapply(t.headSymbol).get
      val liftedVar = Term.mkBound(variable._1, variable._2 - depth).etaExpand
      val res = funBVars.map(bvar => (liftedVar, partialBinding(vargen, t.headSymbol.ty, bvar)))

      leo.Out.finest(s"Result of Project:\n\t${res.map(eq => eq._1.pretty ++ " = " ++ eq._2.pretty).mkString("\n\t")}")

      res
    }
  }

  /////////////////////////////////////
  // Internal utility functions
  /////////////////////////////////////
  @inline private final def flexflex(e: UEq, depth: Int): Boolean = isFlexible(e._1, depth) && isFlexible(e._2, depth)
  @inline private final def flexrigid(e: UEq, depth: Int): Boolean = (isFlexible(e._1, depth) && !isFlexible(e._2, depth)) || (!isFlexible(e._1, depth) && isFlexible(e._2, depth))
  @inline private final def rigidrigid(e: UEq, depth: Int): Boolean = !isFlexible(e._1, depth) && !isFlexible(e._2, depth)
  @inline private final def isFlexible(t: Term, depth: Int): Boolean = {
    import leo.datastructures.Term.Bound
        t.headSymbol match {
          case Bound(_, scope) => scope > depth
          case _ => false
        }
  }
  /** Checks whether the term is a free variable (eta-expanded). */
  private final def isVariable(body: Term, bound: Seq[Type]): Boolean = {
    import leo.datastructures.Term.{Bound, TermApp}
//    val (body, bound) = collectLambdas(t)
    leo.Out.finest(s"isVariable body: ${body.pretty}")
    leo.Out.finest(s"isVariable bound: ${bound.toString}")
    body match {
      case TermApp(head, args) => head match {
        case Bound(_, scope) if scope > bound.size => if (args.size == bound.size)
          boundVarsMatch(args, bound)
        else false
        case _ => false
      }
      case _ => false
    }
  }
  private final def boundVarsMatch(args: Seq[Term], abstractions: Seq[Type]): Boolean = {
    import leo.datastructures.Term.Bound
    if (args.nonEmpty){
      val curArg = args.head
      val curArgAsBound = Bound.unapply(curArg)
      if (curArgAsBound.isDefined) {
        val (ty, scope) = curArgAsBound.get
        if (ty == abstractions.head && scope == args.length)
          boundVarsMatch(args.tail, abstractions.tail)
        else false
      } else false
    } else {
      assert(abstractions.isEmpty)
      true
    }

  }

//  private final def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[UEq0]): Seq[UEq0] =
//    l.map(e => (e._1.substitute(termSubst,typeSubst),e._2.substitute(termSubst,typeSubst), e._3))
  @inline private final def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[(Term, Term)]): Seq[(Term, Term)] =
    l.map(e => (e._1.substitute(termSubst,typeSubst),e._2.substitute(termSubst,typeSubst)))
  @inline private final def applyTySubstToList(typeSubst: Subst, l: Seq[UEq0]): Seq[UEq0] =
    l.map(e => (e._1.substitute(Subst.id, typeSubst),e._2.substitute(Subst.id, typeSubst), e._3))

//  // computes the substitution from the solved problems
//  protected[calculus] def computeSubst(sproblems: Seq[SEq]): Subst = {
//    import leo.datastructures.Term.Bound
//    import leo.datastructures.{TermFront, BoundFront}
//    // Alex: Added check on empty sproblems list. That is correct, is it?
//    if (sproblems.isEmpty) Subst.id
//    else {
//      val maxIdx: Int = Bound.unapply(sproblems.maxBy(e => Bound.unapply(e._1).get._2)._1).get._2
//      var sub = Subst.shift(maxIdx)
//      for (i <- 1 to maxIdx)
//        sproblems.find(e => Bound.unapply(e._1).get._2 == maxIdx - i + 1) match {
//          case Some((_,t)) => sub = sub.cons(TermFront(t))
//          case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
//        }
//      sub
//    }
//  }
//
//  protected[calculus] def computeTySubst(sTyProblems: Seq[STEq]): Subst = {
//    import leo.datastructures.{TypeFront, BoundFront}
//    if (sTyProblems.isEmpty) Subst.id
//    else {
//      val maxIdx: Int = BoundType.unapply(sTyProblems.maxBy(e => BoundType.unapply(e._1).get)._1).get
//      var sub = Subst.shift(maxIdx)
//      for (i <- 1 to maxIdx)
//        sTyProblems.find(e => BoundType.unapply(e._1).get == maxIdx - i + 1) match {
//          case Some((_,t)) => sub = sub.cons(TypeFront(t))
//          case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
//        }
//      sub
//    }
//  }

  private final def zipArgumentsWithAbstractions(l: Seq[Either[Term, Type]], r: Seq[Either[Term, Type]],
                                                 abstractions: Seq[Type]): (Seq[UEq], Seq[UTEq]) =
    zipArgumentsWithAbstractions0(l,r,abstractions, Seq(), Seq())

  @tailrec @inline
  private final def zipArgumentsWithAbstractions0(l: Seq[Either[Term, Type]], r: Seq[Either[Term, Type]],
                                                  abstractions: Seq[Type],
                                                  acc1: Seq[UEq], acc2: Seq[UTEq]): (Seq[UEq], Seq[UTEq]) = {
    import leo.datastructures.Term.λ
    if (l.isEmpty && r.isEmpty) (acc1, acc2)
    else if (l.nonEmpty && r.nonEmpty) {
      val leftHead = l.head
      val rightHead = r.head
      if (leftHead.isLeft && rightHead.isLeft) {
        val leftTerm = λ(abstractions)(leftHead.left.get)
        val rightTerm = λ(abstractions)(rightHead.left.get)
        zipArgumentsWithAbstractions0(l.tail, r.tail, abstractions, (leftTerm, rightTerm) +: acc1, acc2)
      } else if (leftHead.isRight && rightHead.isRight) {
        val leftType = leftHead.right.get
        val rightType = rightHead.right.get
        zipArgumentsWithAbstractions0(l.tail, r.tail, abstractions, acc1, (leftType, rightType) +: acc2)
      } else throw new IllegalArgumentException("Mixed type/term arguments for equal head symbol. Decomp Failing.")
    } else {
      throw new IllegalArgumentException("Decomp on differently sized arguments length. Decomp Failing.")
    }
  }

  private final def collectLambdas(t: Term): (Term, Seq[Type]) = collectLambdas0(t, Seq())
  @tailrec
  private final def collectLambdas0(t: Term, abstractions: Seq[Type]): (Term, Seq[Type]) = {
    import leo.datastructures.Term.:::>
    t match {
      case ty :::> body => collectLambdas0(body, ty +: abstractions)
      case _ => (t, abstractions.reverse)
    }
  }
}


// Look for TODO, TOFIX (and TOTEST in the corresponding test file)
// TODO: change List into a data structure more sutiable to sorting, etc.
/**
 * created on: 15/04/2015
 * author: Tomer Libal
 */
object HuetsPreUnification extends Unification {

  import Term._
  import leo.datastructures.{TermFront, TypeFront}
  import leo.datastructures.BoundFront
  import leo.datastructures.{SearchConfiguration, NDStream, BFSAlgorithm}
  import annotation.tailrec

  /** A `SEq` is a solved equation. */
  type SEq = UEq

  /** `UTEq` is an unsolved type equation. */
  type UTEq = (Type, Type)
  /** `STEq` is a solved type equation. */
  type STEq = UTEq

  final lazy val MAX_DEPTH = leo.Configuration.UNIFICATION_DEPTH

  override def unify (vargen: FreshVarGen, t1 : Term, s1 : Term) : Iterable[UnificationResult] = {
    val t = t1.etaExpand
    val s = s1.etaExpand

    // returns a stream whose head is a pre-unifier and whose body computes the next unifiers
    new NDStream[UnificationResult](new MyConfiguration(Vector((t,s)), Vector(), 0), new MyFun(vargen)) with BFSAlgorithm
  }

  override def unifyAll(vargen: FreshVarGen, constraints: Seq[UEq]): Iterable[UnificationResult] = {
    val expandedContraints = constraints.map(eq => (eq._1.etaExpand, eq._2.etaExpand)).sortWith(sort)
    new NDStream[UnificationResult](new MyConfiguration(expandedContraints.toVector, Vector(), 0), new MyFun(vargen)) with BFSAlgorithm
  }

  protected def isVariable(t: Term): Boolean = Bound.unapply(t).isDefined

  protected def isFlexible(t: Term): Boolean = t.headSymbol match {
    case Bound(_, _) => true // flexible variable
    case _ => false // function symbol (or bound variable <- really, does that exist? I think every bound variable will be
    // instantiated with a skolem term, or is it? )
  }

  // tuples2 of terms are sorted according to terms and terms are sorted such that
  // rigid terms are before flexible ones
  // keeping the list always ordered like that gives us:
  // 1) all flex-flex are at the end and rigid-rigid are at the front
  // 2) if we always apply exhaustively delete and decomp on inserted equations, we have
  // 3) first equation is flex-flex -> problem is in pre-solved form
  // 4) first equation is rigid-rigid -> symbol clash
  // 5) apply bind or imitate+project
  // t is less than s only if it is not flexible and s is rigid
  private def sort(e1: UEq, e2: UEq) =
    (!isFlexible(e1._1) && !isFlexible(e1._2)) ||
    (isFlexible(e2._1) && isFlexible(e2._2))

  // computes the substitution from the solved problems
  protected[calculus] def computeSubst(sproblems: Seq[SEq]): Subst = {
    // Alex: Added check on empty sproblems list. That is correct, is it?
    if (sproblems.isEmpty) Subst.id
    else {
      val maxIdx: Int = Bound.unapply(sproblems.maxBy(e => Bound.unapply(e._1).get._2)._1).get._2
      var sub = Subst.shift(maxIdx)
      for (i <- 1 to maxIdx)
        sproblems.find(e => Bound.unapply(e._1).get._2 == maxIdx - i + 1) match {
          case Some((_,t)) => sub = sub.cons(TermFront(t))
          case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
        }
      sub
    }
  }

  protected[calculus] def computeTySubst(sTyProblems: Seq[STEq]): Subst = {
    if (sTyProblems.isEmpty) Subst.id
    else {
      val maxIdx: Int = BoundType.unapply(sTyProblems.maxBy(e => BoundType.unapply(e._1).get)._1).get
      var sub = Subst.shift(maxIdx)
      for (i <- 1 to maxIdx)
        sTyProblems.find(e => BoundType.unapply(e._1).get == maxIdx - i + 1) match {
          case Some((_,t)) => sub = sub.cons(TypeFront(t))
          case _ => sub = sub.cons(BoundFront(maxIdx - i + 1))
        }
      sub
    }
  }

  private def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[UEq]): Seq[UEq] =
    l.map(e => (e._1.substitute(termSubst,typeSubst),e._2.substitute(termSubst,typeSubst)))
  private def applySubstToTyList(typeSubst: Subst, l: Seq[UTEq]): Seq[UTEq] =
    l.map(e => (e._1.substitute(typeSubst),e._2.substitute(typeSubst)))

  /** To shorten the type signature: `EqState` just collects all term- and type equations. */
  type EqState = (Seq[UEq], Seq[SEq], Seq[UTEq], Seq[STEq])

  // apply exaustively delete, comp and bind on the set and sort it at the end
  @tailrec
  protected[calculus] def detExhaust(vargen: FreshVarGen,
                                     uproblems: Seq[UEq], sproblems: Seq[SEq],
                                     uTyProblems: Seq[UTEq], sTyProlems: Seq[STEq]): EqState = {
    leo.Out.trace(s"Unsolved (term eqs): ${uproblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    leo.Out.trace(s"Unsolved (type eqs): ${uTyProblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")


    /////// Type operations
    val tind1 = uTyProblems.indexWhere(TyDeleteRule.canApply)
    if (tind1 > -1) {
      leo.Out.finest(s"Apply type delete")
      detExhaust(vargen, uproblems, sproblems, uTyProblems.take(tind1) ++ uTyProblems.drop(tind1 + 1), sTyProlems)
    } else {
      val tind2 = uTyProblems.indexWhere(TyBindRule.canApply)
      if (tind2 > -1) {
        leo.Out.finest(s"Apply type bind")
        val be = TyBindRule(uTyProblems(tind2))
        val sb = computeTySubst(Seq(be))
        leo.Out.finest(s"type bind substitution: ${sb.pretty}")
        detExhaust(vargen, applySubstToList(Subst.id, sb, uproblems), applySubstToList(Subst.id, sb, sproblems),
          applySubstToTyList(sb, uTyProblems), applySubstToTyList(sb, sTyProlems) :+ be)
      } else {

        /////// Term operations
        // apply delete
        val ind1 = uproblems.indexWhere(DeleteRule.canApply)
        if (ind1 > -1) {
          leo.Out.finest("Apply Delete")
          detExhaust(vargen, uproblems.take(ind1) ++ uproblems.drop(ind1 + 1), sproblems, uTyProblems, sTyProlems)
          // apply decomp
        } else {
          val ind2 = uproblems.indexWhere(DecompRule.canApply)
          if (ind2 > -1) {
            leo.Out.finest("Apply Decomp")
            val (uproblemsNew, uTyProblemsNew) = DecompRule(vargen, uproblems(ind2))

            detExhaust(vargen, (uproblemsNew ++ uproblems.take(ind2) ++ uproblems.drop(ind2 + 1)).sortWith(sort),
              sproblems, uTyProblemsNew ++ uTyProblems, sTyProlems)
            // apply bind
          } else {
            val ind3 = uproblems.indexWhere(BindRule.canApply)
            if (ind3 > -1) {
              leo.Out.finest("Apply Bind")
              leo.Out.finest(s"Bind on " +
                s"\n\tLeft: ${uproblems(ind3)._1.pretty}\n\tRight: ${uproblems(ind3)._2.pretty}")
              val be = BindRule(vargen, uproblems(ind3))
              leo.Out.finest(s"Resulting equation: ${be._1.pretty} = ${be._2.pretty}")
              val sb = computeSubst(List(be))
              detExhaust(vargen, applySubstToList(sb, Subst.id, uproblems.take(ind3) ++ uproblems.drop(ind3 + 1)),
                applySubstToList(sb, Subst.id, sproblems) :+ be, uTyProblems, sTyProlems)
            } else {
//              // apply Func /* by Alex */
//              val ind4 = uproblems.indexWhere(FuncRule.canApply)
//              if (ind4 > -1) {
//                leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
//                detExhaust(vargen, (uproblems.take(ind4) :+ FuncRule(vargen, uproblems(ind4))) ++ uproblems.drop(ind4 + 1),
//                  sproblems, uTyProblems, sTyProlems)
//              }
//              else
              {
                // none is applicable, do nothing
                (uproblems, sproblems, uTyProblems, sTyProlems)
              }
            }
          }
        }
      }
    }
  }

  // Huets rules
  trait HuetsRule[R] extends Function2[FreshVarGen,UEq, R] {
    // the functional apply applies the rule to an equation in order to produce other equations
    def canApply(e: UEq): Boolean // returns true if we can apply the rule
  }

  /* FuncRule added by Alex */
  object FuncRule extends HuetsRule[UEq] {

    def apply(varGen: FreshVarGen, e: UEq): UEq = {
      leo.Out.trace(s"Apply Func on ${e._1.pretty} = ${e._2.pretty}")
      val funArgTys = e._1.ty.funParamTypes
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, varGen.existingVars, varGen.existingTyVars))
      (Term.mkTermApp(e._1, skTerms).betaNormalize, Term.mkTermApp(e._2, skTerms).betaNormalize)
    }

    def canApply(e: UEq) = {
      // we can apply it if the sides of the equation have functional type
      assert(((e._1.ty == e._2.ty) || (e._1.tyFV.nonEmpty || e._2.tyFV.nonEmpty)), s"Func Rule: Both UEq sides have not-matching type:\n\t${e._1.pretty}\n\t${e._1.ty.pretty}\n\t${e._2.pretty}\n\t${e._2.ty.pretty}")
      e._1.ty.isFunType
    }
  }
  /* new rules end*/

  // not to forget that the approximations must be in eta-long-form
  /**
   * 4a
   * equation is not oriented
   */
  object ImitateRule extends HuetsRule[UEq] {
    private def takePrefixTypeArguments(t: Term): Seq[Type] = {
      t match {
        case _ ∙ args => args.takeWhile(_.isRight).map(_.right.get)
        case _ => Seq()
      }
    }

    def apply(vargen: FreshVarGen, e: UEq): UEq = {
      leo.Out.trace(s"Apply Imitate")
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      val s0 = if (s.headSymbol.ty.isPolyType)
        Term.mkTypeApp(s.headSymbol, takePrefixTypeArguments(s))
      else
        s.headSymbol
      val res = (t.headSymbol,partialBinding(vargen, t.headSymbol.ty,  s0))
      leo.Out.trace(s"Result of Imitate: ${res._1.pretty} = ${res._2.pretty}")
      res
    }
      // must make sure s doesnt have as head a bound variable
    def canApply(e: UEq) = {
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      s.headSymbol match {
        // cannot be flexible and fail on bound variable
        case Bound(_,_) => assert(false, "ImitateRule: Should not happen, right?");false // FIXME
        case _ => true
      }
    }
  }

  /**
   * 4b
   * equation is not oriented
   * Alex: I filtered out all of those bound vars that have non-compatible type. Is that correct?
   */
  object ProjectRule extends HuetsRule[Seq[UEq]] {
    def apply(vargen: FreshVarGen, e: UEq): Seq[UEq] = {
      leo.Out.trace(s"Apply Project")
      // orienting the equation
      val (t,s) = if (isFlexible(e._1)) (e._1,e._2) else (e._2, e._1)
      val bvars = t.headSymbol.ty.funParamTypes.zip(List.range(1,t.headSymbol.ty.arity+1).reverse).map(p => Term.mkBound(p._1,p._2)) // TODO
      leo.Out.finest(s"BVars in Projectrule: ${bvars.map(_.pretty).mkString(",")}")
      //Take only those bound vars that are itself a type with result type == type of general binding
      val funBVars = bvars.filter(bvar => t.headSymbol.ty.funParamTypesWithResultType.endsWith(bvar.ty.funParamTypesWithResultType))
      leo.Out.finest(s"compatible type BVars in Projectrule: ${funBVars.map(_.pretty).mkString(",")}")
      val res = funBVars.map(e => (t.headSymbol,partialBinding(vargen, t.headSymbol.ty, e)))

      leo.Out.trace(s"Result of Project:\n\t${res.map(eq => eq._1.pretty ++ " = " ++ eq._2.pretty).mkString("\n\t")}")

      res
    }
    def canApply(e: UEq) = ??? // always applicable on flex-rigid equations not under application of Bind
  }

  /**
   * 3
   * BindRule tells if Bind is applicable
   * equation is not oriented
   * return an equation (x,s) substitution is computed from this equation later
   */
  object BindRule extends HuetsRule[UEq] {
    def apply(vargen: FreshVarGen, e: UEq) = {
      // orienting the equation
      // FIXME: See FIXME a few lines below
      val (t,s) = if (isVariable(e._1)) (e._1,e._2) else (e._2, e._1)
      // getting flexible head
      (t.headSymbol,s)
    }
    def canApply(e: UEq) = {
      // orienting the equation
      // FIXME: Bind rule needs to have a variable on the left side. isFlexible did not
      // order that correctly if both sides were flex-head but right one was the only one
      // that was indeed a variable
      val (t,s) = if (isVariable(e._1)) (e._1,e._2) else (e._2, e._1)
//      leo.Out.finest(s"isVariable(e._1): ${isFlexible(e._1)}")
//      leo.Out.finest(s"isVariable(e._2): ${isFlexible(e._2)}")
//      leo.Out.finest(s"Can apply bind:\n\t${t.pretty}\n\t${s.pretty}")
      // check head is flexible
      if (!isFlexible(t)) false
      // getting flexible head
      else {
//        leo.Out.finest("isflexible(t)")
        val (_,x) = Bound.unapply(t.headSymbol).get
//        leo.Out.finest(s"bound index: $x")
//        leo.Out.finest(s"t.headSymbol.etaExpand.equals(t): ${t.headSymbol.etaExpand.equals(t)}")
//        leo.Out.finest(s"t.equals(t.headSymbol): ${t.equals(t.headSymbol)}")
//        leo.Out.finest(s"s.looseBounds.contains(x): ${s.looseBounds.contains(x)}")
      // check t is eta equal to x
        if (!t.headSymbol.etaExpand.equals(t) && !t.equals(t.headSymbol)) false
      // check it doesnt occur in s
        else !s.looseBounds.contains(x)
      }
    }
  }

  object TyBindRule {
    def apply(e: UTEq): STEq  = {
      if (e._1.isBoundTypeVar) (e._1, e._2) else (e._2, e._1)
    }

    def canApply(e: UTEq): Boolean = {
      val (l,r) = if (e._1.isBoundTypeVar) (e._1, e._2) else (e._2, e._1)
      if (!l.isBoundTypeVar) false
      else {
        val x = BoundType.unapply(l).get
        !r.typeVars.contains(x)
      }
    }
  }

  /**
   * 1
   * returns true if the equation can be deleted
   */
  object DeleteRule extends HuetsRule[Unit] {
    def apply(vargen: FreshVarGen, e: UEq) = ()
    def canApply(e: UEq) = {
      val (t,s) = e
      t.equals(s)
    }
  }

  /**
    * delete rule for types
    * returns true if the equation can be deleted
    */
  object TyDeleteRule {
    def canApply(e: UTEq) = {
      val (t,s) = e
      t.equals(s)
    }
  }

  /**
   * 2
   * returns the list of equations if the head symbols are the same function symbol.
   */
  object DecompRule extends HuetsRule[(Seq[UEq], Seq[UTEq])] {
    def apply(vargen: FreshVarGen, e: UEq): (Seq[UEq], Seq[UTEq]) = e match {
      case (_ ∙ sq1, _ ∙ sq2) => zipArguments(sq1, sq2)
      case _ => throw new IllegalArgumentException("impossible")
    }
    def canApply(e: UEq) = e match {
      case (hd1 ∙ args1, hd2 ∙ args2) if hd1 == hd2 => !isFlexible(hd1)
      case _ => false
    }
  }

  private final def zipArguments(l: Seq[Either[Term, Type]], r: Seq[Either[Term, Type]]): (Seq[UEq], Seq[UTEq]) = {
    (l,r) match {
      case (Seq(), Seq()) => (Seq(), Seq())
      case (Left(t1) +: rest1, Left(t2) +: rest2) => val rec = zipArguments(rest1, rest2)
        ((t1,t2) +: rec._1, rec._2)
      case (Right(ty1) +: rest1, Right(ty2) +: rest2) => val rec = zipArguments(rest1, rest2)
        (rec._1, (ty1, ty2) +: rec._2)
      case _ => throw new IllegalArgumentException("Mixed type/term arguments for equal head symbol. Decomp Failing.")
    }
  }

  private final val dontcaredepth = -1
  // the state of the search space
  protected case class MyConfiguration(uproblems: Seq[UEq], sproblems: Seq[SEq],
                                       uTyProblems: Seq[UTEq], sTyProblems: Seq[STEq],
                                       result: Option[UnificationResult], isTerminal: Boolean,
                                       searchDepth: Int)
    extends SearchConfiguration[UnificationResult] {
    def this(result: Option[UnificationResult]) = this(Seq(), Seq(), Seq(), Seq(), result, true, dontcaredepth) // for success
    def this(l: Seq[UEq], s: Seq[UEq], searchDepth: Int) = this(l, s, Seq(), Seq(), None, searchDepth < MAX_DEPTH, searchDepth) // for in node
    override def toString  = "{" + uproblems.flatMap(x => ("<"+x._1.pretty+", "+ x._2.pretty+">")) + "}"
  }

  // the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch)
  protected class MyFun(vargen: FreshVarGen) extends Function1[SearchConfiguration[UnificationResult], Seq[SearchConfiguration[UnificationResult]]] {

    import  scala.collection.mutable.ListBuffer

    // Huets procedure is defined here
    override def apply(conf2: SearchConfiguration[UnificationResult]): Seq[SearchConfiguration[UnificationResult]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (uproblems, sproblems, uTyProblems, sTyProblems) = detExhaust(vargen, conf.uproblems,conf.sproblems, conf.uTyProblems, conf.sTyProblems)
      leo.Out.trace(s"Finished detExhaust")
      // if uproblems is empty, then succeeds
      if (uproblems.isEmpty) {
        val termSubst = computeSubst(sproblems)
        val typeSubst = computeTySubst(sTyProblems)
        leo.Out.debug(s"Unification finished, with")
        leo.Out.debug(s"Term substitution ${termSubst.pretty}")
        leo.Out.debug(s"Type substitution ${typeSubst.pretty}")
        Seq(new MyConfiguration(Some(Tuple2((termSubst, typeSubst),Seq()))))
      }
      // else consider top equation
      else {
        val (t,s) = uproblems.head
        leo.Out.finest(s"selected: ${t.pretty} = ${s.pretty}")
        // if it is rigid-rigid -> fail
        if (!isFlexible(t) && !isFlexible(s)) {
          leo.Out.debug("Unification failed. "); Seq()}
        else {
          // Changed: Do not compute default sub, but rather return substitution from
          // solved equations and return list of unsolved ones directly.
          // if it is flex-flex -> all equations are flex-flex
          if (isFlexible(t) && isFlexible(s)) {
            leo.Out.finest(s"Unification finished with Flex-flex")
            Seq(new MyConfiguration(Some(((computeSubst(sproblems), computeTySubst(sTyProblems)),uproblems))))
          } else {
            leo.Out.finest(s"flex-rigid at depth ${conf.searchDepth}")
            // else we have a flex-rigid and we cannot apply bind

            val lb = new ListBuffer[MyConfiguration]
            // compute the imitate partial binding and add the new configuration
            if (ImitateRule.canApply(t,s)) lb.append(new MyConfiguration(ImitateRule(vargen, (t,s))+:uproblems, sproblems, conf.searchDepth+1))

            // compute all the project partial bindings and add them to the return list
            ProjectRule(vargen, (t,s)).foreach (e => lb.append(new MyConfiguration(e+:uproblems, sproblems, conf.searchDepth+1)))

            lb.toList
          }
        }
      }
    }
  }
}

