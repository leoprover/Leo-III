package leo.datastructures

import leo.datastructures.impl.Signature
import leo.{ClauseOrdering, TermOrdering, TypeOrdering}
import leo.datastructures.term.Term
import leo.datastructures.term.Term.{:::>, TypeLambda,∙,Symbol, Bound}

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
  def compare(a: Term, b: Term) = Some(0)
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

/** Polymorphic higher-order recursive path ordering on terms of same type, as given by Jouannaud and Rubio
  * in "The Higher-Order Recursive Path Ordering",1999, doi: 10.1109/LICS.1999.782635  */
object PolyHORecPathOrdering extends TermOrdering {
  def compare(a: Term, b: Term) = {
    // Only terms with equivalent type can be compared
    if (a.ty != b.ty)
      None
    else if (a == b)
      Some(0)
    else {
      compare0(a,b)
    }
  }

  private def compare0(a: Term, b: Term): Option[Int] = {
    assert(a.ty == b.ty)

    if (a == b) {
      Some(0)
    } else if (a.isVariable || b.isVariable) {
      // Variables cannot be compared since orderings are only defined on ground terms.
      None
    } else if (a.isTypeAbs && !b.isTypeAbs || !a.isTypeAbs && b.isTypeAbs) {
      // adapted: If only one term is a type abstraction but the other is not,
      // the terms are not comparable
      None
    } else if (a.isTypeAbs && b.isTypeAbs) {
      // adapted: both terms are type abstractions, compare recursively
      val body1 = TypeLambda.unapply(a).get
      val body2 = TypeLambda.unapply(b).get
      compare0(body1, body2)
    } else if (a.isTermAbs && !b.isTermAbs || !a.isTermAbs && b.isTermAbs) {
      // If only one term is an abstraction but the other is not,
      // the terms are not comparable
      None
    } else if (a.isTermAbs && b.isTermAbs) {
      // Case (7), both terms are abstractions, compare recursively
      val (_, body1) = :::>.unapply(a).get
      val (_, body2) = :::>.unapply(b).get
      compare0(body1, body2)
    } else {
      lazy val (head1, spine1) = ∙.unapply(a).get
      lazy val (head2, spine2) = ∙.unapply(b).get

      lazy val argList1 = filterTermArgs(spine1)
      lazy val argList2 = filterTermArgs(spine2)
      head1 match {
        case Symbol(k1) => {
          // Case (1), > direction
          val it = argList1.iterator
          var stop = false
          while (it.hasNext && !stop) {
            compare0(it.next(), b) match {
              case Some(res) if res >= 0 => {stop = true}
              case _ => ;
            }
          }
          if (stop) { // subterm greater than b was found, return result > 0
            Some(1)
          } else { // continue serach
            head2 match {
              case Symbol(k2) => {
                // Cases 2,3,4, both directions

                if (k1 > k2) {
                  // Case 2, > direction
                  if ((argList2).forall(arg =>
                    compare0(a, arg).getOrElse(Int.MinValue) > 0 || argList1.exists(compare0(_,arg).getOrElse(Int.MinValue) >= 0))) {
                    Some(1)
                  } else {
                    None
                  }
                } else if (k2 < k1) {
                  // Case 2, < direction
                  if ((argList1).forall(arg =>
                    compare0(a, arg).getOrElse(Int.MaxValue) < 0 || argList2.exists(compare0(_,arg).getOrElse(Int.MaxValue) <= 0))) {
                    Some(-1)
                  } else {
                    None
                  }
                } else {
                  // Case 3,4
                  val sig = Signature.get
                  val status = sig(k1).status

                  if (status == 0) { // Multiset
                    mult(argList1, argList2)
                  } else if (status == 1) { // Lexicographic
                    lex(argList1, argList2) match {
                      case None => None
                      case Some(r) if r == 0 => None
                      case Some(r) if r > 0 => if ((argList2).forall(arg =>
                        compare0(a, arg).getOrElse(Int.MinValue) > 0 || argList1.exists(compare0(_,arg).getOrElse(Int.MinValue) >= 0))) {
                        Some(1)
                      } else {
                        None
                      }
                      case Some(r) if r < 0 => if ((argList1).forall(arg =>
                        compare0(a, arg).getOrElse(Int.MaxValue) < 0 || argList2.exists(compare0(_,arg).getOrElse(Int.MaxValue) <= 0))) {
                        Some(-1)
                      } else {
                        None
                      }
                    }
                  } else {
                    assert(false)
                    throw new IllegalArgumentException("this should not happen")
                  }
                }
              }
              case _ => { // Case 5, > direction
                if ((head2 +: argList2).forall(arg =>
                  compare0(a, arg).getOrElse(Int.MinValue) > 0 || argList1.exists(compare0(_,arg).getOrElse(Int.MinValue) >= 0))) {
                  Some(1)
                } else {
                  None
                }
              }
            }
          }
        }
        case _ => {
          //head2 could be signature constant
          //some application (either i.S or t'.S)
          head2 match {
            case Symbol(k2) => {
              // Case 1, < direction
              val it = argList2.iterator
              var stop = false
              while (it.hasNext && !stop) {
                compare0(it.next(), a) match {
                  case Some(res) if res <= 0 => {
                    stop = true
                  }
                  case _ => ;
                }
              }
              if (stop) {
                Some(-1)
              } else {
                // case 5, < direction
                if ((head1 +: argList1).forall(arg =>
                  compare0(a, arg).getOrElse(Int.MaxValue) < 0 || argList2.exists(compare0(_,arg).getOrElse(Int.MaxValue) <= 0))) {
                  Some(-1)
                } else {
                  None
                }
              }
            }
            case _ => mult((head1 +: argList1),(head2 +: argList2)) // case 6 both directions, adopted
          }
        }
      }
    }
  }

