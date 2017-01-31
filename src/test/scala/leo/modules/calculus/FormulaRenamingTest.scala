package leo.modules.calculus

import leo.LeoTestSuite
import leo.datastructures.{Literal, Term}
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
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l)

    assert(left == null && right == null, "There should be no definitions.")
    assert(l1 == l, "The term should not differ.")
  }

  test("Rename or simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = |||(a,b)
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename and simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = &(a,b)
    val l = Literal(t, false)
    val (l1, left, right) = FormulaRenaming(l)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename impl simp"){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }
}
