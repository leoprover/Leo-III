package leo.datastructures.blackboard.scheduler

import scala.collection.mutable
import scala.collection.mutable.HashSet
import java.util.concurrent.Executors

/**
 * exi<p>
 * Central Object for coordinating the ThreadPool responsible for
 * executing the Agents
 * </p>
 * @author Max Wisniewski
 * @since 5/15/14
 */
class Scheduler (numberOfThreads : Int){
  import leo.agents._

  protected val exe = Executors.newFixedThreadPool(numberOfThreads)

  /**
   * List of all registered Agents in the System
   */
  protected[scheduler] val allAgents = new HashSet[Agent] with mutable.SynchronizedSet[Agent]

  def toWork(a : Agent) : Unit = exe.submit(new Runnable {
    override def run(): Unit = if (a.guard()) a.apply()
  })

  def killAll() : Unit = allAgents.foreach(_.cancel())
}
