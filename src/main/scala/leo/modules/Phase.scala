package leo
package modules

import leo.agents.{EmptyResult, Result, Task, FifoAgent, Agent}
import leo.agents.impl._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.modules.normalization.{Simplification, DefExpansion}
import leo.modules.output.{SZS_Theorem, SZS_Error}
import leo.modules.proofCalculi.splitting.ClauseHornSplit
import leo.modules.proofCalculi.{PropParamodulation, IdComparison, Paramodulation}


object Phase {
  def getStdPhases : Seq[Phase] = List(LoadPhase, PreprocessPhase, ParamodPhase)
  def getSplitFirst : Seq[Phase] = List(LoadPhase, PreprocessPhase, ExhaustiveClausificationPhase, SplitPhase, ParamodPhase)

  /**
   * Creates a complete phase from a List of Agents.
   *
   * @param dname - Name of the Phase
   * @param dagents - Agents to be used in this phase.
   * @return - A phase executing all agents until nothing is left to do.
   */
  def apply(dname : String, dagents : Seq[Agent]) : Phase = new CompletePhase {
    override protected def agents: Seq[Agent] = dagents
    override def name: String = dname
  }
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

  /**
   * Returns a short description and
   * all agents, that were started, for this phase.
   *
   * @return
   */
  lazy val description : String = s"  Agents used:\n    ${agents.map(_.name).mkString("\n    ")}"

  /**
   * A list of all agents to be started.
   * @return
   */
  protected def agents : Seq[Agent]

  /**
   * Method to start the agents, defined in `agents`
   */
  protected def start() : Unit = {
    agents.foreach(_.register())
    Scheduler().signal()
  }

  /**
   * Method to finish the agents.
   */
  protected def end() : Unit = {
    Scheduler().pause()
    agents.foreach(_.unregister())
    Scheduler().clear()
  }
}

/**
 * Abstract Phase, that implements
 * the execute to start the agents and wait for all to finish.
 */
trait CompletePhase extends Phase {
  private var finish = false

  private val waitAgent = new Wait

  override def start() : Unit = {
    super.start()
    waitAgent.finish = false
    waitAgent.register()
    Scheduler().signal()
  }

  override def end() : Unit = {
    super.end()
    waitAgent.unregister()
  }
  /**
   * Executes all defined agents and waits till no work is left.
   */
  override def execute() : Boolean = {
    // Starting all agents and signal scheduler
    start()

    // Wait until nothing is left to do
    waitAgent.synchronized(while(!waitAgent.finish) waitAgent.wait())

    // Ending all agents and clear the scheduler
    end()

    // If executing till the end, we will always return true, if other behaviour is wished, it has to be implemented
    return true
  }

  private class Wait extends FifoAgent{
    var finish = false
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent =>
        Out.output("Got Done Event.")
        synchronized{finish = true; notifyAll()};List()
      case StatusEvent(c,s) if c.parentContext == null && c.isClosed => // The root context was closed
        Out.output("Rootcontext Closed.")
        synchronized{finish = true; notifyAll()};List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}


object LoadPhase extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[Agent] = List(new ConjectureAgent)

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = Configuration.PROBLEMFILE

    start()
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
        Blackboard().forceStatus(Context())(SZS_Error)
        //Out.output((SZSOutput(SZS_Error)))
        return false
    }
    Scheduler().signal()
    synchronized{while(!finish) this.wait()}


    end()
    Wait.unregister()
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

object PreprocessPhase extends CompletePhase {
  override val name = "PreprocessPhase"
  override protected val agents: Seq[Agent] = List(new NormalClauseAgent(DefExpansion), new NormalClauseAgent(Simplification))
}

object ExhaustiveClausificationPhase extends CompletePhase {
  override val name = "ClausificationPhase"
  override protected val agents : Seq[Agent] = List(new ClausificationAgent())
}

object SplitPhase extends CompletePhase {
  override val name = "SplitPhase"
  override protected val agents: Seq[Agent] = List(new SplittingAgent(ClauseHornSplit))
}

object ParamodPhase extends CompletePhase {
  override val name : String = "ParamodPhase"
  override protected val agents: Seq[Agent] = List(new ParamodulationAgent(Paramodulation, IdComparison), new ParamodulationAgent(PropParamodulation, IdComparison), new ClausificationAgent())
}