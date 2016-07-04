package leo.modules.phase

import leo.Configuration
import leo.agents.Agent
import leo.modules.agent.preprocessing.{FormulaRenamingAgent, EqualityReplaceAgent, NormalizationAgent, ArgumentExtractionAgent}

/**
  * Created by mwisnie on 3/7/16.
  */
class PreprocessingPhase(as : Seq[Agent]) extends CompletePhase{
override def name: String = "PreProcessing Phase"
  override protected val agents: Seq[Agent] = as
}
