package leo.modules.agent.relevance_filter

import leo.Configuration
import leo.agents.{AbstractAgent, Agent, Task}
import leo.datastructures.ClauseAnnotation.{FromFile, InferredFrom}
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.datastructures.tptp.Commons.AnnotatedFormula
import leo.modules.SZSException
import leo.modules.calculus.CalculusRule
import leo.modules.output.{SZS_CounterTheorem, SZS_Error}
import leo.modules.parsers.Input.processFormula
import leo.modules.relevance_filter.{PreFilterSet, RelevanceFilter}

/**
  * Applies the
  * [[leo.modules.relevance_filter.RelevanceFilter]]
  * to the set of prefilters.
  */
object RelevanceFilterAgent extends AbstractAgent {
  override def name: String = "relevance_filter_agent"
  override val interest : Option[Seq[DataType[Any]]] = Some(Seq(FormulaTakenType, AnnotatedFormulaType))

  override def init(): Iterable[Task] = {
    val insNew = PreFilterSet.getFormulas.toIterator

    var tasks : Seq[Task] = Seq()

    while(insNew.nonEmpty){
      val form = insNew.next()
      if (form.role == Role_Conjecture.pretty || form.role == Role_NegConjecture.pretty || form.function_symbols.isEmpty) {
        // Initially we takethe conjecture and prinzipels
        leo.Out.debug(s"$form : \n  ${if (form.function_symbols.isEmpty) "rule format" else "goal"}\n taken")
        tasks = new RelevanceTask(form, -1, this, SignatureBlackboard.get) +: tasks
      }
    }
    tasks
  }

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
    case r : Delta =>
      val insTaken = (r.inserts(FormulaTakenType) ++ r.updates(FormulaTakenType).map(_._2)).toIterator

      var tasks: Seq[Task] = Seq[Task]()

      while(insTaken.nonEmpty){
        val (form,round) = insTaken.next()
        val touched : Iterable[AnnotatedFormula] = PreFilterSet.getCommonFormulas(form.function_symbols)
        val filter_pass : Iterable[AnnotatedFormula] = touched.filter(f => RelevanceFilter(round+1)(f))
        tasks = tasks ++ filter_pass.map(f => new RelevanceTask(f, round+1, this, SignatureBlackboard.get))
      }
      tasks
    case _ => Seq()
  }
}

class RelevanceTask(form : AnnotatedFormula, round : Int, a : Agent, sig : Signature) extends Task {
  override def name: String = "relevance_task"
  override def getAgent: Agent = a
  override def writeSet(): Map[DataType[Any], Set[Any]] = Map(AnnotatedFormulaType -> Set(form))
  override def readSet(): Map[DataType[Any], Set[Any]] = Map()
  override def run: Delta = {
    if(!PreFilterSet.isUnused(form)) return Result()
    val (name, term, role) = processFormula(form)(sig)
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

object AnnotatedFormulaType extends DataType[AnnotatedFormula] {
  override def convert(d: Any): AnnotatedFormula = d match {
    case c : AnnotatedFormula => c
    case _ => throw new SZSException(SZS_Error, s"Expected Annotated clause, but got ${d}")
  }
}

object FormulaTakenType extends DataType[(AnnotatedFormula, Int)] {
  override def convert(d: Any): (AnnotatedFormula, Int) = d match {
    case (c : AnnotatedFormula, i : Int) => (c,i)
    case _ => throw new SZSException(SZS_Error, s"Expected (Annotated formula, Int), but got ${d}")
  }
}

object NegateConjecture extends CalculusRule {
  override def name: String = "neg_conjecture"
  override val inferenceStatus = SZS_CounterTheorem
}
