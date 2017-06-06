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
import leo.modules.interleavingproc.SZSStatus
import leo.modules.output.SZS_Error


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

  /**
    * Returns the current work and ignores
    * all long running processes.
    * @return
    */
  def getActiveWork : Int
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
private[blackboard] class SchedulerImpl (val numberOfThreads : Int, val blackboard : Blackboard) extends Scheduler {
  import leo.agents._
  val scheduler = this

  private var exe = Executors.newFixedThreadPool(numberOfThreads, MyThreadFactory)
  private val s : SchedulerRun = new SchedulerRun()
  private val w : Writer = new Writer()

  private var sT : Thread = null
  private var sW : Thread = null
  private val workCount : AtomicInteger = new AtomicInteger(0)

  protected val curExec : mutable.Set[Task] = new mutable.HashSet[Task] with mutable.SynchronizedSet[Task]

  protected val freeThreads : mutable.Set[Thread] = new mutable.HashSet[Thread]()

  def openTasks : Int = synchronized(curExec.size)

  override def getCurrentWork: Int = workCount.get()
  override def getActiveWork: Int = AgentWork.overallWork

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
    blackboard.filterAll{a => a.filter(DoneEvent)}
    curExec.clear()
    AgentWork.clear()
    ExecTask.put(EmptyDelta,ExitTask, null)   // For the writer to exit, if he is waiting for a result
    sT.interrupt()
    s.notifyAll()
    AgentWork.synchronized(AgentWork.notifyAll())
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
    blackboard.forceCheck()
    curExec.clear()
    AgentWork.executingAgents() foreach(_.kill())
    AgentWork.clear()
  }

  protected[blackboard] def start() {
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
      this.synchronized{while (curExec.size > numberOfThreads) this.wait()}
      val tasks = blackboard.getTask
      tasks foreach {case (a, t) => AgentWork.inc(a, t)}
//      println(tasks)
      try {
        for ((a, t) <- tasks) {
          this.synchronized {
            curExec.add(t)
            if (endFlag) return // Savely exit
            if (pauseFlag) {
              Out.trace("Scheduler paused.")
              this.wait()
              Out.trace("Scheduler is commencing.")
            } // Check again, if waiting took to long


            // Execute task
            if (!exe.isShutdown) {
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
    val MAX_WAITING_TIME : Long = 1000 // IN MIlliseconds
    val workLoad : Double =  0    // Wait till workLoad Processes are available of all possible ones
    val maxThreads = Configuration.THREADCOUNT


    override def run(): Unit = while(!endFlag) {
      val start : Long = System.currentTimeMillis()
      var end = start
      AgentWork.synchronized {

        // Wait for enough work to be finished, or for the timeout MAX_WAITING_TIME to continue
        while ((end - start) < MAX_WAITING_TIME && (AgentWork.overallWork.toDouble / maxThreads > workLoad || curExec.size == 0)) {
          val rest = Math.max(MAX_WAITING_TIME - end + start, 0)
          val millis1 = System.currentTimeMillis()
          AgentWork.wait(rest)
          if (endFlag) return
          end = System.currentTimeMillis()
        }

        // If the timeout MAX_WAITING_TIME was fired, but all process are blocked, wait till the first one finishes.
        if(AgentWork.overallWork >= maxThreads){
          AgentWork.wait()
        }

        if(AgentWork.overallWork > 0){
          AgentWork.ignoreRest() // Used to not block in the next iteration over the remaining tasks
          blackboard.forceCheck() // To not block in case of no other finished task
        }
      }
      val (result,task, agent) = ExecTask.get()
      val millis1 = System.currentTimeMillis()
//      println(s"+++++++++++ Result start [${if(agent != null) agent.name else "Noname" }] writing: ${(millis1 / 60000) % 60} min ${(millis1 / 1000)%60} s ${millis1 % 1000} ms")
      if(endFlag) return              // Savely exit
      var doneSmth = false
//      println(result)
      if(
        task match {
          case ct : CompressTask => ct.tasks.exists(t => curExec.contains(t))
          case _ => curExec.contains(task)
        }
        )
      {
        work = true

        // Construct Delta of the data really inserted into the data structures
        var delta : Delta = EmptyDelta
        val dsIT = blackboard.getDS(result.types.toSet).iterator
        while(dsIT.hasNext){
          val ds = dsIT.next()
          val realUpdate = ds.updateResult(result)
          delta = delta.merge(realUpdate) // TODO if there is no ds for a datatype? Blackboard level solution?
        }

        ActiveTracker.incAndGet(s"Start Filter after finished ${task.name}")
        // Data Written, Release Locks before filtering
        task match {
          case ct : CompressTask => ct.tasks.foreach{t =>
            LockSet.releaseTask(t) // TODO right position?
            blackboard.finishTask(t)
            curExec.remove(t)
            agent.taskFinished(t)
          }
          case _ =>
            LockSet.releaseTask(task) // TODO right position?
            blackboard.finishTask(task)
            curExec.remove(task)
            agent.taskFinished(task)
        }

//        val millis = System.currentTimeMillis()
//        println(s"+++++++++++ Result end [${agent.name}] writing: ${(millis / 60000) % 60} min ${(millis / 1000)%60} s ${millis % 1000} ms")


        try {
          blackboard.filterAll { a => // Informing agents of the changes
            a.interest match {
              case Some(xs) if xs.isEmpty || (xs.toSet & delta.types.toSet).nonEmpty=>
                ActiveTracker.incAndGet(s"Filter new data\n\t\tin Agent ${a.name}\n\t\tfrom Task ${task.name}")
                exe.submit(new GenFilter(a, delta, task))
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
      task match {
        case ct : CompressTask => ct.tasks.foreach(_ => workCount.decrementAndGet())
        case _ =>  workCount.decrementAndGet()
      }
      scheduler.signal()  // Get new task
      work = false
      blackboard.forceCheck()
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
//        println(s"--- ${a.name} ---\n  Start : ${t.pretty}\n-------")
        val res = t.run
        ExecTask.put(res.immutable, t, a)

//        println(s"--- ${a.name} ---\n  Done : ${t.pretty}\n-------")
      } catch {
        case e : InterruptedException => {
          throw e
        }
        case e : SZSException =>
          SZSDataStore.forceStatus(e.status)
          Out.severe(e.getMessage)
          LockSet.releaseTask(t)
          blackboard.finishTask(t)
          ActiveTracker.decAndGet(s"${a.name} killed with exception.")
          blackboard.filterAll(_.filter(DoneEvent))
          scheduler.killAll()
        case e : Exception =>
          if(e.getMessage != null) leo.Out.severe(e.getMessage) else {leo.Out.severe(s"$e got no message.")}
          if(e.getCause != null) leo.Out.finest(e.getCause.toString) else {leo.Out.severe(s"$e got no cause.")}
          e.printStackTrace()
          LockSet.releaseTask(t)
          blackboard.finishTask(t)
          if(ActiveTracker.decAndGet(s"Agent ${a.name} failed to execute. Commencing to shutdown") <= 0){
            blackboard.forceCheck()
          }
          blackboard.filterAll(_.filter(DoneEvent))
          scheduler.killAll()
      }
      AgentWork.synchronized {
        AgentWork.dec(a, t)
        AgentWork.notifyAll() // Signals the writer over one finished Task
      }
    }
  }

  private class GenFilter(a : Agent, r : Delta, from : Task) extends Runnable{
    override def run(): Unit = {  // TODO catch error and move outside or at least recover
      // Sync and trigger on last check
      try {
        val ts = a.filter(r)
        blackboard.submitTasks(a, ts.toSet)
      } catch {
        case e : SZSException =>
          blackboard.addData(SZSStatus)(e.status)
          Out.severe(e.getMessage)
          scheduler.killAll()
        case e : Exception =>
          blackboard.addData(SZSStatus)(SZS_Error)
          leo.Out.warn(e.getMessage)
          leo.Out.finest(e.getCause.toString)
          scheduler.killAll()
      }
      ActiveTracker.decAndGet(s"Done Filtering data \n\t\t from ${from.name}\n\t\tin Agent ${a.name}") // TODO Remeber the filterSize for the given task to force a check only at the end
      workCount.decrementAndGet()
      blackboard.forceCheck()

      //Release sync
    }
  }

  /**
   * Producer Consumer Implementation for the solutions of the current executing Threads (Agents)
   * and the Task responsible for writing the changes to the Blackboard.
   *
   */
  private object ExecTask {
    private var results : Seq[(Delta,Task, Agent)] = Seq[(Delta,Task, Agent)]()
    private lazy val threshHold = 3
    private lazy val maxMerge = 10

    def get() : (Delta,Task,Agent) = this.synchronized {
      while (true) {
        try {
           while(results.isEmpty) {this.wait()}
           compress()
           val r = results.head
           results = results.tail
//           println(s"-----------\n  Retriving ${r} for filtering.\n--------------")
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

    /**
      * This method compresses the sequence of Deltas into a single
      * Delta. Containing all changes of each delta.
      */
    private def compress() : Unit = {
      // 1. When to compress?
//      println("Before compress: "+results)
      if(results.isEmpty || results.size < threshHold) return
      // 2a. What to Compress
      // TODO Only take maxMerge tasks

      // 2b. Generate Alibi Agent (meta work pass through later.)
      val ca = new CompressAgent(results.map{case (d,t,a) => (t,a)})
      val ct = new CompressTask(results.map(_._2), ca)

      // 3. Calculate compressed Delta

      val it = results.iterator
      var cd : Delta = EmptyDelta
      while(it.hasNext){
        val (d, _ , _) = it.next()
        cd = cd.merge(d)
      }

      // 4. Update list internal list
//      println("Compressed Result" + cd)
      results = Seq((cd, ct, ca))
    }

    def put(r : Delta, t : Task, a : Agent) {
      this.synchronized{
        results = (r,t,a) +: results
        this.notifyAll()
      }
    }
  }

  private class CompressAgent(orignalTaskAgent : Seq[(Task, Agent)]) extends Agent {
    // TODO Return real errors? Leave not implemented?
    override val name: String = s"Compressed Agent (${orignalTaskAgent.map(_._2.name).mkString(",  ")})"
    override def kill(): Unit = orignalTaskAgent.foreach(_._2.kill())
    override def interest: Option[Seq[DataType[Any]]] = None
    override def filter(event: Event): Iterable[Task] = Nil
    override def init(): Iterable[Task] = Nil
    override def maxMoney: Double = Double.MaxValue
    override def taskFinished(t: Task): Unit = t match {
      case ct : CompressTask => ct.tasks.foreach{t1 => t1.getAgent.taskFinished(t1)}
      case _ =>
    }
    override def taskChoosen(t: Task): Unit = ???   // After compression, no task can be choosen or canceled.
    override def taskCanceled(t: Task): Unit = ???
  }

  private class CompressTask(val tasks : Seq[Task], a : CompressAgent) extends Task {
    override val name: String = s"Compressed Task (${tasks.map(_.name).mkString(",  ")})"
    override lazy val run: Delta = EmptyDelta
    override val readSet : Map[DataType[Any], Set[Any]] = {
      if(tasks.isEmpty) {Map()}
      else {
        var r = Map[DataType[Any], Set[Any]]()
        val it = tasks.iterator
        while(it.hasNext){
          val t = it.next()
          val mit = t.readSet.iterator
          while(mit.hasNext){
            val (ty, set) = mit.next()
            r = r + (ty -> set.union(r.getOrElse(ty, Set[Any]())))
          }
        }
        r
      }
    }
    override val writeSet : Map[DataType[Any], Set[Any]] = {
      if(tasks.isEmpty) {Map()}
      else {
        var r = Map[DataType[Any], Set[Any]]()
        val it = tasks.iterator
        while(it.hasNext){
          val t = it.next()
          val mit = t.writeSet.iterator
          while(mit.hasNext){
            val (ty, set) = mit.next()
            r = r + (ty -> set.union(r.getOrElse(ty, Set[Any]())))
          }
        }
        r
      }
    }
    override def bid: Double = 1.0
    override def getAgent: Agent = a
    override lazy val pretty: String = s"Compressed Task (${tasks.map(_.pretty).mkString(",  ")})"
  }

  private object AgentWork {
    protected val agentWork : mutable.Map[Agent, Int] = new mutable.HashMap[Agent, Int]()
    protected val tasks : mutable.HashSet[Task] = new mutable.HashSet[Task]()

    protected var work : Int = 0

    /**
     * Increases the amount of work of an agent by 1.
     *
     * @param a - Agent that executes a task
     * @return the updated number of task of the agent.
     */
    def inc(a : Agent, t : Task) : Int = synchronized{
      work += 1
      tasks.add(t)
      agentWork.get(a) match {
        case Some(v)  => agentWork.update(a,v+1); return v+1
        case None     => agentWork.put(a,1);   return 1
      }
    }

    def dec(a : Agent, t : Task) : Int = synchronized {
        val act = tasks.contains(t)
        if(act) {
          work -= 1
          tasks.remove(t)
        }
        agentWork.get(a) match {
          case Some(v) if v > 1 => agentWork.update(a, v - 1); if(act) return v - 1 else return v
          case Some(v) if v == 1 => agentWork.remove(a); if(act) return 0 else return 1
          case _ => return 0 // Possibly error, but occurs on regular termination, so no output.
        }
    }

    /**
      * This method is used to ignore long lasting work
      * for the waiting in the global loop
      */
    def ignoreRest() : Unit = {
      work = 0
      tasks.clear()
    }

    def executingAgents() : Iterable[Agent] = synchronized(agentWork.keys)

    def clear() = synchronized(agentWork.clear())

    def isEmpty : Boolean = synchronized(agentWork.isEmpty)

    def nonEmpty : Boolean = synchronized(agentWork.nonEmpty)

    def overallWork : Int = synchronized(work)
  }

  /**
   * Empty marker for the Writer to end itself
   */
  private object ExitTask extends Task {
    override def readSet(): Map[DataType[Any], Set[Any]] = Map.empty
    override def writeSet(): Map[DataType[Any], Set[Any]] = Map.empty
    override def bid : Double = 1
    override def name: String = "ExitTask"

    override def run : Delta = {
      EmptyDelta
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
          case _:Throwable => ()
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


