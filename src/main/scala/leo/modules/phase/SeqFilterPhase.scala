package leo.modules.phase

import leo._
import leo.agents.Agent
import leo.datastructures.ClauseAnnotation.{FromFile, InferredFrom}
import leo.datastructures.{ClauseAnnotation, Literal, _}
import leo.datastructures.blackboard.{Blackboard, ClauseType, SignatureBlackboard}
import leo.datastructures.blackboard.scheduler.Scheduler
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.calculus.CalculusRule
import leo.modules.output.SZS_CounterTheorem
import leo.modules.parsers.Input.processFormula
import leo.modules.relevance_filter.{PreFilterSet, RelevanceFilter}

/**
  * Created by mwisnie on 3/10/16.
  */
class SeqFilterPhase(blackboard: Blackboard, scheduler: Scheduler) extends Phase(blackboard, scheduler) {
  override def name: String = "relevance_filter_phase"
  override val agents : Seq[Agent] = Nil // if(negateConjecture) List(new FifoController(new ConjectureAgent)) else Nil

  var finish : Boolean = false

  override def execute(): Boolean = {
    val f = scheduler.submitIndependent(new FilterRun)

    f.get()
    !scheduler.isTerminated
  }

  private class FilterRun extends Runnable {

    override def run(): Unit = try {
      var res : Seq[ClauseProxy] = Seq()
      var taken : Iterable[AnnotatedFormula] = Seq()

      taken = PreFilterSet.getFormulas.filter{f => f.role == Role_Conjecture.pretty || f.role == Role_NegConjecture.pretty}

      var round : Int = 0
      while(taken.nonEmpty){

        // Take all formulas (save the newly touched symbols
        val newsymbs : Iterable[String] = taken.flatMap(f => PreFilterSet.useFormula(f))

        // Translate all taken formulas to clauses
        taken.foreach{f =>
          val (name, term, role) = processFormula(f)(SignatureBlackboard.get)
          val nc : ClauseProxy = if(f.role == Role_Conjecture.pretty || f.role == Role_NegConjecture.pretty)
            negateConjecture(name, term, role)
          else
            AnnotatedClause(Clause(Literal(term, true)), role, FromFile(Configuration.PROBLEMFILE, name), ClauseAnnotation.PropNoProp)
          res = nc +: res
        }

        // Obtain all formulas, that have a
        val possibleCandidates : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(newsymbs)

        // Take the new formulas
        taken = possibleCandidates.filter(f => RelevanceFilter(round)(f))

        round += 1
      }

      res foreach {form => blackboard.addData(ClauseType)(form) }
    } catch {
      case e : ThreadDeath => return
      case _ : Throwable => return
    }
  }

  private def negateConjecture(name : String, term : Term, role : Role) : ClauseProxy = {
    val org = AnnotatedClause(Clause(Literal(term, true)), role, FromFile(Configuration.PROBLEMFILE, name), ClauseAnnotation.PropNoProp)
    AnnotatedClause(Clause(Literal(term, false)), Role_NegConjecture, InferredFrom(NegateConjecture, org), ClauseAnnotation.PropNoProp)
  }
}

object NegateConjecture extends CalculusRule {
  final val name: String = "neg_conjecture"
  final val inferenceStatus = SZS_CounterTheorem
}
