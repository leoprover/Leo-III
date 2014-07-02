package leo.datastructures.blackboard.impl



import leo.agents.{Task, Agent}
import leo.datastructures.internal.{ Term => Formula }
import leo.datastructures.blackboard.scheduler.Scheduler
import scala.collection.concurrent.TrieMap
import leo.datastructures.blackboard._
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


  // Scheduler ATM Here because accessibility in prototype version
  protected[blackboard] val _scheduler = Scheduler(5) // TODO somewhere else
  def scheduler = _scheduler

  override def getFormulas: List[FormulaStore] = getAll(_ => true)

  override def getAll(p: (Formula) => Boolean): List[FormulaStore] = read { formulas =>
    formulas.values.filter { store =>
      p(store.formula)
    }.toList
  }

  override def getFormulaByName(name: String): Option[FormulaStore] = read { formulas =>
    formulas get name
  }

  override def addFormula(name : String, formula: Formula) {
    addFormula(Store.apply(name, formula))
  }

  override def addFormula(formula : FormulaStore) {
    write { formulas =>
      formulas put (formula.name, formula)
    }
    TaskSet.agents.foreach{a => TaskSet.addTasks(a,a.filter(formula))}
  }

  override def removeFormula(formula: FormulaStore): Boolean = rmFormulaByName(formula .name)

  override def rmFormulaByName(name: String): Boolean = write { formulas =>
    formulas.remove(name) match {
      case Some(x) => {
//        observeRemoveSet.filter(_.filterRemove(x)).foreach {o => o.removeFormula(x); o.wakeUp()}
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
  override def registerAgent(a : Agent) : Unit = TaskSet.addAgent(a)

  /**
   * Used by Stores to mark a FormulaStore as Changed, if nothing
   * has to be updated. Handlers can register to these updates
   * @param f
   */
  override protected[blackboard] def emptyUpdate(f: FormulaStore) {
    TaskSet.agents.foreach{a => TaskSet.addTasks(a,a.filter(f))}
  }

  /**
   * Blocking Method to get a fresh Task.
   *
   * @return Not yet executed Task
   */
  override def getTask(): (Agent,Task) = TaskSet.getTask()
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
  protected[blackboard] val agentWork = new Queue[(Agent,Task)]()
  protected[blackboard] var regAgents = Set[Agent]()

  private var work : Int = 0

  def addAgent(a : Agent) {
    this.synchronized(regAgents = regAgents + a)
  }

  def agents : List[Agent] = this.synchronized(regAgents.toList)

  def addTasks(a : Agent, ts : Set[Task]) = this.synchronized {
    try{
      ts.foreach{t => agentWork.enqueue((a,t)); work += 1}
      this.notifyAll()
    } catch {
      case e : InterruptedException => Thread.currentThread().interrupt()
      case e : Exception => throw e
    }
  }

  def getTask() : (Agent,Task) = this.synchronized{
    while(true) {
      try {
        while (work == 0) this.wait()
        work -= 1
        return agentWork.dequeue()
      } catch {
        case e: InterruptedException => Thread.currentThread().interrupt()
        case e: Exception => throw e
      }
    }
    null
  }
}
