package leo.modules.normalization

import leo.datastructures.Term._
import leo.datastructures._
import leo.{Checked, LeoTestSuite}
import org.scalatest.Matchers

class SimplificationTestSuite extends LeoTestSuite with Matchers {
  val s = getFreshSignature

  val p = mkAtom(s.addUninterpreted("p", s.o))
  val q = mkAtom(s.addUninterpreted("q", s.o))

  val toSimplify: Map[Term, Term] = Map[Term, Term](
    (Not(LitTrue()), LitFalse()),
    (Not(LitFalse()), LitTrue()),
    (&(p, p), p),
    (&(p, Not(p)), LitFalse()),
    (<=>(p, p), LitTrue()),
    (&(p, LitTrue()), p),
    (&(p, LitFalse()), LitFalse()),
    (Impl(p, LitTrue()), LitTrue()),
    (Impl(p, LitFalse()), Not(p)),
    (<=>(p, LitTrue()), p),
    (<=>(p, LitFalse()), Not(p)),
    (|||(p, p), p),
    (|||(p, Not(p)), LitTrue()),
    (Impl(p, p), LitTrue()),
    (|||(p, LitTrue()), LitTrue()),
    (|||(p, LitFalse()), p),
    (Impl(LitTrue(), p), p),
    (Impl(LitFalse(), p), LitTrue()),
    (Forall(mkTermAbs(s.o, p)), p),
    (Exists(mkTermAbs(s.o, p)), p),
    (Forall(mkTermAbs(s.o, <=>(mkTermApp(p, mkBound(s.o, 1)), mkTermApp(p, mkBound(s.o, 1))))), LitTrue())
  )

  for ((example, expectedResult) <- toSimplify) {
    test("Simplification Test: " + example.pretty, Checked) {
      simplify(example) should be(expectedResult)
    }
  }

  def simplify(t: Term): Term =
    Simplification.normalize(termToClause(t)).lits.head.term

  def termToClause(t: Term): Clause =
    Clause.mkClause(List(Literal(t, true)), Derived)
}
