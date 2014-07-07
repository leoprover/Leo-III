package leo.agents
package impl

import leo.datastructures.blackboard.{Blackboard, FormulaStore}

object UtilAgents {

  private var con : ConjectureAgent = null
  private var fin : FinishedAgent = null

  def Conjecture() : ConjectureAgent = {
    if (con == null) {
      con = new ConjectureAgent()
      con.register()
    }
    con
  }

  def Finished() : FinishedAgent = {
    if (fin == null) {
      fin = new FinishedAgent
      fin.register()
    }
    fin
  }

  def StdAgents() : Unit = {
    NormalClauseAgent.SimplificationAgent()
    NormalClauseAgent.NegationNormalAgent()
//    NormalClauseAgent.SkolemAgent()
//    NormalClauseAgent.PrenexAgent()
    Finished()
    Conjecture()
  }
}

/**
 * This agents runs on a Formula if it is a conjecture
 * and inserts the negated conjecture.
 *
 */
class ConjectureAgent extends Agent {

  private var _isActive = true

  /**
   *
   * @return true, if this Agent can execute at the moment
   */
  override def isActive: Boolean = _isActive

  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param event - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  override def filter(event: FormulaStore): Set[Task] = if(event.role == "conjecture") Set(new SingleFormTask(event)) else Set.empty[Task]

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  override def register(): Unit = Blackboard().registerAgent(this)

  /**
   * Sets isActive.
   *
   * @param bool
   */
  override def setActive(bool: Boolean): Unit = _isActive = bool

  /**
   * Negates the conjecture and renames the role
   */
  override def run(t: Task) : Result = {
    import leo.datastructures.internal._

    t match {
      case t1: SingleFormTask =>
        val fS = t1.getFormula()
        val form = fS.formula
        val status = fS.status
        val rS = fS.newFormula(Not(form)).newRole("negated_conjecture").newStatus(status & ~3)

        println("Negated Conjecture")

        new StdResult(Set.empty,Map((fS,rS)),Set.empty)
      case _ => throw new IllegalArgumentException("Executing wrong task.")
    }
  }
}


class SingleFormTask(f : FormulaStore) extends Task {
  def getFormula() : FormulaStore = f
  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)
}


class FinishedAgent extends Agent {
  private var _isActive = true
  override def isActive: Boolean = _isActive
  override def setActive(bool: Boolean): Unit = _isActive = bool


  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param event - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  override def filter(event: FormulaStore): Set[Task] = {
    import leo.datastructures.internal._
    event.formula match {
      case LitFalse() => Set(new SingleFormTask(event))
      case _ => Set.empty
    }
  }

  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  override def register(): Unit = Blackboard().registerAgent(this)


  /*
   * TODO : Do we want the agent to stop the Scheduler ???
   */
  override def run(t: Task): Result = {
    leo.datastructures.blackboard.scheduler.Scheduler().pause()
    println("Derived '$false' from the context. Proof found.")
    new StdResult(Set.empty, Map.empty, Set.empty)
  }
}