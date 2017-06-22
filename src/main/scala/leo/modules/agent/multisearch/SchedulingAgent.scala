package leo.modules.agent.multisearch

import leo.Configuration
import leo.agents._
import leo.datastructures.AnnotatedClause
import leo.datastructures.blackboard.{DataType, Delta, Event, ImmutableDelta}
import leo.modules.GeneralState
import leo.modules.output.SZS_Theorem
import leo.modules.prover.RunStrategy

/**
  * Created by mwisnie on 6/7/17.
  */
class SchedulingAgent[S <: GeneralState[AnnotatedClause]](initState : S, tactic : Iterator[RunStrategy]) extends AbstractAgent {
  private val self = this
  override def name: String = "scheduling-agent"
  private val maxPar = Configuration.PAR_SCHED
  private var curExec = 0 // TODO Sync

  private val startTime = System.currentTimeMillis()
  private val timeout = Configuration.TIMEOUT

  override def filter(event: Event): Iterable[Task] = event match {
    case d : Delta =>
      // TODO on the fly scheduling?
      val ins = d.inserts(CompletedState)
      if(ins.nonEmpty){
        leo.Out.info(s"Completed\n   ${d.inserts(CompletedState).map{case s =>
          s"${s.runStrategy.pretty} with szs = ${s.szsStatus.pretty}"
        }.mkString(",\n   ")}")

        curExec -= ins.size
        if(!ins.exists(_.szsStatus == SZS_Theorem)) {
          generateNewRuns()
        }
        else {
          Iterable()
        }
      } else {
        Iterable()
      }
    case _ => Iterable()
  }

  override def init(): Iterable[Task] = {
    generateNewRuns()
  }

  private def generateNewRuns() : Iterable[Task] = {
    var tasks : Seq[Task] = Seq()
    val it = tactic
    val remaining : Int = timeout - ((System.currentTimeMillis() - startTime).toInt / 1000)
    while(it.hasNext && curExec < maxPar){
      curExec += 1
      val strat = it.next()
      val newState : S = initState.copyGeneral.asInstanceOf[S]
      val timedTactic =  new RunStrategy(remaining, strat.primSubst, strat.sos, strat.unifierCount, strat.uniDepth, strat.boolExt, strat.choice)
      // Time!
//      println(s"Commit ${timedTactic.pretty}")
      newState.setRunStrategy(timedTactic)
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
