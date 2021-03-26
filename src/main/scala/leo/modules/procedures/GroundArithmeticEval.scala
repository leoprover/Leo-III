package leo.modules.procedures

import leo.datastructures.{Term, Type, Int0, Rat, Real}
import leo.datastructures.Term.{Integer}
import leo.datastructures.Term.local._
import Simplification.normalizeRat

object GroundArithmeticEval {

  final def apply(t: Term): Term = ???

  final def evalLessInt(n1: Int0, n2: Int0): Boolean = n1 < n2
  final def evalLessRat(n1: Rat, n2: Rat): Boolean = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    num1*denom2 < num2*denom1
  }
  final def evalLessReal(n1: Real, n2: Real): Boolean = ???

//  final def evalGreaterInt(n1: Int0, n2: Int0): Boolean = !evalLessInt(n1, n2)
//  final def evalGreaterRat(n1: Rat, n2: Rat): Boolean = !evalLessRat(n1, n2)
//  final def evalGreaterReal(n1: Real, n2: Real): Boolean = !evalLessReal(n1, n2)

  final def sumInt(n1: Int0, n2: Int0): Int0 = n1 + n2
  final def sumRat(n1: Rat, n2: Rat): Rat = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    // a/b + c/d =  a*d/b*d + c*b / d*b
    normalizeRat(num1*denom2 + num2*denom1, denom1 * denom2)
  }
  final def sumReal(n1: Real, n2: Real): Real = ???

//  final def diffInt(n1: Int0, n2: Int0): Int0 = n1 - n2
//  final def diffRat(n1: Rat, n2: Rat): Rat = {
//    val (num1, denom1) = n1
//    val (num2, denom2) = n2
//    // a/b - c/d =  a*d/b*d - c*b / d*b
//    normalizeRat(num1*denom2 - num2*denom1, denom1 * denom2)
//  }
//  final def diffReal(n1: Real, n2: Real): Real = ???

  final def prodInt(n1: Int0, n2: Int0): Int0 = n1 * n2
  final def prodRat(n1: Rat, n2: Rat): Rat = {
    val (num1, denom1) = n1
    val (num2, denom2) = n2
    // a/b * c/d =  a*c/b*d
    normalizeRat(num1*num2, denom1*denom2)
  }
  final def prodReal(n1: Real, n2: Real): Real = ???
}
