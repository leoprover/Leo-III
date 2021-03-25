package leo.modules.phase

import leo.agents.Agent
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.modules.agent.relevance_filter.RelevanceFilterAgent

/**
  * Created by mwisnie on 3/10/16.
  */
class FilterPhase(blackboard: Blackboard, scheduler: Scheduler) extends CompletePhase(blackboard, scheduler, _ => false) {
  override def name: String = "relevance_filter_phase"
  override protected val agents: Seq[Agent] = Seq(RelevanceFilterAgent)
}
