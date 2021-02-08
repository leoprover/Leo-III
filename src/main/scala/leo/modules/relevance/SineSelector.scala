package leo.modules.relevance

import leo.datastructures.TPTP.AnnotatedFormula

import scala.collection.mutable

/**
  * Sine-like axiom selector, based on Hoder et al. in "Sine Qua Non for Large Theory Reasoning",
  * LNCS 6803, CADE-23, 2011.
  *
  * Instantiation ually via companion object's apply method.
  *
  * TODO: Also use type information for relevance filtering. This is important e.g
  * in set-theoretic reasoning. Consider the following example:
  * (1) Conjecture: There exists a bijection from $i to $o
  * (2) Axioms: $i is of cardinality two.
  *
  * Problem: Conjecture does not contain any symbols, and, the axioms do not
  * contain any symbols in common with the conjecture.
  *
  * @author Alexander Steen <alx.steen@gmail.com>
  */
final class SineSelector(triggerRelation: SineSelector.TriggerRelation,
                         specialAxioms: Seq[AnnotatedFormula],
                         axioms: Seq[AnnotatedFormula],
                         definitions: Seq[(String, AnnotatedFormula)]) {

  def apply(conjectures: Seq[AnnotatedFormula],
            maxDepth: Int,
            maxProportion: Double,
            maxAbsoluteSize: Int): Seq[AnnotatedFormula] = {
    val triggeredSymbols: mutable.Set[String] = mutable.Set.empty
    val triggeredAxioms: mutable.Set[AnnotatedFormula] = mutable.Set.empty // The set of all transitively triggered axiom
    val zeroStepTriggeredSymbols = conjectures.flatMap(_.symbols).distinct // is distinct necessary?
    val definitionSymbolsMap = Map.from(definitions)

    val maxSize: Int = Math.min(maxAbsoluteSize, (maxProportion * axioms.size).toInt)

    if (zeroStepTriggeredSymbols.nonEmpty) {
      val maxDepth0: Float = if (maxDepth < 0) Float.PositiveInfinity else maxDepth.toFloat

      var depthRemaining: Float = maxDepth0
      var lastSelectedAxiomsSize: Int = -1
      var kTriggeredSymbols = zeroStepTriggeredSymbols.toVector // initial setting

      while (depthRemaining > 0 && (triggeredAxioms.size > lastSelectedAxiomsSize)) {
        lastSelectedAxiomsSize = triggeredAxioms.size
        val kTriggeredAxioms = kTriggeredSymbols.flatMap { symbol =>
          if (triggeredSymbols(symbol)) Seq.empty
          else {
            triggeredSymbols.addOne(symbol)
            triggerRelation(symbol)
          }
        }
        if ((triggeredAxioms.size + kTriggeredAxioms.size) > maxSize) {
          triggeredAxioms.addAll(kTriggeredAxioms.take(maxSize - triggeredAxioms.size))
          depthRemaining = 0
        } else {
          triggeredAxioms.addAll(kTriggeredAxioms)
          // We do not need to remove sym from kTriggeredDefSymbols, as it was already triggered)
          val kTriggeredDefSymbols = kTriggeredSymbols.flatMap(sym => definitionSymbolsMap.get(sym).fold(Set.empty[String])(_.symbols))
          kTriggeredSymbols = kTriggeredAxioms.flatMap(_.symbols).concat(kTriggeredDefSymbols)
          depthRemaining = depthRemaining - 1
        }
      }
    }
    val result = triggeredAxioms.addAll(specialAxioms)
    result.toSeq
  }

  override def toString: String = {
    val sb: StringBuilder = new StringBuilder
    sb.append("SiNE fact selector instance.\n")
    sb.append("triggerRelation={\n")
    for ((symbol,formulas) <- triggerRelation) {
      sb.append(s"\ttrigger($symbol,${formulas.map(_.name)}), \n")
    }
    sb.append("}")
    sb.toString()
  }
}

object SineSelector {
  type TriggerRelation = Map[String, Seq[AnnotatedFormula]]

  final def apply(tolerance: Double,
                  distribution: SymbolDistribution,
                  axioms: Seq[AnnotatedFormula],
                  definitions: Seq[(String, AnnotatedFormula)]): SineSelector = {

    // triggerRelation: symbol -> set(axioms triggered by symbol)
    val triggerRelation: mutable.Map[String, Seq[AnnotatedFormula]] = mutable.Map.empty
    // axioms with no symbols in it. take them.
    val specialAxioms: mutable.ListBuffer[AnnotatedFormula] = mutable.ListBuffer.empty
    ////////////////////////////////////////////////////////////////////

    // Compute trigger relation
    // for each symbol s in A: trigger (s, A) iff for all symbols s' occurring in A we have occ(s) ≤ t · occ(s').
    // collect axioms without any symbols in a set 'specialAxioms'
    axioms.foreach { axiom =>
      val symbols = axiom.symbols
      if (symbols.nonEmpty) {
        var minFreq = Int.MaxValue // minimal frequency of some symbol in axiom
        var symbFrequencies: Seq[(String, Int)] = Seq.empty // list of symbols with their frequency
        symbols.foreach { symbol =>
          val freq = distribution(symbol)
          if (freq < minFreq) minFreq = freq
          symbFrequencies = (symbol, freq) +: symbFrequencies
        }
        val localThreshold = minFreq * tolerance
        symbFrequencies.foreach { sf =>
          if (sf._2 <= localThreshold) addTrigger(sf._1, axiom, triggerRelation)
        }
      } else {
        specialAxioms += axiom
      }
    }
    // That's it!
    new SineSelector(triggerRelation.toMap.withDefaultValue(Vector.empty),specialAxioms.toSeq, axioms, definitions)
  }

  @inline
  private[this] final def addTrigger(symbol: String, formula: AnnotatedFormula, store: mutable.Map[String, Seq[AnnotatedFormula]]): Unit = {
    val maybeValue = store.get(symbol)
    if (maybeValue.isDefined) store += (symbol -> (formula +: maybeValue.get))
    else store += (symbol -> Vector(formula))
  }
}
