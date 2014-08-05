package leo.datastructures.blackboard.impl



import leo.agents.{Task, Agent}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.internal.{ Term => Formula }
import scala.collection.concurrent.TrieMap
import leo.datastructures.blackboard._
import scala.collection.mutable
import scala.collection.mutable.{Queue, Map => MMap}

/**
 * Starting Blackboard. Just to replace @see{leoshell.FormulaHandle}
 *
 * @author Max Wisniewski <max.wisniewski@fu-berlin.de>
 * @author Daniel Jentsch <d.jentsch@fu-berlin.de>
 * @since 29.04.2014
 */
class SimpleBlackboard extends Blackboard {

  import FormulaSet._

  var DEBUG : Boolean = true

  // For each agent a List of Tasks to execute

  override def getFormulas: List[FormulaStore] = getAll(_ => true)

  override def getAll(p: (Formula) => Boolean): List[FormulaStore] = read { formulas =>
    formulas.values.filter { store =>
      p(store.formula)
    }.toList
  }

  override def getFormulaByName(name: String): Option[FormulaStore] = read { formulas =>
    formulas get name
  }

  override def addFormula(name : String, formula: Formula, role : String) {
    val s = Store.apply(name, formula, role)
    addFormula(s)
    filterAll(_.filter(s))
  }

  override def addFormula(formula : FormulaStore) {
    write { formulas =>
      formulas put (formula.name, formula)
    }
//    TaskSet.agents.foreach{a => TaskSet.addTasks(a,a.filter(formula))}
  }

  override def removeFormula(formula: FormulaStore): Boolean = rmFormulaByName(formula .name)

  override def rmFormulaByName(name: String): Boolean = write { formulas =>
    formulas.remove(name) match {
      case Some(x) => {
        true
      }
      case None => false
    }
  }

  override def rmAll(p: (Formula) => Boolean) = write { formulas =>
      formulas.values foreach (form => if (p(form.formula)) formulas.remove(form.name) else formulas)
  }

  /**
   * Register a new Handler for Formula adding Handlers.
   * @param a - The Handler that is to register
   */
  override def registerAgent(a : Agent) : Unit = {
    TaskSet.addAgent(a)
    freshAgent(a)
  }


  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask(): (Agent,Task) = TaskSet.getTask()

  override def clear() : Unit = {
    rmAll(_ => true)
    TaskSet.clear()
  }

  /**
   * Gives all agents the chance to react to an event
   * and adds the generated tasks.
   *
   * @param t - Function that generates for each agent a set of tasks.
   */
  override def filterAll(t: (Agent) => Iterable[Task]): Unit = {
    TaskSet.agents.foreach{ a =>
      TaskSet.addTasks(a,t(a))
    }
  }

  /**
   * Method that filters the whole Blackboard, if a new agent 'a' is added
   * to the context.
   *
   * @param a - New Agent.
   */
  override protected[blackboard] def freshAgent(a: Agent): Unit = {
    // ATM only formulas trigger events
    getFormulas.foreach{fS => TaskSet.addTasks(a,a.filter(fS))}
  }
}

/**
 * Handles multi threaded access to a mutable map.
 */
private object FormulaSet {
  // Formulas

  private val formulaMap = new TrieMap[String, FormulaStore]()

  /**
   * Per se se an action itself. Maybe try different syntax, s.t. we know this one locks,
   * the other one not.
   *
   * Not a Problem ATM: writing the same Key twice may introduce inconsitencies, if two
   * distinct formula stores are used.
   */

  def write[R](action: MMap[String, FormulaStore] => R): R = action(formulaMap)

  def read[R](action: MMap[String, FormulaStore] => R): R = action(formulaMap)
}

private object TaskSet {
  import scala.collection.mutable.HashSet

  protected[blackboard] val agentWork = new Queue[(Agent,Task)]()
  protected[blackboard] var regAgents = Set[Agent]()
  protected[blackboard] val execTasks = new HashSet[Task] with mutable.SynchronizedSet[Task]

  private var work : Int = 0

  def clear() : Unit = {
    agentWork.clear()
    work = 0
  }

  def addAgent(a : Agent) {
    this.synchronized(regAgents = regAgents + a)
  }

  def agents : List[Agent] = this.synchronized(regAgents.toList)

  def addTasks(a : Agent, ts : Iterable[Task]) = this.synchronized {
    try{
      ts.foreach{t => agentWork.enqueue((a,t)); work += 1}
      this.notifyAll()
    } catch {
      case e : InterruptedException => Thread.currentThread().interrupt()
      case e : Exception => throw e
    }
  }

  def getTask() : (Agent,Task) = this.synchronized{
    while(!Scheduler().isTerminated()) {
      try {
        while (work == 0) this.wait()
        work -= 1
        var w = agentWork.dequeue()
        // As long as the task collide, we discard them (Updates will be written back and trigger the task anew)
        while (collision(w._2)) {
          while (work == 0) this.wait()
          work -= 1
          w = agentWork.dequeue()
        }

        execTasks.add(w._2)
        return w
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt()
        case e: Exception => throw e
      }
    }
    (null,null)
  }

  private def collision(t : Task) : Boolean = execTasks.exists{e =>
    !t.readSet().intersect(e.writeSet()).isEmpty ||
    !e.readSet().intersect(t.writeSet()).isEmpty ||
    !e.writeSet().intersect((t.writeSet())).isEmpty
  }
}
