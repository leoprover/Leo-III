package leo
package datastructures
package blackboard.scheduler

import java.util.concurrent.atomic.AtomicInteger

import leo.datastructures.blackboard._

import scala.collection.immutable.TreeMap
import scala.collection.mutable
import java.util.concurrent.{Executors, Future, RejectedExecutionException, ThreadFactory, TimeUnit}

import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.context.Context
import leo.modules.SZSException


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

  def submitIndependent(r : Runnable) : Future[_]

  def submitIndependentFree(r : Runnable) : Unit

  def numberOfThreads : Int

  /**
    * In comparrisson to [[openTasks]] which will return the
    * amount of tasks currently executed, this will return
    * tasks and filters
    * @return
    */
  def getCurrentWork : Int
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
protected[scheduler] class SchedulerImpl (val numberOfThreads : Int) extends Scheduler {
  import leo.agents._

  private var exe = Executors.newFixedThreadPool(numberOfThreads, MyThreadFactory)
  private val s : SchedulerRun = new SchedulerRun()
  private val w : Writer = new Writer()

  private var sT : Thread = null
  private var sW : Thread = null
  private val workCount : AtomicInteger = new AtomicInteger(0)

  protected val curExec : mutable.Set[Task] = new mutable.HashSet[Task] with mutable.SynchronizedSet[Task]

  protected val freeThreads : mutable.Set[Thread] = new mutable.HashSet[Thread]()

  def openTasks : Int = synchronized(curExec.size)

  override def getCurrentWork: Int = workCount.get

  override def isTerminated : Boolean = endFlag

  override def submitIndependent(r : Runnable) : Future[_] = {
    exe.submit(r)
  }

  override def submitIndependentFree(r : Runnable) : Unit = {
    val t = new Thread(r)
    t.start()
    synchronized(freeThreads.add(t))
  }

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
    exe.shutdown()
    exe.shutdownNow()
    if(!exe.awaitTermination(30, TimeUnit.MILLISECONDS)) {
      MyThreadFactory.killAll()
    }
    synchronized(freeThreads foreach {t => t.interrupt(); t.stop()})
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
    Blackboard().forceCheck()
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
            case e : InterruptedException => Out.trace("Scheduler interrupted. Quiting now."); return
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
              workCount.incrementAndGet()
            }
          }
        }
      } catch {
        case e : InterruptedException => Out.trace("Scheduler interrupted. Quiting now."); return
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

        val dsIT = Blackboard().getDS(result.keys).iterator
        while(dsIT.hasNext){
          val ds = dsIT.next()
          ds.updateResult(result)
        }

        ActiveTracker.incAndGet(s"Start Filter after finished ${task.name}")
        // Data Written, Release Locks before filtering
        LockSet.releaseTask(task) // TODO right position?
        Blackboard().finishTask(task)
        curExec.remove(task)
        agent.taskFinished(task)


        try {
          Blackboard().filterAll { a => // Informing agents of the changes
            a.interest match {
              case Some(xs) if xs.isEmpty || (xs.toSet & result.keys).nonEmpty=>
                ActiveTracker.incAndGet(s"Filter new data\n\t\tin Agent ${a.name}\n\t\tfrom Task ${task.name}")
                exe.submit(new GenFilter(a, result, task))
                workCount.incrementAndGet()
              case _ => ()
            }
          }
        } catch {
          case e : RejectedExecutionException => return
          case _ : Exception => Out.severe("Problem occured while filtering new tasks.")
        }
      }

      ActiveTracker.decAndGet(s"End Filter after finished ${task.name}")
      workCount.decrementAndGet()
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
  private class GenAgent(a : Agent, t : Task) extends Runnable{
    override def run()  { // TODO catch error and move outside or at least recover
      try {
        val res = t.run
        ExecTask.put(res, t, a)
        AgentWork.dec(a)
      } catch {
        case e : InterruptedException => {
          throw e
        }
        case e : SZSException =>
          SZSDataStore.forceStatus(e.status)
          Out.severe(e.getMessage)
          LockSet.releaseTask(t)
          Blackboard().finishTask(t)
          ActiveTracker.decAndGet(s"${a.name} killed with exception.")
          Blackboard().filterAll(_.filter(DoneEvent()))
          Scheduler().killAll()
        case e : Exception =>
          if(e.getMessage != null) leo.Out.severe(e.getMessage) else {leo.Out.severe(s"$e got no message.")}
          if(e.getCause != null) leo.Out.finest(e.getCause.toString) else {leo.Out.severe(s"$e got no cause.")}
          LockSet.releaseTask(t)
          Blackboard().finishTask(t)
          if(ActiveTracker.decAndGet(s"Agent ${a.name} failed to execute. Commencing to shutdown") <= 0){
            Blackboard().forceCheck()
          }
          Blackboard().filterAll(_.filter(DoneEvent()))
          Scheduler().killAll()
      }
    }
  }

  private class GenFilter(a : Agent, r : Result, from : Task) extends Runnable{
    override def run(): Unit = {  // TODO catch error and move outside or at least recover
      // Sync and trigger on last check
      try {
        val ts = a.filter(r)
        Blackboard().submitTasks(a, ts.toSet)
      } catch {
        case e : SZSException =>
//          SZSDataStore.forceStatus(Context())(e.status) TODO comment in after CASC
          Out.severe(e.getMessage)
//          Blackboard().filterAll(_.filter(DoneEvent()))
//          Scheduler().killAll()
        case e : Exception =>
//          Blackboard().filterAll(_.filter(DoneEvent())) // TODO comment in after Casc
          leo.Out.warn(e.getMessage)
          leo.Out.finest(e.getCause.toString)
//          Scheduler().killAll()
      }
      ActiveTracker.decAndGet(s"Done Filtering data \n\t\t from ${from.name}\n\t\tin Agent ${a.name}") // TODO Remeber the filterSize for the given task to force a check only at the end
      workCount.decrementAndGet()
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
    private var results : Map[Int, Seq[(Result,Task, Agent)]] = TreeMap[Int, Seq[(Result,Task, Agent)]]()

    def get() : (Result,Task,Agent) = this.synchronized {
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

    def put(r : Result, t : Task, a : Agent) {
      this.synchronized{
        val s : Seq[(Result,Task,Agent)] = results.get(r.priority).fold(Seq[(Result,Task,Agent)]()){ x => x}
        results = results + (r.priority -> (s :+ ((r,t,a))))        // Must not be synchronized, but maybe it should
        this.notifyAll()
      }
    }
  }

  private object AgentWork {
    protected val agentWork : mutable.Map[Agent, Int] = new mutable.HashMap[Agent, Int]()

    /**
     * Increases the amount of work of an agent by 1.
     *
     * @param a - Agent that executes a task
     * @return the updated number of task of the agent.
     */
    def inc(a : Agent) : Int = synchronized(agentWork.get(a) match {
      case Some(v)  => agentWork.update(a,v+1); return v+1
      case None     => agentWork.put(a,1); return 1
    })

    def dec(a : Agent) : Int = synchronized(agentWork.get(a) match {
      case Some(v)  if v > 2  => agentWork.update(a,v-1); return v-1
      case Some(v)  if v == 1 => agentWork.remove(a); return 0
      case _                  => return 0 // Possibly error, but occurs on regular termination, so no output.
    })

    def executingAgents() : Iterable[Agent] = synchronized(agentWork.keys)

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
    override def getAgent : Agent = null
  }

  private object MyThreadFactory extends ThreadFactory {

    private var threads : scala.collection.mutable.Set[Thread] = scala.collection.mutable.HashSet[Thread]()

    def killAll() : Unit = threads.synchronized {
      threads.foreach{t =>
        t.interrupt()
        try {
          t.stop()
        } catch {
          case _:Throwable => () // TODO FIX. IMPORTANT. DO NOT USE FURTHER ON
        }
        }
    }

    override def newThread(r: Runnable): Thread = {
      val t = Executors.defaultThreadFactory().newThread(r)
      threads.synchronized{threads.add(t)}
      t
    }
  }
}


