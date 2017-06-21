import leo.datastructures._

package object leo {

  type TermOrdering = leo.datastructures.TermOrdering
  type ClauseProxyOrdering = Ordering[ClauseProxy]

  type ClauseWeight = Weight[Clause]
  type ClauseProxyWeight = Weight[ClauseProxy]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
