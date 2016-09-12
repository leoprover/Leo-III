package leo.agents

import leo.datastructures.{AnnotatedClause, Clause, ClauseProxy}
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSStore}
import leo.modules.output.StatusSZS
import leo.datastructures.context.Context

/**
  * An Agent to run on its own an return only its final result (hopefully [])
  * and the SZS status.
  */
class DoItYourSelfAgent(val procedure : ProofProcedure) extends AbstractAgent{
  override def name: String = procedure.name
  override val interest : Option[Seq[DataType]] = None

  override def init(): Iterable[Task] = {
    val forms = FormulaDataStore.getFormulas
    Seq(new DoItYourSelfTask(this, forms))
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
    case DoItYourSelfMessage(c) =>
      // TODO Blackboard Structure might vary
      val forms = FormulaDataStore.getFormulas
      Seq(new DoItYourSelfTask(this, forms))
    case _ => Iterable()
  }

  /**
    * Method called, when a task cannot be executed
    * and is removed from the task set.
    *
    * @param t
    */
  override def taskCanceled(t: Task): Unit = {}
}

case class DoItYourSelfMessage(c : Context) extends Message

class DoItYourSelfTask(a : DoItYourSelfAgent, fs : Iterable[ClauseProxy]) extends Task{
  override val name: String = a.procedure.name+"Task"
  override val getAgent: Agent = a
  override val writeSet: Map[DataType, Set[Any]] = Map.empty
  override val readSet: Map[DataType, Set[Any]] = Map.empty
  override def run: Result = {
    val fs1 = fs.map(_.asInstanceOf[AnnotatedClause])
    val (status, res) = a.procedure.execute(fs1)
    if(res.isEmpty) return Result()
    var r = Result().insert(StatusType)(SZSStore(status))
    if(res.nonEmpty){
      val it = res.get.iterator
      while(it.hasNext){
        r.insert(ClauseType)(it.next())
      }
    }
    r
  }
  override def bid: Double = 0.3

  override val pretty: String = a.procedure.name
}


/**
  * A proof procedure, that can be executed by it self and not
  * colaborating the blackboard.
  */
trait ProofProcedure {
  def name : String

  /**
    * Executes a sequential proof procedure.
    * Upon return updates the blackboard with
    * <ol>
    *   <li>The SZS status</li>
    *   <li>The remaining proof obligations.<br />The obligation should contain the empty clause, if a proof was found</li>
    * </ol>
    *
    * @param formulas The set of formulas we want to proof a contradiction.
    * @return The SZS status and optinally the remaing proof obligations. In the case of a sucessfull proof the empty
    *         clause should be returned (containing the proof).
    */
  def execute(formulas : Iterable[AnnotatedClause]) : (StatusSZS, Option[Seq[AnnotatedClause]])
}
