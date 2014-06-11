package leo

import leo.datastructures.internal.Term.{mkTermApp => ap,mkAtom}
import leo.datastructures.internal.{LitFalse, LitTrue, ===, Signature}

import leo.modules.churchNumerals.Numerals
import leo.modules.churchNumerals.Numerals.fromInt

import leo.modules.normalization.Simplification

/**
 * Created by lex on 11.06.14.
 */
object ExecutionTest {
  def main(args: Array[String]) {
    val sig = Signature.get
    Signature.withHOL(sig) // include standard hol symbols
    Numerals() // include numerals in signature

    val add = mkAtom(sig.meta("add").key) // fetch addition from signature

    val test = {
      ===(ap(ap(add,2),3), ap(ap(add,3),2))  // theorem: 2+3 = 3+2
    }

    println("To prove: 2+3 = 3+2")
    println()
    println("As term: " + test.pretty)
    println()
    println("Definition expansion ...")
    val test2 = test.expandAllDefinitions
    //println(test2.pretty)

    println("Beta normalizing ...")
    val test3 = test2.betaNormalize
    //println(test3.pretty)

    println("Simplification ...")
    val test4 = Simplification(test3)

    test4 match {
      case LitTrue() => print("THEOREM")
      case LitFalse() => print("COUNTER PROVABLE")
      case _ => print("UNKNOWN")
    }
    println(" (Resulting term: " + test4.pretty + " )")
  }
}
