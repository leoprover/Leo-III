
package leo
package modules.phase

import leo.agents._
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{SZSStore}
import leo.datastructures.blackboard.scheduler.Scheduler


object Phase {
  def getStdPhases : Seq[Phase] = List() //List(new LoadPhase(true), SimplificationPhase, ParamodPhase)
  def getHOStdPhase : Seq[Phase] = List() //List(new LoadPhase(true), PreprocessPhase, ParamodPhase)
  def getSplitFirst : Seq[Phase] = List() //List(new LoadPhase(true), PreprocessPhase, ExhaustiveClausificationPhase, SplitPhase, ParamodPhase)
  def getCounterSat : Seq[Phase] =  List() //List(new LoadPhase(false), FiniteHerbrandEnumeratePhase, PreprocessPhase, ParamodPhase)
  def getCounterSatRemote : Seq[Phase] =  List() //List(new LoadPhase(false), FiniteHerbrandEnumeratePhase, RemoteCounterSatPhase)
  def getExternalPhases : Seq[Phase] = List() //List(new LoadPhase(true), PreprocessPhase, ExternalProverPhase)

  /**
   * Creates a complete phase from a List of Agents.
   *
   * @param dname - Name of the Phase
   * @param dagents - Agents to be used in this phase.
   * @return - A phase executing all agents until nothing is left to do.
   */
  def apply(dname : String, dagents : Seq[Agent]): Phase = new CompletePhase {
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
 *
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
 *
   * @return
   */
  protected def agents : Seq[Agent]

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


  def initWait() : Unit = {
    waitAgent = new CompleteWait
    waitAgent.register()
  }

  override def end() : Unit = {
    super.end()
    waitAgent.unregister()
    waitAgent = null
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

  protected class CompleteWait extends AbstractAgent {
    var finish = false
    var scedKill = false
    override def interest : Option[Seq[DataType]] = Some(Seq(StatusType))
    @inline override val init: Iterable[Task] = Seq()
    override def filter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent =>
        synchronized{finish = true; notifyAll()};List()
      case r : Result =>
        if(r.inserts(StatusType).nonEmpty || r.updates(StatusType).nonEmpty){
          synchronized{finish = true; notifyAll()}
        }
        List()
      case _ => List()
    }
    override def name: String = s"${getName}Terminator"
    override def kill(): Unit = synchronized{
      Out.info(s"$name was killed.")
      scedKill = true
      finish = true
      notifyAll()
    }
  }
}
