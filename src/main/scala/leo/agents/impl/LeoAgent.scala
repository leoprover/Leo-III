package leo.agents.impl

import leo.agents.{EmptyResult, Result, Task}
import leo.datastructures.Not
import leo.datastructures.blackboard.{FormulaEvent, Event, Blackboard, FormulaStore, Message}
import leo.datastructures.Role_Conjecture
import leo.modules.output.logger.Out

/**
 *
 * Agent for a remote Leo invocation
 *
 * @author Max Wisniewski
 * @since 11/18/14
 */
class LeoAgent(path : String) extends ScriptAgent(path){

  /**
   * Processes the result of leo2 and creates a result from it.
   *
   * TODO: Implement the result
   *
   * @param input Inputstream of Leo2
   * @param err Errorstream of Leo2
   * @param exit Exit value of leo2
   * @return
   */
  override def handle(input: Stream[String], err: Stream[String], exit: Int): Result = {
    input foreach {l => Out.output(l)}
    Out.output(s"The exit code is $exit")
    EmptyResult
  }

  /**
   * If a formula was marked for external proving filter the context.
   * It is assume, that the formula is already negated and not in CNF
   * at the moment.
   * @param e
   * @return
   */
  override protected def toFilter(e: Event): Iterable[Task] = e match {
    case FormulaEvent(event) => if((event.status & (32)) != 0){
        return createTask(event)
      }
      return Iterable.empty
    case RemoteInvoke(f) => return createTask(f)
    case _ => return Iterable.empty
  }


  private def createTask(event : FormulaStore) : Iterable[Task] = event.formula match {
    case Left(form) =>
      Out.info(s"[$name] got a task.")
      val conj = event.newRole(Role_Conjecture.pretty).newFormula(Not(form))
      val context : Set[FormulaStore] = Blackboard().getAll{f => f.name != event.name}.toSet[FormulaStore]
      return Iterable(new ScriptTask(context + conj))
    case Right(forms) => return Iterable.empty
  }

}

private class LeoMessage(f : FormulaStore) extends Message{
  def getF : FormulaStore = f
}

object RemoteInvoke {
  def apply(f : FormulaStore) : Message = new LeoMessage(f)

  def unapply(e : Event) : Option[FormulaStore]= e match {
    case event : LeoMessage => Some(event.getF)
    case _ => None
  }
}
