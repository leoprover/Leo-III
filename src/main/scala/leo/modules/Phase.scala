package leo
package modules

import leo.agents.{EmptyResult, Result, Task, FifoAgent, Agent}
import leo.agents.impl._
import leo.datastructures._
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard._
import leo.datastructures.context.{BetaSplit, Context}
import leo.datastructures.impl.Signature
import leo.modules.normalization.{NegationNormal, Skolemization, Simplification, DefExpansion}
import leo.modules.output._
import leo.modules.proofCalculi.enumeration.SimpleEnum
import leo.modules.proofCalculi.splitting.ClauseHornSplit
import leo.modules.proofCalculi.{PropParamodulation, IdComparison, Paramodulation}
import leo.agents.impl.FiniteHerbrandEnumerateAgent
import leo.datastructures.term.Term


object Phase {
  def getStdPhases : Seq[Phase] = List(new LoadPhase(true), SimplificationPhase, ParamodPhase)
  def getHOStdPhase : Seq[Phase] = List(new LoadPhase(true), PreprocessPhase, SimpleEnumerationPhase, ParamodPhase)
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
  }
  /**
   * Executes all defined agents and waits till no work is left.
   */
  override def execute() : Boolean = {
    // Starting all agents and signal scheduler
    init()
    initWait()
    Scheduler().signal()
    // Wait until nothing is left to do
    waitAgent.synchronized(while(!waitAgent.finish) waitAgent.wait())
    if(waitAgent.scedKill) return false
    // Ending all agents and clear the scheduler
    end()

    // If executing till the end, we will always return true, if other behaviour is wished, it has to be implemented
    return true
  }

  protected class CompleteWait extends FifoAgent{
    var finish = false
    var scedKill = false
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent =>
        synchronized{finish = true; notifyAll()};List()
      case StatusEvent(c,s) if c.parentContext == null && c.isClosed => // The root context was closed
        synchronized{finish = true; notifyAll()};List()
      case _ => List()
    }
    override def name: String = s"${getName}Terminator"
    override def run(t: Task): Result = EmptyResult
    override def kill(): Unit = synchronized{
      scedKill = true
      finish = true
      notifyAll()
    }
  }
}


class LoadPhase(negateConjecture : Boolean, problemfile: String = Configuration.PROBLEMFILE) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[Agent] = if(negateConjecture) List(new ConjectureAgent) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = problemfile
    val wait = new Wait(this)

    if(negateConjecture) {
      init()
      wait.register()
      Scheduler().signal()
    }
    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        // Out.output(SZSOutput(e.status))
        Blackboard().forceStatus(Context())(e.status)
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception")
        e.printStackTrace()
        Blackboard().forceStatus(Context())(SZS_Error)
        //Out.output((SZSOutput(SZS_Error)))
        return false
    }
    if(negateConjecture) {
      Scheduler().signal()
      synchronized {
        while (!finish) this.wait()
      }


      end()
      wait.unregister()
    }
    return true
  }

  private class Wait(lock : AnyRef) extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; lock.synchronized(lock.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}


object DomainConstrainedPhase extends Phase{
  override val name = "DomainConstrainedPhase"

  val da = new DomainConstrainedSplitAgent

  override val agents : Seq[Agent] = List(da)

  var finish : Boolean = false

