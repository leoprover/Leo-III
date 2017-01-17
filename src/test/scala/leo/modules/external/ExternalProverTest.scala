package leo.modules.external

import leo.LeoTestSuite
import leo.datastructures.Term.mkAtom
import leo.datastructures._
import leo.modules.HOLSignature._

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
}
