package leo.datastructures.impl

import leo.datastructures.{Term, Literal, Signature}
import leo.modules.HOLSignature.{LitFalse, LitTrue}


protected[impl] sealed abstract class LiteralImpl extends Literal {
  @inline final private def printPol(pol: Boolean): String = if (pol) "t" else "f"
  override lazy val pretty: String = s"[${left.pretty} ≈ ${right.pretty}]^${printPol(polarity)}"
  final def pretty(sig: Signature): String = s"[${left.pretty(sig)} ≈ ${right.pretty(sig)}]^${printPol(polarity)}"
}

object LiteralImpl {
  private var litCounter : Int = 0

  /** Creates a new (equational) literal with equation `left = right`
    * and polarity `polarity` which is unordered.
    * Note that the resulting literal is only
    * equational if `left/right != $true/$false`. */
  final def mkLit(left: Term, right: Term, pol: Boolean, oriented: Boolean = false): Literal = {
    litCounter += 1

    if (left == LitFalse()) {
      NonEqLiteral(litCounter, right, !pol)
    } else if (right == LitFalse()) {
      NonEqLiteral(litCounter, left, !pol)
    } else if (left == LitTrue()) {
      NonEqLiteral(litCounter, right, pol)
    } else if (right == LitTrue()) {
      NonEqLiteral(litCounter, left, pol)
    } else EqLiteral(litCounter,left,right,pol,oriented)
  }

  /** Creates a new (equational) literal of the two terms t1 and t2
    * and polarity `polarity`. During construction, the method
    * tries to order the two terms into and ordered equation left=right,
    * where left >= right wrt. term ordering. If this fails,
    * t1 will be used as left and t2 as right.
    * Note that the resulting literal is only
    * equational if both terms t1 and t2 and not equivalent to $true/$false. */
  final def mkOrdered(t1: Term, t2: Term, pol: Boolean)(implicit sig: Signature): Literal = {
    litCounter += 1

    if (t1 == LitFalse()) {
      NonEqLiteral(litCounter, t2, !pol)
    } else if (t2 == LitFalse()) {
      NonEqLiteral(litCounter, t1, !pol)
    } else if (t1 == LitTrue()) {
      NonEqLiteral(litCounter, t2, pol)
    } else if (t2 == LitTrue()) {
      NonEqLiteral(litCounter, t1, pol)
    } else {
      import leo.Configuration.{TERM_ORDERING => ord}
      import leo.datastructures.{CMP_EQ, CMP_GT, CMP_LT}
      val cmpResult = ord.compare(t1,t2)(sig)
      if (cmpResult == CMP_EQ || cmpResult == CMP_GT)
        EqLiteral(litCounter,t1,t2,pol,true)
      else if (cmpResult == CMP_LT)
        EqLiteral(litCounter,t2,t1,pol,true)
      else  /* else not comparable */
        EqLiteral(litCounter,t1,t2,pol,false)
    }
  }

  /** Create new (non-equational) literal with equation
    * `left = $true` and polarity `pol`. */
  final def mkLit(t: Term, pol: Boolean): Literal = {
    litCounter += 1
    NonEqLiteral(litCounter, t, pol)
  }


  private final case class EqLiteral(id: Int,
                                     left: Term,
                                     right: Term,
                                     polarity: Boolean,
                                     oriented: Boolean) extends LiteralImpl {

    /** Returns a term representation of the literal.
      * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
      *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
      *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ true`). */
    lazy val term: Term = {
      import leo.modules.HOLSignature.{=== => EQ, Not}
      if (polarity) EQ(left,right) else Not(EQ(left,right))
    }

    /** Returns true iff the literal is equational, i.e. iff `l` is an equation `s = t` and not
      * `s = $true` or `s = $false`. */
    val equational: Boolean = true
    /** Returns true iff the literal is propositional. */
    val propositional: Boolean = false

    /** Returns true iff the literal is a positive equation. */
    val equation: Boolean = polarity
    /** Returns true iff the literal is an unification constraint. */
    val uni: Boolean = !polarity
    /** Returns true iff the literal is a flex-flex unification constraint, */
    val flexflex: Boolean = uni && left.flexHead && right.flexHead
    /** Returns true iff the literal has a flexible head. */
    val flexHead: Boolean = false

  }

  private final case class NonEqLiteral(id: Int,
                                   left: Term,
                                   polarity: Boolean) extends LiteralImpl {
    /** The left side of the literal's equation.
      * Invariant: `!equational => right = $true or right = $false`.
      * Invariant: `left > right or !oriented` where `>` is a term ordering. */
    val right: Term = LitTrue
    /** Whether the equation could have been oriented wrt. a term ordering `>`. */
    val oriented: Boolean = true

    /** Returns a term representation of the literal.
      * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
      *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
      *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ true`). */
    lazy val term: Term = {
      import leo.modules.HOLSignature.Not
      if (polarity) left else Not(left)
    }

    /** Returns true iff the literal is equational, i.e. iff `l` is an equation `s = t` and not
      * `s = $true` or `s = $false`. */
    val equational: Boolean = false
    /** Returns true iff the literal is propositional. */
    val propositional: Boolean = left.isAtom

    /** Returns true iff the literal is a positive equation. */
    val equation: Boolean = false
    /** Returns true iff the literal is an unification constraint. */
    val uni: Boolean = false
    /** Returns true iff the literal is a flex-flex unification constraint, */
    val flexflex: Boolean = false
    /** Returns true iff the literal has a flexible head. */
    val flexHead: Boolean = left.isApp && left.headSymbol.isVariable
  }
}
