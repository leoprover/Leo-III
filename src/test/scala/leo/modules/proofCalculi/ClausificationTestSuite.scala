package leo
package modules.proofCalculi

import leo.modules.Utility._
import org.scalatest.FunSuite
import leo.modules.CLParameterParser
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import leo.datastructures.{Derived, Clause}

/**
 * Created by max on 12.01.2015.
 */
@RunWith(classOf[JUnitRunner])
class ClausificationTestSuiteextends extends FunSuite{
  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))

  test("And Positive") {
    clear()
    clearSignature()
    add("fof(a,conjecture,r&(p&q)).")
    add("fof(l, axiom, r).")
    add("fof(r, axiom, (p&q)).")
    val f = get("a")
    val l = get("l").clause
    val r = get("r").clause
    val nc = Clausification.clausify(f.clause)

    assert(testRes(nc,l), "The clausification should contain \n"++l.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
    assert(testRes(nc,r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  test("And Negative") {
    clear()
    clearSignature()
    add("fof(a,conjecture,r&(p&q)).")
    add("fof(l, axiom, r).")
    add("fof(r, axiom, (p&q)).")
    val f = get("a").clause.mapLit(_.flipPolarity)  // Flip the polarity for test
    val r = Clause.mkClause(get("l").clause.lits++get("r").clause.lits, Nil, Derived).mapLit(_.flipPolarity)

    val nc = Clausification.clausify(f)

    assert(testRes(nc, r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  test("Or Positive") {
    clear()
    clearSignature()
    add("fof(a,conjecture,r | (p&q)).")
    add("fof(l, axiom, r).")
    add("fof(r, axiom, (p&q)).")
    val f = get("a").clause  // Flip the polarity for test
    val r = Clause.mkClause(get("l").clause.lits++get("r").clause.lits, Nil, Derived)

    val nc = Clausification.clausify(f)

    assert(testRes(nc, r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  test("Or Negative") {
    clear()
    clearSignature()
    add("fof(a,conjecture,r | (p&q)).")
    add("fof(l, axiom, r).")
    add("fof(r, axiom, (p&q)).")

    val f = get("a").clause.mapLit(_.flipPolarity)
    val l = get("l").clause.mapLit(_.flipPolarity)
    val r = get("r").clause.mapLit(_.flipPolarity)
    val nc = Clausification.clausify(f)

    assert(testRes(nc,l), "The clausification should contain \n"++l.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
    assert(testRes(nc,r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  test("Not Positve") {
    clear()
    clearSignature()
    add("fof(a,conjecture,~q).")
    add("fof(l, axiom, q).")

    val f = get("a").clause
    val r = get("l").clause.mapLit(_.flipPolarity)

    val nc = Clausification.clausify(f)

    assert(testRes(nc, r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  test("Not Negaitve") {
    clear()
    clearSignature()
    add("fof(a,conjecture,~q).")
    add("fof(l, axiom, q).")

    val f = get("a").clause.mapLit(_.flipPolarity)
    val r = get("l").clause

    val nc = Clausification.clausify(f)

    assert(testRes(nc, r), "The clausification should contain \n"++r.pretty++", but was \n"++nc.map(_.pretty).mkString(", "))
  }

  private def testRes(c1 : Seq[Clause], c2 : Clause) : Boolean = c1 exists(cmpClause(_,c2))

  private def cmpClause(c1 : Clause, c2 : Clause) : Boolean = {
    c1.lits forall { l1 =>
      c2.lits exists {l2 =>
        l1.polarity == l2.polarity && l1.term == l2.term
      }
    }
  }
}
