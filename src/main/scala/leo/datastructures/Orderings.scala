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
  def lift[A](f:  (A,A) => Option[Int]): QuasiOrdering[A] = new QuasiOrdering[A] {
    def compare(x: A, y: A) = f(x,y)
  }

  def productOrd[A,B](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B]): QuasiOrdering[(A,B)] = new QuasiOrdering[(A, B)] {
    def compare(x: (A, B), y: (A, B)) = ordA.compare(x._1, y._1) match {
      case Some(0) => ordB.compare(x._2, y._2)
      case res => res
    }
  }
  def productOrd[A,B,C](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B], ordC: QuasiOrdering[C]): QuasiOrdering[(A,B,C)] = new QuasiOrdering[(A, B, C)] {
    def compare(x: (A, B, C), y: (A, B, C)) = ordA.compare(x._1, y._1) match {
      case Some(0) => ordB.compare(x._2, y._2) match {
        case Some(0) => ordC.compare(x._3, y._3)
        case res => res
      }
      case res => res
    }
  }
  def productOrd[A,B,C,D](ordA: QuasiOrdering[A], ordB: QuasiOrdering[B], ordC: QuasiOrdering[C], ordD: QuasiOrdering[D]): QuasiOrdering[(A,B,C,D)] = new QuasiOrdering[(A, B, C,D)] {
    def compare(x: (A, B, C,D), y: (A, B, C,D)) = ordA.compare(x._1, y._1) match {
      case Some(0) => ordB.compare(x._2, y._2) match {
        case Some(0) => ordC.compare(x._3, y._3) match {
          case Some(0) => ordD.compare(x._4, y._4)
          case res => res
        }
        case res => res
      }
      case res => res
    }
  }

  def lexOrd[A](ord: QuasiOrdering[A]): QuasiOrdering[Seq[A]] = new QuasiOrdering[Seq[A]] {
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


/////////////////////
// Precedences
/////////////////////

trait Precedence {
  type Const = Signature#Key
  def compare(x: Const, y: Const): CMP_Result
  def gt(x: Const, y: Const): Boolean = compare(x,y) == CMP_GT
  def ge(x: Const, y: Const): Boolean = compare(x,y) == CMP_GT || compare(x,y) == CMP_EQ
  def lt(x: Const, y: Const): Boolean = compare(x,y) == CMP_LT
  def le(x: Const, y: Const): Boolean = compare(x,y) == CMP_LT || compare(x,y) == CMP_EQ

  protected final def intToCMPRes(x: Int, y: Int): CMP_Result = {
    if (x > y) CMP_GT
    else if (x < y) CMP_LT
    else CMP_EQ
  }
}

object Precedence {
  import leo.datastructures.impl.precedences._
  final val sigInduced: Precedence = Prec_SigInduced
  final val arity: Precedence = Prec_Arity
  final val arity_UnaryFirst: Precedence = Prec_Arity_UnaryFirst
  final val arityOrder: Precedence = Prec_ArityOrder
  final val arityOrder_UnaryFirst: Precedence = Prec_ArityOrder_UnaryFirst
  final val arityInvOrder_UnaryFirst: Precedence = Prec_ArityInvOrder_UnaryFirst
}

///////////////////////
/// Term Orderings
///////////////////////

object TermOrdering {
  import leo.datastructures.impl.orderings._
  final val senseless: TermOrdering = TO_SenselessOrdering
  final val sizeBased: TermOrdering = TO_SizedBased
}

/////////////////////
// Clause Orderings
/////////////////////

object ClauseOrdering {
  import leo.datastructures.impl.orderings._
  /** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin) */
  final val lex_WeightAgeOrigin: ClauseOrdering = CLO_Lex_WeightAgeOrigin
}

///////////////////////
/// Type Orderings
///////////////////////



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

/**
 * A trait for representing quasi-orderings.
 * A quasi-ordering is a
 *
 * - reflexive
 * - transitive
 *
 * binary relation.
 */
trait QuasiOrdering[A] {
  /** Result of comparing `x` with operand `y`.
    *
    * Returns `Some(res)` where:
    *   - `res < 0` when `x < y`
    *   - `res == 0` when `x == y`
    *   - `res > 0` when  `x > y`
    *
    * Returns `None` if the objects cannot be compared.
    * This is due to the fact that this object represents a partial ordering;
    * it is possible that neither `compare(x,y) < 0` or `compare(y,x) < 0`
    * holds for `x != y`.
    */
  def compare(x: A, y: A): Option[Int]

  /** Returns `true` iff `x` and `y` are comparable w.r.t. the underlying ordering. */
  def canCompare(x: A, y: A): Boolean = if (x == y) true
                                        else compare(x,y).isDefined|| compare(y,x).isDefined

  /** Strict comparison `x < y` w.r.t. the underlying ordering. */
  def lt (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MaxValue) < 0
  /** Comparison `x <= y` w.r.t. the underlying ordering. */
  def lteq (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MaxValue) <= 0
  /** Strict comparison `x > y` w.r.t. the underlying ordering. */
  def gt (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MinValue) > 0
  /** Comparison `x >= y` w.r.t. the underlying ordering. */
  def gteq (x: A, y: A): Boolean = compare(x,y).getOrElse(Int.MinValue) >= 0
  /** Comparison `x = y` w.r.t. the underlying ordering .*/
  def eq (x: A, y: A): Boolean = compare(x,y).getOrElse(-4711) == 0
}

/** Trait for quasi-ordered data.
  * @see [[QuasiOrdering]]
  */
trait QuasiOrdered[A] {
  /** Result of comparing `this` with operand `that`.
    *
    * Returns `Some(res)` where:
    *   - `res < 0` when `this < that`
    *   - `res == 0` when `this == that`
    *   - `res > 0` when  `this > that`
    *
    * Returns `None` if the objects cannot be compared.
    * This is due to the fact that this object represents a partial ordering;
    * it is possible that neither `this compareTo that < 0` or `that compareTo this < 0`
    * holds for `this != that`.
    */
  def compareTo(that: A): Option[Int]

  /** Returns true iff `this` and `that`  are comparable w.r.t. the underlying quasi-ordering. */
  def comCompareTo(that: A): Boolean = compareTo(that).isDefined

  /** Returns true iff (this compareTo that) < 0, i.e. if `this` is strictly smaller than `that`. */
  def <  (that: A): Boolean = (this compareTo that).getOrElse(Int.MaxValue) < 0
  /** Returns true iff (this compareTo that) <= 0, i.e. if `this` is smaller than (or equal to) `that`. */
  def <= (that: A): Boolean = (this compareTo that).getOrElse(Int.MaxValue) <= 0
  /** Returns true iff (this compareTo that) > 0, i.e. if `this` is strictly larger than `that`. */
  def >  (that: A): Boolean = (this compareTo that).getOrElse(Int.MinValue) > 0
  /** Returns true iff (this compareTo that) >= 0, i.e. if `this` is larger than (or equal to) `that`. */
  def >= (that: A): Boolean = (this compareTo that).getOrElse(Int.MinValue) >= 0
}
