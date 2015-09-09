package leo
package datastructures

import leo.datastructures.impl.Signature
import Term.{:::>, TypeLambda,∙,Symbol, Bound}

import scala.annotation.tailrec

/**
 * Collection of Ordering relations of terms, clauses, etc.
 *
 * @author Alexander Steen
 * @since 20.08.14
 */

//////////////////////
// Associated traits
//////////////////////

/**
 * A trait for ordering-like relations. Operations
 * for the relation and its reflexive closure.
 */
trait OrderingLike[A] extends Equiv[A] {
  def compare(x: A, y: A): CompareResult = {
    if (equiv(x,y)) CMP_EQ
    else if (lteq(x,y)) CMP_LT /* strictly larger since equality is handled above */
    else if (gteq(x,y)) CMP_GT /* see above */
    else CMP_NC
  }
  def lt(x: A, y: A): Boolean = lteq(x,y) && !equiv(x,y)
  def lteq(x: A, y: A): Boolean
  def gt(x: A, y: A): Boolean = gteq(x,y) && !equiv(x,y)
  def gteq(x: A, y: A): Boolean = lteq(y,x)
}

/** Trait for data ordered by a ordering-like relation.
  * @see [[OrderingLike]]
  */
trait LikeOrdered[A] {
  /** Result of comparing `this` with operand `that`. */
  def compareTo(that: A): CompareResult

  /** Returns true iff `this` and `that`  are comparable w.r.t. the underlying ordering-like relation. */
  def comCompareTo(that: A): Boolean = Orderings.isComparable(compareTo(that))

  /** Returns true iff (this compareTo that) < 0, i.e. if `this` is strictly smaller than `that`. */
  def <  (that: A): Boolean = (this compareTo that) == CMP_LT
  /** Returns true iff (this compareTo that) <= 0, i.e. if `this` is smaller than (or equal to) `that`. */
  def <= (that: A): Boolean = Orderings.isLE(this compareTo that)
  /** Returns true iff (this compareTo that) > 0, i.e. if `this` is strictly larger than `that`. */
  def >  (that: A): Boolean = (this compareTo that) == CMP_GT
  /** Returns true iff (this compareTo that) >= 0, i.e. if `this` is larger than (or equal to) `that`. */
  def >= (that: A): Boolean = Orderings.isGE(this compareTo that)
}

/////////////////////
// Ordering related library functions
/////////////////////

object Orderings {
  import scala.language.implicitConversions

  def isComparable(x: CompareResult): Boolean = (x & ~(CMP_EQ | CMP_UK)) != 0
  def isGE(x: CompareResult): Boolean = (x & (CMP_EQ | CMP_GT)) != 0
  def isLE(x: CompareResult): Boolean = (x & (CMP_EQ | CMP_LT)) != 0

  /** Return a (simple) ordering that is induced by a weighting. */
  def simple[A](weighting: Weight[A]) = new Ordering[A] {
    def compare(a: A, b: A) = weighting.weightOf(a) - weighting.weightOf(b)
  }

  val intOrd = new Ordering[Int] {
    def compare(a: Int, b: Int) = a-b
  }

  def lift[A](f: A => A => Int): Ordering[A] = new Ordering[A] {
    def compare(x: A, y: A) = f(x)(y)
  }
//  def lift[A](f:  (A,A) => Option[Int]): QuasiOrdering[A] = new QuasiOrdering[A] {
//    def compare(x: A, y: A) = f(x,y)
//  }
//
//  def productOrd[A,B](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B]): QuasiOrdering[(A,B)] = new QuasiOrdering[(A, B)] {
//    def compare(x: (A, B), y: (A, B)) = ordA.compare(x._1, y._1) match {
//      case Some(0) => ordB.compare(x._2, y._2)
//      case res => res
//    }
//  }
//  def productOrd[A,B,C](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B], ordC: QuasiOrdering[C]): QuasiOrdering[(A,B,C)] = new QuasiOrdering[(A, B, C)] {
//    def compare(x: (A, B, C), y: (A, B, C)) = ordA.compare(x._1, y._1) match {
//      case Some(0) => ordB.compare(x._2, y._2) match {
//        case Some(0) => ordC.compare(x._3, y._3)
//        case res => res
//      }
//      case res => res
//    }
//  }
//  def productOrd[A,B,C,D](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B], ordC: QuasiOrdering[C], ordD: QuasiOrdering[D]): QuasiOrdering[(A,B,C,D)] = new QuasiOrdering[(A, B, C,D)] {
//    def compare(x: (A, B, C,D), y: (A, B, C,D)) = ordA.compare(x._1, y._1) match {
//      case Some(0) => ordB.compare(x._2, y._2) match {
//        case Some(0) => ordC.compare(x._3, y._3) match {
//          case Some(0) => ordD.compare(x._4, y._4)
//          case res => res
//        }
//        case res => res
//      }
//      case res => res
//    }
//  }

  def lexOrd[A](ord: OrderingLike[A]): OrderingLike[Seq[A]] = new OrderingLike[Seq[A]] {
    def compare(x: Seq[A], y: Seq[A]) = (x.length - y.length) match {
      case 0 => (x,y) match {
        case (Seq(), Seq()) => Some(0)
        case (Seq(xHead, xTail@_*), Seq(yHead, yTail@_*)) => ord.compare(xHead, yHead) match {
          case Some(0) => compare(xTail, yTail)
          case res => res
        }
      }
      case res => Some(res)
    }
  }

