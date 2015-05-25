package leo.modules.phase

import leo._
import leo.agents.{Task, Agent, AgentController}
import leo.datastructures.{ClauseAnnotation, Role_NegConjecture, Role_Conjecture}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSDataStore}
import leo.datastructures.context.Context
import leo.modules.output.{SZS_CounterSatisfiable, SZS_Error}
import leo.modules.calculus.CalculusRule
import leo.modules.{SZSException, Utility}

class LoadPhase(negateConjecture : Boolean, problemfile: String = Configuration.PROBLEMFILE) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[AgentController] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = problemfile

    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        // Out.output(SZSOutput(e.status))
        SZSDataStore.forceStatus(Context())(e.status)
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception")
        e.printStackTrace()
        SZSDataStore.forceStatus(Context())(SZS_Error)
        //Out.output((SZSOutput(SZS_Error)))
        return false
    }
    import leo.datastructures.blackboard.Store
    if(negateConjecture) FormulaDataStore.getAll{NegConjRule.canApply(_)}.foreach{f => FormulaDataStore.removeFormula(f); FormulaDataStore.addNewFormula(NegConjRule.apply(f))}
    return true
  }

  private class Wait(lock : AnyRef) extends Agent{
    override def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; lock.synchronized(lock.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = Result()
  }

  private object NegConjRule extends CalculusRule {
    val name = "neg_conjecture"
    override val inferenceStatus = Some(SZS_CounterSatisfiable)
    def canApply(fs: FormulaStore) = fs.role == Role_Conjecture
    def apply(fs: FormulaStore) = Store(fs.name + "_neg", fs.clause.mapLit(l => l.flipPolarity), Role_NegConjecture, fs.context, fs.status & ~7, ClauseAnnotation(this, fs)) // TODO: This is not generally not valid, fix me
  }
}