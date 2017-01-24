package leo.modules

import leo.datastructures.Term.{intToBoundVar, intsToBoundVar, Λ, λ, mkTermApp => ap, mkTypeApp => tyAp}
import leo.datastructures.Type.{typeVarToType, ∀}
import leo.datastructures.{Term, Signature, Type}

import scala.language.implicitConversions

/**
 * Implementation of polymorphic-typed church numerals.
 *
 * @author Alexander Steen
 * @since 10.06.2014
 */
object Numerals {
  type Church = Term

  /** The zero in church numeral representation */
  val zero: Church = Λ(λ(1 ->: 1,1)((1,1)))

  /** The successor function for church numerals */
  val succ: Term = λ(∀((1 ->: 1) ->: 1 ->: 1))(
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

  /** applies (and normalizes) the successor function `succ` to the given church numeral */
  def succ(n: Church): Church = ap(succ, n).betaNormalize

  /** The add function for church numerals */
  val add: Term = λ(∀((1 ->: 1) ->: 1 ->: 1),∀((1 ->: 1) ->: 1 ->: 1))(
                    Λ(
                      λ(1 ->: 1, 1)(
                        ap(
                          ap(
                            tyAp((4,∀((1 ->: 1) ->: 1 ->: 1)),1),
                            (2,1 ->: 1)
                          ),
                          ap(
                            ap(
                              tyAp((3,∀((1 ->: 1) ->: 1 ->: 1)),1),
                              (2,1 ->: 1)
                            ),
                            (1,1)
                          )
                        )
                      )
                    )
                  )

  /** applies (and normalizes) the `add` function to the given arguments. */
  def add(n: Church, m: Church): Church = ap(ap(add, n), m).betaNormalize

  /** The multiplication function for church numerals */
  val mult: Term = λ(∀((1 ->: 1) ->: 1 ->: 1),∀((1 ->: 1) ->: 1 ->: 1))(
    Λ(
      λ(1 ->: 1)(
          ap(
            tyAp((3,∀((1 ->: 1) ->: 1 ->: 1)),1),
            ap(
              tyAp((2,∀((1 ->: 1) ->: 1 ->: 1)),1),
              (1,1 ->: 1)
            )
          )
      )
    )
  )

  /** Applies (and normalizes) the `mult` function to the given arguments */
  def mult(n: Church, m: Church): Church = ap(ap(mult,n),m).betaNormalize

  /** The multiplication function for church numerals */
  val power: Term = λ(∀((1 ->: 1) ->: 1 ->: 1),∀((1 ->: 1) ->: 1 ->: 1))(
    Λ(
        ap(
          tyAp((1,∀((1 ->: 1) ->: 1 ->: 1)),(1 ->: 1)),
          tyAp((2,∀((1 ->: 1) ->: 1 ->: 1)),1)
        )
    )
  )

  /** Applies (and normalizes) the `power` function to the given arguments */
  def power(n: Church, m: Church): Church = ap(ap(power,n),m).betaNormalize

  implicit def fromInt(n: Int): Church = {
    require(n >= 0, "Church numerals cannot be negative.")
    n match {
      case 0 => zero
      case m => succ(fromInt(n-1))
    }
  }

  def apply(sig: Signature): Unit = {
    sig.addDefined("zero", zero, zero.ty)
    sig.addDefined("succ", succ, succ.ty)
    sig.addDefined("add", add, add.ty)
    sig.addDefined("mult", mult, mult.ty)
    sig.addDefined("power", power, power.ty)
    sig.addDefined("ten", fromInt(10), zero.ty)
    sig.addDefined("hundred", fromInt(100), zero.ty)
  }
}






