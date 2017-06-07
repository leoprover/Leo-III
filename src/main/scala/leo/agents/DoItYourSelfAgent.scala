package leo.agents

import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard._
import leo.modules.output.SZS_Error
import leo.modules.agent.rules.TypedSet
import leo.modules.{GeneralState, SZSException}


case class DoItYourSelfMessage[T <: GeneralState[AnnotatedClause]](s : T) extends Message

case object OpenState extends DataType[GeneralState[AnnotatedClause]]{
  override def convert(d: Any): GeneralState[AnnotatedClause] = d match {
    case s : GeneralState[AnnotatedClause] => s
    case _ => throw new SZSException(SZS_Error, s"[OpenState] : Tried to cast to GeneralState :\n  $d")
  }
}

case object CompletedState extends DataType[GeneralState[AnnotatedClause]]{
  override def convert(d: Any): GeneralState[AnnotatedClause] = d match {
    case s : GeneralState[AnnotatedClause] => s
    case _ => throw new SZSException(SZS_Error, s"[CompleteState] : Tried to cast to GeneralState :\n  $d")
  }
}

/**
  * An Agent to run on its own an return only its final result (hopefully [])
  * and the SZS status.
  */
class DoItYourSelfAgent[S <: GeneralState[AnnotatedClause]]
  (val procedure : ProofProcedure[S],
  stateSpace : Option[TypedSet[DataType[S]]] = None) extends AbstractAgent{
  override def name: String = procedure.name
  override val interest : Option[Seq[DataType[Any]]] = Some(Seq(OpenState))
  private final val self = this


  override def init(): Iterable[Task] = {
    stateSpace match {
      case None => Iterable()
      case Some(states) =>
        val stateIt = states.get(OpenState).iterator
        var tasks : Seq[Task] = Seq()
        while(stateIt.hasNext){
          stateIt.next() match {
            case s : S => tasks = new DoItYourSelfTask(s) +: tasks
            case _ => ()
          }
        }
        tasks
    }
  }

  /**
    * This method should be called, whenever a formula is added to the blackboard.
    *
    * The filter then checks the blackboard if it can generate tasks from it,
    * that will be stored in the Agent.
    *
    * @param event - Newly added or updated formula
    */
  override def filter(event: Event): Iterable[Task] = event match {
    case DoItYourSelfMessage(s : S) =>
        Seq(new DoItYourSelfTask(s))
    case r : Delta =>
      val newstates = r.inserts(OpenState).iterator
      var tasks : Seq[Task] = Nil
      while(newstates.hasNext){
        newstates.next() match {
          case s : S => tasks = new DoItYourSelfTask(s) +: tasks
          case _ => ()
        }
      }
      tasks
    case _ => Iterable()
  }

  /**
    * Method called, when a task cannot be executed
    * and is removed from the task set.
    *
    * @param t
    */
  override def taskCanceled(t: Task): Unit = {}

  class DoItYourSelfTask(state : S) extends Task{
    override val name: String = self.procedure.name+"Task"
    override val getAgent: Agent = self
    override val writeSet: Map[DataType[Any], Set[Any]] = Map.empty
    override val readSet: Map[DataType[Any], Set[Any]] = Map.empty
    override def run: Delta = {
      val resState = self.procedure.execute(state)
      // TODO Filter the result?
      val r = Result()
      r.remove(OpenState)(state)
      r.insert(CompletedState)(resState)
    }
    override def bid: Double = 0.3

    override val pretty: String = self.procedure.name
  }
}


/**
  * A proof procedure, that can be executed by it self and not
  * colaborating the blackboard.
  */
trait ProofProcedure[T <: GeneralState[AnnotatedClause]] {
  def name : String

  /**
    * Executes a sequential proof procedure.
    * Upon return updates the blackboard with
    * <ol>
    *   <li>The SZS status</li>
    *   <li>The remaining proof obligations.<br />The obligation should contain the empty clause, if a proof was found</li>
    * </ol>
    *
    * @param state The set of formulas we want to proof a contradiction.
    * @return The SZS status and optinally the remaing proof obligations. In the case of a sucessfull proof the empty
    *         clause should be returned (containing the proof).
    */
  def execute(state : T) : T
}
