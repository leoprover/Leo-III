package leo.modules.calculus

import leo.datastructures.Type.BoundType
import leo.datastructures.{Subst, Term, Type}

import scala.annotation.tailrec


trait Unification {
  /** A `UEq` is an unsolved equation. */
  type UEq = (Term, Term)
  /** `UTEq` is an unsolved type equation. */
  type UTEq = (Type, Type)
  type TermSubst = Subst
  type TypeSubst = Subst
  type ResultSubst = (TermSubst, TypeSubst)

  type UnificationResult = (ResultSubst, Seq[UEq])
  type ExtendedUnificationResult = (ResultSubst, Seq[UEq], Seq[UEq])

  /**
    * Generates a stream of `UnificationResult`s (tuples of substitutions and unsolved equations)
    * where each result solves the unification constraint `t = s`. The unsolved equations on the `UnificationResult`
    * are hereby all flex-flex unification constraints that are postponed. The result stream
    * is empty, if the equation `t = s` is not unifiable.
    */
  def unify(vargen: FreshVarGen, t : Term, s : Term, depth: Int): Iterable[UnificationResult]

  /**
    * Generates a stream of `UnificationResult`s (tuples of substitutions and unsolved equations)
    * where each result solves all unification constraints `t_i = s_i` in `constraints`.
    * The unsolved equations on the `UnificationResult`
    * are hereby all flex-flex unification constraints that are postponed. The result stream
    * is empty, if the equation `t = s` is not unifiable.
    */
  def unifyAll(vargen: FreshVarGen, constraints: Seq[UEq], depth: Int): Iterable[UnificationResult]
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
object HuetsPreUnification extends Unification {
  import scala.annotation.tailrec
  import leo.datastructures.{SearchConfiguration, NDStream, BFSAlgorithm}

  /** The Depth is the number of lambda abstractions under which a term is nested.*/
  type Depth = Int


  /** `UEq0` extends UEq with an depth indicator. */
  type UEq0 = (Term, Term, Depth)
  /** A `SEq` is a solved equation. */
  type SEq = (Term, Term)

  /** `STEq` is a solved type equation. */
  type STEq = UTEq


  /////////////////////////////////////
  // the state of the search space
  /////////////////////////////////////
  protected case class MyConfiguration(unprocessed: Seq[UEq],
                                       flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
                                       solved: TermSubst, solvedTy: TypeSubst,
                                       result: Option[UnificationResult], searchDepth: Int, maxDepth: Int)
    extends SearchConfiguration[UnificationResult] {
    def this(result: UnificationResult) = this(null, null, null, null, null, Some(result), Int.MaxValue, Int.MaxValue) // for success
    def this(unprocessed: Seq[UEq],
             flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
             solved: TermSubst, solvedTy: TypeSubst,
             searchDepth: Int, maxDepth: Int) = this(unprocessed, flexRigid, flexFlex, solved, solvedTy, None, searchDepth, maxDepth) // for in node

    override final def isTerminal: Boolean = searchDepth >= maxDepth
    override def toString: String  = if (unprocessed != null) s"{result(${result.isDefined}),depth($searchDepth),${unprocessed.map(x => s"<${x._1.pretty},${x._2.pretty}>").mkString}}"
    else s"{result(${result.isDefined}),depth($searchDepth)}"
  }


