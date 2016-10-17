package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures._
import leo.datastructures.impl.SignatureImpl
import leo.modules.HOLSignature.{LitFalse, LitTrue, Forall, o, Exists, |||, &}

/**
 *
 * Takes a Formula in Negation Normal Form and Computes an
 * existential free representation.
 *
 * @author Max Wisniewski
 * @since 6/16/14
 *
 */
object Skolemization extends Normalization{

  override val name : String = "skolemize"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * IMPORTANT: Does only work after NegationNormal form, since polarity is not considered here.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def apply(formula : Clause)(implicit sig: Signature) : Clause = {
    val fv: Set[Term] = formula.implicitlyBound.map{case (v,ty) => Term.mkBound(ty,v)}.toSet
    formula.mapLit(_.termMap {case (l,r) => (l,r) match {
      case (l1, LitTrue())  => (internalNormalize(l1,fv)(sig), LitTrue())
      case (l1, LitFalse()) => (internalNormalize(l1,fv)(sig), LitFalse())
      case _  => (l,r)
    }})
  }

  def apply(literal : Literal)(implicit sig: Signature) : Literal = {
    apply(Clause(literal))(sig).lits.head
  }

  def normalize(t: Term)(implicit sig: Signature): Term = {val fv: Set[Term] = Set() // TODO FIXME
    internalNormalize(t, fv)(sig)
  }

  private def internalNormalize(formula: Term, fV: Set[Term])(sig: Signature): Term = {
    val mini = miniscope(formula)
    val r = skolemize(mini, fV.toSeq)(sig)
    r.betaNormalize
  }

  /**
   *
   *
   * For each exists quantified Term
   * (Exists(\x. t)) we replace x by a quantifier
   *
   * @param formula
   * @return
   */
  private def skolemize(formula : Term, univBounds: Seq[Term])(sig: Signature) : Term = {
    formula match {
      //Remove exist quantifier
      // TODO: Raising Bound variables is borken. Fix it.
      case Exists(s@(ty :::> t))  =>
        val fvs = univBounds //(s.freeVars diff looseBounds).toSeq
      val fv_types = fvs.map(_.ty)
        val skConst = Term.mkAtom(sig.freshSkolemConst(Type.mkFunType(fv_types, ty)))(sig)
        val skTerm = Term.mkTermApp(skConst, fvs)


        var sub: Map[Int, Int] = Map()
        val lBIt = s.looseBounds.iterator
        while (lBIt.hasNext) {
          val b = lBIt.next()
          sub = sub + (b+1 -> b)
        }

        val norm = t.termClosure(Subst.fromMaps(Map(1 -> skTerm),sub)).betaNormalize

        skolemize(norm, univBounds)(sig)
      case Forall(ty :::> t) => Forall(mkTermAbs(ty,skolemize(t, univBounds.map{case Bound(ty, sc) => mkBound(ty, sc+1)} :+ mkBound(ty, 1))(sig)))

      case Symbol(k) ∙ args if !sig.allUserConstants.contains(k) && sig(k).ty.fold(false){ty => ty == o ->: o || ty == o ->: o ->: o}
        => // The symbol is a boolean connective, not defined by the user.
        Term.mkApp(Term.mkAtom(k)(sig), args.map(_.fold({t => Left(skolemize(t, univBounds)(sig))},Right(_))))

      // Reaching any non boolean connective we will stop, since we can no longer distinquish positive from negative equalities
      case term      => term
      //    case _  => formula
    }
  }

  /**
   *
   * Moves a quantifier inward, such that the computed skolemterm
   * does only depend on the minimum amount of variables.
   *
   * @param formula - That will be skolemmized
   * @return - The formula with quantifiers most inward
   */
  private def miniscope(formula : Term) : Term = formula match {
      //First Case, one side is not bound, in AND
    case Exists (ty :::> t) => miniscope(t) match {
      case (t1 & t2) if !Simplification.isBound(t2) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t2)))
        &(left,right)
       case (t1 & t2) if !Simplification.isBound(t1) =>
          val right = miniscope(Exists(mkTermAbs(ty,t2)))
          val left = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t1)))
          &(left,right)
      case (t1 ||| t2) if !Simplification.isBound(t2) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t2)))
        |||(left,right)
      case (t1 ||| t2) if !Simplification.isBound(t1) =>
        val right = miniscope(Exists(mkTermAbs(ty,t2)))
        val left = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t1)))
        |||(left,right)
      case (t1 ||| t2) =>
        val left = miniscope(Exists(mkTermAbs(ty,t1)))
        val right = miniscope(Exists(mkTermAbs(ty,t2)))
        |||(left,right)
      // In neither of the above cases, move inwards
      case s@Symbol(_)            => s
      case s@Bound(_,i)           => if(i == 1) LitTrue() else s
      case f ∙ args   => Exists(\(ty)(Term.mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},(Right(_)))))))
      case ty :::> s  => Exists(\(ty)(mkTermAbs(ty, miniscope(s))))
      case TypeLambda(t) => Exists(\(ty)(mkTypeAbs(miniscope(t))))
//      case _  => formula
    }

      //Same for Forall
    case Forall (ty :::> t) => miniscope(t) match {
      //First Case, one side is not bound, in AND
      case (t1 & t2) if !Simplification.isBound(t2) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t2)))
        &(left,right)
      case (t1 & t2) if !Simplification.isBound(t1) =>
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        val left = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t1)))
        &(left,right)
      //Second Case, one side is not bound in OR
      case (t1 ||| t2) if !Simplification.isBound(t2) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t2)))
        |||(left,right)
      case (t1 ||| t2) if !Simplification.isBound(t1) =>
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        val left = miniscope(Simplification.removeUnbound(mkTermAbs(ty,t1)))
        |||(left,right)
      // Both are bound, and it is a OR
      case (t1 & t2) =>
        val left = miniscope(Forall(mkTermAbs(ty,t1)))
        val right = miniscope(Forall(mkTermAbs(ty,t2)))
        &(left,right)
      // In neither of the above cases, move inwards
      case s@Symbol(_)            => s
      case s@Bound(_,i)           => if(i == 1) LitFalse() else s
      case f ∙ args   => Forall(\(ty)(Term.mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},(Right(_)))))))
      case ty :::> s  => Forall(\(ty)(mkTermAbs(ty, miniscope(s))))
      case TypeLambda(t) => Forall(\(ty)(mkTypeAbs(miniscope(t))))
//      case _  => formula
    }

      // In neither of the above cases, move inwards
    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args   => Term.mkApp(miniscope(f), args.map(_.fold({t => Left(miniscope(t))},(Right(_)))))
    case ty :::> s  => mkTermAbs(ty, miniscope(s))
    case TypeLambda(t) => mkTypeAbs(miniscope(t))
//    case _  => formula

  }
}
