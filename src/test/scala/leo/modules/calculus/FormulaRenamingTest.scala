package leo.modules.calculus

import leo.LeoTestSuite
import leo.datastructures.Term
import leo.modules.HOLSignature._

/**
  * Created by mwisnie on 1/30/17.
  */
class FormulaRenamingTest extends LeoTestSuite{

  test("No rename"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = &(a,b)
    val (t1, units) = FormulaRenaming(t, true)

    assert(units.isEmpty, "There should be no definitions.")
    assert(t1 == t, "The term should not differ.")
  }

  test("Rename or simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = |||(a,b)
    val (t1, units) = FormulaRenaming(t, true)

    assert(units.size == 2, "There should be one definition")
    println(s"${t.pretty(s)} renamed to\n  ${t1.pretty(s)}\n  [${units.map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename and simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = &(a,b)
    val (t1, units) = FormulaRenaming(t, false)

    assert(units.size == 2, "There should be one definition")
    println(s"~(${t.pretty(s)}) renamed to\n  ~(${t1.pretty(s)})\n  [${units.map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename impl simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val (t1, units) = FormulaRenaming(t, true)

    assert(units.size == 2, "There should be one definition")
    println(s"${t.pretty(s)} renamed to\n  ${t1.pretty(s)}\n  [${units.map(_.pretty(s)).mkString(", ")}]")
  }
}
