package leo.modules.external

import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.prover.State

/**
  *
  * Allows a call to external provers asynchronously.
  *
  */
trait AsyncTranslation {
  def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false)
}

class SequentialTranslationImpl extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
    ExtProverControl.sequentialSubmit(clauses, state, force)
  }
}

class AsyncTranslationImpl(scheduler : Scheduler) extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
    val runthread = new Runnable {
      override def run(): Unit = {
        ExtProverControl.sequentialSubmit(clauses, state)
      }
    }
    try {
      scheduler.submitIndependent(runthread) // Free?
    } catch {
      case e : Exception => ()
    }
  }
}