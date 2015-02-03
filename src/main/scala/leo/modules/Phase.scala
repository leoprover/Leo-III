package leo
package modules

import leo.agents.{EmptyResult, Result, Task, FifoAgent}
import leo.agents.impl._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{Blackboard, DoneEvent, StatusEvent, Event}
import leo.modules.output.{SZS_Theorem, SZS_Error}
import leo.modules.proofCalculi.splitting.ClauseHornSplit
import leo.modules.proofCalculi.{PropParamodulation, IdComparison, Paramodulation}


object Phase {
  def getStdPhases : Seq[Phase] = List(LoadPhase, PreprocessPhase, ParamodPhase)
  def getSplitFirst : Seq[Phase] = List(LoadPhase, PreprocessPhase, ExhaustiveClausificationPhase, SplitPhase, ParamodPhase)
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

  /**
   * Returns the name of the phase.
   * @return
   */
  def name : String
}


object LoadPhase extends Phase{
  override val name = "LoadPhase"
  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = Configuration.PROBLEMFILE
    UtilAgents.Conjecture()
    Wait.register()

    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        Out.output(SZSOutput(e.status))
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception")
        e.printStackTrace()
        Out.output((SZSOutput(SZS_Error)))
        return false
    }
    Scheduler().signal()
    synchronized{while(!finish) this.wait()}

    Scheduler().pause()

    Blackboard().unregisterAgent(UtilAgents.Conjecture())
    Blackboard().unregisterAgent(Wait)
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; LoadPhase.synchronized(LoadPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}

object PreprocessPhase extends Phase {
  override val name = "PreprocessPhase"
  var finish : Boolean = false

  override def execute(): Boolean = {
    Scheduler().signal()
    NormalClauseAgent.DefExpansionAgent()
    NormalClauseAgent.SimplificationAgent()

    Wait.register()
    synchronized(while(!finish) wait())

    NormalClauseAgent.DefExpansionAgent().setActive(false)
    NormalClauseAgent.DefExpansionAgent().setActive(false)
    Blackboard().unregisterAgent(Wait)
    Scheduler().clear()
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

object ExhaustiveClausificationPhase extends Phase {
  override val name = "ClausificationPhase"
  var finish : Boolean = false

  override def execute(): Boolean = {
    Scheduler().signal()
    val c = new ClausificationAgent
    c.register()
    Wait.register()
    
    synchronized(while(!finish) wait())

    Blackboard().unregisterAgent(c)
    Blackboard().unregisterAgent(Wait)
    Scheduler().clear()
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; ExhaustiveClausificationPhase.synchronized(ExhaustiveClausificationPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "ExhaustiveClausificationPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}

object SplitPhase extends Phase {
  override val name = "SplitPhase"
  var finish : Boolean = false

  override def execute(): Boolean = {
    Scheduler().signal()
    val c = new SplittingAgent(ClauseHornSplit)
    c.register()

    Wait.register()
    synchronized(while(!finish) wait())

    Blackboard().unregisterAgent(c)
    Blackboard().unregisterAgent(Wait)
    Scheduler().clear()
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; SplitPhase.synchronized(SplitPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "SplitPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}

object ParamodPhase extends Phase {
  override val name : String = "ParamodPhase"
  var finish : Boolean = false

  override def execute(): Boolean = {
    Scheduler().signal()
    val p1 = new ParamodulationAgent(Paramodulation, IdComparison)
    val p2 = new ParamodulationAgent(PropParamodulation, IdComparison)
    // val sp = new SplittingAgent(ClauseHornSplit)

    p1.register()
    p2.register()
    //sp.register()
    WaitForProof.register()
    ClausificationAgent()
    synchronized(while(!finish)wait())
    Blackboard().unregisterAgent(WaitForProof)
    Scheduler().clear()
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