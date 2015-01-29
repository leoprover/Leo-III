package leo.datastructures.blackboard.impl


import leo.agents.{Task, Agent}
import leo.datastructures.LitFalse
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.impl.TreeContextSet
import leo.datastructures.term.Term
import leo.datastructures.{Role, Clause}
import leo.modules.output.StatusSZS
import leo.modules.proofCalculi.TrivRule
import scala.collection.concurrent.TrieMap
import leo.datastructures.blackboard._
import scala.collection.mutable
import scala.collection.mutable.{Queue, Map => MMap}
import leo.datastructures.context.{ContextSet, Context}

/**
 * This blackboard is a first reference implementation for the @see{Blackboard} interface.
 *
 * It utilizes no doubly added formulas and an auction implementation to access and organize tasks.
 *
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @since 19.08.2015
 */
protected[blackboard] class AuctionBlackboard extends Blackboard {

  var isFinished = false

  // For each agent a List of Tasks to execute
  // If FormulaSet is optimized we can optimize internal

  override def getFormulas: Iterable[FormulaStore] = FormulaSet.getAll()

  override def getAll(p: FormulaStore => Boolean): Iterable[FormulaStore] = FormulaSet.getAll().filter(p)

  override def getFormulaByName(name: String): Option[FormulaStore] = FormulaSet.getName(name)

  // Called from outside, therefor we will fitler explicitly
  override def addFormula(name : String, formula: Clause, role : Role, context : Context) : FormulaStore = {
    val s = Store(name, formula, role, context)
    val f = addFormula(s)
    filterAll(_.filter(FormulaEvent(f)))
    return f
  }

  override def addFormula(formula : FormulaStore) : FormulaStore = {
    val f = FormulaSet.add(formula)
    // TODO: handle merge
    f
  }

  override def addNewFormula(formula : FormulaStore) : Boolean = {
    // TODO: Implement Sets to check containment of Clauses.
    if(TrivRule.teqt(formula.clause)) return false
    if (FormulaSet.getAll(formula.context).exists(_.cong(formula)))
      return false
    else {
      FormulaSet.add(formula)
      return true
    }
  }

  override def removeFormula(formula: FormulaStore): Boolean = FormulaSet.rm(formula)

  override def rmFormulaByName(name: String): Boolean = FormulaSet.rmName(name)

  override def rmAll(p: FormulaStore => Boolean) = FormulaSet.getAll().foreach{f => if(p(f)) FormulaSet.rm(f)}

  /**
   * Register a new Handler for Formula adding Handlers.
   * @param a - The Handler that is to register
   */
  override def registerAgent(a : Agent) : Unit = {
    TaskSet.addAgent(a)
    freshAgent(a)
  }

  override def unregisterAgent(a: Agent): Unit = {
    TaskSet.removeAgent(a)
  }

  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask: Iterable[(Agent,Task)] = TaskSet.getTask

  override def clear() : Unit = {
    rmAll(_ => true)
    TaskSet.clear()
    isFinished = false
  }

  /**
   * Gives all agents the chance to react to an event
   * and adds the generated tasks.
   *
   * @param t - Function that generates for each agent a set of tasks.
   */
  override def filterAll(t: (Agent) => Unit): Unit = {
    TaskSet.agents.foreach{ a =>
      t(a)
    }
  }


  /**
   * Removes a task from the list of currently executed tasks.
   *
   * @param t - The finished task.
   */
  override def finishTask(t : Task) : Unit = TaskSet.finishTask(t)

  override def getRunningTasks() : Iterable[Task] = TaskSet.execTasks.toList

  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  override protected[blackboard] def freshAgent(a: Agent): Unit = {
    // ATM only formulas trigger events
    getFormulas.foreach{fS => a.filter(FormulaEvent(fS))}
    forceCheck()
  }

  override def signalTask() : Unit = TaskSet.signalTask()

  override def collision(t : Task) : Boolean = TaskSet.collision(t)

  /**
   *
   * @return all registered agents
   */
  override def getAgents(): Iterable[(Agent,Double)] = TaskSet.regAgents.toSeq

  /**
   * Sends a message to an agent.
   *
   * TODO: Implement without loss of tasks through messages
   *
   * @param m    - The message to send
   * @param to   - The recipient
   */
  override def send(m: Message, to: Agent): Unit = to.filter(m)

  /**
   *
   * <p>
   * Retrieves all formulas in a given context.
   * </p>
   *
   * @param c - A given Context
   * @return All formulas in the context `c`
   */
  override def getFormulas(c: Context): Iterable[FormulaStore] = FormulaSet.getAll(c)

  /**
   * <p>
   * Removes all formulas in the context `c` satisfiying `p`.
   * </p>
   * @param c - A given Context
   * @param p - Predicate the formulas have to satisfy
   * @return Removes all formulas in `c` satisfying `p`
   */
  override def rmAll(c: Context)(p: FormulaStore): Unit = FormulaSet.getAll(c) foreach (FormulaSet.rm(_))

  /**
   * <p>
   * Filters the formulas of a given context.
   * </p>
   *
   * @param c - A given Context
   * @param p Predicate the formulas have to satisfy
   * @return All formulas in `c` satisfying `p`
   */
  override def getAll(c: Context)(p: (FormulaStore) => Boolean): Iterable[FormulaStore] = FormulaSet.getAll(c).filter(p)

  private val szsSet : mutable.Map[Context, StatusSZS] = new mutable.HashMap[Context, StatusSZS]()

  /**
   * Updates the SZS-Status in a given context, if it is
   * not yet known.
   *
   * @param c - The context to update
   * @param s - The new status
   * @return true, if the status was defined yet.
   */
  override def updateStatus(c: Context)(s: StatusSZS): Boolean = szsSet.synchronized{szsSet.get(c) match {
    case Some(a) => return false
    case None => szsSet.put(c,s); return true
  }}

  /**
   * Forces the SZS Status in a context to a new one.
   * Does not fail, if a status is already set.
   *
   * @param c - The context to update
   * @param s - The status to set
   */
  override def forceStatus(c: Context)(s: StatusSZS): Unit = szsSet.synchronized(szsSet.put(c,s))

  /**
   * Returns to a given context the set status.
   * None if no status was previously set.
   *
   * @param c - The searched context
   * @return Some(status) if set, else None.
   */
  override def getStatus(c: Context): Option[StatusSZS] = szsSet.synchronized(szsSet.get(c))

  /**
   * Allows a force check for new Tasks. Necessary for the DoneEvent to be
   * thrown correctly.
   */
  override protected[blackboard] def forceCheck(): Unit = TaskSet.synchronized(TaskSet.notifyAll())
}


