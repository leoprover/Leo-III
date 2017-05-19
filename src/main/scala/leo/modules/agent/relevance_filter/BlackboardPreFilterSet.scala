package leo.modules.agent.relevance_filter

import leo.datastructures.blackboard._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.relevance_filter.PreFilterSet

/**
  * Created by mwisnie on 3/10/16.
  */
object BlackboardPreFilterSet extends DataStore{
  override def storedTypes: Seq[DataType[Any]] = Seq(FormulaTakenType, AnnotatedFormulaType)


  override def isEmpty: Boolean = get(AnnotatedFormulaType).isEmpty

  override def updateResult(r: Delta): Delta = synchronized {
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

    return r  // TODO can return to many results
  }

  override def clear(): Unit = PreFilterSet.clear()
  override def get[T](t: DataType[T]): Set[T] = {
    if (t == AnnotatedFormulaType)
      PreFilterSet.getFormulas.toSet.asInstanceOf[Set[T]]
    else {
      Set.empty
    }
  }
}
