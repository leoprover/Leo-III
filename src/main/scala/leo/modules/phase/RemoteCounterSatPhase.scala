package leo.modules.phase

import leo.agents.impl.{SZSScriptMessage, SZSScriptAgent}
import leo.agents.{FifoController, AgentController}
import leo.datastructures.{Role_Plain, Role_Conjecture}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.blackboard.{Store, Blackboard, FormulaStore}
import leo.datastructures.blackboard.impl.FormulaDataStore
import leo.datastructures.context.Context
import leo.modules.output.{SZS_CounterSatisfiable, SZS_Theorem, StatusSZS}

/**
 * Invokes external scripts if the context was split previoulsy.
 */
object RemoteCounterSatPhase extends CompletePhase {
  override def name: String = "RemoteCounterSatPhase"

  val da : AgentController = new FifoController(SZSScriptAgent("scripts/leoexec.sh")(reInt))

  override protected def agents: Seq[AgentController] = List(da)

  private def reInt(in : StatusSZS) : StatusSZS = in match {
    case SZS_Theorem => SZS_CounterSatisfiable    // TODO Sat -> Countersat
    case e => e
  }
  var finish : Boolean = false

  override def execute(): Boolean = {
    init()


    //val maxCard = Configuration.valueOf("maxCard").fold(3){s => try{s.head.toInt} catch {case _ => 3}}

    // Send all messages
    val it = Context().childContext.iterator
    var con : FormulaStore = null
    try {
      con = FormulaDataStore.getAll(_.role == Role_Conjecture).head
    } catch {
      case _: Throwable => end(); return false
    }
    while(it.hasNext) {
      val c = it.next()
      Blackboard().send(SZSScriptMessage(Store(con.clause, Role_Plain, c, con.status))(c), da)
    }

    initWait()

    Scheduler().signal()
    synchronized{while(!finish) this.wait()}

    end()
    return true
  }
}

