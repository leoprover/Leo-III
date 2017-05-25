package leo.datastructures.blackboard

import leo.agents.Task

import scala.collection.immutable.HashSet

/**
 *
 * The Lockset maintains every read/write lock on data in the blackboard.
 *
 * Newly created tasks should be looked upon, if a lock is already existing.
 *
 * @author Max Wisniewski
 * @since 8/27/15
 */
object LockSet {

  private var tasks : Set[Task] = new HashSet[Task]

  /**
   * Inserts all locks of a task to the current lock set.
   *
   * @param t - The new task to be locked.
   */
  def lockTask(t : Task) : Unit = synchronized {
    tasks = tasks + t
  }

  /**
   *
   * Removes all locks of a task of the current lock set.
   *
   * @param t - The finished task.
   */
  def releaseTask(t : Task) : Unit = synchronized {
    tasks = tasks - t
  }

  /**
   *
   * Tests whether a task is executable, i.e. the locks can be
   * assigned.
   *
   * @param t - The task to be executed
   * @return True iff the locks can be given
   */
  def isExecutable(t : Task) : Boolean = {
    tasks forall {t1 =>
      val c =  t1.collide(t)
      !c
    }
  }

  /**
   * Tests whether a task is reading outdated data, i.e. data
   * currently assigned write lock.
   *
   * @param t
   * @return
   */
  def isOutdated(t : Task) : Boolean = {
    tasks exists {t1 =>
      val sharedTypes = t.lockedTypes & t1.lockedTypes
      sharedTypes exists {d =>
        val w1 = t1.writeSet.getOrElse(d, Set.empty[Any])
        val wc = t.writeSet.getOrElse(d, Set.empty[Any])
        val rc = t.readSet.getOrElse(d, Set.empty[Any])

        (w1 & wc).nonEmpty || (w1 & rc).nonEmpty
      }
    }
  }

  def isEmpty : Boolean = tasks.isEmpty

  /**
   * Removes all locks
   */
  def clear() : Unit = {
    tasks = new HashSet[Task]
  }
}
