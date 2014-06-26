package leo.datastructures.blackboard.scheduler

import scala.collection.mutable
import scala.collection.mutable.HashSet
import java.util.concurrent.Executors

object Scheduler {

  private var s : Scheduler = null

  /**
   * Defines a scheduler with numberOfThreads Threads or
   * a simple get for the singleton.
   *
   * @param numberOfThreads - Number of Threads
   * @return Singleton Scheduler
   */
  def apply(numberOfThreads : Int) : Scheduler = {
    if (s != null) s else new SchedulerImpl(numberOfThreads)
  }

  /**
   * Creates a Scheduler for 5 Threads or a get for the singleton,
   * if the scheduler already exists.
   * @return
   */
  def apply() : Scheduler = apply(5)
}


trait Scheduler {

  /**
   * Terminate all working processes.
   */
  def killAll() : Unit

  /**
   * Tells the Scheduler to continue to Work
   */
  def signal() : Unit

  /**
   * Pauses the execution of the scheduler.
   */
  def pause() : Unit
}





// TODO IF GROWS MOVE TO IMPL PACKAGE
/**
 * <p>
 * Central Object for coordinating the ThreadPool responsible for
 * executing the Agents
 * </p>
 * @author Max Wisniewski
 * @since 5/15/14
 */
protected[scheduler] class SchedulerImpl (numberOfThreads : Int) extends Scheduler {
  import leo.agents._

  protected val exe = Executors.newFixedThreadPool(numberOfThreads)

  /**
   * List of all registered Agents in the System
   */
  protected[scheduler] val allAgents = new HashSet[Agent] with mutable.SynchronizedSet[Agent]

  def signal() = ???

//  def toWork(a : Agent) : Unit = exe.submit(new Runnable {
//    override def run(): Unit = if (a.guard()) a.apply()
//  })

  def killAll() : Unit = ??? // allAgents.foreach(_.cancel())

  def pause() = ???
}


