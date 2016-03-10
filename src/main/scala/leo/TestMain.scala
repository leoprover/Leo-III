package leo

import leo.datastructures.ClauseProxy
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.relevance_filter.{PreFilterSet, SeqFilter}
import leo.modules.{Parsing, CLParameterParser}
import leo.modules.external.ExternalCall
import leo.modules.output.ToTPTP
import leo.modules.phase.LoadPhase

/**
  * Created by mwisnie on 3/7/16.
  */
object TestMain {
  def main(args : Array[String]): Unit ={
    try {
      Configuration.init(new CLParameterParser(args))
    } catch {
      case e: IllegalArgumentException => {
        Out.severe(e.getMessage)
        return
      }
    }

    val read : Iterable[AnnotatedFormula] = Parsing.readProblem(Configuration.PROBLEMFILE)
    println("Used Symbols")
    println(read.map(f => s"$f\n  -> ${f.function_symbols.mkString(", ")}").mkString("\n"))

    val parsed : Iterable[ClauseProxy] = SeqFilter(read)

    println("Used :")
    println(parsed.map(_.pretty).mkString("\n"))
    println("Unused : ")
    println(PreFilterSet.getFormulas.mkString("\n"))

    /*
    val e = ExternalCall.exec("/home/mwisnie/prover/leo2/bin/leo -po 1 ", ToTPTP(it).map(_.output))
    println("Start executing")
    val exitV = e.exitValue
    val output = e.out
    println("Leo2 returned with "+exitV)
    while(output.hasNext){
      val o = output.next()
      println(o)
    }
    */
  }
}
