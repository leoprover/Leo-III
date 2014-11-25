package leo.datastructures.impl

import leo.datastructures.term.Term
import leo.datastructures.{Literal, ===}

/**
 * Implementation of the `Literal` type.
 *
 * @author Alexander Steen
 * @since 19.11.2014
 */
abstract class SimpleLiteral extends Literal {}


object SimpleLiteral {
  private var litCounter : Int = 0

  /** Create a literal of the term `t` and polarity `pol`. */
  def mkLit(t: Term, pol: Boolean): Literal = {
    litCounter += 1

    if (pol)
      PositiveLiteral(t, litCounter)
    else
      NegativeLiteral(t, litCounter)
  }

  private case class PositiveLiteral(term: Term, id: Int) extends SimpleLiteral {
    val polarity = true
    val isUni = false
    val isFlexFlex = false
    lazy val pretty = s"[${term.pretty}] = T"
  }
  private case class NegativeLiteral(term: Term, id: Int) extends SimpleLiteral {
    val polarity = false
    lazy val isUni = term match {
      case (_ === _) => true
      case _ => false
    }
    lazy val isFlexFlex = term match {
      case (l === r) => (l.isApp || l.isAtom) && (r.isApp || r.isAtom) && l.headSymbol.symbols.isEmpty && r.headSymbol.symbols.isEmpty
      case _ => false
    }
    lazy val pretty = s"[${term.pretty}] = F"
  }
}

