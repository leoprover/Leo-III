package leo.modules.input

abstract class ProblemStatistics {
  private[this] var includes0: Int = 0
  private[this] var immediateFormulas0: Int = 0
  private[this] var includedFormulas0: Int = 0
  private[this] var localAxioms0: Int = 0
  private[this] var includedAxioms0: Int = 0

  final def includes: Int = includes0
  final def registerIncludes(n: Int): Unit = includes0 += n

  final def formulas: Int = immediateFormulas + includedFormulas

  final def immediateFormulas: Int = immediateFormulas0
  final def registerImmediateFormulas(n: Int): Unit = immediateFormulas0 += n

  final def includedFormulas: Int = includedFormulas0
  final def registerIncludedFormulas(n: Int): Unit = includedFormulas0 += n

  final def axioms: Int = localAxioms + includedAxioms

  final def localAxioms: Int = localAxioms0
  final def registerLocalAxioms(n: Int): Unit = localAxioms0 += n

  final def includedAxioms: Int = includedAxioms0
  final def registerIncludedAxioms(n: Int): Unit = includedAxioms0 += n
}

object ProblemStatistics {
  final def apply(): ProblemStatistics = new ProblemStatistics {}
}