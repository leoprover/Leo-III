package leo.modules.normalization

import leo.datastructures.internal._
import leo.datastructures.internal.Term._

/**
 *
 * Simple object, that removes syntactic tautologies
 * and idempotent operations.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
object Simplification extends AbstractNormalize{
  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = norm(formula.betaNormalize)

  private def norm(formula : Term) : Term = formula match {
    //case Bound(ty)   => formula // Sollte egal sein
    //case Symbol(key) => formula

      // First normalize, then match
    case s & t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1     => s1
        case (s1, Not(t1)) if s1 == t1  => LitFalse()
        case (Not(s1), t1) if s1 == t1  => LitFalse()
        case (s1, LitTrue())            => s1
        case (LitTrue(), t1)            => t1
        case (s1, LitFalse())           => LitFalse()
        case (LitFalse(), t1)           => LitFalse()
        case (s1, t1)                 => &(s1,t1)
      }
    case (s ||| t) =>
      (norm(s),norm(t)) match {
        case (s1,t1) if s1 == t1      => s1
        case (s1, Not(t1)) if s1 == t1   => LitTrue()
        case (Not(s1),t1) if s1 == t1   => LitTrue()
        case (s1, LitTrue())            => LitTrue()
        case (LitTrue(), t1)            => LitTrue()
        case (s1, LitFalse())           => s1
        case (LitFalse(), t1)           => t1
        case (s1, t1)                 => |||(s1,t1)
      }
    case s <=> t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1 => LitTrue()
        case (s1, LitTrue())        => s1
        case (LitTrue(), t1)        => t1
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitFalse(), t1)       => norm(Not(t1))
        case (s1, t1)             => <=>(s1,t1)
      }
    case s Impl t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1 => LitTrue()
        case (s1, LitTrue())        => LitTrue()
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitTrue(), t1)        => t1
        case (LitFalse(), t1)       => LitTrue()
        case (s1,t1)                => Impl(s1,t1)
      }
    case Not(s) => norm(s) match {
      case LitTrue()    => LitFalse()
      case LitFalse()   => LitTrue()
      case s1           => Not(s1)
    }
    case Forall(t) => norm(t) match {
      case ty :::> t1 =>
        def sigF(x:Signature#Key) : List[Int] = List()
        def sigB(ty : Type, t : Int) : List[Int] = List(t)
        def abs(ty : Type, t : List[Int]) : List[Int] = t.map(_-1).filter(_>0)
        def app(t : List[Int], s : List[Int]) : List[Int] = t ++ s
        def tabs(t : List[Int]) : List[Int] = t
        def tapp(t : List[Int], ty : Type) : List[Int] = t
        if (t1.foldRight(sigF)(sigB)(abs)(app)(tabs)(tapp).contains(1))
          Forall(mkTermAbs(ty, t1))
        else
          mkTermApp(mkTermAbs(ty,t1), mkBound(ty,-1)).betaNormalize       // Instatiation of term, to raise the DeBruijn Indizes refer to abstraction above this level
      case t1         => Forall(t1)
    }
    case Exists(t) => norm(t) match {
      case ty :::> t1 =>
        def sigF(x:Signature#Key) : List[Int] = List()
        def sigB(ty : Type, t : Int) : List[Int] = List(t)
        def abs(ty : Type, t : List[Int]) : List[Int] = t.map(_-1).filter(_>0)
        def app(t : List[Int], s : List[Int]) : List[Int] = t ++ s
        def tabs(t : List[Int]) : List[Int] = t
        def tapp(t : List[Int], ty : Type) : List[Int] = t
        if (t1.foldRight(sigF)(sigB)(abs)(app)(tabs)(tapp).contains(1))
          Exists(mkTermAbs(ty, t1))
        else
          mkTermApp(mkTermAbs(ty,t1), mkBound(ty,-1)).betaNormalize
      case t1         => Exists(t1)
    }

      // Pass through unimportant structures
    case s ::: t    => Term.mkTermApp(norm(s),norm(t))  // Should not happen after beta normalize, unless s is irreduceable
    case s :::: ty  => Term.mkTypeApp(norm(s), ty)
    case ty :::> s  => Term.mkTermAbs(ty, norm(s))
    case TypeLambda(t) => Term.mkTypeAbs(norm(t))
    case _  => formula
  }

  /**
   * Applies iff anything changes. Stupid, because we calculate the simplification very often
   * until we apply it.
   */
  override def applicable(formula: Term): Boolean = normalize(formula) != formula
}
