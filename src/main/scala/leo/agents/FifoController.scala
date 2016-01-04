package leo.agents

import leo._
import leo.datastructures.blackboard.{ActiveTracker, Event, Blackboard, LockSet}

import scala.collection.mutable


/**
 * Implements the sorting, selection and saving of tasks of the agent interface.
 *
 * Only the explicit filtering, own tasks and the execution have to be implemeted.
 *
 *
 * The tasks are executed in the order they are generated.
 */
class FifoController(a : Agent) extends AgentController(a) {

  override def setActive(a : Boolean) = {
    super.setActive(a)
    if(a)
      ActiveTracker.subAndGet(q.synchronized(q.size))
    else
      ActiveTracker.addAndGet(q.synchronized(q.size))
    if(a && q.nonEmpty) Blackboard().signalTask()
  }

  override def openTasks : Int = q.size

  override def unregister(): Unit ={
    super.unregister()
    ActiveTracker.subAndGet(q.synchronized(q.size))
    q.synchronized(q.clear())
  }

  protected val q : mutable.Queue[Task] = new mutable.Queue[Task]()

  override def hasTasks : Boolean = q.synchronized(q.nonEmpty)

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filter(f: Event) : Unit = {
    var done = false
    for(t <- a.toFilter(f)) {
      if (!LockSet.isOutdated(t)) {
        q.synchronized {
          ActiveTracker.incAndGet(s"New Pending task : ${t.pretty}")
          q.enqueue(t)
        }
        done = true
      } else {
        ActiveTracker.addComment(s"Not creating a new task (outdated): ${t.pretty}")
      }
    }
    if(done) {
      Blackboard().signalTask()
    }
  }

  /**
   *
   * Returns a a list of Tasks, the Agent can afford with the given budget ( relative costs <=1)
   *
   */
  override def getTasks : Iterable[Task] = {
    var erg = List[Task]()
    var costs : Double = 0
    q.synchronized {
      for (t <- q) {
        if (costs > 1) return erg
        else {
          costs += t.bid
          erg = t :: erg
        }
      }
    }
    erg
  }

  /**
   * Removes all Tasks
   */
  override def clearTasks(): Unit = {
    ActiveTracker.subAndGet(q.synchronized(q.size))
    q.synchronized(q.clear())
  }

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = q.synchronized(q.iterator.toIterable)

  /**
   *
   * Given a set of (newly) executing tasks, remove all outdated tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = q.synchronized(q.dequeueAll{tbe =>
    nExec.exists{e =>
      if(e.eq(tbe)) {
        true
      }else {
        val sharedTypes = tbe.lockedTypes & e.lockedTypes
        sharedTypes exists { d =>
          val we = e.writeSet().getOrElse(d, Set.empty[Any])
          val wtb = tbe.writeSet().getOrElse(d, Set.empty[Any])
          val rtb = tbe.readSet().getOrElse(d, Set.empty[Any])

          val rem = (we & wtb).nonEmpty || (we & rtb).nonEmpty // If the tbe task excesses any data, that will be updated.
          if (rem && !e.eq(tbe)) Out.trace(s"The task\n  $tbe\n collided with\n  $e\n and was removed.")
          if(rem) ActiveTracker.decAndGet(s"Remove from collision ${tbe.pretty}")
          rem
        }
      }
    }
  })
}