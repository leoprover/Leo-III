package leo.modules.external

import leo.LeoTestSuite
import leo.datastructures.Term.mkAtom
import leo.datastructures._
import leo.modules.HOLSignature._
import leo.modules.output.{SZS_CounterSatisfiable, SZS_Theorem}

/**
  * Created by mwisnie on 1/17/17.
  */
class ExternalProverTest extends LeoTestSuite {

  def createSmallProblem(implicit sig : Signature) : Set[AnnotatedClause] = {
    val p = mkAtom(sig.addUninterpreted("p", o))
    val l1 = Literal(p, true)
    val l2 = Literal(p, false)
    Set(
      AnnotatedClause(Clause(l1), ClauseAnnotation.NoAnnotation),
      AnnotatedClause(Clause(l2), ClauseAnnotation.NoAnnotation)
    )
  }

  def createSmallCSAProblem(implicit sig : Signature) : Set[AnnotatedClause] = {
    val p = mkAtom(sig.addUninterpreted("p", o))
    val l = Literal(p,false)
    Set(AnnotatedClause(Clause(l), ClauseAnnotation.NoAnnotation))
  }

  test("Prover Creation") {
    try {
      val p = ExternalProver.createLeo2("/home/mwisnie/prover/leo2/bin/leo")
      assert(p.name == "leo2")
    } catch {
      case e : NoSuchMethodException => println("Check your export of leo2 : 'export leo = /...'")
      case e : Exception => fail(e.getMessage)
    }
  }

  test("Prover Creation by name") {
    try {
      val p = ExternalProver.createProver("leo2","/home/mwisnie/prover/leo2/bin/leo")
      assert(p.name == "leo2")
    } catch {
      case e : NoSuchMethodException => println("Check your export of leo2 : 'export leo = /...'")
      case e : Exception => fail(e.getMessage)
    }
  }

  test("Translate problem"){
    val p = ExternalProver.createLeo2("/home/mwisnie/prover/leo2/bin/leo")
    implicit val s = getFreshSignature

    val example = createSmallProblem
    val trans = p.translateProblem(example)

    println(s"Translate problem : \n${trans.mkString("\n")}")
  }

  test("Call prover (THM)"){
    val p = ExternalProver.createLeo2("/home/mwisnie/prover/leo2/bin/leo")
    implicit val s = getFreshSignature

    val example = createSmallProblem
    val fres = p.call(example, 1000)

    // Test the non-blocking waiting for the result.
    while(!fres.isCompleted) {
      try{
        Thread.sleep(50)
        println("Look for result")
      } catch{ case e : InterruptedException => ()}
    }
    fres.value match {
      case Some(res) =>
        println(s"Got result ${res.szsStatus()} with exitvalue ${res.exitValue}")
        assert(res.szsStatus == SZS_Theorem, s"Expected ${SZS_Theorem.apply} but got ${res.szsStatus()}")
      case None => fail("Got no result from the prover")
    }
  }

  test("Call prover (CSA)") {
    val p = ExternalProver.createLeo2("/home/mwisnie/prover/leo2/bin/leo")
    implicit val s = getFreshSignature

    val example = createSmallCSAProblem
    val fres = p.call(example, 1000)

    // Test the non-blocking waiting for the result.
    while(!fres.isCompleted) {
      try{
        Thread.sleep(50)
        println("Look for result")
      } catch{ case e : InterruptedException => ()}
    }
    fres.value match {
      case Some(res) =>
        println(s"Got result ${res.szsStatus()} with exitvalue ${res.exitValue}")
        assert(res.szsStatus == SZS_CounterSatisfiable, s"Expected ${SZS_CounterSatisfiable.apply} but got ${res.szsStatus()}")
      case None => fail("Got no result from the prover")
    }
  }
}
