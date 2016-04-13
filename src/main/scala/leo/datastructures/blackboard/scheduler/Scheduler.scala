package leo
package datastructures
package blackboard.scheduler

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.blackboard._

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import java.util.concurrent.{RejectedExecutionException, Executors}


/**
 * Singleton Scheduler
 */
object Scheduler {

  private[scheduler] lazy val s : Scheduler = {val s = new SchedulerImpl(n); s.start(); s}
  private lazy val n : Int = try {Configuration.THREADCOUNT} catch { case _ : Exception => Configuration.DEFAULT_THREADCOUNT}

  /**
   * Creates a Scheduler for 5 Threads or a get for the singleton,
   * if the scheduler already exists.
    *
    * @return
   */
  def apply() : Scheduler = {
    s
  }
}


/**
 * Scheduler Interface
 */

trait Scheduler {

  def isTerminated : Boolean

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

  /**
   * Performs one round of auction
   */
  def step() : Unit

  def clear() : Unit

  protected[scheduler] def start()

  def openTasks : Int
}





// TODO IF GROWS MOVE TO IMPL PACKAGE
/**
 * <p>
 * Central Object for coordinating the ThreadPool responsible for
 * executing the Agents
 * </p>
  *
  * @author Max Wisniewski
 * @since 5/15/14
 */
