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
  def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause])
}

class SequentialTranslationImpl extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = {
    ExtProverControl.sequentialSubmit(clauses, state)
  }
}

class AsyncTranslationImpl(scheduler : Scheduler) extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = {
    val runthread = new Runnable {
      override def run(): Unit = {
        println("Translating")
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