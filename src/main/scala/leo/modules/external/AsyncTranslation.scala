package leo.modules.external

import java.util.concurrent.Executors

import leo.Configuration
import leo.datastructures.{AnnotatedClause, Signature}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.control.externalProverControl.ExtProverControl
import leo.modules.prover.State

import scala.collection.mutable

/**
  *
  * Allows a call to external provers asynchronously.
  *
  */
trait AsyncTranslation {
  def killAll()

  def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false)
}

class SequentialTranslationImpl extends AsyncTranslation {
  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force: Boolean = false): Unit = {
    ExtProverControl.sequentialSubmit(clauses, state, force)
  }

  override def killAll(): Unit = {
    ExtProverControl.sequentialKillExternals()
  }
}

class SchedulerTranslationImpl(scheduler : Scheduler) extends AsyncTranslation {

  private val waitingTime : Long = Configuration.ATP_CALL_INTERVAL

  private val lastCalls : mutable.Map[State[AnnotatedClause], Long] = mutable.Map()
  private val self = this

  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force : Boolean): Unit = {
    val lastCallState : Long= lastCalls.getOrElse(state, 0)
    if(ExtProverControl.shouldRun(clauses,state) && ((lastCallState + waitingTime) <= state.noProofLoops)) {
      val runthread = new Runnable {
        override def run(): Unit = {
          ExtProverControl.sequentialSubmit(clauses, state, force)
        }
      }
      try {
        scheduler.submitIndependent(runthread) // Free?
      } catch {
        case e: Exception => ()
      }
    }
  }

  override def killAll(): Unit = {
    ExtProverControl.sequentialKillExternals()
  }
}

class PrivateThreadPoolTranslationImpl(numberOfThreads : Int) extends AsyncTranslation {

  private val waitingTime : Long = Configuration.ATP_CALL_INTERVAL
  private val threadpool = Executors.newFixedThreadPool(numberOfThreads)

//  private val lastCalls : mutable.Map[State[AnnotatedClause], Long] = mutable.Map()
  private val self = this

  override def call(clauses: Set[AnnotatedClause], state: State[AnnotatedClause], force : Boolean): Unit = {
//    val lastCallState : Long = lastCalls.getOrElse(state, 0)
    if(ExtProverControl.shouldRun(clauses, state)) {
      state.incTranslations
//      lastCalls.put(state, state.noProofLoops)
      // Add a block now to allow tests for still running at the end
      val runthread = new Runnable {
        override def run(): Unit = {
          ExtProverControl.uncheckedSequentialSubmit(clauses, state, force)
          state.decTranslations
        }
      }
      try{
        threadpool.execute(runthread)
      } catch {
        case e : Exception => ()
      }
    }
  }

  override def killAll(): Unit = {
    ExtProverControl.sequentialKillExternals()
    threadpool.shutdownNow()
  }
}

