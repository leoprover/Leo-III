package leo.modules.external


object Capabilities {
  type Info = Map[Language, Features]

  type Language = Int
  final val THF: Language = 1
  final val TFF: Language = 2
  final val FOF: Language = 4
  final val CNF: Language = 8

  type Feature = Int
  final val Polymorphism: Feature = 1
  final val Choice: Feature = 2
  final val Description: Feature = 4
  // ....
  type Features = Int // collection of features

  @inline final def supportsLanguage(info: Info, language: Language): Boolean = {
    info.contains(language)
  }

  @inline final def supportsFeature(info: Info, language: Language)(feature: Feature): Boolean = {
    if (info.contains(language)) leo.datastructures.isPropSet(feature, info(language))
    else false
  }

  final def apply(capability1: (Language, Seq[Feature]), capas: (Language, Seq[Feature])*): Info = {
    var map: Map[Language, Features] = Map.empty
    val allCaps: Seq[(Language, Seq[Feature])] = capability1 +: capas
    allCaps.foreach { case (l, fs) =>
      map = map + (l -> fs.foldLeft(0){case (l,r) => l | r})
    }
    map
  }
}

trait HasCapabilities {
  def capabilities: Capabilities.Info
}
