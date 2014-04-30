package datastructures.internal

/** This type can be mixed-in to supply standard higher-order logic symbol definitions, including
 *
 *  1. Fixed (interpreted) symbols
 *  2. Defined symbols
 *  3. Standard base types
 */
trait HOLSignature {
  val types = List(("$o", KindType),
                   ("$i", KindType))

  val fixedConsts = List(("$true", BaseType("$o")),
                         ("$false", BaseType("$o"))) // and more
}
