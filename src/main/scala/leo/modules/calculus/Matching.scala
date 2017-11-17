package leo.modules.calculus

import leo.datastructures.Type.BoundType

import scala.annotation.tailrec
import leo.datastructures.{BFSAlgorithm, NDStream, SearchConfiguration, Subst, Term, Type}
import leo.modules.myAssert

trait Matching {
  type UEq = (Term, Term); type UTEq = (Type, Type)
  type TermSubst = Subst; type TypeSubst = Subst
  type Result = (TermSubst, TypeSubst)

  /** Returns an iterable of substitutions (σ_i) such that sσ_i = t and there exists no such ϱ
    * which is more general than σ_i. */
  def matchTerms(vargen: FreshVarGen, s: Term, t: Term, forbiddenVars: Set[Int] = null): Iterable[Result]
  def matchTermList(vargen: FreshVarGen, ueqs: Seq[(Term, Term)], forbiddenVars: Set[Int] = null): Iterable[Result]
}
object Matching {
  val impl: Matching = HOPatternMatching
  def apply(vargen: FreshVarGen, s: Term, t: Term, forbiddenVars: Set[Int] = null): Iterable[Matching#Result] =
    impl.matchTerms(vargen, s, t, forbiddenVars)
  def applyList(vargen: FreshVarGen, ueqs: Seq[(Term, Term)], forbiddenVars: Set[Int] = null): Iterable[Matching#Result] =
    impl.matchTermList(vargen, ueqs, forbiddenVars)
}


@deprecated("HOMatching (i.e. matching based on full HO (pre-) unification) " +
  "is broken at the moment (gives false positives).", "Leo-III 1.2")
object HOMatching extends Matching {
  /** The Depth is the number of lambda abstractions under which a term is nested.*/
  type Depth = Int


  /** `UEq0` extends UEq with an depth indicator. */
  type UEq0 = (Term, Term, Depth)
  /** A `SEq` is a solved equation. */
  type SEq = (Term, Term)

  /** `STEq` is a solved type equation. */
  type STEq = UTEq

  /** Maximal unification search depth (i.e. number of flex-rigid rules on search path). */
  final lazy val MAX_DEPTH = leo.Configuration.MATCHING_DEPTH

  /////////////////////////////////////
  // the state of the search space
  /////////////////////////////////////
  protected[calculus] case class MyConfiguration(unprocessed: Seq[UEq],
                                                 flexRigid: Seq[UEq0],
                                                 solved: TermSubst, solvedTy: TypeSubst,
                                                 result: Option[Result], searchDepth: Int)
    extends SearchConfiguration[Result] {
    def this(result: Result) = this(null, null, null, null, Some(result), Int.MaxValue) // for success
    def this(unprocessed: Seq[UEq],
             flexRigid: Seq[UEq0],
             solved: TermSubst, solvedTy: TypeSubst,
             searchDepth: Int) = this(unprocessed, flexRigid, solved, solvedTy, None, searchDepth) // for in node

    override final def isTerminal: Boolean = searchDepth >= MAX_DEPTH
    override def toString  = s"{${unprocessed.map(x => s"<${x._1},${x._2}>").mkString}}"
  }


  def matchTerms(vargen: FreshVarGen, s: Term, t: Term, forbiddenVars: Set[Int] = null): Iterable[Result] =
    matchTermList(vargen, Seq((s,t)), forbiddenVars)
  def matchTermList(vargen: FreshVarGen, ueqs: Seq[(Term, Term)], forbiddenVars: Set[Int] = null): Iterable[Result] = {
    if (ueqs.exists{case (s,t) => s.ty != t.ty}) throw new NotImplementedError()
    else {
      val ueqs0 = ueqs.map {case (s,t) => (s.etaExpand, t.etaExpand)}.toVector
      new NDStream[Result](new MyConfiguration(ueqs0, Vector.empty, Subst.id, Subst.id, 0), new EnumUnifier(vargen, Subst.id)) with BFSAlgorithm
    }
  }

  /////////////////////////////////////
  // Internal search functions
  /////////////////////////////////////
  /** the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch) */
  protected[calculus] class EnumUnifier(vargen: FreshVarGen, initialTypeSubst: TypeSubst) extends Function1[SearchConfiguration[Result], Seq[SearchConfiguration[Result]]] {

    // Huets procedure is defined here
    override def apply(conf2: SearchConfiguration[Result]): Seq[SearchConfiguration[Result]] = {
      val conf = conf2.asInstanceOf[MyConfiguration]
      // we always assume conf.uproblems is sorted and that delete, decomp and bind were applied exaustively
      val (fail, flexRigid, partialUnifier, partialTyUnifier) = detExhaust(conf.unprocessed,
        conf.flexRigid,
        conf.solved, Seq(), conf.solvedTy)
      leo.Out.finest(s"Finished detExhaust")
      // if uTyProblems is non-empty fail
      if (fail) {
        leo.Out.debug(s"Matching failed.")
        Seq()
      } else {
        // if there is no unsolved equation, then succeed
        if (flexRigid.isEmpty) {
          leo.Out.debug(s"Matching finished")
          leo.Out.debug(s"\tTerm substitution ${partialUnifier.normalize.pretty}")
          leo.Out.debug(s"\tType substitution ${partialTyUnifier.normalize.pretty}")
          Seq(new MyConfiguration((partialUnifier.normalize, initialTypeSubst.comp(partialTyUnifier).normalize)))
        }
        // else do flex-rigid cases
        else {
          assert(flexRigid.nonEmpty)
          leo.Out.finest(s"flex-rigid at depth ${conf.searchDepth}")
          val head = flexRigid.head
          import  scala.collection.mutable.ListBuffer
          val lb = new ListBuffer[MyConfiguration]
          // compute the imitate partial binding and add the new configuration
          if (ImitateRule.canApply(head)) lb.append(new MyConfiguration(Seq(ImitateRule(vargen, head)), flexRigid,
            partialUnifier, partialTyUnifier, conf.searchDepth+1))

          // compute all the project partial bindings and add them to the return list
          ProjectRule(vargen, head).foreach (e => lb.append(new MyConfiguration(Seq(e), flexRigid,
            partialUnifier, partialTyUnifier, conf.searchDepth+1)))

          lb.toList
        }
      }
    }
  }

  @tailrec
  final protected[calculus] def tyDetExhaust(uTyProblems: Seq[UTEq], unifier: TypeSubst): Option[TypeSubst] = {
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
        } else {
          val tyBindRuleCanApplyHint = TyBindRule.canApply(head)
          if (tyBindRuleCanApplyHint != CANNOT_APPLY)
            tyDetExhaust(uTyProblems.tail, unifier.comp(TyBindRule.apply(head, tyBindRuleCanApplyHint)))
          else None
        }
      }
    } else Some(unifier)
  }

  /** Exhaustively apply delete, comp and bind on the set  of unprocessed equations. */
  @tailrec
  final protected[calculus] def detExhaust(unprocessed: Seq[UEq],
                                           flexRigid: Seq[UEq0],
                                           solved: TermSubst,
                                           uTyProblems: Seq[UTEq], solvedTy: TypeSubst):
  (Boolean, Seq[UEq0], TermSubst, TypeSubst) = {
    //                  (fail, flexRigid, flexflex, solved, solvedTy)
    leo.Out.finest(s"Unsolved (term eqs): ${unprocessed.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    leo.Out.finest(s"Unsolved (type eqs): ${uTyProblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    if (uTyProblems.nonEmpty) {
      val head = uTyProblems.head

      // Try all type rules
      if (TyDeleteRule.canApply(head))
        detExhaust(unprocessed, flexRigid, solved, uTyProblems.tail, solvedTy)
      else if (TyDecompRule.canApply(head))
        detExhaust(unprocessed, flexRigid, solved, TyDecompRule.apply(head) ++ uTyProblems.tail, solvedTy)
      else {
        val tyFunDecompRuleCanApplyHint = TyFunDecompRule.canApply(head)
        if (tyFunDecompRuleCanApplyHint != TyFunDecompRule.CANNOT_APPLY) {
          detExhaust(unprocessed, flexRigid, solved,
            TyFunDecompRule.apply(head, tyFunDecompRuleCanApplyHint) ++ uTyProblems.tail, solvedTy)
        } else {
          val tyBindRuleCanApplyHint = TyBindRule.canApply(head)
          if (tyBindRuleCanApplyHint != CANNOT_APPLY) {
            val subst = TyBindRule.apply(head, tyBindRuleCanApplyHint)
            leo.Out.finest(s"Ty Bind: ${subst.pretty}")
            detExhaust(applySubstToList(Subst.id, subst, unprocessed),
              applyTySubstToList(subst, flexRigid),
              solved.applyTypeSubst(subst), uTyProblems.tail, solvedTy.comp(subst))
          } else // No type rule applicable for head, so it's a fail, just return a fail state
            (true, flexRigid, solved, solvedTy)
        }
      }
    } else {
      // check unprocessed
      if (unprocessed.nonEmpty) {
        val head0 = unprocessed.head
        leo.Out.finest(s"detExhaust on: ${head0._1.pretty} = ${head0._2.pretty}")
        // Try all term rules
        if (DeleteRule.canApply(head0)) {
          leo.Out.finest("Apply delete")
          detExhaust(unprocessed.tail, flexRigid, solved, uTyProblems, solvedTy)
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
            detExhaust(newUnsolvedTermEqs ++ unprocessed.tail, flexRigid,
              solved, newUnsolvedTypeEqs ++ uTyProblems, solvedTy)
          } else {
            val bindHint = BindRule.canApply(leftBody, rightBody, abstractionCount)
            if (bindHint != CANNOT_APPLY) {
              val subst = BindRule.apply(head0, bindHint)
              leo.Out.finest(s"Bind: ${subst.pretty}")
              detExhaust(
                applySubstToList(subst, Subst.id, flexRigid.map(e => (e._1, e._2)) ++ unprocessed.tail),
                Seq(),
                solved.comp(subst), uTyProblems, solvedTy)
            } else {
              // ... move to according list if nothing applies
              if (!isFlexible(head0._1, abstractionCount)) {
                // if head is not flexible, fail
                (true, flexRigid, solved, solvedTy)
              } else {
                detExhaust(unprocessed.tail, (left, right, abstractionCount) +: flexRigid,
                  solved, uTyProblems, solvedTy)
              }
            }
          }
        }
      } else {
        // no unprocessed left, return sets
        (false, flexRigid, solved, solvedTy)
      }
    }
  }


  /////////////////////////////////////
  // Huets rules
  /////////////////////////////////////
  final val CANNOT_APPLY = -1
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
      case (ComposedType(head1, _), ComposedType(head2, _)) => head1 == head2 // Heads cannot be flexible,
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
    import leo.datastructures.Type.BoundType
    final def apply(e: UTEq, hint: Int): Subst = {
      Subst.singleton(hint, e._2)
    }

    final def canApply(e: UTEq): Int = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      if (!leftIsTypeVar) CANNOT_APPLY
      else {
        val tyVar = BoundType.unapply(e._1).get
        val otherTy = e._2
        if (!otherTy.typeVars.contains(tyVar)) tyVar else CANNOT_APPLY
      }
    }
  }

  /**
    * 1
    * returns true if the equation can be deleted
    */
  object DeleteRule {
    final def canApply(e: UEq): Boolean = e._1 == e._2
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
    final def canApply(e: UEq, depth: Depth): Boolean = e match {
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
    type Side  = Int

    final def apply(e: UEq, variable: Int): Subst = {
      assert(variable != CANNOT_APPLY)
      Subst.singleton(variable, e._2)
    }

    final def canApply(leftBody: Term, rightBody: Term, depth: Int): Int = {
      import leo.datastructures.getVariableModuloEta
      // is applicable if left side is a variable
      val possiblyLeftVar = getVariableModuloEta(leftBody, depth)
      if (possiblyLeftVar > 0) {
        // do occurs check on right
        if (rightBody.looseBounds.contains(possiblyLeftVar + depth)) CANNOT_APPLY else possiblyLeftVar
      } else CANNOT_APPLY
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
      val (t,s) = (e._1,e._2)
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
      val s = e._2
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
      val t = e._1
      // FIXME: what to fix?
      val bvars = t.headSymbol.ty.funParamTypes.zip(List.range(1,t.headSymbol.ty.arity+1).reverse).map(p => Term.mkBound(p._1,p._2))
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
  @inline protected[calculus] final def flexflex(e: UEq, depth: Int): Boolean = isFlexible(e._1, depth) && isFlexible(e._2, depth)
  @inline protected[calculus] final def flexrigid(e: UEq, depth: Int): Boolean = (isFlexible(e._1, depth) && !isFlexible(e._2, depth)) || (!isFlexible(e._1, depth) && isFlexible(e._2, depth))
  @inline protected[calculus] final def rigidrigid(e: UEq, depth: Int): Boolean = !isFlexible(e._1, depth) && !isFlexible(e._2, depth)
  @inline protected[calculus] final def isFlexible(t: Term, depth: Int): Boolean = {
    import leo.datastructures.Term.Bound
    t.headSymbol match {
      case Bound(_, scope) => scope > depth
      case _ => false
    }
  }

  //  private final def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[UEq0]): Seq[UEq0] =
  //    l.map(e => (e._1.substitute(termSubst,typeSubst),e._2.substitute(termSubst,typeSubst), e._3))
  @inline protected[calculus] final def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[(Term, Term)]): Seq[(Term, Term)] =
  l.map(e => (e._1.substitute(termSubst,typeSubst),e._2.substitute(termSubst,typeSubst)))
  @inline private final def applyTySubstToList(typeSubst: Subst, l: Seq[UEq0]): Seq[UEq0] =
    l.map(e => (e._1.substitute(Subst.id, typeSubst),e._2.substitute(Subst.id, typeSubst), e._3))

  protected[calculus] final def zipArgumentsWithAbstractions(l: Seq[Either[Term, Type]], r: Seq[Either[Term, Type]],
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
        zipArgumentsWithAbstractions0(l.tail, r.tail, abstractions, (leftTerm.etaExpand, rightTerm.etaExpand) +: acc1, acc2)
      } else if (leftHead.isRight && rightHead.isRight) {
        val leftType = leftHead.right.get
        val rightType = rightHead.right.get
        zipArgumentsWithAbstractions0(l.tail, r.tail, abstractions, acc1, (leftType, rightType) +: acc2)
      } else throw new IllegalArgumentException("Mixed type/term arguments for equal head symbol. Decomp Failing.")
    } else {
      throw new IllegalArgumentException("Decomp on differently sized arguments length. Decomp Failing.")
    }
  }

  protected[calculus] final def collectLambdas(t: Term): (Term, Seq[Type]) = collectLambdas0(t, Seq())
  @tailrec
  private final def collectLambdas0(t: Term, abstractions: Seq[Type]): (Term, Seq[Type]) = {
    import leo.datastructures.Term.:::>
    t match {
      case ty :::> body => collectLambdas0(body, ty +: abstractions)
      case _ => (t, abstractions.reverse)
    }
  }
}


