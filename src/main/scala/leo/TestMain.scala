package leo

import leo.datastructures.ClauseProxy
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.{BetaSplit, Context}
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.agent.preprocessing.{ArgumentExtractionAgent, EqualityReplaceAgent, FormulaRenamingAgent, NormalizationAgent}
import leo.modules.agent.relevance_filter.BlackboardPreFilterSet
import leo.modules.relevance_filter.{PreFilterSet, SeqFilter}
import leo.modules.{CLParameterParser, Parsing}
import leo.modules.external.ExternalCall
import leo.modules.output.ToTPTP
import leo.modules.phase.{FilterPhase, LoadPhase, PreprocessingPhase}

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

    val loadphase = new LoadPhase(Configuration.PROBLEMFILE)
    val filterphase = new FilterPhase()


    Blackboard().addDS(FormulaDataStore)
    Blackboard().addDS(BlackboardPreFilterSet)

    if(!loadphase.execute()) {
      Scheduler().killAll()
      return
    }

    if(!filterphase.execute()){
      Scheduler().killAll()
      return
    }


    // Were to split?
    // 4 Splits -> 1st Normal, 2nd Normalization + EqualityReplacement, 3rd + ArgumentExtraction, 4th + FormulaRenaming
    Context().split(BetaSplit, 4)
    val cs = Context().childContext.toSeq

    val preprocessphase = new PreprocessingPhase(Seq(new ArgumentExtractionAgent(cs(2),cs(3)), EqualityReplaceAgent, new FormulaRenamingAgent(cs(3)), new NormalizationAgent(cs(1),cs(2),cs(3))))

    if(!preprocessphase.execute()){
      Scheduler().killAll()
      return
    }

    Scheduler().killAll()
    println("Used :")
    println(FormulaDataStore.getFormulas.map(_.pretty).mkString("\n"))
    println("Unused : ")
    println(PreFilterSet.getFormulas.mkString("\n"))

    val c = Context()
    Context.leaves(c)

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
