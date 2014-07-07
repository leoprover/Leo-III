package leo.modules.normalization

import leo.datastructures.internal.Term._
import leo.datastructures.internal._
import java.util.concurrent.atomic.AtomicInteger

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

  /**
   * Normalizes a formula corresponding to the object.
   *
   * @param formula - A annotated formula
   * @return a normalized formula
   */
  override def normalize(formula: Term): Term = {
//    println("Skolemize '"+formula.pretty+"'.")
    val mini = miniscope(formula)
//    println("    Miniscope : '"+mini.pretty+"'.")
    val r = skolemize(mini)
//    println("    Skolemized : '"+r.pretty+"'.")
    r
  }

  /**
   *
   * For each exists quantified Term
   * (Exists(\x. t)) we replace x by a quantifier
   *
   * @param formula
   * @return
   */
  private def skolemize(formula : Term) : Term = formula match {
      //Remove exist quantifier
    case Exists(ty :::> t)  =>
      val t1 = skolemize(t)
      val free : List[(Int, Type)] = Simplification.freeVariables(t1).filter{case (a,b) => a > 1}.map{case (a,b) => (a-1,b)}
      // A skolemvariable takes all above instantiated variables and is a function from these to an
      // object of type ty.
      val types = free map (x => x._2)
      val skoType = Type.mkFunType(types, ty)

      // Creating a fresh Variable of this type and applying it to all free variables
      val skoVar = mkTermApp(mkAtom(Signature.get.freshSkolemVar(skoType)),free map {case (a,b) => mkBound(b,a)})
//      println("New skoVar '"+skoVar.pretty+"' in term '"+(Exists(\(ty)(t1))).pretty+"'.")
      //Lastly replacing the Skolem variable for the Quantifier (thereby raising the free variables)
      mkTermApp(\(ty)(t1), skoVar).betaNormalize
      // Pass through
    case s @@@ t    => mkTermApp(skolemize(s),skolemize(t))
    case s @@@@ ty  => mkTypeApp(skolemize(s),ty)
    case ty :::> s  => mkTermAbs(ty, skolemize(s))
    case TypeLambda(t) => mkTypeAbs(skolemize(t))
    case _  => formula
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
      case s @@@ t    => Exists(\(ty)(mkTermApp(miniscope(s),miniscope(t))))
      case s @@@@ ty  => Exists(\(ty)(mkTypeApp(miniscope(s),ty)))
      case ty :::> s  => Exists(\(ty)(mkTermAbs(ty, miniscope(s))))
      case TypeLambda(t) => Exists(\(ty)(mkTypeAbs(miniscope(t))))
      case _  => formula
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
      case s @@@ t    => Forall(\(ty)(mkTermApp(miniscope(s),miniscope(t))))
      case s @@@@ ty  => Forall(\(ty)(mkTypeApp(miniscope(s),ty)))
      case ty :::> s  => Forall(\(ty)(mkTermAbs(ty, miniscope(s))))
      case TypeLambda(t) => Forall(\(ty)(mkTypeAbs(miniscope(t))))
      case _  => formula
    }

      // In neither of the above cases, move inwards
    case s @@@ t    => mkTermApp(miniscope(s),miniscope(t))
    case s @@@@ ty  => mkTypeApp(miniscope(s),ty)
    case ty :::> s  => mkTermAbs(ty, miniscope(s))
    case TypeLambda(t) => mkTypeAbs(miniscope(t))
    case _  => formula

  }

  /**
   * Checks if the last two statusbits are raised.
   * (status & 7) = status & 0..0111  ~~ Selects the last 3 Bits
   * equals 3 only if the last three bits are set and the 4th not

   * @param formula - Formula to be checked
   * @param status - Status of the formula
   * @return True if a normaliziation is possible, false otherwise
   */
  override def applicable(formula: Term, status : Int): Boolean = (status & 15) == 7

  override def markStatus(status : Int) : Int = status | 15
}
