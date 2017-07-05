package leo.modules.calculus

import leo.{Checked, LeoTestSuite}
import leo.datastructures.{Term, Type}
import leo.datastructures.Term.mkAtom
import leo.modules.HOLSignature.{i, o}
import leo.modules.parsers.Input
import leo.modules.termToClause

/**
  * Created by mwisnie on 7/5/17.
  */
class DomainConstraintInstancesTest extends LeoTestSuite {
  test("Single Instance Single Variable", Checked) {
    implicit val sig = getFreshSignature

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
//    val b = mkAtom(sig.addUninterpreted("b", i))

    val f = Input("! [X : $i]: (p(X))")
    val res = Input("p(a)")
    assert(Term.wellTyped(f))

    val c = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    val cres = FullCNF.apply(freshVarGenFromBlank, termToClause(res, true))
    assert(c.size == 1)
    assert(cres.size == 1)


    val domain = Map(i -> Set(a))

    val ergs = DomainConstraintInstances.apply(c.head, domain, -1)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 1)
    assert(ergs.toSeq.head == cres.head)
  }

  test("Double Instance Single Variable", Checked) {
    implicit val sig = getFreshSignature

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))

    val f = Input("! [X : $i]: (p(X))")
    assert(Term.wellTyped(f))

    val c = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(c.size == 1)


    val domain = Map(i -> Set(a, b))

    val ergs = DomainConstraintInstances.apply(c.head, domain, -1)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 2)
  }

  test("Single Instance Double Variable", Checked) {
    implicit val sig = getFreshSignature

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
//    val b = mkAtom(sig.addUninterpreted("b", i))

    val f = Input("! [X : $i, Y : $i]: (p(X) | q(Y))")
    assert(Term.wellTyped(f))

    val c = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(c.size == 1)


    val domain = Map(i -> Set(a))

    val ergs = DomainConstraintInstances.apply(c.head, domain, -1)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 1)
  }

  test("Double Instance Double Variable", Checked) {
    implicit val sig = getFreshSignature

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))

    val f = Input("! [X : $i, Y : $i]: (p(X) | q(Y))")
    assert(Term.wellTyped(f))

    val c = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(c.size == 1)


    val domain = Map(i -> Set(a, b))

    val ergs = DomainConstraintInstances.apply(c.head, domain, -1)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 4)
  }

  test("Under Max Instances") {
    implicit val sig = getFreshSignature

    val maxInstances = 27

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", i ->: o))
    val r = mkAtom(sig.addUninterpreted("r", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))
    val c = mkAtom(sig.addUninterpreted("c", i))

    val f = Input("! [X : $i, Y : $i, Z : $i]: (p(X) | q(Y) | r(Z))")
    assert(Term.wellTyped(f))

    val cl = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(cl.size == 1)


    val domain = Map(i -> Set(a, b, c))

    val ergs = DomainConstraintInstances.apply(cl.head, domain, maxInstances)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 27)
  }

  test("Over Max Instances") {
    implicit val sig = getFreshSignature

    val maxInstances = 27

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", i ->: o))
    val r = mkAtom(sig.addUninterpreted("r", i ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))
    val c = mkAtom(sig.addUninterpreted("c", i))
    val d = mkAtom(sig.addUninterpreted("d", i))

    val f = Input("! [X : $i, Y : $i, Z : $i]: (p(X) | q(Y) | r(Z))")
    assert(Term.wellTyped(f))

    val cl = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(cl.size == 1)


    val domain = Map(i -> Set(a, b, c, d))

    val ergs = DomainConstraintInstances.apply(cl.head, domain, maxInstances)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size <= 27)
  }

  test("Multi Domain", Checked) {
    implicit val sig = getFreshSignature

    val maxInstances = -1

    val pt = Type.mkType(sig.addBaseType("pt"))

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", pt ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))
    val c = mkAtom(sig.addUninterpreted("c", pt))
    val d = mkAtom(sig.addUninterpreted("d", pt))
    val e = mkAtom(sig.addUninterpreted("e", pt))

    val f = Input("! [X : $i, Y : pt]: (p(X) | q(Y))")
    assert(Term.wellTyped(f))

    val cl = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(cl.size == 1)


    val domain = Map(i -> Set(a, b), pt -> Set(c,d,e))

    val ergs = DomainConstraintInstances.apply(cl.head, domain, maxInstances)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 6)
  }

  /**
    * In this test we consider the sorting.
    * The domain $i should be instanciated, since it produces
    * less clauses.
    */
  test("Multi Domain under Max Instances", Checked) {
    implicit val sig = getFreshSignature

    val maxInstances = 3

    val pt = Type.mkType(sig.addBaseType("pt"))

    val p = mkAtom(sig.addUninterpreted("p", i ->: o))
    val q = mkAtom(sig.addUninterpreted("q", pt ->: o))

    val a = mkAtom(sig.addUninterpreted("a", i))
    val b = mkAtom(sig.addUninterpreted("b", i))
    val c = mkAtom(sig.addUninterpreted("c", pt))
    val d = mkAtom(sig.addUninterpreted("d", pt))
    val e = mkAtom(sig.addUninterpreted("e", pt))

    val f = Input("! [X : $i, Y : pt]: (p(X) | q(Y))")
    assert(Term.wellTyped(f))

    val cl = FullCNF.apply(freshVarGenFromBlank, termToClause(f, true))
    assert(cl.size == 1)


    val domain = Map(i -> Set(a, b), pt -> Set(c,d,e))


    val ergs = DomainConstraintInstances.apply(cl.head, domain, maxInstances)

    println(ergs.map(_.pretty(sig)).mkString("\n"))

    assert(ergs.size == 2)
  }

  test("Extracting all variables (even eta)", Checked){
    implicit val sig = getFreshSignature

    val var_eta = Term.mkTermAbs(i, Term.mkTermApp(Term.mkBound(i, 2), Term.mkBound(i,1)))
    assert(Term.wellTyped(var_eta))

    assert(!var_eta.isVariable)
    assert(leo.datastructures.isVariableModuloEta(var_eta))
  }
}
