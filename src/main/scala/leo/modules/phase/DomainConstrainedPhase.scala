package leo.modules.phase

import leo.Configuration
import leo.agents.{AgentController, FifoController}
import leo.agents.impl.{DomainConstrainedMessage, DomainConstrainedSplitAgent}
import leo.datastructures.blackboard.Blackboard


/**
 * Calls the domain constrained agent on the cardinality passed to the configuration.
 */
object DomainConstrainedPhase extends CompletePhase{
  override val name = "DomainConstrainedPhase"

  val da = new FifoController(new DomainConstrainedSplitAgent)

  override val agents : Seq[AgentController] = List(da)

  var finish : Boolean = false

  override def execute(): Boolean = {
    init()


    val card : Seq[Int] = Configuration.valueOf("card").fold(List(1,2,3)){s => try{(s map {c => c.toInt}).toList} catch {case _:Throwable => List(1,2,3)}}



    Blackboard().send(DomainConstrainedMessage(card),da)

    initWait()

    if(!waitTillEnd()) return false

    end()
    return true
  }
}
