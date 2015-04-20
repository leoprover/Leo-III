package leo.modules.phase

import leo.Configuration
import leo.agents.impl.{SZSScriptMessage, SZSScriptAgent}
import leo.agents.{FifoController, AgentController}
import leo.datastructures.Role_NegConjecture
import leo.datastructures.blackboard.Blackboard
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.modules.SZSException
import leo.modules.output.{SZS_Error, SZS_UsageError}

object ExternalProverPhase extends CompletePhase {
  override def name: String = "ExternalProverPhase"

  lazy val prover = if (Configuration.isSet("with-prover")) {
    Configuration.valueOf("with-prover") match {
      case None => throw new SZSException(SZS_UsageError, "--with-prover parameter used without <prover> argument.")
      case Some(str) => str.head match {
        case "leo2" => {
          val path = System.getenv("LEO2_PATH")
          if (path != null) {
            "scripts/leoexec.sh"
          } else {
            throw new SZSException(SZS_UsageError, "--with-prover used with LEO2 prover, but $LEO2_PATH is not set.")
          }
        }
        case "satallax" => {
          val path = System.getenv("SATALLAX_PATH")
          if (path != null) {
            "scripts/satallaxexec.sh"
          } else {
            throw new SZSException(SZS_UsageError, "--with-prover used with satallax prover, but $SATALLAX_PATH is not set.")
          }
        }
        case "remote-leo2" => "scripts/remote-leoexec.sh"
        case _ => throw new SZSException(SZS_UsageError, "--with-prover parameter used with unrecognized <prover> argument.")
      }
    }
  } else {
    throw new SZSException(SZS_Error, "This is considered an system error, please report this problem.", "CL parameter with-prover lost")
  }

  lazy val extProver : AgentController = new FifoController(SZSScriptAgent(prover)(x => x))


  override protected def agents: Seq[AgentController] = List(extProver)

  override def execute(): Boolean = {
    init()


    val conj = FormulaDataStore.getAll(_.role == Role_NegConjecture).head
    Blackboard().send(SZSScriptMessage(conj)(conj.context), extProver)

    initWait()

    if(!waitTillEnd()) return false

    end()
    return true


  }
}
