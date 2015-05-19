package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.blackboard.{Store, FormulaStore}
import Term._
import leo.datastructures._

/**
 * Calculates the Negation Normal Form (NNF) of a term.
 *
 * @author Max Wisniewski
 * @since 6/12/14
 */
object NegationNormal extends AbstractNormalize{

  override val name : String = "NegationNormal"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Clause): Clause = {
    formula.mapLit { l =>
        val l1 = l.termMap { t =>
          val t1 = if (l.polarity) t else Not(t)
          nnf(rmEq(t1, 1))
        }
        if(l.polarity) l1 else l1.flipPolarity
    }
  }

  private def pol(b : Boolean) : Int = if(b) 1 else -1

  private def rmEq(formula : Term, pol : Int) : Term = formula match {
    case (s <=> t) if pol == 1  => &(Impl(rmEq(s,-1),rmEq(t,1)),Impl(rmEq(t,-1),rmEq(s,1)))
    case (s <=> t) if pol == -1 => |||(&(rmEq(s,-1),rmEq(t,-1)),&(Not(rmEq(s,1)),Not(rmEq(t,1))))

    case Impl(s,t)               => Impl(rmEq(s,(-1)*pol),rmEq(t,pol))
    case Not(t)                  => Not(rmEq(t,(-1)*pol))

    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case s@MetaVar(_,_)         => s
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
    case Not(Not(t))            => nnf(t)

    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case s@MetaVar(_,_)         => s
    case f ∙ args               => Term.mkApp(nnf(f), args.map(_.fold({t => Left(nnf(t))},(Right(_)))))
    case ty :::> t              => mkTermAbs(ty, nnf(t))
    case TypeLambda(t)          => mkTypeAbs(nnf(t))
//    case x                      => x
  }

  /**
   * Checks if status bits 1,2 are raised and the third is not
   *
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(status : Int): Boolean = (status & 7) == 3

  def markStatus(fs : FormulaStore) : FormulaStore = Store(fs.clause, Role_Plain, fs.context, fs.status | 7)
}
