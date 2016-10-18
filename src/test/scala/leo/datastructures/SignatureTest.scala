package leo
package datastructures

import leo.modules.Utility

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
    Utility.printSignature(sig)
    printHLine()
    Out.output("Signature creation successful.")
    printLongHLine()
  }


}
