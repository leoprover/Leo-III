package leo.datastructures.impl

import leo.datastructures.{Term, Literal, ===}

/**
 * Implementation of the `Literal` type.
 *
 * @author Alexander Steen
 * @since 19.11.2014
 */
protected[impl] abstract class SimpleLiteral extends Literal {
  lazy val flexHead: Boolean = !term.isTermAbs && !term.isTypeAbs && term.headSymbol.isVariable

  lazy val eqComponents = term match {
    case (l === r) => Some((l,r))
    case _ => None
  }
}


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
    lazy val isEq = eqComponents.isDefined
    val isUni = false
    val isFlexFlex = false
    lazy val pretty = s"[${term.pretty}] = T"
  }
  private case class NegativeLiteral(term: Term, id: Int) extends SimpleLiteral {
    val polarity = false
    val isEq = false
    lazy val isUni = eqComponents.isDefined
    lazy val isFlexFlex = term match {
      case (l === r) => (l.isApp || l.isAtom) && (r.isApp || r.isAtom) && l.headSymbol.symbols.isEmpty && r.headSymbol.symbols.isEmpty
      case _ => false
    }
    lazy val pretty = s"[${term.pretty}] = F"
  }
}

