package leo.modules.relevance

sealed abstract class AxiomFilterConfig
final case class SineConfig(tolerance: Double, maxAbsoluteSize: Int, maxProportion: Double, maxDepth: Int) extends AxiomFilterConfig
