package leo
package agents
package impl

import leo.datastructures.Term._
import leo.datastructures.impl.Signature
import leo.datastructures.{Exists, Literal, Forall, Term}
import leo.datastructures.blackboard._

/**
 * This Agent removes all initial all quantifiers and replaces them by MetaVariables.
 *
 *
 */
class MetaVarAgent extends Agent {
  override def name: String = "MetavarAgent"
  override val interest : Option[Seq[DataType]] = Some(List(FormulaType))

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case MetavarTask(f) => Result().update(FormulaType)(f)(f.newClause(f.clause.mapLit(replaceQuants(_))))
    case _ => Result()
  }

  /**
   * Triggers the filtering of the Agent.
   *
   * Upon an Event the Agent can generate Tasks, he wants to execute.
   * @param event on the blackboard concerning change of data.
   * @return a List of Tasks the Agent wants to execute.
   */
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, FormulaType) =>
      if((f.status & 31) == 31 && f.clause.lits.exists{l => initQuant(l)}) {
        // TODO: Remove status as soon as skolemize is acostumed to metavars.
        List(MetavarTask(f))
      } else {
        Nil
      }
    case _ => Nil
  }

  private def initQuant(l : Literal) : Boolean = l.term match {
    case Forall(ty :::> t1) if l.polarity => true
    case Exists(ty :::> t1) if !l.polarity => true
    case _  => false
  }

  private def replaceQuants(l : Literal) : Literal = l.termMap{t => replaceQuant(l.polarity)(t).betaNormalize}

  private def replaceQuant(pol : Boolean)(t : Term) : Term = t match {
    case Forall(ty :::> t1) if pol =>  mkTermApp(\(ty)(replaceQuant(pol)(t1)),Term.mkFreshMetaVar(ty))
    case Exists(ty :::> t1) if !pol => mkTermApp(\(ty)(replaceQuant(pol)(t1)),Term.mkFreshMetaVar(ty))
    case _ => t
  }
}

private class MetavarTask(var f : FormulaStore) extends Task {
  override def name: String = "Metavar replace"
  override def writeSet(): Set[FormulaStore] = Set(f)
  override def readSet(): Set[FormulaStore] = Set()
  override def bid(budget: Double): Double = budget / 5

  override def pretty: String = "metavar replace"
}

object MetavarTask {
  def apply(f : FormulaStore) : Task = new MetavarTask(f)

  def unapply(t : Task) : Option[FormulaStore] = t match {
    case t1 : MetavarTask => Some(t1.f)
    case _                => None
  }
}
