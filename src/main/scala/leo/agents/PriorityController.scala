package leo.agents

import leo._
import leo.datastructures.blackboard.{Event, Blackboard}

import scala.collection.mutable

/**
 *
 * Implements the selection and storing of the generated Tasks.
 *
 * Only the explicit fitler and the run method have to be implemented.
 *
 *
 * The tasks are executed sorted by their bid starting with the highest bid.
 */
class PriorityController(a : Agent) extends AgentController(a) {

  override def setActive(a : Boolean) = {
    super.setActive(a)
    if(a && q.nonEmpty) Blackboard().signalTask()
  }

  override def unregister(): Unit = {
    super.unregister()
    synchronized{q.clear()}
  }

  // Sort by a fixed amount of money
  protected var q : mutable.PriorityQueue[Task] = new mutable.PriorityQueue[Task]()(Ordering.by{(x : Task) => x.bid(100)})

  override def hasTasks : Boolean = q.synchronized(q.nonEmpty)

  override def openTasks : Int = synchronized(q.size)

  /**
   * Calls the internal toFilter method and inserts all generated tasks to the priority queue.
   *
   * @param f - Raised Event.
   */
  override def filter(f: Event) : Unit = {
    var done = false
    val it = a.toFilter(f).iterator
    while(it.hasNext) {
      val t = it.next()
      if (!Blackboard().collision(t)) {
        synchronized {
          q.enqueue (t)
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
        // TODO Change to iterator since for is inefficient
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
  override def clearTasks(): Unit = q.synchronized {q.clear()}

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = synchronized(q.iterator.toIterable)

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = {
    synchronized {
      q = q.filter { tbe =>
        nExec.forall{e =>
          val take = e.writeSet().intersect(tbe.writeSet()).isEmpty && e.writeSet().intersect(tbe.writeSet()).isEmpty && e != tbe
          if(!take && e != tbe) Out.trace(s"The task\n  $tbe\n collided with\n  $e\n and was therefore removed.")
          take
        }
      }
    }
  }

}