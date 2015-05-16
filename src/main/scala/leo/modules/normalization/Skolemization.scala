package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.blackboard.FormulaStore
import leo.datastructures.impl.Signature
import Term._

import leo.datastructures._

/**
 *
 * Takes a Formula in Negation Normal Form and Computes an
 * existential free representation.
 *
 * @author Max Wisniewski
 * @since 6/16/14
 *
 */
object Skolemization extends AbstractNormalize{

  override val name : String = "Skolemization"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * IMPORTANT: Does only work after NegationNormal form, since polarity is not considered here.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula : Clause) : Clause = {
    formula.mapLit(_.termMap(internalNormalize(_)))
  }

  private def internalNormalize(formula: Term): Term = {
    val mini = miniscope(formula)
    val r = skolemize(mini, Set())
    r
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
  private def skolemize(formula : Term, looseBounds: Set[Term]) : Term = formula match {
      //Remove exist quantifier
      // TODO: Raising Bound variables is borken. Fix it.
    case Exists(s@(ty :::> t))  =>
//      println("step: freevars: "+s.freeVars.map{_.pretty}.mkString(","))
//      println("step: looseBounds: " + looseBounds.map{_.pretty}.mkString(","))
      val fvs = (s.freeVars diff looseBounds).toSeq
//      println("freevars im skolemization: " + fvs.map(_.pretty).mkString(","))
      val fv_types = fvs.map(_.ty)
      import leo.datastructures.impl.Signature
      val skConst = Term.mkAtom(Signature.get.freshSkolemVar(Type.mkFunType(fv_types, ty)))
      val skTerm = Term.mkTermApp(skConst, fvs)

      var sub: Map[Int, Term] = Map(1 -> skTerm)
      val lBIt = looseBounds.iterator
      while (lBIt.hasNext) {
        val b = lBIt.next()
        val (ty, sc) = Bound.unapply(b).get
        sub = sub + (sc+1 -> mkBound(ty, sc))
      }
      val norm = t.closure(Subst.fromMap(sub)).betaNormalize
//      println("step in skolemization: " + norm.pretty)

      skolemize(norm, looseBounds)
      // Pass through

    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args   => Term.mkApp(skolemize(f, looseBounds), args.map(_.fold({t => Left(skolemize(t, looseBounds))},(Right(_)))))
    case ty :::> s  => mkTermAbs(ty, skolemize(s, looseBounds.map{case Bound(ty, sc) => mkBound(ty, sc+1)} + mkBound(ty, 1)))
    case TypeLambda(t) => mkTypeAbs(skolemize(t, looseBounds))
//    case _  => formula
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
      case s@Bound(_,_)           => s
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
      case s@Bound(_,_)           => s
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

  override def applicable(status : Int): Boolean = (status & 15) == 7

  def markStatus(fs : FormulaStore) : FormulaStore = fs.newStatus(fs.status | 15)
}
