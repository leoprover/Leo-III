package leo.modules.procedures

import leo.datastructures.Term.:::>
import leo.datastructures.{BoundFront, Signature, Subst, Term, Type}
import leo.datastructures.Term.local._
import leo.modules.HOLSignature.{&, Exists, Forall, HOLUnaryConnective, Impl, Not, |||}

/**
  * Miniscoping of formulas (does not assume NNF).
  *
  * @author Max Wisniewski
  * @note Moved and to be refactored.
  */
object Miniscoping extends Function1[Term, Term] {
  final def apply(term: Term, polarity: Boolean): Term = apply0(term, polarity, Vector.empty)
  final def apply(term: Term): Term = apply(term, polarity = true)


  type QUANT_LIST = Seq[(Boolean, Type)]
  type QUANT_ITERATOR = Iterator[(Boolean, Type)]

  type PUSH_TYPE = Int
  @inline final val BOTH : PUSH_TYPE = 3
  @inline final val NONE : PUSH_TYPE = 0
  @inline final val LEFT : PUSH_TYPE = 1
  @inline final val RIGHT : PUSH_TYPE = 2

  /**
    *
    * Performs miniscoping.
    * quants is a stack of removed quantifiers, where
    * (true, ty) --> Forall(\(ty)...)
    * (false, ty) --> Exists(\(ty)...)
    *
    * @param t The term to miniscope
    * @param pol The current polarity
    * @param quants The current quantifier
    * @param sig the signature
    * @return a miniscoped term
    */
  private[this] final def apply0(t : Term, pol : Boolean, quants : QUANT_LIST): Term = {
    t match {
      case Exists(ty :::> body) => apply0(body, pol, quants :+ (!pol, ty))
      case Forall(ty :::> body) => apply0(body, pol, quants :+ (pol, ty))
      case Not(a) => (apply0(a, !pol, quants))
      case (a & b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, pol)
        val amini = apply0(a.substitute(leftSub).betaNormalize, pol, leftQ)
        val bmini = apply0(b.substitute(rightSub).betaNormalize, pol, rightQ)
        prependQuantList(&(amini, bmini), pol, rest)
      case (a ||| b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, !pol)
        val amini = apply0(a.substitute(leftSub).betaNormalize, pol, leftQ)
        val bmini = apply0(b.substitute(rightSub).betaNormalize, pol, rightQ)
        prependQuantList(|||(amini, bmini), pol, rest)
      case Impl(a, b) =>
        val (rest, leftQ, leftSub, rightQ, rightSub) = pushQuants(a, b, quants, pol, !pol)
        val amini = apply0(a.substitute(leftSub), !pol, leftQ)
        val bmini = apply0(b.substitute(rightSub), pol, rightQ)
        prependQuantList(Impl(amini, bmini), pol, rest)
      case other =>
        prependQuantList(other, pol, quants.reverseIterator)
    }
  }

  /**
    *
    * @param left The left side of the operator
    * @param right The right side of the operator
    * @param quants The quantifiers seen to this point
    * @param pol the current polarity
    * @param and if(true) op = AND else op = OR
    * @return
    */
  private[this] final def pushQuants(left : Term, right : Term, quants : QUANT_LIST, pol : Boolean, and : Boolean) : (QUANT_ITERATOR, QUANT_LIST, Subst, QUANT_LIST, Subst) = {
    val it = quants.reverseIterator
    var leftQ : QUANT_LIST = Vector() // Quantifiers pushed left
    var leftSubst : Seq[Int] = Seq()  // Substitution (reversed) removed Quants left
    var rightQ : QUANT_LIST = Vector()  // Quantifiers pushed right
    var rightSubst : Seq[Int] = Seq()  // Substitution (reversed) removed Quants right
    var loop = 1
    while(it.hasNext){
      val q@(quant , ty) = it.next()
      val push = testPush(left, right, loop, quant, and)
      if(push != 0) {
        if ((push & LEFT) == LEFT) leftQ = q +: leftQ // Push the quantifier left if possible
        val nFrontl = leftQ.size
        leftSubst = (if(nFrontl > 0) nFrontl else 1) +: leftSubst      // Update indizes

        if((push & RIGHT) == RIGHT) rightQ = q +: rightQ
        val nFrontr = rightQ.size
        rightSubst = (if(nFrontr > 0) nFrontr else 1)  +: rightSubst
      } else {
        val lSub = revListToSubst(leftSubst, leftQ.size)
        val rSub = revListToSubst(rightSubst, rightQ.size)
        return (Iterator(q)++it, leftQ, lSub, rightQ, rSub)
      }
      loop += 1
    }
    return (it, leftQ, revListToSubst(leftSubst, leftQ.size), rightQ, revListToSubst(rightSubst, rightQ.size))
  }

  @inline private[this] final def revListToSubst(preSubst : Seq[Int], shift : Int) = {
    var s : Subst = Subst.shift(shift)
    val it = preSubst.iterator
    while(it.hasNext){
      s = BoundFront(it.next()) +: s
    }
    s
  }

  @inline private[this] final def testPush(left : Term, right : Term, bound : Int, quant : Boolean, and : Boolean) : PUSH_TYPE = {
    var result = 0
    if (left.looseBounds.contains(bound)) result |= 1
    if (right.looseBounds.contains(bound)) result |= 2

    if((!quant && and || quant && !and) && result == 3) 0
    else result
  }


  /**
    * @param quants Reverse Iterator of the quantifier prefix
    */
  private[this] final def prependQuantList(t : Term, pol : Boolean, quants : QUANT_ITERATOR) : Term = {
    var itTerm : Term = t
    while(quants.hasNext){
      val (q, ty) = quants.next()
      itTerm = quantToTerm(q, pol)(\(ty)(itTerm))
    }
    itTerm
  }

  private[this] final def quantToTerm(quant : Boolean, pol : Boolean) : HOLUnaryConnective = {
    val realQuant = if(pol) quant else !quant
    if(realQuant) Forall else Exists
  }
}
