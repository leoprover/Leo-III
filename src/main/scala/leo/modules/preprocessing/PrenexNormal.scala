package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures._
import leo.modules.HOLSignature.{LitTrue, LitFalse, &, |||, Forall}

/**
 * Computes for a Skolemized Term the Prenex Normal Form
 *
 * @author Max Wisniewski
 * @since 6/17/14
 */
object PrenexNormal extends Normalization {
  override val name : String = "prenex_normal"
  /**
   * Normalizes a formula corresponding to the object.
    * We assume a positive literal weight after applying negationnormalform
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def apply(formula : Clause)(implicit sig: Signature) : Clause = {
    var maxBound = formula.maxImplicitlyBound
    formula.mapLit(lit => lit.termMap {case (l,r) =>
      (internalNormalize(l).betaNormalize,internalNormalize(r).betaNormalize) match {
        case (l1, LitTrue()) if lit.polarity =>
          val (l2, nmaxBound) = moveForallToClause(l1, maxBound)
          maxBound = nmaxBound
          (l2, LitTrue())
        case (l1, LitFalse()) if !lit.polarity =>
          val (l2, nmaxBound) = moveForallToClause(l1, maxBound)
          maxBound = nmaxBound
          (l2,LitFalse())
        case s      => s
      }
    })
  }

  def apply(literal : Literal) : Literal = {
    var maxBound = literal.fv.toSeq.sortBy(_._1).headOption.fold(0){case (i,ty) => i}
    literal.termMap {case (l,r) =>
      (internalNormalize(l).betaNormalize, internalNormalize(r).betaNormalize) match {
        case (l1, LitTrue()) if literal.polarity =>
          val (l2, nmaxBound) = moveForallToClause(l1, maxBound)
          maxBound = nmaxBound
          (l2, LitTrue())
        case (l1, LitFalse()) if !literal.polarity =>
          val (l2, nmaxBound) = moveForallToClause(l1, maxBound)
          maxBound = nmaxBound
          (l2,LitFalse())
        case s      => s
      }

    }
  }

  def normalize(t: Term): Term = {
    internalNormalize(t).betaNormalize
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
    case f ∙ args               => Term.mkApp(internalNormalize(f), args.map(_.fold({t => Left(internalNormalize(t))},(Right(_)))))
    case ty :::> t              => \(ty)(internalNormalize(t))
    case TypeLambda(t)          => mkTypeAbs(internalNormalize(t))
//    case _                      => formula

  }


  private def incrementBound(formula : Term, i : Int) : Term = formula match {
    case s@Symbol(_)           => s
    case Bound(ty,n) if n < i  => formula
    case Bound(ty,n)            => mkBound(ty,n+1)
    case f ∙ args               => Term.mkApp(incrementBound(f,i), args.map(_.fold({t => Left(incrementBound(t,i))},(Right(_)))))
    case ty :::> t              => \(ty)(incrementBound(t,i+1))
    case TypeLambda(t)          => mkTypeAbs(incrementBound(t,i))

//    case _                      => formula
  }

  private def moveForallToClause(t : Term, maxBound : Int) : (Term, Int) = t match {
    case Forall(ty :::> t1)   => moveForallToClause(Term.mkTermApp(Term.mkTermAbs(ty, t1), Term.mkBound(ty, maxBound+1)).betaNormalize, maxBound + 1)
    case s                    => (s, maxBound)
  }
}
