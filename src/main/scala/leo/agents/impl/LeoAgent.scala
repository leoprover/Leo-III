package leo.agents.impl

import leo.agents.{EmptyResult, Result, Task}
import leo.datastructures._
import leo.datastructures.blackboard.{FormulaEvent, Event, Blackboard, FormulaStore, Message}
import leo.modules.output.logger.Out
import leo.datastructures.term.Term

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
    Out.trace(s"[$name]: Got result from external prover: \n"+input.mkString("\n")+"\n")
    Out.output(s"[$name]:The exit code is $exit")
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


  private def createTask(event : FormulaStore) : Iterable[Task] = {
    Out.trace(s"[$name]: Got a task.")
    val conj = event.newRole(Role_Conjecture).newClause(negateClause(event.clause))
    val context : Set[FormulaStore] = Blackboard().getAll(event.context){f => f.name != event.name}.toSet[FormulaStore]
    return Iterable(new ScriptTask(context + conj))
  }

  private def negateClause(c : Clause) : Clause = {
    val lit : Literal = Literal(orLit(c.lits),false)
    return Clause.mkClause(List(lit), Derived)
  }

  private def orLit(l : Seq[Literal]) : Term = l match {
    case Seq()        => LitTrue
    case l1 +: Seq()  => if(l1.polarity) l1.term else Not(l1.term)
    case l1 +: ls   => if(l1.polarity) |||(l1.term, orLit(ls)) else |||(Not(l1.term), orLit(ls))
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