/**
 * Stores the formulas in first in a map from name to the @see{FormulaStore}
 * and secondly a map from @see{Term} to FormulaStore, to see, if a formula has already bee added.
 *
 */
private object FormulaSet {

  private val formulaSet : ContextSet[FormulaStore] = new TreeContextSet[FormulaStore]()

  /**
   * Looks up the termMap, for an already existing store and returns this or the given store
   * after adding it.
   *
   * @return the existing store or the new one
   */
  def add(f : FormulaStore) : FormulaStore = {
    (formulaSet get (f,f.context)) match {
      case Some(f1) =>
        return f1
      case None =>
        formulaSet.add(f, f.context)
        return f
    }
  }

  /**
   * ATM no filter support added (no optimization).
   * Therefor we can savely return everything and filter later.
   *
   * @return All stored formulas
   */
  def getAll() : Iterable[FormulaStore] = formulaSet.getAll

  def getAll(c : Context) : Iterable[FormulaStore] = formulaSet.getAll(c)

  def rm(f : FormulaStore) : Boolean = {
    formulaSet.remove(f, f.context)
  }

  def rmName(n : String) : Boolean = {
    formulaSet.getAll.find {f => f.name == n} match {
      case None => false
      case Some(f) => formulaSet.remove(f, f.context)
    }
  }

