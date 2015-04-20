package leo.agents

import leo._
import leo.datastructures.blackboard.{Event, Blackboard}

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
    if(a && q.nonEmpty) Blackboard().signalTask()
  }

  override def openTasks : Int = q.size

  override def unregister(): Unit ={
    super.unregister()
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
      if (!Blackboard().collision(t)) {
        q.synchronized {
          q.enqueue(t)
        }
        done = true
      }
    }
    if(done) {
      Blackboard().signalTask()
    }
  }

  /**
   *
   * Returns a a list of Tasks, the Agent can afford with the given budget.
   *
   * @param budget - Budget that is granted to the agent.
   */
  override def getTasks(budget: Double): Iterable[Task] = {
    var erg = List[Task]()
    var costs : Double = 0
    q.synchronized {
      for (t <- q) {
        if (costs > budget) return erg
        else {
          costs += t.bid(budget)
          erg = t :: erg
        }
      }
    }
    erg
  }

  /**
   * Removes all Tasks
   */
  override def clearTasks(): Unit = q.synchronized(q.clear())

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = q.synchronized(q.iterator.toIterable)

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = q.synchronized(q.dequeueAll{tbe =>
    nExec.exists{e =>
      val rem = e.writeSet().intersect(tbe.writeSet()).nonEmpty || e.writeSet().intersect(tbe.writeSet()).nonEmpty || e == tbe // Remove only tasks depending on written (changed) data.
      if(rem && e != tbe) Out.trace(s"The task\n  $tbe\n collided with\n  $e\n and was removed.")
      rem
    }
  })
}