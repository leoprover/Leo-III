package leo.datastructures

import leo.Configuration
import leo.datastructures.term.Term

/**
 * Interface for literals, companion object `Literal` provides constructor methods.
 *
 * @author Alexander Steen
 * @since 07.11.2014
 */
trait Literal extends Pretty with Ordered[Literal] with HasCongruence[Literal] {
  /** The unique, increasing literal number. */
  def id: Int
  /** Returns the literal's underlying term. */
  def term: Term
  /** The polarity of the literal, where positive polarity is encoded by the Boolean value `true`,
    * negative polarity by Boolean value `false`. */
  def polarity: Boolean
  /** The weight of the literal. */
  def weight: Int = Configuration.LITERAL_WEIGHTING.weightOf(this)

  /** Returns true iff the literal is a flex-flex unification literal. */
  def isFlexFlex: Boolean
  /** Returns true iff the literal is an unification constraint. */
  def isUni: Boolean

  def compare(that: Literal): Int = Configuration.LITERAL_ORDERING.compare(this, that)
  def cong(that: Literal): Boolean = (this.polarity == that.polarity) && (this.term == that.term)

  def replace(what : Term, by : Term) : Literal = Literal.mkLit(term.replace(what,by), polarity)

  def substitute(s : Subst) : Literal = termMap {_.closure(s)}

  lazy val flipPolarity: Literal =  if (polarity)
                                      Literal.mkNegLit(term)
                                    else
                                      Literal.mkPosLit(term)

  lazy val toTerm: Term = if (polarity)
                            ===(term, LitTrue())
                          else
                            ===(term, LitFalse())

  def fold[A](f: (Term, Boolean) => A): A = f(term, polarity)
  def termMap[A](f: Term => Term): Literal = Literal.mkLit(f(term), polarity)
}

object Literal extends Function2[Term, Boolean, Literal]{
  import leo.datastructures.impl.{SimpleLiteral => LitImpl}
  import leo.datastructures.{=== => EQ}

  /** Create a literal of the term `t` and polarity `pol`. */
  def mkLit(t: Term, pol: Boolean): Literal = LitImpl.mkLit(t, pol)

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

