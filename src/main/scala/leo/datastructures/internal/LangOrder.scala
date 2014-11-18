package leo.datastructures.internal

import leo.datastructures.Pretty

/**
 * Marker type for the 'language level' of terms.
 * A Term is flagged `PROPOSITIONAL` iff it is a propositional formula,
 * analogously for `FIRSTORDER` and `HIGHERORDER`.
 *
 * @author Alexander Steen
 */
sealed abstract class LangOrder extends Ordered[LangOrder]

case object PROPOSITIONAL extends LangOrder {
  def compare(that: LangOrder) = that match {
    case PROPOSITIONAL => 0
    case _ => -1
  }
}

case object FIRSTORDER extends LangOrder {
  def compare(that: LangOrder) = that match {
    case PROPOSITIONAL => 1
    case FIRSTORDER => 0
    case HIGHERORDER => -1
  }
}

case object HIGHERORDER extends LangOrder {
  def compare(that: LangOrder) = that match {
    case HIGHERORDER => 0
    case _ => 1
  }
}