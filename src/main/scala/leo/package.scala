import leo.datastructures.term.Term
import leo.datastructures.{QuasiOrdering, Type, Clause, Literal, Weight}

package object leo {
  type TypeOrdering = QuasiOrdering[Type]
  type TermOrdering = QuasiOrdering[Term]
  type ClauseOrdering = Ordering[Clause]
  type LiteralOrdering = Ordering[Literal]

  type ClauseWeight = Weight[Clause]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
