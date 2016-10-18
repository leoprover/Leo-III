import leo.datastructures._

package object leo {

  type TermOrdering = leo.datastructures.impl.orderings.TO_CPO_Naive.type
  type ClauseProxyOrdering = Ordering[ClauseProxy]

  type ClauseWeight = Weight[Clause]
  type ClauseProxyWeight = Weight[ClauseProxy]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