  private def filterTermArgs(args: Seq[Either[Term, Type]]): Seq[Term] = args match {
    case Seq() => Seq()
    case Seq(h, rest@_*) => h match {
      case Left(term) => term +: filterTermArgs(rest)
      case Right(_) => filterTermArgs(rest)
    }
  }

  private def lex(a: Seq[Term], b: Seq[Term]): Option[Int] = a match {
    case Seq() if b.isEmpty => Some(0)
    case Seq() => Some(-1)
    case Seq(_, rest@_*) if b.isEmpty => Some(1)
    case Seq(t1, tn@_*) => compare0(t1, b.head) match {
      case None => None
      case Some(r) if r == 0 => lex(tn, b.tail)
      case r => r
    }
  }

  private def mult(a: Seq[Term], b: Seq[Term]): Option[Int] = {
    a match {
      case Seq() if b.isEmpty => Some(0)
      case Seq() => Some(-1)
      case _ if b.isEmpty => Some(1)
      case _ => {
        val aMax = maximalElement(a)
        val bMax = maximalElement(b)

        compare(aMax, bMax)match {
          case None => None
          case Some(r) if r == 0 => mult(a.diff(Seq(aMax)), b.diff(Seq(bMax)))
          case r => r
        }
      }
    }
  }

  private def maximalElement(a: Seq[Term]): Term = {
    val it = a.iterator
    var curMax = a.head
    while(it.hasNext) {
      val cur = it.next()
      compare(curMax, cur) match {
        case None => ;
        case Some(r) if r < 0 => curMax = cur
      }
    }
    curMax
  }
}

/**
 * `HORPO` as given by "Polymorphic higher-order recursive path ordering" by Jouannaud and Rubio in
 * Journal of the ACM, Vol 54. No 1. Article 2, March 2007.
 *
 * Since we only work on beta-normalized terms, we remove the functionality
 * property requirement from the ordering, i.e. we do not follow/track possible beta-
 * and eta-normalization steps during the comparison. More concretely, we drop cases (11) and (12) of
 * the original definition.
 */
