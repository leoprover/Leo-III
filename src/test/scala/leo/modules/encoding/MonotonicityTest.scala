package leo.modules.encoding

import leo.{Checked, Ignored, LeoTestSuite}
import leo.datastructures.{Signature, Term, Type}
import leo.modules.parsers.Input

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
    import leo.modules.termToClause

    // New types
    val monkey = Type.mkType(sig.addBaseType("monkey"))
    val banana = Type.mkType(sig.addBaseType("banana"))

    // Introduced symbols to signature
    sig.addUninterpreted("owns", Type.mkFunType(Seq(monkey, banana, o)))
    sig.addUninterpreted("b1", Type.mkFunType(monkey, banana))
    sig.addUninterpreted("b2", Type.mkFunType(monkey, banana))

    // create formulae
    val f1 = Input.readFormula("! [M: monkey]: ((owns @ M @ (b1 @ M)) & (owns @ M @ (b2 @ M)))")
    val f2 = Input.readFormula("! [M: monkey]: ((b1 @ M) != (b2 @ M))")
    val f3 = Input.readFormula("! [M1: monkey, M2: monkey, B: banana]:" +
      " (((owns @ M1 @ B) & (owns @ M2 @ B)) => (M1 = M2))")

    assert(Term.wellTyped(f1))
    assert(Term.wellTyped(f2))
    assert(Term.wellTyped(f3))

    // calculate CNF
    val cnf = CNF(vargen, termToClause(f1)).toSet union CNF(vargen, termToClause(f2)).toSet union CNF(vargen, termToClause(f3)).toSet

    // infinite types
    val noInfTypes: Set[Type] = Set.empty // none known

    // test monotonicity check
    assert(BBPS.monotone(banana, cnf, noInfTypes))
    assert(!BBPS.monotone(monkey, cnf, noInfTypes))
  }

  test("Algebraic Lists (Ex. 12)", Checked) {
    implicit val sig: Signature = getFreshSignature
    import leo.datastructures.Kind.*
    import leo.datastructures.Type.∀
    import leo.modules.HOLSignature.o
    import leo.modules.calculus.{FullCNF => CNF, freshVarGenFromBlank => vargen}
    import leo.modules.termToClause

    // New types
    val list = sig.addTypeConstructor("list", * ->: *)

    // Introduced symbols to signature
    val X: Type = Type.mkVarType(1)
    sig.addUninterpreted("nil", ∀(Type.mkType(list, X)))
    sig.addUninterpreted("cons", ∀(X ->: Type.mkType(list, X) ->: Type.mkType(list, X)))
    sig.addUninterpreted("hd", ∀(Type.mkType(list, X) ->: X))
    sig.addUninterpreted("tl", ∀(Type.mkType(list, X) ->: Type.mkType(list, X)))

    // create formulae
    val f1 = Input.readFormula("! [A: $tType, X: A, Xs: (list @ A)]: ((nil @ A) != (cons @ A @ X @ Xs))")
    val f2 = Input.readFormula("! [A: $tType, Xs: (list @ A)]: ((Xs = (nil @ A)) | (? [Y:A, Ys: (list @ A)]: (" +
      "Xs = (cons @ A @ Y @ Ys))))")
    val f3 = Input.readFormula("! [A: $tType, X: A, Xs: (list @ A)]: (((hd @ A @ (cons @ A @ X @ Xs)) = X) & " +
      "((tl @ A @ (cons @ A @ X @ Xs)) = (Xs)))")

    assert(Term.wellTyped(f1))
    assert(Term.wellTyped(f2))
    assert(Term.wellTyped(f3))

    // calculate CNF
    val cnf = CNF(vargen, termToClause(f1)).toSet union CNF(vargen, termToClause(f2)).toSet union CNF(vargen, termToClause(f3)).toSet

    println(cnf.map(_.pretty(sig)).mkString("\n"))
    // infinite types
    val noInfTypes: Set[Type] = Set.empty // none known

    // test monotonicity check
    assert(!BBPS.monotone(Type.mkType(list, X), cnf, noInfTypes))
    assert(!BBPS.monotone(X, cnf, noInfTypes))
    assert(BBPS.monotone(Type.mkType(list, X), cnf, Set(Type.mkType(list, X))))
    assert(!BBPS.monotone(X, cnf, Set(Type.mkType(list, X))))
  }
}
