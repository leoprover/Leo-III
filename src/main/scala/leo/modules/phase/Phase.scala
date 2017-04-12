
package leo
package modules.phase

import leo.agents._
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.SZSStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.interleavingproc.SZSStatus


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
  def apply(dname : String, dagents : Seq[Agent])(blackboard: Blackboard, scheduler: Scheduler): Phase = new CompletePhase(blackboard, scheduler) {
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
abstract class Phase(val blackboard: Blackboard, val scheduler : Scheduler) {
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
    agents.foreach{a => blackboard.registerAgent(a)}
  }

  /**
   * Method to finish the agents.
   */
  protected def end() : Unit = {
    scheduler.pause()
    agents.foreach(a => blackboard.unregisterAgent(a))
    scheduler.clear()
  }
}

/**
 * Abstract Phase, that implements
 * the execute to start the agents and wait for all to finish.
 */
abstract class CompletePhase(blackboard: Blackboard, scheduler: Scheduler) extends Phase(blackboard, scheduler) {
  private def getName = name
  protected var waitAgent : CompleteWait = null


  def initWait() : Unit = {
    waitAgent = new CompleteWait
    blackboard.registerAgent(waitAgent)
  }

  override def end() : Unit = {
    super.end()
    agents foreach {a => a.kill()}
    blackboard.unregisterAgent(waitAgent)
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
    scheduler.signal()
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
      agents foreach {a => a.kill()}
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
    override def interest : Option[Seq[DataType[Any]]] = Some(Seq(StatusType))
    @inline override val init: Iterable[Task] = Seq()
    override def filter(event: Event): Iterable[Task] = event match {
      case DoneEvent =>
        synchronized{finish = true; notifyAll()};List()
      case r : Delta =>
        if(r.inserts(StatusType).nonEmpty || r.updates(StatusType).nonEmpty || r.inserts(SZSStatus).nonEmpty || r.updates(SZSStatus).nonEmpty){
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
