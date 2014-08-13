package leo.agents
package impl

import leo.datastructures.blackboard.{Blackboard, FormulaStore}
import leo.datastructures.internal._
import leo.modules.proofCalculi.resolution.ResolutionCalculus._

import scala.collection.mutable

object ResolutionAgent {
  var rA : Agent = null

  def apply() : Agent = {
    if(rA == null) {
      rA = new ResolutionAgent
      rA.register()
    }
    rA
  }
}

/**
 *
 * This agent performs the two tasks to resolute on the blackboard
 *
 * @author Max Wisniewski
 * @since 8/12/14
 */
class ResolutionAgent extends Agent {

  override val name = "ResolutionAgent"

  // In our case we want to use only high priority tasks
  private var _isActive : Boolean = false

  override def isActive : Boolean = _isActive

  override def setActive(a : Boolean) = {
    _isActive = a
    if(a && q.nonEmpty) Blackboard().signalTask()
  }


  /**
   * <p>
   * In this method the Agent gets the Blackboard it will work on.
   * Registration for Triggers should be done in here.
   * </p>
   *
   */
  override def register() {
    Blackboard().registerAgent(this)
    setActive(true)
  }

  /*
   * TODO change the ordering, not use priority queue, because budget is not fixed
   */
  protected val q : mutable.PriorityQueue[Task] = new mutable.PriorityQueue[Task]()(Ordering.by{(x : Task) => x.bid(100)})

  /**
   * <p>
   * A predicate that distinguishes interesting and uninteresing
   * Formulas for the Handler.
   * </p>
   * @param f - Newly added formula
   * @return true if the formula is relevant and false otherwise
   */
  override def filter(f: FormulaStore) : Unit = {
    var done = false
    for(t <- toFilter(f)) {
      if (!Blackboard().collision(t)) {
//        println(name + " : Got a task.")
        q.enqueue(t)
        done = true
      }
    }
    if(done) Blackboard().signalTask()
  }

  /**
   *
   * Returns a a list of Tasks, the Agent can afford with the given budget.
   *
   * @param budget - Budget that is granted to the agent.
   */
  override def getTasks(budget: Double): Iterable[Task] = {
//    println("ResolutionAgent: Getting Tasks.")
    var erg = List[Task]()
    var costs : Double = 0
    for(t <- q){
//      println("Testing "+t.toString+" for "+t.bid(budget))
      if(costs > budget) return erg
      else {
        costs += t.bid(budget)
        erg = t :: erg
      }
    }
    println("ResolutionAgent: Send "+erg.size+" task to the auction.")
    erg
  }

  /**
   * Removes all Tasks
   */
  override def clearTasks(): Unit = q.clear()

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = q.iterator.toIterable

  /**
   *
   * Given a set of (newly) executing tasks, remove all colliding tasks.
   *
   * @param nExec - The newly executing tasks
   */
  override def removeColliding(nExec: Iterable[Task]): Unit = {
    q.filterNot{tbe => nExec.exists(_.collide(tbe))}
  }


  protected def toFilter(event: FormulaStore): Iterable[Task] = {
    // Necessary for all running tasks.
    if(event.role != "conjecture" && event.normalized) event.formula match {
        // If the formula is not a CNF until now, we make a PreCNF of it and look, if it is normalizable
      case Left(f) =>
//        println("ResolutionAgent: Got formula, prepare to PreCNF.")
        return prepareNormalize(f :: Nil).fold{new CNFTask(event.newCNF(f::Nil))}{p => new CNFTask(event.newCNF(p))} :: Nil
        // Otherwise we first check for possible resolution partners, and if no exists we try to normalize one step
      case Right(f) =>
        // Check all CNFs in the Blackboard, if they can be resolved with the current one.
        val resT =
          Blackboard().getAll(_.formula.isRight).map{t => (t, prepareResolute(f)(t.cnfFormula))}.filterNot(_._2.isEmpty)
        // If not, try to Normalize
        if (resT.isEmpty) {
//          println("ResolutionAgent: No resolution partner found. Ready to PreCNF.")
          prepareNormalize(f) match {
            case Some(fs) => return (new CNFTask(event.newCNF(fs))) :: Nil
            case None => return Nil
          }
        }
          // If so get all tasks for resolute
        else {
//          println("ResolutionAgent: Try to resolute.")
          return resT.map { case (t, Some((l, r))) => new ResoluteTask(event.newCNF(l), t.newCNF(r))}
        }
    }
    else {
//      println("ResolutionAgent: Got formula with status : " + event.status + ". Will not be considered.")
      Nil
    }
  }

  /**
   * This function runs the specific agent on the registered Blackboard.
   */
  override def run(t: Task): Result = {
    t match {
      case c : CNFTask =>
        val fStore = c.get()
        val n = conjunctionNormalize(fStore.cnfFormula)
//        var out = "ResolutionAgent: Updated '"+fStore.cnfFormula.map(_.pretty).mkString(" , ")+"' to"
//        for(a <- n){
//          out = out +"\n    '"+a.map(_.pretty).mkString(" , ")+"'"
//        }
//        println(out)
        return new StdResult(n.map{nf => fStore.randomName().newCNF(nf).newRole("plain")}.toSet, Map.empty, Set(fStore))
      case r : ResoluteTask =>
        val lStore = r.getL()
        val rStore = r.getR()
        resoluteInfere(lStore.cnfFormula, rStore.cnfFormula) match {
          case None =>
//            println("ResolutionAgent: Error! Nothing to update.")
            return new StdResult(Set.empty, Map.empty, Set.empty)
          case Some(nf) =>
//            println("ResolutionAgent: Resoluted '"+lStore.cnfFormula.map(_.pretty).mkString(" , ")+"' and '"+rStore.cnfFormula.map(_.pretty).mkString(" , ")+"' to\n" +
//              "     '"+nf.map(_.pretty).mkString(" , ")+"'.")
            if(Blackboard().getFormulas.forall{f => f.formula != Right(nf)}) {
              return new StdResult(Set(lStore.newCNF(nf).randomName().newRole("plain")), Map.empty, Set.empty)
            } else {
//              println("Infered already existing formula " + lStore.newCNF(nf).newRole("plain").toString)
              return new StdResult(Set.empty, Map.empty, Set.empty)
            }
        }
    }
  }
}

/**
 * Performs one normalization step, towards
 * @param f
 */
class CNFTask(f : FormulaStore) extends Task {

  override def readSet(): Set[FormulaStore] = Set(f)
  override def writeSet(): Set[FormulaStore] = Set(f)

  override def bid(budget : Double) : Double = budget / 5

  def get() : FormulaStore = f
}

class ResoluteTask(_l : FormulaStore, _r : FormulaStore) extends Task {
  override def readSet(): Set[FormulaStore] = Set(_l, _r)
  override def writeSet(): Set[FormulaStore] = Set.empty

  override def bid(budget : Double) : Double = {
    val rSize = _l.cnfFormula.size + _r.cnfFormula.size - 2
    budget / (rSize + 1)
  }

  def getL() : FormulaStore = _l
  def getR() : FormulaStore = _r
}