package leo.modules.agent.relevance_filter

import leo.datastructures.blackboard.{DataStore, DataType, SignatureBlackboard}
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.relevance_filter.PreFilterSet

/**
  * Created by mwisnie on 3/10/16.
  */
object BlackboardPreFilterSet extends DataStore{
  override def storedTypes: Seq[DataType] = Seq(FormulaTakenType, AnnotatedFormulaType)
  override def update(o: Any, n: Any): Boolean = {
    leo.Out.debug("Update on AnnotatedFormula. Should not happen.")
    false
  }
  override def insert(n: Any): Boolean = n match {
    case (form : AnnotatedFormula) => // New formula
      PreFilterSet.addNewFormula(form)(SignatureBlackboard.get)
      true
    case (form : AnnotatedFormula, round : Int) => // Taken Formula
      PreFilterSet.useFormula(form)(SignatureBlackboard.get)
      true
  }
  override def clear(): Unit = PreFilterSet.clear()
  override def all(t: DataType): Set[Any] = {
    if (t == AnnotatedFormulaType)
      PreFilterSet.getFormulas.toSet
    else {
      Set.empty
    }
  }
  override def delete(d: Any): Unit = ()
}
