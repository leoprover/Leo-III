package leo.modules.external

import leo.Configuration
import leo.datastructures.{AnnotatedClause, Signature}
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.prover.State

import scala.collection.mutable

/**
  *
  * Allows a call to external provers asynchronously.
  *
  */
trait AsyncTranslation {
  def killAll(): Unit

  def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit
}

class SequentialTranslationImpl extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
    ExtProverControl.sequentialSubmit(clauses, state, force)
  }

  override def killAll(): Unit = {
    ExtProverControl.sequentialKillExternals()
  }
}

