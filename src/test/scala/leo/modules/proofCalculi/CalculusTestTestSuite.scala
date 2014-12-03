package leo
package modules.proofCalculi

import leo.Configuration
import leo.datastructures.{Clause, TermIndex}
import leo.datastructures.blackboard.Blackboard
import leo.modules.CLParameterParser
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import leo.modules.Utility._

/**
 * Created by ryu on 12/3/14.
 */
@RunWith(classOf[JUnitRunner])
class CalculusTestTestSuite extends FunSuite{
  Configuration.init(new CLParameterParser(Array("arg0", "-v", "4")))

  test("Term Index") {
    clear()
    clearSignature()
    add("fof(a,conjecture,(p&q)).")
//    context()
  }

  test("Get subterms") {
    clear()
    clearSignature()
    add("fof(a,conjecture,r&(p&q)).")
    val f = get("a")
//    (f.clause.lits.head.term.occurrences) foreach {case (t,_) => Out.output(t.pretty)}
  }

  test("Obtain Partner") {
    clear()
    clearSignature()
    add("fof(a,conjecture,p).")
    add("fof(b,axiom,(p&q)).")

    val a = get("a")
    val b = get("b")
    val res = PropResolution.find(b.clause,a.clause, IdComparison).get
    Out.output("Term: "+res._1.pretty)
    Out.output("Literal: "+res._2.pretty)

  }

  test("Exec") {
    clear()
    clearSignature()
    add("fof(a,conjecture,p).")
    add("fof(b,axiom,(p&q)).")

    val a = get("a")
    val b = get("b")
    Out.output("Formula a: "+a.pretty)
    Out.output("Formula b: "+b.pretty)
    val sub = PropResolution.find(b.clause,a.clause, IdComparison).get

    val t = sub._1
    val l = sub._2
    val s = sub._3

    val arm = a.clause

    val res = PropResolution.exec(b.clause, Clause.empty, t, l, s).mapLit(_.termMap(_.betaNormalize))

    Out.output("Result Clause: "+res.pretty)
  }
}