object RPO extends TermOrdering {
  val GT = Some(1)
  val LT = Some(-1)
  val tyO: TypeOrdering = ???
  val prec: Ordering[Signature#Key] = new Ordering[Signature#Key] {
    def compare(x: Signature#Key, y: Signature#Key) = x - y
  }

  def compare(a: Term, b: Term) = {
    if (a == b)
      Some(0)
    else
      tyO.compare(a.ty, b.ty).flatMap(doCompare(a,b,_))
  }

  private def compare0(a: Term, b: Term, cmpTo: Int): Option[Int] = {
    if (a == b) {
      if (cmpTo == 0) Some(0)
      else None
    } else {
      val tyCmp = tyO.compare(a.ty, b.ty)

      // Either the type order is still compatible, or the terms cannot be ordered
      // (i.e. type incomparable or ordering not compatible to root type ordering result).
      if (tyCmp.isDefined && compatible(cmpTo, tyCmp.get)) {
        doCompare(a, b, cmpTo)
      } else {
        None
      }
    }

  }

  private def doCompare(a: Term, b: Term, cmp: Int): Option[Int] = {
    var res: Option[Int] = None


    if (res.isEmpty && isFuncSymbApp(a) && cmp >= 0) {
      val (_, spineA) = ∙.unapply(a).get
      val argsA = filterTermArgs(spineA) // TODO: ???

      // Case (1) >-Direction
      if (argsA.exists(gteq(_, b))) {
        res = GT
      }

      // Case (7) >-Direction
      if (res.isEmpty && isAppWithoutFuncSymb(b)) {
        val (headB, spineB) = ∙.unapply(b).get
        val argsB = filterTermArgs(spineB)
        if ( A2(a, argsA, headB +: argsB, cmp)) {
          res = GT
        }
      }

      // Case (8) >-Direction
      if (res.isEmpty && b.isTermAbs) {
        val (_, bodyB) = :::>.unapply(b).get

        if (!bodyB.looseBounds.contains(1)) {
          res = compare0(a, bodyB, cmp)
        }
      }
    }


    if (res.isEmpty && isFuncSymbApp(b) && cmp <= 0) {
      val (_, spineB) = ∙.unapply(b).get
      val argsB = filterTermArgs(spineB)

      // Case (1) <-Direction
      if (argsB.exists(lteq(a, _))) {
        res = LT
      }

      // Case (7) <-Direction
      if (res.isEmpty && isAppWithoutFuncSymb(a)) {
        val (headA, spineA) = ∙.unapply(a).get
        val argsA = filterTermArgs(spineA)

        if (A2(b,argsB, headA +: argsA, -cmp)) {  // TODO: Swapping of cmp is not intuitive??
          res = LT
        }
      }

      // Case (8) <-Direction
      if (res.isEmpty && a.isTermAbs) {
        val (_, bodyA) = :::>.unapply(a).get

        if (!bodyA.looseBounds.contains(1)) {
          res = compare0(bodyA, b, cmp)
        }
      }
    }

    // Cases (2),(3),(4)
    if (res.isEmpty && isFuncSymbApp(a) && isFuncSymbApp(b)) {
      val (headA, spineA) = ∙.unapply(a).get
      val (headB, spineB) = ∙.unapply(b).get

      val funcA = Symbol.unapply(headA).get
      val funcB = Symbol.unapply(headB).get

      val headPrec = prec.compare(funcA, funcB)
      if (compatible(cmp, headPrec)) {
        val (argsA, argsB) = (filterTermArgs(spineA), filterTermArgs(spineB))
        if (headPrec > 0) {
          // Case (2), >-direction
          if (A2(a, argsA, argsB, cmp))
            res = GT
        } else if (headPrec < 0) {
          // Case (2), <-direction
          if (A2(b, argsB, argsA, -cmp)) // TODO: Swapping of cmp is not intuitive??
            res = LT
        } else {
          // Case (3) and (4)
          if (Signature.get.apply(funcA).hasMultStatus) {
            // Mult comparison
            val multCmp = mult(argsA, argsB)
            if (multCmp.isDefined) {
              val multCmpValue = multCmp.get

              if (multCmpValue > 0) {
                if (compatible(cmp, multCmpValue)) {
                  res = GT
                }
              } else if (multCmpValue < 0) {
                if (compatible(cmp, multCmpValue)) {
                  res = LT
                }
              } else {
                // TODO: ???
              }
            }
          } else {
            // Lex comparison
            val lexCmp = lex(argsA, argsB)
            if (lexCmp.isDefined) {
              val lexCmpVal = lexCmp.get
              // compatible check not needed since given to lex method. will return none if incompatible
              if (lexCmpVal > 0) {
                if (compatible(cmp, lexCmpVal) && A2(a, argsA, argsB, cmp)) {
                  res = GT
                }
              } else if (lexCmpVal < 0) {
                if (compatible(cmp, lexCmpVal) && A2(b, argsB, argsA, -cmp)) { // TODO: Swapping of cmp is not intuitive??
                  res = LT
                }
              } else {
                // TODO: What to do here? can this happen?
              }
            }
          }
        }
      }

    }

    /** Implements case (10) */
    if (res.isEmpty && a.isTermAbs && b.isTermAbs) {
      val (varTyA, bodyA) = :::>.unapply(a).get
      val (varTyB, bodyB) = :::>.unapply(b).get

      if (tyO.eq(varTyA, varTyB)) {
        res = compare0(bodyA, bodyB, cmp)
      }
    }

    if (res.isEmpty && a.isTermAbs) {
      val (_, bodyA) = :::>.unapply(a).get
      if (cmp >= 0) {
        /** Implements case (6), >=-direction */
        res = compare0(bodyA, b, cmp)
      }
      if (cmp <= 0) {
        /** Implements case (12), <=-direction */
        // eta contract a to `a2`
        val a2 = a.topEtaContract
        if (a != a2)
          res = join(res,compare0(a2, b, cmp))
      }
    }

    if (res.isEmpty && b.isTermAbs) {
      if (cmp >= 0) {
        /** Implements case (12), >-direction */
        // eta contract b to `b2`
        val b2 = b.topEtaContract
        if (b != b2)
          res = compare0(a, b2, cmp)
      }
      if (cmp <= 0) {
        /** Implements case (6), <-direction */
        val (_, bodyB) = :::>.unapply(b).get
        res = join(res,compare0(a, bodyB, cmp))
      }
    }

    if (res.isEmpty && a.isTypeAbs && b.isTypeAbs) {
      val bodyA = TypeLambda.unapply(a).get
      val bodyB = TypeLambda.unapply(b).get

      res = compare0(bodyA, bodyB, cmp)
    }

    // if a is type abstraction and b is type abstraction
    // if a

    res
  }
  // wenn cmp < 0, dann a < b, sonst wenn cmp > 0 dann a > b
  private def nextCase(res: Option[Int]): Boolean = res.isEmpty

  private def filterTermArgs(args: Seq[Either[Term, Type]]): Seq[Term] = args match {
    case Seq() => Seq()
    case Seq(h, rest@_*) => h match {
      case Left(term) => term +: filterTermArgs(rest)
      case Right(_) => filterTermArgs(rest)
    }
  }


  private def join(a: Option[Int], b: => Option[Int]): Option[Int] = if (a.isDefined) a else b

  private def isFuncSymbApp(a: Term): Boolean = {
    if (!a.isApp) false
    else {
      val (head1, _) = ∙.unapply(a).get
      head1.isConstant
    }
  }

  private def isAppWithoutFuncSymb(a: Term): Boolean = {
    if (!a.isApp) false
    else {
      val (head1, _) = ∙.unapply(a).get
      !head1.isConstant
    }
  }

  def A(a: Term, argsA: Seq[Either[Term,Type]], argsB: Seq[Either[Term,Type]], cmp: Int): Boolean = {
    argsB.forall(_ match {
      case Left(t) => compare0(a, t,cmp).getOrElse(-4711) > 0 || argsB.exists(_.fold(compare0(_,t,cmp).getOrElse(-4711) > 0, _ => false))
      case _ => true
    })
  }

  def A2(a: Term, argsA: Seq[Term], argsB: Seq[Term], cmp: Int): Boolean = {
    argsB.forall(t => compare0(a, t,cmp).getOrElse(-4711) > 0 || argsB.exists(compare0(_,t,cmp).getOrElse(-4711) > 0))
  }

  def compatible(cmp1: Int, cmp2: Int):Boolean = Math.abs(cmp1.signum - cmp2.signum) <= 1

  private def lex(a: Seq[Term], b: Seq[Term]): Option[Int] = a match {
    case Seq() if b.isEmpty => Some(0)
    case Seq() => Some(-1)
    case Seq(_, rest@_*) if b.isEmpty => Some(1)
    case Seq(t1, tn@_*) => compare(t1, b.head) match {
      case None => None
      case Some(r) if r == 0 => lex(tn, b.tail)
      case r => r
    }
  }

  private def mult(a: Seq[Term], b: Seq[Term]): Option[Int] = {
    a match {
      case Seq() if b.isEmpty => Some(0)
      case Seq() => Some(-1)
      case _ if b.isEmpty => Some(1)
      case _ => {
        val aMax = maximalElement(a)
        val bMax = maximalElement(b)

        compare(aMax, bMax)match {
          case None => None
          case Some(r) if r == 0 => mult(a.diff(Seq(aMax)), b.diff(Seq(bMax)))
          case r => r
        }
      }
    }
  }

  private def maximalElement(a: Seq[Term]): Term = {
    val it = a.iterator
    var curMax = a.head
    while(it.hasNext) {
      val cur = it.next()
      compare(curMax, cur) match {
        case None => ;
        case Some(r) if r < 0 => curMax = cur
      }
    }
    curMax
  }

}

///////////////////////
/// Type Orderings
///////////////////////



///////////////////////
/// Generic Orderings
///////////////////////



//////////////////////
// Associated traits
//////////////////////

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

