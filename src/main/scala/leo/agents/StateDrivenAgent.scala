package leo.agents

import leo.datastructures.blackboard.{ActiveTracker, DataType, Event}

/**
 * <p>
 * A state driven agent orients itself more on a classical loop,
 * than on the filter based mechanism of the [[FifoController]]
 * and [[PriorityController]]. The selection is based on an existing
 * datastructure in the blackboard and will hence produce tasks only
 * on demand.
 * </p>
 *
 * <p>
 * An agent implemented with this trait can either be sequential
 * [[setMaxTasks(0)]] or concurrent.
 * </p>
 *
 * @author Max Wisniewski
 * @since 10/19/2015
 */
abstract class StateDrivenAgent extends TAgent{
  private var MAX_WORK : Option[Int] = None
  def setMaxTasks(m : Int) : Unit = {MAX_WORK = Some(m)}
  private var toActiveTracker = false


  /**
   * Independent of the event the state driven agent will not
   * react.
   *
   * //TODO a notifier to look next time, could be implemented
   */
  override def filter(event: Event): Unit = {}
  override def clearTasks(): Unit = {}
  override def hasTasks: Boolean = openTasks > 0
  override def removeColliding(nExec: Iterable[Task]): Unit = {}
  override val interest: Option[Seq[DataType]] = Some(Nil)
  override val maxMoney : Double = 50000

  /*
        Implementation for the StateDrivenAgent
   */
  private var workTasks : Set[Task] = Set()

  /**
   * As getTasks with an infinite budget.
   *
   * @return - All Tasks that the current agent wants to execute.
   */
  override def getAllTasks: Iterable[Task] = synchronized{
    workTasks.toList // No one should alter the internal work
  }

  override def taskChoosen(t : Task) : Unit = synchronized{
    workTasks += t
    ActiveTracker.incAndGet()
  }

  override def taskFinished(t : Task) : Unit = synchronized{
    workTasks -= t
  }

  override def register(): Unit = {
    super.register()
  }

  override def unregister() : Unit = {
    super.unregister()
  }

  /**
   * Searches for applicable tasks.
   * The sum of the weighted costs should not be higher, than 1
   *
   * @return A sequence of tasks, the agent wants to execute.
   */
  protected def searchTasks : Iterable[Task]

  private def activeStatus(openTasks : Iterable[Task]) : Unit = {
    if(!toActiveTracker && (openTasks.nonEmpty || workTasks.nonEmpty)) {
      toActiveTracker = true
      ActiveTracker.incAndGet()
    }
    if(toActiveTracker && openTasks.isEmpty && workTasks.isEmpty) {
      toActiveTracker = false
      ActiveTracker.decAndGet()
    }
  }

  override def getTasks(budget: Double): Iterable[Task] = {
    val ts = searchTasks
    activeStatus(ts)
    MAX_WORK match {
      case None => searchTasks
      case Some(t) => searchTasks.filterNot(workTasks.contains(_)).take(t - workTasks.size)
    }
  }
}
