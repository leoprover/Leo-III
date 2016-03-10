
package leo.modules.phase

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.TagName
import leo._
import leo.agents.{TAgent, AgentController}
import leo.datastructures.ClauseAnnotation.{InferredFrom, FromFile}
import leo.datastructures._
import leo.datastructures.blackboard.{ClauseType, Blackboard, Store}
import leo.datastructures.blackboard.impl.{SZSDataStore}
import leo.datastructures.context.Context
import leo.modules.calculus.CalculusRule
import leo.modules.output.{SZS_Theorem, SZS_Error}
import leo.modules.{Parsing, SZSException}

class LoadPhase(problemfile: String = Configuration.PROBLEMFILE, negateConjecture : Boolean = true) extends Phase{
  override val name = "LoadPhase"

  override val agents : Seq[TAgent] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val file = problemfile
    try {
      val it : Iterator[(String, Term, Role)] = Parsing.parseProblem(file).toIterator
      var clauses = List[ClauseProxy]()
      val context = Context()
      var conjecture : Option[String]= None
      while(it.hasNext) {
        val (name, term, role) = it.next()
        if (role != Role_Type) {
          val c = Store(name, Clause(Literal(term, true)), role, context, FromFile(file, name))
          if (role == Role_Conjecture && negateConjecture) {
            if (conjecture.nonEmpty) {
              throw new SZSException(SZS_Error, s"Two conjectures in the problem :\n  1st -> ${conjecture.getOrElse("Missing")}\n  2nd -> $name")
            }
            conjecture = Some(name)
            val c1 = Store(name, Clause(Literal(term, false)), Role_NegConjecture, context, InferredFrom(NegateConjecture, c))
            Blackboard().addData(ClauseType)((c1, context))
          } else {
            Blackboard().addData(ClauseType)((c, context))
          }
        }
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

object NegateConjecture extends CalculusRule {
  override def name: String = "negate_conjecture"
  override val inferenceStatus = Some(SZS_Theorem)
}