  override def execute(): Boolean = {
    init()


    val card : Seq[Int] = Configuration.valueOf("card").fold(List(1,2,3)){s => try{s map {c => c.toInt} toList} catch {case _ => List(1,2,3)}}



    Blackboard().send(DomainConstrainedMessage(card),da)

    Wait.register()

    Scheduler().signal()
    synchronized{while(!finish) this.wait()}


    end()
    Wait.unregister()
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; DomainConstrainedPhase.synchronized(DomainConstrainedPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}

object SimpleEnumerationPhase extends Phase {
  override val name = "SimpleEnumerationPhase"
  var finish = false
  var scKilled = false
  override lazy val description = "Agents used:\n    FiniteHerbrandEnumerationAgent"

  protected var agents: Seq[Agent] = List(new FiniteHerbrandEnumerateAgent(Context(), Map.empty))

  override def execute(): Boolean = {
    val s1 : Set[Type] = (Signature.get.baseTypes - 0 - 1 - 3 - 4 - 5).map(Type.mkType(_))
    val enumse : Map[Type, Seq[Term]] = s1.map{ty => (ty, SimpleEnum.enum(ty).toSeq)}.toMap
    Out.finest(enumse.toString())
    agents = List(new FiniteHerbrandEnumerateAgent(Context(), enumse))

    agents.map(_.register())

    Wait.register()

    Scheduler().signal()
    synchronized{while(!finish) this.wait()}
    if(scKilled) return false

    agents.map(_.unregister())
    Wait.unregister()

    // Remove all formulas containing one of the domains. (Hacky. Move the Test Function to the module package.
    val  a : FiniteHerbrandEnumerateAgent = agents.head.asInstanceOf[FiniteHerbrandEnumerateAgent]

    Blackboard().rmAll(Context()){f => f.clause.lits.exists{l => a.containsDomain(l.term)}}


    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; SimpleEnumerationPhase.synchronized(SimpleEnumerationPhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "SimpleEnumeratePhaseTerminator"
    override def run(t: Task): Result = EmptyResult
    override def kill(): Unit = SimpleEnumerationPhase.synchronized{
      scKilled = true
      finish = true
      SimpleEnumerationPhase.notifyAll()
    }
  }
}

object FiniteHerbrandEnumeratePhase extends Phase {
  override val name = "FiniteHerbrandEnumeratePhase"

  val size : Int = 3
  var finish : Boolean = false
  var scKilled = false
  override lazy val description = "Agents used:\n    FiniteHerbrandEnumerationAgent"

  /**
   * A list of all agents to be started.
   * @return
   */
  protected var agents: Seq[Agent] = List(new FiniteHerbrandEnumerateAgent(Context(), Map.empty)) // A bit of schmu, but I do not want to list the agents here

  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  override def execute(): Boolean = {
    if(!Context().split(BetaSplit, size)) {
      // Set context and reason???
      return false
    }

    agents = Nil

    val s1 : Set[Signature#Key] = Signature.get.baseTypes - 0 - 1 - 3 - 4 - 5// Without kind, numbers and boolean
    val s : Set[Type]= s1.map {k => Type.mkType(k)}

    var it : Int = 0

    val cs : Seq[Context] = Context().childContext.toList
    // Each context, assign the maximal number of elements per domain
    // Then generate teh new clauses and insert them into the blackboard.
    // If it is done build the agents from it.
    (1 to size).zip(cs).foreach { case (i,c1) =>
      // Generate and insert new constants
      val cons : Map[Type, Seq[Clause]] = s.map{ty => (ty, (1 to i).map{_ => newConstant(ty)}.toList)}.toMap

      //TODO Add some constraints?
      //cons.values.map(_.foreach{c => Blackboard().addFormula(s"domainConstrain_${c1.contextID}_${val s = it; it+=1; s}",c,Role_Axiom, c1)})

      // Generate an agent for this setting of domains
      val agent = new FiniteHerbrandEnumerateAgent(c1, cons.mapValues(_.map(_.lits.head.term)))
      agents = agent +: agents
    }

    agents.map(_.register())

    Wait.register()

    Scheduler().signal()
    synchronized{while(!finish) this.wait()}
    if(scKilled) return false

    agents.map(_.unregister())
    Wait.unregister()

    // Remove all formulas containing one of the domains. (Hacky. Move the Test Function to the module package.
    val  a : FiniteHerbrandEnumerateAgent = agents.head.asInstanceOf[FiniteHerbrandEnumerateAgent]

    Blackboard().rmAll(Context()){f => f.clause.lits.exists{l => a.containsDomain(l.term)}}

    return true
  }

  private def newConstant(ty : Type) : Clause = {
    val s = Signature.get
    return Clause.mkClause(List(Literal(s.freshSkolemVar(ty), true)), Derived)
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; FiniteHerbrandEnumeratePhase.synchronized(FiniteHerbrandEnumeratePhase.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
    override def kill(): Unit = FiniteHerbrandEnumeratePhase.synchronized{
      scKilled = true
      finish = true
      FiniteHerbrandEnumeratePhase.notifyAll()
    }
  }
}

object ExternalProverPhase extends CompletePhase {
  override def name: String = "ExternalProverPhase"

  def prover = if (Configuration.isSet("with-prover")) {
    Configuration.valueOf("with-prover") match {
      case None => throw new SZSException(SZS_UsageError, "--with-prover parameter used without <prover> argument.")
      case Some(str) => str.head match {
        case "leo2" => {
          val path = System.getenv("LEO2_PATH")
          if (path != null) {
            "scripts/leoexec.sh"
          } else {
            throw new SZSException(SZS_UsageError, "--with-prover used with LEO2 prover, but $LEO2_PATH is not set.")
          }
        }
        case "satallax" => {
          val path = System.getenv("SATALLAX_PATH")
          if (path != null) {
            "scripts/satallaxexec.sh"
          } else {
            throw new SZSException(SZS_UsageError, "--with-prover used with satallax prover, but $SATALLAX_PATH is not set.")
          }
        }
        case "remote-leo2" => "scripts/remote-leoexec.sh"
        case _ => throw new SZSException(SZS_UsageError, "--with-prover parameter used with unrecognized <prover> argument.")
      }
    }
  } else {
    throw new SZSException(SZS_Error, "This is considered an system error, please report this problem.", "CL parameter with-prover lost")
  }
  def extProver : Agent = SZSScriptAgent(prover)(x => x)

  override protected def agents: Seq[Agent] = List(extProver)

  override def execute(): Boolean = {
    init()


  val conj = Blackboard().getAll(_.role == Role_NegConjecture).head
  Blackboard().send(SZSScriptMessage(conj)(conj.context), extProver)

  initWait()

  Scheduler().signal()

  waitAgent.synchronized{while(!waitAgent.finish) {waitAgent.wait()}}
  if(waitAgent.scedKill) return false

  end()
  return true


  }
}

/**
 * Invokes external scripts if the context was split previoulsy.
 */
object RemoteCounterSatPhase extends CompletePhase {
  override def name: String = "RemoteCounterSatPhase"

  val da : Agent = SZSScriptAgent("scripts/leoexec.sh")(reInt)

  override protected def agents: Seq[Agent] = List(da)

  private def reInt(in : StatusSZS) : StatusSZS = in match {
    case SZS_Theorem => SZS_CounterSatisfiable    // TODO Sat -> Countersat
    case e => e
  }
  var finish : Boolean = false

  override def execute(): Boolean = {
    init()


    //val maxCard = Configuration.valueOf("maxCard").fold(3){s => try{s.head.toInt} catch {case _ => 3}}

    // Send all messages
    val it = Context().childContext.iterator
    var con : FormulaStore = null
    try {
      con = Blackboard().getAll(_.role == Role_Conjecture).head
    } catch {
      case _ => end(); return false
    }
    while(it.hasNext) {
      val c = it.next()
      Blackboard().send(SZSScriptMessage(con.newContext(c))(c), da)
    }
    Wait.register()

    Scheduler().signal()
    synchronized{while(!finish) this.wait()}

    end()
    Wait.unregister()
    return true
  }

  private object Wait extends FifoAgent{
    override protected def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; RemoteCounterSatPhase.synchronized(RemoteCounterSatPhase.notifyAll());List()
      case StatusEvent(c,s) if c.parentContext == null => finish = true; RemoteCounterSatPhase.synchronized(RemoteCounterSatPhase.notifyAll()); List()
      case _ => List()
    }
    override def name: String = "RemoteCounterSatPhaseTerminator"
    override def run(t: Task): Result = EmptyResult
  }
}

object PreprocessPhase extends CompletePhase {
  override val name = "PreprocessPhase"
  override protected val agents: Seq[Agent] = List(new NormalClauseAgent(DefExpansion), new NormalClauseAgent(Simplification), new NormalClauseAgent(NegationNormal),new NormalClauseAgent(Skolemization))
}

object SimplificationPhase extends CompletePhase {
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
