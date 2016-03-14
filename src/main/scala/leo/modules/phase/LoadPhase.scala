
package leo.modules.phase

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName
import leo._
import leo.agents.{TAgent, AgentController}
import leo.datastructures.ClauseAnnotation.{InferredFrom, FromFile}
import leo.datastructures._
import leo.datastructures.blackboard.{ClauseType, Blackboard, Store}
import leo.datastructures.blackboard.impl.{SZSDataStore}
import leo.datastructures.context.Context
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.agent.relevance_filter.AnnotatedFormulaType
import leo.modules.calculus.CalculusRule
import leo.modules.output.{SZS_Theorem, SZS_Error}
import leo.modules.{Parsing, SZSException}

class LoadPhase(problemfile: String = Configuration.PROBLEMFILE) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[TAgent] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = problemfile
    try {
      val it : Iterator[AnnotatedFormula] = Parsing.readProblem(Configuration.PROBLEMFILE).iterator
      while(it.hasNext){
        val form = it.next()
        Blackboard().addData(AnnotatedFormulaType)(form)
      }
    } catch {
      case e : SZSException =>
        SZSDataStore.forceStatus(Context())(e.status)
        Out.severe(e.getMessage)
        return false
      case e : Throwable =>
        Out.severe("Unexpected Exception")
        e.printStackTrace()
        SZSDataStore.forceStatus(Context())(SZS_Error)
        return false
    }
    return true
  }
}
