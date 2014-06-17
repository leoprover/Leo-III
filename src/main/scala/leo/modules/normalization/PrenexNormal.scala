package leo.modules.normalization

import leo.datastructures.internal._
import leo.datastructures.internal.Term._
import Simplification.isBound

/**
 * Computes for a Skolemized Term the Prenex Normal Form
 *
 * @author Max Wisniewski
 * @since 6/17/14
 */
object PrenexNormal extends AbstractNormalize {

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = formula match {

     // TODO : Trailing normalize should be replaced, by a lookup to move all other quantifiers out
    case &(Forall (ty :::> t), s)   =>
      val t1 = normalize(t)
      val s1 = normalize(s)
      normalize(Forall(\(ty)(&(t1, incrementBound(s1,1)))))
    case &(t, Forall (ty :::> s))   =>
      val t1 = normalize(t)
      val s1 = normalize(s)
      normalize(Forall(\(ty)(&(incrementBound(t1,1), s1))))
    case |||(Forall (ty :::> t), s)   =>
      val t1 = normalize(t)
      val s1 = normalize(s)
      normalize(Forall(\(ty)(&(t1, incrementBound(s1,1)))))
    case |||(t, Forall (ty :::> s))   =>
      val t1 = normalize(t)
      val s1 = normalize(s)
      normalize(Forall(\(ty)(&(incrementBound(t1,1), s1))))

    // TODO : Missing rules for conjunctive normal form ?

      //Pass through
    case s ::: t                => mkTermApp(normalize(s),normalize(t))
    case s :::: ty              => mkTypeApp(normalize(s),ty)
    case ty :::> t              => \(ty)(normalize(t))
    case TypeLambda(t)          => mkTypeAbs(normalize(t))
    case _                      => formula

  }


  private def incrementBound(formula : Term, i : Int) : Term = formula match {
    case s ::: t                => mkTermApp(incrementBound(s,i), incrementBound(t,i))
    case s :::: ty              => mkTypeApp(incrementBound(s,i), ty)
    case ty :::> t              => \(ty)(incrementBound(t,i+1))
    case TypeLambda(t)          => mkTypeAbs(incrementBound(t,i))
    case Bound(ty,n) if n < i  => formula
    case Bound(ty,n)            => mkBound(ty,n+1)
    case _                      => formula
  }

  /**
   * Checks whether the given formula is normalizable.
   *
   * @param formula - Formula to be checked
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(formula: Term): Boolean = true
}
