package leo.modules.agent.multisearch

import leo.agents.ProofProcedure
import leo.datastructures.AnnotatedClause
import leo.modules.prover.{SeqLoop, State}

/**
  * Created by mwisnie on 6/7/17.
  */
object SeqLoopLift extends ProofProcedure[State[AnnotatedClause]] {
  override def name: String = "SeqLoopLift"

  override def execute(state: State[AnnotatedClause]): State[AnnotatedClause] = {
    leo.Out.info(s"Started ${state.runStrategy.pretty} for ${state.timeout}s")
    state.fVIndex.reset()
    val start = System.currentTimeMillis()
    SeqLoop.run(state, state.initialProblem.toSeq, System.currentTimeMillis())
    leo.Out.info(s"Finshed ${state.runStrategy.pretty} after ${(System.currentTimeMillis() - start) / 1000}s")
    state
  }
}
