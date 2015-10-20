package leo.datastructures.impl

import leo.datastructures.{LitFalse, LitTrue, Term, Literal}


protected[impl] sealed abstract class LiteralImpl extends Literal {
  @inline final protected def printPol(pol: Boolean): String = if (pol) "t" else "f"
  override def pretty: String = s"[${left.pretty} ≈ ${right.pretty}]^${printPol(polarity)}"
}

object LiteralImpl {
  private var litCounter : Int = 0

  /** Create new (equational) literal with equation `left = right`
    * and polarity `polarity`. Note that the resulting literal is only
    * equational if `right != $true/$false`. */
  final def mkLit(left: Term, right: Term, pol: Boolean): Literal = {
    litCounter += 1

    if (left == LitFalse()) {
      return new NonEqLiteral(litCounter, right, false, pol)
    } else if (right == LitFalse()) {
      return new NonEqLiteral(litCounter, left, false, pol)
    } else if (left == LitTrue()) {
      return new NonEqLiteral(litCounter, right, true, pol)
    } else if (right == LitTrue()) {
      return new NonEqLiteral(litCounter, left, true, pol)
    }
    new EqLiteral(litCounter,left,right,pol)
  }
  /** Create new (non-equational) literal with equation
    * `left = right ≡ $true/$false` and polarity `polarity`. */
  final def mkLit(t: Term, right: Boolean, pol: Boolean): Literal = {
    litCounter += 1
    new NonEqLiteral(litCounter, t, right, pol)
  }


  private final class EqLiteral(val id: Int,
                                t1: Term,
                                t2: Term,
                                val polarity: Boolean) extends LiteralImpl {
    import leo.Configuration.{TERM_ORDERING => ord}
    /** The left side of the literal's equation.
      * Invariant: `left >= right or !oriented` where > is a term ordering. */
    val left: Term = if (ord.gteq(t1,t2, metaVars.map {case (ty,sc) => Term.mkMetaVar(ty,sc)})) t1 else t2
    /** The left side of the literal's equation.
      * Invariant: `!equational => right = $true or right = $false`.
      * Invariant: `left >= right or !oriented` where `>` is a term ordering. */
    val right: Term = if (ord.gteq(t1,t2, metaVars.map {case (ty,sc) => Term.mkMetaVar(ty,sc)})) t2 else t1
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
    val flexflex: Boolean = uni && (left.isApp || left.isAtom) && (right.isApp || right.isAtom) && left.headSymbol.isVariable && right.headSymbol.isVariable
    /** Returns true iff the literal has a flexible head. */
    val flexHead: Boolean = false

  }

  private final class NonEqLiteral(val id: Int,
                                   val left: Term,
                                   rt: Boolean,
                                   val polarity: Boolean) extends LiteralImpl {
    /** The left side of the literal's equation.
      * Invariant: `!equational => right = $true or right = $false`.
      * Invariant: `left > right or !oriented` where `>` is a term ordering. */
    val right: Term = {
      import leo.datastructures.{LitTrue,LitFalse}
      if (rt) LitTrue else LitFalse
    }
    /** Whether the equation could have been oriented wrt. a term ordering `>`. */
    val oriented: Boolean = true

    /** Returns a term representation of the literal.
      * @return Term `s = t` if `polarity`; term `!(s = t)` if `!polarity`,
      *         where `s ≡ left and t ≡ right`. If `!equational` the equality may
      *         be contracted, e.g. `!s` instead of `s = $false` (here `polarity ≡ true`). */
    lazy val term: Term = {
      import leo.datastructures.Not
      if (polarity == rt) left else Not(left)
    }

    /** Returns true iff the literal is equational, i.e. iff `l` is an equation `s = t` and not
      * `s = $true` or `s = $false`. */
    val equational: Boolean = false
    /** Returns true iff the literal is propositional. */
    lazy val propositional: Boolean = left.isAtom

    /** Returns true iff the literal is a positive equation. */
    val equation: Boolean = false
    /** Returns true iff the literal is an unification constraint. */
    val uni: Boolean = false
    /** Returns true iff the literal is a flex-flex unification constraint, */
    val flexflex: Boolean = false
    /** Returns true iff the literal has a flexible head. */
    lazy val flexHead: Boolean = !left.isTermAbs && !left.isTypeAbs && left.headSymbol.isVariable
  }
}
