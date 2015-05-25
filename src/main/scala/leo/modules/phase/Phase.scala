package leo
package modules.phase

import leo.Configuration
import leo.agents._
import leo.agents.impl.{FiniteHerbrandEnumerateAgent, _}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSDataStore, SZSStore}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.{BetaSplit, Context}
import leo.datastructures.impl.Signature
import leo.modules.{SZSException, Utility}
import leo.modules.normalization.{DefExpansion, NegationNormal, Simplification, Skolemization}
import leo.modules.output._
import leo.modules.calculus.enumeration.SimpleEnum
import leo.modules.calculus.splitting.ClauseHornSplit
import leo.modules.calculus.{IdComparison, Paramodulation, PropParamodulation}


object Phase {
  def getStdPhases : Seq[Phase] = List(new LoadPhase(true), SimplificationPhase, ParamodPhase)
  def getHOStdPhase : Seq[Phase] = List(new LoadPhase(true), PreprocessPhase, ParamodPhase)
  def getSplitFirst : Seq[Phase] = List(new LoadPhase(true), PreprocessPhase, ExhaustiveClausificationPhase, SplitPhase, ParamodPhase)
  def getCounterSat : Seq[Phase] =  List(new LoadPhase(false), FiniteHerbrandEnumeratePhase, PreprocessPhase, ParamodPhase)
  def getCounterSatRemote : Seq[Phase] =  List(new LoadPhase(false), FiniteHerbrandEnumeratePhase, RemoteCounterSatPhase)
  def getExternalPhases : Seq[Phase] = List(new LoadPhase(true), PreprocessPhase, ExternalProverPhase)

  /**
   * Creates a complete phase from a List of Agents.
   *
   * @param dname - Name of the Phase
   * @param dagents - Agents to be used in this phase.
   * @return - A phase executing all agents until nothing is left to do.
   */
  def apply(dname : String, dagents : Seq[AgentController]) : Phase = new CompletePhase {
    override protected def agents: Seq[AgentController] = dagents
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
  protected def agents : Seq[AgentController]

  /**
   * Method to start the agents, defined in `agents`
   */
  protected def init() : Unit = {
    agents.foreach(_.register())
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
  private def getName = name
  protected var waitAgent : CompleteWait = null
  private var wController : AgentController = null


  def initWait() : Unit = {
    waitAgent = new CompleteWait
    wController = new FifoController(waitAgent)
    wController.register()
  }

  override def end() : Unit = {
    super.end()
    wController.unregister()
    wController = null
    waitAgent = null
  }

  /**
   * Waits until the Wait Agent signals
   * the end of the execution
   *
   * @return true, if the execution was sucessfull, false otherwise
   */
  def waitTillEnd() : Boolean = {
    Scheduler().signal()
    waitAgent.synchronized{while(!waitAgent.finish) waitAgent.wait()}
    if(waitAgent.scedKill) return false
    return true
  }

  /**
   * Executes all defined agents and waits till no work is left.
   */
  override def execute() : Boolean = {
    // Starting all agents and signal scheduler
    init()
    initWait()

    if(!waitTillEnd()) {
      Out.info(s"$name will be terminated and program is quitting.")
      return false
    }
    // Ending all agents and clear the scheduler
    end()

    // If executing till the end, we will always return true, if other behaviour is wished, it has to be implemented
    return true
  }

  protected class CompleteWait extends Agent {
    var finish = false
    var scedKill = false
    override def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent =>
        synchronized{finish = true; notifyAll()};List()
      case DataEvent(SZSStore(s,c), StatusType) if c.parentContext == null && c.isClosed => // The root context was closed
        synchronized{finish = true; notifyAll()};List()
      case _ => List()
    }
    override def name: String = s"${getName}Terminator"
    override def run(t: Task): Result = Result()
    override def kill(): Unit = synchronized{
      Out.info(s"$name was killed.")
      scedKill = true
      finish = true
      notifyAll()
    }
  }
}