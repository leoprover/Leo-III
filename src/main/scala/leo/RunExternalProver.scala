package leo

import leo.datastructures._
import leo.modules.HOLSignature.Not
import leo.modules.{CLParameterParser, Parsing}
import leo.modules.external.ExternalProver
import leo.modules.output.SZS_Error

/**
  * Created by mwisnie on 9/26/16.
  */
object RunExternalProver {
  def runExternal(): Unit ={
    if(Configuration.ATPS.isEmpty){
      println("% This program tests external ATP calls. Please specify one")
      return
    }
    val (name, path) = Configuration.ATPS.head

    if(name != "leo2" & name != "nitpick"){
      println("% Currently there is only support for leo2 and nitpick.")
      return
    }
    val p = ExternalProver.createProver(name,path)

    implicit val s = Signature.freshWithHOL()
    val input = Parsing.readProblem(Configuration.PROBLEMFILE)
    val input2 = Parsing.processProblem(input)
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

    val max = Configuration.ATP_TIMEOUT.getOrElse(name, Configuration.ATP_STD_TIMEOUT) * 1000

    val fres = p.call(example, max)

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
}