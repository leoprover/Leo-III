
package leo.modules.phase

import java.nio.file.Files

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName
import leo._
import leo.agents.Agent
import leo.datastructures.ClauseAnnotation.{FromFile, InferredFrom}
import leo.datastructures._
import leo.datastructures.blackboard.{Blackboard, ClauseType}
import leo.datastructures.blackboard.impl.SZSDataStore
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.context.Context
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.agent.relevance_filter.AnnotatedFormulaType
import leo.modules.calculus.CalculusRule
import leo.modules.output.{SZS_Error, SZS_InputError, SZS_Theorem}
import leo.modules.{Parsing, SZSException}

class LoadPhase(problemfile: String = Configuration.PROBLEMFILE) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[Agent] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val run = new LoadRun
    val f = Scheduler().submitIndependent(run)
    f.get()
    run.ret_def && !Scheduler().isTerminated
  }

  private class LoadRun extends Runnable{

    var ret_def : Boolean = false

    override def run(): Unit = {
      val file = problemfile
      try {
        val prob = Configuration.PROBLEMFILE

        val it : Iterator[AnnotatedFormula] = {
          if (Files.exists(Parsing.canonicalPath(prob)))
            Parsing.readProblem(prob).iterator
          else {
            val tptpFile = Parsing.tptpHome.resolve(prob)
            if(Files.exists(tptpFile)) {
              Parsing.readProblem(Parsing.tptpHome.resolve(prob).toString).iterator
            } else {
              throw new SZSException(SZS_InputError, s"The file ${prob} does not exist.")
            }
          }
        }
        while(it.hasNext){
          val form = it.next()
          Blackboard().addData(AnnotatedFormulaType)(form)
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
