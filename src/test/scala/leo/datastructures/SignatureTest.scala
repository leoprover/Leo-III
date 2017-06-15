package leo
package datastructures

import leo.modules._

/**
 * Just a little test with signature and types.
 * Succeeds if the creation of the global signature was successful.
 *
 * Created by lex on 05.05.14.
 * @note Updated 04.03.15 - Refactor to a proper test in the course of test clean-ups
 */
class SignatureTest extends LeoTestSuite {

  test("HOL signature creation", Checked) {
    val sig = getFreshSignature
    printHeading("HOL Signature creation")
    printSignature(sig)
    printHLine()
    Out.output("Signature creation successful.")
    printLongHLine()
  }

  test("Signature copy test 1", Checked) {
    val sig = getFreshSignature
    val sig2 = sig.copy
    assert(sig2 != null)
  }

  test("Signature copy test 2", Checked) {
    val sig = getFreshSignature
    sig.addUninterpreted("a", HOLSignature.i)
    val sig2 = sig.copy
    assert(sig.exists("a"))
    assert(sig2.exists("a"))
    sig.addUninterpreted("b", HOLSignature.i)
    assert(sig.exists("b"))
    assert(!sig2.exists("b"))
    sig2.addUninterpreted("c", HOLSignature.i)
    assert(!sig.exists("c"))
    assert(sig2.exists("c"))
  }


}
