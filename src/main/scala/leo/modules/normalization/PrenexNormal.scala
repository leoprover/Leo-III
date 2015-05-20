package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.blackboard.{Store, FormulaStore}
import Term._
import leo.datastructures._

/**
 * Computes for a Skolemized Term the Prenex Normal Form
 *
 * @author Max Wisniewski
 * @since 6/17/14
 */
object PrenexNormal extends AbstractNormalize {

  override val name : String = "PrenexNormal"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula : Clause) : Clause = {
    formula.mapLit(_.termMap(internalNormalize(_)))
  }


  private def internalNormalize(formula: Term): Term = formula match {

     // TODO : Trailing normalize should be replaced, by a lookup to move all other quantifiers out
    case &(Forall (ty :::> t), s)   =>
      val t1 = internalNormalize(t)
      val s1 = internalNormalize(s)
      internalNormalize(Forall(\(ty)(&(t1, incrementBound(s1,1)))))
    case &(t, Forall (ty :::> s))   =>
      val t1 = internalNormalize(t)
      val s1 = internalNormalize(s)
      internalNormalize(Forall(\(ty)(&(incrementBound(t1,1), s1))))
    case |||(Forall (ty :::> t), s)   =>
      val t1 = internalNormalize(t)
      val s1 = internalNormalize(s)
      internalNormalize(Forall(\(ty)(&(t1, incrementBound(s1,1)))))
    case |||(t, Forall (ty :::> s))   =>
      val t1 = internalNormalize(t)
      val s1 = internalNormalize(s)
      internalNormalize(Forall(\(ty)(&(incrementBound(t1,1), s1))))

    // TODO : Missing rules for conjunctive normal form ?

      //Pass through
    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case s@MetaVar(_,_)         => s
    case f ∙ args               => Term.mkApp(internalNormalize(f), args.map(_.fold({t => Left(internalNormalize(t))},(Right(_)))))
    case ty :::> t              => \(ty)(internalNormalize(t))
    case TypeLambda(t)          => mkTypeAbs(internalNormalize(t))
//    case _                      => formula

  }


  private def incrementBound(formula : Term, i : Int) : Term = formula match {
    case s@Symbol(_)           => s
    case s@MetaVar(_,_)        => s
    case Bound(ty,n) if n < i  => formula
    case Bound(ty,n)            => mkBound(ty,n+1)
    case f ∙ args               => Term.mkApp(incrementBound(f,i), args.map(_.fold({t => Left(incrementBound(t,i))},(Right(_)))))
    case ty :::> t              => \(ty)(incrementBound(t,i+1))
    case TypeLambda(t)          => mkTypeAbs(incrementBound(t,i))

//    case _                      => formula
  }

  /**
   * Checks if the last three statusbits are raised.
   * (status & 15) = status & 0..01111  ~~ Selects the last 4 Bits
   * equals 7 only if the last three bits are set and the 4th not
   *
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(status : Int): Boolean = (status & 31) == 15

  def markStatus(fs : FormulaStore) : FormulaStore = Store(fs.clause, Role_Plain, fs.context, fs.status | 31)
}
