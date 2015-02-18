package leo.agents
package impl

import leo.agents.{EmptyResult, Result, Task}
import leo.datastructures.context.Context
import leo.datastructures.term.Term
import leo.datastructures._
import leo.datastructures.blackboard.{Blackboard, FormulaStore, Event, Message}
import leo.modules.output.StatusSZS
import leo.modules.output.logger.Out

object SZSScriptAgent {
  def apply(cmd : String) : Agent = new SZSScriptAgent(cmd)
}

/**
 * A Script agent to execute a external theorem prover
 * and scans the output for the SZS status and inserts it into the Blackboard.
 */
class SZSScriptAgent(cmd : String) extends ScriptAgent(cmd) {
  override val name = s"SZSScriptAgent ($cmd)"

  /**
   * Scans the `input` Stream for an SZS status.
   *
   * @param input - THe input stream, that will be scanned for the status
   * @param err - THe error stream. Will be ignored.
   * @param errno - The return value.
   * @return
   */
  override def handle(fs : Set[FormulaStore], input: Iterator[String], err: Iterator[String], errno: Int): Result = {
    val context = fs.head.context
    val it = input
    while(it.hasNext){
      val line = it.next()
      getSZS(line) match {
        case Some(status) =>
          context.close()
          return new ContextResult(context, status)
        case None         => ()
      }

    }
    return EmptyResult
  }

  /**
   * Returns some SZSStatus, if the line contains one. Else none.
   *
   * @param line - The line to scan for szs status
   * @return Some(StatusSZS) if the line contains one, else none.
   */
  def getSZS(line : String) : Option[StatusSZS] = StatusSZS.answerLine(line)

  override protected def toFilter(event: Event): Iterable[Task] = event match {
    case SZSScriptMessage(f) => Out.output("Will create task."); createTask(f)
    case _                   => List()
  }

  private def createTask(f : FormulaStore) : Iterable[Task] = {
    Out.trace(s"[$name]: Got a task.")
    val conj = f.newRole(Role_Conjecture).newClause(negateClause(f.clause))
    val context : Set[FormulaStore] = Blackboard().getAll(f.context){bf => bf.name != f.name}.toSet[FormulaStore]
    return List(new ScriptTask(context + conj))
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







/**
 * A message with f (the to be conjecture)
 * @param f
 */
private class SZSScriptMessage(val f : FormulaStore) extends Message {}

/**
 * Object to create and deconstruct messages to the SZSScriptAgent.
 */
object SZSScriptMessage {
  /**
   * Creates a new Message to the SZSScriptAgent. The formula `f` will
   * be transformed into the conjecture for the external agent.
   *
   * @param f - The conjecture
   * @return Message for the SZSScriptAgent.
   */
  def apply(f : FormulaStore) : Message = new SZSScriptMessage(f)

  /**
   * Deconstructs an Event, if it is a Message to the SZSScriptAgent.
   *
   * @param m
   * @return
   */
  def unapply(m : Event) : Option[FormulaStore] = m match {
    case m : SZSScriptMessage => Some(m.f)
    case _                    => None
  }
}