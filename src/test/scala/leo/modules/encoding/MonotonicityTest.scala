package leo.modules.encoding

import leo.{Checked, LeoTestSuite}
import leo.datastructures.{Signature, Term, Type}
import leo.modules.Parsing

/**
  * Checks whether the [[leo.modules.encoding.Monotonicity]] inference rules work as expected.
  *
  * @see [[leo.modules.encoding.BBPS]] -- the calculus tested here.
  */
class MonotonicityTest extends LeoTestSuite {
  test("Monkey Village (Ex. 1)", Checked) {
    implicit val sig: Signature = getFreshSignature
    import leo.modules.HOLSignature.o
    import leo.modules.calculus.{FullCNF => CNF, freshVarGenFromBlank => vargen}
    import leo.modules.Utility.termToClause

    // New types
    val monkey = Type.mkType(sig.addBaseType("monkey"))
    val banana = Type.mkType(sig.addBaseType("banana"))

    // Introduced symbols to signature
    sig.addUninterpreted("owns", Type.mkFunType(Seq(monkey, banana, o)))
    sig.addUninterpreted("b1", Type.mkFunType(monkey, banana))
    sig.addUninterpreted("b2", Type.mkFunType(monkey, banana))

    // create formulae
    val f1 = Parsing.parseThf("! [M: monkey]: ((owns @ M @ (b1 @ M)) & (owns @ M @ (b2 @ M)))")
    val f2 = Parsing.parseThf("! [M: monkey]: ((b1 @ M) != (b2 @ M))")
    val f3 = Parsing.parseThf("! [M1: monkey, M2: monkey, B: banana]:" +
      " (((owns @ M1 @ B) & (owns @ M2 @ B)) => (M1 = M2))")

    assert(Term.wellTyped(f1))
    assert(Term.wellTyped(f2))
    assert(Term.wellTyped(f3))

    // calculate CNF
    val cnf = CNF(vargen, termToClause(f1)).toSet union CNF(vargen, termToClause(f2)).toSet union CNF(vargen, termToClause(f3)).toSet

    // infinite types
    val infTypes: Set[Type] = Set.empty // none known

    // test monotonicity check
    assert(BBPS.monotone(banana, cnf, infTypes))
    assert(!BBPS.monotone(monkey, cnf, infTypes))
    assert(!BBPS.monotone(cnf, infTypes))
  }
}