  implicit def toQuasiOrdering[A](ord: Ordering[A]): QuasiOrdering[A] = new QuasiOrdering[A] {
    def compare(x: A, y: A) = Some(ord.compare(x,y))
  }
}


// package orderings {

/////////////////////
// Clause Orderings
/////////////////////

/** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin). */
object CLOrdering_Lex_Weight_Age_Origin extends ClauseOrdering {
  import scala.math.Ordered.orderingToOrdered
  def compare(a: Clause, b: Clause) = ((a.weight, a.id, a.origin)) compare ((b.weight, b.id, b.origin))
}

///////////////////////
/// Term Orderings
///////////////////////

/** Only for debugging and compiling purposes. Will be removed soon. */
object SenselessOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = 0.toByte
}

/** Simple linear well-founded AC-compatible ordering, taken from Isabelle. Adapted.
  *  s < t <=> 1. size(s) < size(t) or
  *          2. size(s) = size(t) and s=f(...) and t=g(...) and f<g or
  *          3. size(s) = size(t) and s=f(s1..sn) and t=f(t1..tn) and
  *             (s1..sn) < (t1..tn) (lexicographically)
  */
object SizeBasedOrdering extends TermOrdering {
  import Type._
  import Orderings._

  lazy val tyOrd: QuasiOrdering[Type] = lift(compareType)
  // (fixed, key/scope, typ, depth)
  lazy val headOrd = productOrd(productOrd(intOrd, intOrd, tyOrd), intOrd)

  // a < b, res < 0
  def compare(a: Term, b: Term): Option[Int] = {
    // We differ in what we mean by head symbol: Isabelle allows lambda abstractions to act
    // as head symbols of no further applicative arguments can be stripped.

    def enrichHead(a: Term) = a match {
      case Symbol(key) => (0,key,Signature.get(key)._ty)
      case Bound(ty, scope) => (1,scope,ty)
    }

    if (a == b) {
      Some(0)
    } else if (a.isTypeAbs && b.isTypeAbs) {
      // adapted: both terms are type abstractions, compare recursively
      val (body1, body2) = (TypeLambda.unapply(a).get, TypeLambda.unapply(b).get)
      compare(body1, body2)
    } else if (a.isTermAbs && b.isTermAbs) {
      val (type1, body1) = :::>.unapply(a).get
      val (type2, body2) = :::>.unapply(b).get
      compare(body1, body2) match {
        case Some(0) => compareType(type1, type2)
        case res => res
      }
    } else {
      Math.signum(a.size - b.size) match { // Cases 1,2,3 ultimately begin here
        case 0 => {
          //Equal size, compare heads and args
          val hdA = (enrichHead(a.headSymbol), a.headSymbolDepth)
          val hdB = (enrichHead(b.headSymbol), b.headSymbolDepth)

          headOrd.compare(hdA, hdB) match {
            case None | Some(0) => if (a.isApp && b.isApp) {
                        val (_, spine1) = ∙.unapply(a).get
                        val (_, spine2) = ∙.unapply(b).get

                        lex(spine1,spine2)
                      } else {
                        None
                      }
            case res => res
          }
        }
        case res => Some(res.toInt) // return ordering given by size difference
      }
    }
  }

  @tailrec
  def compareType(a: Type, b: Type): Option[Int] = {
    def constructorWeight(a: Type): Int = a match {
      case BaseType(_) => 5
      case BoundType(_) => 4
      case (_ -> _) => 3
      case (_ * _) => 2
      case (_ + _) => 1
      case ∀(_) => 0
    }

    def constructorCmp(a: Type, b: Type): Int = Math.signum(constructorWeight(a) - constructorWeight(b)).toInt

    if (a == b) {
      Some(0)
    } else {
      (a,b) match {
        case (BaseType(keyA), BaseType(keyB)) => Some(keyA-keyB) // Order of base types given by their id in signature
        case (domA -> codomA, domB -> codomB) => lex(Seq(Right(domA), Right(codomA)), Seq(Right(domB), Right(codomB)))
        case (leftA * rightA, leftB * rightB) => lex(Seq(Right(leftA), Right(rightA)), Seq(Right(leftB), Right(rightB)))
        case (leftA + rightA, leftB + rightB) => lex(Seq(Right(leftA), Right(rightA)), Seq(Right(leftB), Right(rightB)))
        case (∀(bodyA), ∀(bodyB)) => compareType(bodyA, bodyB)
        case (BoundType(scopeA), BoundType(scopeB)) => Some(scopeA - scopeB)
        case (_,_) => Some(constructorCmp(a,b)) // Ordering of structurally different types given by constructor ordering.
      }
    }
  }

  @tailrec
  def lex(a: Seq[Either[Term,Type]], b: Seq[Either[Term,Type]]): Option[Int] = a match {
      case Seq() if b.isEmpty => Some(0)
      case Seq() => Some(-1)
      case Seq(_, rest@_*) if b.isEmpty => Some(1)
      case Seq(Left(t1), tn@_*) if b.head.isLeft => compare(t1, b.head.left.get) match {
        case None => None
        case Some(0) => lex(tn, b.tail)
        case r => r
      }
      case Seq(Right(ty1), tn@_*) if b.head.isRight => compareType(ty1, b.head.right.get) match {
        case None => None
        case Some(0) => lex(tn, b.tail)
        case r => r
      }
      case _ => None
    }
}



///////////////////////
/// Type Orderings
///////////////////////



///////////////////////
/// Generic Orderings
///////////////////////

//}
