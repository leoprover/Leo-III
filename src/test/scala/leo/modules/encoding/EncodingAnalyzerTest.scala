package leo.modules.encoding

import leo.{Checked, LeoTestSuite}
import leo.datastructures.{Signature, Term, Type}
import leo.modules.HOLSignature.{i, o}
import leo.modules.parsers.Input

/**
  * Created by lex on 2/27/17.
  */
class EncodingAnalyzerTest extends LeoTestSuite {
  test("Analyzer Test 1", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)

    // create formulae
    val f1 = Input.readFormula("a & (p @ a)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyzeFormula(f1)
    printTable(result)
    assert(result.contains(a))
    assert(result.contains(p))
    assert(result(a)._1 == 0)
    assert(result(p)._1 == 1)
    assert(!result(p)._2)
    assert(result(a)._2)
  }

  test("Analyzer Test 2", Checked) {
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

    val result = EncodingAnalyzer.analyzeFormula(f1)
    printTable(result)
    assert(result.contains(a))
    assert(result.contains(b))
    assert(result.contains(p))
    assert(result(a)._1 == 0)
    assert(result(b)._1 == 0)
    assert(result(p)._1 == 1)
    assert(!result(p)._2)
    assert(!result(a)._2)
    assert(result(b)._2)
  }

  test("Analyzer Test 3", Checked) {
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

    val result = EncodingAnalyzer.analyzeFormula(f1)
    printTable(result)
    assert(result.contains(a))
    assert(result.contains(b))
    assert(result.contains(p))
    assert(result.contains(q))
    assert(result.contains(r))
    assert(result(a)._1 == 0)
    assert(result(b)._1 == 0)
    assert(result(p)._1 == 0)
    assert(result(q)._1 == 1)
    assert(result(r)._1 == 1)
    assert(result(a)._2)
    assert(result(b)._2)
    assert(result(p)._2)
    assert(!result(q)._2)
    assert(!result(r)._2)
  }

  test("Analyzer Test 4", Checked) {
    implicit val sig: Signature = getFreshSignature

    // Introduced symbols to signature
    val p = sig.addUninterpreted("p", o ->: o)
    val q = sig.addUninterpreted("q", o ->: o)
    val r = sig.addUninterpreted("r", (o ->: o) ->: o)
    val a = sig.addUninterpreted("a", o)
    val b = sig.addUninterpreted("b", o)

    // create formulae
    val f1 = Input.readFormula("a = (p @ a)")

    assert(Term.wellTyped(f1))

    val result = EncodingAnalyzer.analyzeFormula(f1)
    printTable(result)
    assert(result.contains(a))
    assert(result.contains(p))
    assert(result(a)._1 == 0)
    assert(result(p)._1 == 1)
    assert(result(a)._2)
    assert(!result(p)._2)
  }

  test("Analyzer Test 5", Checked) {
    implicit val sig: Signature = getFreshSignature

    // create formulae
    val (_,f1,_) = Input.readAnnotated("thf(sur_cantor, conjecture, (~ ( ? [F: $i > ($i > $o)] : (\n                                   ! [Y: $i > $o] :\n                                    ? [X: $i] : (\n                                      (F @ X) = Y\n                                    )\n                                 ) ))).")
    import leo.modules.Utility.termToClause
    import leo.datastructures.Clause
    assert(Term.wellTyped(f1))
    println(f1.pretty(sig))
    val cnf = leo.modules.calculus.FullCNF.apply(leo.modules.calculus.freshVarGenFromBlank, termToClause(f1))(sig).toSet
    assert(cnf.forall(Clause.wellTyped))
    cnf.foreach{cl => println(cl.pretty(sig))}
    val result = EncodingAnalyzer.analyze(cnf)
    printTable(result)

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
