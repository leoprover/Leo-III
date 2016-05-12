package leo.modules.agent.relevance_filter

import leo.Configuration
import leo.agents.{TAgent, Task, Agent}
import leo.datastructures.ClauseAnnotation.{InferredFrom, FromFile}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.calculus.CalculusRule
import leo.modules.output.SZS_Theorem
import leo.modules.parsers.InputProcessing
import leo.modules.relevance_filter.{PreFilterSet, RelevanceFilter}

/**
  * Applies the
  * [[leo.modules.relevance_filter.RelevanceFilter]]
  * to the set of prefilters.
  */
object RelevanceFilterAgent extends Agent {
  override def name: String = "relevance_filter_agent"
  override val interest : Option[Seq[DataType]] = Some(Seq(FormulaTakenType, AnnotatedFormulaType))

  /**
    * This method should be called, whenever a formula is added to the blackboard.
    *
    * The filter then checks the blackboard if it can generate tasks from it,
    * that will be stored in the Agent.
    *
    * @param event - Newly added or updated formula
    */
  override def filter(event: Event): Iterable[Task] = event match {
      // TODO define own factors and passmark
    case DataEvent(form : AnnotatedFormula, AnnotatedFormulaType)
      if form.role == Role_Conjecture.pretty || form.role == Role_NegConjecture.pretty || form.function_symbols.isEmpty =>  // Initially we takethe conjecture and prinzipels
        leo.Out.debug(s"$form : \n  ${if(form.function_symbols.isEmpty) "rule format" else "goal"}\n taken")
        Seq(new RelevanceTask(form, -1, this))
    case DataEvent((form : AnnotatedFormula, round : Int), FormulaTakenType) =>
      // New round.
      val touched : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(form.function_symbols)
      val filter_pass : Iterable[AnnotatedFormula] = touched.filter(f => RelevanceFilter(round+1)(f))
      filter_pass.map(f => new RelevanceTask(f, round+1, this))
    case _ => Seq()
  }
}

class RelevanceTask(form : AnnotatedFormula, round : Int, a : TAgent) extends Task {
  override def name: String = "relevance_task"
  override def getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(AnnotatedFormulaType -> Set(form))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    val (name, term, role) = InputProcessing.process(Signature.get)(form)
    val nc : ClauseProxy = if(role == Role_Conjecture)    // TODO Move somewhere else?
      Store(name, Clause(Literal(term, false)), Role_NegConjecture, Context(), InferredFrom(NegateConjecture, Store(name, Clause(Literal(term, true)), role, Context(), FromFile(Configuration.PROBLEMFILE, name))))
    else
      Store(name, Clause(Literal(term, true)), role, Context(), FromFile(Configuration.PROBLEMFILE, name))
    Result().remove(AnnotatedFormulaType)(form).insert(FormulaTakenType)((form,round)).insert(ClauseType)(nc)
  }
  override def bid: Double = 1.0/(5.0 + round.toDouble)

  override val pretty: String = s"relevance_task($form, round = $round)"
  override val toString : String = pretty
}

object AnnotatedFormulaType extends DataType

object FormulaTakenType extends DataType

object NegateConjecture extends CalculusRule {
  override def name: String = "negate_conjecture"
  override val inferenceStatus = Some(SZS_Theorem)
}
