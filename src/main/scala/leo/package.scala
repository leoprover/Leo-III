import leo.datastructures.term.Term
import leo.datastructures.{Clause, Literal, Weight}

package object leo {
  type TermOrdering = Ordering[Term]
  type ClauseOrdering = Ordering[Clause]
  type LiteralOrdering = Ordering[Literal]

  type ClauseWeight = Weight[Clause]
  type LiteralWeight = Weight[Literal]
}
