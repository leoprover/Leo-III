import leo.datastructures._

package object leo {
  type CompareResult = Byte

  /** Unknown compare status. */
  final val CMP_UK: CompareResult = 0.toByte
  /** Not comparable. */
  final val CMP_NC: CompareResult = 1.toByte
  /** Equal by comparison */
  final val CMP_EQ: CompareResult = 2.toByte
  /** (strictly) less. */
  final val CMP_LT: CompareResult = 3.toByte
  /** (strictly) greater. */
  final val CMP_GT: CompareResult = 4.toByte

  type TypeOrdering = QuasiOrdering[Type]
  type TermOrdering = leo.datastructures.impl.orderings.TO_CPO_Naive.type // FIXME Hacky
  type ClauseOrdering = Ordering[Clause]
  type LiteralOrdering = Ordering[Literal]

  type ClauseWeight = Weight[Clause]
  type LiteralWeight = Weight[Literal]

  def Out = leo.modules.output.logger.Out
}
