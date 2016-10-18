package leo.modules.preprocessing

import leo.datastructures.Term._
import leo.datastructures._
import leo.modules.HOLSignature.{Not, ===, LitFalse, LitTrue, &, |||, <=>, Impl, Forall, Exists}

/**
  * Created by mwisnie on 1/5/16.
  */
object Simplification extends Normalization {

  override val name : String = "simplify"

  def polarityNorm(formula : Clause) : Clause = formula.mapLit(polarityNorm(_))

  def polarityNorm(lit : Literal) : Literal = (lit.left, lit.right) match {
    case (Not(l),Not(r))  => Literal(l,r, lit.polarity, lit.oriented)
    case (Not(l),r) => Literal(l,r, !lit.polarity)
    case (l, Not(r)) => Literal(l,r, !lit.polarity)
    case (l,r)  => lit
  }

  override def apply(formula : Clause)(implicit sig: Signature) : Clause = {
    formula.mapLit(apply(_))
  }

  def apply(literal : Literal) : Literal = {
    val left = internalNormalize(literal.left)
    val right = internalNormalize(literal.right)
    Literal(left,right, literal.polarity)
  }

  def normalize(t: Term): Term = internalNormalize(t)

  private def internalNormalize(formula: Term): Term = norm(formula.betaNormalize).betaNormalize

  private def norm(formula : Term) : Term = formula match {
    //case Bound(ty)   => formula // Sollte egal sein
    //case Symbol(key) => formula

    // First normalize, then match
    case s === t =>
      (norm(s), norm(t)) match {
        case (s1,t1) if s1 == t1 => LitTrue
        case (LitTrue(),t1) => t1
        case (s1,LitTrue()) => s1
        case (LitFalse(), t1) => Not(t1)
        case (s1, LitFalse()) => Not(s1)
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
      val (ns, nt) = (norm(s), norm(t))
      val res : Term = (ns, nt) match {
        case (s1, t1) if s1 == t1   => LitTrue
        case (s1, LitTrue())        => s1
        case (LitTrue(), t1)        => t1
        case (s1, LitFalse())       => norm(Not(s1))
        case (LitFalse(), t1)       => norm(Not(t1))
        case (s1, t1)               => &(Impl(s1,t1),Impl(t1,s1))
      }
      return res
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
      case Not(s1)      => s1
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
    case f ∙ args   => Term.mkApp(norm(f), args.map(_.fold({t => Left(norm(t))},(Right(_)))))
    case ty :::> s  => Term.mkTermAbs(ty, norm(s))
    case TypeLambda(t) => Term.mkTypeAbs(norm(t))
  }

  /**
    * Returns a List with deBrujin Indizes, that are free at this level.
    *
    * @param formula
    * @return
    */
  protected[preprocessing] def freeVariables(formula : Term) : List[(Int,Type)] = formula match {
    case Bound(t,scope) => List((scope,t))
    case Symbol(id)     => List()
    case f ∙ args       => freeVariables(f) ++ args.flatMap(_.fold(freeVariables(_), _ => List()))
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
  protected[preprocessing] def isBound(formula : Term) : Boolean = {
    freeVariables(formula).filter {case (a,b) => a == 1}.nonEmpty
  }


  /**
    * Removes the quantifier from a formula, that is free, by instantiating it
    * and betanormalization.
    *
    * @param formula Abstraction with not bound variable.
    * @return the term without the function.
    */
  protected[preprocessing] def removeUnbound(formula : Term) : Term = formula match {
    case ty :::> t =>
      //      println("Removed the abstraction in '"+formula.pretty+"'.")
      mkTermApp(formula,mkBound(ty,-4711)).betaNormalize
    case _        => formula
  }
}
