package leo.modules.calculus

import leo.LeoTestSuite
import leo.datastructures.{Clause, Term}
import Term.{mkAtom, mkTermApp}
import leo.modules.parsers.Input
import leo.modules.HOLSignature.{i, Choice => ε}
import leo.modules.termToClause
//import leo.modules.calculus.{FullCNF, freshVarGenFromBlank, LiftEq}
/**
  * Created by lex on 24.04.17.
  */
class FuncSpecInstanceTest extends LeoTestSuite {
  test("FuncSpec Test 1") {
    implicit val sig = getFreshSignature

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))

    val f = Input("! [F:$i>$i]: ~((F @ a) = b)")
    assert(Term.wellTyped(f))
    println(f.pretty(sig))
    val p = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(p.forall(Clause.wellTyped))
    println(p.map(_.pretty(sig)).mkString("\n"))
    val p0 = p.map{cl =>
      val (a,b,c,d) = LiftEq.canApply(cl)
      if (a) {
        Clause(LiftEq.apply(b,c,d))
      } else cl
    }
    assert(p0.forall(Clause.wellTyped))
    println(p0.map(_.pretty(sig)).mkString("\n"))
    val cl = p0.head
    val lit = cl.lits.head
    val p1 = SolveFuncSpec.apply(i ->: i, Seq((Seq(a), b)))
    println("spec "+p1.pretty(sig))
    assert(Term.wellTyped(p1))
    val p2 = mkTermApp(p1, a).betaNormalize
    println("appled with a "+p2.pretty(sig))
    assert(Term.wellTyped(p2))
    val pred = ε.unapply(p2)
    assert(pred.isDefined)
    val pred0 = pred.get
    println(pred0.pretty(sig))
    assert(Term.wellTyped(pred0))
    val applied = mkTermApp(pred0, p2).betaNormalize
    println(applied.pretty(sig))
    assert(Term.wellTyped(applied))
    val simp = Simp.normalize(applied)
    println(simp.pretty(sig))
    assert(Term.wellTyped(simp))
    
  }
}
