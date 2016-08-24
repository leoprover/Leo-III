package leo.datastructures.impl

import leo.datastructures.{LitFalse, LitTrue, Term, Literal}


protected[impl] sealed abstract class LiteralImpl extends Literal {
  @inline final protected def printPol(pol: Boolean): String = if (pol) "t" else "f"
  override lazy val pretty: String = s"[${left.pretty} ≈ ${right.pretty}]^${printPol(polarity)}"
}

object LiteralImpl {
  private var litCounter : Int = 0

  /** Create new (equational) literal with equation `left = right`
    * and polarity `polarity`. Note that the resulting literal is only
    * equational if `right != $true/$false`. */
  final def mkLit(left: Term, right: Term, pol: Boolean): Literal = {
    litCounter += 1

    if (left == LitFalse()) {
      return new NonEqLiteral(litCounter, right, !pol)
    } else if (right == LitFalse()) {
      return new NonEqLiteral(litCounter, left, !pol)
    } else if (left == LitTrue()) {
      return new NonEqLiteral(litCounter, right, pol)
    } else if (right == LitTrue()) {
      return new NonEqLiteral(litCounter, left, pol)
    }
    new EqLiteral(litCounter,left,right,pol)
  }
  /** Create new (non-equational) literal with equation
    * `left = $true` and polarity `pol`. */
  final def mkLit(t: Term, pol: Boolean): Literal = {
    litCounter += 1
    new NonEqLiteral(litCounter, t, pol)
  }


  private final class EqLiteral(val id: Int,
                                t1: Term,
                                t2: Term,
                                val polarity: Boolean) extends LiteralImpl {
    import leo.Configuration.{TERM_ORDERING => ord}
    /** The left side of the literal's equation.
      * Invariant: `left >= right or !oriented` where > is a term ordering. */
    val left: Term = if (ord.gteq(t1,t2)) t1 else t2
    /** The left side of the literal's equation.
      * Invariant: `!equational => right = $true or right = $false`.
      * Invariant: `left >= right or !oriented` where `>` is a term ordering. */
    val right: Term = if (ord.gteq(t1,t2)) t2 else t1
    /** Whether the equation could have been oriented wrt. a term ordering `>`. */
    val oriented: Boolean = ord.canCompare(t1,t2)

    /** Returns a term representation of the literal.
      * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
      *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
      *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ true`). */
    lazy val term: Term = {
      import leo.datastructures.{=== => EQ, Not}
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

  private final class NonEqLiteral(val id: Int,
                                   val left: Term,
                                   val polarity: Boolean) extends LiteralImpl {
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
      import leo.datastructures.Not
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
