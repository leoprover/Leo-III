package leo.modules.phase

import leo.Configuration
import leo.agents.TAgent
import leo.modules.agent.preprocessing.{FormulaRenamingAgent, EqualityReplaceAgent, NormalizationAgent, ArgumentExtractionAgent}

/**
  * Created by mwisnie on 3/7/16.
  */
class PreprocessingPhase extends CompletePhase{
override def name: String = "PreProcessing Phase"
  override protected val agents: Seq[TAgent] = {
    var s : Seq[TAgent] = Seq()
    s = NormalizationAgent +: s
    s = EqualityReplaceAgent +: s
    if(Configuration.isSet("a"))
      s = ArgumentExtractionAgent +: s
    if(Configuration.isSet("r"))
      s = FormulaRenamingAgent +: s
    s
  }
}
