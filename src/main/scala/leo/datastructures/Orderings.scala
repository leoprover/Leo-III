package leo
package datastructures

import leo.datastructures.impl.Signature
import Term.{:::>, Bound, Symbol, TypeLambda, ∙}

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



  @inline final def isComparable(x: CMP_Result): Boolean = (x & ~CMP_EQ) != 0
  @inline final def isGE(x: CMP_Result): Boolean = (x & (CMP_EQ | CMP_GT)) != 0
  @inline final def isLE(x: CMP_Result): Boolean = (x & (CMP_EQ | CMP_LT)) != 0
  @inline final def invCMPRes(x: CMP_Result): CMP_Result = {
    if (x == CMP_GT) CMP_LT
    else if (x == CMP_LT) CMP_GT
    else x
  }
  final def intToCMPRes(x: Int, y: Int): CMP_Result = {
    if (x > y) CMP_GT
    else if (x < y) CMP_LT
    else CMP_EQ
  }

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

//  def lexOrd[A](ord: QuasiOrdering[A]): QuasiOrdering[Seq[A]] = new QuasiOrdering[Seq[A]] {
//    def compare(x: Seq[A], y: Seq[A]) = (x.length - y.length) match {
//      case 0 => (x,y) match {
//        case (Seq(), Seq()) => Some(0)
//        case (Seq(xHead, xTail@_*), Seq(yHead, yTail@_*)) => ord.compare(xHead, yHead) match {
//          case Some(0) => compare(xTail, yTail)
//          case res => res
//        }
//      }
//      case res => Some(res)
//    }
//  }

  final def mult[A](gt: (A,A) => Boolean): Seq[A] => Seq[A] => Boolean = {
    s => t => {
      if (s.nonEmpty && t.isEmpty) true
      else if (s.nonEmpty && t.nonEmpty) {
        val sameElements = s.intersect(t)
        val remSameS = s.diff(sameElements)
        val remSameT = t.diff(sameElements)
        if (remSameS.isEmpty && remSameT.isEmpty) false
        else mult0(gt, remSameS, remSameT)
      } else false
    }
  }

  @tailrec
  final private def mult0[A](gt: (A,A) => Boolean, s: Seq[A], t: Seq[A]): Boolean = {
    if (t.isEmpty) true
    else if (s.nonEmpty && t.nonEmpty) {
      val sn = s.head
      val tIt = t.iterator
      var keepT: Seq[A] = Seq()
      while (tIt.hasNext) {
        val tn = tIt.next()
        if (!gt(sn,tn)) {
          keepT = keepT :+ tn
        }
      }
      mult0(gt, s.tail,keepT)
    } else false
  }
}


/////////////////////
// Precedences
/////////////////////

trait Precedence {
  import leo.datastructures.Orderings._
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
  final val arityInvOrder: Precedence = Prec_ArityInvOrder
  final val arityOrder_UnaryFirst: Precedence = Prec_ArityOrder_UnaryFirst
  final val arityInvOrder_UnaryFirst: Precedence = Prec_ArityInvOrder_UnaryFirst
}

///////////////////////
/// Term Orderings
///////////////////////

object TermOrdering {
//  import leo.datastructures.impl.orderings._
//  final val senseless: TermOrdering = TO_SenselessOrdering
//  final val sizeBased: TermOrdering = TO_SizedBased
}

/////////////////////
// Clause Orderings
/////////////////////

object ClauseOrdering {
  import leo.datastructures.impl.orderings._
  /** Lexicographic clause ordering on the 3-tuple (clause weight, clause age, clause origin) */
  final val lex_WeightAgeOrigin: ClauseOrdering = CLO_Lex_WeightAgeOrigin
}

object ClauseProxyOrderings {
  import leo.datastructures.impl.orderings._

  final val fifo: ClauseProxyOrdering = CLPO_FIFO
  final val lex_weightAge = CLPO_Lex_WeightAge
  final val goalsfirst = CLPO_GoalsFirst
}

///////////////////////
/// Type Orderings
///////////////////////



//////////////////////
// Associated traits
//////////////////////
//
//trait LeoOrdering[A] {
//  import leo.datastructures.Orderings._
//
//  def gt(s: A, t: A): Boolean
//  def gteq(s: A, t: A): Boolean
//
//  // Defined by gt/ge
//  @inline final def lt(s: A, t: A): Boolean = gt(t,s)
//  @inline final def lteq(s: A, t: A): Boolean = gteq(t,s)
//
//  @inline final def compare(s: A, t: A): CMP_Result = {
//    if (s == t) CMP_EQ
//    else if (gt(s,t)) CMP_GT
//    else if (lt(s,t)) CMP_LT
//    else CMP_NC
//  }
//  @inline final def canCompare(s: A, t: A): Boolean = compare(s,t) != CMP_NC
//}

