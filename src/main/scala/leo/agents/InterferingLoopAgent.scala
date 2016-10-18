package leo.agents
import leo.datastructures.blackboard._

/**
  *
  * An Agent that performs a loop like statement,
  * where the loop body is an atomar transaction.
  *
  * INIT
  *
  * while(BEDINGUNG):
  *   LOOP
  * end
  *
  * Will be translated into :
  *   FILTER --> (First Time)
  *     INIT
  *
  *   FILTER -->
  *   if (BEDINGUNG)
  *     submit(LOOP)
  *
  *   RUN -->
  *     LOOP
  *     send(newRound, this)
  *     insert results
  *
  *
  * The agent can use all datastructures in the blackboard (even during loop).
  * For consistency it is adviced to obtain and flag the used data after the filter phase.
  *
  */
class InterferingLoopAgent[A <: OperationState] (loop : InterferingLoop[A]) extends Agent {
  override val name: String = loop.name
  override val maxParTasks : Option[Int] = Some(1)
  private val self = this

  private var taskExisting = false
  private var firstAttempt = true // Used to not trigger the [[NextIteration]] every time
  private var active : Boolean = true


  /**
    * Searches the Blackboard for possible tasks on initialization.
    *
    * @return All initial available tasks
    */
  override def init(): Iterable[Task] = {
    firstAttempt = false
    val r = loop.canApply.toList.map(op => new InterferringLoopTask(op))
    if(r.isEmpty){
      active = false
    } else if (r.nonEmpty) {
      active = true
      ActiveTracker.incAndGet(s"$name: Loop condition initially positive.")
    }
    taskExisting = r.nonEmpty
    if(loop.terminated) unregister()
    r
  }

  override def register() : Unit = {
    super.register()
  }

  /**
    * This method should be called, whenever a formula is added to the blackboard.
    *
    * The filter then checks the blackboard if it can generate tasks from it,
    * that will be stored in the Agent.
    *
    * @param event - Newly added or updated formula
    */
  override def filter(event: Event): Iterable[Task] = synchronized(event match {
    case NextIteration if !taskExisting && firstAttempt =>    // Case of no real result
      firstAttempt = false
      val r = loop.canApply.toList.map(op => new InterferringLoopTask(op))
      if(r.isEmpty && active){
        active = false
        ActiveTracker.decAndGet(s"$name: Loop condition turned negative.")
      } else if (r.nonEmpty && !active) {
        active = true
        ActiveTracker.incAndGet(s"$name: Loop condition turned positive.")
      }
      taskExisting = r.nonEmpty
      if(loop.terminated) unregister()
      r
    case _ if !taskExisting =>                // Case of a cancel and no other possible match
      val r = loop.canApply.toList.map(op => new InterferringLoopTask(op))
      if(r.isEmpty && active){
        active = false
        ActiveTracker.decAndGet(s"$name: Loop condition turned negative.")
      } else if (r.nonEmpty && !active) {
        active = true
        ActiveTracker.incAndGet(s"$name: Loop condition turned positive.")
      }
      firstAttempt = false    // Race condition
      taskExisting = r.nonEmpty
      if(loop.terminated) unregister()
      r
    case _ => Nil
  })

  /**
    * Declares the agents interest in specific data.
    *
    * @return None -> The Agent does not register for any data changes. <br />
    *         Some(Nil) -> The agent registers for all data changes. <br />
    *         Some(xs) -> The agent registers only for data changes for any type in xs.
    */
  override def interest: Option[Seq[DataType]] = Some(Nil)

  /**
    * Each task can define a maximum amount of money, they
    * want to posses.
    *
    * A process has to be careful with this barrier, for he
    * may never be doing anything if he has to low money.
    *???
    * @return maxMoney
    */
  override def maxMoney: Double = 10000

  override def taskChoosen(t: Task): Unit = {}

  /**
    * <p>
    * This method is called after a task is run and
    * all filter where applied sucessfully
    * </p>
    * <p>
    * Triggers the next iteration of the filter.
    * </p>
    *
    * @param t The comletely finished task
    */
  override def taskFinished(t: Task): Unit = synchronized{
//    println(s"Task finished ${t.pretty}")
    taskExisting = false
    firstAttempt = true
    Blackboard().send(NextIteration, this)
  }


  /**
    * This method is called, whenever the program is forcefully stopped.
    * It has to be implemented to reset internal stati or the agent cannot simply be terminated.
    */
  override def kill(): Unit = {}

  /**
    * Method called, when a task cannot be executed
    * and is removed from the task set.
    *
    * @param t
    */
  override def taskCanceled(t: Task): Unit = synchronized{
    taskExisting = false
  }

  class InterferringLoopTask(opState : A) extends Task {
    override lazy val name: String = loop.name+ s"(${opState.toString})" + "-task"
    override lazy val getAgent: Agent = self
    override lazy val writeSet : Map[DataType, Set[Any]] = opState.write
    override lazy val readSet : Map[DataType, Set[Any]] = opState.read
    override def run: Result = loop(opState)
    override def bid: Double = 1    // Since we only need one task, we can spend all our money

    override lazy val pretty: String = name
  }

  case object NextIteration extends Message
}

/**
  * Allows the execution of an infering loop statement
  */
trait InterferingLoop[A <: OperationState] {
  def name : String

  /**
    * Is set to true, if the loop should not be executed further
    * @return
    */
  def terminated : Boolean = false

  /**
    * Is executed the first time a
    * @return
    */
  def init : Option[A]

  /**
    * Checks for applicability of the loop.
    *
    * @return None, if the loop is not applicable, Some(x) if it is applicable where
    *          x is the state snapshop for the loop to work on.
    */
  def canApply : Option[A]

  /**
    *
    * Application of the loop body based on the snapshop [opState].
    *
    * @param opState Snapshot of the current data
    * @return A Result to insert into the blackboard.
    */
  def apply(opState : A) : Result
}

/**
  * Abstract state for a loop body.
  * Generated by the filter/canApply and used in the loop body/apply
  */
trait OperationState {
  /**
    * Set of datatypes the loop works on
    * @return All touched datatypes
    */
  def datatypes : Iterable[DataType]

  /**
    * The set of data the method looks at.
    * @param ty Type of the data to look at
    * @return Set of data the operation reads
    */
  def readData(ty : DataType) : Set[Any]

  /**
    * Set of data the method wishes to write.
    * @param ty Type of the data the operation writes
    * @return Set of data the operation writes
    */
  def writeData(ty : DataType) : Set[Any]

  private[agents] final lazy val read : Map[DataType, Set[Any]] = datatypes.map(d => (d,readData(d))).filter(_._2.nonEmpty).toMap
  private[agents] final lazy val write : Map[DataType, Set[Any]] = datatypes.map(d => (d,writeData(d))).filter(_._2.nonEmpty).toMap
}
