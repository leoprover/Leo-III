package leo.modules.agent.relevance_filter

import leo.datastructures.blackboard.{DataStore, DataType, Result, SignatureBlackboard}
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.relevance_filter.PreFilterSet

/**
  * Created by mwisnie on 3/10/16.
  */
object BlackboardPreFilterSet extends DataStore{
  override def storedTypes: Seq[DataType] = Seq(FormulaTakenType, AnnotatedFormulaType)

  override def updateResult(r: Result): Boolean = synchronized {
    val ins = r.inserts(AnnotatedFormulaType)
    if(ins.nonEmpty){
      ins.head match {
        case (form : AnnotatedFormula) => // New formula
          PreFilterSet.addNewFormula(form)(SignatureBlackboard.get)
        case (form : AnnotatedFormula, round : Int) => // Taken Formula
          PreFilterSet.useFormula(form)(SignatureBlackboard.get)
      }
      return true
    }
    return false
  }

  override def clear(): Unit = PreFilterSet.clear()
  override def all(t: DataType): Set[Any] = {
    if (t == AnnotatedFormulaType)
      PreFilterSet.getFormulas.toSet
    else {
      Set.empty
    }
  }
}
