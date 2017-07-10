package leo.modules.calculus

import leo.LeoTestSuite
import leo.datastructures.{AnnotatedClause, Literal, Term}
import leo.modules.HOLSignature._
import leo.modules.prover.State

/**
  * Created by mwisnie on 1/30/17.
  */
class FormulaRenamingTest extends LeoTestSuite{

  test("No rename"){
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = &(a,b)
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)

    assert(left == null && right == null, "There should be no definitions.")
    assert(l1 == l, "The term should not differ.")
  }

  test("Rename or simp"){
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = |||(a,b)
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename and simp"){
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = &(a,b)
    val l = Literal(t, false)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename impl simp"){
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(t, true)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename impl: Twice same occurance") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(Impl(a,t), true)
    val l2 = Literal(|||(t, b), true)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 == null && right2 == null, "There should be the reverse definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)}")
  }

  test("Rename impl: Twice same occurance different polarity") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(Impl(a,t), true)
    val l2 = Literal(&(t, b), false)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 != null && right2 != null, "There should be no definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)}\n [${Seq(left2, right2).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename &: Twice same occurance") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(&(a,t), false)
    val l2 = Literal(&(t, b), false)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 == null && right2 == null, "There should be no definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)}")
  }

  test("Rename &: Twice same occurance different polarity") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(&(a,t), false)
    val l2 = Literal(|||(t, b), true)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 != null && right2 != null, "There should be no definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)} \n [${Seq(left2, right2).map(_.pretty(s)).mkString(", ")}]")
  }

  test("Rename |: Twice same occurance") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(|||(a,t), true)
    val l2 = Literal(|||(t, b), false)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 == null && right2 == null, "There should be no definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)}")
  }

  test("Rename |: Twice same occurance different polarity") {
    implicit val s = getFreshSignature
    implicit val state : State[AnnotatedClause] = State.fresh(s)
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = Impl(a,b)
    val l = Literal(|||(a,t), true)
    val l2 = Literal(&(t, b), false)
    val (l1, left, right) = FormulaRenaming(l, state.renamingCash)
    val (l3, left2, right2) = FormulaRenaming(l2, state.renamingCash)

    assert(left != null && right != null, "There should be one definition")
    assert(left2 != null && right2 != null, "There should be no definition")
    println(s"${l.pretty(s)} renamed to\n  ${l1.pretty(s)}\n  [${Seq(left, right).map(_.pretty(s)).mkString(", ")}]")
    println(s"${l2.pretty(s)} renamed to\n  ${l3.pretty(s)} \n [${Seq(left2, right2).map(_.pretty(s)).mkString(", ")}]")
  }
}
