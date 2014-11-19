package leo.datastructures.internal

import leo.datastructures.internal.{=== => EQ}
import leo.datastructures.internal.terms.Term

/**
 * Implementation of the `Literal` type.
 *
 * @author Alexander Steen
 * @since 19.11.2014
 */
abstract class SimpleLiteral extends Literal {
  /** Weight of the `simpleLiteral` is for now fixed by its id number.
    * THIS WILL CHANGE IN THE FUTURE! */
  def weight = litId // TODO: Preliminary implementation
  protected def litId: Int
}


protected object SimpleLiteral {
  private var litCounter : Int = 0

  /** Create a literal of the term `t` and polarity `pol`. */
  def mkLit(t: Term, pol: Boolean): Literal = {
    litCounter += 1

    if (pol)
      PositiveLiteral(t, litCounter)
    else
      NegativeLiteral(t, litCounter)
  }


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


  private case class PositiveLiteral(term: Term, litId: Int) extends SimpleLiteral {
    val polarity = true
    val isUni = false
    val isFlexFlex = false
    lazy val pretty = s"[${term.pretty}] = T"
  }
  private case class NegativeLiteral(term: Term, litId: Int) extends SimpleLiteral {
    val polarity = false
    lazy val isUni = term match {
      case _ === _ => true
      case _ => false
    }
    lazy val isFlexFlex = ???
    lazy val pretty = s"[${term.pretty}] = F"
  }
}

