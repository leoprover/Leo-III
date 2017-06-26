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

  def killAll()
}

class SequentialTranslationImpl extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = {
    ExtProverControl.sequentialSubmit(clauses, state)
  }

  override def killAll(): Unit = {
    ExtProverControl.sequentialKillExternals()
  }
}

class AsyncTranslationImpl(scheduler : Scheduler) extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause]): Unit = {
//    println("Called sync Trans")
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

  override def killAll(): Unit = ()
}