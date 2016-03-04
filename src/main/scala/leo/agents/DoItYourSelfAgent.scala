package leo.agents

import leo.datastructures.Clause
import leo.datastructures.blackboard._
import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSStore}
import leo.modules.output.StatusSZS
import leo.datastructures.context.Context

/**
  * An Agent to run on its own an return only its final result (hopefully [])
  * and the SZS status.
  */
class DoItYourSelfAgent(val procedure : ProofProcedure) extends Agent{
  override def name: String = procedure.name

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
      val forms = FormulaDataStore.getFormulas(c)
      Seq(new DoItYourSelfTask(this, forms, c))
    case _ => Iterable()
  }
}

case class DoItYourSelfMessage(c : Context) extends Message

class DoItYourSelfTask(a : DoItYourSelfAgent, fs : Iterable[AnnotatedClause], c : Context) extends Task{
  override val name: String = a.procedure.name+"Task"
  override val getAgent: TAgent = a
  override def writeSet(): Map[DataType, Set[Any]] = Map()
  override def readSet(): Map[DataType, Set[Any]] = Map()
  override def run: Result = {
    val (status, res) = a.procedure.execute(fs.map(_.clause))
    c.close()
    var r = Result().insert(StatusType)(SZSStore(status, c))
    if(res.nonEmpty){
      val it = res.get.iterator
      while(it.hasNext){
        r.insert(FormulaType)(it.next())
      }
    }
    val l : Map[String, String] = Map("a" -> "b")
    r
  }
  override def bid: Double = 1

  override val pretty: String = a.procedure.name+"Task("+c.contextID+")"
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
  def execute(formulas : Iterable[Clause]) : (StatusSZS, Option[Seq[Clause]])
}
