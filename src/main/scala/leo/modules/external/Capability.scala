package leo.modules.external

import Capability.{Language, Features}

case class Capability(capabilities: Map[Language, Features]) {
  final def supports(language: Language): Boolean = capabilities.contains(language)

  final def supports(feature: Capability.Feature)(in_Language: Language): Boolean =
    Capability.supports(capabilities(in_Language), feature)

  final def apply(language: Language): Features = capabilities(language)
}

object Capability {
  type Language = Int
  final val THF: Language = 1
  final val TFF: Language = 2
  final val FOF: Language = 4
  final val CNF: Language = 8

  type Features = Int
  type Feature = Features

  final val Polymorphism: Feature = 1
  final val Choice: Feature = 2
  final val Description: Feature = 4
  // ....

  @inline final def supports(features: Features, feature: Feature): Boolean = {
    leo.datastructures.isPropSet(feature, features)
  }

  final def apply(capabilities: Map[Language, Seq[Feature]]): Capability = {
    Capability(capabilities.mapValues(_.foldLeft(0){case (r,f) => r | f}))
  }
}
