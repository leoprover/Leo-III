package leo.modules.calculus

import scala.annotation.tailrec

import leo.datastructures.{Term, Type, Subst, NDStream, BFSAlgorithm, SearchConfiguration}

trait Matching {
  type UEq = (Term, Term); type UTEq = (Type, Type)
  type TermSubst = Subst; type TypeSubst = Subst
  type Result = (TermSubst, TypeSubst)

  /** Returns an iterable of substitutions (σ_i) such that sσ_i = t and there exists no such ϱ
    * which is more general than σ_i. */
  def matchTerms(vargen: FreshVarGen, s: Term, t: Term): Iterable[Result]
}

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


  def matchTerms(vargen: FreshVarGen, s: Term, t: Term): Iterable[Result] = {
    if (s.ty != t.ty) throw new NotImplementedError()
    else {
      new NDStream[Result](new MyConfiguration(Seq((s.etaExpand,t.etaExpand)), Seq(), Subst.id, Subst.id, 0), new EnumUnifier(vargen, Subst.id)) with BFSAlgorithm
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

/**
  * Created by lex on 6/4/16.
  */
object FOMatching {
  // FIXME Old implementation, most likely broken
  import leo.datastructures.{Term, Type, Subst}
  import Term.{Bound, ∙}

  type UEq = (Term, Term)

  // #################
  // Exported functions
  // #################

  /**
    * `s` matches `t` iff there exists a substitution sigma such that `s[sigma] = t`.
    * Returns true if such a substitution exists.
    *
    * @param s A term.
    * @param t The term to be matched against (i.e. the term that may be an instance of `s`)
    */
  final def decideMatch(s: Term, t: Term): Boolean = decideMatch0(Vector((s,t)))

  /**
    * `s` matches `t` iff there exists a substitution sigma such that `s[sigma] = t`.
    * Returns such a substitution if existent, None otherwise.
    *
    * @param s A term.
    * @param t The term to be matched against (i.e. the term that may be an instance of `s`)
    */
  final def matches(s: Term, t: Term): Option[Subst] = matches0(Seq((s,t)), Seq())


  // #################
  // Internal functions
  // #################

  // apply exaustively delete, comp and bind on the set. If at the end uproblems is empty,
  // we succeeded, else we fail.
  // Invariant: UEq (l,r) always never swapper to (r',l') where r',l' originate from r,l, respectively.
  @tailrec
  final private def matches0(uproblems: Seq[UEq], sproblems: Seq[UEq]): Option[Subst]  = {
    leo.Out.trace(s"Unsolved: ${uproblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    // apply delete
    val ind1 = uproblems.indexWhere(DeleteRule.canApply)
    if (ind1 > -1) {
      leo.Out.finest("Apply Delete")
      matches0(uproblems.take(ind1) ++ uproblems.drop(ind1 + 1), sproblems)
      // apply decomp
    } else {
      val ind2 = uproblems.indexWhere(DecompRule.canApply)
      if (ind2 > -1) {
        leo.Out.finest("Apply Decomp")
        matches0(DecompRule(uproblems(ind2)) ++ uproblems.take(ind2) ++ uproblems.drop(ind2 + 1), sproblems)
        // apply bind
      } else {
        val ind3 = uproblems.indexWhere(BindRule.canApply)
        if (ind3 > -1) {
          leo.Out.finest("Apply Bind")
          leo.Out.finest(s"Bind on " +
            s"\n\tLeft: ${uproblems(ind3)._1.pretty}\n\tRight: ${uproblems(ind3)._2.pretty}")
          val be = BindRule(uproblems(ind3))
          leo.Out.finest(s"Resulting equation: ${be._1.pretty} = ${be._2.pretty}")
          val sb = computeSubst(be)
          matches0(applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3 + 1)), applySubstToList(sb, sproblems) :+ be)
        } else { // TODO: Rework matching as done in unification without dealing with extensionality.
          //            val ind4 = uproblems.indexWhere(FuncRule.canApply)
          //            if (ind4 > -1) {
          //              leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
          //              matches0((uproblems.take(ind4) :+ FuncRule(uproblems(ind4))(sig)) ++ uproblems.drop(ind4 + 1), sproblems)(sig)
          //            } else {
          if (uproblems.isEmpty) {
            Some(computeSubst(sproblems))
          } else
            None
          //            }
        }
      }
    }
  }

  // apply exaustively delete, comp and bind on the set. If at the end uproblems is empty,
  // we succeeded, else we fail.
  // Invariant: UEq (l,r) always never swapper to (r',l') where r',l' originate from r,l, respectively.
  @tailrec
  final private def decideMatch0(uproblems: Seq[UEq]): Boolean  = {
    leo.Out.trace(s"Unsolved: ${uproblems.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    // apply delete
    val ind1 = uproblems.indexWhere(DeleteRule.canApply)
    if (ind1 > -1) {
      leo.Out.finest("Apply Delete")
      decideMatch0(uproblems.take(ind1) ++ uproblems.drop(ind1 + 1))
      // apply decomp
    } else {
      val ind2 = uproblems.indexWhere(DecompRule.canApply)
      if (ind2 > -1) {
        leo.Out.finest("Apply Decomp")
        decideMatch0(DecompRule(uproblems(ind2)) ++ uproblems.take(ind2) ++ uproblems.drop(ind2 + 1))
        // apply bind
      } else {
        val ind3 = uproblems.indexWhere(BindRule.canApply)
        if (ind3 > -1) {
          leo.Out.finest("Apply Bind")
          leo.Out.finest(s"Bind on " +
            s"\n\tLeft: ${uproblems(ind3)._1.pretty}\n\tRight: ${uproblems(ind3)._2.pretty}")
          val be = BindRule(uproblems(ind3))
          leo.Out.finest(s"Resulting equation: ${be._1.pretty} = ${be._2.pretty}")
          val sb = computeSubst(be)
          decideMatch0(applySubstToList(sb, uproblems.take(ind3) ++ uproblems.drop(ind3 + 1)))
        } else {
          //            val ind4 = uproblems.indexWhere(FuncRule.canApply)
          //            if (ind4 > -1) {
          //              leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
          //              decideMatch0((uproblems.take(ind4) :+ FuncRule(uproblems(ind4))(sig)) ++ uproblems.drop(ind4 + 1))(sig)
          //            } else
          uproblems.isEmpty
        }
      }
    }
  }

  private final def computeSubst(eqn: UEq): Subst = {
    val (t,s) = (eqn._1, eqn._2)
    assert(isVariable(t))
    val (_, idx) = Bound.unapply(t).get
    Subst.singleton(idx, s)
  }

  private final def computeSubst(eqns: Seq[UEq]): Subst = {
    assert(eqns.forall(eqn => isVariable(eqn._1)))
    val eqns2 = eqns.map {case (l,r) => (Bound.unapply(l).get._2, r)}
    Subst.fromSeq(eqns2)
  }

  private final def applySubstToList(s: Subst, l: Seq[UEq]): Seq[UEq] =
    l.map(e => (e._1.substitute(s),e._2.substitute(s)))

  private final def isVariable(t: Term): Boolean = Bound.unapply(t).isDefined
  private final def isFlexible(t: Term): Boolean = Bound.unapply(t.headSymbol).isDefined

  /**
    * 3
    * BindRule tells if Bind is applicable
    * equation is not oriented
    * return an equation (x,s) substitution is computed from this equation later
    */
  private object BindRule {
    def apply(e: UEq) = {
      val (t,s) = (e._1,e._2)
      (t.headSymbol,s)
    }
    def canApply(e: UEq) = {
      val (t,s) = (e._1, e._2)
      if (!isFlexible(t)) false
      else {
        val (_,x) = Bound.unapply(t.headSymbol).get
        if (!t.headSymbol.etaExpand.equals(t) && !t.equals(t.headSymbol)) false // t might be of function type,
        // hence it might be eta expanded to a function, we check that here.

        // check it doesnt occur in s
        else !s.looseBounds.contains(x)
      }
    }
  }

  /**
    * 1
    * returns true if the equation can be deleted
    */
  private object DeleteRule  {
    def canApply(e: UEq) = {
      val (t,s) = e
      t.equals(s)
    }
  }

  /**
    * 2
    * returns the list of equations if the head symbols are the same function symbol.
    */
  private object DecompRule {
    def apply(e: UEq) = e match {
      case (_ ∙ sq1, _ ∙ sq2) => simplifyArguments(sq1).zip(simplifyArguments(sq2))
      case _ => throw new IllegalArgumentException("impossible")
    }
    def canApply(e: UEq) = e match {
      case (hd1 ∙ args1, hd2 ∙ args2) if hd1 == hd2 => {
        if (isFlexible(hd1)) false
        else {
          if (hd1.ty.isPolyType) {
            assert(hd2.ty.isPolyType)
            val tyArgs1 = args1.takeWhile(_.isRight).map(_.right.get)
            val tyArgs2 = args2.takeWhile(_.isRight).map(_.right.get)
            tyArgs1 == tyArgs2
          } else
            true
        }
      }
      case _ => false
    }
  }

  private object FuncRule {
    import leo.datastructures.Signature
    def apply(e: UEq)(sig: Signature): UEq = {
      leo.Out.trace(s"Apply Func on ${e._1.pretty} = ${e._2.pretty}")
      val funArgTys = e._1.ty.funParamTypes
      val skTerms = funArgTys.map(leo.modules.calculus.skTerm(_, Seq(), Seq())(sig)) // TODO: Check if this is ok (no free vars)
      (Term.mkTermApp(e._1, skTerms).betaNormalize, Term.mkTermApp(e._2, skTerms).betaNormalize)
    }

    def canApply(e: UEq) = {
      // we can apply it if the sides of the equation have functional type
      assert(e._1.ty == e._2.ty, s"Func Rule: Both UEq sides have not-matching type:\n\t${e._1.pretty}\n\t${e._1.ty.pretty}\n\t${e._2.pretty}\n\t${e._2.ty.pretty}")
      e._1.ty.isFunType
    }
  }

  private final def simplifyArguments(l: Seq[Either[Term,Type]]): Seq[Term] = l.filter(_.isLeft).map(_.left.get)

}


trait TypeMatching {
  type UEq = (Type, Type) // Left one is the only one that can be bound, right is considered rigid.
  type TypeSubst = Subst

  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  def matching(s: Type, t: Type): Option[TypeSubst]
}
object TypeMatching {
  private val impl: TypeMatching = TypeMatchingImpl

  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  def apply(s: Type, t: Type): Option[TypeMatching#TypeSubst] = impl.matching(s,t)
}

object TypeMatchingImpl extends TypeMatching {
  /** Returns a substitution `Some(σ)` such that sσ = t. Returns `None` if no such `σ` exists.  */
  override def matching(s: Type, t: Type): Option[TypeSubst] = tyDetExhaust(Vector((s,t)), Subst.id)


  @tailrec
  final protected[calculus] def tyDetExhaust(uTyProblems: Seq[UEq], unifier: TypeSubst): Option[TypeSubst] = {
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
        if (tys1.size == tys2.size) EQUAL_LENGTH
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

    final def canApply(e: UEq): Boolean = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      if (!leftIsTypeVar) false
      else {
        val tyVar = BoundType.unapply(e._1).get
        val otherTy = e._2
        !otherTy.typeVars.contains(tyVar)
      }
    }
  }

}
