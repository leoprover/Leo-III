package leo
package datastructures
package blackboard.scheduler

import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.output.{SZS_Theorem, StatusSZS}

import scala.collection.mutable
import java.util.concurrent.Executors


/**
 * Singleton Scheduler
 */
object Scheduler {

  private[scheduler] var s : Scheduler = null
  private var n : Int = 5

  /**
   * Defines a scheduler with numberOfThreads Threads or
   * a simple get for the singleton.
   *
   * @param numberOfThreads - Number of Threads
   * @return Singleton Scheduler
   */
  def apply(numberOfThreads : Int) : Scheduler = {
    n = numberOfThreads
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
  def apply() : Scheduler = {
    apply(n)
  }

  def working() : Boolean = {
    if (s == null) return false
    // s exists
    s.working()
  }
}


/**
 * Scheduler Interface
 */

trait Scheduler {

  def isTerminated() : Boolean

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

  def working() : Boolean
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

  private var exe = Executors.newFixedThreadPool(numberOfThreads)
  private val s : SchedulerRun = new SchedulerRun()
  private val w : Writer = new Writer()

  private var sT : Thread = null
  private var sW : Thread = null

  protected val curExec : mutable.Set[Task] = new mutable.HashSet[Task] with mutable.SynchronizedSet[Task]

  def working() : Boolean = {
    this.synchronized(
      return w.work || curExec.nonEmpty
    )
  }

  override def isTerminated() : Boolean = endFlag

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
    ExecTask.put(ExitResult,ExitTask)   // For the writer to exit, if he is waiting for a result
    exe.shutdownNow()
    curExec.clear()
    AgentWork.executingAgents() foreach(_.kill())
    AgentWork.clear()
    sT.interrupt()
    s.notifyAll()
    curExec.clear()
//    Scheduler.s = null
  }

  var pauseFlag = true
  var endFlag = false

  def pause() : Unit = {s.synchronized(pauseFlag = true);
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
          this.wait()
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
            if (endFlag) return // Savely exit
            if (pauseFlag) {
              Out.trace("Scheduler paused.")
              this.wait()
              Out.trace("Scheduler is commencing.")
            } // Check again, if waiting took to long


            // Execute task
            if (!exe.isShutdown) exe.submit(new GenAgent(a, t))
          }
        }
      } catch {
        case e : InterruptedException => Out.info("Scheduler interrupted. Quiting now"); return
      }
    }
  }

  /**
   * Writes a result back to the blackboard
   */
  private class Writer extends Runnable{
    var work : Boolean = false

    override def run(): Unit = while(!endFlag) {
      val (result,task) = ExecTask.get()
      if(endFlag) return              // Savely exit
      if(curExec.contains(task)) {
        work = true
        // Update blackboard
        var newF : Set[FormulaStore] = Set()
        var closed : List[(Context,StatusSZS)] = List()

        result.newFormula().foreach { f =>
          val up = f.newOrigin(task.writeSet().union(task.readSet()).toList, task.name)
          val ins = Blackboard().addNewFormula(up)
          if (ins) {
            // Keep track of new Formulas
            newF = newF + up
            //Out.trace(s"[Writer]:\n [$task =>]:\n   Füge Formel $up ein.")
          }
        }
        result.removeFormula().foreach(Blackboard().removeFormula(_))
        result.updateFormula().foreach { case (oF, nF) =>
          Blackboard().removeFormula(oF)
          val up = nF.newOrigin(task.writeSet().union(task.readSet()).toList, task.name)
          val ins = Blackboard().addNewFormula(up)
          if (ins) {
            newF = newF + up // Keep track of new formulas
            //Out.trace(s"[Writer]:\n [$task =>]:\n   Füge Formel $up  ein.")
          }
        }

        result.updateStatus().foreach{ case (c,s) =>
          Blackboard().forceStatus(c)(s)
        }

        // Removing Task from Taskset (Therefor remove locks)
        curExec.remove(task)
        Blackboard().finishTask(task)

        // Notify changes
        // ATM only New and Updated Formulas
        Blackboard().filterAll({a =>
          newF.foreach{ f => a.filter(FormulaEvent(f))  // If the result was new, everyone has to be informed
          }
          result.updateStatus.foreach{case (c,s) => a.filter(StatusEvent(c,s))}
          result.updatedContext().foreach{c => a.filter(ContextEvent(c))}
          //task.writeSet().filter{t => !newF.exists(_.cong(t))}.foreach{f => a.filter(FormulaEvent(f))}
          (task.contextWriteSet() ++ result.updatedContext()).foreach{c => a.filter(ContextEvent(c))}
        })
      }
      work = false
      Blackboard().forceCheck()
    }
  }

  /**
   * The Generic Agent runs an agent on a task and writes the Result Back, such that it can be updated to the blackboard.
   * @param a - Agent that will be executed
   * @param t - Task on which the agent runs.
   */
  private class GenAgent(a : Agent, t : Task) extends Runnable{
    override def run()  {
      AgentWork.inc(a)
      ExecTask.put(a.run(t),t)
      AgentWork.dec(a)
        //Out.trace("Executed :\n   "+t.toString+"\n  Agent: "+a.name)
    }
  }

  /**
   * Producer Consumer Implementation for the solutions of the current executing Threads (Agents)
   * and the Task responsible for writing the changes to the Blackboard.
   *
   * // TODO Use of Java Monitors might work with ONE Writer
   */
  private object ExecTask {
    private val results : mutable.Set[(Result,Task)] = new mutable.HashSet[(Result,Task)] with mutable.SynchronizedSet[(Result,Task)]

    def get() : (Result,Task) = this.synchronized {
      while (true) {
        try {
           while(results.isEmpty) this.wait()
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

    def put(r : Result, t : Task) {
      this.synchronized{
        results.add((r,t))        // Must not be synchronized, but maybe it should
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
  }

  /**
   * Marker for the writer to end itself
   */
  private object ExitResult extends Result {
    override def newFormula(): Set[FormulaStore] = ???
    override def updateFormula(): Map[FormulaStore, FormulaStore] = ???
    override def removeFormula(): Set[FormulaStore] = ???
    override def updatedContext(): Set[Context] = ???
    override def updateStatus(): List[(Context, StatusSZS)] = ???
  }

  /**
   * Empty marker for the Writer to end itself
   */
  private object ExitTask extends Task {
    override def readSet(): Set[FormulaStore] = Set.empty
    override def writeSet(): Set[FormulaStore] = Set.empty
    override def bid(budget : Double) : Double = 1
    override def name: String = ???

    override def pretty: String = ???
  }
}


