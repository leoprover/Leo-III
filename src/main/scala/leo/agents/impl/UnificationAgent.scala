package leo
package agents
package impl

import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{UnificationTaskType, UnificationStore, UnifierType, UnifierStore}

/**
 *
 * The unification agent looks out for unification constraints and tests
 * all unused unifier on them
 *
 * @author Max Wisniewski
 * @since 5/13/15
 */
class UnificationAgent extends Agent {
  override def name: String = "unification agent"
  override val interest = Some(List(UnificationTaskType, UnifierType))

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = t match {
    case UnificationTask(f,t1,t2,s) =>
      trace(s"Run unification task on clause ${f.clause.pretty}: \n unify ${t1.pretty} and ${t2.pretty} with \n ${s.pretty}")
      val nf = Store(f.clause.substitute(s), f.created, Role_Plain, f.context, f.status, NoAnnotation) // TODO apply triv rules and replace [t = t] = b with [true] = b
      Result().insert(UnificationTaskType)(nf).insert(UnifierType)(UnifierStore(f,t1,t2,s))
    case FinishUnify(f) =>
      trace(s"Finished unification. Inserted\n   ${f.pretty}\ninto active.")
      Result().insert(FormulaType)(f)
    case _ => Result()
  }

  /**
   * Scans upon insertion of
   * a) new formula if there is a unification constrained.
   * b) a performed unification, if there could be applied another unifier
   */
  override def toFilter(event: Event): Iterable[Task] = event match {
    case DataEvent(f : FormulaStore, UnificationTaskType) =>
      if(f.clause.uniLits.isEmpty) return List(FinishUnify(f))
      f.clause.uniLits.map{ l => l.eqComponents match {
        case Some((t1,t2))  => UnificationStore.nextUnifier(f,t1,t2).fold(Nil : List[Task]){s => List(UnificationTask(f,t1,t2,s))}
        case _  => Nil
      }}.flatten
    case DataEvent(UnifierStore(f, t1, t2, subst), UnifierType) => // Executed One, take the next one
      if(f.clause.uniLits.isEmpty) return List(FinishUnify(f)) // Should not happen
      UnificationStore.nextUnifier(f,t1,t2).fold(Nil : List[Task]){s => List(UnificationTask(f,t1,t2,s))}
    case _ => Nil
  }
}

private case class UnificationTask(f : FormulaStore, t1 : Term, t2 : Term, s : Subst) extends Task {

  override def name: String = "unification task"
  override def writeSet(): Set[FormulaStore] = Set()
  override def readSet(): Set[FormulaStore] = Set(f)
  override def bid(budget: Double): Double = budget / (1+f.clause.uniLits.size)

  override def pretty: String = "unify"
}

private case class FinishUnify(f : FormulaStore) extends Task {
  override def name : String = "finish unification task"
  override def writeSet(): Set[FormulaStore] = Set()
  override def readSet(): Set[FormulaStore] = Set(f)
  override def bid(budget: Double): Double = budget / 2

  override def pretty: String = "unify"
}