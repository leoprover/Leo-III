package leo
package modules

import leo.agents.impl.NormalClauseAgent
import leo.modules.output.SZS_Error


object Phase {
  def getStdPhases : Seq[Phase] = List(LoadPhase, PreprocessPhase)
}

/**
 * Trait for a MainPhase in Leo-III
 *
 * @author Max Wisniewski
 * @since 12/1/14
 */
trait Phase {
  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  def execute() : Boolean
}


object LoadPhase extends Phase{
  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  override def execute(): Boolean = {

    val file = Configuration.PROBLEMFILE

    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        Out.output(SZSOutput(e.status))
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception: "+e.getMessage)
        Out.output((SZSOutput(SZS_Error)))
        return false
    }
    return true
  }
}

object PreprocessPhase extends Phase {
  /**
   * Executes the Phase.
   *
   * @return true, if the phase was performed successful and the next phase is allowed to commence. false, otherwise
   */
  override def execute(): Boolean = {
    NormalClauseAgent.DefExpansionAgent()
    NormalClauseAgent.SimplificationAgent()
    NormalClauseAgent.NegationNormalAgent()

    return true
  }
}