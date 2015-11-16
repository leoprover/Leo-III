import leo.datastructures._

package object leo {

  type TermOrdering = leo.datastructures.impl.orderings.TO_CPO_Naive.type
  type ClauseOrdering = Ordering[Clause]
  type LiteralOrdering = Ordering[Literal]

  type ClauseWeight = Weight[Clause]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
