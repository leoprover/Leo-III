package leo.modules.interleavingproc

import java.util.concurrent.atomic.AtomicInteger

import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.{Clause, Signature}
import leo.datastructures.blackboard.{DataType, Delta, Event, Result}
import leo.modules.control.Control
import leo.modules.output.{SZS_ContradictoryAxioms, SZS_Theorem}

/**
  *
  * Processes the delayed Unification in parallel to the main loop
  *
  * @since 11/17/16
  * @author Max Wisniewski
  */
class DelayedUnificationAgent(unificationStore : UnificationStore[InterleavingLoop.A], state : BlackboardState, sig : Signature) extends AbstractAgent{
  implicit val s = sig
  override val name: String = "DelayedUnificationAgent"
  override val interest: Option[Seq[DataType[Any]]] = Some(Seq(OpenUnification))

  override def filter(event: Event): Iterable[Task] = event match {
    case result : Delta =>
      val ins = result.inserts(OpenUnification).filter{case a : InterleavingLoop.A => unificationStore.containsUni(a)}
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

  class DelayedUnificationTask(ac : InterleavingLoop.A, a : DelayedUnificationAgent, rewrite : Set[InterleavingLoop.A]) extends Task {
    override val name: String = "delayedUnification"

    override def run: Delta = {
//      val millis1 = System.currentTimeMillis()
//      println(s"+++++++++++ Unification start [${ac.id}] : ${(millis1 / 60000) % 60} min ${(millis1 / 1000)%60} s ${millis1 % 1000} ms")
      val result = Result()
      result.remove(OpenUnification)(ac)
      var newclauses = Control.unifyNewClauses(Set(ac))(sig)
//      val sb = new StringBuilder("\nUnify Clauses:")



      newclauses = newclauses.flatMap(cw => Control.cnf(cw))
      newclauses = newclauses.map(cw => Control.shallowSimp(Control.liftEq(cw)))
      val newIt = newclauses.iterator
      if(newIt.isEmpty){
        // Not unifiable. Insert Result
        result.insert(UnprocessedClause)(ac)
      }
      while (newIt.hasNext) {
        var newCl = newIt.next()
        newCl = Control.rewriteSimp(newCl, rewrite)
        assert(Clause.wellTyped(newCl.cl), s"Clause [${newCl.id}] is not well-typed")
        if (Clause.effectivelyEmpty(newCl.cl)){
          result.insert(DerivedClause)(newCl)
          if(state.state.conjecture != null) {
            result.insert(SZSStatus)(SZS_Theorem)
          } else {
            result.insert(SZSStatus)(SZS_ContradictoryAxioms)
          }
        }
        if (!Clause.trivial(newCl.cl)) {
//          sb.append(s"   ${ac.pretty(sig)}\n -->\n   ${newCl.pretty(sig)}")
          result.insert(UnprocessedClause)(newCl)
        } else {
//          sb.append(s"${ac.pretty(sig)} was trivial")
        }
      }
//      sb.append("\n")
//      leo.Out.output(sb.toString())
//      val millis = System.currentTimeMillis()
//      println(s"+++++++++++ Unification done [${ac.id}] : ${(millis / 60000) % 60} min ${(millis / 1000)%60} s ${millis % 1000} ms")
      result

    }

    override lazy val readSet: Map[DataType[Any], Set[Any]] = Map()
    override lazy val writeSet: Map[DataType[Any], Set[Any]] = Map(OpenUnification -> Set(ac))   // TODO Get writelock?
    override lazy val bid: Double = 0.1
    override val getAgent: Agent = a

    override val pretty: String = s"delayedUnification(${ac.pretty(sig)})"
  }
}


