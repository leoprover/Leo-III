package leo.modules.modes

import leo.Configuration
import leo.datastructures._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.external.{Capabilities, ExternalProver}
import leo.modules.output.SZS_Error
import leo.modules.parsers.Input

/**
  * Created by mwisnie on 9/26/16.
  */
object RunExternalProver {
  def apply(parsedProblem: scala.Seq[AnnotatedFormula]): Unit = {
    if(Configuration.ATPS.isEmpty){
      println("% This program tests external ATP calls. Please specify one")
      return
    }
    val (name, path) = Configuration.ATPS.head

    val p = ExternalProver.createProver(name,path)

    implicit val s = Signature.freshWithHOL()
    val input2 = Input.processProblem(parsedProblem)
    var exSeq : Seq[AnnotatedClause] = Seq()
    val it = input2.iterator
    while(it.hasNext){
      val (id, term, role) = it.next()
      if(role == Role_Conjecture){
        val cl = AnnotatedClause(Clause(Seq(Literal(term, false))), Role_NegConjecture, ClauseAnnotation.NoAnnotation, ClauseAnnotation.PropNoProp)
        exSeq = cl +: exSeq
      } else if (role == Role_Axiom || role == Role_NegConjecture) {
        val cl = AnnotatedClause(Clause(Seq(Literal(term, true))), role, ClauseAnnotation.NoAnnotation, ClauseAnnotation.PropNoProp)
        exSeq = cl +: exSeq
      }
    }
    val example : Set[AnnotatedClause] = exSeq.toSet


    // Process our own input
    println(s"Problem to check :\n ${example.map(_.pretty(s)).mkString("\n")}")

    val max = Configuration.ATP_TIMEOUT(name) * 1000 //.getOrElse(name, Configuration.ATP_STD_TIMEOUT) * 1000
    // It uses a MapWithDefault, should return STDTimoout if no entry exists

    val fres = p.call(example, example.map(_.cl), s, Capabilities.THF, max)

    // Test the non-blocking waiting for the result.
    var time = 0
    var freq = 2
    while(!fres.isCompleted) {
      try{
        Thread.sleep(freq)
        time += freq
        freq = Math.max(500, Math.min(max - time, (freq * 5)/3)) // Double the waiting time
        println(s"[$time ms of $max ms]: Waiting for result")
      } catch{ case e : InterruptedException => ()}
    }

    fres.value match {
      case Some(res) =>
        println(s"Got result ${res.szsStatus()} with exitvalue ${res.exitValue}")
        if(res.szsStatus == SZS_Error){
          println(res.error.mkString("\n"))
        }
        println(s"Leo's output:\n  ${res.output.mkString("\n  ")}")
      case None => println("Got no result")
    }
  }

  def apply(): Unit = {
    apply(Input.parseProblemFile(Configuration.PROBLEMFILE))
  }
}
