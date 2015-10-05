package leo.datastructures.impl.orderings

import leo._
import leo.datastructures.Term.{∙, :::>, TypeLambda, Bound, Symbol}
import leo.datastructures.Type.{∀, BoundType, BaseType,->,+,*}
import leo.datastructures.{QuasiOrdering, Orderings, Type, Term}
import leo.datastructures.impl.Signature

import scala.annotation.tailrec


/** Only for debugging and compiling purposes. Will be removed soon. */
object TO_SenselessOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = Some(0)
}


/** Simple linear well-founded AC-compatible ordering, taken from Isabelle. Adapted.
  *  s < t <=> 1. size(s) < size(t) or
  *          2. size(s) = size(t) and s=f(...) and t=g(...) and f<g or
  *          3. size(s) = size(t) and s=f(s1..sn) and t=f(t1..tn) and
  *             (s1..sn) < (t1..tn) (lexicographically)
  */
object TO_SizedBased extends TermOrdering {
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