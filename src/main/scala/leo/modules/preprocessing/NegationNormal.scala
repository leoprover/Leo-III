package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures._
import leo.modules.HOLSignature.{o, Not, <=>, Impl, &, |||, ===, !===, Forall, Exists}
import leo.modules.output.SZS_Theorem

/**
 * Calculates the Negation Normal Form (NNF) of a term.
 *
 * @author Max Wisniewski
 * @since 6/12/14
 */
object NegationNormal extends Normalization{
  override val inferenceStatus = Some(SZS_Theorem)
  override val name : String = "negation_normal"
  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  def apply(formula: Clause)(implicit sig: Signature): Clause = {
    formula.mapLit { li =>
      if(li.left.ty == o) {
        val l1 = li.termMap { case (l, r) => {
          val (t1, t2) = if (li.polarity) (l, r) else (Not(l), r)
          (nnf(rmEq(t1, 1)).betaNormalize, nnf(rmEq(t2, 1)).betaNormalize)
        }
        }
        if (li.polarity) l1 else Literal.flipPolarity(l1)
      } else li
    }
  }

  def apply(literal : Literal) : Literal = {
    if(literal.left.ty != o) return literal
    val l1 = literal.termMap { case (l,r) => {
      val (t1,t2) = if (literal.polarity) (l,r) else (Not(l), r)
      (nnf(rmEq(t1, 1)).betaNormalize,nnf(rmEq(t2, 1)).betaNormalize)
    } }
    if(literal.polarity) l1 else Literal.flipPolarity(l1)
  }

  def normalize(t: Term): Term = {
    nnf(rmEq(t, 1)).betaNormalize
  }

  def normalizeNonExt(t: Term): Term = nnf(t)

  private def pol(b : Boolean) : Int = if(b) 1 else -1

  private def rmEq(formula : Term, pol : Int) : Term = formula match {
    case (s <=> t) if pol == 1    => &(Impl(rmEq(s,-1),rmEq(t,1)),Impl(rmEq(t,-1),rmEq(s,1)))
    case (s <=> t) if pol == -1   => |||(&(rmEq(s,-1),rmEq(t,-1)),&(Not(rmEq(s,1)),Not(rmEq(t,1))))
    case (s === t) if pol == 1 && s.ty == o
            => &(Impl(rmEq(s,-1),rmEq(t,1)),Impl(rmEq(t,-1),rmEq(s,1)))
    case (s === t) if pol == -1 && s.ty == o
            => |||(&(rmEq(s,-1),rmEq(t,-1)),&(Not(rmEq(s,1)),Not(rmEq(t,1))))
    case (s !=== t) if pol == -1 && s.ty == o
          => &(Impl(rmEq(s,-1),rmEq(t,1)),Impl(rmEq(t,-1),rmEq(s,1)))
    case (s !=== t) if pol == 1 && s.ty == o
          => |||(&(rmEq(s,-1),rmEq(t,-1)),&(Not(rmEq(s,1)),Not(rmEq(t,1))))

    case Impl(s,t)               => Impl(rmEq(s,(-1)*pol),rmEq(t,pol))
    case Not(t)                  => Not(rmEq(t,(-1)*pol))

    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args               => Term.mkApp(rmEq(f,pol), args.map(_.fold({t => Left(rmEq(t,pol))},(Right(_)))))
    case ty :::> t              => mkTermAbs(ty, rmEq(t,pol))
    case TypeLambda(t)          => mkTypeAbs(rmEq(t,pol))
//    case _                      => formula
  }

  private def nnf(formula : Term) : Term = formula match {
    case Not(s & t)             =>
      val s1 = nnf(Not(s))
      val t1 = nnf(Not(t))
      |||(s1, t1)
    case Not(s ||| t)           =>
      val s1 = nnf(Not(s))
      val t1 = nnf(Not(t))
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
//    case Impl(s,t)              =>
//      val s1 = nnf(s)
//      val t1 = nnf(t)
//      Impl(s1,t1)
    case Not(Not(t))            => nnf(t)

    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case f ∙ args               => Term.mkApp(nnf(f), args.map(_.fold({t => Left(nnf(t))},(Right(_)))))
    case ty :::> t              => mkTermAbs(ty, nnf(t))
    case TypeLambda(t)          => mkTypeAbs(nnf(t))
  }
}
