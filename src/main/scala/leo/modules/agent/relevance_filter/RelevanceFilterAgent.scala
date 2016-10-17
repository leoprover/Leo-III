package leo.modules.agent.relevance_filter

import leo.Configuration
import leo.agents.{Agent, TAgent, Task}
import leo.datastructures.ClauseAnnotation.{FromFile, InferredFrom}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.calculus.CalculusRule
import leo.modules.output.{SZS_CounterTheorem, SZS_Theorem}
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
        Seq(new RelevanceTask(form, -1, this, SignatureBlackboard.get))
    case DataEvent((form : AnnotatedFormula, round : Int), FormulaTakenType) =>
      // New round.
      val touched : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(form.function_symbols)
      val filter_pass : Iterable[AnnotatedFormula] = touched.filter(f => RelevanceFilter(round+1)(f))
      filter_pass.map(f => new RelevanceTask(f, round+1, this, SignatureBlackboard.get))
    case _ => Seq()
  }
}

class RelevanceTask(form : AnnotatedFormula, round : Int, a : TAgent, sig: Signature) extends Task {
  override def name: String = "relevance_task"
  override def getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map(AnnotatedFormulaType -> Set(form))
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    if(!PreFilterSet.isUnused(form)) return Result()
    val (name, term, role) = InputProcessing.process(sig)(form)
    val nc : ClauseProxy = if(role == Role_Conjecture)    // TODO Move somewhere else?
      AnnotatedClause(Clause(Literal(term, false)), Role_NegConjecture, InferredFrom(NegateConjecture, AnnotatedClause(Clause(Literal(term, true)), role, FromFile(Configuration.PROBLEMFILE, name), ClauseAnnotation.PropNoProp)), ClauseAnnotation.PropNoProp)
    else
      AnnotatedClause(Clause(Literal(term, true)), role, FromFile(Configuration.PROBLEMFILE, name), ClauseAnnotation.PropNoProp)
    Result().remove(AnnotatedFormulaType)(form).insert(FormulaTakenType)((form,round)).insert(ClauseType)(nc)
  }
  override def bid: Double = 1.0/(5.0 + round.toDouble)

  override val pretty: String = s"relevance_task($form, round = $round)"
  override val toString : String = pretty
}

object AnnotatedFormulaType extends DataType

object FormulaTakenType extends DataType

object NegateConjecture extends CalculusRule {
  override def name: String = "neg_conjecture"
  override val inferenceStatus = Some(SZS_CounterTheorem)
}
