package leo.modules.phase

import leo.agents.TAgent

/**
  * Created by mwisnie on 3/7/16.
  */
class PreprocessingPhase extends CompletePhase{
override def name: String = "PreProcessing Phase"
  override protected def agents: Seq[TAgent] = Seq()
}
