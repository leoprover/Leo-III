package leo.modules

import leo.datastructures.{Type, Term, Clause, Signature}
import leo.modules.output.SuccessSZS

/**
  * Collection of relevant types and functions
  * for various calculus-related procedures.
  *
  * @author Alexander Steen <a.steen@fu-berlin.de>
  * @since 20.05.15
  */
package object calculus {

  ///////////////////////////////////
  // Some super types for calculus rules
  ///////////////////////////////////

  /**
    * Base type for calculus rules wrapped in objects/classes.
    * By extending this trait, the rule can be passed the proof output
    * methods (for logging the inference steps).
    */
  trait CalculusRule {
    def name: String
    def inferenceStatus: Option[SuccessSZS] = None
  }

  // Probably everything is obsolete from here ...
  trait CalculusHintRule[Hint] extends CalculusRule {
    type HintType = Hint
  }

  trait UnaryCalculusRule[Res] extends (Clause => Res) with CalculusRule {
    def canApply(cl: Clause): Boolean
  }

  trait PolyadicCalculusRule[Res] extends ((Clause, Set[Clause]) => Res) with CalculusRule {
    def canApply(cl: Clause, cls: Set[Clause]): Boolean
  }

  trait UnaryCalculusHintRule[Res, Hint] extends ((Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl: Clause): (Boolean, Hint)
  }

  trait BinaryCalculusRule[Res, Hint] extends ((Clause, Clause, Hint) => Res) with CalculusHintRule[Hint] {
    def canApply(cl1: Clause, cl2: Clause): (Boolean, Hint)
  }
  // ... until here

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
    /** Returns a fresh variable represented as its loose de-bruijn index
      *  wrt. the context of this generator. */
    def next(ty: Type): (Int, Type)
    /** Return all already used variables within the context of this generator.
      * The "newest" variable is the head of the list.
      * @example If `f` is a FreshVarGen for clause `cl`, then
      *          `f.existingVars == cl.implicitlyBound`
      *          he list of all free variables of a clause*/
    def existingVars: Seq[(Int, Type)]
    /** Return all already used type variable of the generator, e.g.
      * all implicitly universally quantified of the clause's context. */
    def existingTyVars: Seq[Int]
    /**
      * Returns a copy of the underlying FreshVarGen
      */
    def copy: FreshVarGen
  }

  /** Create a [[FreshVarGen]] with the free var context of the clause `cl`. */
  @inline final def freshVarGen(cl: Clause): FreshVarGen = freshVarGen0(cl.implicitlyBound, cl.typeVars.toSeq, cl.maxImplicitlyBound)
  /** Create a [[FreshVarGen]] without any so-far registered free vars. */
  @inline final def freshVarGenFromBlank: FreshVarGen = freshVarGen0(Seq(), Seq(), 0)

  final private def freshVarGen0(variables:  Seq[(Int, Type)], tyVariables: Seq[Int], curVar: Int): FreshVarGen = new FreshVarGen {
    private var cur = curVar
    private var vars: Seq[(Int, Type)] = variables
    private val tyVars: Seq[Int] = tyVariables

    override final def next(ty: Type): (Int, Type) = {
      cur = cur + 1
      vars = (cur, ty) +: vars
      (cur, ty)
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
  final def partialBinding(varGen: FreshVarGen, typ: Type, hdSymb: Term) = {
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

  final def skTerm(goalTy: Type, fvs: Seq[(Int, Type)], tyFvs: Seq[Int])(implicit sig: Signature): Term = {
    val skFunc = Term.mkAtom(sig.freshSkolemConst(mkPolyTyAbstractionType(tyFvs.size,Type.mkFunType(fvs.map(_._2), goalTy))))
    val intermediate = Term.mkTypeApp(skFunc, tyFvs.map(Type.mkVarType))
    Term.mkTermApp(intermediate, fvs.map {case (i,t) => Term.mkBound(t,i)})
  }

  final private def mkPolyTyAbstractionType(count: Int, body: Type): Type = if (count <= 0) body
  else Type.mkPolyType(mkPolyTyAbstractionType(count-1, body))

  /** Checks whether the terms `s` and `t` may be unifiable by a simple syntactic over-approximation.
    * Hence, if {{{!mayUnify(s,t)}}} the terms are not unifiable, otherwise they may be. */
  @inline final def mayUnify(s: Term, t: Term) = mayUnify0(s,t,5)

  final protected def mayUnify0(s: Term, t: Term, depth: Int): Boolean = {
    if (s == t) return true
    if (s.ty.typeVars.isEmpty && t.ty.typeVars.isEmpty) {
      leo.Out.finest(s"mayUnify0: typevars isEmpty")
      if (s.ty != t.ty) return false
    } else {
      leo.Out.finest(s"mayUnify0: typevars not isEmpty")
      if (!mayUnify(s.ty, t.ty)) return false
    }
    if (s.freeVars.isEmpty && t.freeVars.isEmpty) return false // contains to vars, cannot be unifiable TODO: Is this right?
    if (depth <= 0) return true
//    if (s.headSymbol.ty != t.headSymbol.ty) return false

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
    import leo.datastructures.Type._
    (s,t) match {
      case (BaseType(id1), BaseType(id2)) => id1 == id2
      case (a -> b, c -> d) => mayUnify(a,c) && mayUnify(b,d)
      case (a * b, c * d) => mayUnify(a,c) && mayUnify(b,d)
      case (a + b, c + d) => mayUnify(a,c) && mayUnify(b,d)
      case (∀(a), ∀(b)) => mayUnify(a,b)
      case (BoundType(_), _) => true
      case (_, BoundType(_)) => true
      case (ComposedType(id1, args1), ComposedType(id2, args2)) if id1 == id2 => args1.zip(args2).forall(ts => mayUnify(ts._1, ts._2))
      case _ => false
    }
  }

}
