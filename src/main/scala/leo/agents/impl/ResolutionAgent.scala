package leo.agents
package impl

import leo.datastructures.blackboard.{Blackboard, FormulaStore}
import leo.datastructures.internal._
import leo.modules.proofCalculi.resolution.ResolutionCalculus._

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
class ResolutionAgent extends AbstractAgent {
  override protected def toFilter(event: FormulaStore): Iterable[Task] = {
    // Necessary for all running tasks.
    if(event.role != "conjecture" && (event.status & event.normalized) == event.normalized) event.formula match {
        // If the formula is not a CNF until now, we make a PreCNF of it and look, if it is normalizable
      case Left(f) =>
        println("ResolutionAgent: Got formula, prepare to PreCNF.")
        return prepareNormalize(f :: Nil).fold{new CNFTask(event.newCNF(f::Nil))}{p => new CNFTask(event.newCNF(p))} :: Nil
        // Otherwise we first check for possible resolution partners, and if no exists we try to normalize one step
      case Right(f) =>
        // Check all CNFs in the Blackboard, if they can be resolved with the current one.
        val resT =
          Blackboard().getAll(_.formula.isRight).map{t => (t, prepareResolute(f)(t.cnfFormula))}.filterNot(_._2.isEmpty)
        // If not, try to Normalize
        if (resT.isEmpty) {
          println("ResolutionAgent: No resolution partner found. Ready to PreCNF.")
          prepareNormalize(f) match {
            case Some(fs) => return (new CNFTask(event.newCNF(fs))) :: Nil
            case None => return Nil
          }
        }
          // If so get all tasks for resolute
        else {
          println("ResolutionAgent: Try to resolute.")
          return resT.map { case (t, Some((l, r))) => new ResoluteTask(event.newCNF(l), t.newCNF(r))}
        }
    }
    else {
      println("ResolutionAgent: Got formula with status : " + event.status + ". Will not be considered.")
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
        var out = "ResolutionAgent: Updated '"+fStore.cnfFormula.map(_.pretty).mkString(" , ")+"' to"
        for(a <- n){
          out = out +"\n    '"+a.map(_.pretty).mkString(" , ")+"'"
        }
        println(out)
        return new StdResult(n.map{nf => fStore.randomName().newCNF(nf)}.toSet, Map.empty, Set(fStore))
      case r : ResoluteTask =>
        val lStore = r.getL()
        val rStore = r.getR()
        resoluteInfere(lStore.cnfFormula, rStore.cnfFormula) match {
          case None =>
            println("ResolutionAgent: Error! Nothing to update.")
            return new StdResult(Set.empty, Map.empty, Set.empty)
          case Some(nf) =>
            println("ResolutionAgent: Resoluted '"+lStore.cnfFormula.map(_.pretty).mkString(" , ")+"' and '"+rStore.cnfFormula.map(_.pretty).mkString(" , ")+"' to\n" +
              "     '"+nf.map(_.pretty).mkString(" , ")+"'.")
            return new StdResult(Set(lStore.newCNF(nf).randomName()), Map.empty, Set.empty)
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

  override def bid(budget : Double) : Double = 1

  def get() : FormulaStore = f
}

class ResoluteTask(_l : FormulaStore, _r : FormulaStore) extends Task {
  override def readSet(): Set[FormulaStore] = Set(_l, _r)
  override def writeSet(): Set[FormulaStore] = Set.empty

  override def bid(budget : Double) : Double = 1

  def getL() : FormulaStore = _l
  def getR() : FormulaStore = _r
}