protected[scheduler] class SchedulerImpl (numberOfThreads : Int) extends Scheduler {
  import leo.agents._

  private var exe = Executors.newFixedThreadPool(numberOfThreads)
  private val s : SchedulerRun = new SchedulerRun()
  private val w : Writer = new Writer()

  private var sT : Thread = null
  private var sW : Thread = null

  protected val curExec : mutable.Set[Task] = new mutable.HashSet[Task] with mutable.SynchronizedSet[Task]

  def openTasks : Int = synchronized(curExec.size)

  override def isTerminated : Boolean = endFlag

  def signal() : Unit = s.synchronized{
    pauseFlag = false
    s.notifyAll()
  }

  def step() : Unit = s.synchronized{s.notifyAll()}

//  def toWork(a : Agent) : Unit = exe.submit(new Runnable {
//    override def run(): Unit = if (a.guard()) a.apply()
//  })

  def killAll() : Unit = s.synchronized{
    endFlag = true
    pauseFlag = false
    exe.shutdownNow()
    AgentWork.executingAgents() foreach(_.kill())
    Blackboard().filterAll(a => a.filter(DoneEvent()))
    curExec.clear()
    AgentWork.clear()
    ExecTask.put(ExitResult,ExitTask, null)   // For the writer to exit, if he is waiting for a result
    sT.interrupt()
    s.notifyAll()
    curExec.clear()
//    Scheduler.s = null
  }

  var pauseFlag = true
  var endFlag = false

  def pause() : Unit = {s.synchronized(pauseFlag = true)
//    println("Scheduler paused.")
  }

  def clear() : Unit = {
    pause()
    curExec.clear()
    AgentWork.executingAgents() foreach(_.kill())
    AgentWork.clear()
  }

  protected[scheduler] def start() {
//    println("Scheduler started.")
    sT = new Thread(s)
    sT.start()      // Start Scheduler
    sW = new Thread(w)
    sW.start()      // Start writer
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
          Out.trace("Scheduler paused.")
          try {
            this.wait()
          } catch {
            case e : InterruptedException => Out.info("Scheduler interrupted. Quiting now."); return
          }
          Out.trace("Scheduler is commencing.")
        }
        if (endFlag) return // If is ended quit
      }

      // Blocks until a task is available
      val tasks = Blackboard().getTask

      try {
        for ((a, t) <- tasks) {
          this.synchronized {
            curExec.add(t)
            while (curExec.size > numberOfThreads) this.wait()
            if (endFlag) return // Savely exit
            if (pauseFlag) {
              Out.trace("Scheduler paused.")
              this.wait()
              Out.trace("Scheduler is commencing.")
            } // Check again, if waiting took to long


            // Execute task
            if (!exe.isShutdown) {
              AgentWork.inc(a)
              exe.submit(new GenAgent(a, t))
            }
          }
        }
      } catch {
        case e : InterruptedException => Out.info("Scheduler interrupted. Quiting now."); return
      }
    }
  }

  /**
   * Writes a result back to the blackboard
   */
  private class Writer extends Runnable{
    var work : Boolean = false

    override def run(): Unit = while(!endFlag) {
      val (result,task, agent) = ExecTask.get()
      if(endFlag) return              // Savely exit
      var doneSmth = false
      if(curExec.contains(task)) {
        work = true


//        Out.comment("[Writer] : Got task and begin to work.")
        // Update blackboard
        val newD : Map[DataType, Seq[Any]] = result.keys.map {t =>
          (t,result.inserts(t).filter{d =>            //TODO should not be lazy, Otherwise it could generate problems
            var add : Boolean = false
            Blackboard().getDS(t).foreach{ds => add |= ds.insert(d)}  // More elegant without risk of lazy skipping of updating ds?
            doneSmth |= add
            add
          })
        }.toMap

        val updateD : Map[DataType, Seq[Any]] = result.keys.map {t =>
          (t,result.updates(t).filter{case (d1,d2) =>            //TODO should not be lazy, Otherwise it could generate problems
            var add : Boolean = false

            Blackboard().getDS(t).foreach{ds => add |= ds.update(d1,d2)}  // More elegant without risk of lazy skipping of updating ds?
            doneSmth |= add
            add
          }.map(_._2))    // Only catch the new ones
        }.toMap

        result.keys.foreach {t =>
          (t,result.removes(t).foreach{d =>
            Blackboard().getDS(t).foreach{ds =>ds.delete(d)}
          })
        }


        // Data Written, Release Locks before filtering
        LockSet.releaseTask(task) // TODO right position?
        Blackboard().finishTask(task)

        try {
          Blackboard().filterAll { a => // Informing agents of the changes
            a.interest match {
              case None => ()
              case Some(xs) =>
                val ts = if (xs.isEmpty) result.keys else xs
                ts.foreach { t =>
                  // Queuing for filtering on the existing threads
                  newD.getOrElse(t, Nil).foreach { d => ActiveTracker.incAndGet(s"Filter new data ($d)\n\t\tin Agent ${a.name}" ); exe.submit(new GenFilter(a, t, d, task)) } //a.filter(DataEvent(d,t))}
                  updateD.getOrElse(t, Nil).foreach { d => ActiveTracker.incAndGet(s"Filter update to ($d)\n\t\tin Agent ${a.name}"); exe.submit(new GenFilter(a, t, d, task)) } //a.filter(DataEvent(d,t))}
                  // TODO look at not written data,,,
                }
            }
          }
        } catch {
          case e : RejectedExecutionException => return
          case _ : Exception => Out.severe("Problem occured while filtering new tasks.")
        }
      }
//      Out.comment(s"[Writer]: Gone through all.")

      curExec.remove(task)
      agent.taskFinished(task)

      if(ActiveTracker.decAndGet(s"Finished Task : ${task.pretty}") <= 0) Blackboard().forceCheck()
      Scheduler().signal()  // Get new task
      work = false
      Blackboard().forceCheck()
    }
  }

  /**
   * The Generic Agent runs an agent on a task and writes the Result Back, such that it can be updated to the blackboard.
    *
    * @param a - Agent that will be executed
   * @param t - Task on which the agent runs.
   */
  private class GenAgent(a : TAgent, t : Task) extends Runnable{
    override def run()  { // TODO catch error and move outside or at least recover
      try {
        ExecTask.put(t.run, t, a)
        AgentWork.dec(a)
      } catch {
        case e : Exception =>
          leo.Out.severe(e.getMessage)
          //leo.Out.finest(e.getCause.toString)
          if(ActiveTracker.decAndGet(s"Agent ${a.name} failed to execute. Commencing to shutdown") <= 0){
            Blackboard().forceCheck()
          }
          Scheduler().killAll()
      }
    }
  }

  private class GenFilter(a : TAgent, t : DataType, newD : Any, from : Task) extends Runnable{
    override def run(): Unit = {  // TODO catch error and move outside or at least recover
      // Sync and trigger on last check
      try {
        val ts = a.filter(DataEvent(newD, t))
        Blackboard().submitTasks(a, ts.toSet)
      } catch {
        case e : Exception =>
          leo.Out.warn(e.getMessage)
          leo.Out.finest(e.getCause.toString)
      }
      ActiveTracker.decAndGet(s"Done Filtering data (${newD})\n\t\tin Agent ${a.name}") // TODO Remeber the filterSize for the given task to force a check only at the end
      Blackboard().forceCheck()

      //Release sync
    }
  }

  /**
   * Producer Consumer Implementation for the solutions of the current executing Threads (Agents)
   * and the Task responsible for writing the changes to the Blackboard.
   *
   * // TODO Use of Java Monitors might work with ONE Writer
   */
  private object ExecTask {
    private var results : Map[Int, Seq[(Result,Task, TAgent)]] = TreeMap[Int, Seq[(Result,Task, TAgent)]]()

    def get() : (Result,Task,TAgent) = this.synchronized {
      while (true) {
        try {
           while(results.keys.isEmpty) {this.wait()}
           val k = results.keys.head
           val s = results.get(k).get
           val r = s.head
           if(s.size > 1) results = results + (k -> s.tail) else results = results - k
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

    def put(r : Result, t : Task, a : TAgent) {
      this.synchronized{
        val s : Seq[(Result,Task,TAgent)] = results.get(r.priority).fold(Seq[(Result,Task,TAgent)]()){x => x}
        results = results + (r.priority -> (s :+ ((r,t,a))))        // Must not be synchronized, but maybe it should
        this.notifyAll()
      }
    }
  }

  private object AgentWork {
    protected val agentWork : mutable.Map[TAgent, Int] = new mutable.HashMap[TAgent, Int]()

    /**
     * Increases the amount of work of an agent by 1.
     *
     * @param a - Agent that executes a task
     * @return the updated number of task of the agent.
     */
    def inc(a : TAgent) : Int = synchronized(agentWork.get(a) match {
      case Some(v)  => agentWork.update(a,v+1); return v+1
      case None     => agentWork.put(a,1); return 1
    })

    def dec(a : TAgent) : Int = synchronized(agentWork.get(a) match {
      case Some(v)  if v > 2  => agentWork.update(a,v-1); return v-1
      case Some(v)  if v == 1 => agentWork.remove(a); return 0
      case _                  => return 0 // Possibly error, but occurs on regular termination, so no output.
    })

    def executingAgents() : Iterable[TAgent] = synchronized(agentWork.keys)

    def clear() = synchronized(agentWork.clear())

    def isEmpty : Boolean = synchronized(agentWork.isEmpty)

    def nonEmpty : Boolean = synchronized(agentWork.nonEmpty)
  }

  /**
   * Marker for the writer to end itself
   */
  private object ExitResult extends Result {}

  /**
   * Empty marker for the Writer to end itself
   */
  private object ExitTask extends Task {
    override def readSet(): Map[DataType, Set[Any]] = Map.empty
    override def writeSet(): Map[DataType, Set[Any]] = Map.empty
    override def bid : Double = 1
    override def name: String = "ExitTask"

    override def run : Result = {
      Result()
    }

    override def pretty: String = "Exit Task"
    override def getAgent : TAgent = null
  }
}


