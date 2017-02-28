package leo.modules.encoding

import leo.{Checked, LeoTestSuite}
import leo.datastructures.{Signature, Term, Type}
import leo.modules.HOLSignature.{i, o}
import leo.modules.Utility
import leo.modules.parsers.Input

/**
  * Created by lex on 2/27/17.
  */
class TypedFOLEncodingTest extends LeoTestSuite {
  test("Type encoder Test 0", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & b")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType == TypedFOLEncodingSignature.o)
    val bType = TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig)
    println(bType.pretty(foSig))
    assert(bType == TypedFOLEncodingSignature.o)
  }

  test("Problem encoder Test 0", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & b")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    foSig.addUninterpreted("b", TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig))
    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))
  }

  test("Type encoder Test 1", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ a)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType == foSig.boolTy)
  }

  test("Problem encoder Test 1", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ a)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))
  }

  test("Type encoder Test 2", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ b)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType ==  TypedFOLEncodingSignature.o)
    val bType = TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig)
    println(bType.pretty(foSig))
    assert(bType ==   foSig.boolTy)
  }

  test("Problem encoder Test 2", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ b)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    foSig.addUninterpreted("b", TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig))
    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))
  }

  test("Type encoder Test 3", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ a) & (q @ b) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.funTy(foSig.boolTy,foSig.boolTy))
    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType == foSig.boolTy)
    val bType = TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig)
    println(bType.pretty(foSig))
    assert(bType == foSig.boolTy)
    val qType = TypedFOLEncoding.foTransformType(o ->: o, result(q))(sig, foSig)
    println(qType.pretty(foSig))
    assert(qType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val rType = TypedFOLEncoding.foTransformType((o ->: o) ->: o, result(r))(sig, foSig)
    println(rType.pretty(foSig))
    assert(rType == foSig.funTy(foSig.boolTy,foSig.boolTy) ->: TypedFOLEncodingSignature.o)
  }

  test("Problem encoder Test 3", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ a) & (q @ b) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("q", TypedFOLEncoding.foTransformType(o ->: o, result(q))(sig, foSig))
    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    foSig.addUninterpreted("b", TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig))
    foSig.addUninterpreted("r", TypedFOLEncoding.foTransformType((o ->: o) ->: o, result(r))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))
  }


  test("Type encoder Test 4", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ (a & b)) & (q @ (p @ (~(b)))) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.funTy(foSig.boolTy,foSig.boolTy))
    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType == foSig.boolTy)
    val bType = TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig)
    println(bType.pretty(foSig))
    assert(bType == foSig.boolTy)
    val qType = TypedFOLEncoding.foTransformType(o ->: o, result(q))(sig, foSig)
    println(qType.pretty(foSig))
    assert(qType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val rType = TypedFOLEncoding.foTransformType((o ->: o) ->: o, result(r))(sig, foSig)
    println(rType.pretty(foSig))
    assert(rType == foSig.funTy(foSig.boolTy,foSig.boolTy) ->: TypedFOLEncodingSignature.o)
  }

  test("Problem encoder Test 4", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ (a & b)) & (q @ (p @ (~(b)))) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("q", TypedFOLEncoding.foTransformType(o ->: o, result(q))(sig, foSig))
    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    foSig.addUninterpreted("b", TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig))
    foSig.addUninterpreted("r", TypedFOLEncoding.foTransformType((o ->: o) ->: o, result(r))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }

  test("Type encoder Test 5", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", i ->: o)

    // create formulae
    val f1 = Input.readFormula("! [X:$i]: (p @ X)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(i ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == TypedFOLEncodingSignature.i ->: TypedFOLEncodingSignature.o)
    Utility.printSignature(foSig)
  }

  test("Problem encoder Test 5", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", i ->: o)

    // create formulae
    val f1 = Input.readFormula("! [X:$i]: (p @ X)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(i ->: o, result(p))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }

  test("Type encoder Test 6", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", (i ->: i) ->: o)

    // create formulae
    val f1 = Input.readFormula("! [X:$i>$i]: (p @ X)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType((i ->: i) ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.funTy(TypedFOLEncodingSignature.i,TypedFOLEncodingSignature.i) ->: TypedFOLEncodingSignature.o)
    Utility.printSignature(foSig)
  }

  test("Problem encoder Test 6", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", (i ->: i) ->: o)

    // create formulae
    val f1 = Input.readFormula("! [X:$i>$i]: (p @ X)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType((i ->: i) ->: o, result(p))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }

  test("Type encoder Test 7", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", i ->: o)
    val r = sig.addUninterpreted("r", (i ->: o) ->: o)

    // create formulae
    val f1 = Input.readFormula("(! [X:$i]: (p @ X)) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(i ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.funTy(TypedFOLEncodingSignature.i,foSig.boolTy))
    val rType = TypedFOLEncoding.foTransformType((i ->: o) ->: o, result(r))(sig, foSig)
    println(rType.pretty(foSig))
    assert(rType == foSig.funTy(TypedFOLEncodingSignature.i,foSig.boolTy) ->: TypedFOLEncodingSignature.o)
    Utility.printSignature(foSig)
  }

  test("Problem encoder Test 7", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", i ->: o)
    val r = sig.addUninterpreted("r", (i ->: o) ->: o)

    // create formulae
    val f1 = Input.readFormula("(! [X:$i]: (p @ X)) & (r @ p)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(i ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("r", TypedFOLEncoding.foTransformType((i ->: o) ->: o, result(r))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }

  test("Type encoder Test 8", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("p @ (a & b)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val aType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(aType.pretty(foSig))
    assert(aType == foSig.boolTy)
    val bType = TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig)
    println(bType.pretty(foSig))
    assert(bType == foSig.boolTy)

    Utility.printSignature(foSig)
  }

  test("Problem encoder Test 8", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("p @ (a & b)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("a", TypedFOLEncoding.foTransformType(o, result(a))(sig, foSig))
    foSig.addUninterpreted("b", TypedFOLEncoding.foTransformType(o, result(b))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }

  test("Type encoder Test 9", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", i ->: o)

    // create formulae
    val f1 = Input.readFormula("p @ (! [X:$i]: (q @ X))")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)

    printTable(result)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    val pType = TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig)
    println(pType.pretty(foSig))
    assert(pType == foSig.boolTy ->: TypedFOLEncodingSignature.o)
    val qType = TypedFOLEncoding.foTransformType(i ->: o, result(q))(sig, foSig)
    println(qType.pretty(foSig))
    assert(qType == TypedFOLEncodingSignature.i ->: foSig.boolTy)

    Utility.printSignature(foSig)
  }

  test("Problem encoder Test 9", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", i ->: o)

    // create formulae
    val f1 = Input.readFormula("p @ (! [X:$i]: (q @ X))")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyze(f1)
    // new signature for encoded problem
    val foSig = TypedFOLEncodingSignature()

    foSig.addUninterpreted("p", TypedFOLEncoding.foTransformType(o ->: o, result(p))(sig, foSig))
    foSig.addUninterpreted("q", TypedFOLEncoding.foTransformType(i ->: o, result(q))(sig, foSig))

    val translateResult = TypedFOLEncoding.translate(f1, null)(sig, foSig)
    println(translateResult.pretty(foSig))
    Utility.printSignature(foSig)
    assert(Term.wellTyped(translateResult))

    println(s"Additional axioms: ${foSig.usedAuxSymbols.toString()}")
    for (a <- foSig.usedAuxSymbols) {
      val axiom = foSig.proxyAxiom(a)
      if (axiom.isDefined) {
        println(axiom.get.pretty(foSig))
        assert(Term.wellTyped(axiom.get))
      }
    }
  }


  private final def printTable(table: EncodingAnalyzer.ArityTable)(implicit sig: Signature): Unit = {
    println(s"symbol\t|\tarity\t|\tsubterm")
    println(s"-------------------------")
    for (entry <- table) {
      val (id, (arity, subterm)) = entry
      println(s"${sig(id).name}\t|\t$arity\t|\t$subterm")
    }
  }
}
