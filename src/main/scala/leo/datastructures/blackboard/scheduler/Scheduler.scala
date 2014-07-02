package leo.datastructures.blackboard.scheduler

import leo.datastructures.blackboard.Blackboard

import scala.collection.mutable
import scala.collection.mutable.HashSet
import java.util.concurrent.Executors


/**
 * Singleton Scheduler
 */
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
    if (s == null) {
      s = new SchedulerImpl(numberOfThreads)
      s.start()

    }
    return s
  }

  /**
   * Creates a Scheduler for 5 Threads or a get for the singleton,
   * if the scheduler already exists.
   * @return
   */
  def apply() : Scheduler = apply(5)
}


/**
 * Scheduler Interface
 */

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

  protected[scheduler] def start()
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
  private val s : SchedulerRun = new SchedulerRun()
  private val w : Writer = new Writer()

  def signal() : Unit = s.synchronized{pauseFlag = false; s.notifyAll()}

//  def toWork(a : Agent) : Unit = exe.submit(new Runnable {
//    override def run(): Unit = if (a.guard()) a.apply()
//  })

  def killAll() : Unit = s.synchronized{endFlag = true; s.notifyAll()}

  var pauseFlag = true
  var endFlag = false

  def pause() : Unit = s.synchronized(pauseFlag = true); println("Scheduler paused.")

  protected[scheduler] def start() {
    println("Scheduler started.")
    new Thread(s).start()      // Start Scheduler
    new Thread(w).start()      // Start writer
  }

  /**
   * Takes Tasks from the Queue and Executes it.
   */
  private class SchedulerRun extends Runnable {
    override def run(): Unit = while (true) {
      // Check Status TODO Try catch
      this.synchronized {
        if (pauseFlag) {
          // If is paused wait
          println("Scheduler paused.")
          this.wait()
          println("Scheduler is commencing.")
        }
        if (endFlag) return // If is ended quit
      }

      // Blocks until a task is available
      val (a, t) = Blackboard().getTask()

      this.synchronized {
        if (pauseFlag) this.wait() // Check again, if waiting took to long

        // Execute task
        exe.submit(new GenAgent(a, t))
      }
    }
  }

  /**
   * Writes a result back to the blackboard
   */
  private class Writer extends Runnable{
    override def run(): Unit = while(true) {
      val result = ExecTask.get()
      result.newFormula().foreach(Blackboard().addFormula(_))
      result.removeFormula().foreach(Blackboard().removeFormula(_))
      result.updateFormula().foreach{case (oldF,newF) => Blackboard().removeFormula(oldF); Blackboard().addFormula(newF)}
    }
  }

  /**
   * The Generic Agent runs an agent on a task and writes the Result Back, such that it can be updated to the blackboard.
   * @param a - Agent that will be executed
   * @param t - Task on which the agent runs.
   */
  private class GenAgent(a : Agent, t : Task) extends Runnable{
    override def run()  {
      ExecTask.put(a.run(t))
    }
  }

  /**
   * Producer Consumer Implementation for the solutions of the current executing Threads (Agents)
   * and the Task responsible for writing the changes to the Blackboard.
   *
   * // TODO Use of Java Monitors might work with ONE Writer
   */
  private object ExecTask {
    private val results : mutable.Set[Result] = new mutable.HashSet[Result] with mutable.SynchronizedSet[Result]

    def get() : Result = this.synchronized {
      while (true) {
        try {
           if(results.isEmpty) this.wait()
           val r = results.head
           results.remove(r)
           return r
        } catch {
          // If got interrupted exception, restore status and continue
          case e: InterruptedException => Thread.currentThread().interrupt()
           // Any other exception will be rethrown
          case e : Exception => throw e
        }
      }
      null  // Should never be reached
    }

    def put(r : Result) {
      results.add(r)        // Must not be synchronized, but maybe it should
      this.synchronized(this.notifyAll())
    }
  }
}


