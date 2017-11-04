
package leo.modules.phase

import java.nio.file.Files

import leo._
import leo.agents.Agent
import leo.datastructures.blackboard.{Blackboard}
import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.agent.relevance_filter.AnnotatedFormulaType
import leo.modules.output.{SZS_Error, SZS_InputError}
import leo.modules.parsers.Input
import leo.modules.SZSException

class LoadPhase(problemfile: String = Configuration.PROBLEMFILE, blackboard: Blackboard, scheduler: Scheduler) extends Phase(blackboard, scheduler) {
  override val name = "LoadPhase"

  override val agents : Seq[Agent] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val run = new LoadRun
    val f = scheduler.submitIndependent(run)
    f.get()
    run.ret_def && !scheduler.isTerminated
  }

  private class LoadRun extends Runnable{

    var ret_def : Boolean = false

    override def run(): Unit = {
      val file = problemfile
      try {
        val prob = Configuration.PROBLEMFILE

        val it : Iterator[AnnotatedFormula] = {
          if (Files.exists(Input.canonicalPath(prob)))
            Input.parseProblemFile(prob).iterator
          else {
            val tptpFile = Input.tptpHome.resolve(prob)
            if(Files.exists(tptpFile)) {
              Input.parseProblemFile(Input.tptpHome.resolve(prob).toString).iterator
            } else {
              throw new SZSException(SZS_InputError, s"The file ${prob} does not exist.")
            }
          }
        }
        while(it.hasNext){
          val form = it.next()
          blackboard.addData(AnnotatedFormulaType)(form)
        }
      } catch {
        case e : SZSException =>
          SZSDataStore.forceStatus(e.status)
          Out.severe(e.getMessage)
          ret_def =  false
          return
        case e : ThreadDeath =>
          ret_def = false
          return
        case e : Throwable =>
          Out.severe("Unexpected Exception")
          e.printStackTrace()
          SZSDataStore.forceStatus(SZS_Error)
          ret_def =  false
          return
      }
      ret_def = true
    }
  }
}
