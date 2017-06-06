package leo.modules.agent.rules
package control_rules

import leo.datastructures.{AnnotatedClause, Clause}
import leo.datastructures.blackboard.{DataType, Delta}

/**
  * Created by mwisnie on 5/18/17.
  */
class EmptyClauseRule(out : DataType[AnnotatedClause], in : DataType[AnnotatedClause]*)
  extends Rule{
  override def name: String = "emptyClause"
  override def inTypes: Seq[DataType[Any]] = in
  override def moving: Boolean = true
  override def outTypes: Seq[DataType[Any]] = Seq(out)
  override def canApply(r: Delta): Seq[Hint] = {
    val it = in.iterator
    while(it.hasNext){
      val dt = it.next()
      val newData = r.inserts(dt).iterator
      while(newData.hasNext){
        val d = newData.next()
        if(Clause.effectivelyEmpty(d.cl)){
          leo.Out.debug(s"[EmptyClause] Found ${d.pretty}")
          return Seq(new MoveHint(d, dt, out))
        }
      }
    }
    Seq()
  }
}
