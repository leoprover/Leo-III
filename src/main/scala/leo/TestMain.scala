package leo

import leo.datastructures.ClauseProxy
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.modules.CLParameterParser
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

    val load = new LoadPhase("ex1.p")
    Blackboard().addDS(FormulaDataStore)

    load.execute()

    val it : Iterator[ClauseProxy] = FormulaDataStore.getFormulas.toIterator

    while(it.hasNext){
      val cp = it.next()
      Out.output(cp.pretty)
    }
  }
}
