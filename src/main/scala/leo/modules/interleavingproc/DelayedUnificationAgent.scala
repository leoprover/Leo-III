package leo.modules.interleavingproc

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.{Clause}
import leo.datastructures.blackboard.{DataType, Event, Result}
import leo.modules.seqpproc.Control

/**
  *
  * Processes the delayed Unification in parallel to the main loop
  *
  * @since 11/17/16
  * @author Max Wisniewski
  */
class DelayedUnificationAgent(unificationStore : UnificationStore[InterleavingLoop.A], state : BlackboardState[InterleavingLoop.A]) extends AbstractAgent{
  override val name: String = "DelayedUnificationAgent"
  override val interest: Option[Seq[DataType]] = Some(Seq(OpenUnification))

  override def filter(event: Event): Iterable[Task] = event match {
    case result : Result =>
      val ins = result.inserts(OpenUnification)
      val rew = state.state.rewriteRules
      val tasks =  ins.map{case a : InterleavingLoop.A => new DelayedUnificationTask(a, this, rew)}
      return tasks
    case _ => Seq()
  }

  override def init(): Iterable[Task] = {
    val ins = unificationStore.getOpenUni
    val rew = state.state.rewriteRules
    ins.map{a => new DelayedUnificationTask(a, this, rew)}
  }
}

class DelayedUnificationTask(ac : InterleavingLoop.A, a : DelayedUnificationAgent, rewrite : Set[InterleavingLoop.A]) extends Task {
  override val name: String = "delayedUnification"

  override def run: Result = {
    val result = Result()
    result.remove(OpenUnification)(ac)
    val newclauses = Control.preunifyNewClauses(Set(ac))
    val sb = new StringBuilder("\n")
    sb.append(s">>Unified Clause:\n>>   ${ac.pretty}\n>> to")

    val newIt = newclauses.iterator
    while (newIt.hasNext) {
      var newCl = newIt.next()
      newCl = Control.rewriteSimp(newCl, rewrite)

      if (!Clause.trivial(newCl.cl)) {
        sb.append(s"\n>>   ${newCl.pretty}")
        result.insert(UnprocessedClause)(newCl)
      }
    }
    sb.append("\n")
    leo.Out.info(sb.toString())
    result

  }

  override lazy val readSet: Map[DataType, Set[Any]] = Map()
  override lazy val writeSet: Map[DataType, Set[Any]] = Map(OpenUnification -> Set(ac))   // TODO Get writelock?
  override lazy val bid: Double = 0.1
  override val getAgent: Agent = a

  override val pretty: String = s"delayedUnification(${ac.pretty})"
}
