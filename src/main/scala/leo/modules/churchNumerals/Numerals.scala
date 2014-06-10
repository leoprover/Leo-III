package leo.modules.churchNumerals

import scala.language.implicitConversions
import leo.datastructures.internal.Term
import leo.datastructures.internal.Term.{mkTermApp => ap, mkTypeApp => tyAp, Λ, λ,intToBoundVar,intsToBoundVar}
import leo.datastructures.internal.Type.{typeVarToType,∀}

/**
 * Implementation of polymorphic-typed church numerals.
 *
 * @author Alexander Steen
 * @since 10.06.2014
 */
object Numerals {
  type Church = Term

  def zero: Church = Λ(λ(1 ->: 1,1)(1,1))

  def succ: Church = λ(∀((1 ->: 1) ->: 1 ->: 1))(
                          Λ(
                            λ(1 ->:1, 1)(
                              ap(
                                ap(tyAp((3,∀((1 ->: 1) ->: 1 ->: 1)),1),
                                   (2, 1 ->: 1)
                                ),
                                ap((2, 1 ->: 1),
                                   (1, 1))
                                )
                              )
                            )
                          )
  def succ(n: Church): Church = ap(succ, n).betaNormalize

  def main(args: Array[String]) {
    println(succ.ty.pretty)
    println(succ.pretty)

    println(zero.pretty)
    println(ap(succ,zero).pretty)
    println(ap(succ,zero).betaNormalize.pretty)
  }
}
