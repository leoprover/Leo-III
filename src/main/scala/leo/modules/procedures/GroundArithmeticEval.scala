package leo.modules.procedures

import leo.datastructures.{Int0, Rat, Term, Type, prettyRat}
import leo.datastructures.Term.local._
import Simplification.normalizeRat
import leo.modules.HOLSignature.{HOLDifference, HOLGreater, HOLGreaterEq, HOLLess, HOLLessEq, HOLProduct, HOLQuotient, HOLSum, HOLUnaryMinus, LitFalse, LitTrue}
import leo.modules.SZSException
import leo.modules.output.SZS_InputError

import scala.annotation.switch
import scala.math.BigInt

object GroundArithmeticEval {
  /* Literals of type $real are left alone.  The representation is a decimal triple
   * (whole part, decimal places, exponent) that is not closed under the operations:
   * normalizeReal does not normalize, none of sum, product, quotient or the ordering
   * was ever written for it, and negation loses the sign of a number whose whole part
   * is zero, since BigInt has no -0.  Every such case therefore falls through to the
   * unevaluated term below.  Not simplifying is sound; simplifying wrongly is not. */

  final def apply(term: Term): Term = {
    import leo.datastructures.Term.{:::>, TypeLambda, Symbol, ∙, Rational, Real, Integer}

    @inline def applyTermOrType(arg: Either[Term, Type]): Either[Term, Type] = arg match {
      case Left(arg0) => Left(apply(arg0))
      case Right(arg0) => Right(arg0)
    }

    term match {
      case ty :::> body => mkTermAbs(ty, apply(body))
      case TypeLambda(body) => mkTypeAbs(apply(body))
      case f ∙ args if f.isConstant && args.length <= 3  => /* Count the explicit type argument */
        (f: @unchecked) match {
          case Symbol(id) =>
            (id: @switch) match {
              case HOLLess.key =>
                val (left, right) = HOLLess.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => booleanToTerm(evalLessInt(n1, n2))
                  case (Rational(n1), Rational(n2)) => booleanToTerm(evalLessRat(n1, n2))
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLLessEq.key =>
                val (left, right) = HOLLessEq.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => booleanToTerm(evalLessInt(n1, n2) || n1 == n2)
                  case (Rational(n1), Rational(n2)) => booleanToTerm(evalLessRat(n1, n2)  || (normalizeRat _ tupled n1) == (normalizeRat _ tupled n2))
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLGreater.key =>
                val (left, right) = HOLGreater.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => booleanToTerm(evalGreaterInt(n1, n2))
                  case (Rational(n1), Rational(n2)) => booleanToTerm(evalGreaterRat(n1, n2))
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLGreaterEq.key =>
                val (left, right) = HOLGreaterEq.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => booleanToTerm(evalGreaterInt(n1, n2) || n1 == n2)
                  case (Rational(n1), Rational(n2)) => booleanToTerm(evalGreaterRat(n1, n2)  || (normalizeRat _ tupled n1) == (normalizeRat _ tupled n2))
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLSum.key =>
                val (left, right) = HOLSum.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => mkInteger(sumInt(n1, n2))
                  case (Rational(n1), Rational(n2)) => mkRational _ tupled sumRat(n1, n2)
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLDifference.key =>
                val (left, right) = HOLDifference.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => mkInteger(diffInt(n1, n2))
                  case (Rational(n1), Rational(n2)) => mkRational _ tupled diffRat(n1, n2)
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLProduct.key =>
                val (left, right) = HOLProduct.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => mkInteger(prodInt(n1, n2))
                  case (Rational(n1), Rational(n2)) => mkRational _ tupled prodRat(n1, n2)
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLQuotient.key =>
                val (left, right) = HOLQuotient.unapply(term).get
                val simpLeft = apply(left)
                val simpRight = apply(right)
                (simpLeft, simpRight) match {
                  case (Integer(n1), Integer(n2)) => mkRational _ tupled quotInt(n1, n2)
                  case (Rational(n1), Rational(n2)) => mkRational _ tupled quotRat(n1, n2)
                  case _ => mkTermApp(mkTypeApp(f, simpLeft.ty), Seq(simpLeft, simpRight))
                }
              case HOLUnaryMinus.key =>
                val body = HOLUnaryMinus.unapply(term).get
                val simpBody = apply(body)
                simpBody match {
                  case Integer(n) => mkInteger(-n)
                  case Rational(n) => mkRational _ tupled normalizeRat(-n._1, n._2)
                  case _ => mkTermApp(mkTypeApp(f, simpBody.ty), Seq(simpBody))
                }
              case _ => mkApp(f, args.map(applyTermOrType))
            }
        }
      case f ∙ args =>
        // f is a variable or a constant because `term` is in beta nf.
        mkApp(f, args.map(applyTermOrType))
      case _ => term
    }
  }

  @inline private[this] final def booleanToTerm(b: Boolean): Term = if (b) LitTrue else LitFalse
  private[this] final def evalLessInt(n1: Int0, n2: Int0): Boolean = n1 < n2
  private[this] final def evalLessRat(n1: Rat, n2: Rat): Boolean = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    num1*denom2 < num2*denom1
  }

  private[this] final def evalGreaterInt(n1: Int0, n2: Int0): Boolean = evalLessInt(n2, n1)
  private[this] final def evalGreaterRat(n1: Rat, n2: Rat): Boolean = evalLessRat(n2, n1)

  private[this] final def sumInt(n1: Int0, n2: Int0): Int0 = n1 + n2
  private[this] final def sumRat(n1: Rat, n2: Rat): Rat = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    // a/b + c/d =  a*d/b*d + c*b / d*b
    normalizeRat(num1*denom2 + num2*denom1, denom1 * denom2)
  }

  private[this] final def diffInt(n1: Int0, n2: Int0): Int0 = sumInt(n1, -n2)
  private[this] final def diffRat(n1: Rat, n2: Rat): Rat = sumRat(n1, (-n2._1, n2._2))

  private[this] final def prodInt(n1: Int0, n2: Int0): Int0 = n1 * n2
  private[this] final def prodRat(n1: Rat, n2: Rat): Rat = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    // a/b * c/d =  a*c/b*d
    normalizeRat(num1*num2, denom1*denom2)
  }

  private[this] final val bigZero: BigInt = BigInt(0)
  private[this] final def quotInt(n1: Int0, n2: Int0): Rat = n2 match {
    case `bigZero` => throw new SZSException(SZS_InputError, s"Division by zero in expression '$$quotient($n1, $n2)'.")
    case _ => normalizeRat(n1, n2)
  }
  private[this] final def quotRat(n1: Rat, n2: Rat): Rat = n2._1 match {
    case `bigZero` => throw new SZSException(SZS_InputError, s"Division by zero in expression '$$quotient(${prettyRat(n1)}, ${prettyRat(n2)})'.")
    case _ => prodRat(n1, (n2._2, n2._1))
  }
}
