package leo.modules.agent.relevance_filter

import leo.Configuration
import leo.agents.{TAgent, Task, Agent}
import leo.datastructures.ClauseAnnotation.FromFile
import leo.datastructures.{Literal, Clause, ClauseProxy}
import leo.datastructures.blackboard._
import leo.datastructures.context.Context
import leo.datastructures.impl.Signature
import leo.datastructures.tptp.Commons.AnnotatedFormula
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
    case DataEvent(form : AnnotatedFormula, AnnotatedFormulaType) =>
      if(RelevanceFilter(form)){
        Seq(new RelevanceTask(form, 0, this))
      } else Seq()
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
    val nc : ClauseProxy = Store(name, Clause(Literal(term, true)), role, Context(), FromFile(Configuration.PROBLEMFILE, name))
    Result().remove(AnnotatedFormulaType)(form).insert(FormulaTakenType)((form,round)).insert(ClauseType)(nc)
  }
  override def bid: Double = 1.0/(5.0 + round.toDouble)

  override def pretty: String = s"relevance_task($form)"
}

object AnnotatedFormulaType extends DataType

object FormulaTakenType extends DataType