  def getName(n : String) : Option[FormulaStore] = formulaSet.getAll.find {f => f.name == n}
}

private object TaskSet {
  import scala.collection.mutable.HashSet

  var regAgents = mutable.HashMap[Agent,Double]()
  protected[impl] val execTasks = new mutable.HashSet[Task]

  private val AGENT_SALARY : Double = 5

  /**
   * Notifies process waiting in 'getTask', that there is a new task available.
   */
  protected[blackboard] def signalTask() : Unit = this.synchronized(this.notifyAll())

  def clear() : Unit = {
    regAgents.foreach(_._1.clearTasks())
    execTasks.clear()
  }

  def addAgent(a : Agent) {
    this.synchronized(regAgents.put(a,AGENT_SALARY))
  }

  def removeAgent(a : Agent): Unit = {
    this.synchronized(regAgents.remove(a))
  }

  private[impl] def finishTask(t : Task) = synchronized(execTasks.remove(t))

  def agents : List[Agent] = this.synchronized(regAgents.toList.map(_._1))


  /**
   * Gets from any active agent the set of tasks, he wants to execute with his current budget.
   *
   * If the set of tasks is empty he waits until something is added
   * (filter should call signalTask).
   *
   * Of this set we play
   *
   * @return
   */
  def getTask : Iterable[(Agent,Task)] = this.synchronized{

    while(!Scheduler().isTerminated()) {
      try {

//        println("Beginning to get items for the auction.")

        //
        // 1. Get all Tasks the Agents want to bid on during the auction with their current money
        //
        var r: List[(Double, Agent, Task)] = Nil
        while (r.isEmpty) {
          regAgents.foreach { case (a, budget) => if (a.isActive) a.getTasks(budget).foreach { t => r = (t.bid(budget), a, t) :: r}}
          if (r.isEmpty) {
            if(!Scheduler.working()) Blackboard().filterAll{a => a.filter(DoneEvent())}
            TaskSet.wait()
          }
        }

//        println("Got tasks and ready to auction.")
        //
        // 2. Bring the Items in Order (sqrt (m) - Approximate Combinatorical Auction, with m - amount of colliding writes).
        //
        // Sort them by their value (Approximate best Solution by : (value) / (sqrt |WriteSet|)).
        // Value should be positive, s.t. we can square the values without changing order
        //
        val queue: List[(Double, Agent, Task)] = r.sortBy { case (b, a, t) => b * b / t.writeSet().size}

//        println("Sorted tasks.")

        // 3. Take from beginning to front only the non colliding tasks
        // Check the currenlty executing tasks too.
        var newTask: List[(Agent, Task)] = Nil
        for ((price, a, t) <- queue) {
          if (!newTask.exists { e => t.collide(e._2)} && !collision(t)) {
            val budget = regAgents.apply(a)
            if (budget >= price) {
              // The task is not colliding with previous tasks and agent has enough money
              newTask = (a, t) :: newTask
              execTasks.add(t)
              regAgents.put(a, budget - price)
            }
          }
        }

//        println("Choose optimal.")

        //
        // 4. After work pay salary, tell colliding and return the tasks
        //
        for ((a, b) <- regAgents) {
          if(a.maxMoney - b > AGENT_SALARY) {
            regAgents.put(a, b + AGENT_SALARY)
          }
          a.removeColliding(newTask.map(_._2))
        }

//        println("Sending "+newTask.size+" tasks to scheduler.")

        return newTask

        //Lastly interrupt recovery
      } catch {
        case e : InterruptedException => Thread.currentThread().interrupt()
        case e : Exception => throw e
      }
    }
    Nil
  }




  /**
   * Checks if a Task collides with the current executing ones.
   *
   * @param t - Task that could be executed
   *
   * @return true, iff the task collides
   */
  def collision(t : Task) : Boolean = execTasks.exists{e => t.collide(e)}


}
