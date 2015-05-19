package leo.agents
package impl

import leo.datastructures.blackboard.impl.{FormulaDataStore, SZSStore}
import leo.datastructures.context.Context
import leo.datastructures._
import leo.datastructures.blackboard._
import leo.modules.output.{SZS_GaveUp, StatusSZS}
import leo.modules.output.logger.Out

object SZSScriptAgent {
  def apply(cmd : String)(reinterpreteResult : StatusSZS => StatusSZS) : Agent = new SZSScriptAgent(cmd)(reinterpreteResult)
}

/**
 * A Script agent to execute a external theorem prover
 * and scans the output for the SZS status and inserts it into the Blackboard.
 */
class SZSScriptAgent(cmd : String)(reinterpreteResult : StatusSZS => StatusSZS) extends ScriptAgent(cmd) {
  override val name = s"SZSScriptAgent ($cmd)"

  /**
   * Scans the `input` Stream for an SZS status.
   *
   * @param input - THe input stream, that will be scanned for the status
   * @param err - THe error stream. Will be ignored.
   * @param errno - The return value.
   * @return
   */
  override def handle(c : Context, input: Iterator[String], err: Iterator[String], errno: Int): Result = {
    val context = c   // TODO Fix
    val it = input
    val b = new StringBuilder
    while(it.hasNext){
      val line = it.next()
      b.append("  Out: "+line+"\n")
      getSZS(line) match {
        case Some(status) =>
          context.close()
          Out.info(s"[$name]: Got ${status.output} from the external prover.")
          return Result().insert(StatusType)(SZSStore(reinterpreteResult(status), context))
        case None         => ()
      }
    }
    while(err.hasNext){
      val line = err.next()
      b.append("  Err: "+line+"\n")
      getSZS(line) match {
        case Some(status) =>
          context.close()
          Out.info(s"[$name]: Got ${status.output} from the external prover.")
          return Result().insert(StatusType)(SZSStore(reinterpreteResult(status), context))
        case None         => ()
      }
    }
    Out.info(s"[$name]: No SZS status returned in\n${b.toString}")
    context.close()
    return Result().insert(StatusType)(SZSStore(SZS_GaveUp, context))
  }

  /**
   * Returns some SZSStatus, if the line contains one. Else none.
   *
   * @param line - The line to scan for szs status
   * @return Some(StatusSZS) if the line contains one, else none.
   */
  def getSZS(line : String) : Option[StatusSZS] = StatusSZS.answerLine(line)

  override def toFilter(event: Event): Iterable[Task] = event match {
    case SZSScriptMessage(f,c) => createTask(f,c)
    case _                   => List()
  }

  private def createTask(f : FormulaStore, c : Context) : Iterable[Task] = {
    Out.trace(s"[$name]: Got a task.")
    val conj = Store(negateClause(f.clause), Role_Conjecture, f.context, f.status)
    val context : Set[FormulaStore] = FormulaDataStore.getAll(f.context){bf => bf.name != f.name}.toSet[FormulaStore]
    return List(new ScriptTask(context + conj, c))
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
private class SZSScriptMessage(val f : FormulaStore, val c : Context) extends Message {}

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
  def apply(f : FormulaStore)(c : Context) : Message = new SZSScriptMessage(f,c)

  /**
   * Deconstructs an Event, if it is a Message to the SZSScriptAgent.
   *
   * @param m
   * @return
   */
  def unapply(m : Event) : Option[(FormulaStore, Context)] = m match {
    case m : SZSScriptMessage => Some((m.f,m.c))
    case _                    => None
  }
}