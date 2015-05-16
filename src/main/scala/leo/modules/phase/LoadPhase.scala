package leo.modules.phase

import leo._
import leo.agents.impl.ConjectureAgent
import leo.agents.{Task, Agent, FifoController, AgentController}
import leo.datastructures.blackboard.{Result, DoneEvent, Event}
import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.modules.output.SZS_Error
import leo.modules.{SZSException, Utility}

class LoadPhase(negateConjecture : Boolean, problemfile: String = Configuration.PROBLEMFILE) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[AgentController] = if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = problemfile
    val wait = new FifoController(new Wait(this))

    if(negateConjecture) {
      init()
      Scheduler().signal()
    }
    try {
      Utility.load(file)
    } catch {
      case e : SZSException =>
        // Out.output(SZSOutput(e.status))
        SZSDataStore.forceStatus(Context())(e.status)
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception")
        e.printStackTrace()
        SZSDataStore.forceStatus(Context())(SZS_Error)
        //Out.output((SZSOutput(SZS_Error)))
        return false
    }
    if(negateConjecture) {
      wait.register()
      Scheduler().signal()
      synchronized {
        while (!finish) this.wait()
      }


      end()
      wait.unregister()
    }
    return true
  }

  private class Wait(lock : AnyRef) extends Agent{
    override def toFilter(event: Event): Iterable[Task] = event match {
      case d : DoneEvent => finish = true; lock.synchronized(lock.notifyAll());List()
      case _ => List()
    }
    override def name: String = "PreprocessPhaseTerminator"
    override def run(t: Task): Result = Result()
  }
}