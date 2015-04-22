import leo.datastructures._

package object leo {
  type TypeOrdering = QuasiOrdering[Type]
  type TermOrdering = QuasiOrdering[Term]
  type ClauseOrdering = Ordering[Clause]
  type LiteralOrdering = Ordering[Literal]

  type ClauseWeight = Weight[Clause]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