object HOPatternMatching extends Matching {
  /** Returns an iterable of substitutions (σ_i) such that tσ_i = s and there exists no such ϱ
    * which is more general than σ_i. */
  override def matchTerms(vargen: FreshVarGen, t: Term, s: Term, forbiddenVars: Set[Int] = null): Iterable[Result] = {
    matchTermList(vargen, Vector((t,s)), forbiddenVars)
  }

  def matchTermList(vargen: FreshVarGen, ueqs: Seq[(Term, Term)], forbiddenVars: Set[Int] = null): Iterable[Result] = {
    val initialTypeSubst = TypeMatching(ueqs.map(e => (e._1.ty, e._2.ty)))
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val ueqs0 = ueqs.map(eq => (eq._1.substitute(Subst.id, initialTypeSubst0).etaExpand, eq._2.substitute(Subst.id, initialTypeSubst0).etaExpand))
      val forbiddenVars0 = if (forbiddenVars == null) ueqs.flatMap(_._2.looseBounds).toSet else forbiddenVars
      leo.Out.finest(s"Forbidden vars: ${forbiddenVars0.toString()}")
      val matchResult = match0(ueqs0, initialTypeSubst0, vargen, forbiddenVars0)
      if (matchResult.isDefined) {
        leo.Out.finest(s"Matching succeeded!")
        Seq(matchResult.get)
      } else {
        leo.Out.finest(s"Matching failed!")
        Iterable.empty
      }
    }
  }

  /** Wrap up the matching result with the initial type substitution and return as Option. */
  private final def match0(ueqs: Seq[UEq], initialTypeSubst: TypeSubst, vargen: FreshVarGen, forbiddenVars: Set[Int]): Option[Result] = {
    leo.Out.finest(s"match0: ${ueqs.map{case (l,r) => l.pretty ++ " = " ++ r.pretty}.mkString("\n")}")
    val matcher = match1(ueqs, vargen, Subst.id, Subst.id, forbiddenVars)
    if (matcher.isDefined)
      Some((matcher.get._1.normalize, initialTypeSubst.comp(matcher.get._2).normalize))
    else
      None
  }

  type PartialResult = Result
  /** Main matching method: Solve head equations subsequently by applying the according rules. */
  @tailrec
  private final def match1(ueqs: Seq[UEq], vargen: FreshVarGen, partialMatcher: TermSubst, partialTyMatcher: TypeSubst,
                           forbiddenVars: Set[Int]): Option[PartialResult] = {
    import leo.datastructures.Term.{Bound, ∙}
    import leo.datastructures.{partitionArgs, collectLambdas}
    import HuetsPreUnification.{applySubstToList, zipWithAbstractions}

    if (ueqs.isEmpty)
      Some((partialMatcher, partialTyMatcher))
    else {
      val (l0,r0) = ueqs.head
      if (l0 == r0) match1(ueqs.tail, vargen, partialMatcher, partialTyMatcher, forbiddenVars)
      else {
        val l = l0.substitute(partialMatcher, partialTyMatcher).etaExpand
        val r = r0
        leo.Out.finest(s"solve: ${l.pretty} = ${r.pretty}")
        myAssert(Term.wellTyped(l))
        myAssert(Term.wellTyped(r))
        // take off the lambdas
        val (leftBody, leftAbstractions) = collectLambdas(l)
        val (rightBody, rightAbstractions) = collectLambdas(r)
        assert(leftAbstractions == rightAbstractions)
        val abstractionCount = leftAbstractions.size

        (leftBody, rightBody) match {
          case (hd1 ∙ args1, hd2 ∙ args2) => (hd1, hd2) match {
//            case (Bound(ty1, idx1), Bound(ty2, idx2))
//              if idx1 > abstractionCount && idx2 > abstractionCount =>
//              /* flex-flex */
//              leo.Out.finest("Apply Flex-flex")
//              assert(leftBody.ty == rightBody.ty)
//              val partialUniResult = flexflex(idx1-abstractionCount, ty1, args1, idx2-abstractionCount, ty2, args2, vargen, leftBody.ty)
//              match1(ueqs.tail, vargen, partialMatcher.comp(partialUniResult._1), partialTyMatcher.comp(partialUniResult._2))
            case (Bound(ty1, idx1), _) if idx1 > abstractionCount && !forbiddenVars.contains(idx1) =>
              /* flex-rigid or flex-flex */
              if (r.looseBounds.contains(idx1 - abstractionCount)) None
              else {
                leo.Out.finest("Apply Flex-rigid")
                val result = flexrigid(idx1 - abstractionCount, ty1, args1, hd2, args2, rightBody, vargen, leftAbstractions)
                if (result == null) None
                else {
                  val partialMatchingResult = result._1
                  val newUeqs = result._2
                  leo.Out.finest(s"flex-rigid result matcher: ${partialMatchingResult._1.pretty}")
                  leo.Out.finest(s"flex-rigid result new unsolved: ${newUeqs.map{case (l,r) => l.pretty ++ " = " ++ r.pretty}.mkString("\n")}")
                  match1(newUeqs ++ ueqs.tail, vargen, partialMatcher.comp(partialMatchingResult._1),
                    partialTyMatcher.comp(partialMatchingResult._2), forbiddenVars)
                }
              }
            case (_, Bound(_, idx2)) if idx2 > abstractionCount=>
              /* rigid-flex */
              None // right side is considered rigid in this matching setting
            case _ => /* rigid-rigid */
              if (hd1 == hd2) {
                leo.Out.finest("Apply rigid-rigid")
                if (hd1.ty.isPolyType) {
                  leo.Out.finest(s"Poly rigid-rigid")
                  val (tyArgs1, termArgs1) = partitionArgs(args1)
                  val (tyArgs2, termArgs2) = partitionArgs(args2)
                  assert(tyArgs1.size == tyArgs2.size)
                  val tyMatchingConstraints = tyArgs1.zip(tyArgs2)
                  leo.Out.finest(s"ty constraints: ${tyMatchingConstraints.map(c => c._1.pretty + " = " + c._2.pretty).mkString(",")}")
                  val tyMatchingResult = TypeMatching(tyMatchingConstraints)
                  if (tyMatchingResult.isDefined) {
                    val tySubst = tyMatchingResult.get
                    leo.Out.finest(s"Poly rigid-rigid match succeeded: ${tySubst.pretty}")
                    val newUeqs = zipWithAbstractions(termArgs1, termArgs2, leftAbstractions.map(_.substitute(tySubst)))
                    leo.Out.finest(s"New unsolved:\n\t${newUeqs.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
                    match1(applySubstToList(Subst.id, tySubst, newUeqs ++ ueqs.tail), vargen,
                      partialMatcher.applyTypeSubst(tySubst), partialTyMatcher.comp(tySubst), forbiddenVars)
                  } else {
                    leo.Out.finest(s"Poly rigid-rigid uni failed")
                    None
                  }
                } else {
                  val termArgs1 = args1.map(_.left.get)
                  val termArgs2 = args2.map(_.left.get)
                  val newUeqs = zipWithAbstractions(termArgs1, termArgs2, leftAbstractions)
                  leo.Out.finest(s"New unsolved:\n\t${newUeqs.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
                  match1(newUeqs ++ ueqs.tail, vargen, partialMatcher, partialTyMatcher, forbiddenVars)
                }
              } else None

          }
          case _ => assert(false); None
        }
      }
    }
  }


  /** Flex-rigid rule: May fail, returns null if not sucessful. */
  private final def flexrigid(idx1: Int, ty1: Type, args1: Seq[Either[Term, Type]], rigidHd: Term, rigidArgs: Seq[Either[Term, Type]], rigidAsTerm: Term, vargen: FreshVarGen, depth: Seq[Type]): (PartialResult, Seq[UEq]) = {
    import leo.datastructures.partitionArgs
    import leo.datastructures.Term.Bound
    try {
      val args10 = args1.map(_.left.get)
      // This is a bit hacky: We need the new fresh variables
      // introduced by partialBinding(...), so we just take the
      // difference of vars in vargen (those have been introduced).
      // Maybe this should be done better...
      val varsBefore = vargen.existingVars

      if (rigidHd.isVariable && Bound.unapply(rigidHd).get._2 <= depth.size) {
        if (!args10.contains(rigidHd.etaExpand)) return null /*fail*/
        // variables cannot be polymorphic, calculating projection binding.
        // newrigidHd: position of bound rigid hd in flex-args-list
        val newrigidHd = Term.local.mkBound(rigidHd.ty, args10.size - args10.indexOf(rigidHd.etaExpand))
        val binding = partialBinding(vargen, ty1, newrigidHd)
        leo.Out.finest(s"binding: $idx1 -> ${binding.pretty}")
        val varsAfter = vargen.existingVars
        val subst = Subst.singleton(idx1, binding)
        // new equations:
        val newVars = newVarsFromGenerator(varsBefore, varsAfter).reverse // reverse since highest should be the last
        assert(newVars.size == rigidArgs.size)
        val newueqs = newUEqs(newVars, args10, rigidArgs.map(_.left.get), depth)
        ((subst, Subst.id), newueqs)
      } else {
        assert(rigidHd.isConstant || (rigidHd.isVariable && Bound.unapply(rigidHd).get._2 > depth.size))
        // Constants may be polymorphic: Apply types before calculating imitation binding.
        val rigidArgs0 = partitionArgs(rigidArgs)
        assert(rigidArgs0._1.isEmpty || rigidHd.ty.isPolyType)
        val rigidHd0 = if (rigidHd.ty.isPolyType) {
          leo.Out.finest(s"head symbol is polymorphic")
          Term.local.mkTypeApp(rigidHd, rigidArgs0._1)}
        else
          rigidHd
        val binding = partialBinding(vargen, ty1, rigidHd0.lift(ty1.funParamTypes.size - depth.size))
        leo.Out.finest(s"binding: $idx1 -> ${binding.pretty}")
        val varsAfter = vargen.existingVars
        val subst = Subst.singleton(idx1, binding)
        // new equations:
        val newVars = newVarsFromGenerator(varsBefore, varsAfter).reverse // reverse since highest should be the last
        assert(newVars.size == rigidArgs0._2.size) // FIXME
        val newueqs = newUEqs(newVars, args10, rigidArgs0._2, depth)
        ((subst, Subst.id), newueqs)
      }
    } catch {
      case _:NoSuchElementException => null
    }
  }


  private final def newVarsFromGenerator(oldVars: Seq[(Int, Type)], newVars: Seq[(Int, Type)]): Seq[(Int, Type)] = {
    newVars.takeWhile(elem => !oldVars.contains(elem))
  }
  private final def newUEqs(freeVars: Seq[(Int, Type)], boundVarArgs: Seq[Term], otherTermList: Seq[Term], depth: Seq[Type]): Seq[UEq] = {
    import leo.datastructures.Term.local.{mkTermApp, mkBound, λ}
    if (freeVars.isEmpty) Nil
    else {
      val hd = freeVars.head
      (λ(depth)(mkTermApp(mkBound(hd._2, hd._1+depth.size), boundVarArgs)).etaExpand, λ(depth)(otherTermList.head).etaExpand) +: newUEqs(freeVars.tail, boundVarArgs, otherTermList.tail, depth)
    }
  }
}

trait TypeMatching {
  type UEq = (Type, Type) // Left one is the only one that can be bound, right is considered rigid.
  type TypeSubst = Subst

  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  def matching(s: Type, t: Type): Option[TypeSubst]
  /** Returns a substitution `Some(σ)` such that s_iσ = t_i. Returns `None` if no such `σ` exists.  */
  def matching(uEqs: Seq[UEq]): Option[TypeSubst]
}
object TypeMatching {
  private val impl: TypeMatching = TypeMatchingImpl

  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  def apply(s: Type, t: Type): Option[TypeMatching#TypeSubst] = impl.matching(s,t)
  /** Returns a substitution `Some(σ)` such that s_iσ = t_i. Returns `None` if no such `σ` exists.  */
  def apply(uEqs: Seq[TypeMatching#UEq]): Option[TypeSubst] = impl.matching(uEqs)
}

object TypeMatchingImpl extends TypeMatching {
  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  override def matching(s: Type, t: Type): Option[TypeSubst] = matching(Vector((s,t)))
  /** Returns a substitution `Some(σ)` such that s_iσ = t_i. Returns `None` if no such `σ` exists.  */
  def matching(uEqs: Seq[UEq]): Option[TypeSubst] = {
    val forbiddenTyVars = uEqs.flatMap(_._2.typeVars.map(BoundType.unapply(_).get)).toSet
    tyDetExhaust(uEqs.toVector, Subst.id, forbiddenTyVars)
  }

  @tailrec
  final protected[calculus] def tyDetExhaust(uTyProblems: Seq[UEq], unifier: TypeSubst, forbiddenVars: Set[Int]): Option[TypeSubst] = {
    leo.Out.finest(s"tyDetExaust unsolved: ${uTyProblems.map(ueq => ueq._1.pretty ++ " = " ++ ueq._2.pretty).mkString("\n")}")
    if (uTyProblems.nonEmpty) {
      val head0 = uTyProblems.head
      val head = (head0._1.substitute(unifier), head0._2.substitute(unifier))

      if (TyDeleteRule.canApply(head))
        tyDetExhaust(uTyProblems.tail, unifier, forbiddenVars)
      else if (TyDecompRule.canApply(head))
        tyDetExhaust(TyDecompRule.apply(head) ++ uTyProblems.tail, unifier, forbiddenVars)
      else {
        val tyFunDecompRuleCanApplyHint = TyFunDecompRule.canApply(head)
        if (tyFunDecompRuleCanApplyHint != TyFunDecompRule.CANNOT_APPLY) {
          tyDetExhaust(TyFunDecompRule.apply(head, tyFunDecompRuleCanApplyHint) ++ uTyProblems.tail,unifier, forbiddenVars)
        } else if (TyBindRule.canApply(head, forbiddenVars))
          tyDetExhaust(uTyProblems.tail, unifier.comp(TyBindRule.apply(head)), forbiddenVars)
        else
          None
      }
    } else Some(unifier)
  }


  /**
    * Delete rule for types
    * canApply(s,t) iff the equation (s = t) can be deleted
    */
  object TyDeleteRule {
    final def canApply(e: UEq): Boolean = e._1 == e._2
  }

  object TyDecompRule {
    import leo.datastructures.Type.ComposedType
    final def apply(e: UEq): Seq[UEq] = {
      val args1 = ComposedType.unapply(e._1).get._2
      val args2 = ComposedType.unapply(e._2).get._2
      args1.zip(args2)
    }

    final def canApply(e: UEq): Boolean = e match {
      case (ComposedType(head1, _), ComposedType(head2, _)) => head1 == head2 // Heads cannot be flexible,
      // since in TH1 only small types/proper types can be quantified, not type operators
      case _ => false
    }
  }

  object TyFunDecompRule {
    final val CANNOT_APPLY = -1
    final val EQUAL_LENGTH = 0
    final val SECOND_LONGER = 1

    final def apply(e: UEq, hint: Int): Seq[UEq] = {
      assert(hint != CANNOT_APPLY)
      if (hint == EQUAL_LENGTH) {
        e._1.funParamTypesWithResultType.zip(e._2.funParamTypesWithResultType)
      } else {
        val shorterTyList = e._1.funParamTypesWithResultType
        val splittedLongerTy = e._2.splitFunParamTypesAt(shorterTyList.size-1)
        (shorterTyList.last, splittedLongerTy._2) +: shorterTyList.init.zip(splittedLongerTy._1)
      }
    }

    final def canApply(e: UEq): Int = {
      if (!e._1.isFunType || !e._2.isFunType) CANNOT_APPLY
      else {
        val tys1 = e._1.funParamTypesWithResultType
        val tys2 = e._2.funParamTypesWithResultType
        if (tys1.size > tys2.size) CANNOT_APPLY /* impossible to match right side */
        else if (tys1.size == tys2.size) EQUAL_LENGTH
        else { // tys1.size < tys2.size
          if (tys1.last.isBoundTypeVar) // Only possible if last one is variable
            SECOND_LONGER
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
    import leo.datastructures.Type.BoundType
    final def apply(e: UEq): Subst = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      val tyVar = if (leftIsTypeVar) BoundType.unapply(e._1).get else BoundType.unapply(e._2).get
      val otherTy = if (leftIsTypeVar) e._2 else e._1

      Subst.singleton(tyVar, otherTy)
    }

    final def canApply(e: UEq, forbiddenVars: Set[Int]): Boolean = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      if (!leftIsTypeVar) false
      else {
        val tyVar = BoundType.unapply(e._1).get
        val otherTy = e._2
        !forbiddenVars.contains(tyVar) && !otherTy.typeVars.contains(tyVar)
      }
    }
  }

}
