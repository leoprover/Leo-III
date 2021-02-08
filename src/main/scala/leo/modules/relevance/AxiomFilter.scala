package leo.modules.relevance

import leo.datastructures.TPTP.AnnotatedFormula

sealed abstract class AxiomFilterConfig
case object NoAxiomFilter extends AxiomFilterConfig
final case class ThresholdPassFilterConfig(threshold: Int, postfilter: AxiomFilterConfig) extends AxiomFilterConfig
final case class SineConfig(tolerance: Double, maxAbsoluteSize: Int, maxProportion: Double, maxDepth: Int) extends AxiomFilterConfig
final case class MePoConfig(passmark: Double, aging: Double) extends AxiomFilterConfig

sealed trait AxiomFilter[Config <: AxiomFilterConfig] {
  def apply(axioms: Seq[AnnotatedFormula],
            definitions: Seq[(String, AnnotatedFormula)],
            conjectures: Seq[AnnotatedFormula])
           (symbolDistribution: SymbolDistribution, config: Config): (Seq[AnnotatedFormula], Seq[AnnotatedFormula])
}

object DummyFilter extends AxiomFilter[NoAxiomFilter.type] {
  override final def apply(axioms: Seq[AnnotatedFormula],
                           definitions: Seq[(String, AnnotatedFormula)],
                           conjectures: Seq[AnnotatedFormula])
                          (symbolDistribution: SymbolDistribution,
                           config: NoAxiomFilter.type): (Seq[AnnotatedFormula], Seq[AnnotatedFormula]) = {
    (axioms, Seq.empty)
  }
}

object ThresholdPassFilter extends AxiomFilter[ThresholdPassFilterConfig] {
  override final def apply(axioms: Seq[AnnotatedFormula],
                           definitions: Seq[(String, AnnotatedFormula)],
                           conjectures: Seq[AnnotatedFormula])
                          (symbolDistribution: SymbolDistribution,
                           config: ThresholdPassFilterConfig): (Seq[AnnotatedFormula], Seq[AnnotatedFormula]) = {
    if (axioms.size <= config.threshold) (axioms, Seq.empty)
    else {
      config.postfilter match {
        case NoAxiomFilter => DummyFilter.apply(axioms, definitions, conjectures)(symbolDistribution, NoAxiomFilter)
        case config@ThresholdPassFilterConfig(_, _) => ThresholdPassFilter.apply(axioms, definitions, conjectures)(symbolDistribution, config)
        case config@SineConfig(_, _, _, _) => SineFilter.apply(axioms, definitions, conjectures)(symbolDistribution, config)
        case config@MePoConfig(_, _) => MePoFilter.apply(axioms, definitions, conjectures)(symbolDistribution, config)
      }
    }
  }
}

object SineFilter extends AxiomFilter[SineConfig] {
  override final def apply(axioms: Seq[AnnotatedFormula],
                           definitions: Seq[(String, AnnotatedFormula)],
                           conjectures: Seq[AnnotatedFormula])
                          (symbolDistribution: SymbolDistribution,
                           config: SineConfig): (Seq[AnnotatedFormula], Seq[AnnotatedFormula]) = {
    val selector = SineSelector.apply(config.tolerance, symbolDistribution, axioms, definitions)
    val selectedAxioms = selector.apply(conjectures, config.maxDepth, config.maxProportion, config.maxAbsoluteSize)
    val discardedAxioms = axioms.diff(selectedAxioms)
    (selectedAxioms, discardedAxioms)
  }
}

object MePoFilter extends AxiomFilter[MePoConfig] {
  override final def apply(axioms: Seq[AnnotatedFormula],
                           definitions: Seq[(String, AnnotatedFormula)],
                           conjectures: Seq[AnnotatedFormula])
                          (symbolDistribution: SymbolDistribution,
                           config: MePoConfig): (Seq[AnnotatedFormula], Seq[AnnotatedFormula]) = {
    ???
  }
}

