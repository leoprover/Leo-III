package leo.modules.agent.relevance_filter

import leo.datastructures.blackboard._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.relevance_filter.PreFilterSet

/**
  * Created by mwisnie on 3/10/16.
  */
object BlackboardPreFilterSet extends DataStore{
  override def storedTypes: Seq[DataType[Any]] = Seq(FormulaTakenType, AnnotatedFormulaType)

  override def updateResult(r: Delta): Boolean = synchronized {
    val ins = r.inserts(AnnotatedFormulaType).iterator
    val take = r.inserts(FormulaTakenType).iterator

    while(ins.nonEmpty){
      val in = ins.next()
      PreFilterSet.addNewFormula(in)
    }

    while(take.nonEmpty){
      val (taken, round) = take.next()
      PreFilterSet.useFormula(taken)
    }

    return ins.nonEmpty || take.nonEmpty
  }

  override def clear(): Unit = PreFilterSet.clear()
  override def all[T](t: DataType[T]): Set[T] = {
    if (t == AnnotatedFormulaType)
      PreFilterSet.getFormulas.toSet.asInstanceOf[Set[T]]
    else {
      Set.empty
    }
  }
}
