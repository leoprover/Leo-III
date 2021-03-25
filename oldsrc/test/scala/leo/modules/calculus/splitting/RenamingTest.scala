package leo.modules.calculus.splitting

import leo.datastructures.Term
import leo.modules.HOLSignature._
import leo.modules.calculus.FormulaRenaming
import leo.{Checked, LeoTestSuite}

/**
  * Created by mwisnie on 1/26/17.
  */
class RenamingTest extends LeoTestSuite {
  test("CNF_Size Test 1", Checked){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = |||(|||(|||(a,b),|||(a,b)), a)
    val size = FormulaRenaming.size(t,true)

    assert(size == 1, s"CNF_SIZE(${t.pretty(s)} should be 1 but was $size")
  }

  test("CNF_Size Test 2", Checked){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = |||(&(|||(a,b),|||(a,b)), &(a,b))
    val size = FormulaRenaming.size(t,true)

    assert(size == 4, s"CNF_SIZE(${t.pretty(s)} should be 4 but was $size")
  }

  test("CNF_Size Test 3", Checked){
    implicit val s = getFreshSignature
    val ka = s.addUninterpreted("a", o)
    val a = Term.mkAtom(ka)
    val kb = s.addUninterpreted("b", o)
    val b = Term.mkAtom(kb)

    val t = <=>(a,b)
    val size = FormulaRenaming.size(t.δ_expand.betaNormalize,true)

    assert(size == 2, s"CNF_SIZE(${t.δ_expand.betaNormalize.pretty(s)} should be 2 but was $size")
  }
}
