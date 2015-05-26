package leo.modules.phase

import leo.agents.impl._
import leo.agents.{PriorityController, FifoController, AgentController}
import leo.modules.normalization._
import leo.modules.calculus._
import leo.modules.calculus.splitting.ClauseHornSplit

object PreprocessPhase extends CompletePhase {
  override val name = "PreprocessPhase"
  override protected val agents: Seq[AgentController] = List(new FifoController(new NormalClauseAgent(DefExpansion)), new FifoController(new NormalClauseAgent(Simplification)), new FifoController(new NormalClauseAgent(NegationNormal)),new FifoController(new NormalClauseAgent(Skolemization)), new FifoController(new NormalClauseAgent(PrenexNormal)), new FifoController(MetaVarAgent))
}

object SimplificationPhase extends CompletePhase {
  override val name = "PreprocessPhase"
  override protected val agents: Seq[AgentController] = List(new FifoController(new NormalClauseAgent(DefExpansion)), new FifoController(new NormalClauseAgent(Simplification)))
}

object ExhaustiveClausificationPhase extends CompletePhase {
  override val name = "ClausificationPhase"
  override protected val agents : Seq[AgentController] = List(new FifoController(ClausificationAgent))
}

object SplitPhase extends CompletePhase {
  override val name = "SplitPhase"
  override protected val agents: Seq[AgentController] = List(new FifoController(new SplittingAgent(ClauseHornSplit)))
}

object ParamodPhase extends CompletePhase {
  override val name : String = "ParamodPhase"
  override protected val agents: Seq[AgentController] = List(new PriorityController(new NewParamodAgent(NewParamod)),
    new PriorityController(FormulaSelectionAgent),
    new PriorityController(new NewParamodAgent(NewPropParamod)),
    new PriorityController(UnificationAgent)
    /*new PriorityController(new ParamodulationAgent(Paramodulation, IdComparison)), new PriorityController(new ParamodulationAgent(PropParamodulation, IdComparison))*/,
    new PriorityController(ClausificationAgent),
    new PriorityController(PrimSubstAgent),
    new PriorityController(RestrFactAgent),
    new PriorityController(BoolExtAgent),
    new PriorityController(FuncExtAgent))
}

