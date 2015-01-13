package leo.modules.normalization

import leo.datastructures._
import leo.datastructures.blackboard.FormulaStore
import leo.datastructures.term._
import leo.datastructures.tptp.cnf.Formula

import scala.language.implicitConversions
import Term._
import HOLConstant.toTerm

/**
 *
 * Simple object, that removes syntactic tautologies
 * and idempotent operations.
 *
 *
 * Created by Max Wisniewski on 4/7/14.
 */
object Simplification extends AbstractNormalize{

  override val name : String = "Simplification"

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula : Clause) : Clause = {
    formula.mapLit(_.termMap(internalNormalize(_)))
  }


  private def internalNormalize(formula: Term): Term = norm(formula.betaNormalize)

  private def norm(formula : Term) : Term = formula match {
    //case Bound(ty)   => formula // Sollte egal sein
    //case Symbol(key) => formula

      // First normalize, then match
    case s === t =>
      (norm(s), norm(t)) match {
        case (s1,t1) if s1 == t1 => LitTrue
        case (s1,t1)             => ===(s1,t1)
      }
    case s & t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1     => s1
        case (s1, Not(t1)) if s1 == t1  => LitFalse
        case (Not(s1), t1) if s1 == t1  => LitFalse
        case (s1, LitTrue())            => s1
        case (LitTrue(), t1)            => t1
        case (s1, LitFalse())           => LitFalse
        case (LitFalse(), t1)           => LitFalse
        case (s1, t1)                 => &(s1,t1)
      }
    case (s ||| t) =>
      (norm(s),norm(t)) match {
        case (s1,t1) if s1 == t1      => s1
        case (s1, Not(t1)) if s1 == t1   => LitTrue
        case (Not(s1),t1) if s1 == t1   => LitTrue
        case (s1, LitTrue())            => LitTrue
        case (LitTrue(), t1)            => LitTrue
        case (s1, LitFalse())           => s1
        case (LitFalse(), t1)           => t1
        case (s1, t1)                 => |||(s1,t1)
      }
    case s <=> t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1 => LitTrue
        case (s1, LitTrue())        => s1
        case (LitTrue(), t1)        => t1
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitFalse(), t1)       => norm(Not(t1))
        case (s1, t1)             => <=>(s1,t1)
      }
    case s Impl t =>
      (norm(s), norm(t)) match {
        case (s1, t1) if s1 == t1 => LitTrue
        case (s1, LitTrue())        => LitTrue
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitTrue(), t1)        => t1
        case (LitFalse(), t1)       => LitTrue
        case (s1,t1)                => Impl(s1,t1)
      }
    case Not(s) => norm(s) match {
      case LitTrue()    => LitFalse
      case LitFalse()   => LitTrue
      case s1           => Not(s1)
    }
    case Forall(t) => norm(t) match {
      case ty :::> t1 =>
        if (isBound(t1))
          Forall(mkTermAbs(ty, t1))
        else
          removeUnbound(mkTermAbs(ty,t1))
      case t1         => Forall(t1)
    }
    case Exists(t) => norm(t) match {
      case ty :::> t1 =>
        if (isBound(t1))
          Exists(mkTermAbs(ty, t1))
        else
          removeUnbound(mkTermAbs(ty,t1))
      case t1         => Exists(t1)
    }

      // Pass through unimportant structures
    case s@Symbol(_)            => s
    case s@Bound(_,_)           => s
    case s @@@ t    => Term.mkTermApp(norm(s),norm(t))  // Should not happen after beta normalize, unless s is irreduceable
    case f ∙ args   => Term.mkApp(norm(f), args.map(_.fold({t => Left(norm(t))},(Right(_)))))
    case s @@@@ ty  => Term.mkTypeApp(norm(s), ty)
    case ty :::> s  => Term.mkTermAbs(ty, norm(s))
    case TypeLambda(t) => Term.mkTypeAbs(norm(t))
//    case _  => formula
  }

  /**
   * Returns a List with deBrujin Indizes, that are free at this level.
   *
   * @param formula
   * @return
   */
  protected[normalization] def freeVariables(formula : Term) : List[(Int,Type)] = formula match {
    case Bound(t,scope) => List((scope,t))
    case Symbol(id)     => List()
    case s @@@ t        => freeVariables(s) ++ freeVariables(t)
    case f ∙ args       => freeVariables(f) ++ args.flatMap(_.fold(freeVariables(_), _ => List()))
    case s @@@@ ty      => freeVariables(s)
    case ty :::> s      => (freeVariables(s) map {case (a:Int,b:Type) => (a-1,b)}) filter {case (a:Int,b:Type) => a>=1}
    case TypeLambda(t)  => freeVariables(t)
  }

  /**
   * Gets the body of an abstraction and returns true, if the (deBrujin) variable
   * occures in this context.
   *
   * TODO: Move to a package, where it is usefull
   *
   * @param formula - Body of an abstraction
   * @return true, iff the deBrujin Index occurs in the body
   */
  protected[normalization] def isBound(formula : Term) : Boolean = {
    freeVariables(formula).filter {case (a,b) => a == 1}.nonEmpty
  }


  /**
   * Removes the quantifier from a formula, that is free, by instantiating it
   * and betanormalization.
   *
   * @param formula Abstraction with not bound variable.
   * @return the term without the function.
   */
  protected[normalization] def removeUnbound(formula : Term) : Term = formula match {
    case ty :::> t =>
//      println("Removed the abstraction in '"+formula.pretty+"'.")
      mkTermApp(formula,mkBound(ty,-4711)).betaNormalize
    case _        => formula
  }

  /**
   * If the status has the first Bit not set, the term is simplified.
   */
  override def applicable(status : Int): Boolean = (status & 1) == 0


  def markStatus(fs: FormulaStore): FormulaStore = fs.newStatus(fs.status | 1)
}
