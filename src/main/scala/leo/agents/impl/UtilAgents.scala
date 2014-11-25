package leo.agents
package impl

import leo.datastructures.{LitFalse, Not}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{FormulaEvent, Event, Blackboard, FormulaStore}
import leo.modules.output.logger.Out

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
      fin = new FinishedAgent(-1)     // TODO : No idea, maybe delete
      fin.register()
    }
    fin
  }

  def StdAgents() : Unit = {
    NormalClauseAgent.SimplificationAgent()
    NormalClauseAgent.DefExpansionAgent()
    NormalClauseAgent.NegationNormalAgent()
//    NormalClauseAgent.SkolemAgent()
//    NormalClauseAgent.PrenexAgent()
//    Finished()
    Conjecture()
  }
}

/**
 * This agents runs on a Formula if it is a conjecture
 * and inserts the negated conjecture.
 *
 */
class ConjectureAgent extends AbstractAgent {

  override val name = "ConjectureAgent"

  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param e - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  override def toFilter(e: Event): Iterable[Task] = e match {
    case FormulaEvent(event) => event.formula match {
      case Left(f) => if (event.role == "conjecture") List(new SingleFormTask(event)) else Nil
      case Right(_) => Nil
    }
    case _ => Nil
  }

  /**
   * Negates the conjecture and renames the role
   */
  override def run(t: Task) : Result = {
    t match {
      case t1: SingleFormTask =>
        val fS = t1.getFormula()
        val form = fS.simpleFormula
        val status = fS.status
        val rS = fS.newFormula(Not(form)).newRole("negated_conjecture").newStatus(status & ~7)

//        println("Negated Conjecture")

        new StdResult(Set.empty,Map((fS,rS)),Set.empty)
      case _ => throw new IllegalArgumentException("Executing wrong task.")
    }
  }
}


class SingleFormTask(f : FormulaStore) extends Task {
  def getFormula() : FormulaStore = f
  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)

  override def bid(budget : Double) : Double = 1

  override val toString : String = "SingleFormulaTask on "+f.toString

  override def equals(other : Any) = other match {
    case o : SingleFormTask => o.getFormula() == f
    case _                  => false
  }
}





class FinishedAgent(timeout : Int) extends AbstractAgent {

  override val name = "FinishedAgent"

  // Killing if not done in timeout
  private val end : Thread = new Thread(new Runnable {
    override def run(): Unit = {
//      println("Init delay kill.")
      if(timeout < 0) return
      synchronized{
        try {
          wait(timeout * 1000)
          println("% SZS status Timeout")
//          println("Killing task.")
          Scheduler().killAll()
        } catch {
          case e : InterruptedException => return
          case _ : Throwable =>
            println("% SZS status Timeout")
            Scheduler().killAll()
        }
      }
    }
  })

  end.start()


  /**
   * This method should be called, whenever a formula is added to the blackboard.
   *
   * The filter then checks the blackboard if it can generate a task from it.
   *
   * @param e - Newly added or updated formula
   * @return - set of tasks, if empty the agent won't work on this event
   */
  override def toFilter(e: Event): Iterable[Task] = {
    e match {
      case FormulaEvent(event) => event.formula match {
        case Left (LitFalse () ) => List (new SingleFormTask (event) )
        case _ => Nil
      }
      case _  => Out.warn(s"[$name]: Received unkown event $e"); Nil
    }
  }

  /*
   * TODO : Do we want the agent to stop the Scheduler ???
   */
  override def run(t: Task): Result = {
    Scheduler().killAll()
    end.interrupt()
    println("% SZS status Success")
    new StdResult(Set.empty, Map.empty, Set.empty)
  }
}