package leo.agents.impl

import leo.agents.{EmptyResult, Result, Task}
import leo.datastructures.blackboard.{Blackboard, FormulaStore}
import leo.datastructures.internal.Role_Conjecture
import leo.modules.output.logger.Out
import leo.datastructures.internal._

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
   * @param event
   * @return
   */
  override protected def toFilter(event: FormulaStore): Iterable[Task] = {

    if((event.status & (32)) != 0){
      event.formula match {
        case Left(form) =>
          Out.info(s"[$name] got a task.")
          val conj = event.newRole(Role_Conjecture.pretty).newFormula(Not(form))
          val context : Set[FormulaStore] = Blackboard().getAll{f => f.name != event.name}.toSet[FormulaStore]
          return Iterable(new ScriptTask(context + conj))
        case Right(forms) => return Iterable.empty
      }
    }
    Iterable.empty
  }
}
