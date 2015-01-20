package leo
package modules

import leo.agents.{EmptyResult, Result, Task, FifoAgent}
import leo.agents.impl.{UtilAgents, ClausificationAgent, ParamodulationAgent, NormalClauseAgent}
import leo.datastructures.blackboard.{StatusEvent, Event}
import leo.modules.output.{SZS_Theorem, SZS_Error}
import leo.modules.proofCalculi.{PropParamodulation, IdComparison, Paramodulation}


object Phase {
  def getStdPhases : Seq[Phase] = List(LoadPhase, PreprocessPhase, ParamodPhase)
}

/**
 * Trait for a MainPhase in Leo-III
 *
 * @author Max Wisniewski
 * @since 12/1/14
 */
trait Phase {
  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  def execute() : Boolean
}


object LoadPhase extends Phase{
  override def execute(): Boolean = {

    val file = Configuration.PROBLEMFILE
    UtilAgents.Conjecture()

    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        Out.output(SZSOutput(e.status))
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception: "+e.getMessage)
        Out.output((SZSOutput(SZS_Error)))
        return false
    }
    Thread.sleep(500)
    UtilAgents.Conjecture().setActive(false)
    return true
  }
}

object PreprocessPhase extends Phase {
  override def execute(): Boolean = {
    NormalClauseAgent.DefExpansionAgent()
    NormalClauseAgent.SimplificationAgent()

    Thread.sleep(2000)

    NormalClauseAgent.DefExpansionAgent().setActive(false)
    NormalClauseAgent.DefExpansionAgent().setActive(false)
    return true
  }
}

object ParamodPhase extends Phase {
  var finish : Boolean = false

  override def execute(): Boolean = {
    val p1 = new ParamodulationAgent(Paramodulation, IdComparison)
    val p2 = new ParamodulationAgent(PropParamodulation, IdComparison)
    p1.register()
    p2.register()
    WaitForProof.register()
    ClausificationAgent()
    synchronized(while(!finish)wait())
    return true
  }

  private object WaitForProof extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case StatusEvent(c,s) =>
        if (c.parentContext == null && s == SZS_Theorem) {
          finish = true
          ParamodPhase.synchronized(ParamodPhase.notify())
          List()
        } else List()
      case _ => List()
    }
    override def name: String = "DebugControlAgent"
    override def run(t: Task): Result = EmptyResult
  }
}