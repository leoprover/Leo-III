package leo.modules.agent.multisearch

import leo.agents._
import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.{DataType, Delta, Event, ImmutableDelta}
import leo.modules.GeneralState
import leo.modules.prover.RunStrategy

/**
  * Created by mwisnie on 6/7/17.
  */
class SchedulingAgent[S <: GeneralState[AnnotatedClause]](initState : S, tactic : Iterator[RunStrategy]) extends AbstractAgent {
  private val self = this
  override def name: String = "scheduling-agent"

  override def filter(event: Event): Iterable[Task] = event match {
    case d : Delta =>
      // TODO on the fly scheduling?
      if(d.inserts(CompletedState).nonEmpty){
        leo.Out.info(s"Completed ${d.inserts(CompletedState).map{case s =>
          s"${s.runStrategy.pretty} with szs = ${s.szsStatus.pretty}"
        }.mkString(", ")}")
        // TODO Filter potential good SZSStati
      }
      Iterable()
    case _ => Iterable()
  }

  override def init(): Iterable[Task] = {
    var tasks : Seq[Task] = Seq()
    val it = tactic
    while(it.hasNext){
      val tactic = it.next()
      val newState : S = initState.copyGeneral.asInstanceOf[S]  // Casting not avoidable..
      newState.setRunStrategy(tactic)
      tasks = new NewModeTask(newState) +: tasks
    }
    tasks
  }

  class NewModeTask(state : S) extends Task {
    override val name: String = s"NewMode(${tactic.toString}"
    override val run: Delta = new ImmutableDelta(Map(OpenState -> Seq(state)))
    override val readSet: Map[DataType[Any], Set[Any]] = Map()
    override val writeSet: Map[DataType[Any], Set[Any]] = Map()
    override val bid: Double = 0.1
    override val getAgent: Agent = self
    override val pretty: String = name
  }
}
