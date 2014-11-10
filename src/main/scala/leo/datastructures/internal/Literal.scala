package leo.datastructures.internal


import leo.datastructures.Pretty
import leo.datastructures.internal.terms.Term
import leo.datastructures.internal.{=== => EQ}

/**
 * Interface for literals, companion object `Literal` provides constructor methods.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Literal extends Pretty {

  /** Returns the literal's underlying term. */
  def term: Term
  /** The polarity of the literal, where positive polarity is encoded by the Boolean value `true`,
    * negative polarity by Boolean value `false`. */
  def polarity: Boolean
  /** The weight of the literal. */
  def weight: Int

  /** Returns true iff the literal is a flex-flex-literal. */
  def isFlexFlex: Boolean
  /** Returns true iff the literal is an unification constraint. */
  def isUni: Boolean
}

object Literal extends Function2[Term, Boolean, Literal]{

  /** Create a literal of the term `t` and polarity `pol`. */
  def mkLit(t: Term, pol: Boolean): Literal = ???

  // Convenience methods
  def apply(t: Term, pol: Boolean): Literal = mkLit(t, pol)
  /** Create a literal of the term `t` and positive polarity. */
  def mkPosLit(t: Term): Literal = mkLit(t, true)
  /** Create a literal of the term `t` and negative polarity. */
  def mkNegLit(t: Term): Literal = mkLit(t, false)
  /** Create a literal of the form `left == right` (i.e. equality with positive polarity). */
  def mkEqLit(left: Term, right: Term): Literal = mkLit(EQ(left, right), true)
  /** Create a literal of the form `left != right` (i.e. equality with negative polarity). */
  def mkUniLit(left: Term, right: Term): Literal = mkLit(EQ(left, right), false)
}

