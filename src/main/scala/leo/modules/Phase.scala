package leo
package modules

import leo.agents.{EmptyResult, Result, Task, FifoAgent}
import leo.agents.impl._
import leo.datastructures.blackboard.{Blackboard, DoneEvent, StatusEvent, Event}
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
    ContextControlAgent.register()

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
  var finish : Boolean = false

  override def execute(): Boolean = {
    NormalClauseAgent.DefExpansionAgent()
    NormalClauseAgent.SimplificationAgent()

    Wait.register()
    synchronized(while(!finish) wait())

    NormalClauseAgent.DefExpansionAgent().setActive(false)
    NormalClauseAgent.DefExpansionAgent().setActive(false)
    Blackboard().unregisterAgent(Wait)
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; PreprocessPhase.synchronized(PreprocessPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
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
    Blackboard().unregisterAgent(WaitForProof)
    return true
  }

  private object WaitForProof extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case StatusEvent(c,s) =>
        if (c.parentContext == null && s == SZS_Theorem) {
          finish = true
          ParamodPhase.synchronized(ParamodPhase.notifyAll())
          List()
        } else List()
      case d : DoneEvent => finish = true; ParamodPhase.synchronized(ParamodPhase.notifyAll()); List()
      case _ => List()
    }
    override def name: String = "ParamodPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}