package leo.modules.calculus

import leo.datastructures.Type.BoundType
import leo.datastructures.{Subst, Term, Type}
import leo.modules.output.SZS_EquiSatisfiable

trait Unification extends CalculusRule {
  val name = "pre_uni_full"
  override val inferenceStatus = Some(SZS_EquiSatisfiable)

  /** A `UEq` is an unsolved equation. */
  type UEq = (Term, Term)
  /** A `SEq` is a solved equation. */
  type SEq = UEq

  /** `UTEq` is an unsolved type equation. */
  type UTEq = (Type, Type)
  /** `STEq` is a solved type equation. */
  type STEq = UTEq

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
  def unifyAll(vargen: FreshVarGen, constraints: Seq[UEq]): Iterable[UnificationResult]
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
  import leo.modules.calculus.util.executionModels._
  import annotation.tailrec

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
              // apply Func /* by Alex */
              val ind4 = uproblems.indexWhere(FuncRule.canApply)
              if (ind4 > -1) {
                leo.Out.finest(s"Can apply func on: ${uproblems(ind4)._1.pretty} == ${uproblems(ind4)._2.pretty}")
                detExhaust(vargen, (uproblems.take(ind4) :+ FuncRule(vargen, uproblems(ind4))) ++ uproblems.drop(ind4 + 1),
                  sproblems, uTyProblems, sTyProlems)
              }
              else {
                // none is applicable, do nothing
                (uproblems, sproblems, uTyProblems, sTyProlems)
              }
            }
          }
        }
      }
    }
  }

  /*// n is arity of variable
  // m is arity of head
  // hdSymb is head
  // y1,..,yn are new bound variable
  // x1,..,xm are new free variables
  protected[modules] def partialBinding(typ: Type, hdSymb: Term) = {
    val ys = typ.funParamTypes.zip(List.range(1,typ.arity+1)).map(p => Term.mkBound(p._1,p._2))
    val xs =
      if (ys.isEmpty)
        hdSymb.ty.funParamTypes.map(p => Term.mkFreshMetaVar(p))
      else {
        val ysTyp = Type.mkFunType(ys.map(_.ty))
        hdSymb.ty.funParamTypes.map(p => Term.mkTermApp(Term.mkFreshMetaVar(Type.mkFunType(ysTyp,p)), ys))
      }
    val t = Term.mkTermApp(hdSymb,xs)

    val aterm = Term.λ(ys.map(_.ty))(t)
    aterm.etaExpand
  }*/

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
  protected case class MyConfiguration(uproblems: Seq[UEq], sproblems: Seq[SEq], uTyProblems: Seq[UTEq], sTyProblems: Seq[STEq], result: Option[UnificationResult], isTerminal: Boolean, searchDepth: Int)
    extends Configuration[UnificationResult] {
    def this(result: Option[UnificationResult]) = this(Seq(), Seq(), Seq(), Seq(), result, true, dontcaredepth) // for success
    def this(l: Seq[UEq], s: Seq[UEq], unificationDepth: Int) = this(l, s, Seq(), Seq(), None, false, unificationDepth) // for in node
    override def toString  = "{" + uproblems.flatMap(x => ("<"+x._1.pretty+", "+ x._2.pretty+">")) + "}"
  }

  // the transition function in the search space (returned list containing more than one element -> ND step, no element -> failed branch)
  protected class MyFun(vargen: FreshVarGen) extends Function1[Configuration[UnificationResult], Seq[Configuration[UnificationResult]]] {

    import  scala.collection.mutable.ListBuffer

    // Huets procedure is defined here
    override def apply(conf2: Configuration[UnificationResult]): Seq[Configuration[UnificationResult]] = {
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

// TODO: The next stuff should be stored in some general util package
/**
 * created on: 15/04/2015
 * author: Tomer Libal
 */
package util.executionModels {
  import collection.mutable
  import annotation.tailrec

  trait Configuration[S] {
    def result: Option[S]
    def isTerminal: Boolean // terminal nodes are not added to the configuration queue
    def searchDepth: Int // depth of unification node (number of applied flex-rigid cases)
  }

  //mutable, non deterministic, stream
  abstract class NDStream[S /*result type*/ ]( val initial: Configuration[S], val myFun: Configuration[S] => Iterable[Configuration[S]] ) extends Iterable[S] with SearchAlgorithm {

    protected var MAX_DEPTH : Int = leo.Configuration.UNIFICATION_DEPTH

    type T = Configuration[S]
    private val results: mutable.Queue[S] = new mutable.Queue[S]()
    protected var hd: Option[S] = None
    protected val hdFunc: () => Option[S] = () => nextVal
    protected var terminal: Boolean = false
    protected def initDS: Unit = {
      add(initial)
      hd = hdFunc()
    }



    @tailrec
    protected final def nextVal: Option[S] = {
      val res = results.headOption
      if ( res != None ) {
        results.dequeue
        res
      } else {
        val conf = get
        if ( conf == None ) None
        else {
          val confs: Iterable[Configuration[S]] = { myFun( conf.get )}
          confs.foreach( x => {
            if ( x.result != None )
              results.enqueue( x.result.get )
            if ( !x.isTerminal && conf.get.searchDepth < MAX_DEPTH) {
              add(x)
            }
          } )
          nextVal
        }
      }
    }

    // TOFIX: iterator can only be called once right now as the ndstream is mutable!
    var wasCalled = false
    def iterator: Iterator[S] =
      if (!wasCalled) new Iterator[S] {
        wasCalled = true
        def next: S = {
          if (hd.isEmpty && terminal) throw new NoSuchElementException("Stream is empty")
          else {
            if (hd.isEmpty) {hd = hdFunc(); if (hd.isEmpty) {terminal = true;throw new NoSuchElementException("Stream is empty")} }
            val ret = hd.get
            hd = None
            ret
          }
        }
        def hasNext: Boolean = {
          if (hd.isEmpty) {
            if (terminal) false
            else {
              hd = hdFunc()
              if (hd.isEmpty) {
                terminal = true
                false
              } else
                true
            }
          }
          else true
        }
    }
    else throw new UnsupportedOperationException("iterator for NDStream can right now be called only once!")
  }

import collection.mutable.{ Queue => MQueue }
import collection.immutable.Queue
import scala.math.Ordering.Implicits._

  trait SearchAlgorithm {
    type T
    protected def initDS: Unit // called by the algorithm and implemented by some object using it as the object is initialized before the trait
    protected def add( t: T ): Unit
    protected def get: Option[T]
  }

  trait BFSAlgorithm extends SearchAlgorithm {
    private val ds: MQueue[T] = new MQueue[T]()
    initDS // if the object requires the ds to be already existing, then it will not fail now
    protected def add( conf: T ): Unit = ds += conf
    protected def get: Option[T] = {
      val res = ds.headOption
      if ( res != None ) ds.dequeue
      res
    }
  }
}