  /////////////////////////////////////
  // Unifier search starts with these methods
  /////////////////////////////////////
  final def unify(vargen: FreshVarGen, t1 : Term, s1 : Term, uniDepth: Int) : Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val t_ty = t1.ty
    val s_ty = t1.ty
    val initialTypeSubst = TypeUnification(t_ty, s_ty)
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val t = t1.substitute(Subst.id, initialTypeSubst0).etaExpand
      val s = s1.substitute(Subst.id, initialTypeSubst0).etaExpand
      new NDStream[UnificationResult](new MyConfiguration(Vector((t,s)), Vector(), Vector(), Subst.id, Subst.id, 0, uniDepth), new EnumUnifier(vargen, initialTypeSubst0)) with BFSAlgorithm
    }
  }

  final def unifyAll(vargen: FreshVarGen, constraints: Seq[(Term, Term)], uniDepth: Int): Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val initialTypeSubst = TypeUnification(constraints.map(e => (e._1.ty, e._2.ty)))
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val constraints0 = constraints.map(eq => (eq._1.substitute(Subst.id, initialTypeSubst0).etaExpand, eq._2.substitute(Subst.id, initialTypeSubst0).etaExpand))
      new NDStream[UnificationResult](new MyConfiguration(constraints0.toVector, Vector(), Vector(), Subst.id, Subst.id, 0, uniDepth), new EnumUnifier(vargen, initialTypeSubst0)) with BFSAlgorithm
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
                                                                                          conf.solved, conf.solvedTy)
      leo.Out.finest(s"Finished detExhaust")
      // if uTyProblems is non-empty fail
      if (fail) {
        leo.Out.trace(s"Unification failed.")
        Seq()
      } else {
        // if there is no unsolved equation (other than flex-flex), then succeed
        if (flexRigid.isEmpty) {
          leo.Out.trace(s"Unification finished")
          leo.Out.finest(s"\tTerm substitution ${partialUnifier.normalize.pretty}")
          leo.Out.finest(s"\tType substitution ${partialTyUnifier.normalize.pretty}")
          Seq(new MyConfiguration(((partialUnifier.normalize, initialTypeSubst.comp(partialTyUnifier).normalize), flexFlex)))
        }
        // else do flex-rigid cases
        else {
          assert(flexRigid.nonEmpty)
          leo.Out.finest(s"flex-rigid at depth ${conf.searchDepth}")
          val head = flexRigid.head
          leo.Out.finest(s"on ${head._1.pretty} = ${head._2.pretty} (depth ${head._3})")
          import  scala.collection.mutable.ListBuffer
          val lb = new ListBuffer[MyConfiguration]
          // compute the imitate partial binding and add the new configuration
          if (ImitateRule.canApply(head)) lb.append(new MyConfiguration(Vector(ImitateRule(vargen, head)), flexRigid, flexFlex,
            partialUnifier, partialTyUnifier, conf.searchDepth+1, conf.maxDepth))

          // compute all the project partial bindings and add them to the return list
          ProjectRule(vargen, head).foreach (e => lb.append(new MyConfiguration(Vector(e), flexRigid, flexFlex,
            partialUnifier, partialTyUnifier, conf.searchDepth+1, conf.maxDepth)))

          lb.toVector
        }
      }
    }
  }

  /** Exhaustively apply delete, comp and bind on the set  of unprocessed equations. */
  @tailrec
  final protected[calculus] def detExhaust(unprocessed: Seq[UEq],
                         flexRigid: Seq[UEq0], flexFlex: Seq[UEq],
                         solved: TermSubst, solvedTy: TypeSubst):
                        (Boolean, Seq[UEq0], Seq[UEq], TermSubst, TypeSubst) = {
    //                  (fail, flexRigid, flexflex, solved, solvedTy)
    import leo.datastructures.collectLambdas
    leo.Out.finest(s"Unsolved (term eqs): ${unprocessed.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
    // check unprocessed
    if (unprocessed.nonEmpty) {
      val head0 = unprocessed.head
      leo.Out.finest(s"detExhaust on: ${head0._1.pretty} = ${head0._2.pretty}")
      // Try all term rules
      if (DeleteRule.canApply(head0)) {
        leo.Out.finest("Apply delete")
        detExhaust(unprocessed.tail, flexRigid, flexFlex, solved, solvedTy)
      } else {
        val left = head0._1.etaExpand
        val right = head0._2.etaExpand
        leo.Out.finest(s"Left etaExpanded: ${left.pretty}")
        leo.Out.finest(s"right etaExpanded: ${right.pretty}")
        val (leftBody, leftAbstractions) = collectLambdas(left)
        val (rightBody, rightAbstractions) = collectLambdas(right)
        assert(leftAbstractions == rightAbstractions)
        val abstractionCount = leftAbstractions.size

        val decompHint = DecompRule.canApply((leftBody, rightBody), abstractionCount)
        if (decompHint._1) {
          if (decompHint._2.isDefined) {
            leo.Out.finest("Apply decomp")
            val typeSubst = decompHint._2.get
            if (typeSubst == Subst.id) {
              // no need to apply
              val newUnsolvedTermEqs = DecompRule.apply((leftBody, rightBody), leftAbstractions)
              detExhaust(newUnsolvedTermEqs ++ unprocessed.tail,
                flexRigid,
                flexFlex,
                solved, solvedTy)
            } else {
              val newUnsolvedTermEqs = DecompRule.apply((leftBody.typeSubst(typeSubst), rightBody.typeSubst(typeSubst)), leftAbstractions.map(_.substitute(typeSubst)))
              detExhaust(newUnsolvedTermEqs ++ applySubstToList(Subst.id, typeSubst, unprocessed.tail),
                applyTySubstToList(typeSubst, flexRigid),
                applySubstToList(Subst.id, typeSubst, flexFlex),
                solved.applyTypeSubst(typeSubst), solvedTy.comp(typeSubst))
            }
          } else {
            leo.Out.finest("Decomp failed due to type (non-)unifiablity")
            (true, flexRigid, flexFlex, solved, solvedTy)
          }
        } else {
          val bindHint = BindRule.canApply(leftBody, rightBody, abstractionCount)
          if (bindHint != BindRule.CANNOT_APPLY) {
            val subst = BindRule.apply(head0, abstractionCount, bindHint)
            leo.Out.finest(s"Bind: ${subst.pretty}")
            detExhaust(
              applySubstToList(subst, Subst.id, flexRigid.map(e => (e._1, e._2)) ++ flexFlex ++ unprocessed.tail),
              Vector(), Vector(),
              solved.comp(subst), solvedTy)
          } else {
            // ... move to according list if nothing applies
            if (flexflex(head0, abstractionCount))
              detExhaust(unprocessed.tail, flexRigid, head0 +: flexFlex,
                solved, solvedTy)
            else if (rigidrigid(head0, abstractionCount))
              (true, flexRigid, flexFlex, solved, solvedTy) // fail
            else {
              assert(flexrigid(head0, abstractionCount))
              detExhaust(unprocessed.tail, (left, right, abstractionCount) +: flexRigid, flexFlex,
                solved, solvedTy)
            }
          }
        }
      }
    } else {
      // no unprocessed left, return sets
      (false, flexRigid, flexFlex, solved, solvedTy)
    }
  }


  /////////////////////////////////////
  // Huets rules
  /////////////////////////////////////
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
    import leo.datastructures.{termArgs, typeArgs, Subst}

    final def apply(e: UEq, abstractions: Seq[Type]): Seq[UEq] = e match {
      case (_ ∙ sq1, _ ∙ sq2) =>
        val termArgs1 = termArgs(sq1)
        val termArgs2 = termArgs(sq2)
        zipWithAbstractions(termArgs1, termArgs2, abstractions)
      case _ => throw new IllegalArgumentException("impossible")
    }
    final def canApply(e: UEq, depth: Depth): (Boolean, Option[TypeSubst]) = e match {
      case (hd1 ∙ args1, hd2 ∙ args2) if hd1 == hd2 && !isFlexible(hd1, depth) =>
        if (!hd1.ty.isPolyType) (true, Some(Subst.id))
        else {
          val typeArgs1 = typeArgs(args1)
          val typeArgs2 = typeArgs(args2)
          val uniConstraints: Seq[UTEq] = typeArgs1.zip(typeArgs2)
          val uniResult = TypeUnification(uniConstraints)
          if (uniResult.isDefined) {
            (true, uniResult)
          } else (true, None) // This encodes: Applicable but not successful
      }
      case _ => (false, None)
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

    final def canApply(leftBody: Term, rightBody: Term, depth: Int): Int = {
      import leo.datastructures.getVariableModuloEta
      // orienting the equation
      val possiblyLeftVar = getVariableModuloEta(leftBody, depth)
      if (possiblyLeftVar > 0) {
        //left side is variable, do occurs check on right
        if (rightBody.looseBounds.contains(possiblyLeftVar + depth)) CANNOT_APPLY else LEFT_IS_VAR
      } else {
        val possiblyRightVar = getVariableModuloEta(rightBody, depth)
        if (possiblyRightVar > 0) {
          //right side is variable, do occurs check left
          if (leftBody.looseBounds.contains(possiblyRightVar + depth)) CANNOT_APPLY else RIGHT_IS_VAR
        } else CANNOT_APPLY
      }
    }
  }

  /**
    * 4a
    * equation is not oriented
    * not to forget that the approximations must be in eta-long-form
    */
  object ImitateRule {
    final def apply(vargen: FreshVarGen, e: UEq0): UEq = {
      import leo.datastructures.Term.Bound
      leo.Out.finest(s"Apply Imitate")
      leo.Out.finest(s"on ${e._1.pretty} = ${e._2.pretty}")
      val depth : Int = e._3
      // orienting the equation
      val (t,s) = if (isFlexible(e._1, depth)) (e._1,e._2) else (e._2, e._1)
      val s0 = if (s.headSymbol.ty.isPolyType) {
        leo.Out.finest(s"head symbol is polymorphic")
        Term.local.mkTypeApp(s.headSymbol, takePrefixTypeArguments(s))}
      else
        s.headSymbol
      leo.Out.finest(s"chose head symbol to be ${s0.pretty}, type: ${s0.ty.pretty}")
      val variable = Bound.unapply(t.headSymbol).get
      leo.Out.finest(s"variable: ${variable}")
      val liftedVar = Term.local.mkBound(variable._1, variable._2 - depth).etaExpand
      leo.Out.finest(s"lifted variable: ${liftedVar.pretty}")
      val res = (liftedVar, partialBinding(vargen, t.headSymbol.ty,  s0))
      leo.Out.finest(s"Result of Imitate: ${res._1.pretty} = ${res._2.pretty}")
      res
    }

    // must make sure s (rigid-part) doesnt have as head a bound variable
    final def canApply(e: UEq0): Boolean = {
      import leo.datastructures.Term.Bound
      // orienting the equation
      val (_,s) = if (isFlexible(e._1, e._3)) (e._1,e._2) else (e._2, e._1)
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
      val (t,_) = if (isFlexible(e._1,depth)) (e._1,e._2) else (e._2, e._1)
      leo.Out.finest(s"Head symbol type: ${t.headSymbol.ty.pretty}")
      val hdTypes = t.headSymbol.ty.funParamTypesWithResultType
      val hdResultType = hdTypes.last
      val bvars = hdTypes.init.zip(List.range(1,t.headSymbol.ty.arity+1).reverse).map(p => Term.mkBound(p._1,p._2))
      leo.Out.finest(s"BVars in Projectrule: ${bvars.map(_.pretty).mkString(",")}")
      //Take only those bound vars that are itself a type with result type == type of general binding goal
      val funBVars = bvars.filter(bvar => bvar.ty.funParamTypesWithResultType.last == hdResultType)
      leo.Out.finest(s"compatible type BVars in Projectrule: ${funBVars.map(_.pretty).mkString(",")}")
      val variable = Bound.unapply(t.headSymbol).get
      val liftedVar = Term.local.mkBound(variable._1, variable._2 - depth).etaExpand
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

  @tailrec
  private final def takePrefixTypeArguments(t: Term): Seq[Type] = {
    import leo.datastructures.Term.{∙,:::>}
    t match {
      case _ ∙ args => args.takeWhile(_.isRight).map(_.right.get)
      case _ :::> body  => takePrefixTypeArguments(body)
      case _ => Seq()
    }
  }

  @inline protected[calculus] final def applySubstToList(termSubst: Subst, typeSubst: Subst, l: Seq[(Term, Term)]): Seq[(Term, Term)] =
    l.map(e => (e._1.substitute(termSubst,typeSubst).etaExpand,e._2.substitute(termSubst,typeSubst).etaExpand))
  @inline private final def applyTySubstToList(typeSubst: Subst, l: Seq[UEq0]): Seq[UEq0] =
    l.map(e => (e._1.substitute(Subst.id, typeSubst).etaExpand,e._2.substitute(Subst.id, typeSubst).etaExpand, e._3))


  protected[calculus] final def zipWithAbstractions(l: Seq[Term], r: Seq[Term], abstractions: Seq[Type]): Seq[UEq] = {
    zipWithAbstractions0(l,r,abstractions, Vector())
  }

  @tailrec private final def zipWithAbstractions0(l: Seq[Term], r: Seq[Term],
                                                  abstractions: Seq[Type],
                                                  acc: Seq[UEq]): Seq[UEq] = {
    import leo.datastructures.Term.local.λ
    if (l.isEmpty) {
      assert(r.isEmpty, "Decomp on differently sized argument lists, failing.")
      acc
    } else {
      assert(r.nonEmpty, "Decomp on differently sized argument lists, failing.")
      val hdl = l.head; val hdr = r.head
      val left = λ(abstractions)(hdl).etaExpand
      val right = λ(abstractions)(hdr).etaExpand
//      leo.Out.finest(s"left: ${left.pretty} (${left.isEtaNormal})")
//      leo.Out.finest(s"right: ${right.pretty} (${right.isEtaNormal})")
      zipWithAbstractions0(l.tail, r.tail, abstractions, acc :+ (left,right))
    }
  }
}

/**
  * Pattern unification taken from Nipkow: Functional Unification of HO Pattern
  * and slightly adopted.
  * The unify methods only return an mgu if the argument are HO patterns,
  * else the unifiers will be any arbitrary unifier (if existent).
  */
object PatternUnification extends Unification {
  import leo.datastructures.collectLambdas
  import HuetsPreUnification.applySubstToList

    /////////////////////////////////////
  // Unifier search starts with these methods
  /////////////////////////////////////
  final def unify (vargen: FreshVarGen, t1 : Term, s1 : Term, depth: Int) : Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val t_ty = t1.ty
    val s_ty = s1.ty
    val initialTypeSubst = TypeUnification(t_ty, s_ty)
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      val t = t1.substitute(Subst.id, initialTypeSubst0).etaExpand
      val s = s1.substitute(Subst.id, initialTypeSubst0).etaExpand
      val unifyResult = unify0(Vector((t,s)),initialTypeSubst0, vargen)
      if (unifyResult.isDefined) Seq(unifyResult.get)
      else Iterable.empty
    }
  }

  final def unifyAll(vargen: FreshVarGen, constraints: Seq[(Term, Term)], depth: Int): Iterable[UnificationResult] = {
    // 1. check if types are unifiable
    val initialTypeSubst = TypeUnification(constraints.map(e => (e._1.ty, e._2.ty)))
    // 2. Continue only if types are unifiable
    if (initialTypeSubst.isEmpty)
      Iterable.empty
    else {
      val initialTypeSubst0 = initialTypeSubst.get
      leo.Out.finest(s"initialTypeSubst0: ${initialTypeSubst0.pretty}")
      val constraints0 = constraints.map(eq => (eq._1.substitute(Subst.id, initialTypeSubst0).etaExpand, eq._2.substitute(Subst.id, initialTypeSubst0).etaExpand))
      val unifyResult = unify0(constraints0.toVector,initialTypeSubst0, vargen)
      if (unifyResult.isDefined) Seq(unifyResult.get)
      else Iterable.empty
    }
  }

  /** Wrap up the unification result with the initial type substition and return as Option. */
  private final def unify0(ueqs: Seq[UEq], initialTypeSubst: TypeSubst, vargen: FreshVarGen): Option[UnificationResult] = {
    val unifier = unify1(ueqs, vargen, Subst.id, Subst.id)
    if (unifier.isDefined)
      Some((unifier.get._1.normalize, initialTypeSubst.comp(unifier.get._2).normalize), Seq())
    else
      None
  }

  type PartialUniResult = (TermSubst, TypeSubst)

  /** Main unification method: Solve head equations subsequently by applying the according rules. */
  @tailrec
  private final def unify1(ueqs: Seq[UEq], vargen: FreshVarGen, partialUnifier: TermSubst, partialTyUnifier: TypeSubst): Option[PartialUniResult] = {
    import leo.datastructures.Term.{Bound, ∙}
    import leo.datastructures.partitionArgs
    import HuetsPreUnification.zipWithAbstractions

    if (ueqs.isEmpty)
      Some((partialUnifier, partialTyUnifier))
    else {
      val (l0,r0) = ueqs.head
      if (l0 == r0) unify1(ueqs.tail, vargen, partialUnifier, partialTyUnifier)
      else {
        val l = l0.substitute(partialUnifier, partialTyUnifier).etaExpand
        val r = r0.substitute(partialUnifier, partialTyUnifier).etaExpand
        leo.Out.finest(s"solve: ${l.pretty} = ${r.pretty}")
        // take off the lambdas
        val (leftBody, leftAbstractions) = collectLambdas(l)
        val (rightBody, rightAbstractions) = collectLambdas(r)
        assert(leftAbstractions == rightAbstractions)
        val abstractionCount = leftAbstractions.size

        (leftBody, rightBody) match {
          case (hd1 ∙ args1, hd2 ∙ args2) => (hd1, hd2) match {
            case (Bound(ty1, idx1), Bound(ty2, idx2))
              if idx1 > abstractionCount && idx2 > abstractionCount =>
              /* flex-flex */
              leo.Out.finest("Apply Flex-flex")
              assert(leftBody.ty == rightBody.ty)
              val partialUniResult = flexflex(idx1-abstractionCount, ty1, args1, idx2-abstractionCount, ty2, args2, vargen, leftBody.ty)
              unify1(ueqs.tail, vargen, partialUnifier.comp(partialUniResult._1), partialTyUnifier.comp(partialUniResult._2))
            case (Bound(ty1, idx1), _) if idx1 > abstractionCount =>
              /* flex-rigid */
              if (r.looseBounds.contains(idx1 - abstractionCount)) None
              else {
                leo.Out.finest("Apply Flex-rigid")
                val result = flexrigid(idx1 - abstractionCount, ty1, args1, hd2, args2, rightBody, vargen, leftAbstractions)
                if (result == null) None
                else {
                  val partialUniResult = result._1
                  val newUeqs = result._2
                  unify1(newUeqs ++ ueqs.tail, vargen, partialUnifier.comp(partialUniResult._1), partialTyUnifier.comp(partialUniResult._2))
                }
              }
            case (_, Bound(ty2, idx2)) if idx2 > abstractionCount=>
              /* rigid-flex */
              if (l.looseBounds.contains(idx2 - abstractionCount)) None
              else {
                leo.Out.finest("Apply rigid-flex")
                val result = flexrigid(idx2 - abstractionCount, ty2, args2, hd1, args1, leftBody, vargen, leftAbstractions)
                if (result == null) None
                else {
                  val partialUniResult = result._1
                  val newUeqs = result._2
                  unify1(newUeqs ++ ueqs.tail, vargen, partialUnifier.comp(partialUniResult._1), partialTyUnifier.comp(partialUniResult._2))
                }
              }
            case _ => /* rigid-rigid */
              if (hd1 == hd2) {
                leo.Out.finest("Apply rigid-rigid")
                if (hd1.ty.isPolyType) {
                  leo.Out.finest(s"Poly rigid-rigid")
                  val (tyArgs1, termArgs1) = partitionArgs(args1)
                  val (tyArgs2, termArgs2) = partitionArgs(args2)
                  assert(tyArgs1.size == tyArgs2.size)
                  val tyUniConstraints = tyArgs1.zip(tyArgs2)
                  leo.Out.finest(s"ty constraints: ${tyUniConstraints.map(c => c._1.pretty + " = " + c._2.pretty).mkString(",")}")
                  val tyUniResult = TypeUnification(tyUniConstraints)
                  if (tyUniResult.isDefined) {
                    val tySubst = tyUniResult.get
                    leo.Out.finest(s"Poly rigid-rigid uni succeeded: ${tySubst.pretty}")
                    val newUeqs = zipWithAbstractions(termArgs1, termArgs2, leftAbstractions)
                    leo.Out.finest(s"New unsolved:\n\t${newUeqs.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
                    leo.Out.finest(s"partialUnifier before: ${partialUnifier.pretty}")
                    leo.Out.finest(s"partialUnifier after: ${partialUnifier.applyTypeSubst(tySubst).pretty}")
                    leo.Out.finest(s"partialTyUnifier before: ${partialTyUnifier.pretty}")
                    leo.Out.finest(s"partialTyUnifier after: ${partialTyUnifier.comp(tySubst).pretty}")
                    unify1(applySubstToList(Subst.id, tySubst, newUeqs ++ ueqs.tail), vargen, partialUnifier.applyTypeSubst(tySubst), partialTyUnifier.comp(tySubst))
                  } else {
                    leo.Out.finest(s"Poly rigid-rigid uni failed")
                    None
                  }
                } else {
                  val termArgs1 = args1.map(_.left.get)
                  val termArgs2 = args2.map(_.left.get)
                  val newUeqs = zipWithAbstractions(termArgs1, termArgs2, leftAbstractions)
                  leo.Out.finest(s"New unsolved:\n\t${newUeqs.map(eq => eq._1.pretty + " = " + eq._2.pretty).mkString("\n\t")}")
                  unify1(newUeqs ++ ueqs.tail, vargen, partialUnifier, partialTyUnifier)
                }
              } else None

          }
          case _ => assert(false); None
        }
      }
    }
  }

  /** unification of flex-flex equation. Fails if type arguments are applied (not pattern, is it?). */
  private final def flexflex(idx1: Int, ty1: Type, args01: Seq[Either[Term, Type]], idx2: Int, ty2: Type, args02: Seq[Either[Term, Type]], vargen: FreshVarGen, ty: Type): PartialUniResult = {
    import leo.datastructures.Term.local.{λ, mkTermApp, mkBound}
    import leo.datastructures.Type.mkFunType
    try
      {
        val args1 = args01.map(_.left.get)
        val args2 = args02.map(_.left.get)

        if (idx1 == idx2) {
          // flexflex1: Flexhead are the same
          if (args1 == args2) {// trivial
            (Subst.id, Subst.id)
          } else {
            assert(args1.map(_.ty) == args2.map(_.ty))
            assert(ty1 == ty2)
            assert(args1.size == args2.size)
            // Collect all types of argument in order to reconstruct type of fresh
            // free variable H:
            // tys are the types of the actraction
            val tys = args1.map(_.ty)
            // posArgs are those are which are applied to H (argument that match)
            val posArgs = pos(args1, args2)
            // H has type posArgs1 -> posArgs2 -> .... -> ty
            val freshVar = vargen.next(mkFunType(posArgs.map(_.ty), ty))
            // lift fresh var to respect lambda abstractions built around below
            val liftedFreshVar = mkBound(freshVar._2, freshVar._1+tys.size)
            // binding is idx1 -> lamdas(H(posArgs))
            val binding = λ(tys)(mkTermApp(liftedFreshVar, posArgs))
            leo.Out.finest(s"binding: $idx1 -> ${binding.pretty}")
            (Subst.singleton(idx1, binding), Subst.id)
          }
        } else {
          // flexflex2: Flexhead are the different
          if (subset(args1, args2)) { // subset optimizations from paper
          val tys = args2.map(_.ty)
            val liftedVar = mkBound(ty1, idx1+tys.size)

            val argIdx = args2.zipWithIndex.toMap
            val argCount = argIdx.size
            val newArgs = args1.map(t => mkBound(t.ty, argCount - argIdx(t)))
            // binding is idx2 -> lamdas(idx1(args1'))
            val binding = λ(tys)(mkTermApp(liftedVar, newArgs))
            leo.Out.finest(s"binding: $idx2 -> ${binding.pretty}")
            (Subst.singleton(idx2, binding), Subst.id)
          } else if (subset(args2, args1)) { // ditto
          val tys = args1.map(_.ty)
            val liftedVar = mkBound(ty2, idx2+tys.size)

            val argIdx = args1.zipWithIndex.toMap
            val argCount = argIdx.size
            val newArgs = args2.map(t => mkBound(t.ty, argCount - argIdx(t)))
            // binding is idx1 -> lamdas(idx2(args2))
            val binding = λ(tys)(mkTermApp(liftedVar, newArgs))
            leo.Out.finest(s"binding: $idx1 -> ${binding.pretty}")
            (Subst.singleton(idx1, binding), Subst.id)
          } else { // two bindings
          val sameArgs = args1.intersect(args2)

            val arg1Idx = args1.zipWithIndex.toMap
            val arg1Count = arg1Idx.size
            val arg2Idx = args2.zipWithIndex.toMap
            val arg2Count = arg2Idx.size
            val newArgs1 = sameArgs.map(t => mkBound(t.ty, arg1Count - arg1Idx(t)))
            val newArgs2 = sameArgs.map(t => mkBound(t.ty, arg2Count - arg2Idx(t)))

            val tys1 = args1.map(_.ty)
            val tys2 = args2.map(_.ty)
            // H has type sameArgs1 -> sameArgs2 -> .... -> ty
            val freshVar = vargen.next(mkFunType(sameArgs.map(_.ty), ty))
            // lift fresh var to respect lambda abstractions built around below
            val liftedFreshVar1 = mkBound(freshVar._2, freshVar._1+tys1.size)
            val liftedFreshVar2 = mkBound(freshVar._2, freshVar._1+tys2.size)
            // binding1 is idx1 -> lamdas(H(sameArgs))
            val binding1 = λ(tys1)(mkTermApp(liftedFreshVar1, newArgs1))
            // binding2 is idx2 -> lamdas'(H(sameArgs))
            val binding2 = λ(tys2)(mkTermApp(liftedFreshVar2, newArgs2))
            leo.Out.finest(s"binding1: $idx1 -> ${binding1.pretty}")
            leo.Out.finest(s"binding2: $idx2 -> ${binding1.pretty}")
            (Subst.fromMap(Map(idx1 -> binding1, idx2 -> binding2)), Subst.id)
          }
        }
      } catch {
      case _:NoSuchElementException => null
    }
  }

  /** Return argument positions that have matching arguments. */
  private final def pos(args1: Seq[Term], args2: Seq[Term]): Seq[Term] = {
    import leo.datastructures.Term.local.mkBound
    if (args1.isEmpty) {
      assert(args2.isEmpty)
      Nil
    } else {
      val hd1 = args1.head
      val hd2 = args2.head
      assert(hd1.ty == hd2.ty)
      if (hd1 == hd2) {
        mkBound(hd1.ty, args1.size) +: pos(args1.tail, args2.tail)
      } else pos(args1.tail, args2.tail)
    }
  }
  @tailrec
  private final def subset[A](xs: Seq[A], ys: Seq[A]): Boolean = {
    if (xs.isEmpty) true
    else {
      val hd = xs.head
      if (ys.contains(hd)) {
        subset(xs.tail, ys)
      } else false
    }
  }

  /** Flex-rigid rule: May fail, returns null if not sucessful. */
  private final def flexrigid(idx1: Int, ty1: Type, args1: Seq[Either[Term, Type]], rigidHd: Term, rigidArgs: Seq[Either[Term, Type]], rigidAsTerm: Term, vargen: FreshVarGen, depth: Seq[Type]): (PartialUniResult, Seq[UEq]) = {
    import leo.datastructures.partitionArgs
    try {
      val args10 = args1.map(_.left.get)
      // This is a bit hacky: We need the new fresh variables
      // introduced by partialBinding(...), so we just take the
      // difference of vars in vargen (those have been introduced).
      // Maybe this should be done better...
      val varsBefore = vargen.existingVars

      if (rigidHd.isVariable) {
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
        assert(rigidHd.isConstant)
        // Constants may be polymorphic: Apply types before calculating imitation binding.
        val rigidArgs0 = partitionArgs(rigidArgs)
        assert(rigidArgs0._1.isEmpty || rigidHd.ty.isPolyType)
        val rigidHd0 = if (rigidHd.ty.isPolyType) {
          leo.Out.finest(s"head symbol is polymorphic")
          Term.local.mkTypeApp(rigidHd, rigidArgs0._1)}
        else
          rigidHd
        val binding = partialBinding(vargen, ty1, rigidHd0)
        leo.Out.finest(s"binding: $idx1 -> ${binding.pretty}")
        val varsAfter = vargen.existingVars
        val subst = Subst.singleton(idx1, binding)
        // new equations:
        val newVars = newVarsFromGenerator(varsBefore, varsAfter).reverse // reverse since highest should be the last
        assert(newVars.size == rigidArgs0._2.size)
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

  ///////////////////////////////////////
  // Pattern predicate
  ///////////////////////////////////////

  /** Returns true iff `t` is a higher-order pattern. Input must be in beta-normal form. */
  final def isPattern(t: Term): Boolean = isPattern0(t, 0)

  @tailrec
  final private def isPattern0(t: Term, depth: Int): Boolean = {
    import leo.datastructures.Term.{:::>, TypeLambda, Bound, ∙}
    t match {
      case _ :::> body => isPattern0(body, depth+1)
      case TypeLambda(body) => isPattern0(body, depth)
      case head ∙ args => head match {
        case Bound(_, idx) if idx > depth => /* head is free var, check args */
          checkDistinctBound(args, depth)
        case _ => /* head is not a free var, recurse on args */
          allPattern(args, depth)
      }
      case _ => true
    }
  }

  @tailrec
  final private def allPattern(ts: Seq[Either[Term, Type]], depth: Int): Boolean = {
    if (ts.isEmpty) true
    else {
      val t = ts.head
      if (t.isRight) allPattern(ts.tail, depth)
      else {
        if (!isPattern0(t.left.get, depth))
          false /* fail fast if any argument
              is not a pattern */
        else allPattern(ts.tail, depth)
      }
    }
  }

  /** Check if all terms in args are distinct bound variables. Bound if scope <= depth
    * (or eta-equivalent). */
  final private def checkDistinctBound(args: Seq[Either[Term, Type]], depth: Int): Boolean =
    checkDistinctBound0(args, depth, Set())

  @tailrec
  final private def checkDistinctBound0(args: Seq[Either[Term, Type]], depth: Int, used: Set[Int]): Boolean = {
    import leo.datastructures.Term.{Bound, :::>}
    import leo.datastructures.getVariableModuloEta
    if (args.isEmpty) true
    else {
      val arg = args.head
      if (arg.isRight) false /*Free variable dont have type argument, do they? (rank-1 poly)*/
      else {
        val termArg = arg.left.get
        termArg match {
          case Bound(_, idx) if idx <= depth => /* Simple bound variable */
            if (!used.contains(idx))
              checkDistinctBound0(args.tail, depth, used + idx)
            else false
          case _ :::> _ => /* Maybe eta expanded bound variable */
            val possiblyBoundVar = getVariableModuloEta(termArg)
            if (possiblyBoundVar <= 0 || possiblyBoundVar > depth) /* error, not a bound variable */
              false
            else
              if (!used.contains(possiblyBoundVar))
                checkDistinctBound0(args.tail, depth, used + possiblyBoundVar)
              else false
          case _ => false
        }
      }
    }
  }
}



trait TypeUnification {
  /** A `UEq` is an unsolved equation. */
  type UEq = (Type, Type)
  type TypeSubst = Subst

  /** Returns Some(σ) where σ = mgu(t,s) if such a substitution exists, None otherwise. */
  def unify(t: Type, s: Type): Option[TypeSubst] = unify(Vector((t,s)))

  /** Returns Some(σ) where σ = mgu({t_i,s_i}) if such a substitution exists, None otherwise. */
  def unify(constraints: Seq[UEq]): Option[TypeSubst]
}

object TypeUnification {
  private val impl: TypeUnification = TypeUniImpl

  /** Returns Some(σ) where σ = mgu(t,s) if such a substitution exists, None otherwise. */
  final def apply(t: Type, s: Type): Option[TypeUnification#TypeSubst] = impl.unify(t,s)
  /** Returns Some(σ) where σ = mgu({t_i,s_i}) if such a substitution exists, None otherwise. */
  final def apply(constraints: Seq[TypeUnification#UEq]): Option[TypeUnification#TypeSubst] = impl.unify(constraints)
}

object TypeUniImpl extends TypeUnification {
  import leo.modules.calculus.HuetsPreUnification.applySubstToList
  /** Returns Some(σ) where σ = mgu({t_i,s_i}) if such a substitution exists, None otherwise. */
  def unify(constraints: Seq[UEq]): Option[TypeSubst] = tyDetExhaust(constraints, Subst.id)

  @tailrec
  final protected[calculus] def tyDetExhaust(uTyProblems: Seq[UEq], unifier: TypeSubst): Option[TypeSubst] = {
    leo.Out.finest(s"Unsolved type equations: ${uTyProblems.map(ueq => ueq._1.pretty + " = " + ueq._2.pretty).mkString(" , ")}")
    if (uTyProblems.nonEmpty) {
      val head = uTyProblems.head
      leo.Out.finest(s"Taken: ${head._1.pretty} = ${head._2.pretty}")
      if (TyDeleteRule.canApply(head)) {
        leo.Out.finest(s"Apply delete")
        tyDetExhaust(uTyProblems.tail, unifier)
      } else if (TyDecompRule.canApply(head)) {
        leo.Out.finest(s"Apply Decomp")
        tyDetExhaust(TyDecompRule.apply(head) ++ uTyProblems.tail, unifier)
      } else {
        val tyFunDecompRuleCanApplyHint = TyFunDecompRule.canApply(head)
        if (tyFunDecompRuleCanApplyHint != TyFunDecompRule.CANNOT_APPLY) {

          tyDetExhaust(TyFunDecompRule.apply(head, tyFunDecompRuleCanApplyHint) ++ uTyProblems.tail,unifier)
        } else if (TyBindRule.canApply(head)) {
          leo.Out.finest(s"Apply bind")
          val bindResult = TyBindRule.apply(head)
          leo.Out.finest(s"Bind result: ${bindResult.pretty}")
          val newUnsolved = applyTySubstToList(bindResult,uTyProblems.tail)
          tyDetExhaust(newUnsolved, unifier.comp(bindResult))
        } else None
      }
    } else {
      leo.Out.finest(s"Solved: ${unifier.pretty}")
      Some(unifier)
    }
  }

  @inline private final def applyTySubstToList(typeSubst: TypeSubst, list: Seq[UEq]): Seq[UEq] =
    list.map {case (l,r) => (l.substitute(typeSubst), r.substitute(typeSubst))}


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

    final def apply(e: UEq, hint: Int): Seq[UEq] = {
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

    final def canApply(e: UEq): Int = {
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
    final def apply(e: UEq): Subst = {
      val leftIsTypeVar = e._1.isBoundTypeVar

      val tyVar = if (leftIsTypeVar) BoundType.unapply(e._1).get else BoundType.unapply(e._2).get
      val otherTy = if (leftIsTypeVar) e._2 else e._1

      Subst.singleton(tyVar, otherTy)
    }

    final def canApply(e: UEq): Boolean = {
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
}