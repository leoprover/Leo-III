package leo.modules

import leo.Out
import leo.datastructures.{Clause, Kind, Literal, Signature, Subst, Term, Type, mkPolyTermLambdaAbs, mkPolyTypeLambdaAbs}
import leo.modules.HOLSignature.Not
import leo.modules.output.SuccessSZS

/**
  * Collection of relevant types and functions
  * for various calculus-related procedures.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 20.05.15
  */
package object calculus {

  type TypeSubst = Subst
  type TermSubst = Subst

  /**
    * Base type for calculus rules wrapped in objects/classes.
    * By extending this trait, the rule can be passed the proof output
    * methods (for logging the inference steps).
    */
  trait CalculusRule {
    def name: String
    def inferenceStatus: SuccessSZS
  }

  ///////////////////////////////////
  /// Fresh variable generation for clauses
  ////////////////////////////////

  /**
    * A `FreshVarGen` is a stateful object which keeps track of the already
    * used (free) variables of an object (most probably used for [[leo.datastructures.Clause]]s
    * and allows generating new fresh variables with respect to that object's "free var state".
    */
  trait FreshVarGen extends Function1[Type, Term] {
    /** Returns a fresh variable wrt. the context of this generator. */
    def apply(ty: Type): Term = Term.mkBound(ty, next(ty)._1)
    /** Returns a fresh type variable wrt. the context of this generator. */
    def apply(): Type = Type.mkVarType(next())
    /** Returns a fresh variable represented as its loose de-bruijn index
      *  wrt. the context of this generator. */
    def next(ty: Type): (Int, Type)
    /** Returns a fresh type variable represented as its loose de-bruijn index
      * wrt. the context of this generator. */
    def next(): Int
    /** Adds `vars` to the recorded variables. Subsequent calls to
      * methods of this variable generator will consider `vars` as already
      * existent variables. */
    def addVars(vars: Traversable[(Int, Type)]): Unit
    /** Return all already used variables within the context of this generator.
      * The "newest" variable is the head of the list.
      * @example If `f` is a FreshVarGen for clause `cl`, then
      *          `f.existingVars == cl.implicitlyBound`
      *          he list of all free variables of a clause*/
    def existingVars: Seq[(Int, Type)]
    /** Return all already used type variable of the generator, e.g.
      * all implicitly universally quantified of the clause's context. */
    def existingTyVars: Seq[Int]
    def lift(n: Int): Unit

    /** Returns a copy of the underlying FreshVarGen */
    def copy: FreshVarGen
  }

  /** Create a [[FreshVarGen]] with the free var context of the clause `cl`. */
  @inline final def freshVarGen(cl: Clause): FreshVarGen = freshVarGen0(cl.implicitlyBound, cl.typeVars, Clause.maxImplicitlyBound(cl))
  /** Create a [[FreshVarGen]] without any so-far registered free vars. */
  @inline final def freshVarGenFromBlank: FreshVarGen = freshVarGen0(Seq(), Seq(), 0)

  final private def freshVarGen0(variables:  Seq[(Int, Type)], tyVariables: Seq[Int], curVar: Int): FreshVarGen = new FreshVarGen {
    private var cur = curVar
    private var curTy = if (tyVariables.nonEmpty) tyVariables.max else 0
    private var vars: Seq[(Int, Type)] = variables
    private var tyVars: Seq[Int] = tyVariables

    override final def next(ty: Type): (Int, Type) = {
      cur = cur + 1
      vars = (cur, ty) +: vars
      (cur, ty)
    }
    override final def next(): Int = {
      curTy = curTy + 1
      tyVars = curTy +: tyVars
      curTy
    }

    override final def addVars(variables: Traversable[(Int, Type)]): Unit = {
      val newVars = (variables.toSeq ++ vars).distinct
      vars = newVars.sortWith { case (l,r) =>
        l._1 > r._1
      }
      if (newVars.nonEmpty) cur = newVars.maxBy(_._1)._1
    }

    override final def lift(n: Int): Unit = {
      cur = cur + n
      vars = vars.map {case (i,t) => (i+n,t)}
    }

    override final def existingVars: Seq[(Int, Type)] = vars
    override final def existingTyVars: Seq[Int] = tyVars
    override final def copy: FreshVarGen = freshVarGen0(vars, tyVars, cur)
  }

  // Adopted from tomer's code:
  // n is arity of variable
  // m is arity of head
  // hdSymb is head
  // y1,..,yn are new bound variable
  // x1,..,xm are new free variables
  final def partialBinding(varGen: FreshVarGen, typ: Type, hdSymb: Term): Term = {
    // if typ = t1 -> t2 -> ... -> tn -> t(n+1) (where t(n+1) is not a function type)
    // then yTypes is (t1,t2,t3,..., tn)
    val yTypes = typ.funParamTypes
    // y1 ... yn new bound variables
    // yi has type ti
    // yi has de-Bruijn index (n-i)+1, i.e. y1:t1 has de-Bruijn index n and so on,
    // since they are applied as  xi y1 y2 y3 ... yn for each 1 <= i <= m
    // hence to keep type/parameter order y1 binds to the outermost lambda.
    val ys = yTypes.zip(List.range(1,typ.arity+1).reverse).map(p => Term.mkBound(p._1,p._2))
    // we need as many new free variables as arguments required by hdsymb
    // i.e. if hdSymb.ty = u1 -> u2 -> ... -> um -> u(m+1) where u(n+1) is not a function type, then
    // xs = (x1, ..., xm) where xi.ty = ui
    val xs =
      if (ys.isEmpty)
        hdSymb.ty.funParamTypes.map(p => varGen(p))
      else {
        hdSymb.ty.funParamTypes.map(p =>
          // We need to lift each new free variable by ys.size since we create new lambda binders around them
          // in the last step
          Term.mkTermApp({val i = varGen.next(Type.mkFunType(yTypes,p));Term.mkBound(i._2,i._1+ys.size)}, ys))
      }
    val t = Term.mkTermApp(hdSymb,xs)
    val aterm = Term.λ(yTypes)(t)
    aterm.etaExpand
  }

  /**
    *
    */
  final def normalizeType(ty: Type, tyFVs: Seq[Int]): Type = {
    if (tyFVs.isEmpty) ty
    else {
      val tyFVs0 = tyFVs.distinct.sortWith {case (a,b) => a > b}
      if (tyFVs0.head == tyFVs0.size) ty
      else {
        val newTyFvs = Seq.range(tyFVs0.size, 0, -1)
        val tySubst = Subst.fromShiftingSeq(tyFVs.zip(newTyFvs))
        ty.substitute(tySubst)
      }
    }
  }

  def normalizeFVs(fvs0: Seq[Int]): Option[Subst] = {
    val fvs = fvs0.distinct.sorted(Ordering.Int.reverse)
    if (fvs.nonEmpty && fvs.size != fvs.head) {
      Out.finest(s"FV Optimization needed")
      Out.finest(s"Old: \t${fvs.mkString("-")}")
      val newFvs = Seq.range(fvs.size, 0, -1)
      val subst = Subst.fromShiftingSeq(fvs.zip(newFvs))
      Out.finest(s"New: \t${newFvs.mkString("-")} ... subst: ${subst.pretty}")
      Some(subst)
    } else None
  }

  def normalizeTyFVs(tyFvs0: Seq[Int]): Option[Subst] = {
    val tyFvs = tyFvs0.distinct.sorted(Ordering.Int.reverse)
    if (tyFvs.nonEmpty && tyFvs.size != tyFvs.head) {
      Out.finest(s"Ty FV Optimization needed")
      Out.finest(s"Old: \t${tyFvs.mkString("-")}")
      val newTyFvs = Seq.range(tyFvs.size, 0, -1)
      val tySubst = Subst.fromShiftingSeq(tyFvs.zip(newTyFvs))
      Out.finest(s"New: \t${newTyFvs.mkString("-")} ... subst: ${tySubst.pretty}")
      Some(tySubst)
    } else None
  }

  final def skTerm(goalTy: Type, fvs: Seq[(Int, Type)], tyFvs: Seq[Int])(implicit sig: Signature): Term = {
    val funTy = normalizeType(Type.mkFunType(fvs.map(_._2), goalTy), tyFvs)
    val ty = mkPolyTyAbstractionType(tyFvs.size,funTy)
    assert(ty.typeVars.isEmpty,
      s"SK symbol type has free type vars:\n" +
        s"ty: ${ty.pretty(sig)},\n" +
        s"free ty vars: ${ty.typeVars.map(_.pretty(sig)).mkString(",")}\n" +
        s"goalTy: ${goalTy.pretty(sig)},\n" +
        s"fvs: ${fvs.map(fv => s"(${fv._1},${fv._2.pretty(sig)})").mkString(",")},\n" +
        s"tyFvs: ${tyFvs.toString()},\n" +
        s"normalized funTy: ${funTy.pretty(sig)}")
    val skFunc = Term.mkAtom(sig.freshSkolemConst(ty))
    val intermediate = Term.mkTypeApp(skFunc, tyFvs.map(Type.mkVarType))
    val result = Term.mkTermApp(intermediate, fvs.map {case (i,t) => Term.mkBound(t,i)})
    assert(Term.wellTyped(result), s"skTerm Result not well-typed: ${result.pretty(sig)}\n" +
      s"% skFunc: ${skFunc.pretty}, type: ${skFunc.ty.pretty(sig)}")
    result
  }
  /** Create a defined Skolem symbol for the existential/negative-universal case and return
    * its application to the current context (type and term vars).
    *
    * @param a               the λ-abstraction of the quantified formula body: a ≡ (ty :::> body)
    * @param fvs             current (de-Bruijn) term free vars in scope, in the *same order* as used in CNF (params)
    * @param tyFVs           current type free vars in scope
    * @param negatePredicate boolean indicating whether the term originates from a negated universal quantification
    */

  final def skTermDefined(a: Term, fvs: Seq[(Int, Type)], tyFvs: Seq[Int], negatePredicate: Boolean = false)(implicit sig: Signature): Term = {
    import leo.datastructures.Term.:::>
    import leo.datastructures.Term._
    import leo.modules.HOLSignature.{Choice}

    val (goalTy, body0) = a match {
      case ty0 :::> body => (ty0, body)
      case _ => throw new IllegalArgumentException("Expected λ-abstraction for quantified body")
    }

    // Construct the type of the fresh Skolem function
    val funTy = normalizeType(Type.mkFunType(fvs.map(_._2), goalTy), tyFvs)
    val skTy = mkPolyTyAbstractionType(tyFvs.size, funTy)

    // Construct the definition of the fresh Skolem function
    val maybeNegated = if (negatePredicate) Choice(mkTermAbs(goalTy, Not(body0))) else Choice(a)

    // Close potential gaps in the free variables
    val maybeSubst = normalizeFVs(fvs.map(_._1))
    val substTerm0 = maybeSubst match {
      case Some(subst) => maybeNegated.substitute(subst)
      case _ => maybeNegated
    }

    // Abstract over the free variables
    val abstracted0 = mkPolyTermLambdaAbs(fvs.map(_._2), substTerm0)

    // Close potential gaps in the free type-variables
    val maybeTySubst = normalizeTyFVs(tyFvs)
    val substTerm = maybeTySubst match {
      case Some(tySubst) => abstracted0.substitute(Subst.id, tySubst)
      case _ => abstracted0
    }

    // Abstract over the free type-variables
    val abstracted = mkPolyTypeLambdaAbs(tyFvs.length, substTerm)

    // return the fresh Skolem symbol with context variables applied
    val skKey = sig.freshSkolemConst(skTy, Some(abstracted), Signature.PropNoProp)
    val skFunc = Term.mkAtom(skKey)
    val typeApps = Term.mkTypeApp(skFunc, tyFvs.map(Type.mkVarType))
    val termApps = Term.mkTermApp(typeApps, fvs.map { case (i, ty) => mkBound(ty, i) })
    assert(Term.wellTyped(termApps), s"skTerm Result not well-typed: ${termApps.pretty(sig)}\n" +
      s"% skFunc: ${skFunc.pretty}, type: ${skFunc.ty.pretty(sig)}")
    termApps
  }
  final def skType(tyFvs: Seq[Int])(implicit sig: Signature): Type = {
    val freshTypeOp: Signature.Key = sig.freshSkolemTypeConst(mkPolyKindAbstraction(tyFvs.size))
    Type.mkType(freshTypeOp, tyFvs.map(Type.mkVarType))
  }

  final def mkPolyTyAbstractionType(count: Int, body: Type): Type = if (count <= 0) body
  else Type.mkPolyType(mkPolyTyAbstractionType(count-1, body))
  final private def mkPolyKindAbstraction(count: Int): Kind = if (count <= 0) Kind.*
  else Kind.mkFunKind(Kind.*, mkPolyKindAbstraction(count-1))

  /** Checks whether the terms `s` and `t` may be unifiable by a simple syntactic over-approximation.
    * Hence, if {{{!mayUnify(s,t)}}} the terms are not unifiable, otherwise they may be. */
  @inline final def mayUnify(s: Term, t: Term): Boolean = mayUnify0(s,t,5)

  final protected def mayUnify0(s: Term, t: Term, depth: Int): Boolean = {
    if (s == t) return true
    if (s.ty.typeVars.isEmpty && t.ty.typeVars.isEmpty) {
      if (s.ty != t.ty) return false
    } else {
      if (!mayUnify(s.ty, t.ty)) return false
    }
    if (s.freeVars.isEmpty && t.freeVars.isEmpty) return false // contains to vars, cannot be unifiable
    if (depth <= 0) return true

    // Match case on head symbols (over approximation):
    // flex-flex always works*, flex-rigid also works*, rigid-rigid only in same symbols
    // * = if same type
    import leo.datastructures.Term._
    (s,t) match {
      case (Symbol(id1), Symbol(id2)) => id1 == id2 // rigid-rigid
      case (Bound(_,_), _) => true // flex-
      case (_, Bound(_,_)) => true // -flex
      case (_ :::> body1, _ :::> body2) => mayUnify0(body1, body2, depth)
      case (TypeLambda(s2), TypeLambda(t2)) => mayUnify0(s2, t2, depth)
      case (Bound(_,_) ∙ _, _) => true //flex-head, need to assume that it works
      case (_, Bound(_,_) ∙ _) => true // ditto
      case (f1 ∙ args1, f2 ∙ args2) if mayUnify(f1.ty, f2.ty) && args1.length == args2.length => mayUnify0(f1, f2, depth -1) && args1.zip(args2).forall{_ match {
        case (Left(t1), Left(t2)) => mayUnify0(t1, t2, depth -1)
        case (Right(ty1), Right(ty2)) => mayUnify(ty1,ty2)
        case _ => false
      } }
      case _ => false
    }
  }

  /** Checks whether the types `s` and `t` may be unifiable by a simple syntactic over-approximation.
    * Hence, if {{{!mayUnify(s,t)}}} the types are not unifiable, otherwise they may be. */
  @inline final def mayUnify(s: Type, t: Type): Boolean = {
    if (s == t) return true
    if (s.typeVars.isEmpty && t.typeVars.isEmpty) return false
    import leo.datastructures.Type._
    (s,t) match {
      case (BaseType(id1), BaseType(id2)) => id1 == id2
      case (a -> b, c -> d) => mayUnify(a,c) && mayUnify(b,d)
      case (ProductType(tys1), ProductType(tys2)) => tys1.size == tys2.size && tys1.zip(tys2).forall(pair => mayUnify(pair._1, pair._2))
      case (∀(a), ∀(b)) => mayUnify(a,b)
      case (BoundType(_), ∀(_)) => false
      case (BoundType(_), _) => true
      case (∀(_), BoundType(_)) => false
      case (_, BoundType(_)) => true
      case (ComposedType(id1, args1), ComposedType(id2, args2)) if id1 == id2 => args1.zip(args2).forall(ts => mayUnify(ts._1, ts._2))
      case _ => false
    }
  }


  /** Checks whether the terms `s` and `t` may be matchable (s onto t) by a simple syntactic over-approximation.
    * Hence, if {{{!mayMatch(s,t)}}} the terms are not matchable, otherwise they may be. */
  @inline final def mayMatch(s: Term, t: Term): Boolean = mayMatch0(s,t,5)

  final protected def mayMatch0(s: Term, t: Term, depth: Int): Boolean = {
    if (s == t) return true
    if (s.ty.typeVars.isEmpty) {
      if (s.ty != t.ty) return false
    } else {
      if (!mayMatch(s.ty, t.ty)) return false
    }
    if (s.freeVars.isEmpty) return false // contains to vars, cannot be unifiable TODO: Is this right?
    if (depth <= 0) return true
    //    if (s.headSymbol.ty != t.headSymbol.ty) return false

    // Match case on head symbols (over approximation):
    // flex-flex always works*, flex-rigid also works*, rigid-rigid only in same symbols
    // * = if same type
    import leo.datastructures.Term._
    (s,t) match {
      case (Symbol(id1), Symbol(id2)) => id1 == id2 // rigid-rigid
      case (Bound(_,_), _) => true // flex-
      case (_ :::> body1, _ :::> body2) => mayMatch0(body1, body2, depth)
      case (TypeLambda(s2), TypeLambda(t2)) => mayMatch0(s2, t2, depth)
      case (Bound(_,_) ∙ _, _) => true //flex-head, need to assume that it works
      case (f1 ∙ args1, f2 ∙ args2) if mayMatch(f1.ty, f2.ty) && args1.length == args2.length => mayMatch0(f1, f2, depth -1) && args1.zip(args2).forall{_ match {
        case (Left(t1), Left(t2)) => mayMatch0(t1, t2, depth -1)
        case (Right(ty1), Right(ty2)) => mayMatch(ty1,ty2)
        case _ => false
      } }
      case _ => false
    }
  }

  /** Checks whether the types `s` and `t` may be matchable (s onto t) by a simple syntactic over-approximation.
    * Hence, if {{{!mayMatch(s,t)}}} the types are not matchable, otherwise they may be. */
  @inline final def mayMatch(s: Type, t: Type): Boolean = {
    if (s == t) return true
    import leo.datastructures.Type._
    (s,t) match {
      case (BaseType(id1), BaseType(id2)) => id1 == id2
      case (a -> b, c -> d) => mayMatch(a,c) && mayMatch(b,d)
      case (ProductType(tys1), ProductType(tys2)) => tys1.size == tys2.size && tys1.zip(tys2).forall(pair => mayMatch(pair._1, pair._2))
      case (∀(a), ∀(b)) => mayMatch(a,b)
      case (BoundType(_), _) => true
      case (ComposedType(id1, args1), ComposedType(id2, args2)) if id1 == id2 => args1.zip(args2).forall(ts => mayMatch(ts._1, ts._2))
      case _ => false
    }
  }

  final def uniqueFVTypes(cl: Clause): Boolean = {
    val fvs = cl.implicitlyBound
    fvs.size == fvs.toSet.size
  }

  @inline final def isPattern(t: Term): Boolean = PatternUnification.isPattern(t)
  @inline final def isPattern(l: Literal): Boolean = isPattern(l.left) && isPattern(l.right)
  @inline final def isPattern(cl: Clause): Boolean = cl.lits.forall(isPattern)
}
