package leo.modules.normalization

import leo.datastructures.internal._
import leo.datastructures.internal.terms._
import Term._

/**
 * Calculates the Negation Normal Form (NNF) of a term.
 *
 * @author Max Wisniewski
 * @since 6/12/14
 */
object NegationNormal extends AbstractNormalize{
  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = nnf(rmEq(formula, 1))

  private def rmEq(formula : Term, pol : Int) : Term = formula match {
    case (s <=> t) if pol == 1  => &(Impl(rmEq(s,-1),rmEq(t,1)),Impl(rmEq(t,-1),rmEq(s,1)))
    case (s <=> t) if pol == -1 => |||(&(rmEq(s,-1),rmEq(t,-1)),&(Not(rmEq(s,1)),Not(rmEq(t,1))))

    case Impl(s,t)               => Impl(rmEq(s,(-1)*pol),rmEq(t,pol))
    case Not(t)                  => Not(rmEq(t,(-1)*pol))

    case s @@@ t                => mkTermApp(rmEq(s,pol),rmEq(t,pol))
    case s @@@@ ty              => mkTypeApp(rmEq(s,pol),ty)
    case ty :::> t              => mkTermAbs(ty, rmEq(t,pol))
    case TypeLambda(t)          => mkTypeAbs(rmEq(t,pol))
    case _                      => formula
  }

  private def nnf(formula : Term) : Term = formula match {
    case Not(s & t)             =>
      val s1 = nnf(Not(nnf(s)))
      val t1 = nnf(Not(nnf(t)))
      |||(s1, t1)
    case Not(s ||| t)           =>
      val s1 = nnf(Not(nnf(s)))
      val t1 = nnf(Not(nnf(t)))
      &(s1,t1)
    case Not(Forall(ty :::> t)) =>
      val t1 = nnf(t)
      val t2 = nnf(Not(t1))
      Exists(mkTermAbs(ty, t2))
    case Not(Exists(ty :::> t)) =>
      val t1 = nnf(t)
      val t2 = nnf(Not(t1))
      Forall (mkTermAbs(ty, t2))
    case Impl(s,t)              =>
      val s1 = nnf(s)
      val t1 = nnf(t)
      |||(nnf(Not(s1)),t1)
    case Not(Not(t))            => nnf(t)

    case s @@@ t                => mkTermApp(nnf(s), nnf(t))
    case s @@@@ ty              => mkTypeApp(nnf(s), ty)
    case ty :::> t              => mkTermAbs(ty, nnf(t))
    case TypeLambda(t)          => mkTypeAbs(nnf(t))
    case x                      => x
  }

  /**
   * Checks if status bits 1,2 are raised and the third is not
   *
   * @param formula - Formula to be checked
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(formula: Term, status : Int): Boolean = (status & 7) == 3

  override def markStatus(status : Int) : Int = status | 7
}
