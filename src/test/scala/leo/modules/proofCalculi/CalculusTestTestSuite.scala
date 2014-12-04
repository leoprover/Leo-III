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
    val res = PropParamodulation.find(b.clause,a.clause, IdComparison).get
    Out.output("Term: "+res._1.pretty)
    Out.output("Literal: "+res._2.pretty)

  }

  test("Exec Prop") {
    Out.output("\nExec Prop:\n\n")
    clear()
    clearSignature()
    add("fof(a,conjecture,p).")
    add("fof(b,axiom,(p&q)).")

    val a = get("a")
    val b = get("b")
    Out.output("Formula a: "+a.pretty)
    Out.output("Formula b: "+b.pretty)
    val sub = PropParamodulation.find(b.clause,a.clause, IdComparison).get

    val t = sub._1
    val l = sub._2
    val s = sub._3

    val arm = a.clause

    val res = PropParamodulation.exec(b.clause, Clause.empty, t, l, s).mapLit(_.termMap(_.betaNormalize))

    Out.output("Result Clause: "+res.pretty)
  }

  test("Exec Eq") {
    Out.output("\nExec Eq:\n\n")
    clear()
    clearSignature()
    add("fof(a,conjecture,(p=q)).")
    add("fof(b,axiom,(p&q)).")

    val a = get("a")
    val b = get("b")
    Out.output("Formula a: "+a.pretty)
    Out.output("Formula b: "+b.pretty)
    val sub = Paramodulation.find(b.clause,a.clause, IdComparison).get

    val t = sub._1
    val l = sub._2
    val s = sub._3

    val arm = a.clause

    val res = Paramodulation.exec(b.clause, Clause.empty, t, l, s).mapLit(_.termMap(_.betaNormalize))

    Out.output("Result Clause: "+res.pretty)
  }

  test("Negeted Test") {
    Out.output("\nNegated Prop:\n\n")
    clear()
    clearSignature()
    add("fof(a,negated_conjecture,(~p)).")
    add("fof(b,axiom,(p&q)).")

    val a1 = get("a")
    val b1 = get("b")

    val a = a1.newClause(Simp(a1.clause))
    val b = b1.newClause(Simp(b1.clause))

    Out.output("Formula a: "+a.pretty)
    Out.output("Formula b: "+b.pretty)
    val sub = PropParamodulation.find(b.clause,a.clause, IdComparison).get

    val t = sub._1
    val l = sub._2
    val s = sub._3

    val arm = a.clause

    val res = PropParamodulation.exec(b.clause, Clause.empty, t, l, s).mapLit(_.termMap(_.betaNormalize))

    Out.output("Result Clause: "+res.pretty)
  }
}
